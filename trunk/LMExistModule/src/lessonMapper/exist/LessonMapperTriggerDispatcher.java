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
package lessonMapper.exist;

import java.util.Map;
import java.util.Vector;

import lessonMapper.exist.configuration.ConfigurationTrigger;
import lessonMapper.exist.lucene.LuceneIndexerTrigger;

import org.exist.collections.Collection;
import org.exist.collections.CollectionConfigurationException;
import org.exist.collections.triggers.FilteringTrigger;
import org.exist.collections.triggers.TriggerException;
import org.exist.dom.DocumentImpl;
import org.exist.storage.DBBroker;
import org.exist.storage.txn.Txn;
import org.exist.xmldb.XmldbURI;


/**
 * Dispath the trigerring events to the triggers registers in the constuctor
 * @author omotelet
 *
 */


public class LessonMapperTriggerDispatcher extends FilteringTrigger {

	Vector<FilteringTrigger> itsTriggers = new Vector<FilteringTrigger>();

	public LessonMapperTriggerDispatcher() {
		itsTriggers.add(new LuceneIndexerTrigger());
		itsTriggers.add(new ConfigurationTrigger());

	}

	public void prepare(int aEvent, DBBroker aBroker, Txn aTransaction,
			XmldbURI aDocumentPath, DocumentImpl aExistingDocument)
			throws TriggerException {
	for (FilteringTrigger theTrigger : itsTriggers)
			theTrigger.prepare(aEvent, aBroker, aTransaction, aDocumentPath,
					aExistingDocument);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void configure(DBBroker aBroker, Collection aParent, Map aParameters)
			throws CollectionConfigurationException {
		for (FilteringTrigger theTrigger : itsTriggers)
			theTrigger.configure(aBroker, aParent, aParameters);
	}

	@Override
	public void finish(int aEvent, DBBroker aBroker, Txn aTransaction,
			DocumentImpl aDocument) {
		for (FilteringTrigger theTrigger : itsTriggers)
			theTrigger.finish(aEvent, aBroker, aTransaction, aDocument);
	}

}
