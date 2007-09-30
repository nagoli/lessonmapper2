/*
 * LessonMapper 2.
Copyright (C) Olivier Motelet.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package dcc.lessonMapper2.ui;

import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.graph.element.SelectionableNode;
import dcc.lessonMapper2.ui.graph.element.TypedEdge;
import dcc.lessonMapper2.ui.graph.element.lom.GenericActivity;
import dcc.lessonMapper2.ui.graph.element.lom.GenericMaterial;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

public aspect PaintSpeeder {

	static double drawnChildrenLimit = 3;
	static double highQualityLimit = 0.8;
	
	static boolean isHiddenChild(PNode aNode) {
		return (aNode instanceof SelectionableNode ||
				aNode instanceof TypedEdge ||
				aNode instanceof PImage);
	}
	
	
	void around(GenericActivity aNode, PPaintContext paintContext): 
		execution(void GenericActivity+.fullPaint(PPaintContext))
		&& target(aNode) && args(paintContext) {
		
		if (paintContext.getScale()>drawnChildrenLimit || LessonMapper2.getInstance().getUpperActivity()==aNode) {
			//System.out.println("isproceeded "+aNode.getDisplayedTitle() + paintContext.getScale());
			proceed(aNode,paintContext);
			return;
		}
		else{
			//System.out.println("ismodifyed "+aNode.getDisplayedTitle()+paintContext.getScale());
			if (aNode.getVisible() && aNode.fullIntersects(paintContext.getLocalClip())) {			
				paintContext.pushTransform(aNode.getTransform());
				paintContext.pushTransparency(aNode.getTransparency());
				if (!aNode.getOccluded())
					aNode.paint(paintContext);
				int count = aNode.getChildrenCount();		
				for (int i = 0; i < count; i++) {
					PNode each = (PNode) aNode.getChildrenReference().get(i);
					if (!isHiddenChild(each)) 
						each.fullPaint(paintContext);
					//else	System.out.println("not printed "+each);
					
				}				
				paintContext.popTransparency(aNode.getTransparency());
				paintContext.popTransform(aNode.getTransform());
			}
		}
	}
	
	void around(GenericMaterial aNode, PPaintContext paintContext): 
		execution(void GenericMaterial+.fullPaint(PPaintContext))
		&& target(aNode) && args(paintContext) {
		
		if (paintContext.getScale()>drawnChildrenLimit) {
			//System.out.println("isproceeded "+aNode.getDisplayedTitle() + paintContext.getScale());
			proceed(aNode,paintContext);
			return;
		}
		else{
			//System.out.println("ismodifyed "+aNode.getDisplayedTitle()+paintContext.getScale());
			if (aNode.getVisible() && aNode.fullIntersects(paintContext.getLocalClip())) {			
				paintContext.pushTransform(aNode.getTransform());
				paintContext.pushTransparency(aNode.getTransparency());
				if (!aNode.getOccluded())
					aNode.paint(paintContext);
				int count = aNode.getChildrenCount();		
				for (int i = 0; i < count; i++) {
					PNode each = (PNode) aNode.getChildrenReference().get(i);
					if (!isHiddenChild(each)) 
						each.fullPaint(paintContext);
					//else	System.out.println("not printed "+each);
					
				}				
				paintContext.popTransparency(aNode.getTransparency());
				paintContext.popTransform(aNode.getTransform());
			}
		}
	}
	
	before(PNode aNode, PPaintContext paintContext): 
		execution(void PNode+.paint(PPaintContext))
		&& target(aNode) && args(paintContext) {
		if(paintContext.getScale()<highQualityLimit)
			paintContext.setRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
		else paintContext.setRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
		
	}
}
