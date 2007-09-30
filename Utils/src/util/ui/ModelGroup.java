/*
 * Created on Aug 10, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package util.ui;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

/**
 * @author omotelet
 *
 * This class is similar to buttongroup but consider ToggleAction instead
 */
public class ModelGroup extends ButtonGroup{
    
    // the list of buttons participating in this group
    protected Vector<ButtonModel> models = new Vector<ButtonModel>();

    /**
     * The current selection.
     */
    ButtonModel selection = null;

    /**
     * Creates a new <code>ButtonGroup</code>.
     */
    public ModelGroup() {}

    /** add the model associated with this button
     **/
    public void add(AbstractButton aB) {
      add(aB.getModel());
    }
    
    /** remove the model associated with this button
     **/
    public void remove(AbstractButton aB) {
      remove(aB.getModel());
    }
    
    /** add the model associated with this ToggleAction
     **/
    public void add(ToggleAction aB) {
      add(aB.getModel());
    }
    
    /** remove the model associated with this ToggleAction
     **/
    public void remove(ToggleAction aB) {
      remove(aB.getModel());
    }
    
    /**
     * Adds the buttonmodel to the group.
     * @param b the button to be added
     */ 
    public void add(ButtonModel  b) {
        if(b == null) {
            return;
        }
        models.addElement(b);

        if (b.isSelected()) {
            if (selection == null) {
                selection = b;
            } else {
                b.setSelected(false);
            }
        }

        b.setGroup(this);
    }
 
    /**
     * Removes the buttonmodel from the group.
     * @param b the button to be removed
     */ 
    public void remove(ButtonModel b) {
        if(b == null) {
            return;
        }
        models.removeElement(b);
        if(b == selection) {
            selection = null;
        }
        b.setGroup(null);
    }

    /**
     * Returns all the actions that are participating in
     * this group.
     * @return an <code>Enumeration</code> of the actions in this group
     */
    @SuppressWarnings("unchecked")
	public Enumeration getElements() {
        return models.elements();
    }

    /**
     * Returns the model of the selected button.
     * @return the selected button model
     */
    public ButtonModel getSelection() {
        return selection;
    }

    /**
     * Sets the selected value for the <code>ButtonModel</code>.
     * Only one button in the group may be selected at a time.
     * @param m the <code>ButtonModel</code>
     * @param b <code>true</code> if this button is to be
     *   selected, otherwise <code>false</code>
     */
    public void setSelected(ButtonModel m, boolean b) {
        if (b && m != null && m != selection) {
            ButtonModel oldSelection = selection;
            selection = m;
            if (oldSelection != null) {
                oldSelection.setSelected(false);
            }
            m.setSelected(true);
        } 
    }

    /**
     * Returns whether a <code>ButtonModel</code> is selected.
     * @return <code>true</code> if the button is selected,
     *   otherwise returns <code>false</code>
     */
    public boolean isSelected(ButtonModel m) {
        return (m == selection);
    }

    /**
     * Returns the number of actions in the group.
     * @return the button count
     * @since 1.3
     */
    public int getModelCount() {
	if (models == null) {
	    return 0;
	} else {
	    return models.size();
	}
    }

}
