/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.query.rankboostDFP;

import java.util.HashMap;
import java.util.Map;

import lessonMapper.lom.LOM;
import lessonMapper.query.LOMRanking;
import rossi.dfp.dfp;
import rossi.dfp.dfpmath;
import util.Couple;

/**
 * 
 */
public class RoundDistribution extends BipartiteDistribution {

	/**
	 * 
	 */
	BipartiteDistribution itsPreviousDistribution;
	
	/**
	 * 
	 */
	Hypothesis itsHypothesis;
	
	/**
	 * 
	 */
	dfp itsAlpha;
	

	/**
	 * 
	 * 
	 * @param aAlpha 
	 * @param aPreviousDistribution 
	 * @param aHypothesis 
	 */
	public RoundDistribution(BipartiteDistribution aPreviousDistribution, Hypothesis aHypothesis, dfp aAlpha) {
		super();
		itsPreviousDistribution = aPreviousDistribution;
		itsHypothesis = aHypothesis;
		itsAlpha = aAlpha;
		itsExpectedLOMList = itsPreviousDistribution.itsExpectedLOMList;
		itsWholeLOMList = itsPreviousDistribution.itsWholeLOMList;
		isExpectedMap = itsPreviousDistribution.isExpectedMap;
		initVFunction();
		itsPreviousDistribution=null;
		try{
			aPreviousDistribution.finalize();
		}catch(Throwable e){e.printStackTrace();}
	}

	/**
	 * init vfunction and init z.
	 */
	public void initVFunction(){
		Map<Couple<LOMRanking,LOM>, dfp> theTempV0= new HashMap<Couple<LOMRanking,LOM>, dfp>(), theTempV1 = new HashMap<Couple<LOMRanking,LOM>, dfp>();
		dfp theZ0 = ZERO, theZ1 = ZERO; 
		for (Couple<LOMRanking,LOM> theLOM : getLOMList()) {
			dfp theV; 
			if (isExpected(theLOM)){
				if (itsHypothesis.getValueFor(theLOM)==1)
					theV= itsPreviousDistribution.getV(theLOM).multiply(dfpmath.exp(itsAlpha.negate()));
				else theV= itsPreviousDistribution.getV(theLOM);
				theZ1 = theZ1.add(theV);
				theTempV1.put(theLOM, theV);
			}
			else {
				if (itsHypothesis.getValueFor(theLOM)==1)
					theV =itsPreviousDistribution.getV(theLOM).multiply(dfpmath.exp(itsAlpha));
				else theV= itsPreviousDistribution.getV(theLOM);
				theZ0 = theZ0.add(theV);
				theTempV0.put(theLOM, theV);
			}
		}
		// normalize V
		for (Couple<LOMRanking,LOM> theLOM : theTempV0.keySet()) 
			itsVFunction.put(theLOM, theTempV0.get(theLOM).divide(theZ0));
		for (Couple<LOMRanking,LOM> theLOM : theTempV1.keySet()) 
			itsVFunction.put(theLOM, theTempV1.get(theLOM).divide(theZ1));
	}

	
	
	
	
	
}
