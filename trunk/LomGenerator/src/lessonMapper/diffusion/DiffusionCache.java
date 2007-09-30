/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.diffusion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import util.Couple;
import util.Pair;

/**
 * this class is reponsable for caching all the restrictions and suggestions
 * available in the current graph of LOMs.
 * 
 * For cache to work all new relation (or deletion) should be immediately
 * declare to the cache with the appropriate methods
 * 
 * Also change in the values of the lom should be declared to the cache
 * 
 * methods for declaring changes to the cache are synchronized
 * 
 * @author omotelet
 */

public class DiffusionCache {

	/**
	 * 
	 */
	static public boolean IsActive = false;

	/**
	 * 
	 * 
	 * @param aBool 
	 */
	static public void setActive(boolean aBool) {
		IsActive = aBool;
	}

	/**
	 * 
	 */
	public static String serializationFile = "diffusionState.data";

	/**
	 * 
	 */
	private static DiffusionCache ITSInstance = new DiffusionCache();

	/**
	 * 
	 * 
	 * @return 
	 */
	public static DiffusionCache getInstance() {
		return ITSInstance;
	}

	/**
	 * 
	 */
	Map<Couple<LOM, LOMAttribute>, LOMRestrictionSet> itsRestrictionCache = new HashMap<Couple<LOM, LOMAttribute>, LOMRestrictionSet>();

	// Map a l with the li on which l depends.
	/**
	 * 
	 */
	Map<LOM, Set<LOM>> itsAssociatedLOM = new HashMap<LOM, Set<LOM>>();

	/**
	 * 
	 */
	Map<Pair<LOM>, Integer> itsLOMRelationNbs = new HashMap<Pair<LOM>, Integer>();

	/**
	 * 
	 */
	int i = 0;

	/**
	 * 
	 */
	public void reset() {
		itsRestrictionCache.clear();
		itsAssociatedLOM.clear();
		itsLOMRelationNbs.clear();
	}

	/**
	 * declare the addition of a relation to the cache since relations are
	 * always double both sense are considered.
	 * 
	 * @param aLOM 
	 * @param aAssociatedLOM 
	 * 
	 * @return 
	 */
	public Set<LOM> declareNewAssociationFor(LOM aLOM, LOM aAssociatedLOM) {
		int j = i++;
		if (aLOM == null || aAssociatedLOM == null)
			return null;
		System.out.println("started new relation " + j);
		Set<LOM> theLOMs = itsAssociatedLOM.get(aLOM);
		if (theLOMs == null) {
			theLOMs = new LinkedHashSet<LOM>();
			itsAssociatedLOM.put(aLOM, theLOMs);
		}
		theLOMs.add(aAssociatedLOM);
		Set<LOM> theOtherLOMs = itsAssociatedLOM.get(aAssociatedLOM);
		if (theOtherLOMs == null) {
			theOtherLOMs = new LinkedHashSet<LOM>();
			itsAssociatedLOM.put(aAssociatedLOM, theOtherLOMs);
		}
		theOtherLOMs.add(aLOM);
		Pair<LOM> thePair = new Pair<LOM>(aLOM, aAssociatedLOM);
		Integer theRelationNb = itsLOMRelationNbs.get(thePair);
		if (theRelationNb == null)
			itsLOMRelationNbs.put(thePair, 1);
		else
			itsLOMRelationNbs.put(thePair, theRelationNb.intValue() + 1);
		Set<LOM> theUpdatedLOMs = updateRestrictionFor(aAssociatedLOM);
		if (theUpdatedLOMs != null)
			theUpdatedLOMs.addAll(updateRestrictionFor(aLOM));
		else
			theUpdatedLOMs = updateRestrictionFor(aLOM);
		System.out.println("end new relation " + j);
		return theUpdatedLOMs;
	}

	/**
	 * declare the deletion of a relation to the cache *.
	 * 
	 * @param aLOM 
	 * @param aAssociatedLOM 
	 * 
	 * @return 
	 */
	public synchronized Set<LOM> declareDeleteAssociationFor(LOM aLOM,
			LOM aAssociatedLOM) {
		int j = i++;
		System.out.println("started delete relation " + j);
		Set<LOM> theUpdatedLOMs = null;
		Set<LOM> theLOMs = itsAssociatedLOM.get(aLOM);
		Set<LOM> theOtherLOMs = itsAssociatedLOM.get(aAssociatedLOM);
		Integer thePairNb = itsLOMRelationNbs.get(new Pair<LOM>(aLOM,
				aAssociatedLOM));
		if (thePairNb != null && thePairNb.intValue() < 2) {
			if (theLOMs != null) {
				theLOMs.remove(aAssociatedLOM);
				theUpdatedLOMs = updateRestrictionFor(aAssociatedLOM);
			}
			if (theOtherLOMs != null) {
				theOtherLOMs.remove(aLOM);
				if (theUpdatedLOMs != null)
					theUpdatedLOMs.addAll(updateRestrictionFor(aLOM));
				else
					theUpdatedLOMs = updateRestrictionFor(aLOM);
			}
		}
		System.out.println("end delete relation " + j);
		return theUpdatedLOMs;
	}

