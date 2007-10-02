/*
 * LessonMapper 2.
 */
package dcc.lessonMapper2.ui.graph.element.lom;

import lessonMapper.lom.LOMRelationType;
import dcc.lessonMapper2.ui.graph.element.MotionNode;
import dcc.lessonMapper2.ui.lom.LOMRelationChooser;


/**
 * The Class LOMRelationTitleUI.
 */
public class LOMRelationTitleUI extends MotionNode{

	/** The ITS default scale. */
	public static double ITSDefaultScale = 0.7;
	
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The Constructor.
	 */
	public LOMRelationTitleUI() {
		super();
		setScale(1); // init customized scaling
		
	}
	
	/* (non-Javadoc)
	 * @see edu.umd.cs.piccolo.PNode#setScale(double)
	 */
	@Override
	public void setScale(double aScale) {
		super.setScale(aScale*ITSDefaultScale);
	}
	
	/**
	 * update the UI according to the type passed as parameter.
	 * 
	 * @param aType
	 *            the a type
	 */
	public void updateUI(LOMRelationType aType){
		/*LOMAttribute theAttribute = LOMAttribute.getLOMAttribute("educational/semanticdensity");
		BigDecimal theProba = SuggestionProbability.getInstance().getProbaFor(theAttribute, aType);
		BigDecimal theReverseProba = SuggestionProbability.getInstance().getProbaFor(theAttribute, aType.getContrary());
		*/
		setText(aType.getLabel()/*+ theProba+"("+theReverseProba +")"*/);
		setTextPaint(LOMRelationChooser.getColorForType(aType));
	}
	
	/**
	 * Gets the relation UI.
	 * 
	 * @return the relation UI
	 */
	public LOMRelationUI getRelationUI(){
		return (LOMRelationUI)itsEdge;
	}
	
	/**
	 * return true if this LOMRelationTitleUI concerns the SelecionableLOM
	 * passed as parameter.
	 * 
	 * @param aSelectionableLOM
	 *            the a selectionable LOM
	 * 
	 * @return true, if concerns
	 */
	public boolean concerns(SelectionableLOM aSelectionableLOM){
		return getRelationUI().concerns(aSelectionableLOM);
	}

	
	
	/* (non-Javadoc)
	 * @see dcc.lessonMapper2.ui.graph.element.MotionNode#detach()
	 */
	public void detach(){
		getRelationUI().detach();
	}
	
	/**
	 * if detachLOMRelation is true also detach the LOM relation from its
	 * parent.
	 * 
	 * @param detachLOMRelation
	 *            the detach LOM relation
	 */
	public void detach(boolean detachLOMRelation){
		getRelationUI().detach(detachLOMRelation);
	}
	
	
}