/*
 * LessonMapper 2.
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