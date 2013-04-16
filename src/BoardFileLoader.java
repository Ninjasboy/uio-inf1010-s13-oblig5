import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * A board loader.
 * 
 * Loads a board object using a <code>Reader</code> as input stream provider.
 */
class BoardFileLoader
{
	/**
	 * Represents a condition of invalid or suspicious input.
	 */
	class InputFormatException extends RuntimeException
	{
		final private Reader reader;

		InputFormatException(String message, Reader reader)
		{
			super(message);

			this.reader = reader;
		}

		InputFormatException(Throwable cause, Reader reader)
		{
			super(cause);

			this.reader = reader;
		}
	}

	/**
	 * Load a board using a <code>Reader</code>.
	 * 
	 * @param input A reader to use for reading input from.
	 * @return A Board object with loaded values.
	 * @throws IOException If an error occurs during doing file I/O
	 */
	Board loadBoard(Reader input) throws IOException
	{
		final BufferedReader reader =
			(input instanceof BufferedReader) ? ((BufferedReader)input)
				: (new BufferedReader(input));

		try
		{
			/** Parse header. */
			final int dimension = Integer.parseInt(reader.readLine());
			final int boxHeight = Integer.parseInt(reader.readLine());
			final int boxWidth = Integer.parseInt(reader.readLine());

			final int[][] boardData = new int[dimension][dimension];

			for(int y = 0; y < dimension; y++)
			{
				String line = reader.readLine();

				if(line == null)
				{
					throw new InputFormatException("Line of input expected here.", reader);
				}

				if(line.length() != dimension)
				{
					throw new InputFormatException("Wrong number of characters in input line.", reader);
				}

				for(int x = 0; x < dimension; x++)
				{
					final char c = line.charAt(x);

					try
					{
						boardData[y][x] = (c == '.') ? 0 : valueFromChar(c);
					}
					catch(IllegalArgumentException e)
					{
						throw new InputFormatException(e, reader);
					}
				}
			}

			return Board.boardFromArray(dimension, boxWidth, boxHeight,
				boardData);
		}
		finally
		{
			reader.close();
		}
	}
	
	/**
	 * Obtain board element value from a character.
	 * 
	 * Board characters are what is displayed in a Sudoku board, while values
	 * are their numeric counterparts.
	 * 
	 * @param c Character to obtain the value of.
	 * @return Value of the character provided.
	 */
	int valueFromChar(char c)
	{
		if(c >= '1' && c <= '9')
		{
			return c - '0';
		}
		else if(c >= 'A' && c <= 'Z')
		{
			return (c - 'A') + 10;
		}
		else if(c == '@')
		{
			return 36;
		}

		throw new IllegalArgumentException("Invalid character argument.");
	}	
}
