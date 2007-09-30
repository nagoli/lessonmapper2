/*
 * LessonMapper 2.
 */
package dcc.lessonMapper2.ui.lom;


import java.awt.Rectangle;

import lessonMapper.lom.LOM;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.HelperSupport;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * The Class LOMChangeNotifierUI.
 */
public class LOMChangeNotifierUI extends PImage implements HelperSupport{
	
	/** The ITS icon. */
	public static String ITSIcon = "resources/applyIcon.gif" ;
	
	/** The its LOM. */
	protected LOM itsLOM;
	
	/** The its image bounds. */
	protected Rectangle itsImageBounds;
	
	/**
	 * The Constructor.
	 * 
	 * @param aLOM
	 *            the a LOM
	 */
	public LOMChangeNotifierUI(LOM aLOM){
		super(LOMChangeNotifierUI.class.getResource(ITSIcon));
		itsLOM = aLOM;
		//setOffset(-5,-5);
		//setPickable(false);
	}
	
	
	
	/**
	 * Gets the LOM.
	 * 
	 * @return the LOM
	 */
	public LOM getLOM(){
		return itsLOM;
	}
	
	
	/* (non-Javadoc)
	 * @see dcc.lessonMapper2.ui.HelperSupport#getHelp()
	 */
	public String getHelp() {
		return LessonMapper2.getInstance().getLangComment("saveIcon");
	}

	
	
}