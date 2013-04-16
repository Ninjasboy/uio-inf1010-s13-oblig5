/**
 * Model of a Sudoku board.
 */
class Board
{
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
		/** Boxes must "fit" into the board. */
		if((dimension % boxWidth) != 0 || (dimension % boxHeight) != 0)
		{
			throw new IllegalArgumentException("Board boxes do not fit nicely into the board.");
		}

		squares = new Square[dimension][dimension];

		this.dimension = dimension;

		this.boxWidth = boxWidth;
		this.boxHeight = boxHeight;

		/** Create rows. */

		rows = new Row[dimension];

		for(int y = 0; y < dimension; y++)
		{
			this.rows[y] = new Row(y, this);
		}

		/** Create columns. */

		cols = new Column[dimension];

		for(int x = 0; x < dimension; x++)
		{
			this.cols[x] = new Column(x, this);
		}

		/** Create boxes. */

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

	/**
	 * Obtain the row at specified index.
	 * 
	 * @param index Index of row to retrieve.
	 * 
	 * @return The row at specified index.
	 */
	public Column column(int index)
	{
		return cols[index];
	}

	/**
	 * Obtain the row at specified index.
	 * 
	 * @param index Index of row to retrieve.
	 * 
	 * @return The row at specified index.
	 */
	public Row row(int index)
	{
		return rows[index];
	}

	/**
	 * The box including specified column and row.
	 * 
	 * @param colIndex A column.
	 * @param rowIndex A row.
	 * 
	 * @return The box including specified column and row.
	 */
	public Box box(int colIndex, int rowIndex)
	{
		return boxes[rowIndex / boxHeight][colIndex / boxWidth];
	}

	/**
	 * Obtain character value from board value.
	 * 
	 * A character value matches what is displayed on the board. A board value
	 * of 10 will f.e. match the character 'A' and so on.
	 * 
	 * @param value The board value.
	 * @return The character matching the specified value.
	 */
	char charFromSquareValue(int value)
	{
		if(value < 1 || value > 36)
		{
			throw new IndexOutOfBoundsException("Value " + value
				+ " is out of range.");
		}

		return (value < 36) ? ((char)((value < 10) ? (value + 48)
			: (value + 55))) : '@';
	}
}
