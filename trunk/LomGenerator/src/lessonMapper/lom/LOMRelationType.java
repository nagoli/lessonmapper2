/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.lom;

import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import lessonMapper.LangManager;

import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import diffuse.metadata.MetadataSetRelationType;

/**
 * LOMRelationType encapsulate type for the relations.
 * a lomRelationType has a contrary. If the contrary is not defined the relationtype itself is considered as its contrary.
 * Constructor is not public in order to force unique instance of LOMRelationType by string with equal name
 * User: omotelet
 * Date: Mar 16, 2005
 * Time: 11:32:42 AM
 */
public class LOMRelationType implements MetadataSetRelationType {

	
	/**
	 *  
	 */
	public static final String ITSNameSpacePrefix = "lom_rel";
	
	/**
	 * 
	 */
	public static final String ITSNameSpace =  "http://www.imsproject.org/rdf/imsmd_relationv1p2#" ;
	
	/**
	 * 
	 */
	public static URL ITSDescriptionURL = LOM.class.getResource("resources/vocabulary/relation.rdf");
	
	/**
	 * 
	 */
	static final String[][] ITSNameSpaces = new String[][] {
		{ "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#" }, // shoudl
		// remain
		// in
		// first
		// position
		{ "rdfs", "http://www.w3.org/2000/01/rdf-schema#" }, // should
		// remain in
		// second
		// position
		{ "lom_rel", "http://www.imsproject.org/rdf/imsmd_relationv1p2#" },
		{"lm","http://www.dcc.uchile.cl/lessonMapper#"}//sould remain in third position
		};
    

   /**
    * 
    */
   static Hashtable<String,LOMRelationType> ITSRelationTypes = new Hashtable<String,LOMRelationType>();
   
   /**
    * 
    */
   static List<LOMRelationType> ITSAvailableTypes, ITSVisibleTypes;
   
   /**
    * 
    */
   static boolean areAvailableTypesRegistered = false;
   
    /**
     * 
     * 
     * @param aRelationName 
     * 
     * @return 
     */
    public static LOMRelationType getLOMRelationType(String aRelationName) {
        if (!areAvailableTypesRegistered) registerAvailableTypes();
    	aRelationName = aRelationName.toLowerCase().trim();
    	LOMRelationType theRelation = ITSRelationTypes.get(aRelationName);
        if (theRelation == null) {
            theRelation = new LOMRelationType(aRelationName,"not registered: "+aRelationName);
            ITSRelationTypes.put(aRelationName, theRelation);
        }
        return theRelation;
    }
    
