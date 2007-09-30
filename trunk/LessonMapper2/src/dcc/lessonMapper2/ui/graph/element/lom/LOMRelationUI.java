/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package dcc.lessonMapper2.ui.graph.element.lom;

import lessonMapper.lom.LOMRelation;
import lessonMapper.lom.LOMRelationType;
import dcc.lessonMapper2.ui.graph.element.TypedEdge;
import dcc.lessonMapper2.ui.lom.LOMRelationChooser;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * The Class LOMRelationUI.
 */
public class LOMRelationUI extends TypedEdge{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The its LOM relation. */
	protected LOMRelation itsLOMRelation;
	
	/**
	 * The Constructor.
	 * 
	 * @param aNode2
	 *            the a node2
	 * @param aNode1
	 *            the a node1
	 */
	public LOMRelationUI(SelectionableLOM aNode1, SelectionableLOM aNode2) {
		this(aNode1, aNode2, new LOMRelationTitleUI());
	}
	
	/**
	 * The Constructor.
	 * 
	 * @param aNode2
	 *            the a node2
	 * @param aLOMRelationTitle
	 *            the a LOM relation title
	 * @param aNode1
	 *            the a node1
	 */
	public LOMRelationUI(SelectionableLOM aNode1, SelectionableLOM aNode2, LOMRelationTitleUI aLOMRelationTitle) {
		super((PNode)aNode1, (PNode)aNode2, aLOMRelationTitle);
		itsLOMRelation = new LOMRelation(aNode1.getLOM(), aNode2.getLOM());
		updateUI();
	}
 
	/**
	 * The Constructor.
	 * 
	 * @param aNode2
	 *            the a node2
	 * @param aRelation
	 *            the a relation
	 * @param aNode1
	 *            the a node1
	 */
	public LOMRelationUI(SelectionableLOM aNode1, SelectionableLOM aNode2, LOMRelation aRelation){
		super((PNode)aNode1, (PNode)aNode2, new LOMRelationTitleUI());
		itsLOMRelation = aRelation;
		updateUI();
	}
	
	
	
	/**
	 * Update UI.
	 */
	public void updateUI(){
		LOMRelationType theType = itsLOMRelation.getLOMRelationType();
		setStrokePaint(LOMRelationChooser.getColorForType(theType));
		getRelationTitleUI().updateUI(theType);
	}
	
	/**
	 * Gets the LOM relation.
	 * 
	 * @return the LOM relation
	 */
	public LOMRelation getLOMRelation(){
		return itsLOMRelation;		
	}
	
	/**
	 * return true if this relation concerns a relationUI.
	 * 
	 * @param aSelectionableLOM
	 *            the a selectionable LOM
	 * 
	 * @return true, if concerns
	 */
	public boolean concerns(SelectionableLOM aSelectionableLOM){
		return (itsNode1 ==aSelectionableLOM) || (itsNode2 == aSelectionableLOM);
	}
	
	
	/**
	 * replace one of the extremity of the edge by a new one
	 * do nothing if aNewSelecitonableLOM is null
	 */
	public boolean replace(SelectionableLOM aSelectionableLOMtoReplace, SelectionableLOM aNewSelectionableLOM){
		if (aNewSelectionableLOM == null) return false;
		if (aSelectionableLOMtoReplace == itsNode1) {
			setNode1(aNewSelectionableLOM.getPNode());
			updateCurve();
			repaint();
			return true;
		}
		if (aSelectionableLOMtoReplace == itsNode2){
			setNode2(aNewSelectionableLOM.getPNode());
			updateCurve();
			repaint();
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * detach this edge from its parent detach also the associated motion node
	 * and ask if itsLOMRelation should also be detached.
	 */
	public void detach(){
		/*LOM theLOM1 = getLOMRelation().getSourceLOM();
		LOM theLOM2 = getLOMRelation().getTargetLOM();
		LOMAttribute theTitle= LOMAttribute.getLOMAttribute("general/title");
		int isOK = JOptionPane.showConfirmDialog(LessonMapper2.getInstance().getUI(),
				LessonMapper2.getInstance().getLangComment("removeRelation1")+ 
				getLOMRelation().getLOMRelationType().getName()+
				"\n"+ LessonMapper2.getInstance().getLangComment("removeRelation2")+ (theLOM1!=null?theTitle.getValueIn(theLOM1).getValue():"")+
				LessonMapper2.getInstance().getLangComment("removeRelation3")+(theLOM2!=null?theTitle.getValueIn(theLOM2).getValue():"")+" ? ",
				"Warning", JOptionPane.YES_NO_OPTION);
		detach(isOK==JOptionPane.OK_OPTION);*/
		detach(true);
	}
	
	/**
	 * detach this edge from its parent detach also the associated motion node
	 * and also itsLOMRelation if detachLOMRelation is true.
	 * 
	 * @param detachLOMRelation
	 *            the detach LOM relation
	 */
	public void detach(boolean detachLOMRelation){
		removeFromParent();
		itsMotionNode.removeFromParent();
		if (detachLOMRelation) itsLOMRelation.detach();
	}
	
	/**
	 * Gets the relation title UI.
	 * 
	 * @return the relation title UI
	 */
	public LOMRelationTitleUI getRelationTitleUI(){
		return (LOMRelationTitleUI) itsMotionNode;
	}
	
	/**
	 * if the paint is called but the lomrelation UI is detached then reattached
	 * the relation.
	 * 
	 * @param aPaintContext
	 *            the a paint context
	 */
	protected void paint(PPaintContext aPaintContext) {
		if (itsLOMRelation.isDetached()) itsLOMRelation.attach();
		super.paint(aPaintContext);
	}
	
	
	
}
