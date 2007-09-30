package util;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;


/**
 * implementation of a class ordered considering the score of its element
 * it also enables to look for the element by rank or having the rank of an element
 * only one object can be added next times a same object is added even with a different score it is not taken into account
 * @author omotelet
 *
 * @param <C>
 */
public class SortedScoreRankList<C> {

	public static final int ITSPrecision = 4;

	public static final int firstRank = 1;

	public static Comparator<Integer> ITSReverseComparator;
	static {
		ITSReverseComparator = new Comparator<Integer>() {
			public int compare(Integer aO1, Integer aO2) {
				//avoid double precision problems
				return -1 * aO1.compareTo(aO2);
			}
		};
	}

	public static Comparator<Integer> ITSReverseIntegerComparator;
	static {
		ITSReverseIntegerComparator = new Comparator<Integer>() {
			public int compare(Integer aO1, Integer aO2) {
				return -1 * aO1.compareTo(aO2);
			}
		};
	}

	public static Comparator<Point2D> ITSResultPositionComparator = new Comparator<Point2D>() {
		public int compare(Point2D o1, Point2D o2) {
			return o1.getY() < o2.getY() ? 1
					: (o1.getY() == o2.getY() ? 0 : -1);
		};
	};

	private boolean isNaturalOrder, isNormalized;

	public TreeMap<Integer, List<C>> itsSortedScoreToObjectMap;

	public TreeMap<Integer, Integer> itsSortedRankToScoreMap;

	public HashMap<C, Integer> itsObjectToRankMap;

	public HashMap<Integer, Integer> itsScoreToRankMap;

	public SortedScoreRankList() {
		this(true);
	}

	/**
	 * in natural order
	 * best rank (i.e. 1) has better score
	 * in distanceorder 
	 * best rank (i.e 1) has minimum score
	 * @param isNaturalOrder
	 */
	public SortedScoreRankList(boolean isNaturalOrder) {
		this(isNaturalOrder,false);
	}
	
	
	/**
	 * in natural order
	 * best rank (i.e. 1) has better score
	 * in distanceorder 
	 * best rank (i.e 1) has minimum score
	 * 
	 * if isNormalized the getNormalizedScore has no effects
	 * @param isNaturalOrder
	 */
	public SortedScoreRankList(boolean isNaturalOrder,boolean isNormalized ) {
		this.isNormalized = isNormalized;
		this.isNaturalOrder = isNaturalOrder;
		if (isNaturalOrder)
			itsSortedScoreToObjectMap = new TreeMap<Integer, List<C>>(
					ITSReverseComparator);
		else
			itsSortedScoreToObjectMap = new TreeMap<Integer, List<C>>();
		itsSortedRankToScoreMap = new TreeMap<Integer, Integer>();
		itsObjectToRankMap = new HashMap<C, Integer>();
		itsScoreToRankMap = new HashMap<Integer, Integer>();
	}

