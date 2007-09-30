/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.diffusion.fixpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lessonMapper.diffusion.HeuristicTable;
import lessonMapper.diffusion.LOMRestrictionHeuristic;
import lessonMapper.diffusion.LOMRestrictionSet;
import lessonMapper.diffusion.LOMRestrictionValue;
import lessonMapper.diffusion.RestrictionOperator;
import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMRelationType;
import lessonMapper.lom.LOMValue;

/**
 * 
 */
public class ResDifValue extends AttributeBasedDifValue{

	/**
	 * 
	 */
	boolean isUpdatedCompiledValue = false;
	
	/**
	 * 
	 */
	boolean isUpdatedDiffusionValue = false;
	
	/**
	 * 
	 */
	LOMRestrictionSet itsCompiledValue;
	
	/**
	 * 
	 */
	LOMRestrictionSet itsDiffusionValue;
	
	/**
	 * 
	 */
	Map<LOMRelationType, Map<LOM, LOMRestrictionSet>> itsRelatedValues = new HashMap<LOMRelationType, Map<LOM,LOMRestrictionSet>>();
	
	
	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 */
	public ResDifValue(LOMAttribute aAttribute, LOM aLOM) {
		super(aAttribute, aLOM);
		initUpdate();
	}

	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.fixpoint.DifValue#addDiffusion(lessonMapper.lom.LOMRelationType, lessonMapper.lom.LOM, lessonMapper.diffusion.fixpoint.Value)
	 */
	@Override
	public boolean addDiffusion(LOMRelationType aRelationType, LOM aLOM, Value aValue) {
		if (!(aValue instanceof LOMRestrictionSet))
			return false;
		LOMRestrictionSet theResSet = (LOMRestrictionSet) aValue;
		Map<LOM, LOMRestrictionSet> theMap = itsRelatedValues.get(aRelationType);
		if (theMap==null) {
			theMap = new HashMap<LOM, LOMRestrictionSet>();
			itsRelatedValues.put(aRelationType,theMap);
		}
		LOMRestrictionSet theExResValue = theMap.put(aLOM,theResSet);
		if ( (theResSet != null && theExResValue == null) || ! theExResValue.equals(theResSet)){
			//return true;
			//LOMRestrictionSet theExDiffusionValue = getInternDiffusionValue();
			isUpdatedCompiledValue = false;
			isUpdatedDiffusionValue = false;
			//return (! getInternDiffusionValue().equals(theExDiffusionValue));
			return true;
		}
		return false;
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
	protected LOMRestrictionSet getInternCompiledValue() {
		if (isUpdatedCompiledValue) return itsCompiledValue;
		itsCompiledValue = new LOMRestrictionSet();
		for (LOMRelationType theType : itsRelatedValues.keySet()) {
			Map<LOM, LOMRestrictionSet> thePhiCouples = itsRelatedValues.get(theType);
			Set<Class> theHeuristics = HeuristicTable.getInstance()
					.getRestrictionHeuristic(itsAttribute, theType.getContrary());
			for (Class theRestriction : theHeuristics) {
				LOMRestrictionHeuristic theHeuristic = null;
				try {
					theHeuristic = (LOMRestrictionHeuristic) theRestriction
							.newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				for (LOM li : thePhiCouples.keySet()) {
					LOMRestrictionSet theRestrictionSet = thePhiCouples.get(li);
					for (LOMRestrictionValue theRestrictionValue : theRestrictionSet
							.getLOMRestrictionValues()) {
						RestrictionOperator oij = theRestrictionValue.getOperator();
						LOMValue vij = theRestrictionValue.getValue();
						//System.out.println(theHeuristic.toString());
						theHeuristic.process(li, oij, vij);
					}
				}
				itsCompiledValue.add(theHeuristic.getRestrictionValue());
			}
		}
		isUpdatedCompiledValue = true;
		return itsCompiledValue;
	}

	
	
	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.fixpoint.DifValue#getDiffusion()
	 */
	@Override
	public Value getDiffusion() {
		return getInternDiffusionValue();
	}

	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.fixpoint.DifValue#initUpdate()
	 */
	@Override
	public void initUpdate() {
		isUpdatedLOMValue = false;
		isUpdatedCompiledValue=false;
		isUpdatedDiffusionValue = false;
		//itsRelatedValues.clear();
		getInternDiffusionValue();	
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	protected LOMRestrictionSet getInternDiffusionValue() {
		if (isUpdatedDiffusionValue) return itsDiffusionValue;
		itsDiffusionValue = new LOMRestrictionSet(getInternCompiledValue());
		itsDiffusionValue.add(new LOMRestrictionValue(RestrictionOperator.EQ,
					getLOMValue()));
		//itsCompiledValue.mergeOperators(); //TODO check if it is needed
		isUpdatedDiffusionValue = true;
		return itsDiffusionValue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return itsRelatedValues.toString();
	}
	
	
}
