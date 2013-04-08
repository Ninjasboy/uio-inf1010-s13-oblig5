/**
 * Models a single square of a Sudoku board.
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
		
	abstract public int value();	
}
