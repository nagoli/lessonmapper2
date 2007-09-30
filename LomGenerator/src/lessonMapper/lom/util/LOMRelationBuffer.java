package lessonMapper.lom.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMRelation;
import lessonMapper.lom.LOMRelationType;


import org.jdom.Element;

public class LOMRelationBuffer {

	
	
	protected  static LOMRelationBuffer ITSInstance = new LOMRelationBuffer();
	public static LOMRelationBuffer getInstance(){
		return ITSInstance;
	}
	
	
	
	/**
	 * 
	 */
	 Map<String, LOMRelation> itsLOMRelations = new HashMap<String, LOMRelation>();


	
	/**
	 * 
	 * 
	 * @param anID 
	 * 
	 * @return 
	 */
	 public LOMRelation getLOMRelation(String anID) {
		return itsLOMRelations.get(anID);
	}

	
	
	
	
	/**
	 * Keep a buffer of the relations for each LOMs
	 * it uses LinkedHashSet in order to keep determinism when iterating over the LOmRelationSet
	 */
	 Map<LOM, Set<LOMRelation>> ITSLOMRelationForLOMBuffer = new HashMap<LOM, Set<LOMRelation>>(); 
	
	/**
	 * keep a buffer of the relations type with the related LOMs for each LOM
	 */
	 Map<LOM,Map<LOMRelationType, Set<LOM>>> ITSLOMtoRelationTypeBuffer = new HashMap<LOM, Map<LOMRelationType, Set<LOM>>>();
	
	/**
	 * keep a buffer of the related type for each LOMs
	 */
	 Map<LOM,Set<LOM>> ITSLOMtoRelatedLOMBuffer = new HashMap<LOM,  Set<LOM>>();
	
	
	
	
	
	/**
	 * 
	 * 
	 * 
	 * @param aRelation 
	 */
	 public void registerLOMRelation(LOMRelation aRelation) {
		itsLOMRelations.put(aRelation.getID(), aRelation);
		LOM theLOM = aRelation.getSourceLOM();
		init(theLOM);
		//LOM theTargetLOM = aRelation.itsTargetLOM;
		
		//add in the relations for LOm buffer 
		Set<LOMRelation> theList = ITSLOMRelationForLOMBuffer.get(theLOM);
		if (theList == null){
			theList= new LinkedHashSet<LOMRelation>();
			ITSLOMRelationForLOMBuffer.put(theLOM,theList);
		}
		theList.add(aRelation);
		
		//rebuild the related LOM buffer
		rebuildRelatedLOMBufferFor(theLOM);
		
		//rebuild the relation type buffer
		rebuildLOMRelationTypeBufferFor(theLOM);
	}
	

		/**
		 * 
		 * 
		 * @param aRelation 
		 */
		 public void unregisterLOMRelation(LOMRelation aRelation) {
			itsLOMRelations.remove(aRelation.getID());
			LOM theLOM = aRelation.getSourceLOM();
			Set<LOMRelation> theList = ITSLOMRelationForLOMBuffer.get(theLOM);
			if (theList!=null) theList.remove(aRelation);
			//rebuild the related LOM buffer
			rebuildRelatedLOMBufferFor(theLOM);
			
			//rebuild the relation type buffer
			rebuildLOMRelationTypeBufferFor(theLOM);
		 
		 }

	/**
	 * init aLOM in the buffer
	 */
	 public void init(LOM aLOM){
		 if (ITSLOMRelationForLOMBuffer.containsKey(aLOM)) return;
		 //ITSLOMRelationForLOMBuffer.put(aLOM,new LinkedHashSet<LOMRelation>());
		 rebuildLOMRelationFromXMLLOMFor(aLOM);
	 }
	 
	 
	 
	 /**
		 * rebuild all the relation types for aLOM
		 * The table is entirely rebuilt each type a relation which source is aLOM is changed
		 * or a new relation is added or removed
		 */
		 public void rebuildRelatedLOMBufferFor(LOM aLOM){
			 Set<LOM> theSet = new LinkedHashSet<LOM>();
			 for (LOMRelation theRelation : ITSLOMRelationForLOMBuffer.get(aLOM)) {
				theSet.add(theRelation.getTargetLOM());
			}
			ITSLOMtoRelatedLOMBuffer.put(aLOM, theSet); 
		 }
		
	 
	 
