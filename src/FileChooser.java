import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

import javax.swing.JFileChooser;

/**
 * This class implements a level of abstraction to let a user choose a file,
 * based on either Swing or AWT APIs. It hides the particularities of either API
 * and presents a common API itself instead.
 * 
 * @author armenmi
 * 
 */
class FileChooser
{
	private File file;

	/**
	 * Creates a file chooser dialog.
	 * 
	 * @param useNative <code>true</code> to currently use AWT,
	 *        <code>false</code> to use Swing.
	 * @param title Title of the new dialog
	 * @param isSaveDialog <code>true</code> if the dialog is for saving
	 *        file(s), <code>false</code> otherwise.
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

	final File getFile()
	{
		return file;
	}

	private void createNative(String title, boolean isSaveDialog)
	{
		FileDialog fileDialog = new FileDialog((Frame)null, title,
				isSaveDialog ? FileDialog.SAVE : FileDialog.LOAD);

		fileDialog.setVisible(true);

		file =
				(fileDialog.getFile() != null) ? (new File(
						fileDialog.getDirectory() + fileDialog.getFile()))
						: null;
	}

	private void createSwing(String title, boolean isSaveDialog)
	{
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setDialogTitle(title);
		fileChooser.setDialogType(isSaveDialog ? JFileChooser.SAVE_DIALOG
				: JFileChooser.OPEN_DIALOG);

		fileChooser.showOpenDialog(null);

		file = fileChooser.getSelectedFile();
	}
}
