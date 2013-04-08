import java.io.IOException;
import java.io.Writer;

/**
 * A utility class for auxiliary methods.
 */
public class Utils
{
	/**
	 * Platform specific newline string of characters.
	 */
	static final String newLine = System.getProperty("line.separator"); //$NON-NLS-1$

	/**
	 * Write newline string.
	 * 
	 * An alternative would be to use BufferedWriter but this is more generic
	 * and allows to use its underlying superclass.
	 * 
	 * @param writer The writer to use to write newline.
	 * @throws IOException If writing fails. @see Writer Writer class
	 */
	public static void writeNewLine(Writer writer) throws IOException
	{
		writer.write(newLine);
	}	
}
