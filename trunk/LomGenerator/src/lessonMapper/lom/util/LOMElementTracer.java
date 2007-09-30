/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
