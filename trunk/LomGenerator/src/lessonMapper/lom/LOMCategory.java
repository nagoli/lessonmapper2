/*
 * LessonMapper 2.
 */
package lessonMapper.lom;

import java.net.URL;
import java.util.Hashtable;
import java.util.List;

import lessonMapper.LangManager;

import org.jaxen.JaxenException;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Basic encapsulation of the LOM categories it just has an id, label and a
 * comment.
 * 
 * @author omotelet
 */

public class LOMCategory {

	/**
	 * 
	 */
	public static final String ITSNameSpace = "http://www.imsproject.org/rdf/imsmd_rootv1p2#";

	/**
	 * 
	 */
	public static final String ITSDescriptionURL = "resources/vocabulary/root.rdf";

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
			{ "lom", "http://www.imsproject.org/rdf/imsmd_rootv1p2#" } };

	/**
	 * 
	 */
	static Hashtable<String,LOMCategory> ITSCategories = new Hashtable<String,LOMCategory>();

	/**
	 * 
	 * 
	 * @return 
	 */
	static Document getRDFModel() {
		URL theURL = LOM.class.getResource(ITSDescriptionURL);
		try {
			return (new SAXBuilder()).build(theURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 */
	static Document ITSRDFModel = getRDFModel();

	/**
	 * 
	 * 
	 * @param aCategoryName 
	 * 
	 * @return 
	 */
	public static LOMCategory getLOMCategory(String aCategoryName) {
		aCategoryName = aCategoryName.toLowerCase();
		LOMCategory theCategory = ITSCategories.get(aCategoryName);
		if (theCategory == null) {
			theCategory = new LOMCategory(aCategoryName);
			ITSCategories.put(aCategoryName, theCategory);
		}
		return theCategory;
	}

	/**
	 * 
	 */
	String itsID;

	/**
	 * 
	 */
	String itsLabel;

	/**
	 * 
	 */
	String itsComment;

	/**
	 * 
	 * 
	 * @param aCategoryName 
	 */
	private LOMCategory(String aCategoryName) {
		itsID = aCategoryName;
		List theLabelAlt = getXPathOnRDFModel("rdf:RDF/rdf:Property[attribute::rdf:ID='"
				+ itsID + "']/rdfs:label/rdf:Alt");
		if ((theLabelAlt != null) && (theLabelAlt.size() > 0))
			itsLabel = LangManager.getInstance().getLangString(
					(Element) theLabelAlt.get(0));
		else
			itsLabel = itsID + "(not labeled)";
		List theCommentAlt = getXPathOnRDFModel("rdf:RDF/rdf:Property[attribute::rdf:ID='"
				+ itsID + "']/rdfs:comment/rdf:Alt");
		if ((theCommentAlt != null) && (theCommentAlt.size() > 0))
			itsComment = LangManager.getInstance().getLangString(
					(Element) theCommentAlt.get(0));
		else
			itsComment = itsID + "(not commented)";
	}
	
	 /**
 	 * 
 	 * 
 	 * @return 
 	 */
 	public String getComment() {
		return itsComment;
	}


	/**
	 * 
	 * 
	 * @return 
	 */
	public String getID() {
		return itsID;
	}


	/**
	 * 
	 * 
	 * @return 
	 */
	public String getLabel() {
		return itsLabel;
	}

	/**
	 * 
	 * 
	 * @param aXPath 
	 * 
	 * @return 
	 */
	List getXPathOnRDFModel(String aXPath) {
		List result = null;
		try {
			JDOMXPath myXPath = new JDOMXPath(aXPath);
			for (int i = 0; i < ITSNameSpaces.length; i++) {
				String[] theNameSpace = ITSNameSpaces[i];
				myXPath.addNamespace(theNameSpace[0], theNameSpace[1]);
			}
			result = myXPath.selectNodes(ITSRDFModel);
		} catch (JaxenException e) {
			e.printStackTrace();
		}
		return result;
	}

}