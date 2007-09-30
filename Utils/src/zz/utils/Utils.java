package zz.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import zz.utils.ui.NamedObject;

/**
 * Contains useful methods. <p>
 *
 * @version 30-10-2001
 */
public final class Utils
{

	public static final Insets NULL_INSETS = new Insets (0, 0, 0, 0);

	public static final AffineTransform IDENTITY = new AffineTransform();

	/**
	 * Do not use this. <p>
	 */
	private Utils ()
	{
		throw new NoSuchMethodError ();
	}

	/**
	 * Reads the whole specified reader into a string an returns it.
	 */
	public static String readReader (Reader aReader) throws IOException
	{
		StringWriter sw = new StringWriter ();

		char[] cbuf = new char[1024];
		int n;
		while ((n = aReader.read (cbuf)) >= 0)
			sw.write (cbuf, 0, n);

		return sw.toString ();
	}

	/**
	 * Reads the whole specified InputStream into a string and returns it.
	 */
	public static String readInputStream (InputStream anInputStream) throws IOException
	{
		return readReader (new InputStreamReader (anInputStream));
	}

	/**
	 * Returns the greatest of the three specified integers.
	 */
	public static int max (int i1, int i2, int i3)
	{
		return Math.max (i1, Math.max (i2, i3));
	}

	/**
	 * Returns the greatest of the three specified doubles.
	 */
	public static double max (double d1, double d2, double d3)
	{
		return Math.max (d1, Math.max (d2, d3));
	}

	/**
	 * Returns the smallest of the three specified doubles.
	 */
	public static double min (double d1, double d2, double d3)
	{
		return Math.min (d1, Math.min (d2, d3));
	}

	/**
	 * Returns the frame that contains this component
	 */
	public static Frame getFrame (Component aComponent)
	{
		Container theCurrentContainer = aComponent.getParent ();
		while (theCurrentContainer != null)
		{
			if (theCurrentContainer instanceof Frame) return (Frame) theCurrentContainer;
			theCurrentContainer = theCurrentContainer.getParent ();
		}
		return null;
	}

	/**
	 * Returns the swing frame that contains this component
	 */
	public static JFrame getJFrame (JComponent aComponent)
	{
		Container theCurrentContainer = aComponent.getParent ();
		while (theCurrentContainer != null)
		{
			if (theCurrentContainer instanceof JFrame) return (JFrame) theCurrentContainer;
			theCurrentContainer = theCurrentContainer.getParent ();
		}
		return null;
	}

	/**
	 * Returns the JViewPort that contains this component
	 */
	public static JViewport getViewport (Component aComponent)
	{
		Container theCurrentContainer = aComponent.getParent ();
		while (theCurrentContainer != null)
		{
			if (theCurrentContainer instanceof JViewport) return (JViewport) theCurrentContainer;
			theCurrentContainer = theCurrentContainer.getParent ();
		}
		return null;
	}


	/**
	 * Resizes the specified icon.
	 * @return If rescaling is needed, returns a new resized icon. If the requested size is
	 * the size of the icon, anIcon is returned.
	 */
	public static ImageIcon resizeIcon (ImageIcon anIcon, int aWidth, int anHeight)
	{
		int theWidth = anIcon.getIconWidth ();
		int theHeight = anIcon.getIconHeight ();

		if (theWidth == aWidth && theHeight == anHeight) return anIcon;

		Image theImage = anIcon.getImage ();
		return new ImageIcon (theImage.getScaledInstance (aWidth, anHeight, Image.SCALE_SMOOTH));
	}

	private static Cursor itsEmptyCursor;

	static
	{
		BufferedImage theImage = new BufferedImage (1, 1, BufferedImage.TYPE_INT_ARGB);
		itsEmptyCursor = Toolkit.getDefaultToolkit ().createCustomCursor (theImage, new Point (0, 0), "empty");
	}

	public static Cursor getEmptyCursor ()
	{
		return itsEmptyCursor;
	}

	/**
	 * Returns the bounds of the default screen
	 */
	public static Rectangle getScreenBounds ()
	{
		GraphicsEnvironment theGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment ();
		GraphicsDevice theScreenDevice = theGraphicsEnvironment.getDefaultScreenDevice ();
		GraphicsConfiguration theDefaultConfiguration = theScreenDevice.getDefaultConfiguration ();
		return theDefaultConfiguration.getBounds ();
	}

