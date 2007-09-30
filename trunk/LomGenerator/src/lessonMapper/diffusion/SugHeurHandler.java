/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMRelationType;
import lessonMapper.lom.LOMValue;

/**
 * 
 */
public class SugHeurHandler extends SugFunctionHandler {

	/**
	 * 
	 */
	Set<LOM> itsRelatedLOMSet;

	/**
	 * 
	 */
	Set<LOM> itsVisitedLOMs;

	/**
	 * 
	 */
	Map<LOM, LOMValue> itsValueCouples = new LinkedHashMap<LOM, LOMValue>();

	/**
	 * 
	 */
	LOMSuggestionHeuristic itsHeuristic;

	

	/**
	 * 
	 * 
	 * @param aRelatedLOMSet 
	 * @param aParentFunction 
	 * @param aVisitedLOMs 
	 * @param aT 
	 * @param aA 
	 */
	public SugHeurHandler(LOMAttribute aA, LOMRelationType aT,
			Set<LOM> aRelatedLOMSet, Set<LOM> aVisitedLOMs,
			SugDifHandler aParentFunction) {
		setParentFunction(aParentFunction);
		itsLOMAttribute = aA;
		itsRelationType = aT;
		itsRelatedLOMSet = aRelatedLOMSet;
		itsVisitedLOMs = aVisitedLOMs;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.SugFunctionHandler#init()
	 */
	@Override
	public List<SugFunctionHandler> init() {
		List<SugFunctionHandler> theResults = new ArrayList<SugFunctionHandler>();
		Set<Class> theHeuristics = HeuristicTable.getInstance()
				.getSuggestionHeuristic(itsLOMAttribute, itsRelationType);
		for (Class theSuggestion : theHeuristics) {
			try {
				itsHeuristic = (LOMSuggestionHeuristic) theSuggestion
						.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			// PhiSug
			for (LOM theLOM : itsRelatedLOMSet) {
				if (theLOM != null) {
					theResults.add(new SugDifHandler(theLOM, itsRelationType, itsLOMAttribute,
							itsVisitedLOMs, this));
					LOMValue theValue = Diffusion.AcqVal(theLOM,
							itsLOMAttribute);
					if (theValue != null){
						theValue.addNewLOMtoValues(theLOM, itsRelationType);
						itsValueCouples.put(theLOM, theValue);
						
					}
				}
			}
			// we consider there is only one suggestion per couple LOMAttribute,
			// LOMRelationType so we return at first iteration
			return theResults;
		}
		return theResults;
	}

	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aLOMValue 
	 */
	public void addValueFor(LOM aLOM, LOMValue aLOMValue) {
		if (aLOMValue != null)
			if (itsValueCouples.containsKey(aLOM)
					&& itsValueCouples.get(aLOM) != null)
				itsValueCouples.get(aLOM).addValue(aLOMValue);
			else
				itsValueCouples.put(aLOM, aLOMValue);
	}

	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.SugFunctionHandler#callback()
	 */
	@Override
	/**
	 * //calculate heuristic and update de parent
	 */
	public void callback() {
		if (itsHeuristic != null) {
			for (LOM li : itsValueCouples.keySet()) {
				LOMValue vi = itsValueCouples.get(li);
				itsHeuristic.process(li, vi);
			}
			getParentFunction().addValue(itsHeuristic.getSuggestionValue());
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "sugHeur("+itsRelationType+","+itsRelatedLOMSet+")";
	}

	/**
	 * 
	 * 
	 * @param itsParentFunction 
	 */
	public void setParentFunction(SugDifHandler itsParentFunction) {
		super.setParentFunction(itsParentFunction);
	}

	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.SugFunctionHandler#getParentFunction()
	 */
	public SugDifHandler getParentFunction() {
		return (SugDifHandler)super.getParentFunction();
	}

	
	
}