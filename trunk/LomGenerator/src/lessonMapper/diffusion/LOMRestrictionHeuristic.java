/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMValue;



/**
 * User: omotelet
 * Date: Apr 11, 2005
 * Time: 5:50:42 PM.
 */
public interface LOMRestrictionHeuristic extends Heuristic {

    /**
     * 
     */
    public final RestrictionOperator EQ = RestrictionOperator.EQ;
    
    /**
     * 
     */
    public final RestrictionOperator SUP_EQ = RestrictionOperator.SUP_EQ;
    
    /**
     * 
     */
    public final RestrictionOperator INF_EQ = RestrictionOperator.INF_EQ;
    
    /**
     * 
     */
    public final RestrictionOperator SUP = RestrictionOperator.SUP;
    
    /**
     * 
     */
    public final RestrictionOperator INF = RestrictionOperator.INF;
    
    /**
     * 
     */
    public final RestrictionOperator DIF = RestrictionOperator.DIF;
    
    /**
     * 
     */
    public final RestrictionOperator CONTAINS = RestrictionOperator.CONTAINS;
    
    /**
     * 
     */
    public final RestrictionOperator CONTAINED = RestrictionOperator.CONTAINED;


    /**
     * process the heuristic.
     * 
     * @param vij 
     * @param oij 
     * @param li 
     * 
     * @return 
     */
    public  boolean process(LOM li, RestrictionOperator oij, LOMValue vij);

    /**
     * 
     * 
     * @return 
     */
    public  LOMRestrictionValue getRestrictionValue();

    
    
}