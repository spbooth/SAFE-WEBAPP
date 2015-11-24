// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.exec;

import java.io.IOException;

import uk.ac.ed.epcc.webapp.AppContextService;

/** A service to run external commands.
 * 
 * This is encapsulated as a service to allow substitution in tests and to re-use common code.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public interface ExecService extends AppContextService<ExecService> {

	/** Run a external command (waiting for it to complete) 
	 * 
	 * @param timeout_milliseconds
	 * @param command
	 * @return {@link ProcessProxy}
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public ProcessProxy exec(long timeout_milliseconds,String command) throws Exception;
	

	/** un a external command with supplied input (waiting for it to complete) 
	 * 
	 * @param input
	 * @param timeout_milliseconds
	 * @param command
	 * @return {@link ProcessProxy}
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws Exception 
	 */
	public ProcessProxy exec(String input, long timeout_milliseconds,String command) throws Exception;
}
