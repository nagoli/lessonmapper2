/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.lom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;


import org.jdom.Element;

import diffuse.metadata.MetadataSetValue;

import util.Utils;

/**
 * Encapsulate the value of LOM attributes <p/> User: omotelet Date: Mar 17,
 * 2005 Time: 11:49:24 AM.
 */
public class LOMValueVocabularySet extends LOMValue {

	/**
	 * 
	 */
	Set<LOMVocabularyElement> itsValueSet = new HashSet<LOMVocabularyElement>();

	/**
	 * 
	 * 
	 * @param aLOMAttribute 
	 */
	public LOMValueVocabularySet(LOMAttribute aLOMAttribute) {
		super(aLOMAttribute);
	}

	  public Collection<MetadataSetValue> getAtomicValues() {
	       	ArrayList<MetadataSetValue> theArrayList = new ArrayList<MetadataSetValue>();
			theArrayList.add(this);
	       	return theArrayList;
	    }
	
	
	/**
	 * 
	 * 
	 * @param aLOMAttribute 
	 * @param aString 
	 */
	public LOMValueVocabularySet(String aString, LOMAttribute aLOMAttribute) {
		super(aLOMAttribute);
		setValue(aString.trim());
	}

	/**
	 * 
	 * 
	 * @param aLOMAttribute 
	 * @param aSet 
	 */
	public LOMValueVocabularySet(LOMAttribute aLOMAttribute, Set<LOMVocabularyElement> aSet) {
		super(aLOMAttribute);
		itsValueSet = aSet;
	}

	/**
	 * 
	 * 
	 * @param aLOMValueSet 
	 * @param aLOMAttribute 
	 */
	public LOMValueVocabularySet(LOMValueVocabularySet aLOMValueSet, LOMAttribute aLOMAttribute) {
		super(aLOMAttribute);
		itsValueSet.addAll(aLOMValueSet.getSet());
		
	}

