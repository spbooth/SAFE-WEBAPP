// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.session;

/** Default Web-name {@link AppUserNameFinder}.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.3 $")
public class WebNameFinder<AU extends AppUser> extends FieldNameFinder<AU,WebNameFinder> {

	public static final String WEB_NAME = "WebName";

	/**
	 * @param factory
	 * @param realm
	 */
	public WebNameFinder(AppUserFactory factory) {
		super(factory, WEB_NAME);
	}

	

}
