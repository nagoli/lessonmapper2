/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.diffusion;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.LinkedHashMap;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMRelation;
import lessonMapper.lom.LOMRelationType;
import lessonMapper.lom.LOMValue;
import lessonMapper.lom.util.LOMRelationBuffer;
import util.ListWithoutDouble;

/**
 * This class implements the heuristics diffusion framework <p/> <p/> User:
 * omotelet Date: Mar 14, 2005 Time: 12:19:07 PM.
 */
public class Diffusion {

	/**
	 * 
	 */
	public static final int ITSDiffusionLimit = 200;

	/**
	 * return the current value for this LOM.
	 * 
	 * @param aLom 
	 * @param anAttribute 
	 * 
	 * @return 
	 */
	public static LOMValue AcqVal(LOM aLom, LOMAttribute anAttribute) {
		// TODO check consitency of adding lom to the value
		LOMValue theValue = anAttribute.getValueIn(aLom);
		// theValue.addNewLOMtoValues(aLom);
		return theValue;
	}

	// Suggestion heuristic diffusion

	/**
	 * 
	 * 
	 * @param aLom 
	 * @param anAttribute 
	 * 
	 * @return 
	 */
	public static LOMValue SugVal(LOM aLom, LOMAttribute anAttribute) {
		return anAttribute.getSuggestionValueIn(aLom);
	}

	/**
	 * For basic issues of performance we should consider PhiSug: A x 2^L (for
	 * the related LOM) -> ... instead of PhiSug: L x A x R
	 * 
	 * @param aRelatedLOMSet 
	 * @param aVisitedLOMs 
	 * @param aA 
	 * 
	 * @return 
	 */
	private static Map<LOM, LOMValue> PhiSug(LOMAttribute aA,
			Set<LOM> aRelatedLOMSet, Set<LOM> aVisitedLOMs) {
		Map<LOM, LOMValue> theCouples = new LinkedHashMap<LOM, LOMValue>();
		for (LOM theLOM : aRelatedLOMSet) {
			if (theLOM != null) {
				LOMValue theValue = SugDif(theLOM, aA, aVisitedLOMs);
				if (theValue != null)
					theValue.addValue(AcqVal(theLOM, aA));
				else
					theValue = AcqVal(theLOM, aA);
				if (theValue != null)
					theCouples.put(theLOM, theValue);
			}
		}
		return theCouples;
	}

	/**
	 * 
	 * 
	 * @param aL 
	 * @param aA 
	 * 
	 * @return 
	 */
	public static LOMValue SugDif(LOM aL, LOMAttribute aA) {
		// return SugDif(aL, aA, new HashSet<LOM>());
		DepthSugDiffusionManager theDiffusion = new DepthSugDiffusionManager(
				new SugDifHandler(aL, null, aA, new LinkedHashSet<LOM>(), null));
		return theDiffusion.executeDiffusion();
	}

	/**
	 * 
	 * 
	 * @param aL 
	 * @param aVisitedLOMs 
	 * @param aA 
	 * 
	 * @return 
	 */
	private static LOMValue SugDif(LOM aL, LOMAttribute aA,
			Set<LOM> aVisitedLOMs) {
		LOMValue theResultValue = SugVal(aL, aA);
		// add the current LOM to the visited LOM list for avoiding cycles
		aVisitedLOMs.add(aL);
		Set<LOMRelation> theRelations = LOMRelationBuffer.getRelationsIn(aL);
		List<LOMRelationType> theRelationTypes = new ListWithoutDouble<LOMRelationType>();
		for (LOMRelation theRelation : theRelations) {
			theRelationTypes.add(theRelation.getLOMRelationType());
		}
		for (LOMRelationType theRelationType : theRelationTypes) {
			Set<LOM> theRelatedLOMs = new LinkedHashSet<LOM>();
			for (LOMRelation theLOMRelationToConsider : theRelations) {
				if (theLOMRelationToConsider.getLOMRelationType() == theRelationType) {
					LOM theLOM = LOM.getLOM(theLOMRelationToConsider
							.getTargetLOMId());
					// avoid cycles canceling the processing of a related LOMs
					// already visited
					if (theLOM != null && !aVisitedLOMs.contains(theLOM))
						theRelatedLOMs.add(theLOM);
				}
			}
			if (theResultValue != null)
				theResultValue.addValue(SugHeur(aA, theRelationType,
						theRelatedLOMs, aVisitedLOMs));
			else
				theResultValue = SugHeur(aA, theRelationType, theRelatedLOMs,
						aVisitedLOMs);
		}
		// aggregate a lom to all the inherited values
		// TODO depth first
		if (theResultValue != null)
			theResultValue.addNewLOMtoValues(aL, null);
		return theResultValue;
	}

