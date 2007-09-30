package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;



/**
 * implementation of arraylist avoiding double element!
 * it is used instead of hashset when determinism is necesary
 * @author omotelet
 *
 * @param <C>
 */
public class ListWithoutDouble<C> extends ArrayList<C> {

	HashSet<C> itsSet = new HashSet<C>();
	
	public ListWithoutDouble() {
		super();
	}

	public ListWithoutDouble(Collection<? extends C> aC) {
		super();
		addAll(aC);
	}

	public ListWithoutDouble(int aInitialCapacity) {
		super(aInitialCapacity);
	}

	@Override
	public boolean add(C aO) {
		if (itsSet.contains(aO))
			return false; 
		itsSet.add(aO);
		return super.add(aO);
	}
	

	@Override
	public boolean addAll(Collection<? extends C> aC) {
		boolean theBool = true;
		for (C theC : aC) {
			theBool &= add(theC);
		}
		return theBool;
	}
	
	@Override
	public C remove(int aIndex) {
		C theC = super.remove(aIndex);
		itsSet.remove(theC);
		return theC;
	}
	
	@Override
	public boolean remove(Object aO) {
		if (itsSet.remove(aO))
		  return super.remove(aO);
		return false;
	}
	
}
