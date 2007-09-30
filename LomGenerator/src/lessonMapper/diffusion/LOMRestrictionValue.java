/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.diffusion;

import static lessonMapper.diffusion.RestrictionOperator.CONTAINED;
import static lessonMapper.diffusion.RestrictionOperator.DIF;
import static lessonMapper.diffusion.RestrictionOperator.EQ;
import static lessonMapper.diffusion.RestrictionOperator.INF;
import static lessonMapper.diffusion.RestrictionOperator.INF_EQ;
import static lessonMapper.diffusion.RestrictionOperator.SUP;
import static lessonMapper.diffusion.RestrictionOperator.SUP_EQ;

import java.io.Serializable;

import lessonMapper.lom.LOMValue;
import lessonMapper.lom.LOMValueInt;
import lessonMapper.lom.LOMValueSet;
import lessonMapper.lom.LOMValueVocabularySet;
import lessonMapper.lom.LOMVocabulary;

import org.jdom.Element;

/**
 * User: omotelet Date: Mar 31, 2005 Time: 2:44:58 PM.
 */
public class LOMRestrictionValue implements Serializable{

	/**
	 * 
	 */
	RestrictionOperator itsOperator;

	/**
	 * 
	 */
	LOMValue itsValue;

	// LOMAttribute itsLOMAttribute;

	/**
	 * 
	 * 
	 * @param aValue 
	 * @param aOperator 
	 */
	public LOMRestrictionValue(RestrictionOperator aOperator, LOMValue aValue) {
		itsValue = aValue;
		itsOperator = aOperator;
	}

