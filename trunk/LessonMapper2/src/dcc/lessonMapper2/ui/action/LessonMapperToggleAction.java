/*
 * Created on Aug 10, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package dcc.lessonMapper2.ui.action;

import javax.swing.Icon;

import util.ui.AbstractToggleAction;
import util.ui.ToggleAction;
import util.ui.ToggleButtonModel;
import util.ui.ToggleButtonWithSharedModel;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.HelperSupport;
import edu.umd.cs.piccolo.PCanvas;

/**
 * The Class LessonMapperToggleAction.
 * 
 * @author omotelet
 * 
 * add the fields itsLessonMapper and itsCanvas used by most actions
 */
public abstract class LessonMapperToggleAction extends AbstractToggleAction {

  
    

    /**
	 * The Constructor.
	 */
    public LessonMapperToggleAction() {
        super();
     }
    
    /**
	 * The Constructor.
	 * 
	 * @param aName
	 *            the a name
	 */
    public LessonMapperToggleAction(String aName) {
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
    public LessonMapperToggleAction(String aName, Icon aIcon) {
        super(aName, aIcon);
    }
    
    
    
    /**
	 * The Constructor.
	 * 
	 * @param aModel
	 *            the a model
	 * @param aIcon
	 *            the a icon
	 * @param aName
	 *            the a name
	 */
    public LessonMapperToggleAction(String aName, Icon aIcon,
            ToggleButtonModel aModel) {
        super(aName, aIcon, aModel);
    }
    
    /**
	 * Gets the help text.
	 * 
	 * @return the help text
	 */
    public abstract String getHelpText();
    
    
    /* (non-Javadoc)
     * @see util.ui.AbstractToggleAction#createToggleButton()
     */
    public ToggleButtonWithSharedModel createToggleButton(){
        return new ToggleButtonWithSharedModelAndHelper(this);
    }
    
   /**
	 * Gets the main canvas.
	 * 
	 * @return the main canvas
	 */
   public PCanvas getMainCanvas(){
	   return getLessonMapper().getMainCanvas();
   }
    
   /**
	 * Gets the lesson mapper.
	 * 
	 * @return the lesson mapper
	 */
   public LessonMapper2 getLessonMapper(){
	   return LessonMapper2.getInstance();
   }
    
    /**
	 * The Class ToggleButtonWithSharedModelAndHelper.
	 */
    public class ToggleButtonWithSharedModelAndHelper extends ToggleButtonWithSharedModel implements HelperSupport{
    	
	    /** The Constant serialVersionUID. */
	    private static final long serialVersionUID = 1L;
        
        /**
		 * The Constructor.
		 * 
		 * @param a
		 *            the a
		 */
        public ToggleButtonWithSharedModelAndHelper(ToggleAction a) {
            super(a);
            setToolTipText(getHelpText());
        }

        /* (non-Javadoc)
         * @see dcc.lessonMapper2.ui.HelperSupport#getHelp()
         */
        public String getHelp() {
           return getHelpText();
        }
        
         
        
    }
    
    
    
}
