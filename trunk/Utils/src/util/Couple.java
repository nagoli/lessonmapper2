package util;

import java.io.Serializable;



public class Couple<A,B> implements Serializable, Comparable<Couple<A,B>>{

	A itsLeftElement;
	B itsRightElement;
	
	public Couple(A aLeftElement,B aRightElement) {
		itsLeftElement = aLeftElement;
		itsRightElement=aRightElement;
	}


	public Couple(Couple<A,B> aCouple) {
		itsLeftElement = aCouple.itsLeftElement;
		itsRightElement=aCouple.itsRightElement;
	}

	
	public A getLeftElement() {
		return itsLeftElement;
	}

	public void setLeftElement(A aLeftElement) {
		itsLeftElement = aLeftElement;
	}

	public B getRightElement() {
		return itsRightElement;
	}

	public void setRightElement(B aRightElement) {
		itsRightElement = aRightElement;
	}
	
	@Override
	public int hashCode() {
		return ((itsLeftElement!=null)? itsLeftElement.hashCode(): 0) + ((itsRightElement!=null)? itsRightElement.hashCode():0);
	}
	
	@Override
	public String toString() {	
		return "("+getLeftElement()+","+getRightElement()+")";
	}
	
	@SuppressWarnings("unchecked")
	public int compareTo(Couple<A,B> aCouple) {
		if (itsLeftElement instanceof Comparable)
		return ((Comparable)itsLeftElement).compareTo(aCouple.itsLeftElement);
		return -1;
	}
	@Override
	public boolean equals(Object aObj) {
		if (aObj instanceof Couple) {
			Couple theOtherCouple = (Couple) aObj;
			return theOtherCouple.getLeftElement().equals(itsLeftElement)
			&& theOtherCouple.getRightElement().equals(itsRightElement);
		}
		return false;
	}
}
