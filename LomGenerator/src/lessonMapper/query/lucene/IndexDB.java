/*
 * LessonMapper 2.
 */
package lessonMapper.query.lucene;

import java.io.StringReader;
import java.util.List;
import java.util.StringTokenizer;

import lor.LOR;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.tartarus.snowball.SnowballProgram;

/**
 * this class query the db to obtain all the lom registered in the repository
 * then it index them with lucene.
 * 
 * @author omotelet
 */

public class IndexDB {

	/**
	 * 
	 */
	public static String ITSIndexLocation = System.getProperty("user.home")
			+ "/tmp/index";

	/**
	 * 
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {

		Analyzer analyzer = new WhitespaceAnalyzer();
		boolean createFlag = true;
		try {
			IndexWriter writer = new IndexWriter(ITSIndexLocation, analyzer,
					createFlag);
			List<String> theResults = LOR.INSTANCE
					.xmlQuery(""
							+ "declare namespace ims='http://www.imsglobal.org/xsd/imsmd_v1p2';"
							+ " for $lom in /ims:lom"
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
					Document lomDocument = new Document();
					lomDocument.add(new Field("id", 
							theLOMResult.getChildTextTrim("id"),
							Field.Store.YES, Field.Index.UN_TOKENIZED));
					StringTokenizer theContentTokenizer = new StringTokenizer(theLOMResult.getChildText("content"));
					String theContent = "";
					for (String theToken; theContentTokenizer.hasMoreTokens();){ 
						theToken = theContentTokenizer.nextToken();
						theContent += " " + SnowballProgram.stem(theToken, "es"); //we index stemmed version of the metadata
						//	theContent += " zk"+theToken; // we add "zk" top enable leadings stars
					}
					System.out.println(theContent);
					lomDocument.add(new Field("content", new StringReader(theContent)));
					writer.addDocument(lomDocument);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}