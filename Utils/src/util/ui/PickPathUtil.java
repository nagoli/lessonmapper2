package util.ui;

import java.util.Iterator;

import edu.umd.cs.piccolo.util.PPickPath;
import edu.umd.cs.piccolo.PNode;
public class PickPathUtil {

	public static PNode getFirstNodeOfType(PPickPath aPickPath,Class<? extends PNode> aClass){
		for (Iterator theIterator = aPickPath.getNodeStackReference().iterator(); theIterator
				.hasNext();) {
			PNode theNode = (PNode) theIterator.next();
			if (aClass.isInstance(theNode))
				return theNode;
		}
		return null;
	}	
	
	
}
