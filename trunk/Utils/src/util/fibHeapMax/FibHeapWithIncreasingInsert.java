package util.fibHeapMax;

import java.util.HashMap;
import java.util.Map;

/**
 * This class extends FibHeap in such manner than inserting a element already existing correspong to a decrease
 * 
 * @author omotelet
 *
 */


public class FibHeapWithIncreasingInsert<T extends DynamicSetElement> extends FibonacciHeap<T>{

	
	Map<T, FibonacciHeapNode<T>> itsMap = new HashMap<T, FibonacciHeapNode<T>>();
	
	
	
	public FibHeapWithIncreasingInsert() {
		super();
	}
	
	
	@Override
	public FibonacciHeapNode<T> removeMax() {
		FibonacciHeapNode<T> theMaxElement =  super.removeMax();
		itsMap.remove(theMaxElement);
		return theMaxElement;
	}
	
	
	/**
	 * 
	 */
	
	public void insert(T aE) {
		if (itsMap.containsKey(aE)){
			FibonacciHeapNode<T> theNode = itsMap.get(aE);
			increaseKey(theNode, aE.getKey());	
			//System.out.println("I decreased"+ aE);
			return;
		}
		FibonacciHeapNode<T> theNode = new FibonacciHeapNode<T>(aE,aE.getKey());
		super.insert(theNode,aE.getKey());
		itsMap.put(aE, theNode);
	}
	
	
}
