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

import java.io.Serializable;


import org.jdom.Element;

import diffuse.metadata.MetadataSetValue;

/**
 * User: omotelet Date: Apr 18, 2005 Time: 3:24:52 PM.
 */
public abstract class LOMValue implements Serializable, MetadataSetValue {

	/**
	 * 
	 */
	LOM itsOriginalLOM;

	/**
	 * 
	 */
	LOMAttribute itsLOMAttribute;

	/**
	 * 
	 * 
	 * @param aLOMAttribute
	 */
	public LOMValue(LOMAttribute aLOMAttribute) {
		itsLOMAttribute = aLOMAttribute;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public LOMAttribute getLOMAttribute() {
		return itsLOMAttribute;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public LOMVocabulary getLOMVocabulary() {
		return itsLOMAttribute.getLOMVocabulary();
	}

	/**
	 * 
	 * 
	 * @param aLOMValue
	 * 
	 * @return
	 */
	public abstract LOMValue addValue(LOMValue aLOMValue);

	/**
	 * 
	 * 
	 * @param aValue
	 * 
	 * @return
	 */
	public abstract LOMValue addValue(String aValue);

	/**
	 * 
	 * 
	 * @return
	 */
	public abstract String getValue();

	/**
	 * 
	 * 
	 * @return
	 */
	public abstract String getTaggedValue();

	/**
	 * 
	 * 
	 * @param aLOMValue2
	 * 
	 * @return
	 */
	public abstract LOMValue min(LOMValue aLOMValue2);

	/**
	 * 
	 * 
	 * @param aLOMValue2
	 * 
	 * @return
	 */
	public abstract LOMValue max(LOMValue aLOMValue2);

	/**
	 * 
	 * 
	 * @param aLOMValue2
	 * 
	 * @return
	 */
	public abstract LOMValue union(MetadataSetValue aLOMValue2);

	/**
	 * 
	 * 
	 * @param aLOMValue2
	 * 
	 * @return
	 */
	public abstract LOMValue intersection(MetadataSetValue aLOMValue2);

	/**
	 * 
	 * 
	 * @param aLOMValue2
	 * 
	 * @return
	 */
	public abstract LOMValue sum(LOMValue aLOMValue2);

	/**
	 * 
	 * 
	 * @param aLOMValue2
	 * 
	 * @return
	 */
	public abstract LOMValue division(LOMValue aLOMValue2);

	/**
	 * 
	 * 
	 * @param aLOMValue2
	 * 
	 * @return
	 */
	public abstract LOMValue product(LOMValue aLOMValue2);

	/**
	 * 
	 * 
	 * @param aLOMValue2
	 * 
	 * @return
	 */
	public abstract LOMValue subtraction(LOMValue aLOMValue2);

	// public abstract boolean inferior( LOMValue aLOMValue2);

	/**
	 * 
	 * 
	 * @param aLOMValue2
	 * 
	 * @return
	 */
	public abstract boolean inferiorOrEqual(LOMValue aLOMValue2);

	// public abstract boolean superior( LOMValue aLOMValue2);

	public int compareTo(MetadataSetValue aO) {
		if (equals(aO))
			return 0;
		if (aO instanceof LOMValue) {
			LOMValue theLOMValue = (LOMValue) aO;
			if (inferiorOrEqual(theLOMValue))
				return -1;
		}
		return 1;
	}

	/**
	 * 
	 * 
	 * @param aLOMValue2
	 * 
	 * @return
	 */
	public abstract boolean superiorOrEqual(LOMValue aLOMValue2);

	/**
	 * 
	 * 
	 * @param aLOMValue2
	 * 
	 * @return
	 */
	public abstract boolean dif(LOMValue aLOMValue2);

	/**
	 * 
	 * 
	 * @param aLOMValue2
	 * 
	 * @return
	 */
	public abstract boolean contains(MetadataSetValue aLOMValue2);

	/**
	 * 
	 * 
	 * @param aLOMValue2
	 * 
	 * @return
	 */
	public abstract boolean contained(LOMValue aLOMValue2);

	/**
	 * 
	 * 
	 * @return
	 */
	public abstract boolean isEmpty();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public abstract LOMValue clone();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public abstract boolean equals(Object aObject);

	/**
	 * 
	 * 
	 * @return
	 */
	public abstract String getLabel();

	/**
	 * add a new lom at the end of all the lists of lom. create a new list for
	 * the couple in order to avoid side effect
	 * 
	 * @param aLOM
	 * @param aRelation
	 */
	public abstract void addNewLOMtoValues(LOM aLOM, LOMRelationType aRelation);

	/**
	 * return a element based on the values of aLOMValue.
	 * 
	 * @param aLOMValue
	 * 
	 * @return
	 */
	static public Element makeXMLElement(LOMValue aLOMValue) {
		if (aLOMValue instanceof LOMValueInt)
			return LOMValueInt.makeXMLElement((LOMValueInt) aLOMValue);
		if (aLOMValue instanceof LOMValueSet)
			return LOMValueSet.makeXMLElement((LOMValueSet) aLOMValue);
		if (aLOMValue instanceof LOMValueVocabularySet)
			return LOMValueVocabularySet
					.makeXMLElement((LOMValueVocabularySet) aLOMValue);
		return null;
	}

	/**
	 * return aCache with the value stored in aElement.
	 * 
	 * @param aElement
	 * @return
	 */
	// static public LOMValue buildFromXMLElement(Element aElement);
}
