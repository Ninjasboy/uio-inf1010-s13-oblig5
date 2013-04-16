/**
 * A slow-start brute force Sudoku board solver.
 * 
 * It caches data it needs for efficient solving first and then performs fast
 * exhaustive search for solutions.
 * 
 * Because of the initial preparation stage involved in the solving process,
 * this method may prove more beneficial for scenarios where the number of
 * solutions and squares on the board outweighs the costs involved in loading
 * the data structures. The solver is called "large" both because it benefits
 * from larger Sudoku boards and because it achieves its runtime speed
 * efficiency at the cost of memory and slower startup.
 */
public class LargeBruteForceSolver
{
	/**
	 * The event listener used to notify of events during solving of a Sudoku
	 * board.
	 */
	interface EventListener
	{
		/**
		 * Is called when a value of a board square is about to be tested for
		 * validity.
		 * 
		 * @param board The board that is being solved.
		 * @param tryValue The value that is being tried for validity.
		 * @param colIndex The index of the column of the value.
		 * @param rowIdex The index of the row of the value..
		 */
		void onSolverTryBoardValue(Board board, int tryValue, int x, int y);

		/**
		 * Is called when a single solution is found for a board.
		 * 
		 * @param board The board for which the solution has been found.
		 * @param boardValueArray The array of values that comprises the
		 *        solution.
		 */
		void onBoardSolutionComplete(Board board, int[][] boardValueArray);

		/**
		 * Is called when a value of a board square is found to be invalid.
		 * 
		 * @param board The board that is being solved.
		 * @param badValue The tried value that turned out to be invalid.
		 * @param colIndex The index of the column of the value.
		 * @param rowIdex The index of the row of the value.
		 */
		void onBoardSolvingBadValue(Board board, int tryValue, int x, int y);

		/**
		 * Is called when all of the boards solutions have been found.
		 * 
		 * @param board The board that has been solved.
		 */
		void onBoardAllSolutionsComplete(Board board);

		/**
		 * Is called when a board square value is reset.
		 * 
		 * @param board The board that is being solved.
		 * @param value The new value of the square.
		 * @param colIndex The index of the column of the value.
		 * @param rowIdex The index of the row of the value.
		 */
		void onResetBoardValue(Board board, int value, int x, int y);
	}

	/**
	 * Solve the specified board, notifying the specified event listener under
	 * way.
	 * 
	 * @param board The board to solve.
	 * @param eventListener The event listener to use for notifications.
	 */
	void solve(Board board, EventListener eventListener)
	{
		final int boardDimension = board.dimension, boardSize =
			board.dimension * board.dimension;

		final int[][] boardValueArray = new int[boardDimension][boardDimension];
		final int[][] solvableSquares = new int[boardSize][2];

		loadTables(board, boardValueArray, solvableSquares);

		outerLoop: for(int i = 0;;)
		{
			final int x = solvableSquares[i][0];
			final int y = solvableSquares[i][1];

			for(int tryValue = boardValueArray[y][x] + 1; tryValue <= boardDimension; tryValue++)
			{
				eventListener.onSolverTryBoardValue(board, tryValue, x, y);

				if(isValidValue(tryValue, x, y, boardValueArray, board))
				{
					boardValueArray[y][x] = tryValue;
					eventListener.onResetBoardValue(board, tryValue, x, y);

					if(solvableSquares[i + 1] == null) /** End of board reached? */
					{
						/**
						 * Solution is complete.
						 */
						eventListener.onBoardSolutionComplete(board,
							boardValueArray);
					}
					else
					{
						i++;

						continue outerLoop;
					}
				}
				else
				{
					eventListener.onBoardSolvingBadValue(board, tryValue, x, y);
				}
			}

			boardValueArray[y][x] = 0;
			eventListener.onResetBoardValue(board, 0, x, y);

			if(--i < 0)
			{
				break;
			}
		}

		eventListener.onBoardAllSolutionsComplete(board);
	}

	/**
	 * Load auxilary tables for assisting with solving process.
	 * 
	 * @param board The board to load tables from.
	 * @param values The board value array.
	 * @param solvableSquares The array to load sequence of solvable square data
	 *        into.
	 */
	private void loadTables(Board board, int[][] values, int[][] solvableSquares)
	{
		int i = 0;

		for(int y = 0; y < board.dimension; y++)
		{
			for(int x = 0; x < board.dimension; x++)
			{
				final Square square = board.square(x, y);

				values[y][x] = square.value();

				if(square instanceof DynamicSquare)
				{
					solvableSquares[i][0] = x;
					solvableSquares[i][1] = y;

					i++;
				}
			}
		}

		solvableSquares[i] = null;
	}

	/**
	 * Test whether a given value is valid at specified position with regards to
	 * Sudoku rules.
	 * 
	 * @param tryValue The value to test validity for.
	 * @param x The index of the column of the value.
	 * @param y The index of the row of the value.
	 * @param boardValueArray The board value array.
	 * @param board The board that is being solved.
	 * 
	 * @return <code>true</code> if the value is valid, <code>false</code>
	 *         otherwise.
	 */
	private boolean isValidValue(int tryValue, int x, int y,
		int[][] boardValueArray, Board board)
	{
		assert tryValue != boardValueArray[y][x];

		for(int i = 0; i < boardValueArray.length; i++)
		{
			if(boardValueArray[y][i] == tryValue
				|| boardValueArray[i][x] == tryValue)
			{
				return false;
			}
		}

		{
			final int a = x - (x % board.boxWidth);
			final int b = y - (y % board.boxHeight);

			for(int i = b; i < b + board.boxHeight; i++)
			{
				for(int j = a; j < a + board.boxWidth; j++)
				{
					if(boardValueArray[i][j] == tryValue)
					{
						return false;
					}
				}
			}
		}

		return true;
	}
}