	/**
	 * rebuild all the relation types for aLOM
	 * The table is entirely rebuilt each type a relation which source is aLOM is changed
	 * or a new relation is added or removed
	 */
	 public void rebuildLOMRelationTypeBufferFor(LOM aLOM){
		 if (aLOM == null ) {
				System.out.println("try to rebuild relations of a null LOM");
				return;
			}
		 init(aLOM);
		 Map<LOMRelationType, Set<LOM>> theMap = new LinkedHashMap<LOMRelationType, Set<LOM>>();
		 for (LOMRelation theRelation : ITSLOMRelationForLOMBuffer.get(aLOM)) {
			LOMRelationType theType = theRelation.getLOMRelationType();
			Set<LOM> theSet;
			if (!theMap.containsKey(theType)){
				theSet= new LinkedHashSet<LOM>();
				theMap.put(theType, theSet);
			}else theSet = theMap.get(theType);
			theSet.add(theRelation.getTargetLOM());
		}
		ITSLOMtoRelationTypeBuffer.put(aLOM, theMap); 
	 }
	
	 
	/**
	 * register the relations contained in aLOM ,
	 * 
	 * @param aLom
	 */ 
	public void rebuildLOMRelationFromXMLLOMFor(LOM aLom){
		if (aLom == null ) {
			System.out.println("try to rebuild relations of a null LOM");
			return;
		}
		LinkedHashSet<LOMRelation> theLinkedHashSet = new LinkedHashSet<LOMRelation>();
		ITSLOMRelationForLOMBuffer.put(aLom, theLinkedHashSet);
		List theRelationElements = aLom.getNodes(LOM.ITSNameSpacePrefix
				+ ":lom/" + LOM.ITSNameSpacePrefix + ":relation");
		for (int i = 0; i < theRelationElements.size(); i++) {
			Element theElement = (Element) theRelationElements.get(i);
			String theID = theElement.getChildTextTrim("ID", theElement
					.getNamespace());
			LOMRelation theRelation;
			
			String theValue = theElement.getChild("kind",
					theElement.getNamespace()).getChild("value",
					theElement.getNamespace()).getChildTextTrim("langstring",
					theElement.getNamespace());
			if (theValue == null || theValue.equals("")) {
		//		theElement.detach();// remove relation without value
				System.out.println("found but not removed a relation without value");
			}
			else {
				if (theID == null)
					theRelation = new LOMRelation(aLom, theElement);
				else {
					theRelation = getLOMRelation(theID);
					if (theRelation == null)
						theRelation = new LOMRelation(aLom, theElement);
				}
				theLinkedHashSet.add(theRelation);
			}
		}
	}
	
	/**
	 * 
	 * 
	 * @param aLom 
	 */
	public  void unregisterRelationsOf(LOM aLom) {
		for (LOMRelation theRelation : getRelationsIn(aLom)) 
			unregisterLOMRelation(theRelation);
	}
	
	
	public static void resetRelationBuffer(){ 
		getInstance().ITSLOMRelationForLOMBuffer.clear();
		getInstance().ITSLOMtoRelatedLOMBuffer.clear();
		getInstance().ITSLOMtoRelationTypeBuffer.clear();
	}

	
	
	
	/**
	 * return the map of LOMRelationType, relatedLOM of aLOM
	 */
	public static Map<LOMRelationType, Set<LOM>> getRelationTypeMapFor(LOM aLOM){
		getInstance().init(aLOM);
		Map<LOMRelationType,Set<LOM>> theMap =  getInstance().ITSLOMtoRelationTypeBuffer.get(aLOM);
		return theMap;
	}
	
	
	/**
	 * return the related LOMs for aLOM
	 */
	public static  Set<LOM> getRelatedLOMFor(LOM aLOM){
		getInstance().init(aLOM);
		return getInstance().ITSLOMtoRelatedLOMBuffer.get(aLOM);
	}
	
	
	
	/**
	 * return the set of LOMRelation of aLOM
	 * 
	 * @param aLom 
	 * 
	 * @return 
	 */
	public static Set<LOMRelation> getRelationsIn(LOM aLOM) {
		getInstance().init(aLOM);
		return getInstance().ITSLOMRelationForLOMBuffer.get(aLOM);
		
	}

	/**
	 * return the relation with ID anID contained in aLOM if anID is not found
	 * return null.
	 * 
	 * @param aLOM 
	 * @param anID 
	 * 
	 * @return 
	 */
	public  LOMRelation getRelation(String anID, LOM aLOM) {
		Set<LOMRelation> theLOMRelations = getRelationsIn(aLOM); 
		for (LOMRelation theLOMRelation : theLOMRelations) {
			if (anID != null && anID.equals(theLOMRelation.getID()))
				return theLOMRelation;
		}
		return null;
	}

	
	
	
	
}
