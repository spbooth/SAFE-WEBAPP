// Copyright - The University of Edinburgh 2014
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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.9 $")
@WebServlet(name="HeartbeatServlet",urlPatterns="/HearbeatServlet/*")
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
		if( serv != null ) serv.startTimer("Heartbeatlistener");
		last_call=new Date();
		res.setContentType("text/plain");
		String listeners = conn.getInitParameter("heartbeat.listeners");
		ServletOutputStream out = res.getOutputStream();
		if( listeners == null || listeners.trim().length()==0){
			out.println("No listeners");
			return;
		}
		Logger log = getLogger(conn);
		for(String l : listeners.split("\\s*,\\s*")){
			if( serv != null ) serv.startTimer("HeartbeatListener."+l);
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
			if( serv != null ) serv.stopTimer("HeartbeatListener."+l);
		}
		out.println("OK");
		if( serv != null ) serv.stopTimer("Heartbeatlistener");
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
