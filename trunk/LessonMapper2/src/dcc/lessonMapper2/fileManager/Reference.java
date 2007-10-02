/*
 * LessonMapper 2.
 */
package dcc.lessonMapper2.fileManager;

import java.io.Serializable;
import java.util.Date;

import org.jdom.Element;


/**
 * The Class Reference.
 */
public class Reference implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The its ID. */
	protected String itsID;

	/** The its name. */
	protected String itsName;

	/** The its modification date. */
	protected Date itsModificationDate;
	
	/** The its thumb. */
	protected String itsThumb;

	/**
	 * The Constructor.
	 * 
	 * @param aModificationDate
	 *            the a modification date
	 * @param aName
	 *            the a name
	 * @param aId
	 *            the a id
	 * @param aThumb
	 *            the a thumb
	 */
	public Reference(String aId, String aName, Date aModificationDate, String aThumb) {
		super();
		itsID = aId;
		itsName = aName;
		itsModificationDate = aModificationDate;
		itsThumb = aThumb;
	}

	/**
	 * The Constructor.
	 * 
	 * @param aXMLElement
	 *            the a XML element
	 */
	public Reference(Element aXMLElement) {
		super();
		itsID= aXMLElement.getChildText("ID");
		itsName = aXMLElement.getChildText("Name");
		itsModificationDate = new Date(new Long( aXMLElement.getChildText("ModificationDate")));
		itsThumb = aXMLElement.getChildText("Thumb");
	}
	
	/**
	 * Gets the ID.
	 * 
	 * @return the ID
	 */
	public String getID() {
		return itsID;
	}

	/**
	 * Gets the thumb.
	 * 
	 * @return the thumb
	 */
	public String getThumb() {
		return itsThumb;
	}
	
	/**
	 * Sets the ID.
	 * 
	 * @param aId
	 *            the ID
	 */
	public void setID(String aId) {
		itsID = aId;
	}

	/**
	 * Gets the modification date.
	 * 
	 * @return the modification date
	 */
	public Date getModificationDate() {
		return itsModificationDate;
	}

	/**
	 * Sets the modification date.
	 * 
	 * @param aModificationDate
	 *            the modification date
	 */
	public void setModificationDate(Date aModificationDate) {
		itsModificationDate = aModificationDate;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return itsName;
	}

	/**
	 * Sets the name.
	 * 
	 * @param aName
	 *            the name
	 */
	public void setName(String aName) {
		itsName = aName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return (itsName!=null?itsName:" ") + "  " + (itsModificationDate!=null?itsModificationDate.toString():" ");
	}
	
	/**
	 * To XML.
	 * 
	 * @return the element
	 */
	public Element toXML() {
		Element theElement = new Element ("Reference");
		Element theID = new Element("ID");
		theID.setText(itsID);
		Element theName = new Element("Name");
		theName.setText(itsName);
		Element theDate = new Element("ModificationDate");
		theDate.setText(""+itsModificationDate.getTime());
		Element theThumb = new Element("Thumb");
		theThumb.setText(itsThumb);
		theElement.addContent(theID);
		theElement.addContent(theName);
		theElement.addContent(theDate);
		theElement.addContent(theThumb);
		return theElement;
	}
}