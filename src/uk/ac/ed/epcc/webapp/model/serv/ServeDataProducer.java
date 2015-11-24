// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.serv;

import java.util.List;

import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Tagged;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Class that can produce data to be provided to the user.
 * 
 * It is up to the implementing class how much of the path is significant when locating the data.
 * 
 * By convention the tag ServeData should retrieve a default ServeDataProducer for the application.
 * @author spb
 *
 */
public interface ServeDataProducer extends Contexed,Tagged{
	public static final String DEFAULT_SERVE_DATA_TAG="ServeData";
	
	
	/** Get a MimeStreamData corresponding to the data to be served
	 * 
	 * 
	 * 
	 * This method also implements access control and returns null if access is denied.
	 * Note that the {@link SessionService} may not have a current person set so you have to check for
	 * null {@link SessionService} and null {@link AppUser} as well as the users permissions.
	 * @param user
	 * @param path
	 * @return MimeStreamData or null
	 * @throws Exception 
	 */
	public MimeStreamData getData(SessionService user, List<String> path) throws Exception;
	
}