	/**
	 * add aC and aScore associated do not add if aC is null or aC already exists
	 * @param aC
	 * @param aScore
	 */
	public void add(C aC, double aScore) {
		//todo rank should be last rank not first rank
		if (aC == null || itsObjectToRankMap.containsKey(aC))
			return;
		Integer theScore = roundScore(aScore);
		boolean isFirst = itsObjectToRankMap.isEmpty();
		boolean isScoreAlreadyPresent = itsScoreToRankMap.containsKey(theScore);
		SortedMap<Integer, List<C>> theTailMap = itsSortedScoreToObjectMap
				.tailMap(nextScore(theScore));

		int rank = 0;
		if (!isScoreAlreadyPresent) {
			if (isFirst)
				rank = firstRank;
			else if (theTailMap.size() > 0)
				rank = itsScoreToRankMap.get(theTailMap.firstKey())
						- (itsSortedScoreToObjectMap.get(theTailMap.firstKey())
								.size() - 1);
			else {
				Integer theLastKey = itsSortedScoreToObjectMap.lastKey();
				rank = itsScoreToRankMap.get(theLastKey) + 1;
			}
			itsScoreToRankMap.put(theScore, rank);
			itsObjectToRankMap.put(aC, rank);
		}

		List<C> theList;
		if (!isScoreAlreadyPresent) {
			theList = new ArrayList<C>();
			itsSortedScoreToObjectMap.put(theScore, theList);
		} else
			theList = itsSortedScoreToObjectMap.get(theScore);
		theList.add(aC);

		//	put the tail in reverse order
		List<Integer> theTailKeyList = new ArrayList<Integer>(theTailMap
				.keySet());
		if (isScoreAlreadyPresent) //add 1 to all the elment of the same score
			theTailKeyList.add(theScore);
		if (isNaturalOrder)
			Collections.sort(theTailKeyList);
		else
			Collections.sort(theTailKeyList, ITSReverseIntegerComparator);

		//	add 1 to the rank of all the element ranked after
		for (Integer theScore2 : theTailKeyList) {
			int rankToIncrement = itsScoreToRankMap.get(theScore2);
			itsScoreToRankMap.put(theScore2, rankToIncrement + 1);
			itsSortedRankToScoreMap.remove(rankToIncrement);
			itsSortedRankToScoreMap.put(rankToIncrement + 1, theScore2);
			for (C theC : itsSortedScoreToObjectMap.get(theScore2))
				itsObjectToRankMap.put(theC, rankToIncrement + 1);
		}
		if (!isScoreAlreadyPresent)
			itsSortedRankToScoreMap.put(rank, theScore);
	}

	/**
	 * round score to avoid precision problem
	 * @param aScore
	 * @return
	 */
	private Integer roundScore(double aScore) {
		return (int) Math.rint(aScore * Math.pow(10, ITSPrecision));
	}

	/**
	 * add or remove epsilon to the score dependiente del orden
	 * @param aScore
	 * @return
	 */
	private Integer nextScore(Integer aScore) {
		return isNaturalOrder ? aScore - 1 : aScore + 1;
	}

	/**
	 * return the objects associated to a rank
	 * @param aRank
	 * @return
	 */
	public List<C> getObjectsForRank(int aRank) {
		Integer theInteger = itsSortedRankToScoreMap.get(aRank);
		if (theInteger != null)
			return itsSortedScoreToObjectMap.get(theInteger);
		return null;
	}

	/**
	 * contains aC ?
	 * @param aC
	 * @return
	 */
	public boolean contains(C aC) {
		return itsObjectToRankMap.containsKey(aC);
	}

	/**
	 * return the object associated to aScore
	 * @param aScore
	 * @return
	 */
	public List<C> getObjectsForScore(double aScore) {
		return itsSortedScoreToObjectMap.get(roundScore(aScore));
	}

	/**
	 * return the objects having aScore between aFirstScore inclusive and aSecondScore exclusive
	 * @param aFirstScore
	 * @param aSecondScore
	 * @return
	 */
	public List<C> getObjectsForScores(double aFirstScore, double aSecondScore) {
		Integer theFirstScore = roundScore(aFirstScore);
		Integer theSecondScore = roundScore(aSecondScore);
		List<C> theList = new ArrayList<C>();
		for (List<C> theCs : itsSortedScoreToObjectMap.subMap(theFirstScore,
				theSecondScore).values())
			theList.addAll(theCs);
		return theList;
	}
	
	/**
	 * return the objects having aScore between aFirstScore inclusive and aSecondScore exclusive
	 * @param aFirstScore
	 * @param aSecondScore
	 * @return
	 */
	public List<C> getObjectsForNormalizedScores(double aFirstScore, double aSecondScore) {
		if(isNormalized) return getObjectsForScores(aFirstScore, aSecondScore);
		if (!isNaturalOrder) {
			System.out.println("NOT IMPLEMENTED FOR NOT NATURAL ORDER");
			return null;
		}
		Integer theFirstScore = roundScore(unNormalizedScore(aFirstScore));
		Integer theSecondScore = roundScore(unNormalizedScore(aSecondScore));
		List<C> theList = new ArrayList<C>();
		for (List<C> theCs : itsSortedScoreToObjectMap.subMap(theFirstScore,
				theSecondScore).values())
			theList.addAll(theCs);
		return theList;
	}

