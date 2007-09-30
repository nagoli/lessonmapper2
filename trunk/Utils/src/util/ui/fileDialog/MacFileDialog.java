package util.ui.fileDialog;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFilter;


public class MacFileDialog extends OSOrientedFileDialog {

	FileDialog itsFileDialog;
	
	
	
	
	public MacFileDialog(Frame aParentFrame) {
		itsFileDialog = new FileDialog(aParentFrame);
	}

	@SuppressWarnings("deprecation")
	@Override
	public File showAsOpenDialog(String aTitle, String aWildcardFilter) {
		itsFileDialog.setMode(FileDialog.LOAD);
		itsFileDialog.setTitle(aTitle);
		if (aWildcardFilter==null){
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
			itsFileDialog.setFilenameFilter( DirectoryFileFilter.INSTANCE );
		}
		else itsFileDialog.setFilenameFilter(new WildcardFilter(aWildcardFilter));
		itsFileDialog.show();
		System.setProperty("apple.awt.fileDialogForDirectories", "false");
		String theFile = itsFileDialog.getFile();
		if (theFile != null) {
			return new File(itsFileDialog.getDirectory()+theFile);
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public File showAsSaveDialog(String aTitle, String aWildcardFilter) {
		itsFileDialog.setMode(FileDialog.SAVE);
		itsFileDialog.setTitle(aTitle);
		if (aWildcardFilter==null) {
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
			itsFileDialog.setFilenameFilter(DirectoryFileFilter.INSTANCE);
		}
		else itsFileDialog.setFilenameFilter(new WildcardFilter(aWildcardFilter));
		itsFileDialog.show();
		System.setProperty("apple.awt.fileDialogForDirectories", "false");
		String theFile = itsFileDialog.getFile();
		if (theFile != null) {
			return new File(itsFileDialog.getDirectory()+theFile);
		}
		return null;
	}

}
