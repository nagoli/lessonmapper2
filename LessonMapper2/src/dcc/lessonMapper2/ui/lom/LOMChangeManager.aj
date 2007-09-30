/*
 * LessonMapper 2.
 Copyright (C) Olivier Motelet.

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package dcc.lessonMapper2.ui.lom;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMRelation;
import lessonMapper.lom.util.LOMElementTracer;
import lessonMapper.query.LOMRanking;

import org.jdom.Element;

import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.fileManager.LocalRepository;
import dcc.lessonMapper2.ui.eventHandler.SelectionBorderManager;
import dcc.lessonMapper2.ui.graph.element.ContainerNode;
import dcc.lessonMapper2.ui.graph.element.lom.GenericActivity;
import dcc.lessonMapper2.ui.graph.element.lom.SelectionableLOM;
import diffuse.metadata.MetadataSet;
import diffuse.models.res.DownResCMVHolderStorage;
import diffuse.models.res.UpResCMVHolderStorage;
import diffuse.models.res.rules.ResRule;
import diffuse.models.sug.SugCMVHolderStorage;
import diffuse.propagation.CMVDiffusion;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;

/**
 * this aspect is responsible for tracking and updating changes done on LOM
 * elements.
 * 
 * This aspect manage the modification done on LOM attribute so that the nodes
 * holding LOs can show the modification state of the associated LOMs.
 * Modification could be applied and the LOM object saved in the local
 * repository
 * 
 * It is responsible to update the view of the node and also the cache of
 * restriction values: - when a value of an attribute is saved - when a relation
 * is created or deleted
 * 
 * the cache is built when building the nodes therefore call to the restriction
 * values should not change the values of the cache since it is already complety
 * built.
 * 
 * 
 * 
 */

