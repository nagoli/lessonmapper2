/*
 * Created on Aug 10, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package util.ui;


import javax.swing.JToggleButton;

/**
 * @author omotelet
 *
 */
public class ToggleButtonWithSharedModel extends JToggleButton{

    /**
     * @param a
     */
    public ToggleButtonWithSharedModel(ToggleAction a) {
        setModel(a.getModel());
        setAction(a);
        addItemListener(a);
    }

    
    
}
