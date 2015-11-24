// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.model.ParseFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObject;

@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
/** An input that checks for existing records of the same name.
 * 
 * @author spb
 *
 */
public class UnusedNameInput<F extends DataObject> extends TextInput{
	private final ParseFactory<F> fac;
	private F existing=null;
	public UnusedNameInput(ParseFactory<F> fac,F existing){
		this(fac);
		this.existing=existing;
		if( existing != null ){
			setValue(fac.getCanonicalName(existing));
		}
	}
    public UnusedNameInput(ParseFactory<F> fac){
    	super();
    	this.fac=fac;
    	setSingle(true);
    	setOptional(false);
    }
	@Override
	public boolean isOptional() {
		//Make sure that even if DB field is optional this input never is.
		return false;
	}
	@Override
	public void validate() throws FieldException {
		super.validate();
		
		String name = getValue();
		F dup = fac.findFromString(name);
		if( dup != null ){
			if( existing == null || ! existing.equals(dup)){
				throw new ValidateException("Name "+name+" already in use");
			}
		}
	}
	
}