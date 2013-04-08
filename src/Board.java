/**
 * Model of a Sudoku board.
 * 
 * Includes an event listener, that is notified of changes in the model state.
 * 
 * Fra oppgaveteksten: "brett er alle n x n ruter."
 * 
 * @author armenmi
 * 
 */
class Board
{
	/**
	 * Event listener for board solving process.
	 * 
	 * Is notified under solving process of various relevant events.
	 */
	interface EventListener
	{
		void onBoardSolutionComplete(Board board);

		void onBoardAllSolutionsComplete(Board board);

		void onResetBoardSquareValue(DynamicSquare square);

		void onSolvingBadSquareValue(int badValue, DynamicSquare square);
	}

	/**
	 * Creates and returns a new board that has all its squares empty.
	 * 
	 * @param dimension Dimension of the board
	 * @param boxWidth Width of each box in the board
	 * @param boxHeight Height of each box in the board
	 * @return a new Board object.
	 */
	static Board emptyBoard(int dimension, int boxWidth, int boxHeight)
	{
		Board board = new Board(dimension, boxWidth, boxHeight);

		for(int y = 0; y < board.dimension; y++)
		{
			for(int x = 0; x < board.dimension; x++)
			{
				board.squares[y][x] = new DynamicSquare(x, y, board);
			}
		}

		return board;
	}

	/**
	 * Creates a board and initializes it with values specified by an array.
	 * 
	 * @param dimension Dimension of the board
	 * @param boxWidth Width of the board
	 * @param boxHeight Height of the board
	 * @param boardData Array of values for the board, 0 signifies dynamic
	 *        (user-fillable) squares
	 * @param title Title of the board
	 * @return A new board object
	 */
	static Board boardFromArray(int dimension, int boxWidth, int boxHeight,
		int[][] boardData)
	{
		Board board = new Board(dimension, boxWidth, boxHeight);

		for(int y = 0; y < board.dimension; y++)
		{
			for(int x = 0; x < board.dimension; x++)
			{
				board.squares[y][x] =
					(boardData[y][x] != 0)
						? new StaticSquare(boardData[y][x], x, y, board)
						: new DynamicSquare(x, y, board);						
			}
		}

		return board;
	}

	/** Dimension of this board. */
	public final int dimension;

	/** Width of each box in this board. */
	public final int boxWidth;

	/** Width of each box in this board. */
	public final int boxHeight;

	/**
	 * An array of N arrays, each of M squares, where N is amount of rows and M
	 * is amount of columns in this board.
	 */
	final private Square[][] squares;

	/** Array of rows in this board. */
	final private Row[] rows;

	/** Array of columns in this board. */
	final private Column[] cols;

	/** Array of boxes in this board. */
	final private Box[][] boxes;

	/**
	 * Creates a new board of specified dimensions without squares.
	 * 
	 * @param dimension Dimension of new board
	 * @param boxWidth Width of each box in the board
	 * @param boxHeight Height of each box in the board
	 */
	private Board(int dimension, int boxWidth, int boxHeight)
	{
		/** Boxes must fit tightly into the board. */
		if((dimension % boxWidth) != 0 || (dimension % boxHeight) != 0)
		{
			throw new IllegalArgumentException();
		}

		squares = new Square[dimension][dimension];

		this.dimension = dimension;

		this.boxWidth = boxWidth;
		this.boxHeight = boxHeight;

		// / Create rows

		rows = new Row[dimension];

		for(int y = 0; y < dimension; y++)
		{
			this.rows[y] = new Row(y, this);
		}

		// / Create columns

		cols = new Column[dimension];

		for(int x = 0; x < dimension; x++)
		{
			this.cols[x] = new Column(x, this);
		}

		// / Create boxes

		boxes = new Box[dimension / boxHeight][dimension / boxWidth];

		for(int y = 0; y < boxes.length; y++)
		{
			for(int x = 0; x < boxes[y].length; x++)
			{
				boxes[y][x] =
					new Box(x * boxWidth, y * boxHeight, x * (boxWidth + 1), y
						* (boxHeight + 1), this);
			}
		}
	}

	public int value(int colIndex, int rowIndex)
	{
		return square(colIndex, rowIndex).value();
	}

	public Square square(int colIndex, int rowIndex)
	{
		return squares[rowIndex][colIndex];
	}

	public Column column(int index)
	{
		return cols[index];
	}

	public Row row(int index)
	{
		return rows[index];
	}

	public Box box(int colIndex, int rowIndex)
	{
		return boxes[rowIndex / boxHeight][colIndex / boxWidth];
	}

	/**
	 * Returns next (top-left to bottom-right direction) user-fillable square.
	 * 
	 * @param square A square object to start from
	 * @return Closest user-fillable square object.
	 */
	DynamicSquare nextDynamicSquare(Square square)
	{
		return firstDynamicSquare(square.column.index() + 1, square.row.index());
	}

	/**
	 * Returns first empty square in the board. @see Board.nextDynamicSquare()
	 * 
	 * @param colIndex Index of the column from which to start
	 * @param rowIndex Index of the row from which to start
	 * @return First dynamic (user-fillable) square to the right and/or bottom
	 *         of specified column and row.
	 */
	DynamicSquare firstDynamicSquare(int colIndex, int rowIndex)
	{

		int x = colIndex;

		for(int y = rowIndex; y < dimension; y++)
		{
			while(x < dimension)
			{
				if(
				/* squares[y][x].value() == 0 */
				squares[y][x] instanceof DynamicSquare)
				{
					return (DynamicSquare)squares[y][x];
				}

				x++;
			}

			x = 0;
		}

		return null;
	}

	/* Box box(int colIndex, int rowIndex) { return boxes[rowIndex / boxHeight *
	 * (dimension / boxWidth) + colIndex / boxWidth]; } */

	/* int value(int colIndex, int rowIndex) { return
	 * squares[rowIndex][colIndex].value; } */

	/**
	 * Initiates the solving sequence for this board.
	 * 
	 * Causes notification of the event listener under the solving.
	 * 
	 * @deprecated The board should not contain methods like solving itself,
	 *             this does not play well with principles of good object
	 *             oriented system design. This is left to only comply with task
	 *             specification.
	 * 
	 * @return
	 */
	@Deprecated void solve(EventListener eventListener)
	{
		firstDynamicSquare(0, 0).setNumberMeAndTheRest(eventListener);

		eventListener.onBoardAllSolutionsComplete(this);
	}

	char charFromSquareValue(int value)
	{
		if(value < 1 || value > 36)
		{
			throw new IndexOutOfBoundsException("Value out of range.");
		}

		return (value < 36) ? ((char)((value < 10) ? (value + 48)
			: (value + 55))) : '@';
	}
}
