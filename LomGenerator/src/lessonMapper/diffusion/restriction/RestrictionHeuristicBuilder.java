/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.diffusion.restriction;

import java.io.StringReader;
import java.util.Vector;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import javassist.NotFoundException;

/**
 * User: omotelet
 * Date: Apr 12, 2005
 * Time: 6:41:34 PM.
 */
public class RestrictionHeuristicBuilder {

    /**
     * 
     */
    static int theID = 0;
    
    /**
     * 
     */
    String itsRestrictionOperator;
    
    /**
     * 
     */
    String itsAttributeName;
    
    /**
     * 
     */
    String itsRelationName;
    
    /**
     * 
     */
    String itsFormule;

    /**
     * 
     */
    Vector<String> itsFields = new Vector<String>();
    
    /**
     * 
     */
    Vector<String> itsRecurrentStatements = new Vector<String>();
    
    /**
     * 
     */
    String itsFinalStatement;
    //init parser
    /**
     * 
     */
    static RestrictionParser itsParser = new RestrictionParser(new StringReader(""));


    /**
     * 
     * 
     * @param aRelationName 
     * @param aFormule 
     * @param aAttributeName 
     */
    public RestrictionHeuristicBuilder(String aAttributeName, String aRelationName, String aFormule) {
        itsAttributeName = aAttributeName;
        itsRelationName = aRelationName;
        itsFormule = aFormule;
        RestrictionParser.ReInit(new StringReader(itsFormule));
        try {
            RestrictionParser.Heuristic(this);
        } catch (lessonMapper.diffusion.restriction.ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * 
     * @return 
     */
    public Vector getFields() {
        return itsFields;
    }

    /**
     * 
     * 
     * @param aField 
     */
    public void addField(String aField) {
        itsFields.add(aField);
    }

    /**
     * 
     * 
     * @param aOperator 
     */
    public void setRestrictionOperator(String aOperator){
         itsRestrictionOperator=aOperator;
    }

    /**
     * 
     * 
     * @return 
     */
    public String getDefaultFusionStrategy(){
        if (itsRestrictionOperator!=null)
            return "oij.equals("+itsRestrictionOperator+") || oij.equals(EQ)";
        return "oij.equals(EQ)";
    }

    /**
     * 
     * 
     * @return 
     */
    public Vector getRecurrentStatements() {
        return itsRecurrentStatements;
    }

    /**
     * 
     * 
     * @param aRecurrentStatement 
     */
    public void addRecurrentStatement(String aRecurrentStatement) {
        itsRecurrentStatements.add(aRecurrentStatement);
    }

    /**
     * 
     * 
     * @return 
     */
    public String getFinalStatement() {
        return itsFinalStatement;
    }

    /**
     * 
     * 
     * @param aFinalStatement 
     */
    public void setFinalStatement(String aFinalStatement) {
        itsFinalStatement = aFinalStatement;
    }


    /**
     * 
     * 
     * @return 
     */
    public Class getHeuristicClass() {
        //System.out.print("building class");
    	//CGLIBVErsion
    	//Enhancer theEnhancer  = new Enhancer();
    	
    	
    	
    	//JAVAssist version
    	ClassPool thePool = ClassPool.getDefault();
        CtClass theCtClass = thePool.makeClass("lessonMapper.diffusion.LOMRestrictionHeuristic" + theID++);
        try {
        	//System.out.println("local: "+ Thread.currentThread().getContextClassLoader().getResource("lessonMapper/diffusion/LOMRestrictionHeuristic.class"));
        	//System.out.println("system:" + ClassLoader.getSystemClassLoader().getResource("lessonMapper/diffusion/LOMRestrictionHeuristic.class"));
         	theCtClass.setInterfaces(new CtClass[]{thePool.get("lessonMapper.diffusion.LOMRestrictionHeuristic")});
        } catch (NotFoundException e) {
          try{  // may happen in environments with multiple classloaders: className is not available
            // in the initial classpath of javassist's class pool
            // ==> try to append classpath of the class corresponding to className
            thePool.appendClassPath(new ClassClassPath(Class.forName("lessonMapper.diffusion.LOMRestrictionHeuristic")));
            theCtClass.setInterfaces(new CtClass[]{thePool.get("lessonMapper.diffusion.LOMRestrictionHeuristic")});
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }}
        try {
            theCtClass.addField(CtField.make("static lessonMapper.lom.LOMAttribute ITSLOMAttribute = lessonMapper.lom.LOMAttribute.getLOMAttribute(\"" + itsAttributeName + "\");", theCtClass));
            theCtClass.addField(CtField.make("static lessonMapper.lom.LOMRelationType ITSLOMRelationType = lessonMapper.lom.LOMRelationType.getLOMRelationType(\"" + itsRelationName + "\");", theCtClass));
            for (int i = 0; i < itsFields.size(); i++) {
                String theField = (String) itsFields.elementAt(i);
                theCtClass.addField(CtField.make(theField, theCtClass));
            }

            String theMethod1 = "public boolean process(lessonMapper.lom.LOM li, lessonMapper.lom.RestrictionOperator oij, lessonMapper.lom.LOMValue vij) {\n";
            for (int i = 0; i < itsRecurrentStatements.size(); i++) {
                String theStatement = (String) itsRecurrentStatements.elementAt(i);
                theMethod1 += theStatement + "\n";
            }
            theMethod1 += "return true;}";
            String theMethod2 = " public lessonMapper.lom.LOMRestrictionValue getRestrictionValue(){ \n";
            theMethod2 += itsFinalStatement + "\n";
            theMethod2 += "}";
            //System.out.println(itsAttributeName+" "+itsRelationName);
            //System.out.println(theMethod1);
            //System.out.println(theMethod2);
            theCtClass.addMethod(CtNewMethod.make(theMethod1, theCtClass));
            theCtClass.addMethod(CtNewMethod.make(theMethod2, theCtClass));
            return theCtClass.toClass();
        } catch (CannotCompileException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "RestrictionHeuristicBuilder{" + "\n" +
                "itsFields=" + itsFields + "\n" +
                ", itsRecurrentStatements=" + itsRecurrentStatements + "\n" +
                ", itsFinalStatement='" + itsFinalStatement + "'" +
                "}";
    }

   
    
    /**
     * 
     * 
     * @param args 
     */
    public static void main(String[] args) {
        System.out.println(new RestrictionHeuristicBuilder("general/keyword", "haspart", "r=union(vi); return r;").getHeuristicClass());
  
    
    }


}