public aspect LOMChangeManager implements PInputEventListener {

	/**
	 * map LOM with a vector of registered Node for representing this LOM
	 */
	protected Map<LOM, Vector<SelectionableLOM>> itsRegisteredNodes = new HashMap<LOM, Vector<SelectionableLOM>>();

	/**
	 * map LOM with all LOMAttributeUI
	 */
	protected Map<LOM, Vector<LOMAttributeUI>> itsLOMAttributeUIMap = new HashMap<LOM, Vector<LOMAttributeUI>>();

	/**
	 * map LOM with modificated LOMAttributeUI
	 */
	protected Map<LOM, Vector<LOMAttributeUI>> itsLOMModifMap = new HashMap<LOM, Vector<LOMAttributeUI>>();

	/**
	 * map SelectionableLOM with a LOMChangeNotifierUI
	 */
	protected Map<SelectionableLOM, LOMChangeNotifierUI> itsLOMChangeNotifierMap = new HashMap<SelectionableLOM, LOMChangeNotifierUI>();

	/**
	 * track the nodes that refered to a LOM object and register them to
	 * itsRegisteredNodes. It also initializes the LOMModifMap for that LOM (if
	 * it does not already exist)
	 */
	void around(LOM aNewLOM): execution(void SelectionableLOM+.setLOM(LOM)) && args(aNewLOM) {

		SelectionableLOM theNode = (SelectionableLOM) thisJoinPoint.getTarget();
		LOM theExLOM = theNode.getLOM();
		proceed(aNewLOM);
		Vector<SelectionableLOM> theNodes;
		// check if already registered with theExLOM then remove
		if (theExLOM != null && itsRegisteredNodes.containsKey(theExLOM))
			itsRegisteredNodes.get(theExLOM).remove(theNode);
		if (itsRegisteredNodes.containsKey(aNewLOM))
			theNodes = itsRegisteredNodes.get(aNewLOM);
		else {
			theNodes = new Vector<SelectionableLOM>();
			itsRegisteredNodes.put(aNewLOM, theNodes);
		}
		theNodes.add(theNode);
		// System.out.println(itsLOMModifMap.containsKey(aNewLOM));
		if (!itsLOMModifMap.containsKey(aNewLOM)) {
			itsLOMModifMap.put(aNewLOM, new Vector<LOMAttributeUI>());
		}
		// setColor of the title
		theNode.setTitleColor(SelectionBorderManager.getInstance().getLOMColor(
				aNewLOM).getRightElement());
	}

	/**
	 * when anAttributeUI is created inside a LOMAttributePanel this UI is
	 * registered in the itsLOMAttributeUIMap
	 */
	after(LOM aLOM) returning (LOMAttributeUI theUI): 
		call(LOMAttributeUI+.new(LOM,..)) && args(aLOM,..) && within(LOMAttributePanel) {
		if (!itsLOMAttributeUIMap.containsKey(aLOM)) {
			itsLOMAttributeUIMap.put(aLOM, new Vector<LOMAttributeUI>());
		}
		Vector<LOMAttributeUI> theUIs = itsLOMAttributeUIMap.get(aLOM);
		theUIs.add(theUI);
	}

	/**
	 * when a LOM element is changed from inside a material node or an activity
	 * associated LOMAttributeUI are canceled which means that the newvalue of
	 * the element is displayed
	 * 
	 * (for example when a material is attached to a material node, the UI
	 * correponding to the material location and is updated)
	 * 
	 */
	after(): call(Element Element+.setText(..)) && 
					within(SelectionableLOM+)
					&& !cflow(execution(Element GenericActivity.getXMLSerialization()))
				// && !withincode(Element GenericActivity.getXMLSerialization())
					{
		Element theElement = (Element) thisJoinPoint.getTarget();
		LOM theLOM = LOMElementTracer.getLOMFor(theElement);
		LOMAttribute theAttribute = LOMElementTracer
				.getLOMAttributeFor(theElement);
		if (itsLOMAttributeUIMap.containsKey(theLOM)) {
			for (LOMAttributeUI theUI : itsLOMAttributeUIMap.get(theLOM)) {
				if (theUI.getLOMAttribute() == theAttribute) {
					theUI.cancel();
				}
			}
		}
	}

	/*
	 * after(): call(Element Element+.setText(..)) && within(SelectionableLOM+) &&
	 * !withincode(Element GenericActivity.getXMLSerialization()) &&
	 * cflow(execution(Element GenericActivity.getXMLSerialization())) {
	 * System.out.println("oups"); }
	 */

	/**
	 * 
	 * change LOMModifMap when a LOM is modified in a LOMAttributeUI; update the
	 * associated Nodes UI when this Node is called.
	 * 
	 */
	before(boolean newval): set(boolean LOMAttributeUI+.isModified) 
	&& args(newval)
	&& !cflow(execution(LOMAttributeUI+.new(LOM,..))){
		LOMAttributeUI theUI = (LOMAttributeUI) thisJoinPoint.getTarget();
		LOM theLOM = theUI.getLOM();
		Vector<LOMAttributeUI> theModifiedUI = itsLOMModifMap.get(theLOM);
		if (theModifiedUI != null) {
			if (newval) {
				boolean iconHasToBeAdded = theModifiedUI.isEmpty();
				theModifiedUI.add(theUI);
				if (iconHasToBeAdded)
					updateNodeUI(theLOM, true);
			} else {
				theModifiedUI.remove(theUI);
				boolean iconHasToBeRemoved = theModifiedUI.isEmpty();
				if (iconHasToBeRemoved)
					updateNodeUI(theLOM, false);
			}
		}
	}

	/**
	 * add an icon to the nodes refering to aLOM if aBool is true; remove this
	 * icon when false;
	 */
	public void updateNodeUI(LOM aLOM, boolean isIconAdded) {
		if (aLOM != null) {
			Vector<SelectionableLOM> theNodes = itsRegisteredNodes.get(aLOM);
			for (SelectionableLOM theSelectionableLOM : theNodes) {
				if (theSelectionableLOM instanceof PNode) {
					PNode theNode = (PNode) theSelectionableLOM;
					LOMChangeNotifierUI theNotifier;
					if (itsLOMChangeNotifierMap
							.containsKey(theSelectionableLOM))
						theNotifier = itsLOMChangeNotifierMap
								.get(theSelectionableLOM);
					else {
						theNotifier = new LOMChangeNotifierUI(aLOM);
						itsLOMChangeNotifierMap.put(theSelectionableLOM,
								theNotifier);
						if (theNode instanceof ContainerNode) {
							ContainerNode theContainer = (ContainerNode) theNode;
							theContainer.addChildWithoutScaling(theNotifier);
							theContainer.addAlwaysPickableNode(theNotifier);
						} else {
							theNode.addChild(theNotifier);
						}
						theNotifier.setOffset(theNode.getWidth()
								- theNotifier.getWidth(), theNode.getHeight()
								- theNotifier.getHeight());
						theNotifier.addInputEventListener(this);
					}
					if (isIconAdded) {
						theNotifier.setVisible(true);
						theNotifier.setPickable(true);
					} else {
						theNotifier.setVisible(false);
						theNotifier.setPickable(false);
					}
					theNode.repaint();
				}

			}
		}
	}

	public void processEvent(PInputEvent aEvent, int type) {
		if (type == MouseEvent.MOUSE_CLICKED
				&& aEvent.getButton() == MouseEvent.BUTTON1
				&& aEvent.getClickCount() == 2) {
			PNode theNode = aEvent.getPickedNode();
			if (theNode instanceof LOMChangeNotifierUI) {
				LOMChangeNotifierUI theNotifier = (LOMChangeNotifierUI) theNode;
				LOM theLOM = theNotifier.getLOM();
				Vector<LOMAttributeUI> theUIs = new Vector<LOMAttributeUI>(
						itsLOMModifMap.get(theLOM));
				for (LOMAttributeUI theAttributeUI : theUIs) {
					theAttributeUI.saveChanges();
				}
				aEvent.setHandled(true);
			}
		}
	}

	pointcut loadProject(LocalRepository aRepository):
		execution(public GenericActivity LocalRepository.loadUpperActivity()) 
		&& target(aRepository);

	pointcut isLoadingProject(LocalRepository aRepository): 
		cflow(loadProject(aRepository))
		&& if (aRepository != null);// && if(aRepository.isLoadingCache);

	/**
	 * avoid validationUI update when loading project cache (the updateAllView()
	 * method of the upperactivity should afterwards be called )
	 */
	void around():
		execution(public void LOMValidityUI.update()) 
		&& isLoadingProject(LocalRepository)
	{
		// System.out.println("I am loadingProject");
	}

	/**
	 * when a new project is loaded all the nodes are updated after all elements
	 * have being loaded
	 */
	after() returning(GenericActivity aActivity): loadProject(LocalRepository)
	{
		LessonMapper2.getInstance().giveToWorker(new UpdateAll(aActivity));
	}

	/**
	 * when a LOM element is changed from inside a material node or an activity,
	 * cache is updated and the views of all changed nodes are updated
	 * 
	 */
	after():
		call(Element Element+.setText(..)) && within(SelectionableLOM+) 
		&& !cflow(execution(Element GenericActivity.getXMLSerialization()))
	// && !withincode(Element GenericActivity.getXMLSerialization())
		&& !isLoadingProject(LocalRepository)
	{
		Element theElement = (Element) thisJoinPoint.getTarget();
		LOM theLOM = LOMElementTracer.getLOMFor(theElement);
		LOMAttribute theAttribute = LOMElementTracer
				.getLOMAttributeFor(theElement);
		if (theLOM != null && theAttribute != null)
			LessonMapper2.getInstance().giveToWorker(
					new UpdateValueChange(theLOM, theAttribute));
	}

	/**
	 * when a LOM element is modified in a LOMAttributeUI and changes are saved
	 * cache is updated the views of all changed nodes are updated
	 */
	after(): execution(boolean LOMAttributeUI+.saveChanges()) 
	&& !isLoadingProject(LocalRepository) 
    {
		LOMAttributeUI theAttributeUI = (LOMAttributeUI) thisJoinPoint
				.getTarget();
		LOM theLOM = theAttributeUI.getLOM();
		LOMAttribute theAttribute = theAttributeUI.getLOMAttribute();
		if (theLOM != null && theAttribute != null)
			LessonMapper2.getInstance().giveToWorker(
					new UpdateValueChange(theLOM, theAttribute));

	}

	/**
	 * catch the use of lomrelation inside an exisiting lom document (used in
	 * buildFromXmLDocument of activity)
	 */
	after() returning (LOMRelation theRelation): 
		call( LOMRelation LOMRelation.getRelation(String,LOM)) 
		&& !isLoadingProject(LocalRepository) 
	{
		if (theRelation != null) {
			LessonMapper2.getInstance().giveToWorker(
					new UpdateNewRelation(theRelation));

		}
	}

	/**
	 * catch the creatin of new lomrelation
	 */
	after() returning (LOMRelation theRelation) :
		call( LOMRelation.new(..)) 
		&& !isLoadingProject(LocalRepository) 
	{
		LessonMapper2.getInstance().giveToWorker(
				new UpdateNewRelation(theRelation));

	}

	void around() : 
		call(void LOMRelation.attach()) 
		&& !isLoadingProject(LocalRepository)
	{
		LOMRelation theRelation = (LOMRelation) thisJoinPoint.getTarget();
		if (theRelation.isDetached()) {
			proceed();
			LessonMapper2.getInstance().giveToWorker(
					new UpdateNewRelation(theRelation));
		} else
			proceed();

	}

	after() : 
		call(void LOMRelation.detach())
		&& !isLoadingProject(LocalRepository) 
	{
		LOMRelation theRelation = (LOMRelation) thisJoinPoint.getTarget();
		LessonMapper2.getInstance().giveToWorker(
				new UpdateDeleteRelation(theRelation));

	}

	after() :
		call(void LOMRelation.setLOMRelationType(..)) 
		&& !isLoadingProject(LocalRepository) 
	{
		LOMRelation theRelation = (LOMRelation) thisJoinPoint.getTarget();
		LessonMapper2.getInstance().giveToWorker(
				new UpdateModifiedRelation(theRelation));

	}

	public void updateViews(Set<MetadataSet> theLOMs) {
		if (theLOMs == null)
			return;
		for (MetadataSet theLOM : theLOMs)
			if (theLOM instanceof LOM)
				updateView((LOM) theLOM);
	}

	public void updateView(LOM aLOM) {
		Vector<SelectionableLOM> theNodes = itsRegisteredNodes.get(aLOM);
		if (theNodes != null && !theNodes.isEmpty()) {
			for (SelectionableLOM theNode : theNodes) {
				theNode.updateView();
			}
		}
	}

	class UpdateValueChange implements Runnable {
		LOM itsLOM;

		LOMAttribute itsAttribute;

		UpdateValueChange(LOM aLOM, LOMAttribute aAttribute) {
			if (aLOM == null || aAttribute == null)
				System.out.println("null lom or attribute");
			itsLOM = aLOM;
			itsAttribute = aAttribute;
		}

		public void run() {
			// FixPointValueDiffusion.diffuseChangesOf(itsLOM,
			// SugDifValueHolder.getInstance(itsAttribute));
			List<LOM> theLOM = new ArrayList<LOM>();
			theLOM.add(itsLOM);
			CMVDiffusion.diffuseChangesOf(itsLOM, SugCMVHolderStorage
					.getInstance().getCMVHolderCollectionForUpdate(
							itsAttribute, theLOM));
			// if (HeuristicTable.getInstance().hasRestrictions(itsAttribute)){
			// FixPointValueDiffusion.diffuseChangesOf(itsLOM,
			// ResDifValueHolder.getInstance(itsAttribute));
			Set<MetadataSet> theUpdatedLOMs = new HashSet<MetadataSet>();
			if (ResRule.hasDownResRule(itsAttribute))
				theUpdatedLOMs.addAll(CMVDiffusion.diffuseChangesOf(itsLOM,
						DownResCMVHolderStorage.getInstance().getHolderFor(
								itsAttribute)));
			if (ResRule.hasUpResRule(itsAttribute))
				theUpdatedLOMs.addAll(CMVDiffusion.diffuseChangesOf(itsLOM,
						UpResCMVHolderStorage.getInstance().getHolderFor(
								itsAttribute)));
			updateViews(theUpdatedLOMs);

		}

	}

	class UpdateNewRelation implements Runnable {
		LOMRelation itsRelation;

		UpdateNewRelation(LOMRelation aRelation) {
			itsRelation = aRelation;
		}

		public void run() {
			List<LOM> theLOMs = new ArrayList<LOM>();
			theLOMs.add(itsRelation.getSourceLOM());
			theLOMs.add(itsRelation.getTargetLOM());
			Set<MetadataSet> theUpdatedLOMs = new HashSet<MetadataSet>();
			for (LOMAttribute theAttribute : LOMRanking.getAttributeList()) {
				// FixPointValueDiffusion.diffuseChangesOf(theLOMs,SugDifValueHolder.getInstance(theAttribute));
				CMVDiffusion.diffuseChangesOf(theLOMs, SugCMVHolderStorage
						.getInstance().getCMVHolderCollectionForUpdate(
								theAttribute, theLOMs), false);
				// if
				// (HeuristicTable.getInstance().hasRestrictions(theAttribute))
				// FixPointValueDiffusion.diffuseChangesOf(theLOMs,ResDifValueHolder.getInstance(theAttribute));
				if (ResRule.hasDownResRule(theAttribute))
					theUpdatedLOMs.addAll(CMVDiffusion.diffuseChangesOf(
							theLOMs, DownResCMVHolderStorage.getInstance()
									.getHolderFor(theAttribute), false));
				if (ResRule.hasUpResRule(theAttribute))
					theUpdatedLOMs.addAll(CMVDiffusion.diffuseChangesOf(
							theLOMs, UpResCMVHolderStorage.getInstance()
									.getHolderFor(theAttribute), false));
			}
			updateViews(theUpdatedLOMs);
		}
	}

	class UpdateDeleteRelation implements Runnable {
		LOMRelation itsRelation;

		UpdateDeleteRelation(LOMRelation aRelation) {
			itsRelation = aRelation;
		}

		public void run() {
			List<LOM> theLOMs = new ArrayList<LOM>();
			theLOMs.add(itsRelation.getSourceLOM());
			theLOMs.add(itsRelation.getTargetLOM());
			Set<MetadataSet> theUpdatedLOMs = new HashSet<MetadataSet>();
			for (LOMAttribute theAttribute : LOMRanking.getAttributeList()) {
				// FixPointValueDiffusion.diffuseChangesOf(theLOMs,SugDifValueHolder.getInstance(theAttribute));
				CMVDiffusion.diffuseChangesOf(theLOMs, SugCMVHolderStorage
						.getInstance().getCMVHolderCollectionForUpdate(
								theAttribute, theLOMs), false);
				// if
				// (HeuristicTable.getInstance().hasRestrictions(theAttribute))
				// FixPointValueDiffusion.diffuseChangesOf(theLOMs,ResDifValueHolder.getInstance(theAttribute));
				if (ResRule.hasDownResRule(theAttribute))
					theUpdatedLOMs.addAll(CMVDiffusion.diffuseChangesOf(
							theLOMs, DownResCMVHolderStorage.getInstance()
									.getHolderFor(theAttribute), false));
				if (ResRule.hasUpResRule(theAttribute))
					theUpdatedLOMs.addAll(CMVDiffusion.diffuseChangesOf(
							theLOMs, UpResCMVHolderStorage.getInstance()
									.getHolderFor(theAttribute), false));
			}
			updateViews(theUpdatedLOMs);
		}
	}

	class UpdateModifiedRelation implements Runnable {
		LOMRelation itsRelation;

		UpdateModifiedRelation(LOMRelation aRelation) {
			itsRelation = aRelation;
		}

		public void run() {
			Set<MetadataSet> theUpdatedLOMs = new HashSet<MetadataSet>();
			List<LOM> theLOMs = new ArrayList<LOM>();
			theLOMs.add(itsRelation.getSourceLOM());
			theLOMs.add(itsRelation.getTargetLOM());
			for (LOMAttribute theAttribute : LOMRanking.getAttributeList()) {
				//FixPointValueDiffusion.diffuseChangesOf(theLOMs,
				//		SugDifValueHolder.getInstance(theAttribute));
				CMVDiffusion.diffuseChangesOf(theLOMs, SugCMVHolderStorage
						.getInstance().getCMVHolderCollectionForUpdate(
								theAttribute, theLOMs), false);
				// if
				// (HeuristicTable.getInstance().hasRestrictions(theAttribute))
				// FixPointValueDiffusion.diffuseChangesOf(theLOMs,ResDifValueHolder.getInstance(theAttribute));
				if (ResRule.hasDownResRule(theAttribute))
					theUpdatedLOMs.addAll(CMVDiffusion.diffuseChangesOf(
							theLOMs, DownResCMVHolderStorage.getInstance()
									.getHolderFor(theAttribute), false));
				if (ResRule.hasUpResRule(theAttribute))
					theUpdatedLOMs.addAll(CMVDiffusion.diffuseChangesOf(
							theLOMs, UpResCMVHolderStorage.getInstance()
									.getHolderFor(theAttribute), false));
			}
			updateViews(theUpdatedLOMs);
		}
	}

	class UpdateAll implements Runnable {
		GenericActivity itsActivity;

		UpdateAll(GenericActivity aActivity) {
			itsActivity = aActivity;
		}

		public void run() {
			if (itsActivity == null)
				return;
			Set<LOM> theLOMs = itsActivity.getAllChildrenLOM();
			for (LOMAttribute theAttribute : LOMRanking.getAttributeList()) {
				//FixPointValueDiffusion.diffuseChangesOf(theLOMs,
				//		SugDifValueHolder.getInstance(theAttribute));
				CMVDiffusion.diffuseChangesOf(theLOMs, SugCMVHolderStorage
						.getInstance().getCMVHolderCollectionForUpdate(
								theAttribute, theLOMs), true);
				// if
				// (HeuristicTable.getInstance().hasRestrictions(theAttribute))
				// FixPointValueDiffusion.diffuseChangesOf(theLOMs,ResDifValueHolder.getInstance(theAttribute));
				if (ResRule.hasDownResRule(theAttribute))
					CMVDiffusion.diffuseChangesOf(theLOMs,
							DownResCMVHolderStorage.getInstance().getHolderFor(
									theAttribute), true);
				if (ResRule.hasUpResRule(theAttribute))
					CMVDiffusion.diffuseChangesOf(theLOMs,
							UpResCMVHolderStorage.getInstance().getHolderFor(
									theAttribute), true);
			}
			itsActivity.updateAllView();
		}
	}

}
