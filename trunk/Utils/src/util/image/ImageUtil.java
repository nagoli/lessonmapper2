package util.image;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;

import java.awt.image.BufferedImage;

public class ImageUtil {

	private static final GraphicsConfiguration CONFIG = getConfig();

	private static GraphicsConfiguration getConfig()
	{
		GraphicsEnvironment theGraphicsEnvironment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsConfiguration theConfig = theGraphicsEnvironment
				.getDefaultScreenDevice().getDefaultConfiguration();
		return theConfig;
	}

	/**
	 * Creates an image with the same data as the specified image, but in
	 * a format that is more efficient.
	 */
	public static BufferedImage createCompatibleImage(BufferedImage aImage, int transparency)
	{
		if (aImage == null) return null;
		int theW = aImage.getWidth();
		int theH = aImage.getHeight();
		BufferedImage theImage = CONFIG.createCompatibleImage(theW, theH,
				transparency);
		Graphics2D theImgGraphics = theImage.createGraphics();
		theImgGraphics.drawImage(aImage, 0, 0, null);
		return theImage;
	}

	
	
}
