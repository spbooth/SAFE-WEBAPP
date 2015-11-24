// Copyright - The University of Edinburgh 2015
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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.3 $")
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
		return new ResponseTarget<D, R>(response, (Part)p);
	}
	
	public ResponseTarget<D,R> getSibling(boolean up) throws DataFault{
		Part p = part.getFactory().getSibling(part, up);
		if( p == null ){
			return null;
		}
		return new ResponseTarget<D,R>(response, p);
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

}
