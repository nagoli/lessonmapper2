/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.lom;

import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;
import java.rmi.dgc.VMID;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


import lessonMapper.lom.util.LOMRelationBuffer;
import lessonMapper.query.LOMRanking;

import org.jaxen.JaxenException;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import diffuse.metadata.MetadataSet;

/**
 * User: omotelet Date: Mar 7, 2005 Time: 12:22:13 PM <p/> This class
 * encapsulate a JDOM representation of a lessonMapper.lom.LOM XML document
 */
public class LOM implements Serializable, Comparable<LOM>,MetadataSet {

	/**
	 * 
	 */
	public static final String ITSNameSpacePrefix = "ims";

	/**
	 * 
	 */
	public static final String ITSNameSpace = "http://www.imsglobal.org/xsd/imsmd_v1p2";

	/**
	 * 
	 */
	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	/**
	 * 
	 */
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	/**
	 * 
	 */
	public static final String ITSSchemaSource = "resources/imsmd_v1p2p2.xsd";

	/**
	 * 
	 */
	public static final String ITSVoidDocument = "resources/VoidLOM.xml";

	/**
	 * 
	 */
	public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

	static final public String ITSTokenizerLimits = ";,:"; //., \t\n\r\f
	
	/**
	 * 
	 */
	static Hashtable<String, LOM> ITSLOMs = new Hashtable<String, LOM>();

	/**
	 * 
	 */
	static SAXBuilder ITSSAXBuilder;

	/**
	 * 
	 */
	Document itsXMLModel;

	/**
	 * 
	 */
	String itsID = null;

	/**
	 * 
	 */
	String itsMaterialID = null;

	/**
	 * 
	 */
	public LOM() {
		itsXMLModel = getVoidDocument();
		registerLOM(this);
	}
	
	
	/**
	 * 
	 * 
	 * @param aURL 
	 */
	public LOM(URL aURL) {
		this(aURL,false,false);
	}

