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
package lessonMapper.exist.lucene;

import java.util.Map;

import org.exist.collections.Collection;
import org.exist.collections.CollectionConfigurationException;
import org.exist.collections.triggers.DocumentTrigger;
import org.exist.collections.triggers.FilteringTrigger;
import org.exist.collections.triggers.Trigger;
import org.exist.collections.triggers.TriggerException;
import org.exist.dom.DocumentImpl;
import org.exist.memtree.ElementImpl;
import org.exist.memtree.ReferenceNode;
import org.exist.memtree.TextImpl;
import org.exist.security.xacml.AccessContext;
import org.exist.storage.DBBroker;
import org.exist.storage.txn.Txn;
import org.exist.xmldb.XmldbURI;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceIterator;
import org.xml.sax.SAXException;

/**
 * Moidified version of Adam Retter (adam.retter@devon.gov.uk) and Patrick
 * Turcotte(patrek@gmail.com) trigger version
 * 
 */
public class LuceneIndexerTrigger extends FilteringTrigger implements
		DocumentTrigger {
	static final String ITSNameSpacePrefix = "ims";

	static final String ITSNameSpace = "http://www.imsglobal.org/xsd/imsmd_v1p2";

	

	
	public LuceneIndexerTrigger() {
		super();
	}

	@SuppressWarnings("unchecked")
	public void configure(DBBroker broker, Collection parent, Map parameters)
			throws CollectionConfigurationException {
		super.configure(broker, parent, parameters);
	}

	/**
	 * @see org.exist.collections.triggers.DocumentTrigger#prepare(int,
	 *      org.exist.storage.DBBroker, org.exist.storage.txn.Txn,
	 *      org.exist.xmldb.XmldbURI, org.w3c.dom.Document)
	 */
	public void prepare(int event, DBBroker broker, Txn transaction,
			XmldbURI documentPath, DocumentImpl existingDocument)
			throws TriggerException {
		// nothing to do, we work post document addition.
		System.out.println("received object action to prepare");

		if (event == Trigger.UPDATE_DOCUMENT_EVENT
				|| event == Trigger.STORE_DOCUMENT_EVENT)
			return;
		String theID = "";

		try {
			String theQuery = ""
					+ "declare namespace ims='http://www.imsglobal.org/xsd/imsmd_v1p2';"
					+ " for $lom in fn:doc(\""
					+ documentPath.toString()
					+ "\")"
					+ "  return <result>"
					+ "    <id> {$lom/ims:lom/ims:metametadata/ims:identifier/text()} </id>"
					+ "</result>";
			Sequence theResult = broker.getXQueryService().execute(theQuery,
					null, AccessContext.TRIGGER);
			SequenceIterator theIterator = theResult.iterate();
			if (!theIterator.hasNext())
				throw new Exception("no result for query " + theQuery);
			ElementImpl theItem = (ElementImpl) theIterator.nextItem();
			// id is stored as referenceNode
			theID = ((ReferenceNode) theItem.getFirstChild().getFirstChild())
					.getReference().getNodeValue();
		} catch (Exception e) {
			e.printStackTrace();
		}

		removeDocument(broker, theID);

	}

	/**
	 * @see org.exist.collections.triggers.DocumentTrigger#finish(int,
	 *      org.exist.storage.DBBroker, org.exist.storage.txn.Txn,
	 *      org.w3c.dom.Document)
	 */
	public void finish(int event, DBBroker broker, Txn transaction,
			DocumentImpl document) {
		
		if (event == Trigger.REMOVE_DOCUMENT_EVENT)
			return;

		if (document != null) {
			String theID = "";
			String theContentString = "";
			try {
				String theQuery = ""
						+ "declare namespace ims='http://www.imsglobal.org/xsd/imsmd_v1p2';"
						+ " for $lom in fn:doc(\""
						+ document.getURI().toString()
						+ "\")"
						+ "  return <result>"
						+ "    <id> {$lom/ims:lom/ims:metametadata/ims:identifier/text()} </id>"
						+ "    <content> { for $t in $lom//text() return "
						+ "               concat($t,\" \")  } </content>"
						+ "</result>";
				Sequence theResult = broker.getXQueryService().execute(
						theQuery, null, AccessContext.TRIGGER);
				SequenceIterator theIterator = theResult.iterate();
				if (!theIterator.hasNext())
					throw new Exception("no result for query " + theQuery);
				ElementImpl theItem = (ElementImpl) theIterator.nextItem();
				// id is stored as referenceNode
				theID = ((ReferenceNode) theItem.getFirstChild()
						.getFirstChild()).getReference().getNodeValue();
				// content is stored as textImpl
				theContentString = ((TextImpl) theItem.getChildNodes().item(1)
						.getFirstChild()).getNodeValue();

			} catch (Exception e) {
				e.printStackTrace();
			}

			switch (event) {
			case Trigger.STORE_DOCUMENT_EVENT:
				removeDocument(broker, theID);
				addDocument(broker, theID, theContentString);
				break;

			case Trigger.UPDATE_DOCUMENT_EVENT:
				removeDocument(broker, theID);
				addDocument(broker, theID, theContentString);
				break;

			}
		}
	}

	/**
	 * Remove a document from the Lucene Index
	 * 
	 * @param broker
	 * 
	 * @param id
	 *            The name of the document to remove
	 * 
	 * @return true or false indicating success or failure
	 */
	private boolean removeDocument(DBBroker broker, String id) {
		return LuceneIndexer.ITSINSTANCE.removeDocument(id);
	}

	/**
	 * Add a document to the Lucene index
	 * 
	 * @param broker
	 * 
	 * @param id
	 *            The name of the document
	 * @param doc
	 *            The document object
	 * @param resource
	 * 
	 * @return true or false indicating success or failure
	 * @throws DocumentException
	 * @throws SAXException
	 */
	private boolean addDocument(DBBroker broker, String id, String aContent) {
		return LuceneIndexer.ITSINSTANCE.addDocument(id, aContent);

	}

}
