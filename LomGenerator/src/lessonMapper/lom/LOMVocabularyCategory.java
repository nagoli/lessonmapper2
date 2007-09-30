/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.lom;

import java.util.List;

import util.ListWithoutDouble;

/**
 * Compoisite element for LOMVocabulary element
 * Typically it comes as subProperty in the rdf documents
 * User: omotelet
 * Date: Apr 5, 2005
 * Time: 12:57:58 PM.
 */
public class LOMVocabularyCategory extends LOMVocabularyElement{

    /**
     * 
     */
    List<LOMVocabularyElement> itsChildren;

    /**
     * 
     * 
     * @param aName 
     * @param aOrderingValue 
     */
    public LOMVocabularyCategory(String aName, int aOrderingValue) {
        this(aName, aOrderingValue,new ListWithoutDouble<LOMVocabularyElement>());

    }
     
     /**
      * 
      * 
      * @param achildrenElements 
      * @param aName 
      * @param aOrderingValue 
      */
     public LOMVocabularyCategory(String aName, int aOrderingValue, List<LOMVocabularyElement> achildrenElements) {
        super(aName, aOrderingValue,null);
        itsChildren = achildrenElements;

    }
    
    /**
     * 
     * 
     * @param aName 
     */
    public LOMVocabularyCategory(String aName) {
        this(aName,0);
    }

    /**
     * 
     * 
     * @param aSet 
     */
    public void setChildren(List<LOMVocabularyElement> aSet){
        itsChildren = aSet;
    }

    /**
     * 
     * 
     * @param aElement 
     */
    public void addChild(LOMVocabularyElement aElement){
        itsChildren.add(aElement);
    }

    /**
     * 
     * 
     * @return 
     */
    public List getChildren(){
        return itsChildren;
    }


}