	/**
	 * if the value treated match with a predefined vocabulary operators INF,
	 * INFEQ, SUP, SUPEQ, DIF, EQ are transform in CONTAINED + getPotentialValue.
	 * 
	 * @return true if transformation occured
	 * false if it did not
	 */
	public boolean transformAsPotentialValues() {
		if (itsOperator == INF || itsOperator == INF_EQ || itsOperator == SUP
				|| itsOperator == SUP_EQ || itsOperator == DIF
				|| itsOperator == EQ) {
			LOMValue thePotentialValue = getPotentialValue();
			if (thePotentialValue != null) {
				itsValue = thePotentialValue;
				itsOperator = CONTAINED;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * 
	 * @return 
	 */
	public LOMValue getValue() {
		return itsValue;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public RestrictionOperator getOperator() {
		return itsOperator;
	}

	/**
	 * this method merge the current LOMValue with the Value in parameter return
	 * false if the type of the values are not compatible.
	 * 
	 * @param aLOMValue 
	 * 
	 * @return 
	 */
	public boolean merge(LOMValue aLOMValue) {
		// check value are of same type
		if (itsValue.getClass() != aLOMValue.getClass())
			return false;
		
		switch (itsOperator) {
		case INF_EQ:
			itsValue.min(aLOMValue);
			return true;
		case INF:
			itsValue.min(aLOMValue);
			return true;
		case SUP_EQ:
			itsValue.max(aLOMValue);
			return true;
		case SUP:
			itsValue.max(aLOMValue);
			return true;
		case CONTAINS:
			itsValue = itsValue.union(aLOMValue);
			return true;
		case CONTAINED:
			itsValue = itsValue.intersection(aLOMValue);
			return true;
		case EQ:
			// no merge is possible for = unless there are ==
			return false;
		case DIF:
			itsValue = itsValue.union(aLOMValue);
			return true;
		}
		return false;
	}

	/**
	 * return true if aLOMValue is
	 * conforming with this LomRestrictionValue
	 * else return false.
	 * 
	 * @param aLOMValue 
	 * 
	 * @return 
	 */
	public boolean isSatisfiedBy(LOMValue aLOMValue) {
		if (aLOMValue == null) return false;
		//		 check value are of same type
		if (itsValue.getClass() != aLOMValue.getClass())
			return false;
		switch (itsOperator) {
		case INF_EQ:
			return aLOMValue.inferiorOrEqual(itsValue);
	
		case SUP_EQ:
			return aLOMValue.superiorOrEqual(itsValue);
		
		case CONTAINS:
			return aLOMValue.contains(itsValue);
		case CONTAINED:
			return aLOMValue.contained(itsValue);
		case EQ:
			return aLOMValue.equals(itsValue);
		case DIF:
			return aLOMValue.dif(itsValue);
		}
		return false;
	}
	
	/**
	 * return a LMValueSet containing the list of potential value for an
	 * attribute complying wiht this rrestriciton Warning: this method return
	 * null if the potential value cannot be calculated i.e. there is no finite
	 * set of vocabulary associated with this LOMAttribute
	 * 
	 * @return LOMValue
	 */

	public LOMValue getPotentialValue() {
		if (itsValue == null)
			return null;
		LOMVocabulary theVocabulary = itsValue.getLOMVocabulary();
		if (theVocabulary.isEmpty())
			return null;
		LOMValueVocabularySet theResult = new LOMValueVocabularySet(itsValue.getLOMAttribute());

		switch (itsOperator) {
		case INF_EQ:
			theResult = theVocabulary
					.getLOMValueSetInfEq((LOMValueVocabularySet) itsValue);
			break;
		case INF:
			theResult = theVocabulary.getLOMValueSetInf((LOMValueVocabularySet) itsValue);
			break;
		case SUP_EQ:
			theResult = theVocabulary
					.getLOMValueSetSupEq((LOMValueVocabularySet) itsValue);
			break;
		case SUP:
			theResult = theVocabulary.getLOMValueSetSup((LOMValueVocabularySet) itsValue);
			break;
		case CONTAINS:
			theResult = (LOMValueVocabularySet) itsValue;
			break;
		case CONTAINED:
			theResult = (LOMValueVocabularySet) itsValue;
			break;
		case EQ:
			theResult = (LOMValueVocabularySet) itsValue;
			break;
		case DIF:
			theResult = (LOMValueVocabularySet) theVocabulary.getLOMValueSet()
					.subtraction(itsValue);
			break;
		}
		return theResult;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public boolean isEmpty() {
		return (itsValue == null || itsValue.isEmpty());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object aObj) {
		if (aObj instanceof LOMRestrictionValue) {
			LOMRestrictionValue theRes = (LOMRestrictionValue) aObj;
			return theRes.itsOperator.equals(itsOperator)
			&& theRes.itsValue.equals(itsValue);
		}
		return false;
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public LOMRestrictionValue clone() throws CloneNotSupportedException {
		LOMValue theValueClone = null;
		if (itsValue !=null) theValueClone = itsValue.clone();
		return new LOMRestrictionValue(itsOperator,theValueClone);
	}
	
	
	/**
	 * 
	 * 
	 * @return 
	 */
	public String getLabel() {
		return  itsOperator + ": " + itsValue.getLabel();
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "LOMRestrictionValue{" + itsOperator + ", " + itsValue
				+ " for attribute " + itsValue.getLOMAttribute().getName()
				+ "}";
	}
	
	 /**
 	 * return a element based on the values of aLOMValue.
 	 * 
 	 * @param aRestrictionValue 
 	 * 
 	 * @return 
 	 */
	public  static Element makeXMLElement(LOMRestrictionValue aRestrictionValue){
		Element theElement = new Element("LOMRestrictionValue");
		Element theOperator = new Element("Operator");
		theOperator.addContent(aRestrictionValue.itsOperator.name());
		theElement.addContent(theOperator);
		Element theValue = LOMValue.makeXMLElement(aRestrictionValue.getValue());
		theElement.addContent(theValue);
		return theElement;
	}
		
	/**
	 * return aCache with the value stored in aElement.
	 * 
	 * @param aElement 
	 * 
	 * @return 
	 */
	public  static LOMRestrictionValue buildFromXMLElement(Element aElement) {
		RestrictionOperator theOperator = RestrictionOperator.valueOf(RestrictionOperator.class,aElement.getChildTextTrim("Operator"));
		Element theValueElement = aElement.getChild("LOMValueInt");
		LOMValue theValue;
		if (theValueElement!=null) theValue =LOMValueInt.buildFromXMLElement(theValueElement);
		else {
			theValueElement = aElement.getChild("LOMValueVocabularySet");
			if (theValueElement!=null) theValue = LOMValueSet.buildFromXMLElement(theValueElement);
			else theValue= LOMValueSet.buildFromXMLElement(aElement.getChild("LOMValueSet"));
		}
		return new LOMRestrictionValue(theOperator,theValue);
	}
    

	

}
