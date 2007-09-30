/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.lom;

import java.io.IOException;
import java.util.List;

import lessonMapper.lom.util.TypeHandler;

import org.jaxen.JaxenException;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * User: omotelet
 * Date: Mar 18, 2005
 * Time: 2:23:27 PM.
 */
public class LOMSchema {

    /**
     * 
     */
    static final String[] ITSXmlNameSpace = new String[]{"xml", "http://www.w3.org/XML/1998/namespace"};
    
    /**
     * 
     */
    static final String[] ITSXsdNameSpace = new String[]{"xsd", "http://www.w3.org/2001/XMLSchema"};
    
    /**
     * 
     */
    static final String[] ITSXsiNameSpace = new String[]{"xsi", "http://www.w3.org/2001/XMLSchema-instance"};

    /**
     * 
     */
    public static LOMSchema Instance = new LOMSchema();


    /**
     * 
     */
    boolean isLangStringAvoided = false;
    
    /**
     * 
     */
    boolean isSource_ValueAvoided = false;
    
    /**
     * 
     */
    Document itsModel;


    /**
     * 
     */
    LOMSchema() {
        SAXBuilder theBuilder = new SAXBuilder();
        try {
            itsModel = theBuilder.build(LOMSchema.class.getResource(LOM.ITSSchemaSource));
            setLangStringAvoided(true);
            setSource_ValueAvoided(true);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * 
     * @return 
     */
    public boolean isLangStringAvoided() {
        return isLangStringAvoided;
    }


    /**
     * this is put to true if we want to avoid langString effect
     * this CANNOT be set to false if isSource_ValueAvoided.
     * 
     * @param aLangStringAvoided 
     */
    public void setLangStringAvoided(boolean aLangStringAvoided) {
        if (isSource_ValueAvoided) return;
        isLangStringAvoided = aLangStringAvoided;
    }

    /**
     * 
     * 
     * @return 
     */
    public boolean isSource_ValueAvoided() {
        return isSource_ValueAvoided;
    }

    /**
     * this is put to true if we want to avoid  effect of couples source-value
     * if source,value couples are avoided then langstring also is avoided.
     * 
     * @param aSource_ValueAvoided 
     */
    public void setSource_ValueAvoided(boolean aSource_ValueAvoided) {
        isSource_ValueAvoided = aSource_ValueAvoided;
        if (isSource_ValueAvoided) setLangStringAvoided(true);
    }


    /**
     * get nodes for aXPath - Note that current LOM namespace is prefixed with "ims".
     * 
     * @param aXPath 
     * 
     * @return 
     */
    public List getNodes(String aXPath) {
        List result = null;
        try {
            JDOMXPath myXPath = new JDOMXPath(aXPath);
            myXPath.addNamespace(ITSXmlNameSpace[0], ITSXmlNameSpace[1]);
            myXPath.addNamespace(ITSXsdNameSpace[0], ITSXsdNameSpace[1]);
            myXPath.addNamespace(ITSXsiNameSpace[0], ITSXsiNameSpace[1]);
            result = myXPath.selectNodes(itsModel);
        } catch (JaxenException e) {
            e.printStackTrace();
        }
        return result;
    }


//    public Element getElementType(String aName){
//        List theResult= Instance.getNodes("xsd:schema/child::xsd:*[attribute::name='"+aName+"Type']");
//        if (theResult==null) return null;
//        return (Element) theResult.get(0);
//    }

    /**
 * 
 * 
 * @param aName 
 * 
 * @return 
 */
public Element getElement(String aName) {
        List theResult = Instance.getNodes("xsd:schema/child::xsd:*[attribute::name='" + aName + "']");
        if (theResult == null || theResult.isEmpty()) return null;
        return (Element) theResult.get(0);
    }


    /**
     * This method return a list containing
     * first a string with the name of the type
     * If it is a intermediate type as langstring it returns the primitive type and
     * the path until the primitive type
     * It returns null if if does not appear in the LOM Schema.
     * 
     * @param aName 
     * 
     * @return 
     */
    public TypeHandler getType(String aName) {
        // TypeHandler theResult = new TypeHandler();
        Element theElement = getElement(aName);
        if (theElement == null) {
            System.out.println(aName + " is not in LOMSchema");
            return null;
        }
        String theTypeName = theElement.getAttributeValue("type");
        if (isPrimitive(theTypeName)) {
            return new TypeHandler(theTypeName);
        }
        Element theTypeElement = getElement(theTypeName);
        if (theTypeElement.getName().equals("simpleType")) {
            String thePrimitiveTypeName = theTypeElement.getChild("restriction", theTypeElement.getNamespace()).getAttributeValue("base");
            return new TypeHandler(thePrimitiveTypeName);
        }

        if (theTypeElement.getName().equals("complexType")) {
            // we consider the complextype element should have only one child
            Element theComplexTypeChild = (Element) theTypeElement.getChildren().get(0);
            if (theComplexTypeChild.getName().equals("simpleContent")) {
                String thePrimitiveTypeName = theComplexTypeChild.getChild("extension", theTypeElement.getNamespace()).getAttributeValue("base");
                return new TypeHandler(thePrimitiveTypeName);
            }


                if (theComplexTypeChild.getName().equals("sequence")) {
                    List theElementChildren = theComplexTypeChild.getChildren();
                    for (int i = 0; i < theElementChildren.size(); i++) {
                        Element theElementChild = (Element) theElementChildren.get(i);
                           // special process to avoid langstring
                        if (isLangStringAvoided) {
                            if (theElementChild.getName().equals("element")) {
                                if (theElementChild.getAttributeValue("ref")
                                        .equalsIgnoreCase("langstring")) {
                                    TypeHandler theType = getType("langstring");
                                    theType.getPath().add(0, "langstring");
                                    return theType;
                                }
                            }
                        }
                          // special process to avoid source-value effect
                        if (isSource_ValueAvoided) {
                            if (theElementChild.getName().equals("element")) {
                                if (theElementChild.getAttributeValue("ref")
                                        .equalsIgnoreCase("value")) {
                                    TypeHandler theType = getType("value");
                                    theType.getPath().add(0, "value");
                                    return theType;
                                }
                            }
                        }
                    }
            }


//            if (isLangStringAvoided) {
//                if (theComplexTypeChild.getName().equals("sequence")) {
//                    Element theElementChild = (Element) theComplexTypeChild.getChildren().get(0);
//                    if (theElementChild.getName().equals("element")) {
//                        if (theElementChild.getAttributeValue("ref")
//                                .equalsIgnoreCase("langstring")) {
//                            TypeHandler theType = getType("langstring");
//                            theType.getPath().add(0, "langstring");
//                            return theType;
//                        }
//                    }
//                }
//            }


            return new TypeHandler("xsd:complexType");
        }
        return null;
    }

    /**
     * 
     * 
     * @param aType 
     * 
     * @return 
     */
    public boolean isPrimitive(String aType) {
        return (aType.endsWith("string") || aType.endsWith("int"));
    }

    /**
     * 
     * 
     * @param args 
     */
    public static void main(String[] args) {
        //System.out.println( Instance.getElement("langstring") );
        System.out.println(Instance.getType("type"));
    }

}
