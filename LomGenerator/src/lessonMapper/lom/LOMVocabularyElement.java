/*
 * LessonMapper 2.
 */
package lessonMapper.lom;

import java.awt.Image;

import javax.swing.ImageIcon;

import lessonMapper.LangManager;
import lessonMapper.lom.util.IconManager;

import org.jdom.Element;

/**
 * Wrap LOMVocabulary elements
 * to make them comparable
 * User: omotelet
 * Date: Apr 4, 2005
 * Time: 1:15:41 PM.
 */
public class LOMVocabularyElement implements Comparable {

    /**
     * 
     */
    String itsName;
    
    /**
     * 
     */
    int itsOrderingValue;
    
    /**
     * 
     */
    String itsView;
    
    /**
     * 
     */
    ImageIcon itsIcon;
    
    /**
     * 
     * 
     * @param aName 
     * @param aAltElement 
     * @param aOrderingValue 
     */
    public LOMVocabularyElement(String aName, int aOrderingValue, Element aAltElement) {
        itsName = aName;
        itsOrderingValue = aOrderingValue;
        itsView = LangManager.getInstance().getLangString(aAltElement);
        itsIcon= IconManager.getIconForElement(aName);
    }

  
    /**
     * 
     * 
     * @return 
     */
    public String getName() {
        return itsName;
    }

    /**
     * 
     * 
     * @return 
     */
    public int getOrderingValue() {
        return itsOrderingValue;
    }

    /**
     * 
     * 
     * @return 
     */
    public String getLabel() {
    	return itsView;
    }
    
    /**
     * 
     * 
     * @return 
     */
    public ImageIcon getIcon() {
    	return itsIcon;
    }
    
    /**
     * 
     * 
     * @return 
     */
    public Image getCompatibleImage() {
    	return IconManager.getCompatibleImageForElement(itsName);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        LOMVocabularyElement theOtherElement = (LOMVocabularyElement) o;
        return new Integer(itsOrderingValue).compareTo(new Integer(theOtherElement.getOrderingValue()));
    }


    /**
     * 
     * 
     * @return 
     */
    public String print() {
        return "LOMVocabularyElement{" +
                "itsName='" + itsName + "'" +
                ", itsOrderingValue=" + itsOrderingValue +
                "}";
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return itsView;
    }
    
}