/**
 * A single column of squares spanning the height of a Sudoku board.
 */
class Column extends BoardFragment
{
	/**
	 * Create a column.
	 * 
	 * @param index Index of the column.
	 * @param board Board that the column belongs to.
	 */
	Column(int index, Board board)
	{
		super(index, 0, index + 1, board.dimension, board);
	}
	
	/**
	 * Obtain the index of this column.
	 * 
	 * @return The index of this column.
	 */
	int index()
	{
		return left;
	}
}
