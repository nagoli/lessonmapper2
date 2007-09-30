/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lessonMapper.diffusion.LOMRestrictionValue;
import lessonMapper.lom.LOMAttribute;

import org.jdom.Element;

/**
 * 
 */
public class ValidationState implements Serializable {

	/**
	 * 
	 */
	public enum State {
		
		/**
		 * 
		 */
		Valid, 
 /**
  * 
  */
 NotDefined, 
 /**
  * 
  */
 NotValid
	}

	/**
	 * 
	 */
	State itsState;

	/**
	 * 
	 */
	List<LOMRestrictionValue> itsRestrictionValues;

	/**
	 * 
	 */
	LOMAttribute itsAttribute;

	/**
	 * initialize a validation of type NotValid for aAttribute related with
	 * aRestrictionValue for which the nonValidity occured.
	 * 
	 * @param values 
	 * @param aAttribute 
	 */
	public ValidationState(LOMAttribute aAttribute,
			List<LOMRestrictionValue> values) {
		itsState = State.NotValid;
		itsRestrictionValues = values;
		itsAttribute = aAttribute;
	}

	/**
	 * 
	 * 
	 * @param aState 
	 * @param values 
	 * @param aAttribute 
	 */
	public ValidationState(State aState, LOMAttribute aAttribute,
			List<LOMRestrictionValue> values) {
		itsState = aState;
		itsRestrictionValues = values;
		itsAttribute = aAttribute;
	}

	/**
	 * initialize a validation of type NotDefined for aAttribute.
	 * 
	 * @param aAttribute 
	 */
	public ValidationState(LOMAttribute aAttribute) {
		itsState = State.NotDefined;
		itsAttribute = aAttribute;
	}

	/**
	 * initialize a validation of type aState for aAttribute.
	 * 
	 * @param aState 
	 * @param aAttribute 
	 */
	public ValidationState(LOMAttribute aAttribute, State aState) {
		itsState = aState;
		itsAttribute = aAttribute;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public State getState() {
		return itsState;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public LOMAttribute getAttribute() {
		return itsAttribute;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public List<LOMRestrictionValue> getRestrictionValues() {
		return itsRestrictionValues;
	}

	/**
	 * 
	 * 
	 * @param aState 
	 * 
	 * @return 
	 */
	public static Element makeXMLElement(ValidationState aState) {
		Element theRoot = new Element("ValidationState");
		Element theState = new Element("State");
		theState.addContent(aState.itsState.name());
		theRoot.addContent(theState);
		theRoot.addContent(LOMAttribute.makeXMLElement(aState.itsAttribute));
		Element theValidationList = new Element("RestrictionList");
		if (aState.itsRestrictionValues != null)
			for (LOMRestrictionValue theSet : aState.itsRestrictionValues) {
				theValidationList.addContent(LOMRestrictionValue
						.makeXMLElement(theSet));
			}
		theRoot.addContent(theValidationList);
		return theRoot;
	}

	/**
	 * return avalidationstate with the value stored in aElement.
	 * 
	 * @param aElement 
	 * 
	 * @return 
	 */
	public static ValidationState buildFromXMLElement(Element aElement) {
		State theState = State.valueOf(State.class, aElement
				.getChildTextTrim("State"));
		LOMAttribute theAttribute = LOMAttribute.buildFromXMLElement(aElement
				.getChild("LOMAttribute"));
		List<LOMRestrictionValue> theList = new ArrayList<LOMRestrictionValue>();
		for (Iterator iter = aElement.getChild("RestrictionList").getChildren()
				.iterator(); iter.hasNext();) {
			Element theRestriction = (Element) iter.next();
			if (theRestriction.getName().equals("LOMRestrictionValue"))
				theList.add(LOMRestrictionValue
						.buildFromXMLElement(theRestriction));
		}
		return new ValidationState(theState, theAttribute, theList);
	}

}
