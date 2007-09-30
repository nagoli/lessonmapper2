/*
 * LessonMapper 2.
 */
package dcc.lessonMapper2.ui.graph.element.lom;

import java.awt.Color;

import lessonMapper.lom.LOM;
import dcc.lessonMapper2.LMProject;
import dcc.lessonMapper2.ui.graph.element.SelectionableNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * SelectionableLOM are nodes providing an access to an asssociated LOM instance
 * They also implement the method updateView permitting to make caching of some
 * LOM values used when painting the node.
 * 
 * @author omotelet
 */
public interface SelectionableLOM extends SelectionableNode {

	/**
	 * Gets the LOM.
	 * 
	 * @return the LOM
	 */
	public LOM getLOM();
	
	/**
	 * Sets the LOM.
	 * 
	 * @param aLOM
	 *            the LOM
	 */
	public void setLOM(LOM aLOM);
	
	/**
	 * Update view.
	 */
	public void updateView();
	
	/**
	 * Sets the title color.
	 * 
	 * @param aColor
	 *            the title color
	 */
	public void setTitleColor(Color aColor);
	
	/**
	 * Full paint.
	 * 
	 * @param paintContext
	 *            the paint context
	 */
	public void fullPaint(PPaintContext paintContext);
	
	/* (non-Javadoc)
	 * @see dcc.lessonMapper2.ui.graph.element.SelectionableNode#paint(edu.umd.cs.piccolo.util.PPaintContext)
	 */
	public void paint(PPaintContext paintContext);
	
	public void paintAfterChildren(PPaintContext paintContext);
	
	
	/**
	 * Sets the LM project.
	 * 
	 * @param aProject
	 *            the LM project
	 */
	public void setLMProject(LMProject aProject) ;
	
	/**
	 * Gets the LM project.
	 * 
	 * @return the LM project
	 */
	public LMProject getLMProject();
	
	/**
	 * Gets the material URL.
	 * 
	 * @return the material URL
	 */
	public String getMaterialURL();
	
	
	/**
	 * Gets the material name in lowercase since the 
	 * URI are in lowercase on the repository
	 * 
	 * @return the material name
	 */
	public String getMaterialName();
	
	
	/**
	 * modify material i.e. change the lomid and material id of this lom and its children if applicable 
	 */
	public void modifyMaterial();
	
	/**
	 * change context i.e. change the lomid of this lom and its children if applicable
	 */
	public void modifyContext();
	
	
}