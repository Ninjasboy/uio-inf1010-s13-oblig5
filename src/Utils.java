package inf1010.oblig3;

import java.io.IOException;
import java.io.Writer;

/**
 * A class that includes useful methods.
 * 
 * @author armenmi
 * 
 */
public class Utils {

	static final String newLine = System.getProperty("line.separator");

	/**
	 * Aids in writing new lines into Writer streams.
	 * 
	 * Alternative is using BufferedWriter but this is more generic and allows
	 * to use its underlying superclass.
	 * 
	 * @param writer
	 * @throws IOException
	 */
	public static void writeNewLine(Writer writer) throws IOException {
		writer.write(newLine);
	}
}
