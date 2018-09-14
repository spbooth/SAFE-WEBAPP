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
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer;
import uk.ac.ed.epcc.webapp.session.SessionDataProducer;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Servlet to serve ServeData objects
 * 
 * Note this has to work for externally authenticated sessions as well as 
 * logged in sessions
 * @author spb
 *
 */

@WebServlet(name="DataServlet",urlPatterns=ServeDataServlet.DATA_PATH+"*")
public class ServeDataServlet extends WebappServlet {

	/**
	 * 
	 */
	public static final String DATA_PATH = "/Data/";
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res,
		AppContext conn)  {
		try{
		Logger log = getLogger(conn);
		
		log.debug("In ServeDataServlet");
		List<String> args =conn.getService(ServletService.class).getArgs();
		if( args.size() < 2 ){
			log.debug("Bad number of args "+args.size());
			res.sendError(HttpServletResponse.SC_BAD_REQUEST,"Insufficient arguments");
			
			return;
		}
		String tag = args.get(0);
		ServeDataProducer producer=conn.makeObjectWithDefault(ServeDataProducer.class,SessionDataProducer.class, tag);
		if( producer == null ){
			log.debug("No producer");
			res.sendError(HttpServletResponse.SC_BAD_REQUEST,"No Producer "+tag);
			return;
		}
		args.remove(0);
		try{
				// Note in principle we might allow anonymous access or
			    // access to unauthenticated sessions.
				SessionService<?> person = conn.getService(SessionService.class);
				MimeStreamData msd = producer.getData(person, args);
				if( msd == null){
					res.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
				res.setContentLength((int) msd.getLength());
				res.setContentType(msd.getContentType());
				msd.write(res.getOutputStream());
				return;
			
		}catch(IOException e){
			if( ! res.isCommitted()){
				res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
			return;
		}catch(DataNotFoundException dne){
			// No logging just an expired/wrong url
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}catch(DataException e){
			getLogger(conn).error("Data error in ServeDataServlet",e);
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;

		}
		}catch(Exception t){
			getLogger(conn).error("Error caught in ServeDataServlet",t);
			if( ! res.isCommitted()){
				try {
					res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				} catch (IOException e) {
				}
			}
			

			
			return;		
		}
		
	}
	
	public static  String getURL(AppContext conn,ServeDataProducer producer,List<String> args) throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(DATA_PATH);
		sb.append(producer.getTag());
		if( args != null ){
			for(String s : args){
				sb.append("/");
				sb.append(s);
			}
		}
		// If this is a dynamically generated object then try to use the more lightweight method
		String name = producer.getDownloadName(conn.getService(SessionService.class), args);

		if(name != null && name.length() > 0){
			sb.append("/");
			sb.append(name);
		}
			
		
		return sb.toString();
	}
	public static  ExtendedXMLBuilder addLink(AppContext conn,ExtendedXMLBuilder hb,ServeDataProducer producer, List<String> args,String text){
		hb.open("a");
		try {
			hb.attr("href", conn.getService(ServletService.class).encodeURL(getURL(conn, producer, args)));
			hb.attr("target","_blank"); // always in new tab for download
		} catch (Exception e) {
			conn.error(e,"Error making URL");
		}
		hb.clean(text);
		hb.close();
		return hb;
	}
}