	/**
	 * Returns the maximum window bounds (taking taskbar etc into account)
	 */
	public static Rectangle getMaximumWindowBounds ()
	{
		GraphicsEnvironment theGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment ();
		return theGraphicsEnvironment.getMaximumWindowBounds ();
	}

	public static final int MINIMUM_SIZE = 1;
	public static final int PREFERRED_SIZE = 2;
	public static final int MAXIMUM_SIZE = 3;

	/**
	 * Returns one of getPreferredSize, getMaximumSize or getMinimumSize of the specified
	 * component according to the value of aType.
	 * @param aComponent The component to get the size from
	 * @param aType Which size to get (must be one of MINIMUM_SIZE, PREFERRED_SIZE and MAXIMUM_SIZE)
	 */
	public static Dimension getASize (Component aComponent, int aType)
	{
		switch (aType)
		{
			case MINIMUM_SIZE:
				return aComponent.getMinimumSize ();

			case PREFERRED_SIZE:
				return aComponent.getPreferredSize ();

			case MAXIMUM_SIZE:
				return aComponent.getMaximumSize ();
		}

		throw new IllegalArgumentException ("aType must be one of MINIMUM_SIZE, PREFERRED_SIZE and MAXIMUM_SIZE");
	}

	/**
	 * Returns a color similar but lighter than the specified color.
	 * @param aCoefficient If 0, the returned color will be totally white.
	 * If 1, the returned color will be the same as the specified color.
	 */
	public static Color getLighterColor (Color aColor, float aCoefficient)
	{
		Color theLighterColor = null;
		if (aColor != null)
		{
			int r = (int) (255 - (255 - aColor.getRed ()) * aCoefficient);
			int g = (int) (255 - (255 - aColor.getGreen ()) * aCoefficient);
			int b = (int) (255 - (255 - aColor.getBlue ()) * aCoefficient);
			theLighterColor = new Color (r, g, b);
		}
		return theLighterColor;
	}

	public static Color getLighterColor (Color aColor)
	{
		return getLighterColor (aColor, 0.3f);
	}

	/**
	 * Tests if objects are both null or equal
	 */
	public static boolean equalOrBothNull (Object o1, Object o2)
	{
		if (o1 == null || o2 == null) return o1 == o2;
		else return o1 == o2 || o1.equals (o2);
	}

	/**
	 * Tests if objects are different
	 */
	public static boolean different (Object o1, Object o2)
	{
		return ! equalOrBothNull(o1, o2);
	}


	/**
	 * Appends to the specified StringBuffer:
	 * a comma (", ") if the buffer was not empty
	 * the specified number followed by a space
	 * either of the two strings, according to the value of the number (if 1, single is used)
	 */
	protected static void writePortion (StringBuffer aBuffer, int aNumber, String aSingleString, String aPluralString)
	{
		if (aBuffer.length () > 0) aBuffer.append (", ");
		aBuffer.append (aNumber);
		aBuffer.append (" ");
		aBuffer.append (aNumber == 1 ? aSingleString : aPluralString);
	}

	public static void simulateKeyTyped (Component target, char c)
	{
		EventQueue q = target.getToolkit ().getSystemEventQueue ();
		q.postEvent (new KeyEvent (target, KeyEvent.KEY_TYPED,
		        System.currentTimeMillis (), 0, KeyEvent.VK_UNDEFINED, c));
	}

	/**
	 * I'd rather not say what this method is for...
	 * See EasyJTable
	 */
	public static void simulateDeadKey (final Component target)
	{
		SwingUtilities.invokeLater (new Runnable ()
		{
			public void run ()
			{
				EventQueue q = target.getToolkit ().getSystemEventQueue ();

				q.postEvent (new KeyEvent (target, KeyEvent.KEY_PRESSED,
				        System.currentTimeMillis (), 0, KeyEvent.VK_ALT, '\u0000'));

				q.postEvent (new KeyEvent (target, KeyEvent.KEY_RELEASED,
				        System.currentTimeMillis (), 0, KeyEvent.VK_ALT, '\u0000'));
			}
		});
	}

	public static final Composite ALPHA_04 = AlphaComposite.getInstance (AlphaComposite.SRC_OVER, 0.4f);
	public static final Composite ALPHA_06 = AlphaComposite.getInstance (AlphaComposite.SRC_OVER, 0.6f);
	public static final Composite ALPHA_02 = AlphaComposite.getInstance (AlphaComposite.SRC_OVER, 0.2f);
	public static final Composite ALPHA_OPAQUE = AlphaComposite.getInstance (AlphaComposite.SRC);

