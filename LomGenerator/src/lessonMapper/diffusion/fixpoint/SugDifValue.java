/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.diffusion.fixpoint;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMRelationType;
import lessonMapper.lom.LOMValue;
import lessonMapper.lom.LOMValueSet;
import lessonMapper.lom.LOMValueVocabularySet;
import lessonMapper.lom.diffuse.LOMSuggestionProbability;

import org.tartarus.snowball.SnowballProgram;

/**
 * SugValue encapsulates a map of string, value (between 0 an 1).
 * 
 * @author omotelet
 */

public class SugDifValue extends AttributeBasedDifValue {

	/**
	 * used when comparing two sugvalue.
	 */
	static double ITSPrecision = 0.001;
	
	/**
	 * 
	 */
	static double ITSMin = 0.001;

	/**
	 * 
	 */
	boolean isUpdatedDiffusionValue = false,isUpdatedCompiledValue = false;
	
	/**
	 * 
	 */
	SugValue itsDiffusionValue,itsCompiledValue;

	/**
	 * 
	 */
	List<String> itsLOMValuesAsList;
	
	/**
	 * 
	 */
	Map<LOMRelationType, Map<LOM, SugValue>> itsRelatedValues = new HashMap<LOMRelationType, Map<LOM,SugValue>>();
	

	
	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 */
	public SugDifValue(LOMAttribute aAttribute, LOM aLOM) {
		super( aAttribute,aLOM);
	    //init CompiledValue
		initUpdate();
	}
	
	
	/**
	 * 
	 * 
	 * @param aSugDifValueToClone 
	 */
	public SugDifValue(SugDifValue aSugDifValueToClone) {
		super(aSugDifValueToClone.itsAttribute,aSugDifValueToClone.itsLOM);
		for (Entry<LOMRelationType, Map<LOM,SugValue>> theEntry : aSugDifValueToClone.itsRelatedValues.entrySet()) {
			itsRelatedValues.put(theEntry.getKey(),new HashMap<LOM, SugValue>(theEntry.getValue()));
		}
		//init CompiledValue
		initUpdate();
	}

	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.fixpoint.DifValue#initUpdate()
	 */
	@Override
	public void initUpdate() {
		isUpdatedLOMValue=false;
		isUpdatedDiffusionValue=false;
		isUpdatedCompiledValue=false ;
		//itsRelatedValues.clear();
		getInternDiffusionValue();
	}
	
	/**
	 * return the LOMValue for the associated lom as a list of tokenized strings.
	 * 
	 * @return 
	 */
	public List<String> getLOMValuesAsList() {
		if (!isUpdatedLOMValue) {
			List<String> theLOMValues = new ArrayList<String>();
			LOMValue theLOMValue = getLOMValue();
			if (theLOMValue != null) {
				if (theLOMValue instanceof LOMValueSet) {
					List<String> theList = ((LOMValueSet) theLOMValue)
							.getValueList();
					for (String theString : theList) {
						StringTokenizer theStringTokenizer = new StringTokenizer(
								theString.toLowerCase(), LOM.ITSTokenizerLimits );
						while (theStringTokenizer.hasMoreElements()) {
							String s = ((String) theStringTokenizer
									.nextElement()).trim();
							if (!s.equals(""))
								theLOMValues.add(s);
						}
					}
				} else if (theLOMValue instanceof LOMValueVocabularySet){
					theLOMValues.addAll(((LOMValueVocabularySet) theLOMValue)
							.getValueList());
				}
				else theLOMValues.add(theLOMValue.getValue());
			}
			itsLOMValuesAsList = theLOMValues;
		}
		return itsLOMValuesAsList;
	}

	
	
	/**
	 * add all aValue to  this
	 * return true if this operation made a change in this
	 * else return false.
	 * 
	 * @param aLOM 
	 * @param aValue 
	 * @param aRelationType 
	 * 
	 * @return 
	 */
	@Override
	public boolean addDiffusion(LOMRelationType aRelationType,LOM aLOM, Value aValue) {
		if (!(aValue instanceof SugValue))
			return false;
		SugValue theSugValue = (SugValue) aValue;
		Map<LOM, SugValue> theMap = itsRelatedValues.get(aRelationType);
		if (theMap==null) {
			theMap = new HashMap<LOM, SugValue>();
			itsRelatedValues.put(aRelationType,theMap);
		}
		SugValue theExSugValue = theMap.put(aLOM,theSugValue);
		if ( theExSugValue == null || ! theExSugValue.equals(theSugValue)){
			//return true;
			SugValue theExCompiledValue = getInternDiffusionValue();
			isUpdatedDiffusionValue = false;
			isUpdatedCompiledValue = false;
			return (! getInternDiffusionValue().equals(theExCompiledValue));
			//return true;
		}
		return false;
	}

