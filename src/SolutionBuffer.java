import java.util.LinkedList;
import java.util.List;

/**
 * A buffer containing all solutions for a given Sudoku board.
 */
class SolutionBuffer
{
	/**
	 * Maximum amount of solutions a buffer will hold.
	 * 
	 * Adding a solution when the buffer is full causes causes removal of the
	 * head element of solution list, effectively removing the oldest added
	 * solution.
	 */
	private static final int MAX_LIST_SIZE = 500;

	/**
	 * The board that the solutions apply to.
	 */
	public final Board board;

	/**
	 * List of solutions.
	 */
	private final List<int[][]> solutionList;

	/**
	 * Creates a solution buffer that is initially empty.
	 * 
	 * @param board The board that the solutions apply to.
	 */
	SolutionBuffer(Board board)
	{
		this.board = board;
		solutionList = new LinkedList<int[][]>();
	}

	/**
	 * Take a snapshot of the board, i.e. all the values in the its squares.
	 * 
	 * This method is an alternative version of the <code>add(Board
	 * board)</code> method.
	 * 
	 * @param boardValueArray An array containing board values, row for row,
	 *        column for column.
	 */
	void addSnapshot(final int[][] boardValueArray)
	{
		assert noZeroValues(boardValueArray);

		if(size() == MAX_LIST_SIZE)
		{
			System.err
				.println("Solution buffer has exceeded maximum allowed size, removing first element.");

			solutionList.remove(0);
		}

		solutionList.add(boardValueArrayCopy(boardValueArray));
	}

	/**
	 * Obtain a solution as a two-dimensional array, specified by a so-called
	 * solution ID or an index.
	 * 
	 * @param solutionID Index of the solution in the solution list.
	 * @return A two-dimensional array with board values that comprise the
	 *         solution.
	 */
	int[][] get(int solutionID)
	{
		return solutionList.get(solutionID).clone();
	}

	/**
	 * Obtain size of this buffer, which is the amount of solutions it contains.
	 * 
	 * @return Amount of solutions in this buffer.
	 */
	int size()
	{
		return solutionList.size();
	}

	int[][] boardValueArrayCopy(final int[][] boardValueArray)
	{
		final int boardDimension = boardValueArray.length;

		final int[][] boardValueArrayCopy =
			new int[boardDimension][boardDimension];

		for(int i = 0; i < boardDimension; i++)
		{
			System.arraycopy(boardValueArray[i], 0, boardValueArrayCopy[i], 0,
				boardDimension);
		}

		return boardValueArrayCopy;
	}
	
	@Debug private boolean noZeroValues(int[][] a)
	{
		for(int y = 0; y < a.length; y++)
		{
			for(int x = 0; x < a.length; x++)
			{
				if(a[y][x] == 0)
				{
					return false;
				}
			}
		}

		return true;
	}	
}