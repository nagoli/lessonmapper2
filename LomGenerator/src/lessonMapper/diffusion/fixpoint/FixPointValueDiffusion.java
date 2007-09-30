/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.diffusion.fixpoint;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMRelation;
import lessonMapper.lom.util.LOMRelationBuffer;
import util.ListWithoutDouble;
import util.QueueWithoutRepetition;

/**
 * this class is responsible for the update of the SugValues
 * for now the diffusion values are reseted before any change
 * there is no dynamic change management
 * for sug value :
 * - value change could be go and come back i.e
 * diffuse until a change is not accepted + replace value form which origin is equivalent to the diffusion
 * then diffuse in other sense .
 * - edge removed:
 * remove value form source in target and reversely
 * then diffuse change for both nodes as explained before
 * 
 * -edge added:
 * diffuse changes for both target and source
 * 
 * @author omotelet
 */

public class FixPointValueDiffusion {

	/**
	 * 
	 */
	static public boolean IsActive = false;
	
	/**
	 * 
	 * 
	 * @param aBool 
	 */
	static public void setActive(boolean aBool){
		IsActive = aBool;
	}
	
//	
//	static public Map<LOMAttribute, DifValueHolder> AllDifHolders = new ArrayList<DifValueHolder>();
//	static {
//		AllDifHolders.add(SugDifValueHolder.getInstance());
//	}
//	
//	/**
//	 * diffuse the changes of aLOM for aAttribute 
//	 * for all the DifHolders registered in AllDifHolders
//	 * @param aLOM
//	 * @param aAttribute
//	 */
//	public static void diffuseChangesOf(LOM aLOM, LOMAttribute aAttribute){
//		diffuseChangesOf(aLOM,aAttribute,AllDifHolders);
//	}
//	
	/**
 * diffuse the changes of aLOM for aAttribute
 * for the DifValueHolders given as arguments.
 * 
 * @param aLOM 
 * @param aHolderList 
 * 
 * @return 
 */
	public static Set<LOM> diffuseChangesOf(LOM aLOM, 
			Collection<DifValueHolder> aHolderList){
		Set<LOM> theModifiedLOMs = new HashSet<LOM>();
		for (DifValueHolder theHolder : aHolderList) 
			theModifiedLOMs.addAll( diffuseChangesOf(aLOM, theHolder));
		return theModifiedLOMs;
	}
	
	/**
	 * diffuse the changes of aLOM for aAttribute
	 * for the DifValueHolder given as argument.
	 * 
	 *  the LOM and its related elements are added as updated LOMs 
	 *  -- related elements are added to the set of updated LOMs
	 *     in order to ensure that if the new value of aLOM can be influenced by its neighbors
	 *     this influence can occur.
	 * @param aHolder 
	 * @param aLOM 
	 * 
	 * @return 
	 */
	public static Set<LOM> diffuseChangesOf(LOM aLOM, 
	    DifValueHolder aHolder){
		List<LOM> theLOMsToModify= new ArrayList<LOM>();
		theLOMsToModify.add(aLOM);
		Set<LOM> theRelatedLOMs= LOM.getRelatedLOM(aLOM);
		if (theRelatedLOMs != null) theLOMsToModify.addAll(theRelatedLOMs); 
		  // add related element to ensure diffusion 
		Set<LOM> theModifiedLOMs = makeDiffusion( theLOMsToModify, aHolder);
		//makeDiffusionSet( theLOMsToModify, aHolder);
		//makeDiffusionRandomList( theLOMsToModify, aHolder);
		//makeDiffusionQueueAtEnd( theLOMsToModify, aHolder);
		//ITSComplexityLog.println();
		//System.out.println(aHolder.toString()+": " +count);
		return theModifiedLOMs;
	}
	
	
	/**
 * diffuse the changes of the LOMs of aLOMList for aAttribute
 * for the DifValueHolders given as arguments.
 * 
 * @param aHolderList 
 * @param aLOMList 
 * 
 * @return 
 */
	public static Set<LOM> diffuseChangesOf(Collection<LOM> aLOMList,
			Collection<DifValueHolder> aHolderList){
		Set<LOM> theModifiedLOMs = new HashSet<LOM>();
		for (DifValueHolder theHolder : aHolderList) 
			theModifiedLOMs.addAll(diffuseChangesOf(aLOMList, theHolder));
		return theModifiedLOMs;
	
	}
	
