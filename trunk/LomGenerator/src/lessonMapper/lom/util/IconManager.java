/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.lom.util;

import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import util.image.ImageUtil;

/**
 * this class is responsible for managing the icons associated to some LOM
 * properties and instancias of LOM Properties. icons are defined in a rdf file
 * called icons.rdf
 * 
 * @author omotelet
 */

public class IconManager {

	/**
	 * 
	 */
	public static final String ITSNameSpace = "http://www.dcc.uchile.cl/lessonMapper2";

	/**
	 * 
	 */
	public static final String ITSDescriptionURL = "resources/icons.rdf";

	/**
	 * 
	 */
	static final String[][] ITSNameSpaces = new String[][] {
			{ "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#" }, // shoudl
			// remain
			// in
			// first
			// position
			{ "rdfs", "http://www.w3.org/2000/01/rdf-schema#" }, // should
			// remain in
			// second
			// position
			{ "lm", "http://www.dcc.uchile.cl/lessonmapper#" } };

	/**
	 * 
	 */
	static IconManager ITSInstance = new IconManager();

	/**
	 * 
	 * 
	 * @return 
	 */
	public static IconManager getInstance() {
		return ITSInstance;
	}

	/**
	 * 
	 * 
	 * @param aPropertyName 
	 * 
	 * @return 
	 */
	public static ImageIcon getIconForElement(String aPropertyName) {
		return getInstance().getIcon(aPropertyName);
	}
	
	/**
	 * 
	 * 
	 * @param aPropertyName 
	 * 
	 * @return 
	 */
	public static Image getCompatibleImageForElement(String aPropertyName) {
		return getInstance().getCompatibleImage(aPropertyName);
	}

	/**
	 * 
	 */
	Hashtable<String, ImageIcon> itsIcons = new Hashtable<String, ImageIcon>();
	
	/**
	 * 
	 */
	Hashtable<String, BufferedImage> itsImages = new Hashtable<String, BufferedImage>();

	/**
	 * 
	 */
	Document itsRdfModel;

	/**
	 * 
	 */
	protected IconManager() {
		URL theURL = IconManager.class.getResource(ITSDescriptionURL);
		try {
			try {
				Document theModel = (new SAXBuilder()).build(theURL);

				List theList = theModel.getRootElement().getChildren();
				for (Iterator iter = theList.iterator(); iter.hasNext();) {
					Object theNext = iter.next();
					if (theNext instanceof Element) {
						Element theElement = (Element) theNext;
						String theID = theElement.getAttributeValue("ID",
								Namespace.getNamespace(ITSNameSpaces[0][0],
										ITSNameSpaces[0][1]));
						Element theIconChild = theElement.getChild("icon",
								Namespace.getNamespace(ITSNameSpaces[2][0],
										ITSNameSpaces[2][1]));
						if (theID != null && theIconChild != null) {
							Attribute theValue = theIconChild.getAttribute(
									"value", Namespace.getNamespace(
											ITSNameSpaces[0][0],
											ITSNameSpaces[0][1]));
							if (theValue != null) {
								String theIconURL = theValue.getValue();
								//System.out.println(theIconURL);
								
								BufferedImage theImage = ImageIO.read(IconManager.class
										.getResource(theIconURL));
								BufferedImage theCompatibleImage = ImageUtil.createCompatibleImage(theImage,Transparency.TRANSLUCENT);
								if (theCompatibleImage != null) itsImages.put(theID,theCompatibleImage);
								ImageIcon theIcon = new ImageIcon(theImage);
								if (theIcon != null)
									itsIcons.put(theID, theIcon);
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (JDOMException e2) {
			e2.printStackTrace();
		}

	}

	/**
	 * 
	 * 
	 * @param aIconName 
	 * 
	 * @return 
	 */
	public ImageIcon getIcon(String aIconName) {
		return itsIcons.get(aIconName);
	}
	
	/**
	 * 
	 * 
	 * @param aIconName 
	 * 
	 * @return 
	 */
	public Image getCompatibleImage(String aIconName) {
		return itsImages.get(aIconName);
	}

}
