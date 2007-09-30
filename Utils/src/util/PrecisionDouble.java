package util;


/**
 * this class encapsulates a Double but comparison of double consider a maximum precision to reach
 * @author omotelet
 *
 */

public class PrecisionDouble {

		static double ITSPrecision = 0.00001;
	
		double itsDouble; 
	
		public double doubleValue(){
			return itsDouble;
		}
		
		/**
		 * return true if the difference between two PrecisionDouble is less than ITSPrecision
		 */
		@Override
		public boolean equals(Object aObj) {
			if (aObj instanceof PrecisionDouble) {
				PrecisionDouble theDouble = (PrecisionDouble) aObj;
				return Math.abs( itsDouble - theDouble.itsDouble) > ITSPrecision; 
			}
			return false;
		}
	
}