	/**
	 * declare the modification of a relation to the cache *.
	 * 
	 * @param aLOM 
	 * @param aAssociatedLOM 
	 * 
	 * @return 
	 */
	public synchronized Set<LOM> declareModifiedAssociationFor(LOM aLOM,
			LOM aAssociatedLOM) {
		int j = i++;
		System.out.println("started modified relation " + j);
		Set<LOM> theUpdatedLOMs = updateRestrictionFor(aAssociatedLOM);
		if (theUpdatedLOMs != null)
			theUpdatedLOMs.addAll(updateRestrictionFor(aLOM));
		else
			theUpdatedLOMs = updateRestrictionFor(aLOM);
		System.out.println("end modified relation " + j);
		return theUpdatedLOMs;
	}

	/**
	 * declare the change of a value to the cache.
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public synchronized Set<LOM> declareValueChangeFor(LOM aLOM,
			LOMAttribute aAttribute) {
		int j = i++;
		System.out.println("started value update " + j);
		Set<LOM> theUpdatedLOMs = updateRestrictionFor(aLOM, aAttribute, true);
		System.out.println("end value update " + j);
		return theUpdatedLOMs;
	}

	/**
	 * return the restriction for aLOM and aAttribute if the restriction is not
	 * cached it updates the value.
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public LOMRestrictionSet getRestrictionFor(LOM aLOM, LOMAttribute aAttribute) {
		try {
			//System.out.println("I am using restriction cache"+itsRestrictionCache);
			LOMRestrictionSet theRestrictionSet = itsRestrictionCache
					.get(new Couple<LOM, LOMAttribute>(aLOM, aAttribute));
			if (theRestrictionSet != null)
				return theRestrictionSet.clone();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * return true if the value has change or false if do not change.
	 * 
	 * @param aLOM 
	 * @param aRestrictionSet 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	private boolean setRestrictionFor(LOM aLOM, LOMAttribute aAttribute,
			LOMRestrictionSet aRestrictionSet) {
		Couple<LOM, LOMAttribute> theCouple = new Couple<LOM, LOMAttribute>(
				aLOM, aAttribute);
		if (aLOM != null && aAttribute != null) {
			LOMRestrictionSet theRestrictionSet = itsRestrictionCache
					.get(theCouple);
			itsRestrictionCache.put(theCouple, aRestrictionSet);
			if (aRestrictionSet != null
					&& !aRestrictionSet.equals(theRestrictionSet))
				return true;
			if (aRestrictionSet == null && theRestrictionSet == null)
				return false;
		}
			return false;
	}

	/**
	 * call updateRestrictionFor on all the LOMs of aAssociatedLOMs.
	 * 
	 * @param aListOfUpdates 
	 * @param aAttribute 
	 * @param aAssociatedLOMs 
	 */
	private void updateRestrictionFor(Set<LOM> aAssociatedLOMs,
			LOMAttribute aAttribute, Set<LOM> aListOfUpdates) {
		if (aAssociatedLOMs != null)
			for (LOM aLOM : aAssociatedLOMs)
				if (aLOM != null && !aListOfUpdates.contains(aLOM))
					updateRestrictionFor(aLOM, aAttribute, aListOfUpdates,
							false);
	}

	/**
	 * update restriction for aLOM aAttribute calculate the new restriction and
	 * put it in the cache if the value has changed, it also updates all the
	 * associated lom and modify aListOfUpdates a list of Lom to avoid cycles.
	 * 
	 * @param aLOM 
	 * @param aListOfUpdates 
	 * @param aAttribute 
	 * @param concernValueChange 
	 */
	private void updateRestrictionFor(LOM aLOM, LOMAttribute aAttribute,
			Set<LOM> aListOfUpdates, boolean concernValueChange) {
		//TODO if (aLOM == null || aAttribute == null ) return;
		LOMRestrictionSet theRestriction = Diffusion.ResDif(aLOM, aAttribute);
		// System.out.println("calculate the restriction for "+
		// LOMAttribute.getLOMAttribute("general/title").getValueIn(aLOM) + "
		// "+aAttribute.getLabel() );
		if (setRestrictionFor(aLOM, aAttribute, theRestriction)
				|| concernValueChange) {
			// System.out.println("changes recorded" );

			aListOfUpdates.add(aLOM);
			updateRestrictionFor(itsAssociatedLOM.get(aLOM), aAttribute,
					aListOfUpdates);
		}
	}

