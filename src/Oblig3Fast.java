import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
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
class Oblig3Fast implements FastBruteForceBoardSolver.EventListener
{
	public static void main(String[] args)
	{
		if(args.length >= 2)
		{
			new Oblig3Fast(args[0], args[1]);
		}
		else if(args.length >= 1)
		{
			new Oblig3Fast(args[0], null);
		}
		else
		{
			new Oblig3Fast(null, null);
		}
	}

	/** IDE-unassisted debugging mode switch. */
	final boolean debug = true;

	/**
	 * Whether to prefer AWT to Swing. AWT > Swing :-). Say no to alien
	 * invasion.
	 * 
	 * FYI, JVM on Apple Macs has native file dialogs when using AWT.
	 */
	final boolean useAWT = true;

	/** Board file that this application loads. */
	private File boardFile;
	/** Board that this application loads and displays. */
	private Board board;
	/** Board frame that this application creates for displaying the board. */
	private BoardFrame boardFrame;
	// /** File to write solution buffer to. */
	private File solutionFile;
	/** Solution buffer object that holds all of the boards solutions. */
	private List<int[][]> solutionList;
	/**
	 * Whether the solution solving thread is suspended. Used with
	 * IDE-unassisted debugging.
	 */
	private boolean solverThreadSuspended;

	private Thread solverThread;
	
	/**
	 * Semi-interactive version.
	 * 
	 * Will always load a board from a file. May or may not ask user to write
	 * solutions to a file.
	 * 
	 * @param boardFilePath Path of board file to load
	 * @param solutionBufferFilePath Path of solution file to write
	 */
	Oblig3Fast(String boardFilePath, String solutionBufferFilePath)
	{
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

		if(boardFile == null && boardFilePath == null)
		{
			return;
		}

		loadBoardFromFile();

		if(solutionBufferFilePath != null)
		{
			solutionFile = new File(solutionBufferFilePath);
		}

		startSolvingBoard();
	}

	/**
	 * Callback method.
	 * 
	 * Only used with IDE-unassisted debugging now. Is called when a value of a
	 * square of a board is reset.
	 * 
	 * @param square The board square that has had its value reset.
	 */
	@Override public void onResetBoardValue(Board board, int x, int y, int value)
	{
		if(debug)
		{
			boardFrame.updateCurrent(x, y, value);
			
			/*try
			{
				Thread.sleep(10);
			}
			catch(InterruptedException e)
			{
				
			}*/
			
			//suspendSolvingThread();
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
	@Override public void onBoardSolvingBadValue(Board board, int badValue,
			int x, int y)
	{
		if(debug)
		{
			boardFrame.updateSquare(x, y, badValue, Color.RED);
			
			/*try
			{
				Thread.sleep(10);
			}
			catch(InterruptedException e)
			{
				
			}*/
			
			//suspendSolvingThread();
		}
	}

	/**
	 * Callback method.
	 * 
	 * Is called when a single solution is found for a board.
	 * 
	 * @param board the board that has been solved.
	 */
	@Override public void onBoardSolutionComplete(Board board, int[][] boardValueArray)
	{
		solutionList.add(boardValueArray);
		
		/* System.err.println("Added a solution to the solution buffer."); */
	}

	/**
	 * Callback method.
	 * 
	 * Is called when all of the boards solutions have been found.
	 * 
	 * @param board the board that has been solved.
	 */
	@Override public void onBoardAllSolutionsComplete(Board board)
	{
		/* System.err.println("All solutions have been found."); */

		if(debug)
		{
			return;
		}

		if(solutionFile == null)
		{
			if(!debug)
			{
				boardFrame = new BoardFrame(board, solutionList, debug);
			}
		}
		else
		{
			saveSolutions();
		}
	}

	/** Package-private methods. */

	/**
	 * Initiates solving process by forking a solving thread.
	 * 
	 * The board is set to notify this object of changes in model state. In
	 * IDE-unassisted debugging mode, the board view will be constructed and
	 * updated during the solving process.
	 */
	void startSolvingBoard()
	{
		final FastBruteForceBoardSolver.EventListener eventListener = this;

		solutionList = new ArrayList<int[][]>();

		solverThread = new Thread(new Runnable()
		{
			@Override public void run()
			{
				FastBruteForceBoardSolver solver = new FastBruteForceBoardSolver();
				
				solver.solve(board, eventListener);
			}
		});
		
		if(debug)
		{
			boardFrame = new BoardFrame(board, solutionList, debug);
		}

		solverThread.start();
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

		return fileChooser.getFile();
	}

	/**
	 * Procedure that loads a board from the file used as a startup parameter.
	 */
	private void loadBoardFromFile()
	{
		BoardFileLoader loader = new BoardFileLoader();

		try
		{
			board = loader.boardFromFile(boardFile);
		}
		catch(Exception e)
		{
			System.err.println("Could not load board from file.");
			e.printStackTrace();
		}
	}

	/**
	 * Procedure to save the solution buffer to the solutions file or stdout.
	 */
	void saveSolutions()
	{

		if(solutionFile == null)
		{
			solutionFile = chooseFileDialog("Save board solution buffer", true);

			if(solutionFile == null)
			{
				/** Saving simply aborted. */
				return;
			}
		}

		// System.err.println("Writing solution buffer.");

		try
		{
			Writer writer = null;

			if(solutionFile != null)
			{
				writer = new BufferedWriter(new FileWriter(solutionFile));

			}
			else
			{
				writer = new OutputStreamWriter(System.out);
			}

			SolutionBufferWriter solutionBufferWriter =
					new SolutionBufferWriter(writer);

			solutionBufferWriter.write(solutionBuffer);

			writer.close();
		}
		catch(IOException e)
		{
			System.err.println("Could not save board solution buffer to file.");
			e.printStackTrace();
		}
	}
	
	void onClickStepButton()
	{
		resumeSolvingThread();
	}
}
