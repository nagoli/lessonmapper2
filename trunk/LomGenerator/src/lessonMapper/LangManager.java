/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper;

/**
 * LangManager holds the language to be used in this application.
 * Classes should use the method getLangString(Element) to access to the language
 * -dependent labels. 
 */

import java.util.Iterator;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * 
 */
public class LangManager {

	
	public  static String DEFAULT = "EN";

	
	/**
	 * 
	 */
	static LangManager ITSInstance;

	/**
	 * 
	 * 
	 * @return 
	 */
	public static LangManager getInstance() {
		if (ITSInstance == null)
			ITSInstance = new LangManager(DEFAULT);
		return ITSInstance;
	}

	/**
	 * set the language for the LangManager.
	 * 
	 * @param aLang 
	 */
	public static void setLang(String aLang) {
		ITSInstance = new LangManager(aLang);
	}

	/**
	 * 
	 */
	public String itsLang;

	/**
	 * 
	 * 
	 * @param aLang 
	 */
	public LangManager(String aLang) {
		itsLang = aLang.trim();
	}

	/**
	 * return the String corresponding to the current language aAltElement is
	 * intended to match rdf requirements <rdfs:Alt> <rdf:li xml:lang="..">
	 * .....
	 * 
	 * @param aAltElement 
	 * 
	 * @return 
	 */
	public String getLangString(Element aAltElement) {
		if (aAltElement != null)
			for (Iterator iter = aAltElement.getChildren().iterator(); iter
					.hasNext();) {
				Element element = (Element) iter.next();
				Attribute theLangAttribute = element.getAttribute("lang",
						Namespace.getNamespace("xml","http://www.w3.org/XML/1998/namespace"));
				if (theLangAttribute != null
						&& theLangAttribute.getValue().equalsIgnoreCase(
								itsLang))
					return element.getText();
			}
		return null;

	}

	
	
}
