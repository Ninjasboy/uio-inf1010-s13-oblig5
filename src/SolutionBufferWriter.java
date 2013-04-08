import java.io.IOException;
import java.io.Writer;

/**
 * A solution buffer writer.
 * 
 * Writes solution set to a text file in a particular format.
 */
class SolutionBufferWriter
{
	/**
	 * Writer to use.
	 */
	final private Writer writer;

	/**
	 * Creates a solution buffer writer that uses the specified writer object.
	 * 
	 * @param writer Writer to use.
	 */
	SolutionBufferWriter(Writer writer)
	{
		this.writer = writer;
	}

	/**
	 * Writes a solution buffer.
	 * 
	 * @throws IOException An I/O error occurs during writing.
	 */
	public void write(SolutionBuffer solutionBuffer) throws IOException
	{
		for(int i = 0; i < solutionBuffer.size(); i++)
		{
			writeSolution(solutionBuffer, i);
		}
	}

	/**
	 * Writes a single solution specified by its index.
	 * 
	 * @param solutionBuffer The solution container.
	 * @param index Index of the solution to use.
	 * @throws IOException An I/O error occurs during writing.
	 */
	private void writeSolution(SolutionBuffer solutionBuffer, int index)
			throws IOException
	{
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
