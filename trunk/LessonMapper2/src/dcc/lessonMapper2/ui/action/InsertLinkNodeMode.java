/*
 * Created on Aug 10, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package dcc.lessonMapper2.ui.action;

import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.graph.element.LinkNode;
import edu.umd.cs.piccolo.PNode;

/**
 * The Class InsertLinkNodeMode.
 * 
 * @author omotelet This action inset linkNode
 */
public class InsertLinkNodeMode extends InsertNodeMode{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
    
    /** The ITS text. */
    public static String ITSText = "insertNodeMode";

    
    /**
	 * The Constructor.
	 */
    public InsertLinkNodeMode() {
        super(ITSText);
     }
    
    
    /* (non-Javadoc)
     * @see dcc.lessonMapper2.ui.action.InsertNodeMode#getNodeToInsert()
     */
    public PNode getNodeToInsert() {
         return new LinkNode();
    }
    
    /* (non-Javadoc)
     * @see dcc.lessonMapper2.ui.action.LessonMapperToggleAction#getHelpText()
     */
    public String getHelpText() {
        return LessonMapper2.getInstance().getLangComment("insertLinkNode");
    }
    

}
