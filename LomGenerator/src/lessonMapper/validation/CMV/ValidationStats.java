/*
 * LessonMapper 2.
 */
package lessonMapper.validation.CMV;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * keep the statistiques: nb of valid element,
 * nb of not valid element
 * nb of not defined element.
 * 
 * @author omotelet
 */
public class ValidationStats implements Serializable{
	
	/**
	 * 
	 */
	protected List<ValidationState> itsValids = new ArrayList<ValidationState>(),
	itsNotDefineds = new ArrayList<ValidationState>(),
	itsNotValids = new ArrayList<ValidationState>();
	
	
	
	/**
	 * 
	 * 
	 * @param theState 
	 */
	public void addValidationState(ValidationState theState) {
		switch(theState.getState()) {
		case Valid: itsValids.add(theState); break;
		case NotDefined: itsNotDefineds.add(theState); break;
		case NotValid: itsNotValids.add(theState); break;
		}
	}
	
	
	
	/**
	 * 
	 * 
	 * @return 
	 */
	public int getNotDefinedNb() {
		return itsNotDefineds.size();
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public int getNotValidNb() {
		return itsNotValids.size();
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public int getValidNb() {
		return itsValids.size();
	}


	/**
	 * 
	 * 
	 * @return 
	 */
	public List<ValidationState> getNotDefineds() {
		return itsNotDefineds;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public List<ValidationState> getNotValids() {
		return itsNotValids;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public List<ValidationState> getValids() {
		return itsValids;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return  getNotDefinedNb()+ " nd/ " +getValidNb() +" v/ "+getNotValidNb() + " nv" ;
	}
	
		
	
}