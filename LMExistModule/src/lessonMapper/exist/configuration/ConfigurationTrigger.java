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
package lessonMapper.exist.configuration;

import java.util.Map;

import org.exist.collections.Collection;
import org.exist.collections.CollectionConfigurationException;
import org.exist.collections.triggers.DocumentTrigger;
import org.exist.collections.triggers.FilteringTrigger;
import org.exist.collections.triggers.TriggerException;
import org.exist.dom.DocumentImpl;
import org.exist.storage.DBBroker;
import org.exist.storage.txn.Txn;
import org.exist.xmldb.XmldbURI;

/**
 * execute configuration processing all ITSUpdateFrequency times
 * 
 */
public class ConfigurationTrigger extends FilteringTrigger implements
		DocumentTrigger {
	static final String ITSNameSpacePrefix = "ims";

	static final String ITSNameSpace = "http://www.imsglobal.org/xsd/imsmd_v1p2";

	
	static int ITSUpdateFrequency = 10;
	
	static int ITSCounter = 0;
	public static boolean ISBLOCKED = false;

	public ConfigurationTrigger() {
		super();
	}

	@SuppressWarnings("unchecked")
	public void configure(DBBroker broker, Collection parent, Map parameters)
			throws CollectionConfigurationException {
		super.configure(broker, parent, parameters);

		try {
		
		} catch (Exception e) {
			throw new CollectionConfigurationException();
		}

	}

	/**
	 * @see org.exist.collections.triggers.DocumentTrigger#prepare(int,
	 *      org.exist.storage.DBBroker, org.exist.storage.txn.Txn,
	 *      org.exist.xmldb.XmldbURI, org.w3c.dom.Document)
	 */
	public void prepare(int event, DBBroker broker, Txn transaction,
			XmldbURI documentPath, DocumentImpl existingDocument)
			throws TriggerException {
		// nothing to do, we work on post document addition.
		

	}

	

	
	/**
	 * update the configuration each ITSUpdateFrequency times
	 *
	 * @see org.exist.collections.triggers.DocumentTrigger#finish(int,
	 *      org.exist.storage.DBBroker, org.exist.storage.txn.Txn,
	 *      org.w3c.dom.Document)
	 */
	public void finish(int event, DBBroker broker, Txn transaction,
			DocumentImpl document) {
		if (ISBLOCKED || ITSCounter++%ITSUpdateFrequency!=0) return;
		//TODO process in another thread which  no more than one thread waiting for its turm
		ProbabilityBuilder.buildProba();
		
	}

}