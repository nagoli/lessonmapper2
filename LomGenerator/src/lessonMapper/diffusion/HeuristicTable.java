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
package lessonMapper.diffusion;

import java.net.URL;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lessonMapper.diffusion.restriction.RestrictionHeuristicBuilder;
import diffuse.metadata.MetadataSetAttribute;
import diffuse.metadata.MetadataSetRelationType;

import org.jaxen.JaxenException;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * This class holds the heuristics definition A -> T -> H(heuristics) User:
 * omotelet Date: Apr 11, 2005 Time: 4:52:45 PM.
 */
public abstract class HeuristicTable {

	/**
	 * 
	 */
	public static URL ITSHeuristicsURL = HeuristicTable.class
			.getResource("resources/heuristics.xml");;
	static {
		try {
			System.out.println("theUrl" + ITSHeuristicsURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private static HeuristicTable INSTANCE = new LOMHeuristicTable();

	public static HeuristicTable getInstance() {
		if (INSTANCE == null)
			INSTANCE = new LOMHeuristicTable();
		return INSTANCE;
	}

	/**
	 * 
	 */
	Document itsDocument = null;

	/**
	 * 
	 */
	Hashtable<MetadataSetAttribute, Hashtable<MetadataSetRelationType, Set<Class>>> itsHashtable = new Hashtable<MetadataSetAttribute, Hashtable<MetadataSetRelationType, Set<Class>>>();

	/**
	 * 
	 */
	HeuristicTable() {
		init();
	}

	/**
	 * 
	 * 
	 * @param aHeuristic
	 * @param aRelationType
	 * @param aAttribute
	 * 
	 * @return
	 */
	public boolean put(MetadataSetAttribute aAttribute, MetadataSetRelationType aRelationType,
			Class aHeuristic) {
		return get(aAttribute, aRelationType).add(aHeuristic);
	}

	/**
	 * 
	 * 
	 * @param aHeuristic
	 * @param aRelationName
	 * @param aAttributeName
	 * 
	 * @return
	 */
	public abstract boolean put(String aAttributeName, String aRelationName,
			Class aHeuristic) ;
	/**
	 * 
	 * 
	 * @param aRelation
	 * @param aAttribute
	 * 
	 * @return
	 */
	public Set<Class> get(MetadataSetAttribute aAttribute, MetadataSetRelationType aRelation) {
		Hashtable<MetadataSetRelationType, Set<Class>> theRelationTable = itsHashtable
				.get(aAttribute);
		if (theRelationTable == null) {
			theRelationTable = new Hashtable<MetadataSetRelationType, Set<Class>>();
			itsHashtable.put(aAttribute, theRelationTable);
		}
		Set<Class> theHeuristicSet = theRelationTable.get(aRelation);
		if (theHeuristicSet == null) {
			theHeuristicSet = new HashSet<Class>();
			theRelationTable.put(aRelation, theHeuristicSet);
		}
		return theHeuristicSet;
	}

	/**
	 * * return true if there are some restrictions registered for aAttribute.
	 * 
	 * @param aAttribute
	 * 
	 * @return
	 */
	public boolean hasRestrictions(MetadataSetAttribute aAttribute) {
		Hashtable<MetadataSetRelationType, Set<Class>> theRelationTable = itsHashtable
				.get(aAttribute);
		if (theRelationTable == null || theRelationTable.isEmpty())
			return false;
		for (Set<Class> theHeuristics : theRelationTable.values()) {
			if (theHeuristics.isEmpty())
				continue;
			for (Class o : theHeuristics) {
				if (LOMRestrictionHeuristic.class.isAssignableFrom(o))
					return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * 
	 * @param aRelation
	 * @param aAttribute
	 * 
	 * @return
	 */
	public Set<Class> getSuggestionHeuristic(MetadataSetAttribute aAttribute,
			MetadataSetRelationType aRelation) {
		Set<Class> theSuggestionHeuristics = new LinkedHashSet<Class>();
		Set<Class> theHeuristics = get(aAttribute, aRelation);
		for (Class o : theHeuristics) {
			if (LOMSuggestionHeuristic.class.isAssignableFrom(o))
				theSuggestionHeuristics.add(o);
		}
		return theSuggestionHeuristics;
	}

	/**
	 * 
	 * 
	 * @param aRelation
	 * @param aAttribute
	 * 
	 * @return
	 */
	public Set<Class> getRestrictionHeuristic(MetadataSetAttribute aAttribute,
			MetadataSetRelationType aRelation) {
		Set<Class> theRestrictionHeuristics = new LinkedHashSet<Class>();
		Set<Class> theHeuristics = get(aAttribute, aRelation);
		for (Class o : theHeuristics) {
			if (LOMRestrictionHeuristic.class.isAssignableFrom(o))
				theRestrictionHeuristics.add(o);
		}
		return theRestrictionHeuristics;
	}

	/**
	 * this method contains the definition of the heuristics todo xml version.
	 */
	public void init() {
		buildHeuristicsDocument();
		buildHeuristicsTable();
	}

	/**
	 * 
	 */
	private void buildHeuristicsTable() {
		if (itsDocument != null) {

			/*
			 * try { JDOMXPath myXPath = new JDOMXPath("heuristic/suggestion");
			 * List result = myXPath.selectNodes(itsDocument); for (int i = 0; i <
			 * result.size(); i++) { Element theElement = (Element)
			 * result.get(i); String theAttribute =
			 * theElement.getAttribute("attribute") .getValue(); String
			 * theRelation = theElement.getAttribute("relation") .getValue();
			 * String theRule = theElement.getText(); put(theAttribute,
			 * theRelation, new SuggestionHeuristicBuilder(theAttribute,
			 * theRelation, theRule).getHeuristicClass()); } } catch
			 * (JaxenException e) { e.printStackTrace(); }
			 */

			/*
			 * try { JDOMXPath myXPath = new
			 * JDOMXPath("heuristic/defaultsuggestion"); List result =
			 * myXPath.selectNodes(itsDocument); Element theElement = (Element)
			 * result.get(0); String theRule = theElement.getText(); for
			 * (MetadataSetAttribute theAttribute : LOMRanking.getAttributeList()) for
			 * (MetadataSetRelationType theRelation : MetadataSetRelationType
			 * .getAvailableTypes()) put(theAttribute.getName(),
			 * theRelation.getName(), new
			 * SuggestionHeuristicBuilder(theAttribute .getName(),
			 * theRelation.getName(), theRule).getHeuristicClass()); } catch
			 * (JaxenException e) { e.printStackTrace(); }
			 */

			try {
				JDOMXPath myXPath = new JDOMXPath("heuristic/restriction");
				List result = myXPath.selectNodes(itsDocument);
				for (int i = 0; i < result.size(); i++) {
					Element theElement = (Element) result.get(i);
					String theAttribute = theElement.getAttribute("attribute")
							.getValue();
					String theRelation = theElement.getAttribute("relation")
							.getValue();
					String theRule = theElement.getText();
					Class theHeuristicClass = new RestrictionHeuristicBuilder(theAttribute,
							theRelation, theRule).getHeuristicClass();
					if (theHeuristicClass!=null) put(theAttribute, theRelation,
							theHeuristicClass);
				}
			} catch (JaxenException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	private void buildHeuristicsDocument() {
		try {
			SAXBuilder theBuilder = new SAXBuilder();
			URL theURL = ITSHeuristicsURL;
			itsDocument = theBuilder.build(theURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
