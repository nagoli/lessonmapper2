/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.diffusion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lessonMapper.diffusion.fixpoint.Value;
import lessonMapper.lom.LOMValue;

import org.jdom.Element;

/**
 * This class encapsulates the List of LOMRestrictionValue
 * Basically it keeps one reference by operator
 * <p/>
 * <p/>
 * User: omotelet
 * Date: Apr 8, 2005
 * Time: 2:43:58 PM.
 */
public class LOMRestrictionSet implements Serializable, Value {

	/**
	 * 
	 */
	Map<RestrictionOperator, LOMRestrictionValue> itsTable;

	/**
	 * 
	 */
	public LOMRestrictionSet() {
		itsTable = new EnumMap<RestrictionOperator, LOMRestrictionValue>(
				RestrictionOperator.class);
	}

	/**
	 * 
	 * 
	 * @param aRestrictionSet 
	 */
	public LOMRestrictionSet(LOMRestrictionSet aRestrictionSet) {
		this();
		concat(aRestrictionSet);
	}

	/**
	 * add a restriction value to this restriction set
	 * return false  if the restriction value is empty
	 * return false if the value could not be merged.
	 * 
	 * @param aRestrictionValue 
	 * 
	 * @return 
	 */
	public boolean add(LOMRestrictionValue aRestrictionValue) {
		if (aRestrictionValue.isEmpty())
			return false;
		RestrictionOperator theOperator = aRestrictionValue.getOperator();
		LOMRestrictionValue theExistingValue = get(theOperator);
		if (theExistingValue == null) {
			itsTable.put(theOperator, aRestrictionValue);
			return true;
		} else
			return theExistingValue.merge(aRestrictionValue.getValue());
	}

	/**
	 * concat this with aLOMRestrictionSet given in parameter
	 * returns this.
	 * 
	 * @param aLOMRestrictionSet 
	 * 
	 * @return 
	 */
	public LOMRestrictionSet concat(LOMRestrictionSet aLOMRestrictionSet) {
		if (aLOMRestrictionSet != null) {
			for (LOMRestrictionValue theValue : aLOMRestrictionSet
					.getLOMRestrictionValues()) {
				try {
					add(theValue.clone());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return this;
	}

	/**
	 * 
	 * 
	 * @param anOperator 
	 * 
	 * @return 
	 */
	public LOMRestrictionValue get(RestrictionOperator anOperator) {
		return itsTable.get(anOperator);
	}

	/**
	 * return the list of restriction value that do not satisfy with aLOMValue.
	 * 
	 * @param aLOMValue 
	 * 
	 * @return List<LOMrestrictionValue>
	 */
	public List<LOMRestrictionValue> restrictionsNotSatisfiedBy(
			LOMValue aLOMValue) {
		List<LOMRestrictionValue> theValues = new ArrayList<LOMRestrictionValue>();
		for (LOMRestrictionValue theRestrictionValue : getLOMRestrictionValues()) {
			if (!theRestrictionValue.isSatisfiedBy(aLOMValue))
				theValues.add(theRestrictionValue);
		}
		return theValues;
	}

	/**
	 * return the list of restriction value that satisfy with aLOMValue.
	 * 
	 * @param aLOMValue 
	 * 
	 * @return List<LOMrestrictionValue>
	 */
	public List<LOMRestrictionValue> restrictionsSatisfiedBy(LOMValue aLOMValue) {
		List<LOMRestrictionValue> theValues = new ArrayList<LOMRestrictionValue>();
		for (LOMRestrictionValue theRestrictionValue : getLOMRestrictionValues()) {
			if (theRestrictionValue.isSatisfiedBy(aLOMValue))
				theValues.add(theRestrictionValue);
		}
		return theValues;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public LOMRestrictionSet clone() throws CloneNotSupportedException {
		LOMRestrictionSet theCopy = new LOMRestrictionSet();
		for (LOMRestrictionValue theValue : getLOMRestrictionValues()) {
			if (theValue != null)
				theCopy.add(theValue.clone());
		}
		return theCopy;
	}

	/**
	 * this method merge the restrictionvalues it contains
	 * for ex: contained (a, b ,c, d) y inf+eq b  are merged in contained (a,b).
	 */
	public void mergeOperators() {
		for (LOMRestrictionValue theRestrictionValue : getLOMRestrictionValues()) {
			RestrictionOperator theOperator = theRestrictionValue.getOperator();
			boolean hasChanged = theRestrictionValue
					.transformAsPotentialValues();
			if (hasChanged) {
				itsTable.remove(theOperator);
				add(theRestrictionValue);
			}
		}
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public Collection<LOMRestrictionValue> getLOMRestrictionValues() {
		return itsTable.values();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object aObj) {
		if (!(aObj instanceof LOMRestrictionSet))
			return false;
		LOMRestrictionSet theSet = (LOMRestrictionSet) aObj;
		if (itsTable.size() != theSet.itsTable.size())
			return false;
		for (RestrictionOperator theOperator : itsTable.keySet()) {
			if (! itsTable.get(theOperator).equals(
					theSet.itsTable.get(theOperator))) return false;
		}
		 return true;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public boolean isEmpty() {
		return itsTable.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "LOMRestrictionSet{" + itsTable + "}";
	}

	/**
	 * return a element based on the values of aLOMRestrictionSet.
	 * 
	 * @param aRestrictionSet 
	 * 
	 * @return 
	 */
	public static Element makeXMLElement(LOMRestrictionSet aRestrictionSet) {
		Element theElement = new Element("LOMRestrictionSet");
		if (aRestrictionSet != null)
			for (LOMRestrictionValue theValue : aRestrictionSet.itsTable
					.values()) {
				theElement.addContent(LOMRestrictionValue
						.makeXMLElement(theValue));
			}
		return theElement;
	}

	/**
	 * return aRestrictionSet with the value stored in aElement.
	 * 
	 * @param aElement 
	 * 
	 * @return 
	 */
	public static LOMRestrictionSet buildFromXMLElement(Element aElement) {
		LOMRestrictionSet theSet = new LOMRestrictionSet();
		for (Iterator iter = aElement.getChildren().iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			if (element.getName().equals("LOMRestrictionValue"))
				theSet.add(LOMRestrictionValue.buildFromXMLElement(element));
		}
		return theSet;
	}

	/* public static void main(String[] args) {
	 LOMRestrictionValue theRestrictionValue1 = new LOMRestrictionValue( LOMRestrictionValue.CONTAINED,
	 new LOMValueSet(",  lowdensity, highdensity",LOMAttribute.getLOMAttribute("educational/semanticdensity")));
	 LOMRestrictionValue theRestrictionValue2 = new LOMRestrictionValue(LOMRestrictionValue.INF,
	 new LOMValueSet(" , highdensity",LOMAttribute.getLOMAttribute("educational/semanticdensity")));
	 LOMRestrictionValue theRestrictionValue3 = new LOMRestrictionValue(LOMRestrictionValue.CONTAINS,
	 new LOMValueSet(" , verylowdensity, highdensity",LOMAttribute.getLOMAttribute("educational/semanticdensity")));
	 LOMRestrictionSet v1 = new LOMRestrictionSet(), v2 = new LOMRestrictionSet();
	 v1.add(theRestrictionValue1);
	 v1.add(theRestrictionValue2);
	 v2.add(theRestrictionValue3);
	 System.out.println(v1.concat(v2));
	 }*/

}
