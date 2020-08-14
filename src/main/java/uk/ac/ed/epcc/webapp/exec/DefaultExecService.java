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

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;

/**
 * @author spb
 *
 */

public class DefaultExecService extends AbstractContexed implements ExecService {

	/**
	 * @param conn 
	 * 
	 */
	public DefaultExecService(AppContext conn) {
		super(conn);
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
		


		if( timeout < 0L){
			timeout=0L;
		}
		DeferredProcessProxy worker = exec_deferred(input == null ? null :input.getBytes(), line);
		worker.execute(timeout);

		return worker;

	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.exec.ExecService#exec(long, java.lang.String)
	 */
	@Override
	public ProcessProxy exec(long timeout_milliseconds, String command)
			throws Exception {
		return exec(null,timeout_milliseconds,command);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.exec.ExecService#exec_deferred(java.lang.String, java.lang.String)
	 */
	@Override
	public DeferredProcessProxy exec_deferred(byte input[], String command) throws IOException {
		if( command.contains(":")) {
			String frags[] = command.split("\\s*:\\s*");
			
			StringBuilder sb = new StringBuilder();
			sb.append(getContext().getInitParameter("ssh.command","ssh"));
			for( int i=0 ; i< frags.length ; i++) {
				if( i < (frags.length - 2)) {
					sb.append(" -J ");
				}else {
					sb.append(" ");
				}
				sb.append(frags[i]);
			}
			
			command= sb.toString();
		}
		Runtime rt = Runtime.getRuntime();
		String result=null;
		Process p =rt.exec(command);
		return new ProcessHandler(input,p);
		
	}

	
}