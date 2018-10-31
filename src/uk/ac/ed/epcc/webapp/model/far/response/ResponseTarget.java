//| Copyright - The University of Edinburgh 2015                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
package uk.ac.ed.epcc.webapp.model.far.response;

import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.far.PartManager;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.PartManager.Part;
import uk.ac.ed.epcc.webapp.model.far.PartOwner;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response;

/** target for transitions that fill out form responses.
 * @author spb
 * @param <D> type of {@link DynamicForm}
 * @param <R> type of {@link Response}
 *
 */

public class ResponseTarget<D extends DynamicForm, R extends Response<D>> {

	/**
	 * @param response
	 * @param part
	 */
	public ResponseTarget(R response, Part part) {
		super();
		this.response = response;
		this.part = part;
	}
	private final R response;
	private final PartManager.Part part;
	public R getResponse() {
		return response;
	}
	public PartManager.Part getPart() {
		return part;
	}
	
	public ResponseTarget<D,R> getParent(){
		PartOwner p =  part.getOwner();
		if( p == null || ! (p instanceof Part)){
			return null;
		}
		return new ResponseTarget<>(response, (Part)p);
	}
	
	public ResponseTarget<D,R> getSibling(boolean up) throws DataFault{
		Part p = part.getFactory().getSibling(part, up);
		if( p == null ){
			return null;
		}
		return new ResponseTarget<>(response, p);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((part == null) ? 0 : part.hashCode());
		result = prime * result
				+ ((response == null) ? 0 : response.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponseTarget other = (ResponseTarget) obj;
		if (part == null) {
			if (other.part != null)
				return false;
		} else if (!part.equals(other.part))
			return false;
		if (response == null) {
			if (other.response != null)
				return false;
		} else if (!response.equals(other.response))
			return false;
		return true;
	}
	public String toString(){
		return "ResponseTarget["+response+","+part+"]";
	}

}