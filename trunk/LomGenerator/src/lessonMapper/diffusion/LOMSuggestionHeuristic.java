/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
