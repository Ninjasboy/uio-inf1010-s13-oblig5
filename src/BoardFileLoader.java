import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Implements loading of Sudoku boards from file.
 */
class BoardFileLoader
{
	static int valueFromChar(char c)
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

		throw new IllegalArgumentException("Invalid character supplied.");
	}

	/**
	 * Loads a board from a file.
	 * 
	 * @param file File with the board data
	 * @return A Board object with loaded values.
	 * @throws IOException If an error occurs during doing file I/O
	 * @throws Exception If file data is in an invalid format during loading.
	 */
	Board boardFromFile(File file) throws IOException, Exception
	{		
		BufferedReader reader = new BufferedReader(new FileReader(file));

		try
		{
			// / Parse header

			int dimension = Integer.parseInt(reader.readLine());
			int boxHeight = Integer.parseInt(reader.readLine());
			int boxWidth = Integer.parseInt(reader.readLine());

			int[][] boardData = new int[dimension][dimension];

			for(int y = 0; y < dimension; y++)
			{
				String line = null;

				line = reader.readLine();

				if(line == null)
				{
					throw new Exception("File corrupt.");
				}

				if(line.length() != dimension)
				{
					throw new Exception("File corrupt.");
				}

				for(int x = 0; x < dimension; x++)
				{
					char c = line.charAt(x);

					int value;

					if(c == '.')
					{
						value = 0;
					}
					else
					{
						try
						{
							value = valueFromChar(c);
						}
						catch(IllegalArgumentException e)
						{
							throw new RuntimeException("File corrupt.", e);
						}
					}

					boardData[y][x] = value;
				}
			}

			return Board.boardFromArray(
					dimension,
					boxWidth,
					boxHeight,
					boardData,
					file.getPath());
		}
		finally
		{
			reader.close();
		}
	}
}
