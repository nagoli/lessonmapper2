/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.diffusion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMRelation;
import lessonMapper.lom.LOMRelationType;
import lessonMapper.lom.LOMValue;
import lessonMapper.lom.util.LOMRelationBuffer;
import util.ListWithoutDouble;

/**
 * 
 */
public class SugDifHandler extends SugFunctionHandler {

	/**
	 * 
	 */
	LOM itsLOM;

	/**
	 * 
	 */
	Set<LOM> itsVisitedLOMs;

	/**
	 * 
	 */
	LOMValue itsValue;

	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aParentFunction 
	 * @param aType 
	 * @param aVisitedLOMs 
	 * @param aA 
	 */
	public SugDifHandler(LOM aLOM, LOMRelationType aType, LOMAttribute aA,
			Set<LOM> aVisitedLOMs, SugFunctionHandler aParentFunction) {
		setParentFunction(aParentFunction);
		itsLOM = aLOM;
		itsLOMAttribute = aA;
		// to make a new copy of the relation avoid cycles but permit to go through the whole graph
		itsVisitedLOMs = new HashSet<LOM> (aVisitedLOMs);
		itsRelationType = aType;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.SugFunctionHandler#init()
	 */
	@Override
	public List<SugFunctionHandler> init() {
		List<SugFunctionHandler> theResults = new ArrayList<SugFunctionHandler>();
		addValue(Diffusion.SugVal(itsLOM, itsLOMAttribute));
		// add the current LOM to the visited LOM list for avoiding cycles
		itsVisitedLOMs.add(itsLOM);
		Set<LOMRelation> theRelations = LOMRelationBuffer.getRelationsIn(itsLOM);
		List<LOMRelationType> theRelationTypes = new ListWithoutDouble<LOMRelationType>();
		List<LOMRelationType> theForbiddenRelationTypes = DiffusionLimit
				.getForbiddenRelationTypes(getRelationTypeHistory(new ArrayList<LOMRelationType>()));
		for (LOMRelation theRelation : theRelations) {
			LOMRelationType theRelationType = theRelation.getLOMRelationType();
			if (!theForbiddenRelationTypes.contains(theRelationType)) {
				theRelationTypes.add(theRelationType);
			}
		}
		for (LOMRelationType theRelationType : theRelationTypes) {
			Set<LOM> theRelatedLOMs = new LinkedHashSet<LOM>();
			for (LOMRelation theLOMRelationToConsider : theRelations) {
				if (theLOMRelationToConsider.getLOMRelationType() == theRelationType) {
					LOM theLOM = LOM.getLOM(theLOMRelationToConsider
							.getTargetLOMId());
					// avoid cycles canceling the processing of a related LOMs
					// already visited
					// also limit the diffusion to ITSDiffusionLimit
					if (theLOM != null && !itsVisitedLOMs.contains(theLOM) && itsVisitedLOMs.size()<Diffusion.ITSDiffusionLimit) {
						itsVisitedLOMs.add(theLOM);
						theRelatedLOMs.add(theLOM);
					}
				}
			}
			if (!theRelatedLOMs.isEmpty())
				theResults.add(new SugHeurHandler(itsLOMAttribute,
						theRelationType, theRelatedLOMs, itsVisitedLOMs, this));
		}
		return theResults;
	}

	/**
	 * 
	 * 
	 * @param aValue 
	 */
	public void addValue(LOMValue aValue) {
		if (itsValue != null)
			itsValue.addValue(aValue);
		else
			itsValue = aValue;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.SugFunctionHandler#callback()
	 */
	@Override
	// update its parent
	public void callback() {
		if (getValue() != null)
			getValue().addNewLOMtoValues(itsLOM, itsRelationType);
		getParentFunction().addValueFor(itsLOM, getValue());
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public LOMValue getValue() {
		return itsValue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "sugDif(" + itsRelationType + "," + itsLOM + ")";
	}

	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.SugFunctionHandler#setParentFunction(lessonMapper.diffusion.SugFunctionHandler)
	 */
	public void setParentFunction(SugFunctionHandler itsParentFunction) {
		super.setParentFunction(itsParentFunction);
	}

	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.SugFunctionHandler#getParentFunction()
	 */
	public SugHeurHandler getParentFunction() {
		return (SugHeurHandler) super.getParentFunction();
	}

}
