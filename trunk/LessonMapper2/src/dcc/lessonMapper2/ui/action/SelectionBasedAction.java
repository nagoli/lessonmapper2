/*
 * Created on Aug 17, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package dcc.lessonMapper2.ui.action;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import dcc.lessonMapper2.ui.HelperSupport;

/**
 * The Class SelectionBasedAction.
 * 
 * @author omotelet
 */
public abstract class SelectionBasedAction extends AbstractAction        {

    
    /**
	 * The Constructor.
	 */
    public SelectionBasedAction() {
        super();
    }

    /**
	 * The Constructor.
	 * 
	 * @param aName
	 *            the a name
	 */
    public SelectionBasedAction(String aName) {
        super(aName);
    }

    /**
	 * The Constructor.
	 * 
	 * @param aIcon
	 *            the a icon
	 * @param aName
	 *            the a name
	 */
    public SelectionBasedAction(String aName, Icon aIcon) {
        super(aName, aIcon);
       
    }

   /**
	 * Gets the button.
	 * 
	 * @return the button
	 */
   public JButton getButton(){
       return new JButtonWithHelper(this); 
   }
   
   /**
	 * Gets the help text.
	 * 
	 * @return the help text
	 */
   public abstract String getHelpText();
   
   /**
	 * The Class JButtonWithHelper.
	 */
   public class JButtonWithHelper extends JButton implements HelperSupport{

	   /** The Constant serialVersionUID. */
   	private static final long serialVersionUID = 1L;
    
    /**
	 * The Constructor.
	 * 
	 * @param a
	 *            the a
	 */
    public JButtonWithHelper(Action a){
        super(a);
        
    }
       
    /* (non-Javadoc)
     * @see dcc.lessonMapper2.ui.HelperSupport#getHelp()
     */
    public String getHelp() {
        return getHelpText();
    }
       

}
}
