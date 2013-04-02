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
class SolutionBufferWriter
{
	final private Writer writer;

	SolutionBufferWriter(Writer writer)
	{
		this.writer = writer;
	}

	/**
	 * Writes a solution buffer using the writer.
	 */
	void write(SudokuBuffer solutionBuffer) throws IOException
	{
		for(int i = 0; i < solutionBuffer.getSolutionCount(); i++)
		{
			writeSolution(solutionBuffer, i);
		}
	}

	/**
	 * Writes a single solution.
	 * 
	 * @param data buffer of values (usually returned by SudokuBuffer.get.
	 * @param index Index of the solution to use.
	 * @throws IOException
	 */
	void writeSolution(SudokuBuffer solutionBuffer, int index)
			throws IOException
	{
		// System.err.println("Writing solution #" + (index + 1));

		writer.write(Integer.toString(index + 1) + ": "); //$NON-NLS-1$

		final int[][] data = solutionBuffer.get(index);

		for(int y = 0; y < data.length; y++)
		{
			for(int x = 0; x < data.length; x++)
			{
				int value = data[y][x];

				if(value == 0)
				{
					value = solutionBuffer.board.value(x, y);
				}

				writer.write(solutionBuffer.board.charFromSquareValue(value));
			}

			writer.write("// "); //$NON-NLS-1$
		}

		Utils.writeNewLine(writer);
	}
}
