package inf1010.oblig3;

import java.io.IOException;
import java.io.Writer;

/**
 * Writes the solution buffer.
 * 
 * Uses a specific file format.
 * 
 * @author armenmi
 * 
 */
class SolutionBufferWriter {

	SudokuBuffer solutionBuffer;
	Writer writer;

	SolutionBufferWriter(SudokuBuffer solutionBuffer, Writer writer)
			throws IOException {
		write(solutionBuffer, writer);
	}

	/**
	 * Writes the buffer using specified writer.
	 */
	void write(SudokuBuffer solutionBuffer, Writer writer) throws IOException {
		this.writer = writer;
		this.solutionBuffer = solutionBuffer;

		for(int i = 0; i < solutionBuffer.getSolutionCount(); i++) {
			writeSolution(solutionBuffer.get(i), i);
		}
	}

	/**
	 * Writes a single solution.
	 * 
	 * @param data
	 *            buffer of values (usually returned by SudokuBuffer.get.
	 * @param index
	 *            Index of the solution to use.
	 * @throws IOException
	 */
	void writeSolution(int[][] data, int index) throws IOException {
		// System.err.println("Writing solution #" + (index + 1));

		writer.write(Integer.toString(index + 1) + ": ");

		for(int y = 0; y < data.length; y++) {
			/**
			 * A potential problem is that for a sequence that is say, "12345",
			 * it is impossible to know whether the values are 1, 23, 45 or 12,
			 * 3, 45 or another combination of these...
			 */

			for(int x = 0; x < data.length; x++) {
				int value = data[y][x];

				if(value == 0) {
					value = solutionBuffer.board().value(x, y);
				}

				writer.write(Integer.toString(value));
			}

			writer.write("// ");
		}

		Utils.writeNewLine(writer);
	}
}
