/*
 * LessonMapper 2.
 */
package lessonMapper.query.rankboost;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import lessonMapper.lom.LOM;
import lessonMapper.query.LOMRanking;

import org.jdom.Element;

import util.Couple;

/**
 * 
 */
public class RankBoostRanker implements Comparable<RankBoostRanker>{

	/**
	 * 
	 */
	static public double ZERO = 0;
	
	/**
	 * 
	 */
	static public double ONE = 1;
	
	/**
	 * 
	 */
	static public double TWO = 2;
	
	/**
	 * 
	 */
	static public double MINUSONE = -1;
	
	/**
	 * 
	 */
	protected Map<Hypothesis, Double> itsHypothesisAndAlphaList;
	
	
	public static Element makeXMLInstance(RankBoostRanker aRanker){
		Element theElement = new Element("RankBoostRanker");
		for (Entry<Hypothesis,Double> theEntry : aRanker.itsHypothesisAndAlphaList.entrySet()) {
			Element theHypAlpha= new Element("hypothesisAndAlpha");
			Element theAlpha = new Element("alpha");
			theAlpha.setText(theEntry.getValue().toString());
			theHypAlpha.addContent(theAlpha);
			theHypAlpha.addContent(Hypothesis.makeXMLInstance(theEntry.getKey()));
			theElement.addContent(theHypAlpha);
		}
		return theElement;
	}
	
	
	@SuppressWarnings("unchecked")
	public static RankBoostRanker buildFromXMLInstance(Element aElement){
		RankBoostRanker theRB = new RankBoostRanker();
		for(Element theChild: (List<Element>)aElement.getChildren("hypothesisAndAlpha")){
			theRB.itsHypothesisAndAlphaList.put(
					Hypothesis.buildFromXMLInstance(theChild.getChild("hypothesis"))
					, Double.parseDouble(theChild.getChildText("alpha")));
		}
		return theRB;
	}
	

	
	/**
	 * 
	 */
	protected int itsRoundDone;
	
	/**
	 * 
	 */
	public RankBoostRanker(){
		itsHypothesisAndAlphaList= new TreeMap<Hypothesis, Double>();
	}
	
	/**
	 * 
	 * 
	 * @param aRankBoostRanker 
	 */
	public RankBoostRanker(RankBoostRanker aRankBoostRanker){
		itsHypothesisAndAlphaList= new TreeMap<Hypothesis, Double>(aRankBoostRanker.itsHypothesisAndAlphaList);
		itsRoundDone = aRankBoostRanker.itsRoundDone;
	}
	
	/**
	 * return ture if no hypoithesis have been set this ranker
	 * @return
	 */
	public boolean isEmpty(){
		return itsHypothesisAndAlphaList.isEmpty(); 
	}
	
	/**
	 * return hypothesis map.
	 * 
	 * @return 
	 */
	public Map<Hypothesis, Double> getHypothesisMap() {
		return itsHypothesisAndAlphaList;
	}
	
	/**
	 * return the score for a LOM and aLOMRanking.
	 * 
	 * @param aLOM 
	 * @param aRanking 
	 * 
	 * @return 
	 */
	public double getHypothesisFor(LOMRanking aRanking, LOM aLOM) {
		double theSum = ZERO; 
		for (Entry<Hypothesis, Double> theCouple : itsHypothesisAndAlphaList.entrySet()) {
			if (theCouple.getKey().getValueFor(
					new Couple<LOMRanking, LOM>(aRanking, aLOM)) == 1)
			theSum += theCouple.getValue();
		}
		return theSum;
	}

	/**
	 * return number of round calculated for building the hypothesis of this RBRanker.
	 * 
	 * @return 
	 */
	 public int getRoundDone() {
		 return itsRoundDone;
	 }
	 
	 /* (non-Javadoc)
 	 * @see java.lang.Comparable#compareTo(java.lang.Object)
 	 */
 	public int compareTo(RankBoostRanker aO) {
		return new Integer(itsRoundDone).compareTo(aO.getRoundDone());
	}
	 
}