//| Copyright - The University of Edinburgh 2011                            |
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