/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.lom.util;

import java.util.Vector;

/**
 * This class holds a type and a path.
 * Typically the type is aPrimitiveType or a complexType
 * The Path is used for intermediate types as langstring
 * For ex: A/langstring/xsd:String will have a
 * TypeHandler [ type = "xsd:String", path= ["langstring"] ]
 * 
 * 
 * User: omotelet
 * Date: Mar 18, 2005
 * Time: 7:09:28 PM
 */
public class TypeHandler {

    /**
     * 
     */
    String itsType;
    
    /**
     * 
     */
    Vector<String> itsPath = new Vector<String>();

    /**
     * 
     */
    public TypeHandler() {
    }

    /**
     * 
     * 
     * @param aType 
     */
    public TypeHandler(String aType) {
        itsType = aType;
    }

    /**
     * 
     * 
     * @param aPath 
     * @param aType 
     */
    public TypeHandler(String aType,Vector<String> aPath) {
        itsPath = aPath;
        itsType = aType;
    }

    /**
     * 
     * 
     * @return 
     */
    public String getType() {
        return itsType;
    }

    /**
     * 
     * 
     * @param aType 
     */
    public void setType(String aType) {
        itsType = aType;
    }

    /**
     * 
     * 
     * @return 
     */
    public Vector<String> getPath() {
        return itsPath;
    }

    /**
     * 
     * 
     * @param aPath 
     */
    public void setPath(Vector<String> aPath) {
        itsPath = aPath;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "TypeHandler{" +
                "itsType='" + itsType + "'" +
                ", itsPath=" + itsPath +
                "}";
    }
}
