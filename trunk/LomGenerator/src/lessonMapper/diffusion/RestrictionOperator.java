/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion;

import java.io.Serializable;



	/**
	 * this enum matches the operator needed to expres the restrictions.
	 */
	public enum RestrictionOperator implements Serializable{
		
		/**
		 * 
		 */
		INF,
/**
 * 
 */
SUP,
/**
 * 
 */
INF_EQ,
/**
 * 
 */
SUP_EQ,
/**
 * 
 */
EQ,
/**
 * 
 */
DIF,
/**
 * 
 */
CONTAINED,
/**
 * 
 */
CONTAINS

	}