	/**
	 * return the objects having aScore between aFirstScore inclusive and aSecondScore exclusive
	 * @param aFirstRank
	 * @param aSecondRank
	 * @return
	 */
	public List<C> getObjectsForRanks(int aFirstRank, int aSecondRank) {
		List<C> theList = new ArrayList<C>();
		for (Integer theScore : itsSortedRankToScoreMap.subMap(aFirstRank,
				aSecondRank).values())
			theList.addAll(itsSortedScoreToObjectMap.get(theScore));
		return theList;
	}

	/**
	 * return the rank of aC 
	 * return -1 if not defined
	 * @param aC
	 * @return
	 */
	public int getRankFor(C aC) {
		Integer theInteger = itsObjectToRankMap.get(aC);
		if (theInteger != null)
			return theInteger;
		return -1;
	}

	/**
	 * return the score for aC 
	 * return next to the last
	 * @param aC
	 * @return
	 */
	public double getScoreFor(C aC) {
		try {
			if (isEmpty()) return 0;
			int theScore;
			if (itsObjectToRankMap.containsKey(aC)) {
				theScore = itsSortedRankToScoreMap.get(itsObjectToRankMap
						.get(aC));
			} else
				theScore = nextScore(itsSortedScoreToObjectMap.lastKey());
			return getDoubleScore(theScore);
		} catch (Exception e) {
			System.out.println("*****************WARNING**************");
			return 0;
		}
	}

	/**
	 * return the score for aC 
	 * return next to the last
	 * @param aC
	 * @return
	 */
	public String getScoreStringFor(C aC) {
		try {
			if (isEmpty()) return "0";
			int theScore;
			if (itsObjectToRankMap.containsKey(aC)) {
				theScore = itsSortedRankToScoreMap.get(itsObjectToRankMap
						.get(aC));
			} else
				theScore = nextScore(itsSortedScoreToObjectMap.lastKey());
			return new BigDecimal(theScore).divide(new BigDecimal(10).pow(ITSPrecision)).toPlainString();
		} catch (Exception e) {
			System.out.println("*****************WARNING**************");
			return "0";
		}
	}
	
	private double getDoubleScore(int theScore) {
		return ((double) theScore) * Math.pow(10, -1 * ITSPrecision);
	}

	/**
	 * build the recall precision values for this result set
	 * @param aResultList
	 * @return
	 */
	public List<Point2D.Double> getRecallPrecisionList(
			List<C> aExpectedObjectList) {
		List<Point2D.Double> theRecallPrecisionList = new ArrayList<Point2D.Double>();
		int theExpectedResultNb = aExpectedObjectList.size();
		int theRetrievedExpectedResultNb = 0;
		for (Entry<Integer, List<C>> theEntry : itsSortedScoreToObjectMap
				.entrySet()) {
			for (C theC : theEntry.getValue())
				if (aExpectedObjectList.contains(theC))
					theRetrievedExpectedResultNb++;
			double precision = theRetrievedExpectedResultNb * 1.0
					/ (itsScoreToRankMap.get(theEntry.getKey()));
			//double precision = theRetrievedExpectedResultNb*1.0/itsScoreToRankMap.get(theEntry.getKey());
			double recall = theRetrievedExpectedResultNb * 1.0
					/ theExpectedResultNb;
			theRecallPrecisionList.add(new Point2D.Double(recall, precision));
		}
		return theRecallPrecisionList;
	}

	protected static List<Point2D.Double> getRecallPrecisionList(
			List aResultList, List itsExpectedLOMIDs) {
		List<Point2D.Double> theRecallPrecisionList = new ArrayList<Point2D.Double>();
		int theExpectedResultNb = itsExpectedLOMIDs.size();
		int theRetrievedExpectedResultNb = 0;
		int theResultNb = 0;
		for (Object theC : aResultList) {
			theResultNb++;
			if (itsExpectedLOMIDs.contains(theC))
				theRetrievedExpectedResultNb++;
			double precision = theRetrievedExpectedResultNb * 1.0 / theResultNb;
			double recall = theRetrievedExpectedResultNb * 1.0
					/ theExpectedResultNb;
			theRecallPrecisionList.add(new Point2D.Double(recall, precision));
		}
		return theRecallPrecisionList;
	}

