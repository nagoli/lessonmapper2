/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
