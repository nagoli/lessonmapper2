package util;

import java.util.ArrayList;
import java.util.List;


public class Utils {

	
	public static double[] toDoubleArray(List<Double> theList) {
		double[] theArray = new double[theList.size()];
		int i=0;
		for (double theDouble : theList) {
			theArray[i]=theDouble;
			i++;
		}
		return theArray;
	}
	
	
	
	/**
	 * return a new string based on aString but 
	 * with inserted the expression anExpression 
	 * all aNumber characters
	 */
	public static String insertInString(String aString, String aExpression, int aNumber){
		int theLength = aString.length();
		String theNewString="";
		int i=0;
		for(; i+aNumber<theLength;i+=aNumber){
			theNewString += aString.substring(i,i+aNumber)+aExpression;
		}
		theNewString+=aString.substring(i,theLength);
		return theNewString;
	}
	
	
	/**
	 *retun a list of string list corresponding to all the combination posible of
	 *n element for aList
	 * @param aList
	 * @param n
	 * @return
	 */
	public static List<List<String>> combinationsOf(List<String> aList, int n){
		if (aList.size()<n || n<=0)  return null;
		List<List<String>> theCombinations = new ArrayList<List<String>>();
		for (String theString : aList) {
			if (n==1) {
				List<String> theCombination =  new ArrayList<String>();
				theCombination.add(theString);
				theCombinations.add(theCombination);
			}else {
				List<String> theSubList = aList.subList(aList.indexOf(theString)+1,aList.size());
				List<List<String>> theSubCombinations = combinationsOf(theSubList,n-1);
				if (theSubCombinations!=null)
					for (List<String> theSubCombination : theSubCombinations) {
						List<String> theCombination =  new ArrayList<String>();
						theCombination.add(theString);
						theCombination.addAll(theSubCombination);
						theCombinations.add(theCombination);
					}
			}
		}	
		return theCombinations;
	}
			
	/**
	 * return the sum of the sizes of the sublist
	 * @param aListofList
	 * @return
	 */
	public static int elementNb(List<List<String>> aListofList) {
		int theNb=0;
		for (List theList : aListofList) {
			theNb+=theList.size();
		}
		return theNb;
	}
	
	/**
	 * return the similarity between two lists of elements
	 * return 0 if the two list are empty;
	 * WARNING: this method is not resistant to repetitions of elements
	 * inside a same list
	 * @param aList
	 * @param anotherList
	 * @return
	 */
	public static double listSimilarity(List aList, List anotherList){
		if (aList.isEmpty() || anotherList.isEmpty()) return 0;
		int distance = 0;
		for(Object a : aList){
			if (anotherList.contains(a)) distance++; 
		}
		return 1.0*distance / Math.sqrt(aList.size()*anotherList.size());
	}
	
	/**
	 * return the similarity proba between two lists of elements
	 * return 0 if the two list are empty;
	 * calculate the propbability of anotherList given aList
	 * WARNING: this method is not resistant to repetitions of elements
	 * inside a same list
	 * @param aList
	 * @param anotherList
	 * @return
	 */
	public static double listSimilarityProba(List aList, List anotherList){
		if ( aList.isEmpty()) return 0;
		int intersection = 0;
		for(Object a : anotherList){
			if (aList.contains(a)) intersection++; 
		}
		return 1.0*intersection / aList.size();
	}
	
	
	public static void main(String[] args) {
		List<String> theTest = new ArrayList<String>();
		theTest.add("a");
		theTest.add("b");
		theTest.add("c");
		theTest.add("d");
		theTest.add("e");
		List<String> theTest2 = new ArrayList<String>();
		theTest2.add("a");
		theTest2.add("b");
		theTest2.add("d");
		theTest2.add("c");
		theTest2.add("e");
		System.out.println(listSimilarity(theTest,theTest2));
	}
}
