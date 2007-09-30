/*
 * Created on Aug 10, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package dcc.lessonMapper2.ui.action;

import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.graph.element.ContainerNode;
import edu.umd.cs.piccolo.PNode;

/**
 * The Class InsertContainerMode.
 * 
 * @author omotelet
 */
public class InsertContainerMode extends InsertNodeMode {

	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
    
    /** The ITS text. */
    public static String ITSText = "insertContainerMode";
    
    
    
    /**
	 * The Constructor.
	 */
    public InsertContainerMode() {
        super(ITSText);
    }
    
    /* (non-Javadoc)
     * @see dcc.lessonMapper2.ui.action.InsertNodeAction#getNodeToInsert()
     */
    /* (non-Javadoc)
     * @see dcc.lessonMapper2.ui.action.InsertNodeMode#getNodeToInsert()
     */
    public PNode getNodeToInsert() {
        return new ContainerNode();
    }

    /* (non-Javadoc)
     * @see dcc.lessonMapper2.ui.action.LessonMapperToggleAction#getHelpText()
     */
    public String getHelpText() {
        return LessonMapper2.getInstance().getLangComment("insertContainer");
    }
    
}