	public List<Point2D> getRelevantResultPositions(List<C> theExpectedResults) {
		List<Point2D> theRelevantRetrievalLOMNb = new ArrayList<Point2D>();
		int i = 1;
		for (C theCs : theExpectedResults) {
			theRelevantRetrievalLOMNb.add(new Point(i, getRankFor(theCs)));
			i++;
		}
		Collections
				.sort(theRelevantRetrievalLOMNb, ITSResultPositionComparator);
		return theRelevantRetrievalLOMNb;
	}

	public List<Point2D> getRelevantResultScores(List<C> theExpectedResults) {
		List<Point2D> theRelevantRetrievalLOMNb = new ArrayList<Point2D>();
		int i = 1;
		for (C theCs : theExpectedResults) {
			theRelevantRetrievalLOMNb.add(new Point2D.Double(i,
					getScoreFor(theCs)));
			i++;
		}
		Collections
				.sort(theRelevantRetrievalLOMNb, ITSResultPositionComparator);
		return theRelevantRetrievalLOMNb;
	}

	public boolean isEmpty() {
		return itsSortedScoreToObjectMap.isEmpty();
	}

	public double getMaximumScore() {
		int theScore = getMaximumIntScore();
		return getDoubleScore(theScore);
	}
	
	public double getMaximumRank() {
		if (isEmpty())
			return 1;
		return itsSortedRankToScoreMap.lastKey();
	}
	

	private int getMaximumIntScore() {
		if (isEmpty())
			return 0;
		int theScore;
		if (isNaturalOrder)
			theScore = itsSortedScoreToObjectMap.firstKey();
		else
			theScore = itsSortedScoreToObjectMap.lastKey();
		return theScore;
	}

	public double getMinimumScore() {
		int theScore = getMinimumIntScore();
		return getDoubleScore(theScore);
	}

	private int getMinimumIntScore() {
		if (isEmpty())
			return 0;
		int theScore;
		if (isNaturalOrder)
			theScore = itsSortedScoreToObjectMap.lastKey();
		else
			theScore = itsSortedScoreToObjectMap.firstKey();
		return theScore;
	}

	/*
	 * return aScore between 0.02 and 1 in natural order 
	 * or between 0.01 and 0.99 in reverse order 
	 */
	public double getNormalizedScoreFor(C aC) {
		if (isNormalized) return getScoreFor(aC);
		if (!itsObjectToRankMap.containsKey(aC))
			if (isNaturalOrder)
				return 0.01;
			else
				return 1;
		int theScore = itsSortedRankToScoreMap.get(itsObjectToRankMap.get(aC));
		int theMin = getMinimumIntScore();
		int theMax = getMaximumIntScore();
		int theNormalizedMin, theNormalizedMax;
		if (isNaturalOrder) {
			theNormalizedMin = roundScore(0.02);
			theNormalizedMax = roundScore(1);
		} else {
			theNormalizedMin = roundScore(0.01);
			theNormalizedMax = roundScore(0.99);
		}
		theScore = theScore + (theNormalizedMin - theMin);
		theScore = theNormalizedMax * theScore
				/ (theMax + (theNormalizedMin - theMin));
		return getDoubleScore(theScore);
	}
	
