//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.exec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;

/**
 * @author spb
 *
 */

public class DefaultExecService implements ExecService,Contexed {

	private final AppContext conn;
	/**
	 * @param conn 
	 * 
	 */
	public DefaultExecService(AppContext conn) {
		this.conn=conn;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#cleanup()
	 */
	public void cleanup() {

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	public Class<? super ExecService> getType() {
		return ExecService.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.exec.ExecService#exec(java.lang.String)
	 */
	public ProcessProxy exec(String input ,long timeout,String line) throws Exception {
		Runtime rt = Runtime.getRuntime();
		String result=null;
		Process p =rt.exec(line);


		if( timeout < 0L){
			timeout=0L;
		}

		ProcessHandler worker = new ProcessHandler(input == null ? null :input.getBytes(),p);
		worker.execute(timeout);

		return worker;

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	public AppContext getContext() {
		return conn;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.exec.ExecService#exec(long, java.lang.String)
	 */
	@Override
	public ProcessProxy exec(long timeout_milliseconds, String command)
			throws Exception {
		return exec(null,timeout_milliseconds,command);
	}

}