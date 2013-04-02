/**
 * Model of a user-fillable (solvable, dynamic) square element of a Sudoku
 * board.
 * 
 * @author amn
 * 
 */
class DynamicSquare extends Square
{

	DynamicSquare(int colIndex, int rowIndex, Board board)
	{
		super(colIndex, rowIndex, board);
	}

	/**
	 * Recursively aids in solving the board, square for square.
	 * 
	 * @deprecated The squares should not contain methods that solve the board.
	 *             I would argue that a separate solver object that implements a
	 *             specific method of solving a Sudoku board takes care of the
	 *             solving entirely.
	 */
	@Deprecated void setNumberMeAndTheRest()
	{
		int tryValue;

		for(tryValue = value + 1; tryValue <= board.dimension; tryValue++)
		{

			/*
			 * System.err.println("Trying value " + tryValue +
			 * " for square at row " + rowIndex + " column " + colIndex);
			 */

			if(row().isValidValue(tryValue) &&
					col().isValidValue(tryValue) &&
					box().isValidValue(tryValue))
			{

				/*
				 * System.err.println(colIndex + ":" + rowIndex + " " + tryValue
				 * + " WIN.");
				 */

				reset(tryValue);

				DynamicSquare nextDynamicSquare = board.nextDynamicSquare(this);

				if(nextDynamicSquare == null)
				{
					/**
					 * This and all previous square have gotten a valid value
					 * each. There are no more squares, so we have a solution on
					 * our hands.
					 */
					board.eventListener.onBoardSolutionComplete(board);
				}
				else
				{
					nextDynamicSquare.setNumberMeAndTheRest();
				}
			}
			else
			{

				/*
				 * System.err.println(colIndex + ":" + rowIndex + " " + tryValue
				 * + " FAIL.");
				 */

				board.eventListener.onSolvingBadSquareValue(tryValue, this);
			}
		}

		/**
		 * No more squares or no valid values for this square. Clear the value
		 * and try another value for the previous square again.
		 */
		reset(0);
	}

	/**
	 * Resets the value of this square to specified value.
	 * 
	 * Will notify the object that is set as event listener for the board.
	 * 
	 * @param newValue New value of this square to set to.
	 */
	void reset(int newValue)
	{
		this.value = newValue;

		board.eventListener.onResetBoardSquareValue(this);
	}
}
