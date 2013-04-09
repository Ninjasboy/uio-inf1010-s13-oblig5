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

	class Box
	{
		/** Right and bottom are edges beyond this box. */
		final int left, top, right, bottom;
		
		Box(int left, int top, int width, int height)
		{
			this.left = left;
			this.top = top;
			this.right = left + width;
			this.bottom = top + height;
		}
		
		boolean isValidValue(int tryValue, int[][] boardValueArray)
		{
			for(int i = top; i < bottom; i++)
			{
				for(int j = left; j < right; j++)
				{
					if(boardValueArray[i][j] == tryValue)
					{
						return false;
					}
				}
			}
			
			return true;
		}
	}
	public void solve(Board board, EventListener eventListener)
	{
		final int boardDimension = board.dimension, boardSize =
			board.dimension * board.dimension;

		final int[][] boardValueArray = new int[boardDimension][boardDimension];
		final int[][] solvableSquares = new int[boardSize][2];

		loadBoardData(board, boardValueArray, solvableSquares);
		
		final Box[][] boxMap = newBoxMap(board);
				
		outerLoop: for(int i = 0;;)
		{
			final int x = solvableSquares[i][0];
			final int y = solvableSquares[i][1];

			for(int tryValue = boardValueArray[y][x] + 1; tryValue <= boardDimension; tryValue++)
			{
				eventListener.onSolverTryBoardValue(board, tryValue, x, y);
				
				if(isValidValue(tryValue, x, y, boardValueArray, board, boxMap))
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

	private void loadBoardData(Board board, int[][] values,
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

	private Box[][] newBoxMap(Board board)
	{
		final Box[][] boxMap = new Box[board.dimension][board.dimension];
		
		final int boardDimension = board.dimension, boxWidth = board.boxWidth, boxHeight = board.boxHeight;
		
		for(int y = 0; y < boardDimension; y++)
		{
			for(int x = 0; x < boardDimension; x++)
			{
				boxMap[y][x] = new Box(x - (x % boxWidth), y - (y % board.boxHeight), boxWidth, boxHeight); 
			}
		}
		
		return boxMap;
	}
	
	boolean isValidValue(int tryValue, int x, int y, int[][] boardValueArray,
		Board board, Box[][] boxMap)
	{
		assert tryValue != boardValueArray[y][x];

		for(int i = 0; i < boardValueArray.length; i++)
		{
			if(	boardValueArray[y][i] == tryValue ||
				boardValueArray[i][x] == tryValue)
			{
				return false;
			}
		}
		
		return boxMap[y][x].isValidValue(tryValue, boardValueArray);
	}
}
