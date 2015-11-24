// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.data.forms;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObject;

/** Objects that customize a creation form.
 * These can be a {@link CreateTemplate} or a {@link Composite} attached to the factory.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public interface CreateCustomizer <BDO extends DataObject>{
	/**
	 * Perform target specific customisation of a creation Form. For example
	 * adding a special validator. Note that this is called in addition to the
	 * basic customiseForm call
	 * 
	 * @param f
	 *            Form to be modified
	 * @throws Exception 
	 */
	public void customiseCreationForm(Form f) throws Exception;
	
	/** Populate target from form.
	 * This can be sub-classes to add additional information such as the requesting Person
	 * or other information derived other than from the form parameters.
	 * Normally the form contents have already been set in the Object but the form is passed to this method in case any
	 * form parameters are used to control the way the object is created.
	 * 
	 * @param dat Object being created
	 * @param f {@link Form} providing parameters
	 * @throws DataException 
	 */
	public abstract void preCommit(BDO dat, Form f) throws DataException;

	/** Take any action after object commit.
	 * 
	 * @param dat 
	 * @param f
	 * @throws Exception
	 */
	public abstract void postCreate(BDO dat, Form f) throws  Exception;

}
