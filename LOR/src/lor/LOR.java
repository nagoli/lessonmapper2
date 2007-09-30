/*
 * LessonMapper 2.
Copyright (C) Francisco Carbonel - Olivier Motelet.

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

package lor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.exist.xmldb.XQueryService;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.BinaryResource;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

public class LOR {

	public static LOR INSTANCE = new LOR();

	//public static String DBAddress = "elane.dcc.uchile.cl:8800";

	public static String DBAddress = "localhost:8080";

	
	
	/**
	 * Envia una consulta XQuery al LOR
	 * 
	 * @param xquery
	 * @return un java.util.Vector con strings uno por respuesta
	 */
	boolean isInitDriver = false;
	
	
	public Vector<String> xmlQuery(String xquery) {
		Vector<String> resultsStrings = new Vector<String>();
		Collection col = null;
		try {
			if (!isInitDriver) initDriver();

			col = DatabaseManager.getCollection("xmldb:exist://" + DBAddress
					+ "/exist/xmlrpc/db/lom");

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

	
	public String getLOMLocationWithID(String aLOMID) {
		return "http://" + DBAddress + "/exist/servlet/db/lom/" + IDtoLorID(aLOMID);
	}

	
	/**
	 * Consulta por un LOM con ID espec?fico return null if it does not exist or
	 * return the first result of more than one element has the same ID
	 * 
	 * @param anID
	 * @return LOM source
	 */
	public String getLOM(String lomID) {
		/*
		 * String xquery = "declare namespace n =
		 * \"http://www.imsglobal.org/xsd/imsmd_v1p2\";\n"+
		 * "doc()//n:lom[n:general/n:identifier = '"+anID+"']";
		 */
		String xquery = "declare namespace n = \"http://www.imsglobal.org/xsd/imsmd_v1p2\";\n"
				+ "//n:lom[n:metametadata/n:identifier = '"
				+ lomID
				+ "']";

		Vector<String> r = xmlQuery(xquery);
		if (r.isEmpty())
			return null;
		return  r.firstElement();
	}

	public String getThumbLocationWithMaterialID(String aMaterialID) {
		return "http://" + DBAddress + "/exist/servlet/db/lo/" + IDtoLorID(aMaterialID)
				+ "/thumb.jpg";
	}

	/**
	 * Coloca un lom en el LOR con un ID espec’fico
	 * 
	 * @param ID
	 *            id ?nico del lom
	 * @param lom
	 *            Este argumento puede ser de tipo File o String
	 * @throws Exception
	 */
	public void put(String lomID, String lom) throws Exception {

		putObject(lomID, lom, null, null, null, null);
	}

	

	/**
	 * Coloca un lom en el LOR con un ID espec’fico y adem‡s agrega un material
	 * 
	 * @param ID
	 *            id ?nico del lom
	 * @param lom
	 *            Este argumento puede ser de tipo File o String
	 * @param lo
	 *            File del material
	 * @throws Exception
	 */
	public void put(String lomID, String lom, String loID, String filename, File lo)
			throws Exception {

		putObject(lomID, lom, loID, filename, lo, null);
	}

	

	/**
	 * Coloca un lom en el LOR con un ID espec’fico y adem‡s agrega un material
	 * y una imagen en formato jpg
	 * 
	 * @param ID
	 *            id ?nico del lom
	 * @param lom
	 *            Este argumento puede ser de tipo File o String
	 * @param lo
	 *            File del material
	 * @param thumbnail
	 *            archivo de imagen del material en formato jpg
	 * @throws Exception
	 */
	public void put(String lomID, String lom, String loID, String fileName, File lo,
			File thumbnail) throws Exception {

		putObject(lomID, lom, loID, fileName, lo, thumbnail);
	}

	
	/**
	 * Coloca un lom en el LOR con un ID espec’fico y adem‡s agrega un material
	 * y una imagen en formato jpg
	 * 
	 * @param ID
	 *            id ?nico del lom
	 * @param lom
	 *            Este argumento puede ser de tipo String
	 * @param lo
	 *            File del material
	 * @param thumbnail
	 *            bytearray de imagen del material en formato jpg
	 * @throws Exception
	 */
	public void put(String lomID, String lom, String loID, String fileName, File lo,
			BufferedImage thumbnail) throws Exception {

		OutputStream out = new ByteArrayOutputStream();
		ImageIO.write(thumbnail, "jpg", out);
		putObject(lomID, lom, loID, fileName, lo, out);

	}

	/**
	 * Coloca un lom en el LOR con un ID espec’fico y adem‡s agrega un material
	 * y una imagen en formato jpg
	 * 
	 * @param ID
	 *            id ?nico del lom
	 * @param lom
	 *            Este argumento puede ser de tipo File o String
	 * @param lo
	 *            File del material
	 * @param thumbnail
	 *            archivo de imagen del material en formato jpg
	 * @throws Exception
	 */
	private void putObject(String lomID, String lom, String loID, String filename,
			File material, Object thumbnail) throws Exception {

		if (!isInitDriver) initDriver();

		// Obtenemos la colecci—n lom, podemos usar usuario y contrase–a
		Collection col = DatabaseManager.getCollection("xmldb:exist://"
				+ DBAddress + "/exist/xmlrpc/db/lom");
			XMLResource doc = (XMLResource) col
				.createResource(IDtoLorID(lomID), "XMLResource");
			doc.setContent(lom);
			col.storeResource(doc);
		
		if (loID != null) {

			// try to get collection
			col = DatabaseManager.getCollection("xmldb:exist://" + DBAddress
					+ "/exist/xmlrpc/db/lo");
			CollectionManagementService mgtService = (CollectionManagementService) col
					.getService("CollectionManagementService", "1.0");
			col = mgtService.createCollection(IDtoLorID(loID));
			if (material != null) {
				// create new XMLResource; an id will be assigned to the new
				// resource
				BinaryResource bindoc = (BinaryResource) col.createResource(
						filename, "BinaryResource");
				bindoc.setContent(material);
				System.out.print("storing material " + bindoc.getId() + "...");
				col.storeResource(bindoc);
				System.out.println("ok.");
			}
			if (thumbnail != null) {
				BinaryResource binthumb = (BinaryResource) col.createResource(
						"thumb.jpg", "BinaryResource");
				binthumb.setContent(thumbnail);
				System.out.print("storing material " + binthumb.getId() + "...");
				col.storeResource(binthumb);
				System.out.println("ok.");
			}
		}

	}




	public String getBaseURIForLO(String loID, String docName){
		return "http://" + DBAddress + "/exist/servlet/db/lo/" + IDtoLorID(loID)
		+ "/" +docName;
	}
	
	
	
	/**
	 * Takes aQueryID, aKeywordQuery and aQueryNodeId as paremeter
	 * the context of the query node has previously been stored with the method putQueryContext(String aQueryID, String aLOMID, String aLOM)
	 * return an ordered set of LOM that may match the query given in parameters
	 * @return
	 */
	public List<String> LMQuery(String aQueryID, String aKeywordQuery, String aQueryNodeID){
		ArrayList<String> theResults= new ArrayList<String>();
		try {
			System.out.println("Querying repository...");
			if (!isInitDriver)
				initDriver();
			// creacion de la colecci—n para la query 
			Collection col = DatabaseManager.getCollection("xmldb:exist://"
					+ DBAddress + "/exist/xmlrpc/db/LMQuery");
			XQueryService service = (XQueryService) col.getService(
					"XQueryService", "1.0");
			String theQueryUpdate = "update insert <query> " + "<id>"
					+ aQueryID + "</id>" + "<queryNodeID>" + aQueryNodeID
					+ "</queryNodeID>" + "<keywordQuery>" + aKeywordQuery
					+ "</keywordQuery>" + "</query> into /pendingQueries";
			CompiledExpression compiled = service.compile(theQueryUpdate);
			service.execute(compiled);
			System.out.print("ok1...");
			String theQuerySearch = "lm:search('" + aQueryID + "')";
			compiled = service.compile(theQuerySearch);
			ResourceSet resultSet = service.execute(compiled);
			ResourceIterator results = resultSet.getIterator();
			while (results.hasMoreResources()) {
				XMLResource res = (XMLResource) results.nextResource();
				theResults.add((String) res.getContent());
			}
			System.out.print("ok2");
		} catch (Exception e) {
			System.out.println("Collection Problem with the DB while querying it");
			//e.printStackTrace();
		}
		return theResults;
	}
	
	/**
	 * set the results chosen by the user for a particular query
	 * @param aQueryID
	 * @param aResultID
	 */
	public void setLMQueryResult(String aQueryID, String aResultID){
		String thePendingQuery = "/pendingQueries/query[id eq '"+ aQueryID + "']";
		String theUpdate1 = "update insert <expectedLOMIDs> "+ aResultID+ "</expectedLOMIDs> into " +thePendingQuery;
		String theUpdate2 = "update insert " +thePendingQuery+" into /completeQueries";
		String theUpdate3 = "update delete " + thePendingQuery;
		try {
			if (!isInitDriver) initDriver();
		// add to completeQuery and remove from pending queries
		Collection col = DatabaseManager.getCollection("xmldb:exist://"
				+ DBAddress + "/exist/xmlrpc/db/LMQuery");
		XQueryService service = (XQueryService) col.getService(
				"XQueryService", "1.0");
		service.execute(service.compile(theUpdate1));
		service.execute(service.compile(theUpdate2));
		service.execute(service.compile(theUpdate3));
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	/**
	 * remove the data associated to a particular query form the repository
	 * @param aQueryID
	 */
	public void removeQuery(String aQueryID){
		System.out.println("Remove query ...");
		String thePendingQuery = "/pendingQueries/query[id eq '"+ aQueryID + "']";
		String theUpdate = "update delete " + thePendingQuery;
		try {
			if (!isInitDriver) initDriver();
		// remove from pending-query list
		Collection col = DatabaseManager.getCollection("xmldb:exist://"
				+ DBAddress + "/exist/xmlrpc/db/LMQuery");
		XQueryService service = (XQueryService) col.getService(
				"XQueryService", "1.0");
		service.execute(service.compile(theUpdate));
		//remove query context data
		CollectionManagementService mgtService = (CollectionManagementService) col
		.getService("CollectionManagementService", "1.0");
		mgtService.removeCollection(IDtoLorID(aQueryID));
		System.out.print("ok");
		} catch (Exception e) {
			System.out.println("Connection Problem with the DB while trying to send query result information");
			//e.printStackTrace();
		}	
	}
	
	
	/**
	 * register query on the repository
	 * @param queryID
	 */
	public void registerQuery(String queryID) throws Exception{
		System.out.println("Registering query ...");
		if (!isInitDriver) initDriver();
		// creacion de la colecci—n para la query 
		Collection col = DatabaseManager.getCollection("xmldb:exist://"
				+ DBAddress + "/exist/xmlrpc/db/LMQuery");
		CollectionManagementService mgtService = (CollectionManagementService) col
		.getService("CollectionManagementService", "1.0");
		col = mgtService.createCollection(IDtoLorID(queryID));
		//System.out.print("ok");
	}
	
	
	
	/**
	 * store the context of a query (i.e. the LOMs of the LMProject from which the query was done)
	 * @param args
	 */
	public void putQueryContext(String queryID, String lomID, Object lom) throws Exception {

		if (!isInitDriver) initDriver();
		System.out.println("Sending query context...");
		// Obtenemos la colecci—n lom, podemos usar usuario y contrase–a
		Collection col = DatabaseManager.getCollection("xmldb:exist://"
				+ DBAddress + "/exist/xmlrpc/db/LMQuery/"+IDtoLorID(queryID));
        //TODO check if we need to create the collection
		// Creamos el recurso en el Repositorio con el ID entregado
		XMLResource doc = (XMLResource) col
				.createResource(IDtoLorID(lomID), "XMLResource");
		doc.setContent(lom);
		col.storeResource(doc);
		System.out.print("ok");
	}


	private void initDriver() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, XMLDBException {
		// Inicializar el sriver
		String driver = "org.exist.xmldb.DatabaseImpl";
		Class cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		DatabaseManager.registerDatabase(database);
		isInitDriver = true;
	}
	
	/**
	 * give a ID for a LorID
	 * @param a
	 * @return
	 */
	public static String LorIDtoID(String aLorID){
		StringTokenizer theTokenizer = new StringTokenizer(aLorID, "_");
		String theID = "";
		while ( theTokenizer.hasMoreTokens()) 
			theID += theTokenizer.nextToken() + (theTokenizer.hasMoreTokens()? ":":"");
		return theID;
	}
	
	/**
	 * give a LorID for a ID
	 * @param a
	 * @return
	 */
	public static String IDtoLorID(String aID){
		StringTokenizer theTokenizer = new StringTokenizer(aID, ":");
		String theLorID = "";
		while ( theTokenizer.hasMoreTokens()) 
			theLorID += theTokenizer.nextToken() + (theTokenizer.hasMoreTokens()? "_":"");
		return theLorID;
	}
	
	public static void main(String[] args) {
		System.out.println(INSTANCE.xmlQuery(" declare namespace ims='http://www.imsglobal.org/xsd/imsmd_v1p2'; /ims:lom"));

	}

	
	
	
	
}
