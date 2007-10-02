/*
 * LessonMapper 2.
 */

package lessonMapper.lom.util;



import java.util.HashMap;
import java.util.Map;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;

import org.jdom.Element;



/**
 * this aspect traces the element created in LOMAttribute.getNodes(aLOM) so that
 * it may be possible to get the attribute and the modificated LOM associated
 * with this DOM Element
 */
public class LOMElementTracer {

	 /**
 	 * 
 	 */
 	static Map<Element, LOM > ITSElementToLOMMap = new HashMap<Element,LOM>();
	 
	 
	 /**
 	 * 
 	 */
 	static Map<Element, LOMAttribute> ITSElementToLOMAttributeMap = new HashMap<Element,LOMAttribute>();
 

	
	
	
	/**
	 * 
	 * 
	 * @param aElement 
	 * 
	 * @return 
	 */
	static public LOM getLOMFor(Element aElement){
		return ITSElementToLOMMap.get(aElement);
	}
	
	/**
	 * 
	 * 
	 * @param aElement 
	 * 
	 * @return 
	 */
	static public LOMAttribute getLOMAttributeFor(Element aElement){
		return ITSElementToLOMAttributeMap.get(aElement);
	}
	
	
}