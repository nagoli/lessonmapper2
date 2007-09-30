/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package dcc.lessonMapper2.fileManager;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lor.LOR;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import util.Couple;
import dcc.lessonMapper2.LMProject;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.graph.element.lom.GenericActivity;
import dcc.lessonMapper2.ui.graph.element.lom.GenericMaterial;
import dcc.lessonMapper2.ui.graph.element.lom.SelectionableLOM;
import edu.umd.cs.piccolo.PNode;

/**
 * lomManager is responsible for the loading and saving of LOM archives. LOM
 * archives are created with the ID of the embedded LOMs Activities are saved in
 * a directory related to the ID.
 * 
 * LocalRepository keep a list of exisiting material.
 * 
 * @author omotelet
 */

public class LocalRepository implements Serializable {
	
	
	public static void testSignedInner(){
		System.out.println("from other package");
		 LessonMapper2.getInstance().new SaveProjectAsTask(null);
	}
	
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Archive path end. */
	static String ArchivePathEnd = File.separator;

	/** The Constant PropertyFile. */
	final public static String PropertyFile = "property.data";

	/** The Constant CacheFile. */
	final public static String CacheFile = "cache.data";
	
	/** The Constant newRefID. */
	public final static String newRefID = "newRef";

	/** The its LM project. */
	protected LMProject itsLMProject;

	/** The its existing LO ms. */
	protected Map<String, Reference> itsExistingLOMs;

	/** The its existing activities. */
	protected Map<String, Reference> itsExistingActivities;

	/** The its saved activities. */
	protected Set<String> itsSavedActivities;
	
	/** The is loading cache. */
	public boolean isLoadingCache = false;

	/**
	 * The Constructor.
	 * 
	 * @param aProject
	 *            the a project
	 */
	@SuppressWarnings("unchecked")
	public LocalRepository(LMProject aProject) {
		initExisitingDocuments();
		itsLMProject = aProject;
	}

	/**
	 * Inits the exisiting documents.
	 */
	private void initExisitingDocuments() {
		itsExistingLOMs = new HashMap<String, Reference>();
		itsExistingActivities = new HashMap<String, Reference>();
		itsSavedActivities = new HashSet<String>();
	}

	/**
	 * build and save the property and cache files for this project.
	 */
	public void savePropertyAndCacheFiles() {
		try {
			//build property file
			Element theRoot = new Element("LessonMapperProject");
			Element theUpperActivity = new Element("UpperActivity");
			theUpperActivity.setText(itsLMProject.getUpperActivity().getID());
			theRoot.addContent(theUpperActivity);
			Document thePropertyDocument = new Document(theRoot);
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(getPropertyFile()), "UTF-8");
			new XMLOutputter(Format.getPrettyFormat()).output(
					thePropertyDocument, out);
			//build Cache file
			/*Element theCacheRoot = new Element("LessonMapperCache");
			theCacheRoot.addContent(DiffusionCache.makeXMLElement(DiffusionCache.getInstance()));
			theCacheRoot.addContent(ValidationCache.makeXMLElement(ValidationCache.getInstance()));
			OutputStreamWriter cacheOut = new OutputStreamWriter(
					new FileOutputStream(getCacheFile()), "UTF-8");
			new XMLOutputter(Format.getPrettyFormat()).output(
					new Document(theCacheRoot), cacheOut);*/
	} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * associate the references contained in aMap.
	 * 
	 * @param aElement
	 *            the a element
	 * @param aMap
	 *            the a map
	 */
	public void associateToElement(Element aElement, Map<String, Reference> aMap) {
		for (Reference theReference : aMap.values()) {
			aElement.addContent(theReference.toXML());
		}
	}

	/**
	 * Save on repository.
	 * 
	 * @param anActivity
	 *            the an activity
	 * 
	 * @return true, if save on repository
	 */
	public boolean saveOnRepository(GenericActivity anActivity) {
		return saveActivity(anActivity, true);
	}

	/**
	 * Gets the material location for ID.
	 * 
	 * @param aMaterialID
	 *            the a material ID
	 * 
	 * @return the material location for ID
	 */
	public String getMaterialLocationForID(String aMaterialID) {
		return getMaterialLocationForID(aMaterialID, getArchivePath());
	}

	/**
	 * Gets the material location for ID.
	 * 
	 * @param aArchivePath
	 *            the a archive path
	 * @param aMaterialID
	 *            the a material ID
	 * 
	 * @return the material location for ID
	 */
	public String getMaterialLocationForID(String aMaterialID,
			String aArchivePath) {
		return aArchivePath + ArchivePathEnd + getPathFromID(aMaterialID)
				+ File.separator;
	}
	
	
	