	/**
	 * return the unNormalized score for a score 
	 * WARNING precision loss due to rounding make the normalization function not bijective...
	 * @param theScore
	 * @return
	 */
	public double unNormalizedScore(double aScore){
		double theMin = getMinimumScore();
		double theMax = getMaximumScore();
		double theNormalizedMin,theNormalizedMax ;
		if (isNaturalOrder) {
			theNormalizedMin = 0.02;
			theNormalizedMax = 1;
		} else {
			theNormalizedMin = 0.01;
			theNormalizedMax = 0.99;
		}
		double theScore = aScore * (theMax + (theNormalizedMin - theMin)) / theNormalizedMax;
		theScore = theScore + theMin - theNormalizedMin;
		return theScore;
	}
	
	
	/*
	 * return aScore between 0.02 and 1 in natural order 
	 */
	public double getNormalizedScoreInNaturalOrderFor(C aC) {
		if (isNormalized)
			if (!isNaturalOrder) {
				System.out.println("getNormalizedScoreInOrderFor not implemented for not natural order and normalized list");
				return 0.02;
			}
			else return getScoreFor(aC);
		if (isNaturalOrder) return getNormalizedScoreFor(aC);
		if (!itsObjectToRankMap.containsKey(aC))
			return 0.01;
		int theScore = -1*itsSortedRankToScoreMap.get(itsObjectToRankMap.get(aC));
		int theMin = -1*getMaximumIntScore();
		int theMax = -1*getMinimumIntScore();
		int theNormalizedMin, theNormalizedMax;
		theNormalizedMin = roundScore(0.02);
		theNormalizedMax = roundScore(1);
		theScore = theScore + (theNormalizedMin - theMin);
		theScore = theNormalizedMax * theScore
				/ (theMax + (theNormalizedMin - theMin));
		return getDoubleScore(theScore);
	}
	
	public List<String>  diff(SortedScoreRankList<C> aRanking) {
		List<String> theDiff= new ArrayList<String>();
		for(C theC : itsObjectToRankMap.keySet()){
			Integer theRank1 = itsObjectToRankMap.get(theC);
			Integer theRank2 = aRanking.itsObjectToRankMap.get(theC);
			if (!theRank1.equals(theRank2)) theDiff.add( theC.toString() +"--"+theRank1+"<>"+theRank2);    
		}
		return theDiff;
	}
	
	
	

	public static void main(String[] args) {
		SortedScoreRankList<String> theList = new SortedScoreRankList<String>(
				true);
		theList.add("3", 3.123451121);
		theList.add("4", 4.1234499999);
		theList.add("1", 1.12345000001);
		theList.add("3bis", 3.12);
		theList.add("2", 2);
		theList.add("3-3", 3.18478987);
		theList.add("3-4", 3.548987);
		theList.add("3-5", 3.4887);
		theList.add("3-6", 3.1234566621);
		theList.add("4-2", 3.18511111);
		theList.add("3test", 3.1234566621);
		theList.add("78", -5.0);
		System.out.println(theList.getNormalizedScoreFor("78"));
		theList.unNormalizedScore(theList.getNormalizedScoreFor("3-3"));
		System.out.println(theList.getRankFor("2"));
		System.out.println(theList.getScoreFor("35"));
		System.out.println(theList.getObjectsForRank(3));
		System.out.println(theList.getObjectsForScore(1.23456444));
		System.out.println(theList.getObjectsForRanks(2, 5));
		System.out.println(theList.getObjectsForScores(5, 4));
		ArrayList<String> theArrayList = new ArrayList<String>();
		theArrayList.add("3");
		theArrayList.add("1");

		List<Point2D.Double> theRecallPrecisionList = theList
				.getRecallPrecisionList(theArrayList);
		System.out.println(theRecallPrecisionList);
		System.out.println(IRUtil.interpolateRecallPrecisionList(
				theRecallPrecisionList, 0.05));
		ArrayList<String> theArrayList2 = new ArrayList<String>();
		theArrayList2.add("4");
		theArrayList2.add("3");
		theArrayList2.add("3bis");
		theArrayList2.add("2");
		theArrayList2.add("1");
		List<Point2D.Double> theRecallPrecisionList2 = getRecallPrecisionList(
				theArrayList2, theArrayList);
		System.out.println(theRecallPrecisionList2);
		System.out.println(IRUtil.interpolateRecallPrecisionList(
				theRecallPrecisionList2, 0.05));

	}

}
