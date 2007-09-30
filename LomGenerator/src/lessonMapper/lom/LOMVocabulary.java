/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.lom;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import lessonMapper.LangManager;

import org.jaxen.JaxenException;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import util.ListWithoutDouble;

/**
 * This class holds the vocabulary for a LOM attribute. A LOMVocalbulary is
 * linked to a LOMAttirbute instance. User: omotelet Date: Mar 31, 2005 Time:
 * 4:13:06 PM
 */
public class LOMVocabulary {

	/**
	 * 
	 */
	static Hashtable<LOMAttribute,LOMVocabulary> itsLOMVocabularyList = new Hashtable<LOMAttribute,LOMVocabulary>();

	/**
	 * 
	 * 
	 * @param aLOMAttribute 
	 * 
	 * @return 
	 */
	public static LOMVocabulary getLOMVocabulary(LOMAttribute aLOMAttribute) {
		if (!itsLOMVocabularyList.containsKey(aLOMAttribute))
			itsLOMVocabularyList.put(aLOMAttribute, new LOMVocabulary(
					aLOMAttribute));
		return itsLOMVocabularyList.get(aLOMAttribute);
	}

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
			{"lm","http://www.dcc.uchile.cl/lessonMapper#"}, //should remain at third position
			
			{ "lom", "http://www.imsproject.org/rdf/imsmd_rootv1p2#" },
			{ "lom_ann", "http://www.imsproject.org/rdf/imsmd_annotationv1p2#" },
			{ "lom_cls",
					"http://www.imsproject.org/rdf/imsmd_classificationv1p2#" },
			{ "lom_edu", "http://www.imsproject.org/rdf/imsmd_educationalv1p2#" },
			{ "lom_gen", "http://www.imsproject.org/rdf/imsmd_generalv1p2#" },
			{ "lom_life", "http://www.imsproject.org/rdf/imsmd_lifecyclev1p2#" },
			{ "lom_meta",
					"http://www.imsproject.org/rdf/imsmd_metametadatav1p2#" },
			{ "lom_rel", "http://www.imsproject.org/rdf/imsmd_relationv1p2#" },
			{ "lom_rights", "http://www.imsproject.org/rdf/imsmd_rightsv1p2#" },
			{ "lom_tech", "http://www.imsproject.org/rdf/imsmd_technicalv1p2#" } };

	/**
	 * 
	 */
	public static final String annotationPrefix = "lom_ann";

	/**
	 * 
	 */
	public static final String classificationPrefix = "lom_cls";

	/**
	 * 
	 */
	public static final String educationalPrefix = "lom_edu";

	/**
	 * 
	 */
	public static final String generalPrefix = "lom_gen";

	/**
	 * 
	 */
	public static final String lifecyclePrefix = "lom_life";

	/**
	 * 
	 */
	public static final String metametadataPrefix = "lom_meta";

	/**
	 * 
	 */
	public static final String relationPrefix = "lom_rel";

	/**
	 * 
	 */
	public static final String rightsPrefix = "lom_rights";

	/**
	 * 
	 */
	public static final String technicalPrefix = "lom_tech";

	/**
	 * 
	 */
	public static  String annotationURL = "resources/vocabulary/annotation.rdf";

	/**
	 * 
	 */
	public static  String classificationURL = "resources/vocabulary/classification.rdf";

	/**
	 * 
	 */
	public static  String educationalURL = "resources/vocabulary/educational.rdf";

	/**
	 * 
	 */
	public static  String generalURL = "resources/vocabulary/general.rdf";

	/**
	 * 
	 */
	public static  String lifecycleURL = "resources/vocabulary/lifecycle.rdf";

	/**
	 * 
	 */
	public static  String metametadataURL = "resources/vocabulary/metametadata.rdf";

	/**
	 * 
	 */
	public static  String relationURL = "resources/vocabulary/relation.rdf";

	/**
	 * 
	 */
	public static  String rightsURL = "resources/vocabulary/rights.rdf";

	/**
	 * 
	 */
	public static  String rootURL = "resources/vocabulary/root.rdf";

	/**
	 * 
	 */
	public static  String technicalURL = "resources/vocabulary/technical.rdf";

	/**
	 * 
	 */
	static SAXBuilder ITSSAXBuilder;

	/**
	 * 
	 */
	static String ITSNullID = "notDefined";
	
	/**
	 * 
	 */
	static LOMVocabularyElement ITSVoidElement ;
	
	static  {
		Element theNullAlt = null;
		URL theURL = LOM.class.getResource(LOMCategory.ITSDescriptionURL);
		try {
			Document theModel =  (new SAXBuilder()).build(theURL);
			JDOMXPath myXPath = new JDOMXPath("rdf:RDF/rdf:Property[attribute::rdf:ID='"
					+ ITSNullID + "']/rdfs:label/rdf:Alt");
			for (int i = 0; i < ITSNameSpaces.length; i++) {
				String[] theNameSpace = ITSNameSpaces[i];
				myXPath.addNamespace(theNameSpace[0], theNameSpace[1]);
			}
			List theLabelAlt= myXPath.selectNodes(theModel);
			if ((theLabelAlt!=null)&&(theLabelAlt.size()>0)) 
				theNullAlt = (Element) theLabelAlt.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ITSVoidElement = new LOMVocabularyElement(ITSNullID,-1000,theNullAlt);
	}
	
	
	/**
	 * 
	 */
	LOMAttribute itsLOMAttribute;

	/**
	 * 
	 */
	Document itsRDFModel;

	/**
	 * 
	 */
	List<LOMVocabularyElement> itsVocabularySet;
	
	/**
	 * 
	 */
	Vector<LOMVocabularyElement> itsVocabularyOrderedVector;

	/**
	 * 
	 */
	String itsCategory;

	/**
	 * 
	 */
	String itsPropertyName;
	
	/**
	 * 
	 */
	String itsPropertyLabel;
	
	/**
	 * 
	 */
	String itsPropertyComment;
	
	

	/**
	 * 
	 * 
	 * @param aLOMAttribute 
	 */
	LOMVocabulary(LOMAttribute aLOMAttribute) {
		itsLOMAttribute = aLOMAttribute;
		init();
	}

	/**
	 * 
	 */
	public void init() {
		String theName = itsLOMAttribute.getName();
		int theFirstSlash = theName.indexOf('/');
		itsCategory = theName.substring(0, theFirstSlash);
		int theLastSlash = theName.lastIndexOf('/');
		itsPropertyName = theName.substring(theLastSlash + 1);
		try {
			Field theField = LOMVocabulary.class.getField(itsCategory + "URL");
			String theLocation = (String) theField.get(this);
			URL theRDFURL ;
			if (theLocation.startsWith("file:")||theLocation.startsWith("http:"))
				theRDFURL = new URL(theLocation);
			else 	theRDFURL = LOMVocabulary.class.getResource((String) theField.get(this));
			itsRDFModel = getSaxBuilder().build(theRDFURL);
			itsVocabularySet = buildVocabularySet(itsPropertyName);
			List theLabelAlt = getXPathOnRDFModel("rdf:RDF/rdf:Property[attribute::rdf:ID='"
					+ itsPropertyName + "']/rdfs:label/rdf:Alt");
			if (theLabelAlt.size()>0) 
				itsPropertyLabel = LangManager.getInstance().getLangString((Element)theLabelAlt.get(0));
			else itsPropertyLabel = itsPropertyName+"(not labeled)";
			List theCommentAlt = getXPathOnRDFModel("rdf:RDF/rdf:Property[attribute::rdf:ID='"
					+ itsPropertyName + "']/rdfs:comment/rdf:Alt");
			if (theCommentAlt.size()>0) 
				itsPropertyComment = LangManager.getInstance().getLangString((Element)theCommentAlt.get(0));
			else itsPropertyComment = itsPropertyName+"(not commented)";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method initialize a vocabulary list for a given attribute. The
	 * vocabulary is the list of instances declared in the rdf files associated
	 * with the given category entering the range attribute of the targeted
	 * property
	 * 
	 * @param aProperty 
	 * 
	 * @return 
	 * 
	 * @throws Exception 
	 */
	List<LOMVocabularyElement> buildVocabularySet(String aProperty) throws Exception {

		List<LOMVocabularyElement> theSet = new ListWithoutDouble<LOMVocabularyElement>();
		// get VocabularyClassName
		List theList = getXPathOnRDFModel("rdf:RDF/rdf:Property[attribute::rdf:ID='"
				+ aProperty + "']/rdfs:range/attribute::rdf:resource");
		// case wher property do not exist or has no range

		// find subproperty of aProperty
		/*List theSecondList = getXPathOnRDFModel("rdf:RDF/rdf:Property/rdfs:subPropertyOf[attribute::rdf:resource='#"
				+ aProperty + "']/ancestor::rdf:Property");
		for (int i = 0; i < theSecondList.size(); i++) {
			Element theSubPropertiesElement = (Element) theSecondList.get(i);
			// todo create composite vocabulary or other type of element ???
		}*/
		if (theList.size() != 0) {
			String theVocabularyClassName = ((Attribute) theList.get(0))
					.getValue();
			// todo manage also the instance of subclasses ....
			// the name comes as '#theClassName'

			List theElements = getXPathOnRDFModel("rdf:RDF/"
					+ getCategoryPrefix() + ":"
					+ theVocabularyClassName.substring(1));
			for (int i = 0; i < theElements.size(); i++) {
				Element theElement = (Element) theElements.get(i);
				String theName = theElement.getAttribute(
						"ID",
						Namespace.getNamespace(ITSNameSpaces[0][0],
								ITSNameSpaces[0][1])).getValue();
				Attribute theOrderingAttribute = theElement
						.getAttribute("orderingValue", Namespace.getNamespace(ITSNameSpaces[2][0],
								ITSNameSpaces[2][1]));
				int theOrderingValue = 0;
				if (theOrderingAttribute != null) {
					theOrderingValue = theOrderingAttribute.getIntValue();
				}
				Element theLabel = theElement.getChild("label",Namespace.getNamespace(ITSNameSpaces[1][0],ITSNameSpaces[1][1]));
				Element theAlt = null;
				if (theLabel !=null)
					theAlt=theLabel.getChild("Alt",Namespace.getNamespace(ITSNameSpaces[0][0],ITSNameSpaces[0][1]));
				theSet.add(new LOMVocabularyElement(theName, theOrderingValue,theAlt));
			}
		}
		return theSet;
	}

	/**
	 * 
	 * 
	 * @return 
	 * 
	 * @throws Exception 
	 */
	URL getCategoryURL() throws Exception {
		Field theField = LOMVocabulary.class.getField(itsCategory + "URL");
		return LOMVocabulary.class.getResource((String) theField.get(this));
	}

	/**
	 * 
	 * 
	 * @return 
	 * 
	 * @throws Exception 
	 */
	String getCategoryPrefix() throws Exception {
		Field theField = LOMVocabulary.class.getField(itsCategory + "Prefix");
		return (String) theField.get(this);
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public List getVocabularySet() {
		return itsVocabularySet;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public boolean isEmpty(){
		return itsVocabularySet.isEmpty();
	}
	
	/**
	 * return the vocabulary element corresponding to this String
	 * return a blank vocabulary element with a negative ordering value.
	 * 
	 * @param aString 
	 * 
	 * @return 
	 */
	public LOMVocabularyElement getVocabularyElement(String aString){
		for (Iterator iter = getVocabularySet().iterator(); iter.hasNext();) {
			LOMVocabularyElement theElement = (LOMVocabularyElement) iter.next();		
			if (theElement.getName().equalsIgnoreCase(aString)) return theElement;
		}
		return ITSVoidElement;
	}
	
	/**
	 * 
	 * 
	 * @return 
	 */
	public Vector<LOMVocabularyElement> getVocabularyOrderedVector() {
		if (itsVocabularyOrderedVector==null) {
		Vector<LOMVocabularyElement> theVocabularyVector = new Vector<LOMVocabularyElement>();
		 for (Iterator iter = getVocabularySet().iterator(); iter.hasNext();) {
			LOMVocabularyElement element = (LOMVocabularyElement) iter.next();
			theVocabularyVector.add(element);
		}
		theVocabularyVector.add(ITSVoidElement);
		Comparator<LOMVocabularyElement> theComparator = new Comparator<LOMVocabularyElement>() {
					
			public int compare(LOMVocabularyElement theElement1, LOMVocabularyElement theElement2) {
				if (theElement1.getOrderingValue() == theElement2
						.getOrderingValue())
					return 0;
				if (theElement1.getOrderingValue() < theElement2
						.getOrderingValue())
					return -1;
				else
					return 1;
			}

			public boolean equals(Object obj) {
				return super.equals(obj);
			}

		};
		Collections.sort(theVocabularyVector,theComparator); 
		itsVocabularyOrderedVector = theVocabularyVector;
		}
		return itsVocabularyOrderedVector;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public LOMValueVocabularySet getLOMValueSet() {
		LOMValueVocabularySet theSet = new LOMValueVocabularySet(itsLOMAttribute);
		theSet.getSet().addAll(itsVocabularySet);
		return theSet;
	}
	
	/**
	 * returns a LOMValue containing the element of this vocabulary set
	 * inferior to all the elements contained in aValueSet.
	 * 
	 * @param aValueSet 
	 * 
	 * @return 
	 */
	public LOMValueVocabularySet getLOMValueSetInf(LOMValueVocabularySet aValueSet){
		Vector<LOMVocabularyElement> theVoc = getVocabularyOrderedVector();
		int theLimit = theVoc.size()-1; 
		for (LOMVocabularyElement element :  aValueSet.getSet()) {
			theLimit = Math.min(theLimit, theVoc.indexOf(element));
		}
		LOMValueVocabularySet theValueSet = new LOMValueVocabularySet(itsLOMAttribute);
		for (int i = 0; i < theLimit; i++) {
			LOMVocabularyElement theElement = theVoc.get(i);
			theValueSet.addValue(theElement);
		}
		return theValueSet;
	}
	
	/**
	 * returns a LOMValue containing the element of this vocabulary set
	 * inferior o equal to the min of the  elements contained in aValueSet.
	 * 
	 * @param aValueSet 
	 * 
	 * @return 
	 */
	public LOMValueVocabularySet getLOMValueSetInfEq(LOMValueVocabularySet aValueSet){
		Vector<LOMVocabularyElement> theVoc = getVocabularyOrderedVector();
		int theLimit = theVoc.size()-1; 
		for (LOMVocabularyElement element :  aValueSet.getSet()) {
			theLimit = Math.min(theLimit, theVoc.indexOf(element));
		}
		LOMValueVocabularySet theValueSet = new LOMValueVocabularySet(itsLOMAttribute);
		for (int i = 0; i <= theLimit; i++) {
			LOMVocabularyElement theElement = theVoc.get(i);
			theValueSet.addValue(theElement);
		}
		return theValueSet;
	}
	
	/**
	 * returns a LOMValue containing the element of this vocabulary set
	 * superior to all the elements contained in aValueSet.
	 * 
	 * @param aValueSet 
	 * 
	 * @return 
	 */
	public LOMValueVocabularySet getLOMValueSetSup(LOMValueVocabularySet aValueSet){
		Vector<LOMVocabularyElement> theVoc = getVocabularyOrderedVector();
		int theLimit = 0; 
		for (LOMVocabularyElement element :  aValueSet.getSet()) {
			theLimit = Math.max(theLimit, theVoc.indexOf(element));
		}
		LOMValueVocabularySet theValueSet = new LOMValueVocabularySet(itsLOMAttribute);
		for (int i = theLimit +1 ; i < theVoc.size(); i++) {
			LOMVocabularyElement theElement = theVoc.get(i);
			theValueSet.addValue(theElement);
		}
		return theValueSet;
	}
	
	/**
	 * returns a LOMValue containing the element of this vocabulary set
	 * superior o equal to the max of the elements contained in aValueSet.
	 * 
	 * @param aValueSet 
	 * 
	 * @return 
	 */
	public LOMValueVocabularySet getLOMValueSetSupEq(LOMValueVocabularySet aValueSet){
		Vector<LOMVocabularyElement> theVoc = getVocabularyOrderedVector();
		int theLimit = 0; 
		for (LOMVocabularyElement element :  aValueSet.getSet()) {
			theLimit = Math.max(theLimit, theVoc.indexOf(element));
		}
		LOMValueVocabularySet theValueSet = new LOMValueVocabularySet(itsLOMAttribute);
		for (int i = theLimit ; i < theVoc.size(); i++) {
			LOMVocabularyElement theElement = theVoc.get(i);
			theValueSet.addValue(theElement);
		}
		return theValueSet;
	}
	
	/**
	 * 
	 * 
	 * @param aXPath 
	 * 
	 * @return 
	 */
	public List getXPathOnRDFModel(String aXPath) {
		List result = null;
		try {
			JDOMXPath myXPath = new JDOMXPath(aXPath);
			for (int i = 0; i < ITSNameSpaces.length; i++) {
				String[] theNameSpace = ITSNameSpaces[i];
				myXPath.addNamespace(theNameSpace[0], theNameSpace[1]);
			}
			result = myXPath.selectNodes(itsRDFModel);
		} catch (JaxenException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public SAXBuilder getSaxBuilder() {
		if (ITSSAXBuilder == null) {
			ITSSAXBuilder = new SAXBuilder();
		}
		return ITSSAXBuilder;
	}

	/**
	 * 
	 * 
	 * @param aString 
	 * 
	 * @return 
	 */
	public int getOrderingValueOf(String aString) {
		for (Iterator theIterator = itsVocabularySet.iterator(); theIterator
				.hasNext();) {
			LOMVocabularyElement theVocabularyElement = (LOMVocabularyElement) theIterator
					.next();
			if (theVocabularyElement.getName().trim().equalsIgnoreCase(aString.trim()))
				return theVocabularyElement.getOrderingValue();
		}
		return 0;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "LOMVocabulary{" + "itsRDFModel=" + itsRDFModel
				+ ", itsVocabularySet=" + itsVocabularySet + ", itsCategory='"
				+ itsCategory + "'" + ", itsPropertyName='" + itsPropertyName
				+ "'" + "}";
	}

	/**
	 * 
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		System.out.println(new LOMVocabulary(LOMAttribute
				.getLOMAttribute("technical/requirement")));
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public String getPropertyComment() {
		return itsPropertyComment;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public String getPropertyLabel() {
		return itsPropertyLabel;
	}
	
	/**
	 * 
	 * 
	 * @return 
	 */
	public String getCategory() {
		return itsCategory;
	}

}