	/**
	 * Gets the material thumb for ID.
	 * 
	 * @param aMaterialID
	 *            the a material ID
	 * 
	 * @return the material thumb for ID
	 */
	public String getMaterialThumbForID(String aMaterialID) {
		return getMaterialThumbForID(aMaterialID, getArchivePath());
	}

	/**
	 * Gets the material thumb for ID.
	 * 
	 * @param aArchivePath
	 *            the a archive path
	 * @param aMaterialID
	 *            the a material ID
	 * 
	 * @return the material thumb for ID
	 */
	public String getMaterialThumbForID(String aMaterialID, String aArchivePath) {
		return getMaterialLocationForID(aMaterialID, aArchivePath)
				+ "thumb.jpg";
	}

	/**
	 * save the LOM object held by aSelectionableLOM and set the modification
	 * date to the current time.
	 * 
	 * @param aSelectionableLOM
	 *            the a selectionable LOM
	 * 
	 * @return true, if save LOM
	 */
	public boolean saveLOM(SelectionableLOM aSelectionableLOM) {
		LOM theLOM = aSelectionableLOM.getLOM();
		String theID = theLOM.getID();
		// TODO manage LOM contributributionelement
		// LOMAttribute theDate =
		// LOMAttribute.getLOMAttribute("lifecycle/contribute");
		// Element theDateElement = (Element) theDate.getNodesIn(aLOM);
		// theDateElement.setText(new Date().toString());
		File file = new File(getArchivePath() + ArchivePathEnd+ getPathFromID(theID) + ".xml");
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8");
			new XMLOutputter(Format.getPrettyFormat()).output(theLOM
					.getXMLModel(), out);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		LOMAttribute theTitleAttribute = LOMAttribute
				.getLOMAttribute("general/title");
		String theTitle = theTitleAttribute.getValueIn(theLOM).getValue();

		if (itsExistingLOMs.containsKey(theID)) {
			Reference theRef = itsExistingLOMs.get(theID);
			theRef.setName(theTitle);
			theRef.setModificationDate(new Date());
		} else {
			itsExistingLOMs.put(theID, new Reference(theID, theTitle,
					new Date(), getMaterialThumbForID(theLOM.getMaterialID())));
		}
		return true;
	}

	/**
	 * save the activity passed in parameters and its children isTopActivity is
	 * true if this activity should be added to the savedActivity list.
	 * 
	 * @param isTopActivity
	 *            the is top activity
	 * @param anActivity
	 *            the an activity
	 * 
	 * @return true, if save activity
	 */
	public boolean saveActivity(GenericActivity anActivity,
			boolean isTopActivity) {
		LOM theLOM = anActivity.getLOM();
		File file = new File(anActivity.getFileLocation());
		File theThumb = new File(getMaterialThumbForID(theLOM.getMaterialID()));
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8");
			new XMLOutputter(Format.getPrettyFormat()).output(anActivity
					.getXMLSerialization(), out);
			RenderedImage theImage = (RenderedImage) (anActivity.toImage());
			ImageIO.write(theImage, "jpeg", theThumb);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		String theID = theLOM.getID();
		LOMAttribute theTitleAttribute = LOMAttribute
				.getLOMAttribute("general/title");
		String theTitle = theTitleAttribute.getValueIn(theLOM).getValue();
		if (itsExistingActivities.containsKey(theID)) {
			Reference theRef = itsExistingActivities.get(theID);
			theRef.setName(theTitle);
			theRef.setModificationDate(new Date());
		} else {
			itsExistingActivities.put(theID, new Reference(theID, theTitle,
					new Date(), theThumb.getAbsolutePath()));
		}
		if (isTopActivity) {
			itsSavedActivities.add(theID);
		}
		if (!saveLOM(anActivity))
			return false;
		boolean state = true;
		for (Iterator iter = anActivity.getChildrenIterator(); iter.hasNext();) {
			PNode element = (PNode) iter.next();
			if (element instanceof GenericActivity) {
				state = state && saveActivity((GenericActivity) element, false);
			} else if (element instanceof GenericMaterial) {
				state = state && saveMaterial((GenericMaterial) element);
			}
		}

		return state;
	}

