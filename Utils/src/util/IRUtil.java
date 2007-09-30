package util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flanagan.analysis.Stat;

public class IRUtil {

	public static List<Point2D.Double> interpolateRecallPrecisionList(
			List<Point2D.Double> aRecallPrecisionList, double interpolationStep) {
		List<Point2D.Double> theInterpolatedRecallPrecisionList = new ArrayList<Point2D.Double>();
		// create a map (i,j) where j is the max precision of all the recall
		// precision couple
		// of aRecallPrecisionList for which the recall is in
		// [i,i+interpolationStep]
		Map<Double, Double> theMap = new HashMap<Double, Double>();
		for (Point2D.Double thePoint : aRecallPrecisionList) {
			double theRecall = ((int) (thePoint.getX() / interpolationStep))
					* interpolationStep;
			theRecall = round(theRecall);
			double thePrecision = 0;
			if (theMap.containsKey(theRecall))
				thePrecision = theMap.get(theRecall);
			thePrecision = Math.max(thePrecision, thePoint.getY());
			theMap.put(theRecall, thePrecision);
		}
		double previousPrecision = 0;
		for (double recall = 1; recall >= 0; recall = round(recall
				- interpolationStep)) {
			double precision = 0;
			if (theMap.containsKey(recall))
				precision = theMap.get(recall);
			precision = Math.max(precision, previousPrecision);
			theInterpolatedRecallPrecisionList.add(new Point2D.Double(recall,
					precision));
			previousPrecision = precision;
		}
		return theInterpolatedRecallPrecisionList;
	}

	/**
	 * round in base 100
	 * 
	 * @param aDouble
	 * @return
	 */
	public static double round(double aDouble) {
		return Math.round(aDouble * 100) * 1.0 / 100;
	}

	public static List<Point2D.Double> calculateMeanRP(
			List<List<Point2D.Double>> theRPList, double interpolationStep) {
		List<Point2D.Double> theMeanRP = new ArrayList<Point2D.Double>();
		Map<Double, double[]> theMap = new HashMap<Double, double[]>();

		for (List<Point2D.Double> theList : theRPList) {
			int i =0;
			for (Point2D.Double thePoint : theList) {
				if (!theMap.containsKey(thePoint.getX()))
					theMap.put(thePoint.getX(), new double[(int)Math.round(1/interpolationStep)+1]);
				theMap.get(thePoint.getX())[i]=thePoint.getY();
				i++;
			}
		}
		
		for (double theRecall = 0; theRecall <= 1; theRecall = round(theRecall
				+ interpolationStep)) {
			double thePrecision = Stat.mean(theMap.get(theRecall));
			theMeanRP.add(new Point2D.Double(theRecall,thePrecision));
		}
		return theMeanRP;
	}

}
