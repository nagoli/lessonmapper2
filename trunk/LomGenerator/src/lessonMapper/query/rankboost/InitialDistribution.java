/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.query.rankboost;

import java.util.List;

import lessonMapper.lom.LOM;
import lessonMapper.query.LOMRanking;
import util.Couple;

/**
 * initial distribution is based on the bipartite rankboost definition:
 * we have to disjoint subset X0 and X1 respectively the not expected lom set and the expected lom set
 * this distribution will rank all X1 over X0 and say nothing about the others.
 * 
 * @author omotelet
 */
public class InitialDistribution extends BipartiteDistribution {

	
  
	/**
	 * 
	 * 
	 * @param aWholeLOMList 
	 * @param aExpectedLOMList 
	 */
	public InitialDistribution(List<Couple<LOMRanking,LOM>> aExpectedLOMList, List<Couple<LOMRanking,LOM>> aWholeLOMList) {
		super(); 
		itsExpectedLOMList = aExpectedLOMList;
		itsWholeLOMList = aWholeLOMList;
		for (Couple<LOMRanking,LOM> theLom : itsExpectedLOMList) {
			itsVFunction.put(theLom, ONE/(1.0*itsExpectedLOMList.size()));
			isExpectedMap.put(theLom, true);
		}
		for (Couple<LOMRanking,LOM> theLom : itsWholeLOMList) 
			if (!itsVFunction.containsKey(theLom)){
				itsVFunction.put(theLom, ONE/(1.0*(itsWholeLOMList.size()-itsExpectedLOMList.size())));
				isExpectedMap.put(theLom, false);
			}
	}

	
	
	
	
}
