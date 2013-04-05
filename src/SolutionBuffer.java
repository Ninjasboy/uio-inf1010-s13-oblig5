import java.util.LinkedList;
import java.util.List;

/**
 * Manages the entire set of solutions for a single Sudoku board.
 */
class SolutionBuffer
{
	private static final int MAX_LIST_SIZE = 500;

	public final Board board;

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
	 * Takes a snapshot of the board, i.e. all the values in the its squares.
	 */
	void add(Board board)
	{
		int[][] boardValueArray = new int[board.dimension][board.dimension];

		for(int y = 0; y < board.dimension; y++)
		{
			for(int x = 0; x < board.dimension; x++)
			{
				boardValueArray[y][x] = board.value(x, y);
			}
		}

		solutionList.add(boardValueArray);
	}

	/**
	 * Takes a snapshot of the board, i.e. all the values in the its squares.
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
	 * Returns a solution object (two-dimensional array) specified by a
	 * so-called solution ID or an index.
	 * 
	 * @param solutionID
	 * @return
	 */
	int[][] get(int solutionID)
	{
		return solutionList.get(solutionID).clone();
	}

	/**
	 * Counts amount of solutions for the board.
	 * 
	 * @return Amount of solutions for the board.
	 */
	int size()
	{
		return solutionList.size();
	}
}
