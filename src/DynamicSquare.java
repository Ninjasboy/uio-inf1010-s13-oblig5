import com.sun.xml.internal.bind.annotation.OverrideAnnotationOf;

/**
 * A user-fillable (solvable, dynamic) square element of a Sudoku board.
 */
class DynamicSquare extends Square
{
	int value;
	
	/**
	 * Create a dynamic square that lies on specified column and row of a Sudoku
	 * board.
	 * 
	 * @param colIndex Index of the column the square will lie on.
	 * @param rowIndex Index of the row the square will lie on.
	 * @param board The board that this square is part of.
	 */
	DynamicSquare(int colIndex, int rowIndex, Board board)
	{
		super(colIndex, rowIndex, board);
	}

	/**
	 * Resets the value of this square to specified value and sends a
	 * notification using the specified event listener object.
	 * 
	 * @param newValue New value of this square to set to.
	 * @param eventListener The object to notify of value reset.
	 */
	private void setValue2(int newValue, Board.EventListener eventListener)
	{
		this.value = newValue;

		eventListener.onResetBoardSquareValue(this);
	}

	/**
	 * Check whether the specified value is a valid square value as far as
	 * Sudoku rules are concerned. This naturally involves checking whether the
	 * value is not duplicate across the row, column and the box that this
	 * square lies on.
	 * 
	 * @param value The value to check for validity.
	 * @return <code>true</code> if the value is accepted, <code>false</code>
	 *         otherwise.
	 */
	boolean isValidValue(int value)
	{
		return column.isValidValue(value) &&
				row.isValidValue(value) &&
				box.isValidValue(value);
	}
	
	public int value()
	{
		return value;
	}
}
