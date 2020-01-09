package uk.ac.ed.epcc.webapp.email.inputs;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
/** A {@link RestrictedEmailFieldValidator} that uses a standard config property 
 * <b>service.email.forbidden</b> for forbidden email patterns
 * 
 * 
 * @author spb
 *
 */
public class ServiceAllowedEmailFieldValidator extends RestrictedEmailFieldValidator implements Contexed {

	public static final String SERVICE_EMAIL_FORBIDDEN_CONFIG = "service.email.forbidden";
	private final AppContext conn;
	public ServiceAllowedEmailFieldValidator(AppContext conn) {
		super(conn.getInitParameter(SERVICE_EMAIL_FORBIDDEN_CONFIG));
		this.conn=conn;
	}
	@Override
	public AppContext getContext() {
		return conn;
	}
	

}