	/**
	 * for basic issues of performance we should consider SugHeur: A x T x 2^L
	 * (for the related LOMSet) instead of SugHeur: A x T x L -> ...
	 * 
	 * @param aRelatedLOMSet 
	 * @param aVisitedLOMs 
	 * @param aT 
	 * @param aA 
	 * 
	 * @return 
	 */
	private static LOMValue SugHeur(LOMAttribute aA, LOMRelationType aT,
			Set<LOM> aRelatedLOMSet, Set<LOM> aVisitedLOMs) {
		LOMValue theResultValue = null;
		Map<LOM, LOMValue> thePhiCouples = null;
		Set<Class> theHeuristics = HeuristicTable.getInstance()
				.getSuggestionHeuristic(aA, aT);
		for (Class theSuggestion : theHeuristics) {
			LOMSuggestionHeuristic theHeuristic = null;
			try {
				theHeuristic = (LOMSuggestionHeuristic) theSuggestion
						.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			if (thePhiCouples == null)
				thePhiCouples = PhiSug(aA, aRelatedLOMSet, aVisitedLOMs);
			for (LOM li : thePhiCouples.keySet()) {
				LOMValue vi = thePhiCouples.get(li);
				theHeuristic.process(li, vi);
			}
			if (theResultValue == null)
				theResultValue = theHeuristic.getSuggestionValue();
			else
				theResultValue.addValue(theHeuristic.getSuggestionValue());
		}
		return theResultValue;
	}

	// Suggestion heuristic diffusion

	/**
	 * 
	 * 
	 * @param aLom 
	 * @param anAttribute 
	 * 
	 * @return 
	 */
	public static LOMRestrictionSet ResVal(LOM aLom, LOMAttribute anAttribute) {
		if (anAttribute == null)
			return null;
		return anAttribute.getBasicRestrictionSet(aLom);
	}

	/**
	 * For basic issues of performance we should consider PhiSug: A x 2^L (for
	 * the related LOM) -> ... instead of PhiSug: L x A x R
	 * 
	 * @param aRelatedLOMSet 
	 * @param aVisitedLOMs 
	 * @param aA 
	 * 
	 * @return 
	 */
	private static Map<LOM, LOMRestrictionSet> PhiRes(LOMAttribute aA,
			Set<LOM> aRelatedLOMSet, Set<LOM> aVisitedLOMs) {
		Map<LOM, LOMRestrictionSet> theCouples = new TreeMap<LOM, LOMRestrictionSet>();
		for (LOM theLOM : aRelatedLOMSet) {
			if (theLOM != null) {
				//LOMRestrictionSet theSet=null;//diffusion not active!
				LOMRestrictionSet theSet = ResDif(theLOM, aA, new LinkedHashSet<LOM>(
						aVisitedLOMs), false);
				if (theSet != null) {
					theSet.add(new LOMRestrictionValue(RestrictionOperator.EQ,
							AcqVal(theLOM, aA)));
				} else {
					theSet = new LOMRestrictionSet();
					theSet.add(new LOMRestrictionValue(RestrictionOperator.EQ,
							AcqVal(theLOM, aA)));
				}
				theCouples.put(theLOM, theSet);
			}
		}
		return theCouples;
	}

	/**
	 * 
	 * 
	 * @param aL 
	 * @param aA 
	 * 
	 * @return 
	 */
	public static LOMRestrictionSet ResDif(LOM aL, LOMAttribute aA) {
		Set<LOM> theLOMs = new TreeSet<LOM>();
		theLOMs.add(aL);
		try {
			return ResDif(aL, aA, theLOMs, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param aL 
	 * @param firstStep 
	 * @param aVisitedLOMs 
	 * @param aA 
	 * 
	 * @return 
	 */
	private static LOMRestrictionSet ResDif(LOM aL, LOMAttribute aA,
			Set<LOM> aVisitedLOMs, boolean firstStep) {
		if (aL == null)
			System.out.println("null LOM");
		LOMRestrictionSet theResultValue = ResVal(aL, aA);
		// add the current LOM to the visited LOM list for avoiding cycles
		// aVisitedLOMs.add(aL); // is added later
		if (firstStep) { //block diffusion if false
			Set<LOMRelation> theRelations = LOMRelationBuffer.getRelationsIn(aL);
			List<LOMRelationType> theRelationTypes = new ListWithoutDouble<LOMRelationType>();
			if (theRelations == null)
				System.out.println("null relations");
			else {
				for (LOMRelation theRelation : theRelations) {
					theRelationTypes.add(theRelation.getLOMRelationType());
				}
				for (LOMRelationType theRelationType : theRelationTypes) {
					Set<LOM> theRelatedLOMs = new LinkedHashSet<LOM>();
					for (LOMRelation theLOMRelationToConsider : theRelations) {
						if (theLOMRelationToConsider.getLOMRelationType() == theRelationType) {
							LOM theLOM = LOM.getLOM(theLOMRelationToConsider
									.getTargetLOMId());
							// avoid cycles canceling the processing of a related LOMs
							// already visited
							// limit the diffusion to ITSDiffusionLimit
							if (theLOM != null
									&& !aVisitedLOMs.contains(theLOM)
									&& aVisitedLOMs.size() < ITSDiffusionLimit) {
								theRelatedLOMs.add(theLOM);
								aVisitedLOMs.add(theLOM);
							}
						}
					}
					if (!theRelatedLOMs.isEmpty())
						if (theResultValue != null)
							theResultValue.concat(ResHeur(aA, theRelationType,
									theRelatedLOMs, aVisitedLOMs));
						else
							theResultValue = ResHeur(aA, theRelationType,
									theRelatedLOMs, aVisitedLOMs);
				}
			}
		}
		return theResultValue;
	}

	/**
	 * for basic issues of performance we should consider ResHeur: A x T x 2^L
	 * (for the related LOMSet) instead of ResHeur: A x T x L -> ...
	 * 
	 * @param aRelatedLOMSet 
	 * @param aVisitedLOMs 
	 * @param aT 
	 * @param aA 
	 * 
	 * @return 
	 */
	private static LOMRestrictionSet ResHeur(LOMAttribute aA,
			LOMRelationType aT, Set<LOM> aRelatedLOMSet, Set<LOM> aVisitedLOMs) {
		LOMRestrictionSet theResultRestrictions = new LOMRestrictionSet();
		Map<LOM, LOMRestrictionSet> thePhiCouples = null;
		Set<Class> theHeuristics = HeuristicTable.getInstance()
				.getRestrictionHeuristic(aA, aT);
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
			if (thePhiCouples == null)
				thePhiCouples = PhiRes(aA, aRelatedLOMSet, aVisitedLOMs);
			for (LOM li : thePhiCouples.keySet()) {
				LOMRestrictionSet theRestrictionSet = thePhiCouples.get(li);
				for (LOMRestrictionValue theRestrictionValue : theRestrictionSet
						.getLOMRestrictionValues()) {
					RestrictionOperator oij = theRestrictionValue.getOperator();
					LOMValue vij = theRestrictionValue.getValue();
					theHeuristic.process(li, oij, vij);
				}
			}
			theResultRestrictions.add(theHeuristic.getRestrictionValue());
		}
		return theResultRestrictions;
	}

	/**
	 * 
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		LOM theL1 = new LOM(LOM.class
				.getResource("resources/test/TestLOM1.xml"));
		LOM theL2 = new LOM(LOM.class
				.getResource("resources/test/TestLOM2.xml"));
		@SuppressWarnings("unused")
		LOM theL3 = new LOM(LOM.class
				.getResource("resources/test/TestLOM3.xml"));
		System.out.println(SugDif(theL2, LOMAttribute
				.getLOMAttribute("educational/context")));
		System.out.println(ResDif(theL2, LOMAttribute
				.getLOMAttribute("educational/context")));

	}

}