	/**
	 * 
	 * 
	 * @param aLOMValueSet 
	 */
	public LOMValueVocabularySet(LOMValueVocabularySet aLOMValueSet) {
		this(aLOMValueSet, aLOMValueSet.getLOMAttribute());
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public List<String> getValueList(){
		List<String> theValueList = new ArrayList<String>();
		for (LOMVocabularyElement theElement : itsValueSet) {
			if (theElement != LOMVocabulary.ITSVoidElement)
				theValueList.add(theElement.getName());
		}
		return theValueList;
	}
	
	@Override
	public int hashCode() {
		int theHashCode = 0;
		for (String theString : getValueList()) {
			theHashCode+=theString.hashCode();
		}
		return theHashCode;
	}
	
	
	
	/**
	 * 
	 * 
	 * @param aString 
	 */
	public void setValue(String aString) {
		StringTokenizer theStringTokenizer = new StringTokenizer(aString
				.toLowerCase(), LOM.ITSTokenizerLimits );
		while (theStringTokenizer.hasMoreElements()) {
			String s = ((String) theStringTokenizer.nextElement()).trim();
			if (!s.equals(""))
				addValue(s);
		}
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#addNewLOMtoValues(lessonMapper.lom.LOM, lessonMapper.lom.LOMRelationType)
	 */
	@Override
	public void addNewLOMtoValues(LOM aLOM, LOMRelationType aRelation) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#addValue(lessonMapper.lom.LOMValue)
	 */
	public LOMValue addValue(LOMValue aLOMValue) {
		if (aLOMValue instanceof LOMValueVocabularySet){
			itsValueSet.addAll(((LOMValueVocabularySet) aLOMValue).getSet());
			if (!isEmpty()) itsValueSet.remove(LOMVocabulary.ITSVoidElement);
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#addValue(java.lang.String)
	 */
	public LOMValue addValue(String aValue) {
		itsValueSet.add(getLOMVocabulary().getVocabularyElement(aValue));
		if (!isEmpty()) itsValueSet.remove(LOMVocabulary.ITSVoidElement);
		return this;
	}

	/**
	 * 
	 * 
	 * @param aValue 
	 * 
	 * @return 
	 */
	public LOMValue addValue(LOMVocabularyElement aValue) {
		itsValueSet.add(aValue);
		if (!isEmpty()) itsValueSet.remove(LOMVocabulary.ITSVoidElement);
		return this;
	}
	

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#getValue()
	 */
	public String getValue() {
		String theResult = "";
		for (LOMVocabularyElement theValue : itsValueSet) {
			theResult += (theResult.equals("") ? "" : ", ") + theValue.getName();
		}
		return theResult;
	}

	public boolean isUndefinedValue() {
		for (LOMVocabularyElement theElement : itsValueSet) {
			if (theElement!=LOMVocabulary.ITSVoidElement) return false;
		} 
		return true;
	}
	
	
	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#getTaggedValue()
	 */
	@Override
	public String getTaggedValue() {
		return getLabel();
	}

	/**
	 * return all couples tq val is min of.
	 * 
	 * @param aLOMValue2 
	 * 
	 * @return 
	 */
	public LOMValue min(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueVocabularySet))
			return this;
		Set<LOMVocabularyElement> theMinString = new HashSet<LOMVocabularyElement>();
		int theMinValue = 99999999;
		for (LOMVocabularyElement	theElement : getSet()) {
			if (theElement == LOMVocabulary.ITSVoidElement) continue;
			int theValue = theElement.getOrderingValue();
			if (theValue < theMinValue) {
				theMinValue = theValue;
				theMinString.clear();
				theMinString.add(theElement);
			}
			if (theValue == theMinValue) {
				theMinString.add(theElement);
			}
		}
		for (LOMVocabularyElement	theElement : ((LOMValueVocabularySet)aLOMValue2).getSet()) {
			if (theElement == LOMVocabulary.ITSVoidElement) continue;
			int theValue = theElement.getOrderingValue();
			if (theValue < theMinValue) {
				theMinValue = theValue;
				theMinString.clear();
				theMinString.add(theElement);
			}
			if (theValue == theMinValue) {
				theMinString.add(theElement);
			}
		}
		
		return new LOMValueVocabularySet(getLOMAttribute(), theMinString);
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#max(lessonMapper.lom.LOMValue)
	 */
	public LOMValue max(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueVocabularySet))
			return this;
		
		Set<LOMVocabularyElement> theMaxString = new HashSet<LOMVocabularyElement>();
		int theMaxValue = -99999999;
		for (LOMVocabularyElement	theElement : getSet()) {
			if (theElement == LOMVocabulary.ITSVoidElement) continue;
			int theValue = theElement.getOrderingValue();
			if (theValue > theMaxValue) {
				theMaxValue = theValue;
				theMaxString.clear();
				theMaxString.add(theElement);
			}
			if (theValue == theMaxValue) {
				theMaxString.add(theElement);
			}
		}
		for (LOMVocabularyElement	theElement : ((LOMValueVocabularySet)aLOMValue2).getSet()) {
			if (theElement == LOMVocabulary.ITSVoidElement) continue;
			int theValue = theElement.getOrderingValue();
			if (theValue > theMaxValue) {
				theMaxValue = theValue;
				theMaxString.clear();
				theMaxString.add(theElement);
			}
			if (theValue == theMaxValue) {
				theMaxString.add(theElement);
			}
		}
		
		return new LOMValueVocabularySet(getLOMAttribute(), theMaxString);
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#union(lessonMapper.lom.LOMValue)
	 */
	public LOMValue union(MetadataSetValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueVocabularySet))
			return this;
		return new LOMValueVocabularySet(this).addValue((LOMValueVocabularySet)aLOMValue2);
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#intersection(lessonMapper.lom.LOMValue)
	 */
	public LOMValue intersection(MetadataSetValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueVocabularySet))
			return this;
		LOMValueVocabularySet theValueVocabularySet = ((LOMValueVocabularySet) aLOMValue2);
		LOMValueVocabularySet theResult = new LOMValueVocabularySet(
				getLOMAttribute() != null ? getLOMAttribute() : theValueVocabularySet
						.getLOMAttribute());
		for (LOMVocabularyElement theElement : theValueVocabularySet.getSet()) {
			if (getSet().contains(theElement)) {
				theResult.addValue(theElement);
			}
		}
		return theResult;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#sum(lessonMapper.lom.LOMValue)
	 */
	public LOMValue sum(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueVocabularySet))
			return this;
		System.out.println("sum not yet defined for LOMValueVocabularySet");
		return this;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#division(lessonMapper.lom.LOMValue)
	 */
	public LOMValue division(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueVocabularySet))
			return this;
		System.out.println("division not yet defined for LOMValueVocabularySet");
		return this;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#product(lessonMapper.lom.LOMValue)
	 */
	public LOMValue product(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueVocabularySet))
			return this;
		System.out.println("product not yet defined for LOMValueVocabularySet");
		return this;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#subtraction(lessonMapper.lom.LOMValue)
	 */
	public LOMValue subtraction(LOMValue aLOMValue2) {
		// delas wth set substraction
		if (!(aLOMValue2 instanceof LOMValueVocabularySet))
			return this;
		LOMValueVocabularySet theResult = new LOMValueVocabularySet(
				getLOMAttribute() != null ? getLOMAttribute() : aLOMValue2
						.getLOMAttribute());
		for (LOMVocabularyElement theElement : getSet()) {
			if (!((LOMValueVocabularySet) aLOMValue2).getSet().contains(theElement))
				theResult.addValue(theElement);
		}
		return theResult;
	}


	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#inferiorOrEqual(lessonMapper.lom.LOMValue)
	 */
	public boolean inferiorOrEqual(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueVocabularySet))
			return false;
		LOMValueVocabularySet theOtherValue = ((LOMValueVocabularySet) aLOMValue2);
		//if (theOtherValue.isEmpty() ) return true;
		//if (isEmpty()) return true;
		boolean isInferior = true;
		for (LOMVocabularyElement theComparedValue : theOtherValue.getSet()) {
			if (theComparedValue == LOMVocabulary.ITSVoidElement) continue;
			int theComparedValueRank = theComparedValue.getOrderingValue();
			for (LOMVocabularyElement theLocalValue : getSet()) {
				if (theLocalValue == LOMVocabulary.ITSVoidElement) continue;
				int theLocalValueRank =theLocalValue.getOrderingValue();
				isInferior &= theLocalValueRank <= theComparedValueRank;
			}
		}
		return isInferior;
	}

	

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#superiorOrEqual(lessonMapper.lom.LOMValue)
	 */
	public boolean superiorOrEqual(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueVocabularySet))
			return false;
		LOMValueVocabularySet theOtherValue = ((LOMValueVocabularySet) aLOMValue2);
		if (theOtherValue.isEmpty() ) return true;
		if (isEmpty()) return false;
		boolean isSuperior = true;
		for (LOMVocabularyElement theComparedValue : theOtherValue.getSet()) {
			if (theComparedValue == LOMVocabulary.ITSVoidElement) continue;
			int theComparedValueRank = theComparedValue.getOrderingValue();
			for (LOMVocabularyElement theLocalValue : getSet()) {
				if (theLocalValue == LOMVocabulary.ITSVoidElement) continue;
				int theLocalValueRank = theLocalValue.getOrderingValue();
				isSuperior &= theLocalValueRank >= theComparedValueRank;
			}
		}
		return isSuperior;
	}

	
	
	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#dif(lessonMapper.lom.LOMValue)
	 */
	public boolean dif(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueVocabularySet))
			return false;
		return !equals(((LOMValueVocabularySet) aLOMValue2));

	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#contains(lessonMapper.lom.LOMValue)
	 */
	public boolean contains(MetadataSetValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueVocabularySet))
			return false;
		boolean theResult = true;
		for (LOMVocabularyElement theElement : ((LOMValueVocabularySet) aLOMValue2)
				.getSet()) {
			if (theElement == LOMVocabulary.ITSVoidElement) continue;
			theResult = theResult && getSet().contains(theElement);
		}
		return theResult;

	}

	/**
	 * 
	 * 
	 * @param aString 
	 * 
	 * @return 
	 */
	public boolean containsString(String aString) {
		LOMVocabularyElement theElement = getLOMVocabulary().getVocabularyElement(aString);
		if (theElement == null || theElement == LOMVocabulary.ITSVoidElement) return true;
		return getSet().contains(theElement);
	}
	
	
	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#contained(lessonMapper.lom.LOMValue)
	 */
	public boolean contained(LOMValue aLOMValue2) {
		return aLOMValue2.contains(this);
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (!(o instanceof LOMValueVocabularySet))
			return false;
		LOMValueVocabularySet theOtherSet =  (LOMValueVocabularySet) o;
		boolean isEqual = true;
		for (LOMVocabularyElement theElement : getSet()){
			if (theElement == LOMVocabulary.ITSVoidElement) continue;
			isEqual &= theOtherSet.getSet().contains(theElement);
		}
		for (LOMVocabularyElement theElement : theOtherSet.getSet()){
			if (theElement == LOMVocabulary.ITSVoidElement) continue;
			isEqual &= getSet().contains(theElement);
		}
		return isEqual;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public Collection<LOMVocabularyElement> getSet() {
		return itsValueSet;
	}

	/**
	 * return false if this Vocabulary value
	 * is empty or has only a notdefined element.
	 * 
	 * @return 
	 */
	public boolean isEmpty() {
		for (LOMVocabularyElement theElement : getSet()) {
			if (theElement != LOMVocabulary.ITSVoidElement)
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#clone()
	 */
	@Override
	public LOMValueVocabularySet clone() {
		return new LOMValueVocabularySet(this);
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#getLabel()
	 */
	@Override
	public String getLabel() {
		String theLabel=null ;
		for (LOMVocabularyElement theVocabularyElement : getSet()) {
				if (theLabel == null)
					theLabel = theVocabularyElement.getLabel();
				else
					theLabel += ", "
							+ theVocabularyElement.getLabel();
			}
		return theLabel;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "LOMValueVocabularySet{" + itsValueSet + "}";
	}

	
	/**
	 * calculate vector similarity between both this lom value and the other
	 * only strings of this LOMVAlue are taken into account to make the calculus.
	 * 
	 * @param aValue 
	 * 
	 * @return 
	 */
	public double getTermSimilarityWith(LOMValue aValue) {
		if (! (aValue instanceof LOMValueVocabularySet)) return 0;
		LOMValueVocabularySet theOtherValue = (LOMValueVocabularySet) aValue;
		List<String> theStrings=new ArrayList<String>();
		for (LOMVocabularyElement theElement : itsValueSet) 
			theStrings.add(theElement.getName());
		List<String> theOtherStrings = new ArrayList<String>();
		for (LOMVocabularyElement theElement : theOtherValue.itsValueSet) 
			theOtherStrings.add(theElement.getName());
		return Utils.listSimilarity(theStrings, theOtherStrings); 
	}

	/**
	 * calculate  similarity between both this lom value and the other
	 * only strings of this LOMVAlue are taken into account to make the calculus
	 * = proba of aValue given THIS.
	 * 
	 * @param aValue 
	 * 
	 * @return 
	 */
	public double getTermSimilarityProbaWith(LOMValue aValue) {
		if (! (aValue instanceof LOMValueVocabularySet)) return 0;
		LOMValueVocabularySet theOtherValue = (LOMValueVocabularySet) aValue;
		List<String> theStrings = new ArrayList<String>();
		for (LOMVocabularyElement theElement : itsValueSet) 
			theStrings.add(theElement.getName());
		List<String> theOtherStrings= new ArrayList<String>();
		for (LOMVocabularyElement theElement : theOtherValue.itsValueSet) 
			theOtherStrings.add(theElement.getName());
		return Utils.listSimilarityProba(theStrings, theOtherStrings); 
	}
	
	
	
	
	/**
	 * return a element based on the values of aLOMValue.
	 * 
	 * @param aLOMValue 
	 * 
	 * @return 
	 */
	public static Element makeXMLElement(LOMValueVocabularySet aLOMValue) {
		Element theElement = new Element("LOMValueVocabularySet");
		Element theSet = new Element("Set");
		theSet.addContent(aLOMValue.getValue());
		theElement.addContent(theSet);
		Element theAttribute = LOMAttribute.makeXMLElement(aLOMValue
				.getLOMAttribute());
		theElement.addContent(theAttribute);
		return theElement;
	}

	/**
	 * return aCache with the value stored in aElement.
	 * 
	 * @param aElement 
	 * 
	 * @return 
	 */
	public static LOMValue buildFromXMLElement(Element aElement) {
		String theSet = aElement.getChild("Set").getTextTrim();
		LOMAttribute theAttribute = LOMAttribute.buildFromXMLElement(aElement
				.getChild("LOMAttribute"));
		// FIXME change null by a lom
		return new LOMValueVocabularySet(theSet, theAttribute);
	}

	

}