    /**
     * 
     * 
     * @param aVisibility 
     * @param aLabel 
     * @param aRelationName 
     * @param aCoupling 
     * @param aContrary 
     * 
     * @return 
     */
    public static LOMRelationType registerLOMRelationType(String aRelationName, 
    		String aLabel, String aContrary, boolean aVisibility, boolean aCoupling) {
        aRelationName = aRelationName.toLowerCase().trim();
        LOMRelationType theRelation = getLOMRelationType(aRelationName);
        theRelation.itsLabel=aLabel;
        theRelation.itsContrary=getLOMRelationType(aContrary);
        theRelation.isVisible=aVisibility;
        theRelation.isStronglyCoupled=aCoupling;
        return theRelation;
    }
    

    
    /**
     * register the available types.
     * Relation type are defined as rdf properties. It differs from
     * other attribute vocabulary defined as rdf instance
     */
    public static void registerAvailableTypes(){
    	List<LOMRelationType> theTypes = new ArrayList<LOMRelationType>();
    	URL theURL = ITSDescriptionURL;
    	List results = null;
    	try {
			Document theRDFModel = (new SAXBuilder()).build(theURL);
			JDOMXPath myXPath = new JDOMXPath("rdf:RDF/rdf:Property");
			for (int i = 0; i < ITSNameSpaces.length; i++) {
				String[] theNameSpace = ITSNameSpaces[i];
				myXPath.addNamespace(theNameSpace[0], theNameSpace[1]);
			}
			results = myXPath.selectNodes(theRDFModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (results != null){
			for (Object theResult : results) {
				areAvailableTypesRegistered =true;
				Element theProperty = (Element) theResult;
				String theType = theProperty.getAttribute("ID", theProperty.getNamespace()).getValue();
				String theContraryType = theProperty.getAttribute("contrary", Namespace.getNamespace(ITSNameSpaces[3][0],ITSNameSpaces[3][1])).getValue();
				boolean theVisibility =true, theCoupling = false;
				try {
					theVisibility = theProperty.getAttribute("visible", Namespace.getNamespace(ITSNameSpaces[3][0],ITSNameSpaces[3][1])).getBooleanValue();
					theCoupling = theProperty.getAttribute("stronglyCoupled", Namespace.getNamespace(ITSNameSpaces[3][0],ITSNameSpaces[3][1])).getBooleanValue();
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				Element theLabel = theProperty.getChild("label",Namespace.getNamespace(ITSNameSpaces[1][0],ITSNameSpaces[1][1]));
				Element theAlt = null;
				if (theLabel !=null)
					theAlt=theLabel.getChild("Alt",Namespace.getNamespace(ITSNameSpaces[0][0],ITSNameSpaces[0][1]));
				String theTypeLabel = LangManager.getInstance().getLangString(theAlt);
				if (theTypeLabel==null) theTypeLabel = theType + "(notLabeled)";
				theTypes.add(registerLOMRelationType(theType,theTypeLabel,theContraryType, theVisibility,theCoupling));
			}
		}
    	ITSAvailableTypes = theTypes;
    }
    
    /**
     * 
     * 
     * @return 
     */
    public static List<LOMRelationType> getAvailableTypes(){
    	if (!areAvailableTypesRegistered) registerAvailableTypes();
    	return ITSAvailableTypes;
    }
    
    /**
     * 
     * 
     * @return 
     */
    public static List<LOMRelationType> getAvailableVisibleTypes(){
    	if (ITSVisibleTypes ==null) {
    		List<LOMRelationType> theAvailableTypes = getAvailableTypes();
        	ITSVisibleTypes = new ArrayList<LOMRelationType>();
        	for (LOMRelationType theType : theAvailableTypes) {
        		if (theType.isVisible) ITSVisibleTypes.add(theType);
    		}
    	}
    	return ITSVisibleTypes;
    }
    
    /**
     * 
     */
    String itsName;
    
    /**
     * 
     */
    String itsLabel;
    
    /**
     * 
     */
    boolean isVisible = true;
    
    /**
     * 
     */
    LOMRelationType itsContrary;
    
    /**
     * 
     */
    boolean isStronglyCoupled = false;
    
    /**
     * 
     * 
     * @param aLabel 
     * @param aName 
     */
    private LOMRelationType(String aName, String aLabel){
       itsName=aName;
       itsLabel=aLabel;
       itsContrary = this;
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
     * @param aRelationName 
     * 
     * @return 
     */
    public boolean is(String aRelationName){
        return itsName.equalsIgnoreCase(aRelationName);
    }

    /**
     * 
     * 
     * @return 
     */
    public boolean isVisible() {
    	return isVisible;
    }
     
    /**
     * 
     * 
     * @return 
     */
    public LOMRelationType getContrary() {
    	return itsContrary;
    }
    
    /**
     * 
     * 
     * @return 
     */
    public boolean isStronglyCoupled() {
    	return isStronglyCoupled;
    }
    
    
    /**
     * 
     * 
     * @return 
     */
    public String getLabel() {
    	return itsLabel;
    }


    /**
     * return a element based on the values of aLOMRelationType.
     * 
     * @param aLOMRelationType 
     * 
     * @return 
     */
	public static Element makeXMLElement(LOMRelationType  aLOMRelationType){
		Element theElement = new Element("LOMRelationType");
		Element theID = new Element("name");
		theID.addContent(aLOMRelationType.getName());
		theElement.addContent(theID);
		return theElement;
	}
		
	/**
	 * return aLOMARelationType with the value stored in aElement.
	 * 
	 * @param aElement 
	 * 
	 * @return 
	 */
	public static LOMRelationType buildFromXMLElement(Element aElement) {
		return getLOMRelationType( aElement.getChildTextTrim("name"));
	}
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
       /* return "LOMRelationType{" +
                "itsName='" + itsName + "'" +
                "}";*/
    	return itsName;
    }
}
