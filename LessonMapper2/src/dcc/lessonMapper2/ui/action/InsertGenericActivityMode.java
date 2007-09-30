/** Created on Aug 10, 2005
*
* LessonMapper2 Copyright DCC Universidad de Chile
*/
package dcc.lessonMapper2.ui.action;

import javax.swing.ImageIcon;

import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.LMUI;
import dcc.lessonMapper2.ui.graph.element.lom.GenericActivity;
import edu.umd.cs.piccolo.PNode;

/**
 * The Class InsertGenericActivityMode.
 * 
 * @author omotelet
 */
public class InsertGenericActivityMode extends InsertNodeMode {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
   
   /** The ITS text. */
   public static String ITSText = "insertGenericActivityMode";
   
   /** The ITS icon. */
   public static String ITSIcon = LMUI.ITSNewActivityIcon;
   
   
   /**
	 * The Constructor.
	 */
   public InsertGenericActivityMode() {
	   super(null,new ImageIcon(LessonMapper2.getInstance().getUI().getClass().getResource(ITSIcon)));
	      }
   
   /* (non-Javadoc)
    * @see dcc.lessonMapper2.ui.action.InsertNodeAction#getNodeToInsert()
    */
   /* (non-Javadoc)
    * @see dcc.lessonMapper2.ui.action.InsertNodeMode#getNodeToInsert()
    */
   public PNode getNodeToInsert() {
       return new GenericActivity();
   }

   /* (non-Javadoc)
    * @see dcc.lessonMapper2.ui.action.LessonMapperToggleAction#getHelpText()
    */
   public String getHelpText() {
       return LessonMapper2.getInstance().getLangComment("insertActivity");
   }
   
}
