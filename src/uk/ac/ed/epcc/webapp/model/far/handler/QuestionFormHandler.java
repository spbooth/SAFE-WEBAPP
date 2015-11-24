// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far.handler;

import uk.ac.ed.epcc.webapp.Targetted;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager;

/**
 * @author spb
 * @param <T> type of input
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public interface QuestionFormHandler<T> extends  Targetted<T> {
	/** build a configuration form 
	 * 
	 * @param f
	 */
	public void buildConfigForm(Form f);
	/** read the configuration from the form.
	 * the form is assumed to have validated correctly.
	 * 
	 * @param f
	 * @return true if configured ok
	 */
	public Input<T> parseConfiguration(Form f);
	
	public Class<? extends ResponseDataManager> getDataClass();
	
	/** are there any config parameters needed/valid.
	 * 
	 * @return
	 */
	public boolean hasConfig();
}
