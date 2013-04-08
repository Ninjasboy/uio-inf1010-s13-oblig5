import java.util.LinkedList;
import java.util.List;

/**
 * A buffer containing all solutions for a given Sudoku board.
 */
class SolutionBuffer
{
	/**
	 * Maximum amount of solutions a buffer will hold, before trimming.
	 */
	private static final int MAX_LIST_SIZE = 500;

	/**
	 * The board the solutions apply to.
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
	 * @param board The board to add snapshot of as a solution. The parameter
	 *        <code>board</code> may be superflous, since this buffer references
	 *        the original board anyway, but is kept for posterity.
	 */
	void add(Board board)
	{
		assert board == this.board;

		final int[][] boardValueArray =
				new int[board.dimension][board.dimension];

		for(int y = 0; y < board.dimension; y++)
		{
			for(int x = 0; x < board.dimension; x++)
			{
				boardValueArray[y][x] = board.value(x, y);
			}
		}

		add(boardValueArray);
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
	void add(int[][] boardValueArray)
	{
		if(size() == MAX_LIST_SIZE)
		{
			System.err
					.println("Solution buffer has exceeded maximum allowed size, removing first element.");

			solutionList.remove(0);
		}

		solutionList.add(boardValueArray);
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
}
