// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far;

import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;



/** abstract superclass for objects that can own part of a dynamic form.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.4 $")
public abstract class PartOwner extends DataObject {

	/**
	 * @param r
	 */
	protected PartOwner(Record r) {
		super(r);
	}

	/** get the owning {@link DynamicForm}
	 * 
	 * @return the {@link DynamicForm}
	 */
	public abstract DynamicForm getForm();
	/** get a {@link FormResult} to view this object.
	 * 
	 * @return
	 */
	public abstract FormResult getViewResult();
}
