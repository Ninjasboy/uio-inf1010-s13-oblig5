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
	 * Writes a solution buffer.
	 * 
	 * @throws IOException An I/O error occurs during writing.
	 */
	public void write(SolutionBuffer solutionBuffer, Writer writer) throws IOException
	{
		for(int i = 0; i < solutionBuffer.size(); i++)
		{
			writeSolution(solutionBuffer, i, writer);
		}
	}

	/**
	 * Writes a single solution specified by its index.
	 * 
	 * @param solutionBuffer The solution container.
	 * @param index Index of the solution to use.
	 * @throws IOException An I/O error occurs during writing.
	 */
	private void writeSolution(SolutionBuffer solutionBuffer, int index, Writer writer)
			throws IOException
	{
		writer.write(Integer.toString(index + 1) + ": "); //$NON-NLS-1$

		final int[][] data = solutionBuffer.get(index);
		
		for(int y = 0; y < data.length; y++)
		{
			for(int x = 0; x < data.length; x++)
			{
				assert data[y][x] != 0;
				
				writer.write(solutionBuffer.board.charFromSquareValue(data[y][x]));
			}

			writer.write("// "); //$NON-NLS-1$
		}

		Utils.writeNewLine(writer);
	}
}
