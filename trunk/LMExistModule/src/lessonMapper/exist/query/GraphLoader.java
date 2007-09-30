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
package lessonMapper.exist.query;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import lessonMapper.exist.LocalExistUtils;
import lessonMapper.lom.LOM;
import lor.LOR;

import org.jdom.JDOMException;

/**
 * graph loader is responsible for loading all the loms associated to a query ID
 * 
 * @author omotelet
 */

public class GraphLoader implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Archive path end. */
	static String ArchivePathEnd = File.separator;

	/** The Constant PropertyFile. */
	final public static String PropertyFile = "property.data";

	/** The its archive path. */
	protected String itsArchivePath;

	@SuppressWarnings("unchecked")
	public GraphLoader(String aQueryID) throws IOException, JDOMException {

		// load all lom in the directory
		List<String> theResults = LocalExistUtils
				.localXMLQuery(""
						+ "declare namespace ims='http://www.imsglobal.org/xsd/imsmd_v1p2';"
						+ " collection('/db/LMQuery/" + LOR.IDtoLorID(aQueryID) + "')/ims:lom");
		for (String theString : theResults) 
			loadLOM(theString);
		
	}

	

	/**
	 * Load LOM.
	 * 
	 * @param anID
	 *            the an ID
	 * 
	 * @return the LOM
	 */
	public LOM loadLOM(String aLOMXMLRepresentation) {
			return new LOM(aLOMXMLRepresentation);
	}



}
