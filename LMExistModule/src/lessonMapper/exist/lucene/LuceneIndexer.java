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

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.StringTokenizer;

import lessonMapper.exist.LocalExistUtils;
import lessonMapper.lom.LOM;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.tartarus.snowball.SnowballProgram;

import util.SortedScoreRankList;
import util.system.FileManagement;

/**
 * This class is responsible for the indexation with lucene of the LOs of the
 * repository. addDocument and remove document are called by trigger or by
 * main() of this class which reindex the whole repository
 * 
 * @author omotelet
 */

public class LuceneIndexer {

	/**
	 * 
	 */
	public static String ITSIndexLocation = System.getProperty("user.home")
			+ "/LM2Config/index/";

	public static LuceneIndexer ITSINSTANCE = new LuceneIndexer();

	Analyzer itsAnalyzer = new WhitespaceAnalyzer();
	boolean isCreateFlag = true;
	IndexWriter itsWriter;

	private LuceneIndexer() {
		initWriter();
	}

	private void initWriter() {
		try {
			itsWriter = new IndexWriter(ITSIndexLocation, itsAnalyzer,
					isCreateFlag);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * remove a document aId form the index and flush the writer
	 * 
	 * @param aId
	 * @return
	 */
	public boolean removeDocument(String aId) {
		return removeDocument(aId, true);
	}

	/**
	 * remove a Document form the index flush it if isFlushed is true
	 * 
	 * @param aId
	 * @param isFlushed
	 * @return
	 */

	public boolean removeDocument(String aId, boolean isFlushed) {
		try {

			System.out.println("delete " + aId);

			itsWriter.deleteDocuments(new Term("id", aId));
			if (isFlushed) {
				itsWriter.flush();
				/*
				 * System.out
				 * .println(IndexReader.open(ITSIndexLocation).numDocs());
				 */
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * flush the writer
	 */
	public void flush() {
		try {
			itsWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * close the writer
	 */
	public void close() {
		try {
			itsWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * remove all indexed elements
	 */
	public void resetIndex() {
		close();
		try {
			FileManagement.getFileManagement().cleanDirectory(
					new File(ITSIndexLocation));
		} catch (Exception e) {
			e.printStackTrace();
		}
		initWriter();
		System.out.println("Index resetted");
	}

	/**
	 * add a document in the index and flush the change
	 * 
	 * @param aId
	 * @param aContent
	 * @return
	 */
	public boolean addDocument(String aId, String aContent) {
		return addDocument(aId, aContent, true);
	}

	/**
	 * add a document in the index and flush the change if isFlushed
	 * 
	 * @param aId
	 * @param aContent
	 * @param isFlushed
	 * @return
	 */
	public boolean addDocument(String aId, String aContent, boolean isFlushed) {
		Document lomDocument = new Document();
		lomDocument.add(new Field("id", aId, Field.Store.YES,
				Field.Index.UN_TOKENIZED));
		StringTokenizer theContentTokenizer = new StringTokenizer(aContent);
		String theContent = "";
		for (String theToken; theContentTokenizer.hasMoreTokens();) {
			theToken = theContentTokenizer.nextToken();
			theContent += " " + SnowballProgram.stem(theToken, "es"); // we
			// index
			// stemmed
			// version
			// of
			// the
			// metadata
			// theContent += " zk"+theToken; // we add "zk" top enable leadings
			// stars
		}
		// System.out.println(theContent);
		lomDocument.add(new Field("content", new StringReader(theContent)));
		try {
			System.out.println("add document " + aId);
			itsWriter.addDocument(lomDocument);
			if (isFlushed) {
				itsWriter.flush();
				/*
				 * System.out
				 * .println(IndexReader.open(ITSIndexLocation).numDocs());
				 */
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * re-index all the elements of the DB
	 */
	public void reindex() {
		try {
			System.out.println("rebuild Lucene index");
			resetIndex();
			List<String> theResults = LocalExistUtils
					.localXMLQuery(""
							+ "declare namespace ims='http://www.imsglobal.org/xsd/imsmd_v1p2';"
							+ " for $lom in collection('/db/lom')/ims:lom"
							+ "  return <result> "
							+ "    <id> {$lom/ims:metametadata/ims:identifier/text()} </id>"
							+ "    <content> { for $t in $lom//text() return "
							+ "               concat($t,\" \")  } </content>"
							+ "</result>");
			SAXBuilder theBuilder = new SAXBuilder();
			for (String theString : theResults) {
				try {
					Element theLOMResult = theBuilder.build(
							new StringReader(theString)).getRootElement();
					String theID = theLOMResult.getChildTextTrim("id");
					String theContent = theLOMResult.getChildText("content");
					addDocument(theID, theContent, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("Lucene index rebuilt");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static SortedScoreRankList<LOM> getLuceneResults(String aKeywordQuery) {
		String theReformulatedQuery = "";
		try {
			BooleanQuery.setMaxClauseCount(1000 * 1000);
			IndexReader reader;

			reader = IndexReader.open(LuceneIndexer.ITSIndexLocation);

			Searcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser("content", analyzer);
			// version for stemmed index
			theReformulatedQuery = SnowballProgram
					.stemList(aKeywordQuery, "es");
			Query query = parser.parse(theReformulatedQuery);
			Hits hits = searcher.search(query);
			SortedScoreRankList<LOM> theLuceneResults = new SortedScoreRankList<LOM>(
					true, false);
			for (int i = 0; i < hits.length(); i++) {
				LOM theLOM = LOM.getLOM(hits.doc(i).get("id"));
				theLuceneResults.add(theLOM, (double) hits.score(i));
			}
			reader.close();
			return theLuceneResults;
		} catch (IOException e) {
			System.out
					.println("Problem with index file. Maybe nothing has been added yet to the repository. Otherwise call lm:rebuildIndex() on DB");
			return new SortedScoreRankList<LOM>(true, false);
		} catch (ParseException e) {
			System.out.println("Problem with lucene query: >>> "
					+ theReformulatedQuery);
			return new SortedScoreRankList<LOM>(true, false);
		}

	}

}
