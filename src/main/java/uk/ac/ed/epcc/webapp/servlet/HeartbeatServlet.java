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
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.cron.HeartbeatListener;
import uk.ac.ed.epcc.webapp.model.cron.LockFactory;
import uk.ac.ed.epcc.webapp.model.cron.LockFactory.Lock;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.timer.TimeClosable;
import uk.ac.ed.epcc.webapp.timer.TimerService;

/** A servlet to trigger timed events from a remote cron job
 * 
 * Alternatively if the parameter <b>HeartbeatServlet.run_thread</b> is set to true
 * the heartbeat will be run from a thread at a frequency of <b>HeartbeatServlet.repeat</b> minutes.
 * 
 * The default is not to run a thread but trigger the heartbeatservet from an external cron.
 * this is to avoid multiple heartbeats in a fail-over or parallel deployment configuration 
 * 
 * 
 * @author spb
 *
 */

@WebServlet(name="HeartbeatServlet",urlPatterns="/HearbeatServlet/*,/HeartbeatServlet/*")
public class HeartbeatServlet extends ContainerAuthServlet {

	private static final String HEARTBEAT_SERVLET_LOCK_NAME = "HeartbeatServlet";
	private static Date last_call;
	private static boolean running=false;
	/**
	 * 
	 */
	public HeartbeatServlet() {
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.ContainerAuthServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, uk.ac.ed.epcc.webapp.AppContext, java.lang.String)
	 */
	@Override
	protected synchronized void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn, String user) throws ServletException, IOException {
		
		
		boolean ok=true;
		res.setContentType("text/plain");
		try(ServletOutputStream out = res.getOutputStream()){
			if( ! running) {
				ok = runHeartbeat(conn, out);
			}else {
				getLogger(conn).error("Overlapping calls to HeartbeatServlet "+last_call);
				ok=false;
			}
			if( ok ){
				out.println("OK");
			}else{
				out.println("FAIL");
			}
		}
		return;
	}
	public synchronized static Date getLastCall(AppContext conn){
		try {
			LockFactory lock_fac = LockFactory.getFactory(conn);
			Lock lock = lock_fac.findFromString(HEARTBEAT_SERVLET_LOCK_NAME);
			if( lock != null) {
				return lock.lastLocked();
			}
		}catch(Exception e) {
			conn.getService(LoggerService.class).getLogger(HeartbeatServlet.class).error("Error getting last locked",e);
		}
		// fall back to static value on this servlet
		return last_call;
	}
	
	public synchronized boolean runHeartbeat(AppContext conn, ServletOutputStream out) throws IOException{
		boolean ok=true;
		
		long max_wait=conn.getLongParameter("max.heartbeat.millis", 60000L);
		Logger log = getLogger(conn);
		TimerService serv = conn.getService(TimerService.class);
		
		Lock lock = null;
		try(TimeClosable timer = new TimeClosable(conn, "HeartbeatListener")){
			running=true;
			CurrentTimeService time = conn.getService(CurrentTimeService.class);
			last_call=time.getCurrentTime();

			String listeners = conn.getExpandedProperty("heartbeat.listeners");

			if( listeners == null || listeners.trim().length()==0){
				if( out != null ){
					out.println("No listeners");
				}
				return ok;
			}
			LockFactory lock_f = LockFactory.getFactory(conn);
			lock = lock_f.makeFromString(HEARTBEAT_SERVLET_LOCK_NAME);
			Calendar cal = Calendar.getInstance();
			Date now = time.getCurrentTime();

			if( lock.isLocked()) {
				Date d = lock.wasLockedAt();
				cal.setTime(d);
				cal.add(Calendar.HOUR,1);
				if(now.after(cal.getTime())) {
					getLogger(conn).error(lock.getName()+" locked since "+d);
				}
				return false;
			}
			if( lock.takeLock()) {
				for(String l  : listeners.split("\\s*,\\s*")){
					if( serv != null ) serv.startTimer("HeartbeatListener."+l);
					long begin=System.currentTimeMillis();
					try{
						HeartbeatListener listener = conn.makeObject(HeartbeatListener.class, l);
						if( listener != null ){
							if( out != null ){ 
								out.println("Running"+l);
							}
							log.debug("Running "+l);
							Date next = listener.run();
							if( out != null ){ 
								out.println("Next run expected "+next);
							}
							log.debug("Next run expected "+next);
						}else{
							log.error("No HearBeatListener constructed for tag "+l);
						}
					}catch(Exception t){
						ok=false;
						log.error("Error in hearbeatlistener "+l,t);
					}finally{
						if( serv != null ) serv.stopTimer("HeartbeatListener."+l);
					}
					long elapsed = (System.currentTimeMillis() - begin);
					if( elapsed > max_wait ){
						log.error("Long heartbeat call "+l+" "+(elapsed/1000L)+" seconds");
					}
				}
			}else {
				getLogger(conn).error("Failed to take heartbeat lock");
			}
		} catch (Exception e) {
			getLogger(conn).error("Error in HeartbeatListener",e);
		}finally{
			if( lock != null ) {
				try {
					lock.releaseLock();
				} catch (DataFault e) {
					getLogger(conn).error("Error removing lock");
				}
			}
			running=false;
		}
		
		
		return ok;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		AppContext conn=null;
		try{

			conn= ErrorFilter.makeContext(config.getServletContext());
			boolean run_thread = conn.getBooleanParameter("HeartbeatServlet.run_thread",false);
			long repeat = conn.getLongParameter("HeartbeatServlet.repeat", 5);


			if( run_thread ){
				runner = new Runner(config);
				ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
				service.scheduleWithFixedDelay(runner, repeat, repeat, TimeUnit.MINUTES);
			}
		}catch(Exception t){
			config.getServletContext().log("Error checking for heartbeat-thread", t);
		}finally {
			if( conn != null ) {
				AppContext.clearContext();
				conn.close();
			}
		}
	}
	public Runner runner=null;
	public ScheduledExecutorService service=null;
	public class Runner implements Runnable{
		public Runner(ServletConfig config) {
			super();
			this.config = config;
			
		}

		private final ServletConfig config;
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			AppContext conn=null;
			try {
				conn = ErrorFilter.makeContext(config.getServletContext());
				runHeartbeat(conn, null);
			} catch (Exception e) {
				config.getServletContext().log("Error in Runner", e);
			}finally {
				if( conn != null ) {
					AppContext.clearContext();
					conn.close();	
				}
			}

		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy() {
		if( service != null ){
			try {
				service.shutdownNow();
				service.awaitTermination(15, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				
			}
		}
		super.destroy();
	}

	public synchronized static void reset() {
		last_call=null;
	}
}