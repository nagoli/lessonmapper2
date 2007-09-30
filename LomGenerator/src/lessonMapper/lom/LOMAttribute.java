/*
 * LessonMapper 2.
Copyright (C) Olivier Motelet.
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.lom;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import lessonMapper.diffusion.LOMRestrictionSet;
import lessonMapper.diffusion.LOMRestrictionValue;
import lessonMapper.diffusion.RestrictionOperator;
import lessonMapper.lom.util.TypeHandler;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import diffuse.metadata.MetadataSet;
import diffuse.metadata.MetadataSetAttribute;
import diffuse.metadata.MetadataSetValue;

/**
 * this class encapsulates the attribute description In particular it manages
 * LOM namespaces <p/> User: omotelet Date: Mar 14, 2005 Time: 12:21:49 PM.
 */
public class LOMAttribute implements Serializable, MetadataSetAttribute {

	/**
	 * 
	 */
	static Hashtable<String, LOMAttribute> ITSAttributes = new Hashtable<String, LOMAttribute>();

	/**
	 * 
	 * 
	 * @param aAttributeName
	 * 
	 * @return
	 */
	public static LOMAttribute getLOMAttribute(String aAttributeName) {
		aAttributeName = aAttributeName.toLowerCase();
		LOMAttribute theAttribute = ITSAttributes.get(aAttributeName);
		if (theAttribute == null) {
			theAttribute = new LOMAttribute(aAttributeName);
			ITSAttributes.put(aAttributeName, theAttribute);
		}
		return theAttribute;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public static Collection<LOMAttribute> getRegisteredAttribute() {
		return ITSAttributes.values();
	}

	/**
	 * 
	 * 
	 * @param attributeListURL
	 * 
	 * @return
	 */
	public static Vector<LOMAttribute> getAttributeList(URL attributeListURL) {
		Vector<LOMAttribute> theList = new Vector<LOMAttribute>();
		Document theDOMList;
		SAXBuilder theBuilder = new SAXBuilder();
		try {
			theDOMList = theBuilder.build(attributeListURL);
			String theStringList = theDOMList.getRootElement()
					.getTextNormalize();
			StringTokenizer theTokenizer = new StringTokenizer(theStringList);
			for (; theTokenizer.hasMoreTokens();) {
				theList.add(LOMAttribute.getLOMAttribute(theTokenizer
						.nextToken()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return theList;

	}

	/**
	 * 
	 */
	String itsXPath;

	/**
	 * 
	 */
	String itsName;

	/**
	 * 
	 */
	TypeHandler itsType;

	/**
	 * 
	 */
	private LOMVocabulary itsLOMVocabulary;

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
	 */
	LOMCategory itsCategory;

	/**
	 * do not use LOMAttribute contructors use getLOMAttribute instead.
	 * 
	 * @param aAttributeName
	 */
	protected LOMAttribute(String aAttributeName) {
		itsName = aAttributeName.toLowerCase().trim();
		init();
	}

	/**
	 * 
	 */
	protected void init() {
		itsXPath = LOM.ITSNameSpacePrefix + ":lom/" + LOM.ITSNameSpacePrefix
				+ ":";
		itsXPath += new String(itsName).replaceAll("/", "/"
				+ LOM.ITSNameSpacePrefix + ":");
		int theLastSlash = itsName.lastIndexOf('/');
		itsType = LOMSchema.Instance.getType(itsName
				.substring(theLastSlash + 1));
		if (itsType != null && itsType.getPath() != null)
			for (String s : itsType.getPath()) {
				itsXPath += "/" + LOM.ITSNameSpacePrefix + ":" + s;
			}
		itsLOMVocabulary = LOMVocabulary.getLOMVocabulary(this);
		itsLabel = itsLOMVocabulary.getPropertyLabel();
		itsComment = itsLOMVocabulary.getPropertyComment();
		itsCategory = LOMCategory
				.getLOMCategory(itsLOMVocabulary.getCategory());
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public LOMVocabulary getLOMVocabulary() {
		return itsLOMVocabulary;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public LOMCategory getLOMCategory() {
		return itsCategory;
	}

	/**
	 * 
	 * 
	 * @param aLOM
	 * 
	 * @return
	 */
	public List getNodesIn(LOM aLOM) {
		return aLOM.getNodes(getXPath());
	}

	public MetadataSetValue getValueIn(MetadataSet aMetadataSet) {
		if (aMetadataSet instanceof LOM)
			return getValueIn((LOM) aMetadataSet);
		else
			return null;
	}

	/**
	 * return the LOMValue associated to this attribute returns null if no
	 * primitive type is found.
	 * 
	 * @param aLOM
	 * 
	 * @return
	 */
	public LOMValue getValueIn(LOM aLOM) {
		LOMValue theValue;
		if (!getLOMVocabulary().isEmpty())
			theValue = new LOMValueVocabularySet(this);
		else if (itsType.getType().endsWith("string"))
			theValue = new LOMValueSet(this);
		else if (itsType.getType().endsWith("int"))
			theValue = new LOMValueInt(this);
		else
			return null;

		List theNodes = aLOM.getNodes(getXPath());
		for (int i = 0; i < theNodes.size(); i++) {
			Element theElement = (Element) theNodes.get(i);
			theValue.addValue(theElement.getText());
		}
		return theValue;
	}

	public MetadataSetValue createValueWith(List<String> aStringList) {
		LOMValue theValue;
		if (!getLOMVocabulary().isEmpty())
			theValue = new LOMValueVocabularySet(this);
		else if (itsType.getType().endsWith("string"))
			theValue = new LOMValueSet(this);
		else if (itsType.getType().endsWith("int"))
			theValue = new LOMValueInt(this);
		else
			return null;

		for (String theString : aStringList) {
			theValue.addValue(theString);
		}
		return theValue;
	}

	/**
	 * check if aString is contained in this attribute for aLOM.
	 * 
	 * @param aLOM
	 * @param aString
	 * 
	 * @return
	 */
	public boolean containsValueIn(LOM aLOM, String aString) {
		LOMValue theValue = getValueIn(aLOM);
		if (theValue instanceof LOMValueInt) {
			return theValue.contains(new LOMValueInt(aString, this));
		} else if (theValue instanceof LOMValueVocabularySet)
			return theValue.contains(new LOMValueVocabularySet(aString, this));
		else
			return theValue.contains(new LOMValueSet(aString, this));
	}

	/**
	 * this method may be used to add basic suggestions for anAttribute It
	 * should serve has input for an external suggestion to be include in the
	 * diffusion process.
	 * 
	 * @param aLOM
	 * 
	 * @return
	 */
	public LOMValue getSuggestionValueIn(LOM aLOM) {

		return null;
		// todo we could imagine that suggestion values are manage at level of
		// the LMS - for example the author could be the same as the user
		// loggin..
	}

	/**
	 * return the basic restrictions for this attribute (typically the
	 * vocabulary) This method may be extended to include other source of
	 * restriction in the diffusion framework.
	 * 
	 * @param aLOM
	 * 
	 * @return
	 */
	public LOMRestrictionSet getBasicRestrictionSet(LOM aLOM) {
		if (itsLOMVocabulary.getVocabularySet().isEmpty())
			return null;
		LOMRestrictionValue theValue = new LOMRestrictionValue(
				RestrictionOperator.CONTAINED, itsLOMVocabulary
						.getLOMValueSet());
		LOMRestrictionSet theSet = new LOMRestrictionSet();
		theSet.add(theValue);
		return theSet;
	}

	/**
	 * 
	 * 
	 * @param aAttributeName
	 * 
	 * @return
	 */
	public boolean is(String aAttributeName) {
		return itsName.equalsIgnoreCase(aAttributeName);
	}

	/**
	 * return the xpath of the element condering the isAvoidLOM option of
	 * LOMSchema.
	 * 
	 * @return
	 */
	public String getXPath() {

		return new String(itsXPath);
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String getName() {
		return itsName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "LOMAttribute{" + "itsName='" + itsName + "'" + "}";
	}

	/**
	 * 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// test
		LOM theL1 = new LOM(LOM.class
				.getResource("resources/test/TestLOM1.xml"));
		LOMAttribute theAttribute = new LOMAttribute("technical/size");
		System.out.println(theAttribute.getValueIn(theL1));
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
	public String getLabel() {
		return itsLabel;
	}

	/**
	 * return a element based on the values of aLOMAttribute.
	 * 
	 * @param aLOMAttribute
	 * 
	 * @return
	 */
	public static Element makeXMLElement(LOMAttribute aLOMAttribute) {
		Element theElement = new Element("LOMAttribute");
		Element theID = new Element("name");
		theID.addContent(aLOMAttribute.getName());
		theElement.addContent(theID);
		return theElement;
	}

	/**
	 * return aLOMAttribute with the value stored in aElement.
	 * 
	 * @param aElement
	 * 
	 * @return
	 */
	public static LOMAttribute buildFromXMLElement(Element aElement) {
		return getLOMAttribute(aElement.getChildTextTrim("name"));
	}

	/**
	 * 
	 * 
	 * @return
	 * 
	 * @throws ObjectStreamException
	 */
	public Object writeReplace() throws ObjectStreamException {
		return new SerializedForm(itsName);
	}

	/**
	 * 
	 */
	private class SerializedForm implements Serializable {

		/**
		 * 
		 */
		private String name;

		/**
		 * 
		 * 
		 * @param aName
		 */
		public SerializedForm(String aName) {
			this.name = aName;
		}

		/**
		 * 
		 * 
		 * @return
		 * 
		 * @throws ObjectStreamException
		 */
		public Object readResolve() throws ObjectStreamException {
			return getLOMAttribute(name);
		}
	}

}
