import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
class Application implements Board.EventListener, BoardFrame.EventListener
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
		new Application((args.length >= 1) ? args[0] : null, (args.length >= 2)
			? args[1] : null);
	}

	/** IDE-unassisted debugging mode switch. */
	final boolean debug = false;

	/**
	 * Whether to prefer AWT to Swing. AWT > Swing :-)
	 * 
	 * I have observed that on Mac OS X, AWT has native file dialogs while Swing
	 * doesn't (didn't?).
	 */
	final boolean useAWT = true;

	/** Board file that this application loads board data from. */
	private File boardFile;

	/** Board that this application loads and displays. */
	private Board board;

	/** Board frame that this application creates for displaying the board. */
	private BoardFrame boardFrame;

	final private String solutionFileSpec;

	private SolutionBufferWriter solutionBufferWriter;

	/** File to write solution buffer to, if any. */
	private File solutionFile;

	/** Solution buffer object that holds all of the boards solutions. */
	private SolutionBuffer solutionBuffer;

	/**
	 * Marks whenever the solution solving thread is suspended. Used with
	 * IDE-unassisted debugging.
	 */
	private boolean solverThreadSuspended;

	/** The thread solving the board. */
	private Thread solverThread;

	/**
	 * Whether to delay interactive solving process on each square value tried.
	 * Value of <code>-1</code> signifies that solving will not be delayed.
	 */
	final private long solveStepDelay = 100;

	/**
	 * Create an application.
	 * 
	 * The application may load a board from file specified on command line or
	 * let user choose a file using a GUI. Likewise, if the solution list output
	 * file was specified on command line, it will save the solutions to the
	 * specified file instead of displaying them with a GUI.
	 * 
	 * @param boardFilePath Path of board file to load or <code>null</code> to
	 *        let user choose one with a GUI.
	 * @param solutionBufferFilePath Path of solution file to write or
	 *        <code>null</code> to display solutions with a GUI.
	 * @throws IOException An I/O error occurs while loading the board from
	 *         file.
	 * @throws FileNotFoundException The file to load the board from was not
	 *         found.
	 */
	Application(String boardFilePath, String solutionFileSpec)
		throws FileNotFoundException, IOException
	{
		this.solutionFileSpec = solutionFileSpec;

		if(boardFilePath != null)
		{
			boardFile = new File(boardFilePath);
		}
		else
		{
			boardFile = chooseFileDialog("Load board from file", false);

			if(boardFile == null)
			{
				return;
			}
		}

		board = loadBoardFromFile();

		startSolvingBoard();
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
	@Override public void onResetBoardSquareValue(DynamicSquare square)
	{
		if(debug)
		{
			boardFrame.updateAsCurrentSquare(square);

			suspendSolvingThread();	
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
	@Override public void onSolvingBadSquareValue(int badValue, DynamicSquare square)
	{
		if(debug)
		{
			boardFrame.updateSquare(square, badValue, Color.RED);
			
			suspendSolvingThread();
		}
	}

	/**
	 * Callback method.
	 * 
	 * Is called when a single solution is found for a board.
	 * 
	 * @param board the board that has been solved.
	 */
	@Override public void onBoardSolutionComplete(Board board)
	{
		solutionBuffer.addSnapshot(board);
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
		if(solutionFileSpec == null)
		{
			if(boardFrame == null)
			{
				createBoardFrame(board);
			}

			if(solutionBuffer.size() > 0)
			{
				boardFrame.showBoardSolution(0);
			}
		}
		else
		{
			try
			{
				saveSolutions();
			}
			catch(IOException e)
			{
				throw new RuntimeException("Couldn't save solutions.", e);
			}
		}
	}

	@Override public void onStepButtonClicked()
	{
		resumeSolvingThread();
	}
	
	/**
	 * Initiates solving process by starting a solving thread which will
	 * progress in parallel with the calling execution process.
	 * 
	 * The application will be notified of events during solving process. In
	 * IDE-unassisted debugging mode, the board view will be constructed and
	 * updated during the solving process.
	 */
	void startSolvingBoard()
	{
		solutionBuffer = new SolutionBuffer(board);

		solverThread = new Thread()
		{
			@Override public void run()
			{
				board.solve(Application.this);
			}
		};

		if(debug)
		{
			createBoardFrame(board);
			boardFrame.stepButton.setEnabled(true);
		}

		solverThread.start();
	}

	private void createBoardFrame(Board board)
	{
		assert board == this.board;

		String boardFrameTitle;

		try
		{
			boardFrameTitle = boardFile.getCanonicalPath();
		}
		catch(IOException e)
		{
			boardFrameTitle = "?";
		}

		boardFrame =
			new BoardFrame(board, solutionBuffer, boardFrameTitle, debug, this);
	}

	/**
	 * Suspends the thread that is solving the board.
	 * 
	 * Useful with IDE-unassisted debugging.
	 */
	synchronized void suspendSolvingThread()
	{
		solverThreadSuspended = true;

		synchronized(this)
		{
			while(solverThreadSuspended)
			{
				try
				{
					wait();
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Resumes the thread that is solving the board.
	 * 
	 * Useful with IDE-unassisted debugging.
	 */
	synchronized void resumeSolvingThread()
	{
		solverThreadSuspended = false;

		notify();
	}

	/**
	 * Private methods.
	 */

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
	 * Load a board from the file (specified on command line).
	 * 
	 * @throws IOException An I/O error occurs while loading board.
	 * @throws FileNotFoundException The file to load the board from was not
	 *         found.
	 */
	private Board loadBoardFromFile() throws FileNotFoundException, IOException
	{
		final BoardFileLoader loader = new BoardFileLoader();

		final FileReader fileReader = new FileReader(boardFile);

		try
		{
			return loader.loadBoard(fileReader);
		}
		finally
		{
			fileReader.close();
		}
	}

	/**
	 * Procedure to save the solution buffer to the solutions file or stdout.
	 * 
	 * The resource leak warning is a false positive - the <code>writer</code>
	 * is always closed because it is enclosed in a <code>finally</code> block.
	 * 
	 * @throws IOException An I/O error occurs while writing solution buffer.
	 */
	void saveSolutions() throws IOException
	{
		assert solutionFileSpec != null;
		assert solutionFile == null;
		
		if(!solutionFileSpec.equals("-"))
		{
			solutionFile = new File(solutionFileSpec);
		}
		
		Writer writer =
			(solutionFileSpec.equals("-")) ? (new OutputStreamWriter(System.out))
				: (new BufferedWriter(new FileWriter(solutionFile)));

		solutionBufferWriter = new SolutionBufferWriter(writer);

		try
		{
			solutionBufferWriter.write(solutionBuffer);
		}
		finally
		{
			writer.close();
		}
	}
	
	private void solveDelay()
	{
		if(solveStepDelay >= 0)
		{
			try
			{
				Thread.sleep(solveStepDelay);
			}
			catch(InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}