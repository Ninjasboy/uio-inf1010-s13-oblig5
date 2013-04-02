/**
 * Fra oppgaveteksten: "rute er den minste enheten og er navnet vi bruker om de
 * minste enhetene på brettet; feltet som det kan stå ett tall (eller en
 * bokstav) i."
 * 
 * @author armenmi
 * 
 */
class Square
{
	public final int colIndex;
	public final int rowIndex;
	public final Board board;

	protected int value;

	/**
	 * Creates a dynamic square at specified column and row, that is part of
	 * specified board.
	 * 
	 * @param colIndex Zero-based column index of the new square on the board.
	 * @param rowIndex Zero-based row index of the new square on the board.
	 * @param board The board that the new square is part of.
	 */
	Square(int colIndex, int rowIndex, Board board)
	{
		this.colIndex = colIndex;
		this.rowIndex = rowIndex;
		this.board = board;
	}

	int getValue()
	{
		return value;
	}
	
	Row row()
	{
		return board.row(rowIndex);
	}

	Column col()
	{
		return board.col(colIndex);
	}

	Box box()
	{
		return board.box(colIndex, rowIndex);
	}	
}
