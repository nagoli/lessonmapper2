package util.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

/**
 * make shadow (from Chris Campbell's Blog
 * http://weblogs.java.net/blog/campbell/archive/2006/07/java_2d_tricker_2.html)
 * 
 * @author omotelet
 * 
 */
public class ShadowMaker {

	private static Color getMixedColor(Color c1, float pct1, Color c2,
			float pct2) {
		float[] clr1 = c1.getComponents(null);
		float[] clr2 = c2.getComponents(null);
		for (int i = 0; i < clr1.length; i++) {
			clr1[i] = (clr1[i] * pct1) + (clr2[i] * pct2);
		}
		return new Color(clr1[0], clr1[1], clr1[2], clr1[3]);
	}

	
/**
 * paint a shadow for aShape using a composite of inColor and outColor 
 * Need to be called before painting the shape
 * 
 */
	public static void paintShadow(Graphics2D g2, Shape aShape, float shadowWidth, float strokeWidth, Color inColor, Color outColor) {
	g2.translate(shadowWidth,shadowWidth);
	//g2.scale(0.98, 0.98);
	float sw = shadowWidth * 2 + strokeWidth;
	g2.setColor(getMixedColor(inColor, 0.5f, outColor, 0.5f));
	g2.setStroke(new BasicStroke(sw));
	g2.draw(aShape);
	g2.setColor(getMixedColor(inColor, 0.75f, outColor, 0.25f));
	g2.setStroke(new BasicStroke(sw-(shadowWidth*2)));
	g2.draw(aShape);
	
//	
//	for (float i = sw; i >= sw /*strokeWidth*/; i -= precision) {
//			float pct = (float) (sw - i) / (sw - 1f);
//			g2.setColor(getMixedColor(inColor, pct, outColor,
//					1.0f - pct));
//			g2.setStroke(new BasicStroke(i));
//			g2.draw(aShape);
//		}
//		
	//	g2.scale(1/0.98, 1/0.98);
		g2.translate(-shadowWidth,-shadowWidth);
	}

//	public static void paintShadow(Graphics2D g2, Shape aShape, float shadowWidth,float precision) {
//		paintShadow(g2, aShape, shadowWidth, Color.LIGHT_GRAY, Color.white,precision);
//	}
//	
	
	
	
}
	
	