	/**
	 * diffuse the changes of the LOMs of aLOMList for aAttribute
	 * for the DifValueHolder given as argument.
	 * 
	 * @param aHolder 
	 * @param aLOMList 
	 * 
	 * @return 
	 */
	public static Set<LOM> diffuseChangesOf(Collection<LOM> aLOMList,
			DifValueHolder aHolder){
		
		Set<LOM> theModifiedLOMs = makeDiffusion( aLOMList, aHolder);
//		makeDiffusionSet( aLOMList, aHolder);
//		makeDiffusionRandomList( aLOMList , aHolder);
//		makeDiffusionQueueAtEnd( aLOMList, aHolder);
//		ITSComplexityLog.println();
		//System.out.println(aHolder.toString()+": " +count);
		return theModifiedLOMs;
	}
	
	
	/**
	 * 
	 * 
	 * @param aHolder 
	 * @param aListOfChangedLOMs 
	 */
	static void makeDiffusionList( QueueWithoutRepetition<LOM> aListOfChangedLOMs,DifValueHolder aHolder){
		int count =0;
		Random theRandom = new Random();
		ListWithoutDouble<LOM> theLOMs = new ListWithoutDouble<LOM>();
		theLOMs.addAll(aListOfChangedLOMs);
		aHolder.initUpdate( aListOfChangedLOMs);
		while(!theLOMs.isEmpty()){
			LOM theLOM = theLOMs.remove(theRandom.nextInt(theLOMs.size()));
			for (LOM theLom2 : diffusionTurn(theLOM,aHolder)) 
				theLOMs.add(theLom2);
			count++;
			if (count%100 ==0) System.out.println(count+"("+theLOMs.size()+")."); 
		}
	}
	
	/**
	 * 
	 */
	static Date ITSDate = new Date(System.currentTimeMillis());

	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	static String ITSDateString = "" + ITSDate.getMonth() + "_"
			+ ITSDate.getDate() + "_" + ITSDate.getHours() + "_"
			+ ITSDate.getMinutes() + "_";

	/**
	 * 
	 */
	static PrintStream ITSComplexityLog;
	static {
		try {
			ITSComplexityLog = new PrintStream(
					"/USERDISK/these/LMProjects/tests/logs/"
							+ ITSDateString
							+ "DiffusionComplexity.log");
		} catch (Exception e) {
			System.out.println("--------Diffusion Complexity file not created");
		}
	}
	
	static int count =0;
	static long totaltime =0;
	
	
	/**
	 * queue without forcing offerAtEnd is
	 * better than queue with offer
	 * better than random
	 * better than set with get first
	 * 
	 * algo is O(n *n * m ) where m is the max number of related lom for 1 lom
	 * it may be proved to be in O(n*m).
	 * 
	 * @param aHolder 
	 * @param aListOfChangedLOMs 
	 * 
	 * @return 
	 */
	 static Set<LOM> makeDiffusion( Collection<LOM> aListOfChangedLOMs,DifValueHolder aHolder){
		 long timestamp = System.currentTimeMillis();
		 QueueWithoutRepetition<LOM> theLOMsToModify= new QueueWithoutRepetition<LOM>();
		for (LOM theLom : aListOfChangedLOMs) 
				theLOMsToModify.offer(theLom);
		 Set<LOM> theModifiedElements = new HashSet<LOM>();
		aHolder.initUpdate(aListOfChangedLOMs);
		while(!theLOMsToModify.isEmpty()){
			count++;
			LOM theLOM = theLOMsToModify.poll();
			List<LOM> theDiffusionTurn = diffusionTurn(theLOM,aHolder);
			for (LOM theModifiedLOM : theDiffusionTurn)
				theLOMsToModify.offer(theModifiedLOM);
			theModifiedElements.addAll(theDiffusionTurn);
			//System.out.print(theLOM +"--"+aHolder.getValue(theLOM).toString()+"||");
			//if (count%100 ==0) System.out.println(count+"("+aListOfChangedLOMs.size()+")."); 
		}
		aHolder.endUpdate();
		totaltime += System.currentTimeMillis() - timestamp;
		System.out.println("FixedDifTime="+totaltime);
		System.out.println("FixedDifCount="+count);
		//		long delta =  System.currentTimeMillis() - timestamp;
//		System.out.println(delta);
//		System.out.println("complexity=" + count/aHolder.size()  );
//		System.out.println("operation per second= " + (1000*count/delta));
//		//ITSComplexityLog.print(count*1.0/aHolder.size() + " ");
		return theModifiedElements;
	}

