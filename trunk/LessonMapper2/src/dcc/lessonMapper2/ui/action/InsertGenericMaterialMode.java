/*
 * Created on Aug 10, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package dcc.lessonMapper2.ui.action;

import javax.swing.ImageIcon;

import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.LMUI;
import dcc.lessonMapper2.ui.graph.element.lom.GenericMaterial;
import edu.umd.cs.piccolo.PNode;

/**
 * The Class InsertGenericMaterialMode.
 * 
 * @author omotelet This action inset linkNode
 */
public class InsertGenericMaterialMode extends InsertNodeMode{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
    
    /** The ITS text. */
    public static String ITSText = "insertGenericMaterialMode";
    
    /** The ITS icon. */
    public static String ITSIcon = LMUI.ITSNewMaterialIcon;
    
    /**
	 * The Constructor.
	 */
    public InsertGenericMaterialMode() {
    	   super(null,new ImageIcon(LessonMapper2.getInstance().getUI().getClass().getResource(ITSIcon)));
     }
    
    
    /* (non-Javadoc)
     * @see dcc.lessonMapper2.ui.action.InsertNodeMode#getNodeToInsert()
     */
    public PNode getNodeToInsert() {
         return new GenericMaterial();
    }
    
    /* (non-Javadoc)
     * @see dcc.lessonMapper2.ui.action.LessonMapperToggleAction#getHelpText()
     */
    public String getHelpText() {
        return LessonMapper2.getInstance().getLangComment("insertMaterial");
    }
    

}
