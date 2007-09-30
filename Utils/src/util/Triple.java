package util;

public class Triple<A,C,B> extends Couple<A,B> {

	C itsMiddleElement;
	
	public Triple(A aLeftElement, C aMiddleElement, B aRightElement) {
		super(aLeftElement, aRightElement);
		itsMiddleElement = aMiddleElement;
	}

	public C getMiddleElement() {
		return itsMiddleElement;
	}

	public void setMiddleElement(C aMiddleElement) {
		itsMiddleElement = aMiddleElement;
	}
	
	@Override
	public int hashCode() {
		return itsMiddleElement.hashCode()+super.hashCode();
	}
	
	@Override
	public boolean equals(Object aObj) {
		if (aObj instanceof Triple) {
			Triple theOtherTriple = (Triple) aObj;
			return theOtherTriple.getMiddleElement().equals(itsMiddleElement)
			&& super.equals(aObj);
		}
		return false;
	}
	
}
