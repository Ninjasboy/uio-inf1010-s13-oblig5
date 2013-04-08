import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

import javax.swing.JFileChooser;

/**
 * A file chooser user interface dialog.
 * 
 * This class implements a level of abstraction to let a user choose a file,
 * based on either Swing or AWT APIs. It hides the particularities of either API
 * and presents a common API itself instead.
 */
class FileChooser
{
	/**
	 * The file chosen by this chooser.
	 */
	private File file;

	/**
	 * Create and show a file chooser dialog.
	 * 
	 * @param useNative <code>true</code> to use AWT, <code>false</code> to use
	 *        Swing.
	 * @param title Title of the dialog.
	 * @param isSaveDialog <code>true</code> if the dialog is for saving
	 *        file(s), <code>false</code> otherwise (loading).
	 */
	FileChooser(boolean useNative, String title, boolean isSaveDialog)
	{
		if(useNative)
		{
			createNative(title, isSaveDialog);
		}
		else
		{
			createSwing(title, isSaveDialog);
		}
	}

	final File file()
	{
		return file;
	}

	/**
	 * Create a native (AWT) file open/save dialog.
	 * 
	 * @param title Title of the dialog.
	 * @param isSaveDialog <code>true</code> if the dialog is for saving
	 *        file(s), <code>false</code> otherwise (loading).
	 */
	private void createNative(String title, boolean isSaveDialog)
	{
		final FileDialog fileDialog = new FileDialog((Frame)null, title,
				isSaveDialog ? FileDialog.SAVE : FileDialog.LOAD);

		fileDialog.setVisible(true);

		file =
				(fileDialog.getFile() != null) ? (new File(
						fileDialog.getDirectory() + fileDialog.getFile()))
						: null;
	}

	/**
	 * Create a Swing file open/save dialog.
	 * 
	 * @param title Title of the dialog.
	 * @param isSaveDialog <code>true</code> if the dialog is for saving
	 *        file(s), <code>false</code> otherwise (loading).
	 */
	private void createSwing(String title, boolean isSaveDialog)
	{
		final JFileChooser fileChooser = new JFileChooser();

		fileChooser.setDialogTitle(title);
		fileChooser.setDialogType(isSaveDialog ? JFileChooser.SAVE_DIALOG
				: JFileChooser.OPEN_DIALOG);

		fileChooser.showOpenDialog(null);

		file = fileChooser.getSelectedFile();
	}
}
