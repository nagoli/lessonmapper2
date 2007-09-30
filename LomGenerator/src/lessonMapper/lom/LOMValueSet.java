/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.lom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jdom.Element;
import org.tartarus.snowball.SnowballProgram;

import diffuse.metadata.MetadataSetValue;
import util.Couple;
import util.Utils;

/**
 * Encapsulate the value of LOM attributes <p/> User: omotelet Date: Mar 17,
 * 2005 Time: 11:49:24 AM.
 */
public class LOMValueSet extends LOMValue {

	/**
	 * 
	 */
	public static final String ITSStemingLang = "es";
	
	/**
	 * 
	 */
	LOMValueHashSet itsValueSet = new LOMValueHashSet();

	/**
	 * 
	 * 
	 * @param aLOMAttribute 
	 */
	public LOMValueSet(LOMAttribute aLOMAttribute) {
		super(aLOMAttribute);
	}

	/**
	 * 
	 * 
	 * @param aLOMAttribute 
	 * @param aString 
	 */
	public LOMValueSet(String aString, LOMAttribute aLOMAttribute) {
		super(aLOMAttribute);
		setValue(aString.trim());
	}

	/**
	 * 
	 * 
	 * @param aLOMAttribute 
	 * @param aSet 
	 */
	public LOMValueSet(LOMValueHashSet aSet, LOMAttribute aLOMAttribute) {
		super(aLOMAttribute);
		itsValueSet = aSet;
	}

	/**
	 * 
	 * 
	 * @param aLOMValueSet 
	 * @param aLOMAttribute 
	 */
	public LOMValueSet(LOMValueSet aLOMValueSet, LOMAttribute aLOMAttribute) {
		super(aLOMAttribute);
		itsValueSet.addAll(aLOMValueSet.getSet());
	}

	/**
	 * 
	 * 
	 * @param aLOMValueSet 
	 */
	public LOMValueSet(LOMValueSet aLOMValueSet) {
		this(aLOMValueSet, aLOMValueSet.getLOMAttribute());
	}

	
	
	/**
	 * 
	 * 
	 * @return 
	 */
	public List<String> getValueList(){
		List<String> theValueList = new ArrayList<String>();
		for (Couple<String, List<Couple<LOM, LOMRelationType>>> theString : itsValueSet) 
			theValueList.add(theString.getLeftElement());
		return theValueList;
	}
	