	/**
	 * save the material passed in parameters and its thumb.
	 * 
	 * @param aMaterial
	 *            the a material
	 * 
	 * @return true, if save material
	 */
	public boolean saveMaterial(GenericMaterial aMaterial) {
		LOM theLOM = aMaterial.getLOM();
		File theThumb = new File(getMaterialThumbForID(theLOM.getMaterialID()));
		theThumb.getParentFile().mkdirs();
		try {
			theThumb.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			RenderedImage theImage = (RenderedImage) aMaterial.getThumbImage();
			if (theImage != null)
				ImageIO.write(theImage, "jpeg", theThumb);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return saveLOM(aMaterial);
	}

	/**
	 * Gets the path from ID.
	 * 
	 * @param aID
	 *            the a ID
	 * 
	 * @return the path from ID
	 */
	public String getPathFromID(String aID) {
		String thePath = "";
		int i = 0;
		for (; i + 10 < aID.length(); i += 10) {
			thePath += aID.substring(i, i + 10).replace('-', 'M').replace(':',
					File.separatorChar);
		}
		thePath += aID.substring(i, aID.length()).replace('-', 'M').replace(
				':', File.separatorChar);
		return thePath;
	}

	/**
	 * Load LOM.
	 * 
	 * @param anID
	 *            the an ID
	 * 
	 * @return the LOM
	 */
	public LOM loadLOM(String anID) {
		// if (itsExistingLOMs.containsKey(anID)){
		try {
			// return new LOM(new URL("file://"+ArchivePath +
			// getPathFromID(anID) + ".xml"));

			return new LOM(new File(getArchivePath() + ArchivePathEnd
					+ getPathFromID(anID) + ".xml"));
		} catch (Exception e) {
			e.printStackTrace();
			String theResult = LOR.INSTANCE.getLOM(anID);
			if (theResult != null)
				return new LOM(theResult);
			return null;
		}

	}

	/**
	 * Load upper activity.
	 * 
	 * @return the generic activity
	 */
	public GenericActivity loadUpperActivity() {
		try {
			Document theFile = new SAXBuilder().build(getPropertyFile());
			Element theRoot = (Element) theFile.getContent().get(0);
			String theUpperActivityID = theRoot.getChild("UpperActivity")
					.getTextTrim();
			GenericActivity theActivity ;
			/*try {
				isLoadingCache = true;
			
				Document theCacheFile = new SAXBuilder().build(getCacheFile());
				//Element theCacheRoot = (Element) theCacheFile.getContent().get(0);
				 theActivity = getActivity(theUpperActivityID);
				/*DiffusionCache.buildFromXMLElement(
						theCacheRoot.getChild("DiffusionCache"),
						DiffusionCache.getInstance());
				ValidationCache.buildFromXMLElement(
						theCacheRoot.getChild("ValidationCache"),
						ValidationCache.getInstance());*/
				/*}catch (Exception e1) {
					e1.printStackTrace();
					System.out.println("No valid cache file was given");
					isLoadingCache = false;
					theActivity = getActivity(theUpperActivityID);
				}*/
		    isLoadingCache = false;
		    theActivity = getActivity(theUpperActivityID);
		   // theActivity.updateAllView();
		    return theActivity;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("No valid project was found");
			isLoadingCache = false;
		}
		return null;
	}

	/**
	 * return the activity corresponding to aRef.
	 * 
	 * @param aRef
	 *            the a ref
	 * 
	 * @return the activity
	 */
	public GenericActivity getActivity(Reference aRef) {
		if (aRef != null) {
			if (aRef.getID().equals(newRefID)) {
				return new GenericActivity();
			}
			LOM theActivityLOM = loadLOM(aRef.getID());
			String theActivityLocation = LOMAttribute.getLOMAttribute(
					"technical/location").getValueIn(theActivityLOM).getValue();
			Couple<Document,GenericActivity> theActivityFromURLCouple = GenericActivity.getActivityFromURL(theActivityLocation,
					theActivityLOM, itsLMProject,true);
			if (theActivityFromURLCouple !=null)
			return theActivityFromURLCouple.getRightElement();
		} 
		return null;
	}

	/**
	 * return the activity corresponding to aRef.
	 * 
	 * @param aID
	 *            the a ID
	 * 
	 * @return the activity
	 */
	public GenericActivity getActivity(String aID) {
		if (aID != null){
		LOM theActivityLOM = loadLOM(aID);
		Couple<Document,GenericActivity> theActivityFromURLCouple = GenericActivity.getActivityFromURL(getMaterialLocationForID(theActivityLOM.getMaterialID())
				+ GenericActivity.ITSActivityFileName,
				theActivityLOM, itsLMProject,true);
		if (theActivityFromURLCouple !=null)
		return theActivityFromURLCouple.getRightElement();
		}
		return new GenericActivity();
	}

	/**
	 * Gets the archive path.
	 * 
	 * @return the archive path
	 */
	public String getArchivePath() {
		return itsLMProject.getArchivePath();
	}

	/**
	 * Gets the property file.
	 * 
	 * @return the property file
	 */
	public String getPropertyFile() {
		return getArchivePath() + ArchivePathEnd + PropertyFile;
	}

	/**
	 * Gets the cache file.
	 * 
	 * @return the cache file
	 */
	public String getCacheFile() {
		return getArchivePath() + ArchivePathEnd + CacheFile;
	}

	
}
