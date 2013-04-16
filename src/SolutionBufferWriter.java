import java.io.IOException;
import java.io.Writer;

/**
 * A solution buffer writer.
 * 
 * Feeds an entire set of solutions to a <code>Writer</code> object.
 */
class SolutionBufferWriter
{
	/**
	 * Write a solution buffer using a specified <code>Writer</code>.
	 * 
	 * @param solutionBuffer The solution buffer to write.
	 * @param writer The <code>Writer</code> object that realizes the actual
	 *        writing.
	 * 
	 * @throws IOException An I/O error occurs during writing.
	 */
	public void write(SolutionBuffer solutionBuffer, Writer writer)
		throws IOException
	{
		for(int i = 0; i < solutionBuffer.size(); i++)
		{
			writeSolution(solutionBuffer, i, writer);
		}
	}

	/**
	 * Write a single solution specified by its index.
	 * 
	 * @param solutionBuffer The solution container.
	 * @param index Index of the solution to use.
	 * @param writer The <code>Writer</code> object that realizes the actual
	 *        writing.
	 * 
	 * @throws IOException An I/O error occurs during writing.
	 */
	private void writeSolution(SolutionBuffer solutionBuffer, int index,
		Writer writer) throws IOException
	{
		writer.write(Integer.toString(index + 1) + ": "); //$NON-NLS-1$

		final int[][] data = solutionBuffer.get(index);

		for(int y = 0; y < data.length; y++)
		{
			for(int x = 0; x < data.length; x++)
			{
				assert data[y][x] != 0;

				writer.write(solutionBuffer.board
					.charFromSquareValue(data[y][x]));
			}

			writer.write("// "); //$NON-NLS-1$
		}

		Utils.writeNewLine(writer);
	}
}
