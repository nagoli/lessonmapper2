/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
