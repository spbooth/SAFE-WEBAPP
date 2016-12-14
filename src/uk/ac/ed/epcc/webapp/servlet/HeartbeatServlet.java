//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;








import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.cron.HeartbeatListener;
import uk.ac.ed.epcc.webapp.timer.TimerService;

/** A servlet to trigger timed events from a remote cron job
 * @author spb
 *
 */

@WebServlet(name="HeartbeatServlet",urlPatterns="/HearbeatServlet/*,/HeartbeatServlet/*")
public class HeartbeatServlet extends ContainerAuthServlet {

	private static Date last_call;
	/**
	 * 
	 */
	public HeartbeatServlet() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.ContainerAuthServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, uk.ac.ed.epcc.webapp.AppContext, java.lang.String)
	 */
	@Override
	protected synchronized void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn, String user) throws ServletException, IOException {
		TimerService serv = conn.getService(TimerService.class);
		long max_wait=conn.getLongParameter("max.heartbeat.millis", 60000L);
		boolean ok=true;
		res.setContentType("text/plain");
		ServletOutputStream out = res.getOutputStream();
		Logger log = getLogger(conn);
		if( serv != null ) serv.startTimer("Heartbeatlistener");
		try{
			last_call=new Date();

			String listeners = conn.getExpandedProperty("heartbeat.listeners");

			if( listeners == null || listeners.trim().length()==0){
				out.println("No listeners");
				return;
			}


			for(String l : listeners.split("\\s*,\\s*")){
				if( serv != null ) serv.startTimer("HeartbeatListener."+l);
				try{
					HeartbeatListener listener = conn.makeObject(HeartbeatListener.class, l);
					if( listener != null ){
						out.println("Running"+l);
						log.debug("Running "+l);
						Date next = listener.run();
						out.println("Next run expected "+next);
						log.debug("Next run expected "+next);
					}else{
						log.error("No HearBeatListener constructed for tag "+l);
					}
				}catch(Throwable t){
					ok=false;
					log.error("Error in hearbeatlistener "+l,t);
				}finally{
					if( serv != null ) serv.stopTimer("HeartbeatListener."+l);
				}
			}

		}finally{
			if( serv != null ) serv.stopTimer("Heartbeatlistener");
		}
		if( ok ){
			out.println("OK");
		}else{
			out.println("FAIL");
		}
		long elapsed = (System.currentTimeMillis() - last_call.getTime());
		if( elapsed > max_wait ){
			log.warn("Long heartbeat run "+(elapsed/1000L)+" seconds");
		}
		return;
	}
	public synchronized static Date getLastCall(){
		return last_call;
	}

}