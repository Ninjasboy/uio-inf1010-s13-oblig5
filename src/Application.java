import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.awt.Color;

/**
 * Main application.
 * 
 * When no arguments are present, asks user to select a board file, loads a
 * board, and presents a Sudoku board view with the solutions, and navigation
 * buttons.
 * 
 * When a single argument is present, loads the board from the file specified by
 * the argument.
 * 
 * When two arguments are present, loads the board from the file specified by
 * the first argument, solves it, and writes the solutions to the file specified
 * by the second argument.
 * 
 */
class Application implements LargeBruteForceSolver.EventListener,
	BoardFrame.EventListener
{
	/**
	 * Entry point of the application.
	 * 
	 * @param args Command line arguments, see class documentation.
	 * @throws FileNotFoundException File to load the board from was not found.
	 * @throws IOException An I/O error occurs while loading the board from
	 *         file.
	 */
	public static void main(String[] args)
		throws FileNotFoundException, IOException
	{
		Application application = new Application(args);
	}

	/**
	 * A debugging support object.
	 */
	@Debug class Debugger
	{
		/** Whether to monitor progress of solving visually in the board frame. */
		final boolean interactiveSolving;

		/** Whether to use manual stepping while solving the board. */
		final boolean useStepping;

		/**
		 * Whether to delay interactive solving process on each square value
		 * tried. Value of <code>-1</code> signifies that solving will not be
		 * delayed.
		 */
		final int interactiveDelay;

		/** Whether to time solving procedure. */
		final boolean timeSolver;

		/**
		 * State whether solving thread is suspended as part of user manually
		 * stepping through the solving thread progress.
		 */
		volatile boolean solverThreadSuspended;

		/**
		 * Create new debugging object and initialize it using an environment
		 * variable map.
		 * 
		 * @param env The environment variable map.
		 */
		Debugger(Map<String, String> env)
		{
			interactiveSolving = dbgEnvBoolean("debug.interactiveSolving", env);
			useStepping =
				dbgEnvBoolean("debug.interactiveSolving.useStepping", env);
			interactiveDelay =
				dbgEnvInteger("debug.interactiveSolving.delay", env);
			timeSolver = dbgEnvBoolean("debug.timeSolver", env);
		}

		/**
		 * Obtain boolean value from the verbatim value of an environment
		 * variable.
		 * 
		 * @param varName Name of the variable to obtain value of.
		 * @param env Environment variable map containing variables and their
		 *        values.
		 * @return Value of the variable.
		 */
		public boolean dbgEnvBoolean(String varName, Map<String, String> env)
		{
			return env.containsKey(varName) ? Boolean.parseBoolean(env
				.get(varName)) : false;
		}

		/**
		 * Obtain integer value from the verbatim value of an environment
		 * variable.
		 * 
		 * @param varName Name of the variable to obtain value of.
		 * @param env Environment variable map containing variables and their
		 *        values.
		 * @return Value of the variable.
		 */
		public int dbgEnvInteger(String varName, Map<String, String> env)
		{
			return env.containsKey(varName) ? Integer
				.parseInt(env.get(varName)) : -1;
		}

		/**
		 * Suspends the thread that is solving the board.
		 * 
		 * Part of the runtime debugging.
		 * 
		 * @throws InterruptedException If waiting for thread suspend release is
		 *         interrupted.
		 */
		private synchronized void suspendSolvingThread()
			throws InterruptedException
		{
			solverThreadSuspended = true;

			while(solverThreadSuspended)
			{
				wait();
			}
		}

		/**
		 * Resumes the thread that is solving the board.
		 * 
		 * Useful with IDE-unassisted debugging.
		 */
		private synchronized void resumeSolvingThread()
		{
			solverThreadSuspended = false;

			notifyAll();
		}
		
		/**
		 * Called by event listener methods during solving of the board.
		 */
		private void onStep()
		{
			if(useStepping)
			{
				try
				{
					suspendSolvingThread();
				}
				catch(InterruptedException e)
				{
					/** Ignore interruption, but tell what happened. */
					e.printStackTrace();
				}
			}
			else
			{
				solveDelay();
			}
		}

		/**
		 * Encapsulates delaying of solving thread.
		 */
		private void solveDelay()
		{
			if(interactiveDelay >= 0)
			{
				try
				{
					Thread.sleep(interactiveDelay);
				}
				catch(InterruptedException e)
				{
					throw new RuntimeException(e);
				}
			}
		}		
	}

	/** Debugging support object */
	final public Debugger debug;

	/**
	 * Whether to prefer AWT to Swing. AWT > Swing :-)
	 * 
	 * I have observed that on Mac OS X, AWT has native file dialogs while Swing
	 * doesn't (didn't?).
	 */
	final public boolean useAWT = true;

	/** Command line arguments. */
	final private String[] args;

	/** The file that the board is loaded from, used for board frame title. */
	private File boardFile;

	/** The Swing/AWT frame displaying the board. */
	private BoardFrame boardFrame;

	/** Solution buffer object, containing solutions to the board. */
	private SolutionBuffer solutionBuffer;

	/**
	 * Create the application and initialize it according to specified command
	 * line arguments.
	 * 
	 * The application may load a board from file specified on command line or
	 * let user choose a file using a GUI. Likewise, if the solution list output
	 * file was specified on command line, it will save the solutions to the
	 * specified file instead of displaying them with a GUI.
	 * 
	 * @param args An array of command line arguments.
	 * @throws IOException An I/O error occurs while loading the board from
	 *         file.
	 * @throws FileNotFoundException The file to load the board from was not
	 *         found.
	 */
	Application(String[] args) throws FileNotFoundException, IOException
	{
		debug = new Debugger(System.getenv());

		this.args = args;

		final File boardFile =
			(args.length > 0 && !(args[0].equals("-"))) ? (new File(args[0]))
				: chooseFileDialog("Load board from file", false);

		if(boardFile == null)
		{
			return;
		}

		startSolvingBoard(loadBoard(boardFile), boardFile);
	}

	/**
	 * Load a board from the board file.
	 * 
	 * @param boardFile The file to load the board from.
	 * 
	 * @throws IOException An I/O error occurs while loading the board.
	 * @throws FileNotFoundException The file to load the board from was not
	 *         found.
	 */
	public Board loadBoard(File boardFile)
		throws FileNotFoundException, IOException
	{
		final FileReader fileReader = new FileReader(boardFile);

		try
		{
			return (new BoardFileLoader()).loadBoard(fileReader);
		}
		finally
		{
			fileReader.close();
		}
	}

	/**
	 * Initiates solving process by starting a solving thread which will
	 * progress in parallel with the calling execution process.
	 * 
	 * The application will be notified of events during solving process. In
	 * IDE-unassisted debugging mode, the board view will be constructed and
	 * updated during the solving process.
	 * 
	 * @param board The board to start solving.
	 * @param boardFile The file that the board is loaded from, to use as board
	 *        title.
	 */
	private void startSolvingBoard(final Board board, File boardFile)
	{
		this.boardFile = boardFile;

		solutionBuffer = new SolutionBuffer(board);

		if(debug.interactiveSolving)
		{
			boardFrame = newBoardFrame(board, boardFile);

			if(debug.useStepping)
			{
				boardFrame.stepButton.setEnabled(true);
			}

			boardFrame.setVisible(true);
		}

		Thread solverThread = new Thread()
		{
			@Override public void run()
			{
				final LargeBruteForceSolver solver =
					new LargeBruteForceSolver();

				final long startSolvingTS = System.nanoTime();

				solver.solve(board, Application.this);

				if(Application.this.debug.timeSolver)
				{
					System.err
						.println("Solving took "
							+ (int)((System.nanoTime() - startSolvingTS) / 1000 / 1000)
							+ " milliseconds.");
				}
			}
		};

		solverThread.start();
	}

	/**
	 * Callback method.
	 * 
	 * Only used with IDE-unassisted debugging now. Is called when a value of a
	 * square of a board is found to be invalid.
	 * 
	 * @param badValue The value that is found to be invalid.
	 * @param square The board square that the value was tried for.
	 */
	@Override public void onSolverTryBoardValue(Board board, int tryValue,
		int colIndex, int rowIndex)
	{
		if(debug.interactiveSolving)
		{
			boardFrame.updateSquare(colIndex, rowIndex, tryValue, Color.YELLOW);

			debug.onStep();
		}
	}

	/**
	 * Callback method.
	 * 
	 * Only used with IDE-unassisted debugging now. Is called when a value of a
	 * square of a board is reset.
	 * 
	 * @param square The board square that has had its value reset.
	 * @throws InterruptedException
	 */
	@Override public void onResetBoardValue(Board board, int value,
		int colIndex, int rowIndex)
	{
		if(debug.interactiveSolving)
		{
			boardFrame.updateSquare(colIndex, rowIndex, value, Color.GREEN);

			debug.onStep();
		}
	}

	/**
	 * Callback method.
	 * 
	 * Only used with IDE-unassisted debugging now. Is called when a value of a
	 * square of a board is found to be invalid.
	 * 
	 * @param badValue The value that is found to be invalid.
	 * @param square The board square that the value was tried for.
	 */
	@Override public void onBoardSolvingBadValue(Board board, int tryValue,
		int colIndex, int rowIndex)
	{
		if(debug.interactiveSolving)
		{
			boardFrame.updateSquare(colIndex, rowIndex, tryValue, Color.RED);

			debug.onStep();
		}
	}

	/**
	 * Callback method.
	 * 
	 * Is called when a single solution is found for a board.
	 * 
	 * @param board the board that has been solved.
	 */
	@Override public void onBoardSolutionComplete(Board board,
		int[][] boardValueArray)
	{
		solutionBuffer.addSnapshot(boardValueArray);
	}

	/**
	 * Callback method.
	 * 
	 * Is called when all of the boards solutions have been found.
	 * 
	 * @param board The board that has been solved.
	 */
	@Override public void onBoardAllSolutionsComplete(Board board)
	{
		if(args.length <= 1)
		{
			boardFrame = newBoardFrame(board, boardFile);

			if(solutionBuffer.size() > 0)
			{
				boardFrame.showBoardSolution(0);
			}

			boardFrame.setVisible(true);
		}
		else
		{
			try
			{
				saveSolutions(solutionBuffer, args[1]);
			}
			catch(IOException e)
			{
				throw new RuntimeException("Couldn't save solutions.", e);
			}
		}
	}

	@Debug @Override public void onStepButtonClicked()
	{
		debug.resumeSolvingThread();
	}

	/**
	 * Create a board frame. A board frame gives the user a view of the board.
	 * 
	 * @param board The data model for the new frame.
	 */
	private BoardFrame newBoardFrame(Board board, File boardFile)
	{
		return new BoardFrame(board, solutionBuffer, boardFile.getPath(), debug, this);
	}

	/**
	 * Presents a dialog that lets the user to choose a file.
	 * 
	 * @param title Title of the dialog
	 * @param isSaveDialog Whether this dialog is for saving a file or loading
	 *        it.
	 * @return Selected file object.
	 */
	private File chooseFileDialog(String title, boolean isSaveDialog)
	{
		FileChooser fileChooser = new FileChooser(useAWT, title, isSaveDialog);

		return fileChooser.file();
	}

	/**
	 * Procedure to save the solution buffer to the solutions file or stdout.
	 * 
	 * The resource leak warning is a false positive - the <code>writer</code>
	 * is always closed because it is enclosed in a <code>finally</code> block.
	 * 
	 * @throws IOException An I/O error occurs while writing solution buffer.
	 */
	private void saveSolutions(SolutionBuffer solutionBuffer,
		String outputFileSpec) throws IOException
	{
		assert outputFileSpec != null;

		final Writer writer =
			outputFileSpec.equals("-")
				? (new OutputStreamWriter(System.out))
				: (new BufferedWriter(new FileWriter(new File(outputFileSpec))));

		final SolutionBufferWriter solutionBufferWriter =
			new SolutionBufferWriter();

		try
		{
			solutionBufferWriter.write(solutionBuffer, writer);
		}
		finally
		{
			writer.close();
		}
	}	
}
