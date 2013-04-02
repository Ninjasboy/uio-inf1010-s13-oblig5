import java.util.LinkedList;
import java.util.List;

/**
 * Manages the entire set of solutions for a single Sudoku board.
 * 
 * @author armenmi
 * 
 */
class SudokuBuffer
{
	private static final int MAX_LIST_SIZE = 500;

	public final Board board;

	private final List<int[][]> solutionList;

	/**
	 * Creates a solution buffer that is initially empty.
	 * 
	 * @param board The board that the solutions apply to.
	 */
	SudokuBuffer(Board board)
	{
		this.board = board;
		solutionList = new LinkedList<int[][]>();
	}

	/**
	 * Takes a snapshot of the board, i.e. all the values in the its squares.
	 */
	void insert(Board board)
	{
		assert (board == this.board);
		
		if(getSolutionCount() == MAX_LIST_SIZE)
		{
			System.err
					.println("List has exceeded maximum allowed size, removing first element.");

			solutionList.remove(0);
		}

		int[][] boardData = new int[board.dimension][board.dimension];

		for(int y = 0; y < board.dimension; y++)
		{
			for(int x = 0; x < board.dimension; x++)
			{

				// Square square = board.square(x, y);

				boardData[y][x] = board.value(x, y);/*
													 * (square instanceof
													 * DynamicSquare) ?
													 * board.square( x,
													 * y).value() : 0;
													 */
			}
		}

		solutionList.add(boardData);
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
	int getSolutionCount()
	{
		return solutionList.size();
	}

	final Board board()
	{
		return board;
	}
}