	/**
	 * update restrictions for aLom and aAttribute isValueChange flag if this
	 * update concerns a change in the value of the LOM.
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 * @param concernValueChange 
	 * 
	 * @return 
	 */
	private Set<LOM> updateRestrictionFor(LOM aLOM, LOMAttribute aAttribute,
			boolean concernValueChange) {
		Set<LOM> theListOfUpdates = new LinkedHashSet<LOM>();
		updateRestrictionFor(aLOM, aAttribute, theListOfUpdates,
				concernValueChange);
		return theListOfUpdates;
	}

	/**
	 * update restrictions for aLom on all the aAttribute registered in
	 * LOMAttribute.
	 * 
	 * @param aLOM 
	 * 
	 * @return 
	 */
	private Set<LOM> updateRestrictionFor(LOM aLOM) {
		Set<LOM> theListOfUpdates = new LinkedHashSet<LOM>();
		for (LOMAttribute aAttribute : LOMAttribute.getRegisteredAttribute()) {
			Set<LOM> theUpdates = updateRestrictionFor(aLOM, aAttribute, false);
			if (theUpdates != null)
				theListOfUpdates.addAll(theUpdates);
		}
		return theListOfUpdates;
	}

	/**
	 * 
	 * 
	 * @param aPath 
	 */
	public void saveDiffusionState(String aPath) {
		try {
			// TODO save diffusion state
			ObjectOutputStream theStream = new ObjectOutputStream(
					new FileOutputStream(aPath + File.separator
							+ serializationFile));
			theStream.writeObject(itsAssociatedLOM);
			theStream.writeObject(itsRestrictionCache);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 
	 * @param aPath 
	 */
	@SuppressWarnings("unchecked")
	public void loadDiffusionState(String aPath) {
		try {

			ObjectInputStream theStream = new ObjectInputStream(
					new FileInputStream(aPath + File.separator
							+ serializationFile));
			itsAssociatedLOM = (Map<LOM, Set<LOM>>) theStream.readObject();
			itsRestrictionCache = (Map<Couple<LOM, LOMAttribute>, LOMRestrictionSet>) theStream
					.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * return a element based on the values of aCache.
	 * 
	 * @param aCache 
	 * 
	 * @return 
	 */
	public static Element makeXMLElement(DiffusionCache aCache) {
		// Map<Couple<LOM, LOMAttribute>, LOMRestrictionSet> itsRestrictionCache
		// = new HashMap<Couple<LOM, LOMAttribute>, LOMRestrictionSet>();

		// Map a l with the li on which l depends.
		// Map<LOM, Set<LOM>> itsAssociatedLOM = new HashMap<LOM, Set<LOM>>();

		Element theRoot = new Element("DiffusionCache");

		Element theAssociatedLOMList = new Element("AssociatedLOMList");
		for (Entry<LOM, Set<LOM>> theEntry : aCache.itsAssociatedLOM.entrySet()) {
			Element theAssociatedLOM = new Element("AssociatedLOM");
			Element theLOMElement = LOM.makeXMLElement(theEntry.getKey());
			if (theLOMElement != null) {
				theAssociatedLOM.addContent(theLOMElement);
				Element theSet = new Element("Set");
				for (LOM theLOM : theEntry.getValue()) {
					Element theInnerLOMElement = LOM.makeXMLElement(theLOM);
					if (theInnerLOMElement != null)
						theSet.addContent(LOM.makeXMLElement(theLOM));
				}
				theAssociatedLOM.addContent(theSet);
				theAssociatedLOMList.addContent(theAssociatedLOM);
			}
		}
		theRoot.addContent(theAssociatedLOMList);

		Element theLOMRelationNbList = new Element("LOMRelationNbList");
		for (Entry<Pair<LOM>, Integer> theEntry : aCache.itsLOMRelationNbs
				.entrySet()) {
			Pair<LOM> thePair = theEntry.getKey();
			Element thePairNbElement = new Element("LOMRelationNb");
			thePairNbElement.addContent(LOM.makeXMLElement(thePair.getOne()));
			thePairNbElement.addContent(LOM.makeXMLElement(thePair.getOther()));
			Element theNumber = new Element("Nb");
			theNumber.setText(theEntry.getValue().toString());
			thePairNbElement.addContent(theNumber);
			theLOMRelationNbList.addContent(thePairNbElement);
		}
		theRoot.addContent(theLOMRelationNbList);

		Element theRestrictionCacheList = new Element("RestrictionCacheList");
		for (Entry<Couple<LOM, LOMAttribute>, LOMRestrictionSet> theEntry : aCache.itsRestrictionCache
				.entrySet()) {
			Element theRestrictionCache = new Element("RestrictionCache");
			theRestrictionCache.addContent(LOM.makeXMLElement(theEntry.getKey()
					.getLeftElement()));
			theRestrictionCache.addContent(LOMAttribute.makeXMLElement(theEntry
					.getKey().getRightElement()));
			theRestrictionCache.addContent(LOMRestrictionSet
					.makeXMLElement(theEntry.getValue()));
			theRestrictionCacheList.addContent(theRestrictionCache);
		}
		theRoot.addContent(theRestrictionCacheList);

		return theRoot;
	}

	/**
	 * return aCache with the value stored in aElement. if aCache is null make a
	 * new diffusionCache;
	 * 
	 * @param aElement 
	 * @param aCache 
	 * 
	 * @return 
	 */
	public static DiffusionCache buildFromXMLElement(Element aElement,
			DiffusionCache aCache) {
		aCache.itsRestrictionCache = new HashMap<Couple<LOM, LOMAttribute>, LOMRestrictionSet>();
		aCache.itsAssociatedLOM = new HashMap<LOM, Set<LOM>>();

		Element theAssociatedLOMList = aElement.getChild("AssociatedLOMList");
		for (Iterator iter = theAssociatedLOMList.getChildren().iterator(); iter
				.hasNext();) {
			Element theAssociatedLOM = (Element) iter.next();
			if (theAssociatedLOM.getName().equals("AssociatedLOM")) {
				LOM theLOM = LOM.buildFromXMLElement(theAssociatedLOM
						.getChild("LOM"));
				Set<LOM> theLOMs = aCache.itsAssociatedLOM.get(theLOM);
				if (theLOMs == null) {
					theLOMs = new LinkedHashSet<LOM>();
					aCache.itsAssociatedLOM.put(theLOM, theLOMs);
				}
				for (Iterator iterator = theAssociatedLOM.getChild("Set")
						.getChildren().iterator(); iterator.hasNext();) {
					Element theOtherLOM = (Element) iterator.next();
					if (theOtherLOM.getName().equals("LOM")) {
						LOM theBuiltLOM = LOM.buildFromXMLElement(theOtherLOM);
						if (theBuiltLOM != null)
							theLOMs.add(theBuiltLOM);
					}
				}
			}
		}
		Element theLOMRelationNbList = aElement.getChild("LOMRelationNbList");
		for (Iterator iter = theLOMRelationNbList.getChildren().iterator(); iter
				.hasNext();) {
			Element thePairNbElement = (Element) iter.next();
			List theLOMs = thePairNbElement.getChildren("LOM");
			LOM theOneLOM = LOM.buildFromXMLElement((Element) theLOMs.get(0));
			LOM theOtherLOM = LOM.buildFromXMLElement((Element) theLOMs.get(1));
			Integer theNb = new Integer(thePairNbElement.getChildTextTrim("Nb"));
			aCache.itsLOMRelationNbs.put(new Pair<LOM>(theOneLOM, theOtherLOM),
					theNb);
		}

		Element theRestrictionCacheList = aElement
				.getChild("RestrictionCacheList");
		for (Iterator iter = theRestrictionCacheList.getChildren().iterator(); iter
				.hasNext();) {
			Element theRestrictionCache = (Element) iter.next();
			if (theRestrictionCache.getName().equals("RestrictionCache")) {
				LOM theLOM = LOM.buildFromXMLElement(theRestrictionCache
						.getChild("LOM"));
				LOMAttribute theAttribute = LOMAttribute
						.buildFromXMLElement(theRestrictionCache
								.getChild("LOMAttribute"));
				LOMRestrictionSet theRestrictionSet = LOMRestrictionSet
						.buildFromXMLElement(theRestrictionCache
								.getChild("LOMRestrictionSet"));
				if (theLOM == null || theAttribute == null)
					try {
						System.out
								.println("<--- Missing LOM or LOMAttribute in restriction cache");
						new XMLOutputter(Format.getPrettyFormat()).output(
								theRestrictionCache, System.out);
						System.out.println("--->");
					} catch (Exception e) {
						e.printStackTrace();
					}
				else
					aCache.setRestrictionFor(theLOM, theAttribute,
							theRestrictionSet);
			}
		}
		return aCache;
	}

	/**
	 * 
	 * 
	 * @param aElement 
	 * 
	 * @return 
	 */
	public static DiffusionCache buildFromXMLElement(Element aElement) {
		return buildFromXMLElement(aElement, new DiffusionCache());
	}

}
