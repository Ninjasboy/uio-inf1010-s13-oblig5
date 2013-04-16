/**
 * Models a single square of a Sudoku board.
 * 
 * You may notice that this class lacks a <code>value</code> variable. This is
 * because such value would be immutable for so-called static squares, and there
 * is no way to override mutability in Java, so the value variable is included
 * in subclasses of this class, with different mutability modifier. This way I
 * can also statically guarantee that the value of a static square is immutable.
 */
abstract class Square
{
	/**
	 * Row this square lies on.
	 */
	final public Row row;

	/**
	 * Column this square lies on.
	 */
	final public Column column;

	/**
	 * Box this square lies in.
	 */
	final public Box box;

	/**
	 * Sudoku board this square lies in.
	 */
	final public Board board;

	/**
	 * Create a square that lies on a particular column and row in a board.
	 * 
	 * @param colIndex Zero-based column index of the square.
	 * @param rowIndex Zero-based row index of the square.
	 * @param board The board that the square is part of.
	 */
	Square(int colIndex, int rowIndex, Board board)
	{
		this.column = board.column(colIndex);
		this.row = board.row(rowIndex);
		this.box = board.box(colIndex, rowIndex);
		this.board = board;
	}

	/**
	 * Obtain the value of this square.
	 * 
	 * @return The value of this square.
	 */
	abstract int value();
}
