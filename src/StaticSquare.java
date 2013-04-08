/**
 * A square that is given by the puzzle author, i.e. a square that cannot be
 * modified by the solver.
 * 
 * @see Square Square class
 */
class StaticSquare extends Square
{
	/** Value of this square. */
	public final int value;

	/**
	 * Creates a static square with a given value.
	 * 
	 * @param value Value to initialize the square with.
	 * @param colIndex Index of the column this square lies in on the board.
	 * @param rowIndex Index of the row this square lies in on the board.
	 * @param board The board this square lies in.
	 */
	StaticSquare(int value, int colIndex, int rowIndex, Board board)
	{
		super(colIndex, rowIndex, board);

		if(value == 0)
		{
			throw new RuntimeException("Static square value cannot be zero.");
		}

		this.value = value;
	}

	/**
	 * Obtain square value.
	 * 
	 * @return Value of this square.
	 */
	public int value()
	{
		return value;
	}
}
