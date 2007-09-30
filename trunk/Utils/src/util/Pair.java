package util;

import java.util.ArrayList;

/**
 * the class Pair is a set limited to 2 elements  
 * @author omotelet
 *
 * @param <A>
 */

public class Pair<A> extends ArrayList<A> {

	
	public Pair(A aElement,A anotherElement) {
		super();
		add(aElement);
		add(anotherElement);
	}

	@Override
	public boolean add(A aO) {
		if (size() <2)
		return super.add(aO);
		else return false;
	}
	
	public A getOne() {
		return super.get(0);
	}
	
	public A getOther() {
		return super.get(1);
	}
	
	 @Override
	public int hashCode() {
		return get(0).hashCode()+get(1).hashCode();
	}
	
	@Override
	public boolean equals(Object aO) {
		if (aO instanceof Pair) {
			Pair theOtherPair = (Pair) aO;
			return theOtherPair.contains(get(0)) && theOtherPair.contains(get(1));
		}
		else return false;
	}
	
}