	/**
	 * Returns the position of aRelative relative to aOrigin.
	 */
	public static Point getRelativePosition (Component aRelative, Component aOrigin)
	{
		Point theRelativePoint = aRelative.getLocationOnScreen ();
		Point theOriginPoint = aOrigin.getLocationOnScreen ();

		return new Point (theRelativePoint.x - theOriginPoint.x, theRelativePoint.y - theOriginPoint.y);
	}


	/**
	 * Returns the smallest rectangle that contains the specified rectangle after it is transformed
	 * by the specified transform
	 */
	public static Rectangle2D transformRect (Rectangle2D aRectangle, AffineTransform aTransform)
	{
		if (aTransform == null || aTransform.isIdentity ()) return aRectangle;

		double[] coords = new double[]{aRectangle.getX (), aRectangle.getY (),
		                               aRectangle.getX () + aRectangle.getWidth (), aRectangle.getY (),
		                               aRectangle.getX () + aRectangle.getWidth (), aRectangle.getY () + aRectangle.getHeight (),
		                               aRectangle.getX (), aRectangle.getY () + aRectangle.getHeight ()};

		aTransform.transform (coords, 0, coords, 0, 4);

		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		for (int i = 0; i < 4; i++)
		{
			double x = coords[2 * i];
			double y = coords[2 * i + 1];
			if (x < minX) minX = x;
			if (x > maxX) maxX = x;
			if (y < minY) minY = y;
			if (y > maxY) maxY = y;
		}

		return new Rectangle2D.Double (minX, minY, maxX - minX, maxY - minY);
	}

	/**
	 *  Returns a non-null string, replacing a null value by an empty string.
	 * @return If aString is not null, returns aString, otherwise returns "".
	 */
	public static String ensureStringNotNull (String aString)
	{
		if (aString != null)
			return aString;
		else
			return "";
	}

	/**
	 * Mixes two colors
	 */
	public static Color averageColor (Color aColor1, float aWeight1, Color aColor2, float aWeight2)
	{
		if (aColor1 == null) return aColor2;
		if (aColor2 == null) return aColor1;

		int r = (int) ((aColor1.getRed () * aWeight1 + aColor2.getRed () * aWeight2) / (aWeight1 + aWeight2));
		int g = (int) ((aColor1.getGreen () * aWeight1 + aColor2.getGreen () * aWeight2) / (aWeight1 + aWeight2));
		int b = (int) ((aColor1.getBlue () * aWeight1 + aColor2.getBlue () * aWeight2) / (aWeight1 + aWeight2));

		return new Color (r, g, b);
	}


	private static final Rectangle AUTOSCROLL_RECTANGLE = new Rectangle(0, 0, 1, 1);
	/**
	 * Listener to add to a component which was activated for autoscrolling.
	 */
	public static final MouseMotionListener AUTOSCROLL_DRAG_LISTENER = new MouseMotionAdapter ()
	{
		public void mouseDragged (MouseEvent e)
		{
			AUTOSCROLL_RECTANGLE.setLocation(e.getX (), e.getY ());
			((JComponent) e.getSource ()).scrollRectToVisible (AUTOSCROLL_RECTANGLE);
		}
	};

    /**
     * Adds to the collections all the items returned by the iterator.
	 * @return The given collection.
     */
    @SuppressWarnings("unchecked")
	public static final Collection fillCollection (Collection aCollection, Iterator aIterator)
    {
        for (Iterator theIterator = aIterator; theIterator.hasNext ();) 
        	aCollection.add (theIterator.next ());
		return aCollection;
    }

    /**
     * Adds to the collections all the items returned by the iterable.
	 * @return The given collection.
     */
    public static final Collection fillCollection (Collection aCollection, Iterable aIterable)
    {
    	return fillCollection(aCollection, aIterable.iterator());
    }

	public static final String getObjectName (Object aObject)
	{
		if (aObject == null) return "null";
		else if (aObject instanceof NamedObject)
		{
			NamedObject theNamedObject = (NamedObject) aObject;
			return theNamedObject.getName();
		}
		else return aObject.toString();
	}

	public static final ImageIcon getObjectIcon (Object aObject)
	{
		if (aObject == null) return null;
		else if (aObject instanceof NamedObject)
		{
			NamedObject theNamedObject = (NamedObject) aObject;
			return theNamedObject.getIcon();
		}
		else return null;
	}

