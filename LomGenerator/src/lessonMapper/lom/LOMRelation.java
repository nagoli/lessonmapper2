/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.lom;

import java.rmi.dgc.VMID;

import lessonMapper.lom.util.LOMRelationBuffer;


import org.jdom.Element;

import diffuse.metadata.MetadataSetRelation;
import diffuse.metadata.MetadataSetRelationType;


/**
 * basic encapsulation of LOM relations An ID is aggragated in order to manage
 * graphical positioning
 * 
 * 
 * User: omotelet Date: Mar 17, 2005 Time: 12:40:35 PM.
 */
public class LOMRelation implements MetadataSetRelation{

	
	/**
	 * return the relation with ID anID contained in aLOM if anID is not found
	 * return null.
	 * 
	 * @param aLOM 
	 * @param anID 
	 * 
	 * @return 
	 */
	public static  LOMRelation getRelation(String anID, LOM aLOM) {
		return LOMRelationBuffer.getInstance().getRelation(anID, aLOM);
	}

	
	
	

	/**
	 * 
	 */
	protected String itsSourceLOMId, itsTargetLOMId;

	/**
	 * 
	 */
	protected LOM itsSourceLOM, itsTargetLOM;

	/**
	 * 
	 */
	protected LOMRelationType itsLOMRelationType = LOMRelationType
			.getLOMRelationType("notdefined");

	/**
	 * 
	 */
	protected LOMRelation itsContraryRelation;

	/**
	 * 
	 */
	protected Element itsValueElement;
	
	/**
	 * 
	 */
	protected Element itsMainElement, itsRootElement,
			itsIDElement, itsContraryIDElement;

	/**
	 * 
	 */
	protected boolean isDetached = false;

	/**
	 * create a new relation betweem aSourceLOM and aTargetLOM aggregate the
	 * object to the SourceXMLRepresentation and consider aContraryRelation as
	 * its contraryRelation.
	 * 
	 * @param aSourceLOM 
	 * @param aTargetLOM 
	 */
	public LOMRelation(LOM aSourceLOM, LOM aTargetLOM) {
		this(aSourceLOM, aTargetLOM, null);
		setContraryRelation(new LOMRelation(aTargetLOM, aSourceLOM, this));
	}

	/**
	 * create a new relation between aSourceLOM and aTargetLOM aggregate the
	 * object to the SourceXMLRepresentation and consider aContraryRelation as
	 * its contraryRelation.
	 * 
	 * @param aSourceLom 
	 * @param aContraryRelation 
	 * @param aTargetLom 
	 */
	private LOMRelation(LOM aSourceLom, LOM aTargetLom,
			LOMRelation aContraryRelation) {
		if (aSourceLom==null || aTargetLom == null) 
			System.out.println("I am creating a relation with a null lom ...");
		itsSourceLOM = aSourceLom;
		itsTargetLOM = aTargetLom;

		itsSourceLOMId = aSourceLom.getID();
		itsTargetLOMId = aTargetLom.getID();

		itsRootElement = (Element) itsSourceLOM.getNodes(
				LOM.ITSNameSpacePrefix + ":lom").get(0);
		// create the node
		itsMainElement = new Element("relation", LOM.ITSNameSpace);
		Element theKindElement = new Element("kind", LOM.ITSNameSpace);
		Element theValueElement = new Element("value", LOM.ITSNameSpace);
		itsValueElement = new Element("langstring", LOM.ITSNameSpace);
		itsMainElement.addContent(theKindElement.addContent(theValueElement
				.addContent(itsValueElement)));

		Element theResourceElement = new Element("resource", LOM.ITSNameSpace);
		Element theCatalogElement = new Element("catalogentry",
				LOM.ITSNameSpace);
		Element theEntryElement = new Element("entry", LOM.ITSNameSpace);
		Element theTargetIDElement = new Element("langstring", LOM.ITSNameSpace);
		theTargetIDElement.addContent(itsTargetLOMId);
		itsMainElement.addContent(theResourceElement
				.addContent(theCatalogElement.addContent(theEntryElement
						.addContent(theTargetIDElement))));
		itsContraryIDElement = new Element("ContraryID", LOM.ITSNameSpace);
		itsMainElement.addContent(itsContraryIDElement);
		itsRootElement.addContent(itsMainElement);
		initIDElement();
		LOMRelationBuffer.getInstance().registerLOMRelation(this);
//		setLOMRelationType(itsLOMRelationType);
		setContraryRelation(aContraryRelation);
	}

