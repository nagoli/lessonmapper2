package util.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

public class FileManagement {

	private static FileManagement ITSInstance;

	public static FileManagement getFileManagement() throws Exception {
		if (ITSInstance != null)
			return ITSInstance;
		if (System.getProperty("os.name").startsWith("Mac")) {
			ITSInstance = new MacFileManagement();
			return ITSInstance;
		}
		ITSInstance = new FileManagement();
		return ITSInstance;

		// if (System.getProperty("os.name").startsWith("Win")){
		// ITSInstance = new WinFileManagement();
		// return ITSInstance;
		// }
		//		
		// throw new Exception("not supported os");

	}

	/**
	 * copy Afile to AnotherFile if aFile could not be copied locally return
	 * null
	 * 
	 * @param aFile
	 * @param anotherFile
	 * @return
	 */
	// public abstract boolean copy(String aFile, String anotherFile);
	public boolean copy(File aFile, File aDestinationFile) {

		try {
			if (aFile.isDirectory())
				FileUtils.copyDirectory(aFile, aDestinationFile);
			else
				FileUtils.copyFile(aFile, aDestinationFile);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean copy(URL aURL, File aDestinationFile) {
		try {
			FileUtils.copyURLToFile(aURL, aDestinationFile);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * remove all the elements of a directory
	 * 
	 * @param aDirectoryToClear
	 */
	public boolean cleanDirectory(File aDirectoryToClear) {
		try {
			FileUtils.cleanDirectory(aDirectoryToClear);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * open the file or the url
	 * 
	 * @param aFile
	 */
	public void openFile(String aFile, boolean isPresentationMode) {

		try {

			Object obj = Class.forName("java.awt.Desktop").getDeclaredMethod(
					"getDesktop").invoke(null);

			if (aFile.startsWith("http://")) {
				URI theURL;
				try {
					theURL = new URI(aFile);
				} catch (Exception e) {
					System.out.println("Malformed URL " + aFile);
					return;
				}
				try {
					Class[] urlClass = new Class[] { java.net.URI.class };
					Class.forName("java.awt.Desktop").getDeclaredMethod(
							"browse", urlClass).invoke(obj, theURL);
					// Desktop.getDesktop().browse(theURL);
				} catch (Exception e) {
					System.out.println("Problem opening File");
					return;
				}

			} else {
				File theFile;
				try {
					theFile = new File(aFile);
				} catch (Exception e) {
					System.out.println("File not found " + aFile);
					return;
				}

				try {
					Class[] f = new Class[] { java.io.File.class };
					Class.forName("java.awt.Desktop").getDeclaredMethod("open",
							f).invoke(obj, theFile);
				} catch (Exception e) {
					System.out.println("Problem opening File");
					return;
				}
			}

		} catch (Exception e) {
			System.out
					.println("WARNING: Java 1.6 is needed to be able to open files");
		}
	}

	/**
	 * unzip aFileToUnzip to aDestinationDirectory
	 * @param aFileToUnzip
	 * @param aDestinationDirectory
	 */
	
	public static void unzip(File aFileToUnzip, File aDestinationDirectory) {

		try {
			ZipFile zf = new ZipFile(aFileToUnzip);
			Enumeration zipEnum = zf.entries();
			String dir = aDestinationDirectory.getAbsolutePath();

			while (zipEnum.hasMoreElements()) {
				ZipEntry item = (ZipEntry) zipEnum.nextElement();

				if (item.isDirectory()) // Directory
				{
					File newdir = new File(dir + File.separator
							+ item.getName());
					System.out.print("Creating directory " + newdir + "..");
					newdir.mkdir();
					System.out.println("Done!");
				} else // File
				{
					String newfile = dir + File.separator + item.getName();
					System.out.print("Writing " + newfile + "..");

					InputStream is = zf.getInputStream(item);
					FileOutputStream fos = new FileOutputStream(newfile);

					int ch;

					while ((ch = is.read()) != -1) {
						fos.write(ch);
					}

					is.close();
					fos.close();

					System.out.println("Done!");
				}
			}

			zf.close();
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	
	public static void main(String[] args) {
		unzip(new File("/Users/omotelet/Desktop/Tutorial.zip"), new File("/Users/omotelet/Desktop"));
		
	}
	
	
	
}
