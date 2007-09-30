package util.system.thumb;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

import util.system.FileManagement;

/**
 * This class enables the construction of thumbnails
 * It can be customized with setDefaultFrame and addWindowToHide
 * @author omotelet
 *
 */

public  class ThumbMaker {

	
	public  URL itsHTMLIcon = getClass().getResource("resources/safari.jpg");
	public  URL itsPDFIcon = getClass().getResource("resources/pdf.jpg");
	public  URL itsPPTIcon = getClass().getResource("resources/ppt.jpg");
	public  URL itsTextIcon = getClass().getResource("resources/text.jpg");
	public  URL itsMovIcon = getClass().getResource("resources/mov.jpg");
	public  URL itsImageIcon = getClass().getResource("resources/jpg.jpg");
	public  URL itsWordIcon = getClass().getResource("resources/doc.jpg");
	public  URL itsJavaIcon = getClass().getResource("resources/java.jpg");
	public  URL itsFlashIcon = getClass().getResource("resources/flash.jpg");
	public  URL itsKeynoteIcon = getClass().getResource("resources/key.jpg");
	
	public static ThumbMaker ITSInstance;
	
	
	public static ThumbMaker getThumbMaker() throws Exception{
		if (ITSInstance !=null) return ITSInstance;
		if (System.getProperty("os.name").startsWith("Mac")){
			ITSInstance = new MacThumbMaker();
			return ITSInstance;
		}
		ITSInstance = new ThumbMaker();
		return ITSInstance;
	}
	
	 final JFrame itsScreenTaker = new JFrame("Thumbnail");
	 final ThumbNotificationBox itsNotificationBox= new ThumbNotificationBox();
	 JButton itsScreenTakerButton;
	 String itsFirstExplanation="click here when ready to take the screenshot";
	 String itsSecondExplanation="select an area for your thumbnail";
	 Set<JFrame> itsListOfWindowsToHide = new HashSet<JFrame>();
	
	 ThumbMaker() {
		initScreenTaker();
		
	}

	public void setExplanations(String aFirstExplanation, String aSecondExplanation) {
		itsFirstExplanation=aFirstExplanation;
		itsSecondExplanation=aSecondExplanation;
		initScreenTaker();
	}
	
	public void addWindowToHide(JFrame aWindowToHide) {
		itsListOfWindowsToHide.add(aWindowToHide);
	}
	
	/**
	 * make the default thumb for this file and save it in aThumbname
	 * @param aFileName
	 * @param aThumbName
	 */
	public void makeDefaultThumb(String aFileName, String aThumbName) {
		String theExtension = aFileName.substring(aFileName.lastIndexOf(".")).toLowerCase();
		URL theThumb;
		if (theExtension.equals(".key"))
			theThumb = itsKeynoteIcon;
		else if (theExtension.equals(".ppt")
				|| theExtension.equals(".pps"))
			theThumb = itsPPTIcon;
		else if (theExtension.equals(".doc")||theExtension.equals(".rtf"))
			theThumb = itsWordIcon;
		else if (theExtension.equals(".swf"))
			theThumb = itsFlashIcon;
		else if (theExtension.equals(".jpg")
				|| theExtension.equals(".gif")
				|| theExtension.equals(".tiff")
				|| theExtension.equals(".png"))
			theThumb = itsImageIcon;
		else if (theExtension.equals(".mov")
				|| theExtension.equals(".avi")
				|| theExtension.equals(".mpg"))
			theThumb = itsMovIcon;
		else if (theExtension.equals(".pdf")
				|| theExtension.equals(".ps"))
			theThumb = itsPDFIcon;
		else if (theExtension.equals(".html"))
			theThumb = itsHTMLIcon;
		else if (theExtension.equals(".txt"))
			theThumb = itsTextIcon;
		else
			theThumb = itsHTMLIcon;
		try {
			File theThumbFile = new File(aThumbName);
			FileManagement.getFileManagement().copy(theThumb, theThumbFile);
		/*			theThumbFile.mkdirs();
			theThumbFile.createNewFile();
			ImageIO.write(ImageIO.read(theThumb),"jpeg", theThumbFile);
*/
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public  void makeAutomaticThumb(String aFileName, String aThumbName) throws ThumbNotBuildException{
		System.out.println("not supported OS");
	}
	
	/**
	 * open the file given as aFileName
	 * take a list of windows to hide and a texts giving the instruction to the user
	 * first : the moment when the user choose to takescreenshot
	 * second : the selection of the interesting part of the screen   
	 * @param aFileName
	 * @param aThumbName
	 * @param aListOfWondowToHide
	 * @throws ThumbNotBuildException
	 */
	public synchronized void makeInteractiveThumb(String aFileName, String aThumbName) throws ThumbNotBuildException{
		Map<JFrame,Integer> theWindowsStates = new HashMap<JFrame,Integer>(); 
		for (JFrame theWindow: itsListOfWindowsToHide) {
			theWindowsStates.put(theWindow,theWindow.getExtendedState());
			theWindow.setExtendedState(JFrame.ICONIFIED);
		}
		try {
			FileManagement.getFileManagement().openFile(aFileName,false);
		} catch (Exception e) {
			e.printStackTrace();
			restoreWindowState(theWindowsStates);
			throw new ThumbNotBuildException();
		}
		itsScreenTaker.setVisible(true);
		BufferedImage theImage = itsNotificationBox.pop();
		File theFile = new File (aThumbName);
		try {
			ImageIO.write(theImage,"jpg",theFile);
		} catch (IOException e) {
			e.printStackTrace();
			restoreWindowState(theWindowsStates);
			throw new ThumbNotBuildException();
		}
		restoreWindowState(theWindowsStates);
		
	}

	private void restoreWindowState(Map<JFrame, Integer> theWindowsStates) {
		for(JFrame theWindow: theWindowsStates.keySet()) {
			theWindow.setExtendedState(theWindowsStates.get(theWindow));
		}
	}
	
	public void initScreenTaker() {
		itsScreenTaker.getContentPane().removeAll();
		itsScreenTaker.setAlwaysOnTop(true);
		itsScreenTakerButton= new JButton(itsFirstExplanation);
		itsScreenTakerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aE) {
				itsScreenTaker.setVisible(false);
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Dimension screenSize = toolkit.getScreenSize();
				Rectangle screenRect = new Rectangle(screenSize);
				// create screen shot
				try {
					Robot robot = new Robot();
					BufferedImage image = robot.createScreenCapture(screenRect);
					new ScreenShotFrame(image,itsNotificationBox,itsSecondExplanation);
				}catch (Exception e) {
				}		
			}
		});
		//itsScreenTaker.getContentPane().setLayout(new BorderLayout());
		itsScreenTaker.getContentPane().add(itsScreenTakerButton);
		itsScreenTaker.pack();
		itsScreenTaker.setVisible(false);		
		
	}
	/**
	 * this method return an absolute path for this URL (URL should be file)
	 * so that windows system accept the request.
	 * @return
	 */
	public String getPathForURL(URL aURL){
		if (aURL.getProtocol().startsWith("http")) return aURL.toExternalForm();
		try{
			
		 return (new File(aURL.toURI()).getAbsolutePath());
		}
		catch (Exception e) {
			e.printStackTrace();
			return aURL.toExternalForm();
		}
	}
	
	
	
	public class ThumbNotBuildException extends Exception{

		public ThumbNotBuildException() {
			super("the thumb was not build");	
		}	
	}
	
	
}
