/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
