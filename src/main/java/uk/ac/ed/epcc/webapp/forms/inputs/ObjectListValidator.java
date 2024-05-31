package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;
import uk.ac.ed.epcc.webapp.validation.SingleLineFieldValidator;

/** A {@link FieldValidator} for a list of tags that must 
 * resolve to objects assignable to a particular type.
 * @author Stephen Booth
 *
 */
public final class ObjectListValidator implements SingleLineFieldValidator {
	/**
	 * 
	 */
	private final AppContext conn;
	/**
	 * 
	 */
	private final Class<?> target;

	/**
	 * @param conn
	 * @param target
	 */
	public ObjectListValidator(AppContext conn, Class<?> target) {
		this.conn = conn;
		this.target = target;
	}

	@Override
	public void validate(String list) throws FieldException {
		if( list != null && list.trim().length() > 0){
			for(String n : list.split("\\s*,\\s*")){
				if( conn.makeObjectWithDefault(target,null, n)==null){
					throw new ValidateException("tag "+n+" not a "+target.getCanonicalName());
				}
			}
		}
		
	}
}