/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.lom;

import java.util.ArrayList;
import java.util.Collection;


import org.jdom.Element;

import diffuse.metadata.MetadataSetValue;


/**
 * This class encapsulates the integer value of the LOM elements
 * <p/>
 * User: omotelet
 * Date: Mar 21, 2005
 * Time: 10:36:17 AM.
 */
public class LOMValueInt extends LOMValue {


    /**
     * 
     */
    int itsInt = 0;

    /**
     * 
     * 
     * @param aLOMAttribute 
     */
    public LOMValueInt(LOMAttribute aLOMAttribute) {
        super(aLOMAttribute);
    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#addValue(java.lang.String)
     */
    public LOMValue addValue(String aValue) {
        itsInt += new Integer(aValue).intValue();

        return this;
    }

    public Collection<MetadataSetValue> getAtomicValues() {
       	ArrayList<MetadataSetValue> theArrayList = new ArrayList<MetadataSetValue>();
		theArrayList.add(this);
       	return theArrayList;
    }
    
    @Override
    public int hashCode() {
    	return itsInt;
    }
    
 
    /**
     * 
     * 
     * @param aLOMAttribute 
     * @param aInt 
     */
    public LOMValueInt(int aInt, LOMAttribute aLOMAttribute) {
        super(aLOMAttribute);
        itsInt = aInt;
    }

    /**
     * 
     * 
     * @param aLOMAttribute 
     * @param aString 
     */
    public LOMValueInt(String aString, LOMAttribute aLOMAttribute) {
        super(aLOMAttribute);
        itsInt = new Integer(aString).intValue();
    }

    public boolean isUndefinedValue() {
    	return itsInt == 0;
    }
    

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#addValue(lessonMapper.lom.LOMValue)
     */
    public LOMValue addValue(LOMValue aLOMValue) {
        if (aLOMValue instanceof LOMValueInt)
            itsInt += new Integer(aLOMValue.getValue()).intValue();
        return this;
    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#getValue()
     */
    public String getValue() {
        return "" + itsInt;
    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#getTaggedValue()
     */
    public String getTaggedValue() {
        return getValue();
    }
    
    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#min(lessonMapper.lom.LOMValue)
     */
    public LOMValue min(LOMValue aLOMValue2) {
        if (!(aLOMValue2 instanceof LOMValueInt)) return this;
        return new LOMValueInt(Math.min(getIntValue(), ((LOMValueInt) aLOMValue2).getIntValue()), getLOMAttribute()!=null?getLOMAttribute():aLOMValue2.getLOMAttribute());
    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#max(lessonMapper.lom.LOMValue)
     */
    public LOMValue max(LOMValue aLOMValue2) {
        if (!(aLOMValue2 instanceof LOMValueInt)) return this;
        return new LOMValueInt(Math.max(getIntValue(), ((LOMValueInt) aLOMValue2).getIntValue()), getLOMAttribute()!=null?getLOMAttribute():aLOMValue2.getLOMAttribute());

    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#union(lessonMapper.lom.LOMValue)
     */
    public LOMValue union(MetadataSetValue aLOMValue2) {
        if (!(aLOMValue2 instanceof LOMValueInt)) return this;
        System.out.println("union not yet defined for LOMValueInt");

        return this;//todo implement it;

    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#intersection(lessonMapper.lom.LOMValue)
     */
    public LOMValue intersection(MetadataSetValue aLOMValue2) {
        if (!(aLOMValue2 instanceof LOMValueInt)) return this;
        System.out.println("intersection not yet defined for LOMValueInt");

        return this;//todo implement it;

    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#sum(lessonMapper.lom.LOMValue)
     */
    public LOMValue sum(LOMValue aLOMValue2) {
        if (!(aLOMValue2 instanceof LOMValueInt)) return this;
        return new LOMValueInt(getIntValue() + ((LOMValueInt) aLOMValue2).getIntValue(), getLOMAttribute()!=null?getLOMAttribute():aLOMValue2.getLOMAttribute());

    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#division(lessonMapper.lom.LOMValue)
     */
    public LOMValue division(LOMValue aLOMValue2) {
        if (!(aLOMValue2 instanceof LOMValueInt)) return this;
        return new LOMValueInt(getIntValue() / ((LOMValueInt) aLOMValue2).getIntValue(), getLOMAttribute()!=null?getLOMAttribute():aLOMValue2.getLOMAttribute());

    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#product(lessonMapper.lom.LOMValue)
     */
    public LOMValue product(LOMValue aLOMValue2) {
        if (!(aLOMValue2 instanceof LOMValueInt)) return this;
        return new LOMValueInt(getIntValue() * ((LOMValueInt) aLOMValue2).getIntValue(), getLOMAttribute()!=null?getLOMAttribute():aLOMValue2.getLOMAttribute());

    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#subtraction(lessonMapper.lom.LOMValue)
     */
    public LOMValue subtraction(LOMValue aLOMValue2) {
        if (!(aLOMValue2 instanceof LOMValueInt)) return this;
        return new LOMValueInt(getIntValue() - ((LOMValueInt) aLOMValue2).getIntValue(), getLOMAttribute()!=null?getLOMAttribute():aLOMValue2.getLOMAttribute());

    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof LOMValueInt)) return false;
        return getIntValue() == ((LOMValueInt) obj).getIntValue();
    }

    /**
     * 
     * 
     * @param aLOMValue2 
     * 
     * @return 
     */
    public boolean inferior(LOMValue aLOMValue2) {
        if (!(aLOMValue2 instanceof LOMValueInt)) return false;
        return getIntValue() < ((LOMValueInt) aLOMValue2).getIntValue();

    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#inferiorOrEqual(lessonMapper.lom.LOMValue)
     */
    public boolean inferiorOrEqual(LOMValue aLOMValue2) {
        if (!(aLOMValue2 instanceof LOMValueInt)) return false;
        return getIntValue() <= ((LOMValueInt) aLOMValue2).getIntValue();

    }

    /**
     * 
     * 
     * @param aLOMValue2 
     * 
     * @return 
     */
    public boolean superior(LOMValue aLOMValue2) {
        if (!(aLOMValue2 instanceof LOMValueInt)) return false;
        return getIntValue() > ((LOMValueInt) aLOMValue2).getIntValue();

    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#superiorOrEqual(lessonMapper.lom.LOMValue)
     */
    public boolean superiorOrEqual(LOMValue aLOMValue2) {
        if (!(aLOMValue2 instanceof LOMValueInt)) return false;
        return getIntValue() >= ((LOMValueInt) aLOMValue2).getIntValue();

    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#dif(lessonMapper.lom.LOMValue)
     */
    public boolean dif(LOMValue aLOMValue2) {
        if (!(aLOMValue2 instanceof LOMValueInt)) return false;
        return getIntValue() != ((LOMValueInt) aLOMValue2).getIntValue();

    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#contains(lessonMapper.lom.LOMValue)
     */
    public boolean contains(MetadataSetValue aLOMValue2) {
        if (!(aLOMValue2 instanceof LOMValueInt)) return false;
        System.out.println("contains not yet defined for LOMValueInt");
        return equals(aLOMValue2);
    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#contained(lessonMapper.lom.LOMValue)
     */
    public boolean contained(LOMValue aLOMValue2) {
        if (!(aLOMValue2 instanceof LOMValueInt)) return false;
        System.out.println("contained not yet defined for LOMValueSet");
        return equals(aLOMValue2);
    }

    /**
     * 
     * 
     * @return 
     */
    public int getIntValue() {
        return itsInt;
    }

    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#clone()
     */
    @Override
    public LOMValueInt clone() {
       	return new LOMValueInt(itsInt,itsLOMAttribute);
    }
    
    /**
     * return false.
     * 
     * @return 
     */
    public boolean isEmpty(){
    	return false;
    }
   
    /* (non-Javadoc)
     * @see lessonMapper.lom.LOMValue#getLabel()
     */
    public String getLabel() {
    	return ""+itsInt;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "LOMValueInt{" +
                "itsInt=" + itsInt +
                "}";
    }

    

	/**
	 * add a new lom at the end of all the lists of lom. create a new list for
	 * the couple in order to avoid side effect
	 * 
	 * @param aLOM 
	 * @param aType 
	 */
	public void addNewLOMtoValues(LOM aLOM, LOMRelationType aType) {
		//FIXME this function do nothing is there a sense to add the tag on Valueint?
	}
    
    
    /**
     * return a element based on the values of aLOMValue.
     * 
     * @param aLOMValue 
     * 
     * @return 
     */
	public  static Element makeXMLElement(LOMValueInt aLOMValue){
		Element theElement = new Element("LOMValueInt");
		Element theInt = new Element("Int");
		theInt.addContent(""+aLOMValue.getIntValue());
		theElement.addContent(theInt);
		Element theAttribute = LOMAttribute.makeXMLElement(aLOMValue.getLOMAttribute());
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
	public  static LOMValue buildFromXMLElement(Element aElement) {
		int theInteger = Integer.parseInt( aElement.getChild("Int").getTextTrim());
		LOMAttribute theAttribute = LOMAttribute.buildFromXMLElement(aElement.getChild("LOMAttribute"));
		return new LOMValueInt(theInteger, theAttribute);
	}

	
    

}