	/**
	 * return a new SugValue with values multiplied by the probas.
	 * 
	 * @return 
	 */
	@Override
	public Value getDiffusion() {
		return getInternDiffusionValue();
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	protected SugValue getInternDiffusionValue() {
		if (isUpdatedDiffusionValue) return itsDiffusionValue;
		itsDiffusionValue = new SugValue();
		for (LOMRelationType theType : itsRelatedValues.keySet()) {
			try {
				double theProba = LOMSuggestionProbability.getLOMInstance().getProbaFor(
					itsAttribute, theType).doubleValue();
				for (SugValue theRelatedSug : itsRelatedValues.get(theType).values())
					itsDiffusionValue.addAll(theRelatedSug,theProba);
			
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		for (String theString : getLOMValuesAsList())
			itsDiffusionValue.add(theString, 1);
		isUpdatedDiffusionValue = true;
		return itsDiffusionValue;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.fixpoint.DifValue#getValue()
	 */
	@Override
	public Value getValue() {
		return getInternCompiledValue();
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	protected SugValue getInternCompiledValue() {
		if (isUpdatedCompiledValue) return itsCompiledValue;
		itsCompiledValue = new SugValue();
		itsCompiledValue.addAll(getInternDiffusionValue(), 1);
		for (String theString : getLOMValuesAsList())
			itsCompiledValue.remove(theString);
		isUpdatedCompiledValue = true;
		return itsCompiledValue;
	}

	
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String theString = "Compiled= " +getInternDiffusionValue().toString()+"****** Related= "+itsRelatedValues;
		return theString;
	}
	

	/**
	 * return a map of string bigdecimal corresponding to the suggestion without
	 * considering the actiual LOMValue.
	 * 
	 * @return 
	 */
	public Map<String, BigDecimal> getSuggestionListAsStemmedBigDecimal() {
		Map<String, BigDecimal> theList = new LinkedHashMap<String, BigDecimal>();

		for (Entry<String, Double> theEntry : getInternDiffusionValue().getEntrySet()) {
			String theString = SnowballProgram.stem(theEntry.getKey(), "es")
					.toLowerCase();
			if (!theList.containsKey(theString)
					|| theList.get(theString).doubleValue() < theEntry
							.getValue())
				theList.put(theString, new BigDecimal(theEntry.getValue()));
		}
		for (String theString : getLOMValuesAsList()) {
			theString = SnowballProgram.stem(theString, "es").toLowerCase();
			theList.put(theString, BigDecimal.ONE);
		}
		return theList;
	}

	/**
	 * return a map of string bigdecimal corresponding to the suggestion without
	 * considering the actiual LOMValue.
	 * 
	 * @return 
	 */
	public Map<String, BigDecimal> getSuggestionListAsBigDecimal() {
		Map<String, BigDecimal> theList = new LinkedHashMap<String, BigDecimal>();
		for (Entry<String, Double> theEntry : getInternDiffusionValue().getEntrySet()) {
			theList.put(theEntry.getKey(), new BigDecimal(theEntry.getValue()));
		}
		return theList;
	}

	
	/**
	 * 
	 */
	public class SugValue implements Value{
		
		/**
		 * 
		 */
		Map<String, Double> itsSugValues = new HashMap<String, Double>();
		
		
		/**
		 * 
		 * 
		 * @param aString 
		 * 
		 * @return 
		 */
		public double getValueFor(String aString) {
			Double thePrecisionDouble = itsSugValues.get(aString);
			if (thePrecisionDouble != null)
				return thePrecisionDouble;
			return 0;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return itsSugValues.toString();
		}
		
		
		
		
		/**
		 * add all the element of aSugValue multiplicated by aFactor
		 * in this
		 * if (there are different or higher than the existing).
		 * 
		 * @param aFactor 
		 * @param aSugValue 
		 */
		public void addAll(SugValue aSugValue, double aFactor) {
			for (Entry<String, Double> theEntry : aSugValue.itsSugValues.entrySet()) {
				String theString = theEntry.getKey();
				double theNewValue = theEntry.getValue()*aFactor;
				add(theString, theNewValue);
			}
		}

		/**
		 * add theString if different or higher than the oldvalue.
		 * 
		 * @param theNewValue 
		 * @param theString 
		 */
		public void add(String theString, double theNewValue) {
			double theValue = getValueFor(theString);
			if ( !aproximatedEqual(theValue, theNewValue) && theValue < theNewValue){
				if (theNewValue<ITSMin)
					itsSugValues.remove(theString);
				else itsSugValues.put(theString, theNewValue);
			}
		}
		
		/**
		 * 
		 * 
		 * @param theString 
		 * 
		 * @return 
		 */
		public double remove(String theString){
			Double theDouble = itsSugValues.remove(theString);
			if (theDouble == null) return 0;
			return theDouble;
		}
		
		/**
		 * 
		 * 
		 * @param aDouble 
		 * @param anotherDouble 
		 * 
		 * @return 
		 */
		public boolean aproximatedEqual(double aDouble, double anotherDouble){
			return Math.abs(aDouble - anotherDouble) <= ITSPrecision;
		}
		
		/**
		 * 
		 * 
		 * @return 
		 */
		public Set<Entry<String, Double>> getEntrySet(){
			return itsSugValues.entrySet();
		}
		
		/**
		 * 
		 * 
		 * @return 
		 */
		public Map<String,Double> getMap(){
			return itsSugValues;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object aObj) {
			if (aObj != null && aObj instanceof SugValue) {
				SugValue theSug = (SugValue) aObj;
				Set<String> theStrings = new HashSet<String>();
				theStrings.addAll(itsSugValues.keySet());
				theStrings.addAll(theSug.itsSugValues.keySet());
				for (String theString : theStrings) {
					if (!aproximatedEqual(getValueFor(theString),theSug.getValueFor(theString))) 
						return false;
				}
				return true;
			}
			return false;
		}
		
	}
	
	
}
