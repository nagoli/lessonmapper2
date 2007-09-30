package util.ui.fileDialog;

import java.awt.Frame;
import java.io.File;
import java.io.FileFilter;

import javax.swing.JFileChooser;

import org.apache.commons.io.filefilter.WildcardFilter;

public class DefaultFileDialog extends OSOrientedFileDialog {

	JFileChooser itsFileChooser;
	protected Frame itsParentFrame;

	public DefaultFileDialog(Frame aParentFrame) {
		itsParentFrame = aParentFrame;
		itsFileChooser = new JFileChooser();
	}

	@Override
	public File showAsOpenDialog(String aTitle, String aWildcardFilter) {
		itsFileChooser.setDialogTitle(aTitle);
		if (aWildcardFilter != null) {
			final FileFilter theWildcardFilter = new WildcardFilter(
					aWildcardFilter);
			itsFileChooser
					.setFileFilter(new javax.swing.filechooser.FileFilter() {
						@Override
						public boolean accept(File aF) {
							return theWildcardFilter.accept(aF);
						}

						@Override
						public String getDescription() {
							// TODO Auto-generated method stub
							return null;
						}
					});
		} else
			itsFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int theAction = itsFileChooser.showOpenDialog(itsParentFrame);
		itsFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (theAction == JFileChooser.APPROVE_OPTION) {
			return itsFileChooser.getSelectedFile();
		}
		return null;
	}

	@Override
	public File showAsSaveDialog(String aTitle, String aWildcardFilter) {
		itsFileChooser.setDialogTitle(aTitle);
		if (aWildcardFilter != null) {
			final FileFilter theWildcardFilter = new WildcardFilter(
					aWildcardFilter);
			itsFileChooser
					.setFileFilter(new javax.swing.filechooser.FileFilter() {
						@Override
						public boolean accept(File aF) {
							return theWildcardFilter.accept(aF);
						}

						@Override
						public String getDescription() {
							// TODO Auto-generated method stub
							return null;
						}
					});
		} else
			itsFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int theAction = itsFileChooser.showSaveDialog(itsParentFrame);
		itsFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (theAction == JFileChooser.APPROVE_OPTION) {
			return itsFileChooser.getSelectedFile();
		}
		return null;
	}

}