	/**
	 * create a LOMRelationObject with this relationElement.
	 * 
	 * @param aRelationElement 
	 * @param aSourceLom 
	 */
	public LOMRelation(LOM aSourceLom, Element aRelationElement) {
		itsSourceLOM = aSourceLom;
		itsRootElement = (Element) itsSourceLOM.getNodes(
				LOM.ITSNameSpacePrefix + ":lom").get(0);
		itsSourceLOMId = aSourceLom.getID();
		itsMainElement = aRelationElement;
		itsIDElement = itsMainElement.getChild("ID", itsMainElement
				.getNamespace());
		if (itsIDElement == null)
			initIDElement();
		itsValueElement = itsMainElement.getChild("kind",
				itsMainElement.getNamespace()).getChild("value",
				itsMainElement.getNamespace()).getChild("langstring",
				itsMainElement.getNamespace());
		itsLOMRelationType = LOMRelationType.getLOMRelationType(itsValueElement
				.getTextTrim());
		itsTargetLOMId = itsMainElement.getChild("resource",
				itsMainElement.getNamespace()).getChild("catalogentry",
				itsMainElement.getNamespace()).getChild("entry",
				itsMainElement.getNamespace()).getChildTextTrim("langstring",
				itsMainElement.getNamespace());
		itsTargetLOM = LOM.getLOM(itsTargetLOMId);
		if (aSourceLom==null || itsTargetLOM == null) 
			System.out.println("I am creating a relation with a null lom ... ");
		itsContraryIDElement = itsMainElement.getChild("ContraryID",
				itsMainElement.getNamespace());
		LOMRelationBuffer.getInstance().registerLOMRelation(this);
		if (itsTargetLOM != null) {
			if (itsContraryIDElement != null)
				itsContraryRelation = LOMRelationBuffer.getInstance().getRelation(itsContraryIDElement
						.getTextTrim(), itsTargetLOM);
			else
				itsContraryRelation = new LOMRelation(itsTargetLOM,
						itsSourceLOM, this);
		}
	}

	/**
	 * 
	 */
	public void initIDElement() {
		itsIDElement = new Element("ID", itsMainElement.getNamespace());
		itsIDElement.setText(new VMID().toString().trim());
		itsMainElement.addContent(itsIDElement);
	}

	/**
	 * 
	 * 
	 * @param aContraryRelation 
	 */
	private void setContraryRelation(LOMRelation aContraryRelation) {
		itsContraryRelation = aContraryRelation;
		if (itsContraryRelation != null) {
			if (itsContraryIDElement == null) {
				itsContraryIDElement = new Element("ContraryID",
						LOM.ITSNameSpace);
				itsMainElement.addContent(itsContraryIDElement);
			}
			itsContraryIDElement.setText(aContraryRelation.getID());
		}
		//check type consistency
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public String getID() {
		return itsIDElement.getTextTrim();
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public String getSourceLOMId() {
		return itsSourceLOMId;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public String getTargetLOMId() {
		return itsTargetLOMId;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public LOM getSourceLOM() {
		return itsSourceLOM;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public LOM getTargetLOM() {
		return itsTargetLOM;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public LOMRelationType getLOMRelationType() {
		return itsLOMRelationType;
	}

	public MetadataSetRelationType getRelationType(){
		return getLOMRelationType();
	}
	
	
	/**
	 * set the lomRelation to the type desined.
	 * 
	 * @param theRelationType 
	 */
	public void setLOMRelationType(String theRelationType) {
		setLOMRelationType(LOMRelationType.getLOMRelationType(theRelationType));
	}

	
	
	/**
	 * 
	 * 
	 * @param theRelationType 
	 */
	public void setLOMRelationType(LOMRelationType theRelationType) {
		itsLOMRelationType = theRelationType;
		itsValueElement.setText(theRelationType.getName());
		LOMRelationType theContraryRelationType = theRelationType.getContrary();
		if (itsContraryRelation != null) {
			itsContraryRelation.itsLOMRelationType = theContraryRelationType;
			itsContraryRelation.itsValueElement.setText(theContraryRelationType
					.getName());
		}
		LOMRelationBuffer.getInstance().rebuildLOMRelationTypeBufferFor(itsSourceLOM);
		LOMRelationBuffer.getInstance().rebuildLOMRelationTypeBufferFor(itsTargetLOM);
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public LOMRelation getContraryRelation() {
		return itsContraryRelation;
	}

	/**
	 * detach from its parent and call detach on contrary element.
	 */
	public void detach() {
		if (!isDetached()) {
			itsMainElement.detach();
			isDetached = true;
			LOMRelationBuffer.getInstance().unregisterLOMRelation(this);
			if (itsContraryRelation != null)
				itsContraryRelation.detach();
		}
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public boolean isDetached() {
		return isDetached;
	}

	/**
	 * 
	 */
	public void attach() {
		if (isDetached) {
			itsRootElement.addContent(itsMainElement);
			isDetached = false;
			if (itsContraryRelation != null)
				itsContraryRelation.attach();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "LOMRelation{"+ itsLOMRelationType + ":"+ itsSourceLOM + "--" + itsTargetLOM+"}";
//		return "LOMRelation{ itsID=" + getID() + " itsSourceLOMId='"
//				+ itsSourceLOMId + "'" + ", itsTargetLOMId='" + itsTargetLOMId
//				+ "'" + ", itsLOMRelationType=" + itsLOMRelationType + "}";
	}

}
