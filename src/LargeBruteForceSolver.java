/**
 * A slower-start brute force Sudoku board solver.
 * 
 * It prepares data structures in preferred layout in the beginning and then
 * performs efficient exhaistive search for solutions.
 * 
 * Because of the initial preparation stage involved in the solving process,
 * this method may prove more beneficial for scenarios where the number of
 * solutions and squares on the board outweighs the costs involved in loading
 * the data structures. The solver is called "large" both because it benefits
 * larger boards and because it achieves its runtime speed efficiency at the
 * cost of memory.
 */
public class LargeBruteForceSolver
{
	interface EventListener
	{
		void onSolverTryBoardValue(Board board, int tryValue, int x, int y);
		
		void onBoardSolutionComplete(Board board, int[][] boardValueArray);

		void onBoardSolvingBadValue(Board board, int tryValue, int x, int y);

		void onBoardAllSolutionsComplete(Board board);

		void onResetBoardValue(Board board, int value, int x, int y);
	}

	public void solve(Board board, EventListener eventListener)
	{
		final int boardDimension = board.dimension, boardSize =
			board.dimension * board.dimension;

		int[][] boardValueArray = new int[boardDimension][boardDimension];
		int[][] solvableSquares = new int[boardSize][2];

		loadBoardData(board, boardValueArray, solvableSquares);

		outerLoop: for(int i = 0;;)
		{
			int x = solvableSquares[i][0];
			int y = solvableSquares[i][1];

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
						eventListener.onBoardSolutionComplete(board, boardValueArray);
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

	public void loadBoardData(Board board, int[][] values,
		int[][] solvableSquares)
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

	boolean isValidValue(int tryValue, int x, int y, int[][] boardValueArray,
		Board board)
	{
		assert tryValue != boardValueArray[y][x];

		for(int i = 0; i < boardValueArray.length; i++)
		{
			if(boardValueArray[y][i] == tryValue)
			{
				return false;
			}
		}

		for(int i = 0; i < boardValueArray.length; i++)
		{
			if(boardValueArray[i][x] == tryValue)
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
