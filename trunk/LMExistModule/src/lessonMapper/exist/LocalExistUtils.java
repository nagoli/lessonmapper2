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

import java.util.Vector;

import org.exist.security.xacml.AccessContext;
import org.exist.storage.DBBroker;
import org.exist.storage.serializers.Serializer;
import org.exist.xmldb.XQueryService;
import org.exist.xquery.value.NodeValue;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceIterator;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

public class LocalExistUtils {

	/**
	 * serialize the results of a xml query done with a reference on a specific
	 * broker
	 * 
	 * @param xquery
	 * @param aBroker
	 * @param aContext
	 * @return
	 */

	public static Vector<String> localXMLQuery(String xquery, DBBroker aBroker,
			AccessContext aContext) {
		Vector<String> resultsStrings = new Vector<String>();
		try {
			Sequence theResult = aBroker.getXQueryService().execute(xquery,
					null, aContext);
			SequenceIterator theIterator = theResult.iterate();
			Serializer serializer = aBroker.getSerializer();
			while (theIterator.hasNext()) {
				NodeValue next = (NodeValue) theIterator.nextItem();
				resultsStrings.add(serializer.serialize(next));
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return resultsStrings;
	}

	/**
	 * get xquery results serialized
	 * 
	 * @param xquery
	 * @return
	 */
	public static Vector<String> localXMLQuery(String xquery) {
		Vector<String> resultsStrings = new Vector<String>();
		Collection col = null;
		try {
			initDriver();
			col = DatabaseManager.getCollection("xmldb:exist:/db", "admin",
					null);
			XQueryService service = (XQueryService) col.getService(
					"XQueryService", "1.0");
			CompiledExpression compiled = service.compile(xquery);
			ResourceSet resultSet = service.execute(compiled);
			ResourceIterator results = resultSet.getIterator();
			while (results.hasMoreResources()) {
				XMLResource res = (XMLResource) results.nextResource();
				resultsStrings.add((String) res.getContent());
			}
		} catch (XMLDBException e) {
			System.err.println("XML:DB Exception occured " + e.errorCode);
			System.err.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException e1) {
					e1.printStackTrace();
				}
			}
		}
		return resultsStrings;
	}

	static boolean isInitDriver = false;

	private static void initDriver() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, XMLDBException {
		if (!isInitDriver) {
			String driver = "org.exist.xmldb.DatabaseImpl";
			Class cl = Class.forName(driver);
			Database database = (Database) cl.newInstance();
			DatabaseManager.registerDatabase(database);
			isInitDriver = true;
		}
	}

}
