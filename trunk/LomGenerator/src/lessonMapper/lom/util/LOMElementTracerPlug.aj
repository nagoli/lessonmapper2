/*
 * LessonMapper 2.
 */
package lessonMapper.lom.util;

import java.util.List;

import org.jdom.Element;
import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
public aspect LOMElementTracerPlug {
	
	after(LOM aLOM) returning (List theResult): execution(List LOMAttribute.getNodesIn(LOM)) && args(aLOM){
		 //List theResult = proceed(aLOM);
		LOMAttribute theAttribute = (LOMAttribute) thisJoinPoint.getTarget();
		if (theResult!=null){
			for (int i = 0; i < theResult.size()	; i++) {
				Element theElement = (Element)theResult.get(i);
				LOMElementTracer.ITSElementToLOMAttributeMap.put(theElement,theAttribute);
				LOMElementTracer.ITSElementToLOMMap.put(theElement,aLOM);
			}
		}
		//return theResult;
	}
	
}