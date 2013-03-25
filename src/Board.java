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
	static Board emptyBoard(int dimension, int boxWidth, int boxHeight,
			String title)
	{
		Board board = new Board(dimension, boxWidth, boxHeight, title);

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
			int[][] boardData, String title)
	{
		Board board = new Board(dimension, boxWidth, boxHeight, title);

		for(int y = 0; y < board.dimension; y++)
		{
			for(int x = 0; x < board.dimension; x++)
			{

				board.squares[y][x] =
						(boardData[y][x] != 0) ? new StaticSquare(
								boardData[y][x], x, y, board)
								: new DynamicSquare(x, y,
										board);
			}
		}

		return board;
	}

	/**
	 * An object that has its methods called when changes to the board occur.
	 */
	public EventListener eventListener;

	/** Some form of a title for this board. */
	private String title;

	/** Dimension of this board. */
	private int dimension;

	/** Width of each box in this board. */
	private int boxWidth;

	/** Width of each box in this board. */
	private int boxHeight;

	/**
	 * An array of N arrays, each of M squares, where N is amount of rows and M
	 * is amount of columns in this board.
	 */
	private Square[][] squares;

	/** Array of rows in this board. */
	private Row[] rows;

	/** Array of columns in this board. */
	private Column[] cols;

	/** Array of boxes in this board. */
	private Box[] boxes;

	/**
	 * Creates a new board of specified dimensions without squares.
	 * 
	 * @param dimension Dimension of new board
	 * @param boxWidth Width of each box in the board
	 * @param boxHeight Height of each box in the board
	 */
	private Board(int dimension, int boxWidth, int boxHeight, String title)
	{

		/** Boxes must fit tightly into the board. */
		assert (dimension % boxWidth) == 0;
		assert (dimension % boxHeight) == 0;

		this.title = title;

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

		int nBoxes = (dimension / boxWidth) * (dimension / boxHeight);

		boxes = new Box[nBoxes];

		int i = 0;

		for(int y = 0; y < dimension; y += boxHeight)
		{
			for(int x = 0; x < dimension; x += boxWidth)
			{
				boxes[i++] = new Box(x, y, x + boxWidth, y + boxHeight, this);
			}
		}
	}

	/**
	 * Returns next (top-left to bottom-right direction) user-fillable square.
	 * 
	 * @param square A square object to start from
	 * @return Closest user-fillable square object.
	 */
	DynamicSquare nextDynamicSquare(Square square)
	{

		return firstDynamicSquare(square.colIndex + 1, square.rowIndex);
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

	final Row row(int index)
	{
		return rows[index];
	}

	final Square[] rowSquares(int index)
	{
		return squares[index];
	}

	final Column col(int index)
	{
		return cols[index];
	}

	final Box box(int colIndex, int rowIndex)
	{

		return boxes[rowIndex / boxHeight * (dimension / boxWidth) + colIndex
				/ boxWidth];
	}

	final Square square(int colIndex, int rowIndex)
	{
		return squares[rowIndex][colIndex];
	}

	final int value(int colIndex, int rowIndex)
	{
		return squares[rowIndex][colIndex].value();
	}

	/**
	 * Initiates the solving sequence for this board.
	 * 
	 * Causes notification of the event listener under the solving.
	 * 
	 * @deprecated The board should not contain methods like solving itself,
	 *             this does not play well with principles of good object
	 *             oriented system design. This is left in only to comply with
	 *             task specification.
	 * 
	 * @param eventListener
	 * @return
	 */
	void solve()
	{
		firstDynamicSquare(0, 0).setNumberMeAndTheRest();
		eventListener.onBoardAllSolutionsComplete(this);
	}

	final String title()
	{
		return title;
	}

	final int boxWidth()
	{
		return boxWidth;
	}

	final int boxHeight()
	{
		return boxHeight;
	}

	final int dimension()
	{
		return dimension;
	}
}
