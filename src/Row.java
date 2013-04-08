/**
 * A single row spanning the width of a Sudoku board.
 */
class Row extends BoardFragment
{
	/**
	 * Create a row with a given index on a Sudoku board.
	 * 
	 * @param index Index of the row on the board.
	 * @param board The board the row belongs to.
	 */
	Row(int index, Board board)
	{
		super(0, index, board.dimension, index + 1, board);
	}

	/**
	 * Obtain the index of this row.
	 * 
	 * @return Index of this row.
	 */
	int index()
	{
		return top;
	}
}
