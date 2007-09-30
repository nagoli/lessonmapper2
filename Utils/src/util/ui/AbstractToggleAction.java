/*
 * Created on Aug 10, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package util.ui;

import javax.swing.AbstractAction;
import javax.swing.Icon;


/**
 * @author omotelet
 *
 *this class extends abstractaction to assume the toggle mode of the button
 *An toogle action is associated to a togglebuttonmodel. 
 *Therefore all buttons associated with this action will share a same model.
 *
 * Implementing synchronization between several model is a useless extension of the MVC model. 
 * For that reason this action is based on the sharing of a unique model.
 * This point is also trying to enable buttongroup working for various UI of a same togglemodel.
 *  */
public abstract class AbstractToggleAction extends AbstractAction implements ToggleAction{

    protected ToggleButtonModel itsModel;
    
    
    
    /**
     * 
     */
    public AbstractToggleAction() {
        this(null,null);
    }
    /**
     * @param aName
     */
    public AbstractToggleAction(String aName) {
        this(aName,null);
    }
    /**
     * @param aName
     * @param aIcon
     */
    public AbstractToggleAction(String aName, Icon aIcon) {
        this(aName, aIcon, new ToggleButtonModel());
    }
    
    public AbstractToggleAction(String aName, Icon aIcon,ToggleButtonModel aModel) {
        super(aName, aIcon);
       
        setModel(aModel);
    }
    
    public void setModel(ToggleButtonModel aModel){
       itsModel = aModel;
    }
    
    public ToggleButtonModel getModel(){
       return itsModel; 
    }
    
    public ToggleButtonWithSharedModel createToggleButton(){
        return new ToggleButtonWithSharedModel(this);
    }
    
    
}