	  public Collection<MetadataSetValue> getAtomicValues() {
	       	ArrayList<MetadataSetValue> theArrayList = new ArrayList<MetadataSetValue>();
	       	for (Couple<String, List<Couple<LOM, LOMRelationType>>> theString : itsValueSet) 
				theArrayList.add(new LOMValueSet(theString.getLeftElement(),itsLOMAttribute));
	       	return theArrayList;
	    }
	
	
	/**
	 * 
	 * 
	 * @param aString 
	 */
	public void setValue(String aString) {
		StringTokenizer theStringTokenizer = new StringTokenizer(aString, LOM.ITSTokenizerLimits );
		
		while (theStringTokenizer.hasMoreElements()) {
			String s = ((String) theStringTokenizer.nextElement()).trim();
			if (!s.equals(""))
				itsValueSet.add(s);
		}
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#addValue(lessonMapper.lom.LOMValue)
	 */
	public LOMValue addValue(LOMValue aLOMValue) {
		if (aLOMValue instanceof LOMValueSet)
			itsValueSet.addAll(((LOMValueSet) aLOMValue).getSet());
		return this;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#addValue(java.lang.String)
	 */
	public LOMValue addValue(String aValue) {
		setValue(aValue);
		return this;
	}

	/**
	 * 
	 * 
	 * @param aValue 
	 * 
	 * @return 
	 */
	public LOMValue addValue(
			Couple<String, List<Couple<LOM, LOMRelationType>>> aValue) {
		itsValueSet.add(aValue);
		return this;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#getValue()
	 */
	public String getValue() {
		String theResult = "";
		for (String theValue : itsValueSet.getAllValues()) {
			theResult += (theResult.equals("") ? "" : ", ") + theValue;
		}
		return theResult;
	}
	
	public boolean isUndefinedValue() {
		 return getValue().trim().equals("");
	}
	

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#getTaggedValue()
	 */
	public String getTaggedValue() {
		String theResult = "";
		for (Couple<String, List<Couple<LOM, LOMRelationType>>> theCouple : itsValueSet) {
			String theRelations = "";
			for (Couple<LOM, LOMRelationType> theLOMAndRelation : theCouple
					.getRightElement()) {
				theRelations += "('"
						+ LOMAttribute.getLOMAttribute("general/title")
								.getValueIn(theLOMAndRelation.getLeftElement())
								.getValue() + "',";
				if (theLOMAndRelation.getRightElement() != null)
					theRelations += theLOMAndRelation.getRightElement().itsLabel;
				theRelations += "),";
			}
			String s = "(-" + theCouple.getLeftElement() + "=" + theRelations
					+ ")";
			theResult += (theResult.equals("") ? "" : ", ") + s;
		}
		return theResult;
	}

	/**
	 * return all couples tq val is min of.
	 * 
	 * @param aLOMValue2 
	 * 
	 * @return 
	 */
	public LOMValue min(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueSet))
			return this;
		if (getLOMVocabulary().isEmpty()) {
			System.out
					.println("min not defined for LOMValueSet without vocabulary");
			return this;
		}
		List<String> theMinString = new ArrayList<String>();
		int theMinValue = 999999999;
		for (String theString : getSet().getAllValues()) {
			int theValue = getLOMVocabulary().getOrderingValueOf(theString);
			if (theValue < theMinValue) {
				theMinValue = theValue;
				theMinString.clear();
				theMinString.add(theString);
			}
			if (theValue == theMinValue) {
				theMinString.add(theString);
			}
		}
		for (String theString : ((LOMValueSet) aLOMValue2).getSet()
				.getAllValues()) {
			int theValue = getLOMVocabulary().getOrderingValueOf(theString);
			if (theValue < theMinValue) {
				theMinValue = theValue;
				theMinString.clear();
				theMinString.add(theString);
			}
			if (theValue == theMinValue) {
				theMinString.add(theString);
			}
		}
		LOMValueHashSet theSet = getSet().getAllCouplesWithValues(theMinString);
		theSet.addAll(((LOMValueSet) aLOMValue2).getSet()
				.getAllCouplesWithValues(theMinString));

		return new LOMValueSet(((LOMValueSet) aLOMValue2).getSet(),
				getLOMAttribute() != null ? getLOMAttribute() : aLOMValue2
						.getLOMAttribute());
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#max(lessonMapper.lom.LOMValue)
	 */
	public LOMValue max(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueSet))
			return this;
		if (getLOMVocabulary().isEmpty()) {
			System.out
					.println("max not defined for LOMValueSet without vocabulary");
			return this;
		}
		List<String> theMaxString = new ArrayList<String>();
		int theMaxValue = -99999999;
		for (String theString : getSet().getAllValues()) {
			int theValue = getLOMVocabulary().getOrderingValueOf(theString);
			if (theValue > theMaxValue) {
				theMaxValue = theValue;
				theMaxString.clear();
				theMaxString.add(theString);
			}
			if (theValue == theMaxValue) {
				theMaxString.add(theString);
			}
		}
		for (String theString : ((LOMValueSet) aLOMValue2).getSet()
				.getAllValues()) {
			int theValue = getLOMVocabulary().getOrderingValueOf(theString);
			if (theValue > theMaxValue) {
				theMaxValue = theValue;
				theMaxString.clear();
				theMaxString.add(theString);
			}
			if (theValue == theMaxValue) {
				theMaxString.add(theString);
			}
		}
		LOMValueHashSet theSet = getSet().getAllCouplesWithValues(theMaxString);
		theSet.addAll(((LOMValueSet) aLOMValue2).getSet()
				.getAllCouplesWithValues(theMaxString));

		return new LOMValueSet(((LOMValueSet) aLOMValue2).getSet(),
				getLOMAttribute() != null ? getLOMAttribute() : aLOMValue2
						.getLOMAttribute());
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#union(lessonMapper.lom.LOMValue)
	 */
	public LOMValue union(MetadataSetValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueSet))
			return this;
		return new LOMValueSet(this).addValue(((LOMValueSet) aLOMValue2));

	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#intersection(lessonMapper.lom.LOMValue)
	 */
	public LOMValue intersection(MetadataSetValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueSet))
			return this;
		LOMValueSet theValueSet = ((LOMValueSet) aLOMValue2);
		LOMValueSet theResult = new LOMValueSet(
				getLOMAttribute() != null ? getLOMAttribute() : theValueSet
						.getLOMAttribute());
		List<String> theList = getSet().getAllValues();
		
		List<String> theList2 = theValueSet.getSet()
				.getAllValues();
		for (String theString : theList) {
			if (theList2.contains(theString)) {
				theResult.getSet().addAll(
						getSet().getAllCouplesWithValue(theString));
				theResult.getSet().addAll(
						theValueSet.getSet()
								.getAllCouplesWithValue(theString));
			}
		}
		return theResult;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#sum(lessonMapper.lom.LOMValue)
	 */
	public LOMValue sum(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueSet))
			return this;
		System.out.println("sum not yet defined for LOMValueSet");
		return this;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#division(lessonMapper.lom.LOMValue)
	 */
	public LOMValue division(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueSet))
			return this;
		System.out.println("division not yet defined for LOMValueSet");
		return this;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#product(lessonMapper.lom.LOMValue)
	 */
	public LOMValue product(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueSet))
			return this;
		System.out.println("product not yet defined for LOMValueSet");
		return this;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#subtraction(lessonMapper.lom.LOMValue)
	 */
	public LOMValue subtraction(LOMValue aLOMValue2) {
		// delas wth set substraction
		if (!(aLOMValue2 instanceof LOMValueSet))
			return this;
		LOMValueSet theResult = new LOMValueSet(
				getLOMAttribute() != null ? getLOMAttribute() : aLOMValue2
						.getLOMAttribute());
		for (Couple<String, List<Couple<LOM, LOMRelationType>>> theCouple : getSet()) {
			if (!((LOMValueSet) aLOMValue2).getSet().contains(theCouple))
				theResult.addValue(theCouple);
		}
		return theResult;
	}

	/**
	 * 
	 * 
	 * @param aLOMValue2 
	 * 
	 * @return 
	 */
	public boolean inferior(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueSet))
			return false;
		if (getLOMVocabulary().isEmpty()) {
			System.out
					.println("inferior not defined for LOMValueSet without vocabulary");
			return false;
		}
		boolean isInferior = true;
		for (String theComparedValue : ((LOMValueSet) aLOMValue2).getSet()
				.getAllValues()) {
			int theComparedValueRank = getLOMVocabulary().getOrderingValueOf(
					theComparedValue);
			for (String theLocalValue : getSet().getAllValues()) {
				int theLocalValueRank = getLOMVocabulary().getOrderingValueOf(
						theLocalValue);
				isInferior &= theLocalValueRank < theComparedValueRank;
			}
		}
		return isInferior;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#inferiorOrEqual(lessonMapper.lom.LOMValue)
	 */
	public boolean inferiorOrEqual(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueSet))
			return false;
		if (getLOMVocabulary().isEmpty()) {
			System.out
					.println("inferiorOrEqual not defined for LOMValueSet without vocabulary");
			return false;
		}
		boolean isInferior = true;
		for (String theComparedValue : ((LOMValueSet) aLOMValue2).getSet()
				.getAllValues()) {
			int theComparedValueRank = getLOMVocabulary().getOrderingValueOf(
					theComparedValue);
			for (String theLocalValue : getSet().getAllValues()) {
				int theLocalValueRank = getLOMVocabulary().getOrderingValueOf(
						theLocalValue);
				isInferior &= theLocalValueRank <= theComparedValueRank;
			}
		}
		return isInferior;
	}

	/**
	 * 
	 * 
	 * @param aLOMValue2 
	 * 
	 * @return 
	 */
	public boolean superior(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueSet))
			return false;
		if (getLOMVocabulary().isEmpty()) {
			System.out
					.println("superior not defined for LOMValueSet without vocabulary");
			return false;
		}
		boolean isSuperior = true;
		for (String theComparedValue : ((LOMValueSet) aLOMValue2).getSet()
				.getAllValues()) {
			int theComparedValueRank = getLOMVocabulary().getOrderingValueOf(
					theComparedValue);
			for (String theLocalValue : getSet().getAllValues()) {
				int theLocalValueRank = getLOMVocabulary().getOrderingValueOf(
						theLocalValue);
				isSuperior &= theLocalValueRank > theComparedValueRank;
			}
		}
		return isSuperior;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#superiorOrEqual(lessonMapper.lom.LOMValue)
	 */
	public boolean superiorOrEqual(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueSet))
			return false;
		if (getLOMVocabulary().isEmpty()) {
			System.out
					.println("superior not defined for LOMValueSet without vocabulary");
			return false;
		}
		boolean isSuperior = true;
		for (String theComparedValue : ((LOMValueSet) aLOMValue2).getSet()
				.getAllValues()) {
			int theComparedValueRank = getLOMVocabulary().getOrderingValueOf(
					theComparedValue);
			for (String theLocalValue : getSet().getAllValues()) {
				int theLocalValueRank = getLOMVocabulary().getOrderingValueOf(
						theLocalValue);
				isSuperior &= theLocalValueRank >= theComparedValueRank;
			}
		}
		return isSuperior;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#dif(lessonMapper.lom.LOMValue)
	 */
	public boolean dif(LOMValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueSet))
			return false;
		return !equals(((LOMValueSet) aLOMValue2));

	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#contains(lessonMapper.lom.LOMValue)
	 */
	public boolean contains(MetadataSetValue aLOMValue2) {
		if (!(aLOMValue2 instanceof LOMValueSet))
			return false;
		boolean theResult = true;
		for (Couple<String, List<Couple<LOM, LOMRelationType>>> theCouple : ((LOMValueSet) aLOMValue2)
				.getSet()) {
			theResult = theResult && getSet().contains(theCouple);
		}
		return theResult;

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
		if (!(o instanceof LOMValueSet))
			return false;
		return getSet().equals(((LOMValueSet) o).getSet());
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public LOMValueHashSet getSet() {
		return itsValueSet;
	}

	/**
	 * return the stae of the value set or if it contains only the "" string.
	 * 
	 * @return 
	 */
	public boolean isEmpty() {
		if (getSet().isEmpty())
			return true;
		for (String theValue : getSet().getAllValues()) {
			if (!theValue.equals(""))
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#clone()
	 */
	@Override
	public LOMValueSet clone() {
		return new LOMValueSet(this);
	}

	/* (non-Javadoc)
	 * @see lessonMapper.lom.LOMValue#getLabel()
	 */
	@Override
	public String getLabel() {
		String theLabel = null;
		if (itsLOMAttribute==null || itsLOMAttribute.getLOMVocabulary().isEmpty()) {
			for (String theValue : getSet().getAllValues()) {
				if (theLabel == null)
					theLabel = new String(theValue);
				else
					theLabel += ", " + theValue;
			}
		} else {
			LOMVocabulary theVocabulary = itsLOMAttribute.getLOMVocabulary();
			for (String theValue : getSet().getAllValues()) {
				if (theLabel == null)
					theLabel = new String(theVocabulary.getVocabularyElement(
							theValue).toString());
				else
					theLabel += ", "
							+ theVocabulary.getVocabularyElement(theValue)
									.toString();
			}
		}

		return theLabel;
	}

	@Override
	public int hashCode() {
		int theHashCode = 0;
		for (String theString : getBasicStemmedTerms()) {
			theHashCode+=theString.hashCode();
		}
		return theHashCode;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "LOMValueSet{" + itsValueSet + "}";
	}

	/**
	 * add a new lom at the end of all the lists of lom. create a new list for
	 * the couple in order to avoid side effect
	 * 
	 * @param aLOM 
	 * @param aRelationType 
	 */
	public void addNewLOMtoValues(LOM aLOM, LOMRelationType aRelationType) {
		getSet().addNewLOMtoValues(aLOM, aRelationType);
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
		if (! (aValue instanceof LOMValueSet)) return 0;
		LOMValueSet theOtherValue = (LOMValueSet) aValue;
		List<String> theStrings = getBasicStemmedTerms();
		List<String> theOtherStrings=theOtherValue.getBasicStemmedTerms();
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
		if (! (aValue instanceof LOMValueSet)) return 0;
		LOMValueSet theOtherValue = (LOMValueSet) aValue;
		List<String> theStrings = getBasicStemmedTerms();
		List<String> theOtherStrings=theOtherValue.getBasicStemmedTerms();
		return Utils.listSimilarityProba(theStrings, theOtherStrings); 
	}
	
	
	
	/**
	 * return a list of stemmed terms not inherited be any suggestion.
	 * 
	 * @return 
	 */
	public List<String> getBasicStemmedTerms() {
		List<String> theStrings = new ArrayList<String>();
		for ( Couple<String, List<Couple<LOM, LOMRelationType>>> theValue :itsValueSet){
			if ( theValue.getRightElement()==null || theValue.getRightElement().isEmpty()){
				StringTokenizer theStringTokenizer = new StringTokenizer(
						theValue.getLeftElement(), LOM.ITSTokenizerLimits );
				for (String theToken; theStringTokenizer
						.hasMoreTokens();) {
					theToken= theStringTokenizer.nextToken();
					theToken = SnowballProgram.stem(theToken,ITSStemingLang);
					if (!theStrings.contains(theToken))
						theStrings.add(theToken);
				}
			}
		}
		return theStrings;
	}

	
	/**
	 * return a element based on the values of aLOMValue.
	 * 
	 * @param aLOMValue 
	 * 
	 * @return 
	 */
	public static Element makeXMLElement(LOMValueSet aLOMValue) {
		Element theElement = new Element("LOMValueSet");
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
		return new LOMValueSet(theSet, theAttribute);
	}

	/**
	 * 
	 */
	class LOMValueHashSet extends
			Vector<Couple<String, List<Couple<LOM, LOMRelationType>>>> {

		// make copy of couples to avoid side-effect

		@Override
		public synchronized String toString() {
			String thePrint = "[";
			boolean isFirst = true;
			for (Couple<String, List<Couple<LOM, LOMRelationType>>> theCouple : this) {
				String theLeftElement = theCouple.getLeftElement();
				if (theLeftElement.trim().equals("")) thePrint+= (!isFirst?", ":"")+"EmptyString" ;
				else thePrint+= (!isFirst?", ":"")+theLeftElement ;
				isFirst=false;
			}
			thePrint+="]";
			
			return thePrint;
		}
		
		/**
		 * 
		 * 
		 * @param c 
		 * 
		 * @return 
		 */
		public synchronized boolean addAll(
				Collection<? extends Couple<String, List<Couple<LOM, LOMRelationType>>>> c) {
			boolean theBool = false;
			for (Couple<String, List<Couple<LOM, LOMRelationType>>> theCouple : c) {
				theBool |= add(theCouple);
			}
			return theBool;
		}

		/**
		 * 
		 * 
		 * @param aCouple 
		 * 
		 * @return 
		 */
		@Override
		/**
		 * WARNING this method should avoid side-effect associated with couple
		 * Thus, copies of the parameters are added instead of references
		 * (except for the String which remains a reference
		 */
		public synchronized boolean add(
				Couple<String, List<Couple<LOM, LOMRelationType>>> aCouple) {
			List<Couple<LOM, LOMRelationType>> theListCopy = new ArrayList<Couple<LOM, LOMRelationType>>();
			theListCopy.addAll(aCouple.getRightElement());
			Couple<String, List<Couple<LOM, LOMRelationType>>> theCopy = new Couple<String, List<Couple<LOM, LOMRelationType>>>(
					aCouple.getLeftElement(), theListCopy);
			return super.add(theCopy);
		}

		/**
		 * add a new value (string) to the set.
		 * 
		 * @param aString 
		 * 
		 * @return 
		 */
		public synchronized boolean add(String aString) {
			Couple<String, List<Couple<LOM, LOMRelationType>>> theCouple = new Couple<String, List<Couple<LOM, LOMRelationType>>>(
					aString, new ArrayList<Couple<LOM, LOMRelationType>>());
			return super.add(theCouple);
		}

		/**
		 * WARNING this version of contains is based on the comparison of the
		 * string not on the comparison of the list of Couple(LOM, RelationType).
		 * 
		 * @param aElem 
		 * 
		 * @return 
		 */
		@Override
		public synchronized boolean contains(Object aElem) {
			if (aElem instanceof Couple)
				for (Couple<String, List<Couple<LOM, LOMRelationType>>> theCouple : this) {
					Object theString2 = ((Couple) aElem).getLeftElement();
					if (theString2 instanceof String) {
						String theComparedString = ((String) theString2).toLowerCase().trim();
						StringTokenizer theStringTokenizer = new StringTokenizer(
								theCouple.getLeftElement().toLowerCase().trim(), LOM.ITSTokenizerLimits );
						for (String theToken; theStringTokenizer
								.hasMoreTokens();) {
							theToken = theStringTokenizer.nextToken();
							if (SnowballProgram.contains(theToken, theComparedString, ITSStemingLang)) 
								return true;
						}
					}
				}
			return false;
		}

		/**
		 * 
		 * 
		 * @return 
		 */
		public synchronized List<String> getAllValues() {
			List<String> theList = new ArrayList<String>();
			for (Couple<String, List<Couple<LOM, LOMRelationType>>> theCouple : this) {
				if (!theList.contains(theCouple.getLeftElement())) {
					theList
							.add(theCouple.getLeftElement()
									.trim());
					//removed toLowercase
				}
			}
			return theList;
		}

		/**
		 * 
		 * 
		 * @param aValue 
		 * 
		 * @return 
		 */
		public synchronized LOMValueHashSet getAllCouplesWithValue(String aValue) {
			List<String> theList = new ArrayList<String>();
			theList.add(aValue);
			return getAllCouplesWithValues(theList);
		}

		/**
		 * 
		 * 
		 * @param values 
		 * 
		 * @return 
		 */
		public synchronized LOMValueHashSet getAllCouplesWithValues(
				List<String> values) {
			LOMValueHashSet theSet = new LOMValueHashSet();
			for (Couple<String, List<Couple<LOM, LOMRelationType>>> theCouple : this)
				for (String theValue : values)
					if (theCouple.getLeftElement().equals(theValue))
						theSet.add(theCouple);
			return theSet;
		}

		/**
		 * add a new lom at the end of all the lists of lom. create a new list
		 * for the couple in order to avoid side effect
		 * 
		 * @param aLOM 
		 * @param aType 
		 */
		public synchronized void addNewLOMtoValues(LOM aLOM,
				LOMRelationType aType) {
			if (aLOM != null) {
				for (Couple<String, List<Couple<LOM, LOMRelationType>>> theCouple : this) {
					ArrayList<Couple<LOM, LOMRelationType>> theList = new ArrayList<Couple<LOM, LOMRelationType>>();
					theList.addAll(theCouple.getRightElement());
					theList.add(new Couple<LOM, LOMRelationType>(aLOM, aType));
					theCouple.setRightElement(theList);
				}
			}
		}

		/**
		 * 
		 * 
		 * @param aO 
		 * 
		 * @return 
		 */
		@Override
		public synchronized boolean equals(Object aO) {
			if (!(aO instanceof LOMValueHashSet) ) return false;
			LOMValueHashSet theSet = (LOMValueHashSet) aO;
			for (Couple<String, List<Couple<LOM, LOMRelationType>>> theCouple : this) {
				if (!theSet.contains(theCouple)) return false;
			} 
			for (Couple<String, List<Couple<LOM, LOMRelationType>>> theCouple : theSet) {
				if (!contains(theCouple)) return false;
			} 
			return true;
		}
		
		
	
		
	}

}
