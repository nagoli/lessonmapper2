package util;

import flanagan.math.Fmath;

public class Proba {

	/**
	 * number of combination of n things taken k at a time
	 * @param k
	 * @param n
	 */
	public static int combination(int n, int k) {
		if (k>n) return 0;
		return Fmath.factorial(n)/(Fmath.factorial(n-k)*Fmath.factorial(k));
		
	}
	
	
	/**
	 * calculus of hypergeometric probability for  P(number=k)
	 * N = total number of elements
	 * D = total number of expected elements
	 * n = number of taken elements
	 * k = number of taken expected element
	 * @param k
	 * @param N
	 * @param D
	 * @param n
	 * @return
	 */
	public static double hypergeometric(int k, int N, int D, int n) {
		return combination(D,k)*combination(N-D,n-k)*1.0/(1.0*combination(N,n));
	}
	
	/**
	 * calculate the tail probability for P(number>=k)
	 * @param k
	 * @param N
	 * @param D
	 * @param n
	 * @return
	 */
	public static double hypergeometricSupTail(int k, int N, int D, int n) {
		double theProb = 0;  
		for (int k2 =k; k2<=n && k2<=n;k2++)
			theProb+=hypergeometric(k2,N,D,n);
		return theProb;
	}
	
}
