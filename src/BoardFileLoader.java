import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class manages loading Sudoku boards from files.
 * 
 * Files are written in a specific format.
 * 
 * @author armenmi
 * 
 */
class BoardFileLoader {
	static int valueFromChar(char c) {
		if(c >= '1' && c <= '9') {
			return c - '0';
		} else if(c >= 'A' && c <= 'Z') {
			return c - '@';
		} else if(c == '@') {
			return 36;
		}

		return -1;
	}

	/**
	 * Loads a board from a file.
	 * 
	 * @param file
	 *            File with the board data
	 * @return A Board object with loaded values.
	 * @throws IOException
	 *             If an error occurs during doing file I/O
	 * @throws Exception
	 *             If file data is in an invalid format during loading.
	 */
	Board boardFromFile(File file) throws IOException, Exception {

		BufferedReader reader = null;

		reader = new BufferedReader(new FileReader(file));

		int dimension = -1, boxHeight = -1, boxWidth = -1;

		// / Parse header

		dimension = Integer.parseInt(reader.readLine());
		boxHeight = Integer.parseInt(reader.readLine());
		boxWidth = Integer.parseInt(reader.readLine());

		int[][] boardData = new int[dimension][dimension];

		for(int y = 0; y < dimension; y++) {
			String line = null;

			line = reader.readLine();

			if(line == null) {
				throw new Exception("File corrupt.");
			}

			if(line.length() != dimension) {
				throw new Exception("File corrupt.");
			}

			for(int x = 0; x < dimension; x++) {
				char c = line.charAt(x);

				int value;

				if(c == '.') {
					value = 0;
				} else {

					value = valueFromChar(c);

					if(value == -1) {
						throw new Exception("File corrupt.");
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
}
