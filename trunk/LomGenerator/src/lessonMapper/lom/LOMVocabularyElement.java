/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
