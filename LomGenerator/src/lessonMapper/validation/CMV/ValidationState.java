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
package lessonMapper.validation.CMV;

import java.io.Serializable;
import java.util.List;

import diffuse.models.res.ResCMV;

import lessonMapper.lom.LOMAttribute;

/**
 * 
 */
public class ValidationState implements Serializable {

	/**
	 * 
	 */
	public enum State {
		Valid,
		NotDefined,
		NotValid
	}

	State itsState;

	List<ResCMV> itsUncompliedResCMVs;

	/**
	 * 
	 */
	LOMAttribute itsAttribute;

	/**
	 * initialize a validation of type NotValid for aAttribute related with
	 * aRestrictionValue for which the nonValidity occured.
	 * 
	 * @param values
	 * @param aAttribute
	 */
	public ValidationState(LOMAttribute aAttribute,
			List<ResCMV> aUncompliedResCMVs) {
		itsState = State.NotValid;
		itsUncompliedResCMVs = aUncompliedResCMVs;
		itsAttribute = aAttribute;
	}

	/**
	 * 
	 * 
	 * @param aState
	 * @param values
	 * @param aAttribute
	 */
	public ValidationState(State aState, LOMAttribute aAttribute,
			List<ResCMV> aUncompliedResCMVs) {
		itsState = aState;
		itsUncompliedResCMVs = aUncompliedResCMVs;
		itsAttribute = aAttribute;
	}

	/**
	 * initialize a validation of type NotDefined for aAttribute.
	 * 
	 * @param aAttribute
	 */
	public ValidationState(LOMAttribute aAttribute) {
		itsState = State.NotDefined;
		itsAttribute = aAttribute;
	}

	/**
	 * initialize a validation of type aState for aAttribute.
	 * 
	 * @param aState
	 * @param aAttribute
	 */
	public ValidationState(LOMAttribute aAttribute, State aState) {
		itsState = aState;
		itsAttribute = aAttribute;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public State getState() {
		return itsState;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public LOMAttribute getAttribute() {
		return itsAttribute;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public List<ResCMV> getUncompliedResCMVs() {
		return itsUncompliedResCMVs;
	}

	


}