	/**
	 * Returns whether the container or one of its children contains a component.
	 */ 
	public static boolean containsComponent (Container aContainer, Component aComponent)
	{
		Component[] theArray = aContainer.getComponents();
		for (int i = 0; i < theArray.length; i++)
		{
			Component theComponent = theArray[i];
			if (theComponent == aComponent) return true;
			else if (theComponent instanceof Container)
			{
				Container theContainer = (Container) theComponent;
				if (containsComponent(theContainer, aComponent)) return true;
			}
		}
		return false;
	}

	/**
	 * Similar to {@link java.util.List#indexOf}, except that it invokes
	 * the equals method of the objects in the list rather than that of the specified
	 * object.
	 * @param aObject An object to look for.
	 * @return The index of the first object whose equals method returns true.
	 */
	public static int indexOf (Object aObject, java.util.List aList)
	{
		int theIndex = 0;
		for (Iterator theIterator = aList.iterator (); theIterator.hasNext ();)
		{
			Object o = theIterator.next ();
			if (o.equals(aObject)) return theIndex;
			theIndex ++;
		}
		return -1;
	}

	/**
	 * Searches and returns the first object in the list whose equals method returns
	 * true when passed the specified object.
	 */
	public static Object find (Object aObject, java.util.List aList)
	{
		int theIndex = indexOf(aObject, aList);
		if (theIndex == -1) return null;
		else return aList.get (theIndex);
	}
	
	/**
	 * Removes and returns the first object in the list whose equals method
	 * returns true when passed the specified object.
	 */
	public static Object remove (Object aObject, java.util.List aList)
	{
		int theIndex = indexOf(aObject, aList);
		if (theIndex == -1) return null;
		else return aList.remove (theIndex);
	}

	public static final Cursor BUSY_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);

	/**
	 * Shows a busy cursor, and restores the previous cursor only when the event dispatchin
	 * thread finishes its work.
	 * @param aComponent We need a component to perform this operation. Can be any component of the frame.
	 */
	public static void showBusyCursor (final Component aComponent)
	{
		final Cursor thePreviousCursor = aComponent.getCursor();
		aComponent.setCursor(BUSY_CURSOR);
		SwingUtilities.invokeLater(new Runnable ()
		{
			public void run ()
			{
				aComponent.setCursor(thePreviousCursor);
			}
		});
	}

	/**
	 * Rounds the specified double with the given number of decimals.
	 */
	public static double round (double aValue, int aNDecimals)
	{
		double theK = Math.pow (10, aNDecimals);

		return Math.round(aValue *= theK) / theK;
	}

	@SuppressWarnings("unchecked")
	public static Collection cloneCollection (Collection aCollection)
	{
		if (aCollection == null) return null;
		try
		{
			Collection theCloneCollection = (Collection) aCollection.getClass().newInstance();
			for (Iterator theIterator = aCollection.iterator (); theIterator.hasNext ();)
			{
				Object theObject = (Object) theIterator.next ();
				if (theObject instanceof IPublicCloneable)
				{
					IPublicCloneable theCloneable = (IPublicCloneable) theObject;
					Object theClone = theCloneable.clone();
					theCloneCollection.add (theClone);
				}
				else theCloneCollection.add (theObject);
			}
			return theCloneCollection;
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static Map cloneMap (Map aMap)
	{
		if (aMap == null) return null;
		try
		{
			Map theClone = (Map) aMap.getClass().newInstance();
			for (Iterator theIterator = aMap.entrySet ().iterator (); theIterator.hasNext ();)
			{
				Map.Entry theEntry = (Map.Entry) theIterator.next ();

				Object theKey = theEntry.getKey();
				Object theValue = theEntry.getValue();

				Object theKeyClone;
				Object theValueClone;

				if (theKey instanceof IPublicCloneable)
				{
					IPublicCloneable theCloneable = (IPublicCloneable) theKey;
					theKeyClone = theCloneable.clone();
				}
				else theKeyClone = theKey;

				if (theValue instanceof IPublicCloneable)
				{
					IPublicCloneable theCloneable = (IPublicCloneable) theValue;
					theValueClone = theCloneable.clone();
				}
				else theValueClone = theValue;

				theClone.put (theKeyClone, theValueClone);
			}
			return theClone;
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return null;
		}
	}

	public static boolean isWithin(int aInf,int aSup,int aValue)
	{
		return (aValue>aInf && aValue<aSup);
	}
	
	/**
	 * Writes a newline and a number of spaces into the specified string builder.
	 */
	public static void indent (StringBuilder aBuilder, int aIndent)
	{
		aBuilder.append("\r\n");
		for (int i=0;i<aIndent;i++) aBuilder.append(' ');
	}

	
}