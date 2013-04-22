/**
 * A user-fillable (solvable, dynamic) square element of a Sudoku board.
 */
class DynamicSquare extends Square
{
	int value;
	
	DynamicSquare nextDynamicSquare;
	
	/**
	 * Create a dynamic square that lies on specified column and row of a Sudoku
	 * board.
	 * 
	 * @param colIndex Index of the column the square will lie on.
	 * @param rowIndex Index of the row the square will lie on.
	 * @param board The board that this square is part of.
	 */
	DynamicSquare(int colIndex, int rowIndex, Board board, DynamicSquare prevDynamicSquare)
	{
		super(colIndex, rowIndex, board);
		
		if(prevDynamicSquare != null)
		{
			prevDynamicSquare.nextDynamicSquare = this;
		}
	}

	/**
	 * The entry point that recursively solves this boards value using the
	 * so-called exhaustive ("brute force") approach.
	 * 
	 * @deprecated The squares should not contain methods that solve the board.
	 *             I would argue that a separate solver object that implements a
	 *             specific method of solving a Sudoku board takes care of the
	 *             solving entirely.
	 * 
	 * @param eventListener An object that is notified under the solving
	 *        process.
	 */
	@Deprecated void setNumberMeAndTheRest(Board.EventListener eventListener)
	{
		int tryValue;

		for(tryValue = value + 1; tryValue <= board.dimension; tryValue++)
		{
			if(isValidValue(tryValue))
			{
				setValue(tryValue, eventListener);

				if(nextDynamicSquare == null)
				{
					/**
					 * This and all previous square have now a valid value each.
					 * There are no more squares, so we have a solution on our
					 * hands.
					 */
					eventListener.onBoardSolutionComplete(board);
				}
				else
				{
					nextDynamicSquare.setNumberMeAndTheRest(eventListener);
				}
			}
			else
			{
				eventListener.onSolvingBadSquareValue(tryValue, this);
			}
		}

		/**
		 * No more squares or no valid values for this square. Clear the value
		 * and try another value for the previous square again.
		 */
		setValue(0, eventListener);
	}

	/**
	 * Resets the value of this square to specified value and sends a
	 * notification using the specified event listener object.
	 * 
	 * @param newValue New value of this square to set to.
	 * @param eventListener The object to notify of value reset.
	 */
	private void setValue(int newValue, Board.EventListener eventListener)
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
