/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMValue;

/**
 * User: omotelet
 * Date: Apr 11, 2005
 * Time: 5:50:28 PM.
 */
public interface LOMSuggestionHeuristic extends Heuristic {

    /**
     * process the heuristic.
     * 
     * @param vi 
     * @param li 
     * 
     * @return 
     */
    public boolean process(LOM li, LOMValue vi);



    /**
     * 
     * 
     * @return 
     */
    public LOMValue getSuggestionValue();
}