	 /**
 	 * 
 	 * 
 	 * @param aHolder 
 	 * @param aListOfChangedLOMs 
 	 */
 	static void makeDiffusionSet( Collection<LOM> aListOfChangedLOMs,DifValueHolder aHolder){
		 int count =0;
		 //double theSize = aListOfChangedLOMs.size();	
		 aHolder.initUpdate(aListOfChangedLOMs);
			Set<LOM> theSet = new HashSet<LOM>();
			theSet.addAll(aListOfChangedLOMs);
			while(!theSet.isEmpty()){
				count++;
				LOM theLOM = theSet.iterator().next();
				theSet.remove(theLOM);
				List<LOM> theDiffusionTurn = diffusionTurn(theLOM,aHolder);
				for (LOM theModifiedLOM : theDiffusionTurn)
					theSet.add(theModifiedLOM);
				//System.out.print(theLOM +"--"+aHolder.getValue(theLOM).toString()+"||");
				//if (count%100 ==0) System.out.println(count+"("+aListOfChangedLOMs.size()+")."); 
			}
			aHolder.endUpdate();
			ITSComplexityLog.print(count*1.0/aHolder.size() + " ");
		}
	 
	 
	 /**
 	 * 
 	 * 
 	 * @param aHolder 
 	 * @param aListOfChangedLOMs 
 	 */
 	static void makeDiffusionRandomList( Collection<LOM> aListOfChangedLOMs,DifValueHolder aHolder){
		 int count =0; 
		    aHolder.initUpdate(aListOfChangedLOMs);
			Random theRandom = new Random();
			ListWithoutDouble<LOM> theLOMs = new ListWithoutDouble<LOM>();
			theLOMs.addAll(aListOfChangedLOMs);
			while(!theLOMs.isEmpty()){
				count++;
				LOM theLOM = theLOMs.remove(theRandom.nextInt(theLOMs.size()));
				List<LOM> theDiffusionTurn = diffusionTurn(theLOM,aHolder);
				for (LOM theModifiedLOM : theDiffusionTurn)
					theLOMs.add(theModifiedLOM);
				//System.out.print(theLOM +"--"+aHolder.getValue(theLOM).toString()+"||");
				//if (count%100 ==0) System.out.println(count+"("+aListOfChangedLOMs.size()+")."); 
			}
			aHolder.endUpdate();
			ITSComplexityLog.print(count*1.0/aHolder.size() + " ");
	 }	
	 
	 /**
 	 * 
 	 * 
 	 * @param aHolder 
 	 * @param aListOfChangedLOMs 
 	 */
 	static void makeDiffusionQueueAtEnd( Collection<LOM> aListOfChangedLOMs,DifValueHolder aHolder){
		 int count =0;
		 QueueWithoutRepetition<LOM> theLOMsToModify= new QueueWithoutRepetition<LOM>();
			for (LOM theLom : aListOfChangedLOMs) 
					theLOMsToModify.offer(theLom);
		 aHolder.initUpdate(aListOfChangedLOMs);
		while(!theLOMsToModify.isEmpty()){
			count++;
			LOM theLOM = theLOMsToModify.poll();
			List<LOM> theDiffusionTurn = diffusionTurn(theLOM,aHolder);
			for (LOM theModifiedLOM : theDiffusionTurn)
				theLOMsToModify.offerAtEnd(theModifiedLOM);
			//System.out.print(theLOM +"--"+aHolder.getValue(theLOM).toString()+"||");
			//if (count%100 ==0) System.out.println(count+"("+aListOfChangedLOMs.size()+")."); 
		}
		aHolder.endUpdate();
		ITSComplexityLog.print(count*1.0/aHolder.size() + " ");
	 }	
	 
	 
	 
	
	 /**
 	 * 
 	 * 
 	 * @param aHolder 
 	 * @param aChangedLOM 
 	 * 
 	 * @return 
 	 */
 	static List<LOM> diffusionTurn(LOM aChangedLOM, DifValueHolder aHolder){
		Value theChangedValue = aHolder.getValue(aChangedLOM).getDiffusion();
		List<LOM> theModifiedLOMs = new ArrayList<LOM>();
		Set<LOMRelation> theRelations = LOMRelationBuffer.getRelationsIn(aChangedLOM);
		for (LOMRelation theRelation : theRelations) {
			LOM theRelatedLOM = LOM.getLOM(theRelation.getTargetLOMId());
			if (theRelatedLOM != null){
				DifValue theRelatedValue = aHolder.getValue(theRelatedLOM);
				boolean hasChanged = theRelatedValue.addDiffusion(theRelation.getLOMRelationType(),aChangedLOM,theChangedValue);
				if (hasChanged)
					theModifiedLOMs.add(theRelatedLOM); 
			}
		}
		return theModifiedLOMs;
	}
	
	
}
