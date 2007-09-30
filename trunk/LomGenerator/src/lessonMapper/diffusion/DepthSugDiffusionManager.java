/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMValue;



/**
 * allow a depth first execution of the diffusion process.
 * 
 * @author omotelet
 */
public class DepthSugDiffusionManager {

	/**
	 * 
	 */
	Stack<SugFunctionHandler> itsStack = new Stack<SugFunctionHandler>();
	
	/**
	 * 
	 */
	Queue<SugFunctionHandler> itsQueue = new LinkedList<SugFunctionHandler>();
	
	/**
	 * 
	 */
	SugDifHandler itsTopFunction ;
	
	/**
	 * 
	 */
	Set<LOM> itsVisitedLOMs ;
	
	/**
	 * 
	 * 
	 * @param aFunction 
	 */
	public DepthSugDiffusionManager(SugDifHandler aFunction) {
		itsTopFunction=aFunction;
		itsVisitedLOMs = aFunction.itsVisitedLOMs;
	}
	
	/**
	 * 
	 * 
	 * @return 
	 */
	public LOMValue executeDiffusion() {
		itsQueue.offer(itsTopFunction);
		//init all the functions in depth first
		while(!itsQueue.isEmpty()) {
			SugFunctionHandler theFunction = itsQueue.poll();
			List<SugFunctionHandler> theResultingFunctions = theFunction.init();
			for (SugFunctionHandler theResultingFunction : theResultingFunctions) {
				itsQueue.offer(theResultingFunction);
			}
			itsStack.push(theFunction);
		}
		//make the callback until there is only the topfunction in the stack
		while(itsStack.size()>1) {
			itsStack.pop().callback();
		}
		return itsTopFunction.getValue();
	}
	
	
	
}