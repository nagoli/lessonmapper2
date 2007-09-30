package util.ui;

import java.awt.Image;
import java.util.Hashtable;

import javax.swing.ImageIcon;


import edu.umd.cs.piccolo.nodes.PImage;

/**
 * This class is used to scale images. It conatins a buffer for optimizing
 * multiple scapling of a same instance
 * 
 * @author omotelet
 * 
 */

public class ImageScaling {

	
	private static ImageScaling ITSInstance;
	
	public static ImageScaling getInstance() {
		if (ITSInstance==null)
			ITSInstance= new ImageScaling();
		return ITSInstance;
	}
	
	public  Hashtable<ImageAndFormatWrapper, ImageIcon> ITSScalingBuffer = new Hashtable<ImageAndFormatWrapper, ImageIcon>();
	
	
	public ImageIcon scaleImageIconUpTo(ImageIcon aIcon, int aMaxWidth,
			int aMaxHeight) {
		if (aIcon == null)
			return null;
		ImageAndFormatWrapper theWrapper = new ImageAndFormatWrapper(aIcon,
				aMaxWidth, aMaxHeight);
		if (!ITSScalingBuffer.containsKey(theWrapper)) {
			double theWidth = (double) aIcon.getIconWidth() / aMaxWidth;
			double theHeight = (double) aIcon.getIconHeight() / aMaxHeight;
			double theRatio = Math.max(theWidth, theHeight);
			Image theScaledImage = aIcon.getImage().getScaledInstance(
					(int) ((double) aIcon.getIconWidth() / theRatio),
					(int) ((double) aIcon.getIconHeight() / theRatio),
					Image.SCALE_SMOOTH);
			ImageIcon theNewIcon = new ImageIcon(theScaledImage);
			ITSScalingBuffer.put(theWrapper, theNewIcon);
		}
		return ITSScalingBuffer.get(theWrapper);
	}

	public void scalePImageIconTo(PImage aIcon, int aMaxWidth,
			int aMaxHeight) {
		if (aIcon == null || aIcon.getImage() == null)
			return;
		double theWidth = (double) aMaxWidth / aIcon.getImage().getWidth(null);
		double theHeight = (double) aMaxHeight
				/ aIcon.getImage().getHeight(null);
		double theRatio = Math.min(theWidth, theHeight);
		aIcon.setScale(theRatio);
	}

	public  class ImageAndFormatWrapper {
		ImageIcon itsIcon;

		int itsMaxWidth, itsMaxHeight;

		public ImageAndFormatWrapper(ImageIcon aIcon, int aMaxWidth,
				int aMaxHeight) {
			itsIcon = aIcon;
			itsMaxWidth = aMaxWidth;
			itsMaxHeight = aMaxHeight;
		}

		@Override
		public boolean equals(Object aObj) {
			if (aObj instanceof ImageAndFormatWrapper) {
				ImageAndFormatWrapper theWrapper = (ImageAndFormatWrapper) aObj;
				return theWrapper.itsIcon == itsIcon
						&& theWrapper.itsMaxHeight == itsMaxHeight
						&& theWrapper.itsMaxWidth == itsMaxWidth;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return itsIcon.hashCode()+itsMaxWidth+itsMaxHeight;
		}
		
	}

}
