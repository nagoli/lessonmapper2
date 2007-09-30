package util.ui.fileDialog;

import java.awt.Frame;
import java.io.File;

/**
 * implement a os-dependent file dialog i.e. JFileChooser by default 
 * or FileDialog for mac
 * @author omotelet
 *
 */

public abstract class OSOrientedFileDialog {
	
	public static OSOrientedFileDialog create(Frame aParentFrame ){
		if (System.getProperty("os.name").startsWith("Mac"))
			return new MacFileDialog(aParentFrame); 
		else return new DefaultFileDialog(aParentFrame);
	}
	
	/**
	 * return the chosen file or null cancel
	 * @param aTitle
	 * @param aWildcardFilter
	 * @return
	 */
	public abstract File showAsSaveDialog(String aTitle, String aWildcardFilter);
	
	
	/**
	 * return the chosen file or null cancel
	 * @param aTitle
	 * @param aWildcardFilter
	 * @return
	 */
	public abstract File showAsOpenDialog(String aTitle, String aWildcardFilter);
	
	
}
