package lessonMapper.lom.diffuse;

import java.net.URL;
import java.util.List;

import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMRelationType;

import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import diffuse.metadata.MetadataSetAttribute;
import diffuse.metadata.MetadataSetRelationType;
import diffuse.models.res.parser.ResRuleBuilder;
import diffuse.models.res.rules.ResRule;


public class LOMResRuleBuilder extends ResRuleBuilder {

	public static URL ITSRestrictionURL = LOMResRuleBuilder.class
	.getResource("resources/restrictions.xml");

	public LOMResRuleBuilder(String aAttributeName, String aRelationName,
			String aFormule) {
		super(aAttributeName, aRelationName, aFormule);
	}

	@Override
	public MetadataSetAttribute getAttribute() {
		return LOMAttribute.getLOMAttribute(itsAttributeName);
	}

	@Override
	public MetadataSetRelationType getRelationType() {
		return LOMRelationType.getLOMRelationType(itsRelationName);
	}

	
	
	public static void parseRules(){
		
		try {
			SAXBuilder theBuilder = new SAXBuilder();
			Document theDocument = theBuilder.build(ITSRestrictionURL);
			JDOMXPath myXPath = new JDOMXPath("heuristic/restriction");
			List result = myXPath.selectNodes(theDocument);
			for (int i = 0; i < result.size(); i++) {
				Element theElement = (Element) result.get(i);
				String theAttribute = theElement.getAttribute("attribute")
						.getValue();
				String theRelation = theElement.getAttribute("relation")
						.getValue();
				String theRule = theElement.getText();
				LOMResRuleBuilder theResRuleBuilder = new LOMResRuleBuilder(theAttribute,
						theRelation, theRule);
				ResRule.register(theResRuleBuilder.getAttribute(), theResRuleBuilder.getRelationType(), theResRuleBuilder.getRule());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		System.out.println(new LOMResRuleBuilder("general/aggregationlevel", "haspart",
				"CONTAINED 'Level1'").getRule());
	}

}
