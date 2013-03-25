/**
 * Models a square that is given by the puzzle author, i.e. a square that is not
 * to be modified by the puzzle solver.
 * 
 * @author armenmi
 * 
 */
class StaticSquare extends Square {
	/**
	 * Creates a static square with a given value.
	 * 
	 * @param value
	 *            Value to initialize the square with.
	 * @param colIndex
	 *            @see Square class
	 * @param rowIndex
	 *            @see Square class
	 * @param board
	 *            @see Square class
	 */
	StaticSquare(int value, int colIndex, int rowIndex, Board board) {
		super(colIndex, rowIndex, board);

		this.value = value;
	}
}
