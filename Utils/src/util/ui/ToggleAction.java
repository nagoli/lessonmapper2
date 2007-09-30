/*
 * Created on Aug 10, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package util.ui;

import java.awt.event.ItemListener;

import javax.swing.Action;

/**
 * @author omotelet
 *
 * this extension of the Action interface includes the needs for listening toggling events
 * ( like select - unselect)
 */
public interface ToggleAction extends Action, ItemListener{

    public ToggleButtonModel getModel();

    
}
