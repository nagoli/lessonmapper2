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
package dcc.lessonMapper2.fileManager;

import java.awt.image.BufferedImage;
import java.io.File;

import lessonMapper.lom.LOM;
import lor.LOR;
import dcc.lessonMapper2.ui.graph.element.lom.GenericActivity;
import dcc.lessonMapper2.ui.graph.element.lom.SelectionableLOM;

/**
 * this aspect is in charge of recording the project currently developed in the
 * distant repository defined by the package LOR. In fact it makes that all call
 * to saveLOM also imply a put of that LOM in the distant repository. This
 * aspect is active only when the calls to saveLOM are done in the control flow
 * of the method putProjectOnRepository
 */

public aspect DistantRepository {

	pointcut saveRepository():execution(boolean LocalRepository.saveOnRepository(GenericActivity));

	pointcut saveLOM(LocalRepository aRepository, SelectionableLOM aSelectionableLOM): execution(boolean LocalRepository.saveLOM(SelectionableLOM)) 
		&& target(aRepository) && args(aSelectionableLOM)
	     && cflow(saveRepository());

	/**
	 * catch all saveLOM and record the LOM on the 
	 * distant repository: if the LOM has no LO associated 
	 * it is not saved on the distant repository
	 * 
	 */
	
	after(LocalRepository aRepository, SelectionableLOM aSelectionableLOM) returning( boolean theBool) : saveLOM( aRepository, aSelectionableLOM) 	{
		LOM theLOM = aSelectionableLOM.getLOM();
		String theID = theLOM.getID();
		String theURL = aSelectionableLOM.getMaterialURL();
		String theMaterialID = theLOM.getMaterialID();
		String theThumbURL = aRepository.getMaterialThumbForID(theMaterialID); 
		File theThumb = new File(theThumbURL);
		if (theURL != null && !theURL.equals("")) {
			if (!theURL.startsWith("http")) {
				File theLO = new File(theURL);
				String theModifiedLOMXML = theLOM.toXMLStringWithLocation(LOR.INSTANCE.getBaseURIForLO(theMaterialID,aSelectionableLOM.getMaterialName()));
				try {
					if (!theThumb.exists())
//						check if theAssociatedNode is an Activity and if it is make a thumb of this activity
						if (aSelectionableLOM instanceof GenericActivity) {
							GenericActivity theActivity= (GenericActivity)aSelectionableLOM;					
							BufferedImage theImage = (BufferedImage)theActivity.toImage();
							LOR.INSTANCE.put(theID, theModifiedLOMXML,theMaterialID, aSelectionableLOM.getMaterialName(),  theLO ,theImage);
							//???? TODO check why the material was null 
						}
						else LOR.INSTANCE.put(theID,theModifiedLOMXML, theMaterialID, aSelectionableLOM.getMaterialName(), theLO);
					else LOR.INSTANCE.put(theID, theModifiedLOMXML, theMaterialID,aSelectionableLOM.getMaterialName(),theLO, theThumb);
				} catch (Exception e) {
					e.printStackTrace();
					theBool = false;
				}
			} else {
				try {
					if (!theThumb.exists()) {
						LOR.INSTANCE.put(theID, theLOM.toXMLString());
					}
					else LOR.INSTANCE.put(theID, theLOM.toXMLString(),theMaterialID,null,null,theThumb);
				} catch (Exception e) {
					e.printStackTrace();
					theBool = false;
				}
			}
		}

		else
			theBool = true;
		// do not record lom without lo on the repository
	}

}
