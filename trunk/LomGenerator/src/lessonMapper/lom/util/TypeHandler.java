/*
 * LessonMapper 2.
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