/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion;

import java.util.ArrayList;
import java.util.List;

import lessonMapper.lom.LOMRelationType;

/**
 * this class is responsible for managing the limitation of the diffusion
 * In particular it implements the method
 * getForbiddenRelationTypes(aRelationTypeList) returning the
 * forbidden relations for a list.
 * 
 * @author omotelet
 */
public class DiffusionLimit {

	/**
	 * 
	 * 
	 * @param aRelationTypeList 
	 * 
	 * @return 
	 */
	public static  List<LOMRelationType>  getForbiddenRelationTypes(List<LOMRelationType> aRelationTypeList){
		List<LOMRelationType> theForbiddenTypes = new ArrayList<LOMRelationType>();
		
		/*if (aRelationTypeList.contains(LOMRelationType.getLOMRelationType("ispartof")))
			theForbiddenTypes.add(LOMRelationType.getLOMRelationType("haspart"));*/
		return theForbiddenTypes;
	}
	
	
}