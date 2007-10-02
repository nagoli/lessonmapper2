/*
 * LessonMapper 2.
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