package util.ui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * ColorMosaic provides different colors with the nextColor method.
 * 
 * @author omotelet
 * 
 */

public class ColorMosaic {

	static ColorMosaic ITSInstance = new ColorMosaic();

	public static Color nextColor() {
		return ITSInstance.getNextColor();
	}

	public static Color nextRandomColor() {
		return ITSInstance.getNextRandomColor();
	}

	public static Color nextRandomColor(int aSeed) {
		return ITSInstance.getNextRandomColor(aSeed);
	}

	Color[] theColors = new Color[] { new Color(196, 231, 250),
			new Color(254, 121, 65), new Color(82, 226, 120),
			new Color(249, 255, 119), new Color(255, 133, 130),
			new Color(205, 211, 187) };

	int length;

	int indice = 0;

	/**
	 * itsSeededRandoms keeps a map of random number builder 
	 */
	Map<Integer, Random> itsSeededRandoms= new HashMap<Integer, Random> ();

	ColorMosaic() {
		length = theColors.length;
	}

	Color getNextColor() {
		return theColors[jumpNext()];
	}

	Color getNextRandomColor() {
		return getNextRandomColor(45433444);
	}

	Color getNextRandomColor(int aSeed) {
		Integer theSeed = new Integer(aSeed);
		if (!itsSeededRandoms.containsKey(theSeed)) {
			itsSeededRandoms.put(theSeed, new Random(theSeed));
		}
		Random theRandom = itsSeededRandoms.get(theSeed);
		int r = theRandom.nextInt(255);
		int g = theRandom.nextInt(255);
		int b = theRandom.nextInt(255);
		if (r+g+b<150 || r+g+b>600) {
			return getNextRandomColor();
		}
		return new Color(r, g, b);
	}

	/**
	 * return the current coloer and jump to the next
	 */
	int jumpNext() {
		int old = indice;
		indice++;
		if (indice == length)
			indice = 0;
		return old;
	}
}