	public LOM(URL aURL,boolean withNewID,boolean withNewLOID) {
		try {
			itsXMLModel = getSaxBuilder().build(aURL);
			init(withNewID,withNewLOID);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public LOM(LOM aLOM,boolean withNewID,boolean withNewLOID) {
		this(aLOM.toXMLString(),withNewID,withNewLOID);
	} 
	
	/**
	 * 
	 * 
	 * @param aFile 
	 * 
	 * @throws Exception 
	 */
	
	public LOM(File aFile) throws Exception{
		this(aFile,false,false);
	}
	
	
	public LOM(File aFile,boolean withNewID,boolean withNewLOID) throws Exception{
		try {
			itsXMLModel = getSaxBuilder().build(aFile);
			init(withNewID,withNewLOID);
		} catch (JDOMException e) {
			System.out.println("invalid XML for building LOM");
			throw e;
		} catch (IOException e) {
			System.out.println("invalid file for building LOM");
			throw e;
		}
	}

	/**
	 * 
	 * 
	 * @param aXMLString 
	 */
	public LOM(String aXMLString) {
		this(aXMLString,false,false);
	}
	
	
	public LOM(String aXMLString,boolean withNewID,boolean withNewLOID) {

		try {
			itsXMLModel = getSaxBuilder().build(new StringReader(aXMLString));
			init(withNewID,withNewLOID);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 
	 * @param aXMLModel 
	 */
	public LOM(Document aXMLModel) {
		itsXMLModel = aXMLModel;
		init(false,false);
	}
	
	
	
	
	
	/**
	 * init a LOM 
	 */
	private void init(boolean withNewID,boolean withNewLOID) {
		cleanDocument(itsXMLModel);
		addVoidFields();
		String theOldID=null;
		if (withNewID){
			setNewID();
			if (withNewLOID)
				theOldID= getMaterialID();
				setNewLOID();
		}
		registerLOM(this);
		if (withNewID && withNewLOID) cleanRelations(theOldID);
	}

	/**
	 * this method remove all the relations from this lom 
	 * and aggregate a new isVersionOf relation between this LO and
	 * the previous one
	 * 
	 */
	private void cleanRelations(String aOldID){
		List theRelationElements = getNodes(LOM.ITSNameSpacePrefix
				+ ":lom/" + LOM.ITSNameSpacePrefix + ":relation");
		for (Iterator theIterator = theRelationElements.iterator(); theIterator
				.hasNext();) {
			Element theRelationElement = (Element) theIterator.next();
			theRelationElement.detach();
		}
		//TODO add isVersionOf relation (need oldLOM not only oldID)
	}
	
	
	
	
	

	/**
	 * register this LOM object so that it could be easily found return the
	 * previous LOM with that ID otherwise null.
	 * 
	 * @param aLOM 
	 * 
	 * @return 
	 */
	public static LOM registerLOM(LOM aLOM) {
		LOM theEx = unRegisterLOM(aLOM);
		ITSLOMs.put(aLOM.getID(), aLOM);
		return theEx;
	}
	
	/**
	 * return registered LOMs.
	 * 
	 * @return 
	 */
	public static Collection<LOM> getRegisteredLOM(){
		return ITSLOMs.values();
	}
	
	
	/**
	 * reset LOM Cache.
	 */
	public static void resetCache() {
		ITSLOMs.clear();
	}

	/**
	 * unregister aLOM, returns the LOM or null if the object was not registered.
	 * 
	 * @param aLOM 
	 * 
	 * @return 
	 */
	public static LOM unRegisterLOM(LOM aLOM) {
		if (ITSLOMs.contains(aLOM)){
			LOMRelationBuffer.getInstance().unregisterRelationsOf(aLOM);
			return ITSLOMs.remove(aLOM.getID());
		}
		return null;
	}

	/**
	 * return theLOM associated with this ID.
	 * 
	 * @param anID 
	 * 
	 * @return 
	 */
	public static LOM getLOM(String anID) {
		LOM theLOM = ITSLOMs.get(anID);
		return theLOM;
	}

	/**
	 * return theLOM associated with this ID.
	 * 
	 * @param aXMLString 
	 * 
	 * @return 
	 */
	public static LOM getLOMWithXMLRepresentation(String aXMLString) {
		try {
			Document theXMLModel = getSaxBuilder().build(
					new StringReader(aXMLString));
			LOM theLOM = getLOM(getID(theXMLModel));
			if (theLOM == null)
				theLOM = new LOM(theXMLModel);
			return theLOM;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public Document getXMLModel() {
		return itsXMLModel;
	}

	/**
	 * return the id of the lom 
	 * create it if necesary
	 * 
	 * @return 
	 */
	public String getID() {
		if (itsID == null) {
			String theID=null;
			Element theIdentifer = null;
			boolean isUndefined=true;
			List theNodes = getNodes(
								"ims:lom/ims:metametadata/ims:identifier");
			if (theNodes.size()>0) {
				theIdentifer = (Element) theNodes.get(0);
				theID = theIdentifer.getTextTrim();
				if (!theID.equals("") && theID.length() > 20) isUndefined = false;
			}
			if (isUndefined){
				theID = new VMID().toString().trim();
				//TODO create node if 
				if (theIdentifer == null) {
					theIdentifer = new Element("identifier",ITSNameSpace);
					/*((Element) getNodes(
					"ims:lom/ims:metametadata").get(0)).addContent(theIdentifer);*/
				}
				theIdentifer.setText(theID);
			}
			itsID = theID;
		}

		return itsID;
	}

	/*
	 * assign a new id to this lom
	 * this method supposes that VoidDocument content a identifier element metametadata/identifier
	 */
	public String setNewID() {
			String theID=new VMID().toString().trim();
			List theNodes = getNodes(
								"ims:lom/ims:metametadata/ims:identifier");
			Element theIdentifer = (Element) theNodes.get(0);
			theIdentifer.setText(theID);
			
			
			itsID = theID;
			return itsID;
	}
	
	

	/*
	 * assign a new id to the associated LO
	 * this method supposes that VoidDocument content an identifier element general/identifier
	 * 
	 */
	public String setNewLOID() {
			String theID=new VMID().toString().trim();
			List theNodes = getNodes(
								"ims:lom/ims:general/ims:identifier");
			Element theIdentifer = (Element) theNodes.get(0);
			theIdentifer.setText(theID);
			return theID;
	}
	
	
	
	/**
	 * 
	 * 
	 * @param aXMLModel 
	 * 
	 * @return 
	 */
	public static String getID(Document aXMLModel) {
		Element theIdentifer = (Element) getNodes(
				"ims:lom/ims:metametadata/ims:identifier", aXMLModel).get(0);
		String theID = theIdentifer.getTextTrim();
		return theID;
	}

	/**
	 * return the ID of the material.
	 * 
	 * @return 
	 */
	public String getMaterialID() {
		if (itsMaterialID == null) {
			Element theIdentifer = (Element) getNodes(
					"ims:lom/ims:general/ims:identifier").get(0);
			String theID = theIdentifer.getTextTrim();
			if (theID.equals("") || theID.length() < 20)
				setNewMaterialID();
			else
				itsMaterialID = theID;
		}
		return itsMaterialID;
	}

	/**
	 * set a new ID for the material and returns it.
	 * 
	 * @return 
	 */
	public String setNewMaterialID() {
		Element theIdentifer = (Element) getNodes(
				"ims:lom/ims:general/ims:identifier").get(0);
		String theID = new VMID().toString().trim();
		theIdentifer.setText(theID);
		itsMaterialID = theID;
		return itsMaterialID;
	}

	/**
	 * material ID is aURL if the material is web accessible, a ID.
	 */
	public void updateMaterialID() {

	}

	/**
	 * get value for aXPath - Note that current LOM namespace is prefixed with
	 * "ims".
	 * 
	 * @param aXPath 
	 * 
	 * @return 
	 */
	public String getValue(String aXPath) {
		String result = null;
		try {
			JDOMXPath myXPath = new JDOMXPath(aXPath);
			myXPath.addNamespace(ITSNameSpacePrefix, ITSNameSpace);
			result = myXPath.valueOf(itsXMLModel);
		} catch (JaxenException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * get nodes for aXPath - Note that current LOM namespace is prefixed with
	 * "ims".
	 * 
	 * @param aXPath 
	 * 
	 * @return 
	 */
	public List getNodes(String aXPath) {
		List result = null;
		try {
			JDOMXPath myXPath = new JDOMXPath(aXPath);
			myXPath.addNamespace(ITSNameSpacePrefix, ITSNameSpace);
			result = myXPath.selectNodes(itsXMLModel);
			// System.out.println(aXPath + " " + itsID + result);
		} catch (JaxenException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * get nodes for aXPath - Note that current LOM namespace is prefixed with
	 * "ims".
	 * 
	 * @param aXPath 
	 * @param aXMLModel 
	 * 
	 * @return 
	 */
	public static List getNodes(String aXPath, Document aXMLModel) {
		List result = null;
		try {
			JDOMXPath myXPath = new JDOMXPath(aXPath);
			myXPath.addNamespace(ITSNameSpacePrefix, ITSNameSpace);
			result = myXPath.selectNodes(aXMLModel);
			// System.out.println(aXPath + " " + itsID + result);
		} catch (JaxenException e) {
			e.printStackTrace();
		}
		return result;
	}

	// public LOMValue getLOMValue(LOMAttribute aAttribute) {
	// List theResult =getNodes(aAttribute.g)
	// }

	/**
	 * 
	 * 
	 * @param aDocument 
	 * 
	 * @return 
	 */
	protected String display(Document aDocument) {
		return display(aDocument.getRootElement(), "  ");
	}

	/**
	 * 
	 * 
	 * @param aContent 
	 * @param s 
	 * 
	 * @return 
	 */
	private String display(Content aContent, String s) {
		String display = aContent.toString() + "\n";
		if (aContent instanceof Element) {
			Element theElement = (Element) aContent;
			for (int i = 0; i < theElement.getContent().size(); i++) {
				display += s
						+ display((Content) theElement.getContent().get(i), s
								+ "  ");

			}
		}
		return display;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public Document getVoidDocument() {
		Document theDoc = null;
		try {
			URL theURL = LOM.class.getResource(ITSVoidDocument);
			theDoc = getSaxBuilder().build(theURL);
			cleanDocument(theDoc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return theDoc;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public static SAXBuilder getSaxBuilder() {
		if (ITSSAXBuilder == null) {
			ITSSAXBuilder = new SAXBuilder();
			// ITSSAXBuilder.setValidation(true);
			ITSSAXBuilder.setValidation(false);
			ITSSAXBuilder.setIgnoringElementContentWhitespace(true);
			// ITSSAXBuilder.getXMLFilter().getContentHandler();
			ITSSAXBuilder.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
			ITSSAXBuilder.setProperty(JAXP_SCHEMA_SOURCE, new File(LOM.class
					.getResource(ITSSchemaSource).getFile()));
		}
		return ITSSAXBuilder;
	}

	
	
	/**
	 * This method remove unexpected Text due to formatting.
	 * 
	 * @param aDoc 
	 */
	protected void cleanDocument(Document aDoc) {
		Element theRoot = aDoc.getRootElement();
		removeNonLOMText(theRoot);
	}

	/**
	 * remove the Text fields of aElement if aElemnt has chidren elements.
	 * 
	 * @param aElement 
	 */
	private void removeNonLOMText(Element aElement) {
		if (aElement != null) {
			if (!aElement.getChildren().isEmpty()) {
				List theContents = aElement.getContent();
				Vector<Content> theContentsToRemove = new Vector<Content>();
				for (int i = 0; i < theContents.size(); i++) {
					Content theContent = (Content) theContents.get(i);
					if (theContent instanceof Text)
						theContentsToRemove.add(theContent);
					else if (theContent instanceof Element)
						removeNonLOMText((Element) theContent);
				}
				for (Content theContent : theContentsToRemove) {
					aElement.removeContent(theContent);
				}
			}
		}
	}

	/**
	 * 
	 */
	protected void addVoidFields() {
		Document theVoidDocument = getVoidDocument();
		Element theVoidRootElement = theVoidDocument.getRootElement();
		Element theRootElement = itsXMLModel.getRootElement();
		if (theRootElement == null)
			itsXMLModel.setRootElement(theVoidRootElement);
		else
			checkPairs(theRootElement, theVoidRootElement);
	}

	/**
	 * 
	 * 
	 * @param aExistingElement 
	 * @param aVoidElement 
	 */
	private void checkPairs(Element aExistingElement, Element aVoidElement) {
		// System.out.println("checked " + aExistingElement + "with void " +
		// aVoidElement);
		Element[] theElementsToAdd = new Element[aVoidElement.getChildren()
				.size()];
		// find the missing elements
		for (int i = 0; i < aVoidElement.getChildren().size(); i++) {
			Element theVoidChild = (Element) aVoidElement.getChildren().get(i);
			Element theExisitingChild = aExistingElement.getChild(theVoidChild
					.getName(), theVoidChild.getNamespace());
			if (theExisitingChild != null)
				checkPairs(theExisitingChild, theVoidChild);
			else {
				theElementsToAdd[i] = theVoidChild;
				// System.out.println("added" + theVoidChild + " to " +
				// aExistingElement.getContent() + "at " + (i));
			}
		}
		// add the missing elements
		for (int i = 0; i < theElementsToAdd.length; i++) {
			Element theElement = theElementsToAdd[i];
			if (theElement != null) {
				if (i >= aExistingElement.getContentSize())
					aExistingElement.addContent(theElement.detach());
				else
					aExistingElement.addContent(i, theElement.detach());
			}
		}

		Attribute[] theAttributestoAdd = new Attribute[aVoidElement
				.getAttributes().size()];
		// find missing attributes
		for (int i = 0; i < aVoidElement.getAttributes().size(); i++) {
			Attribute theVoidAttribute = (Attribute) aVoidElement
					.getAttributes().get(i);
			Attribute theExisitingAttribute = aExistingElement
					.getAttribute(theVoidAttribute.getName(), theVoidAttribute
							.getNamespace());
			if (theExisitingAttribute == null)
				theAttributestoAdd[i] = theVoidAttribute;
		}
		// add missing attributes
		for (int i = 0; i < theAttributestoAdd.length; i++) {
			Attribute theAttribute = theAttributestoAdd[i];
			if (theAttribute != null) {
				if (i >= aExistingElement.getAttributes().size())
					aExistingElement.setAttribute(theAttribute.detach());
			}
		}

	}

	
	/**
	 * copy all the attribute of the model in this lom
	 * (attribute are those refered  by the class LOMRanking).
	 *  TODO Better use another attribute list than LOMRanking ...
	 * @param aModelLOM 
	 */
	public void rebuildWithModel(LOM aModelLOM){
		for (LOMAttribute theAttribute : LOMRanking.getAttributeList()) {
			Element theModelElement = (Element) theAttribute.getNodesIn(aModelLOM).get(0);
			Element theLocalElement = (Element) theAttribute.getNodesIn(this).get(0);
			Element theParent = theLocalElement.getParentElement();
			theLocalElement.detach();
			theParent.addContent((Element)theModelElement.clone());
		}
	}

	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		return LOMAttribute.getLOMAttribute("general/title").getValueIn(this).getValue();
	}

	/**
	 * 
	 * return the XML string for this LOM 
	 * @return 
	 */
	public String toXMLString() {
		 XMLOutputter outp = new XMLOutputter();
		 outp.setFormat(Format.getPrettyFormat());
		 return outp.outputString(itsXMLModel);
	
	}

	/**
	 * 
	 * return the XML string for this LOM with the technical/location given in parameter
	 * @return 
	 */
	public String toXMLStringWithLocation(String aLocation) {
		Element theLocation = (Element) getNodes(
		"ims:lom/ims:technical/ims:location").get(0);
		String theExLocation = theLocation.getText();
		theLocation.setText(aLocation);
		String theXMLString = new XMLOutputter(Format.getPrettyFormat()).outputString(itsXMLModel);
		theLocation.setText(theExLocation);
		return theXMLString;
	}

	
	
	
	/**
	 * 
	 * 
	 * @return 
	 * 
	 * @throws ObjectStreamException 
	 */
	public Object writeReplace() throws ObjectStreamException {
		return new SerializedForm(itsID);
	}

	/**
	 * 
	 */
	private class SerializedForm implements Serializable {

		/**
		 * 
		 */
		private String ID;

		/**
		 * 
		 * 
		 * @param aID 
		 */
		public SerializedForm(String aID) {
			this.ID = aID;
		}

		/**
		 * 
		 * 
		 * @return 
		 * 
		 * @throws ObjectStreamException 
		 */
		public Object readResolve() throws ObjectStreamException {
			LOM theLOM = getLOM(ID);
			if (theLOM != null)
				return theLOM;
			else {
				System.out
						.println("lom was not found at deserialization new lom created");
				return new LOM(ID);
			}
		}
	}

	/**
	 * return a element based on the values of aLOM.
	 * 
	 * @param aLOM 
	 * 
	 * @return 
	 */
	public static Element makeXMLElement(LOM aLOM) {
		if (aLOM == null)
			return null;
		Element theElement = new Element("LOM");
		Element theID = new Element("ID");
		theID.addContent(aLOM.getID());
		theElement.addContent(theID);
		return theElement;
	}

	/**
	 * return aCache with the value stored in aElement.
	 * 
	 * @param aElement 
	 * 
	 * @return 
	 */
	public static LOM buildFromXMLElement(Element aElement) {
		return getLOM(aElement.getChildTextTrim("ID"));
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(LOM aO) {
		if (aO == this) return 0;
		return getID().compareTo(aO.getID());
	}

	public Set<LOM> getRelatedMetadataSet(){
		return LOMRelationBuffer.getRelatedLOMFor(this);
	}
	
	public Map<LOMRelationType, Set<LOM>> getRelationTypeMap() {
		return LOMRelationBuffer.getRelationTypeMapFor(this);
	}
	
	
	
	public static Set<LOM> getRelatedLOM(LOM aLOM){
			return LOMRelationBuffer.getRelatedLOMFor(aLOM);
//		Set<LOM> theSet = new HashSet<LOM>(); 
//		Set<LOMRelation> theRelations = LOMRelationBuffer.getRelationsIn(aLOM);
//		for (LOMRelation theRelation : theRelations) {
//			LOM theRelatedLOM = getLOM(theRelation.getTargetLOMId());
//			if (theRelatedLOM != null) theSet.add(theRelatedLOM);
//		}
//		return theSet;
	}


	
	
}
