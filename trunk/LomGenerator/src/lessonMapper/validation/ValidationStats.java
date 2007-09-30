/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

/**
 * keep the statistiques: nb of valid element,
 * nb of not valid element
 * nb of not defined element.
 * 
 * @author omotelet
 */
public class ValidationStats implements Serializable{
	
	/**
	 * 
	 */
	protected List<ValidationState> itsValids = new ArrayList<ValidationState>(),
	itsNotDefineds = new ArrayList<ValidationState>(),
	itsNotValids = new ArrayList<ValidationState>();
	
	
	
	/**
	 * 
	 * 
	 * @param theState 
	 */
	public void addValidationState(ValidationState theState) {
		switch(theState.getState()) {
		case Valid: itsValids.add(theState); break;
		case NotDefined: itsNotDefineds.add(theState); break;
		case NotValid: itsNotValids.add(theState); break;
		}
	}
	
	
	
	/**
	 * 
	 * 
	 * @return 
	 */
	public int getNotDefinedNb() {
		return itsNotDefineds.size();
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public int getNotValidNb() {
		return itsNotValids.size();
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public int getValidNb() {
		return itsValids.size();
	}


	/**
	 * 
	 * 
	 * @return 
	 */
	public List<ValidationState> getNotDefineds() {
		return itsNotDefineds;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public List<ValidationState> getNotValids() {
		return itsNotValids;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public List<ValidationState> getValids() {
		return itsValids;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return  getNotDefinedNb()+ " nd/ " +getValidNb() +" v/ "+getNotValidNb() + " nv" ;
	}
	
	/**
	 * 
	 * 
	 * @param aStats 
	 * 
	 * @return 
	 */
	public static Element makeXMLElement(ValidationStats aStats) {
		Element theRoot = new Element("ValidationStats");
		Element theValid = new Element("Valid");
	    for (ValidationState theState : aStats.itsValids) 
			theValid.addContent(ValidationState.makeXMLElement(theState));
		theRoot.addContent(theValid);
		Element theNotValid = new Element("NotValid");
	    for (ValidationState theState : aStats.itsNotValids) 
			theNotValid.addContent(ValidationState.makeXMLElement(theState));
		theRoot.addContent(theNotValid);
		Element theNotDefined = new Element("NotDefined");
	    for (ValidationState theState : aStats.itsNotDefineds) 
			theNotDefined.addContent(ValidationState.makeXMLElement(theState));
		theRoot.addContent(theNotDefined);
		return theRoot;
	}

	/**
	 * return avalidationstate with the value stored in aElement.
	 * 
	 * @param aElement 
	 * 
	 * @return 
	 */
	public static ValidationStats buildFromXMLElement(Element aElement) {
		ValidationStats theStats = new ValidationStats();
		Element theValid = aElement.getChild("Valid");
	    for (Iterator iter = theValid.getChildren().iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			theStats.itsValids.add(ValidationState.buildFromXMLElement(element));
		}
	    Element theNotValid = aElement.getChild("NotValid");
	    for (Iterator iter = theNotValid.getChildren().iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			theStats.itsNotValids.add(ValidationState.buildFromXMLElement(element));
		}
	    Element theNotDefined = aElement.getChild("NotDefined");
	    for (Iterator iter = theNotDefined.getChildren().iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			theStats.itsNotDefineds.add(ValidationState.buildFromXMLElement(element));
		}
	    return theStats;
	}
	
	
}