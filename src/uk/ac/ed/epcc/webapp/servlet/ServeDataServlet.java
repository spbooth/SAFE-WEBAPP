// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.servlet;

import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.Logger;
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
@uk.ac.ed.epcc.webapp.Version("$Id: ServeDataServlet.java,v 1.24 2015/07/21 21:23:37 spb Exp $")
@WebServlet(name="DataServlet",urlPatterns=ServeDataServlet.DATA_PATH+"*")
public class ServeDataServlet extends WebappServlet {

	/**
	 * 
	 */
	static final String DATA_PATH = "/Data/";
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
			
		}catch(DataException e){
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;

		}
		}catch(Throwable t){
			conn.error(t,"Error caught in ServeDataServlet");
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
		MimeStreamData msd = producer.getData(conn.getService(SessionService.class), args);
		if( msd != null ){
			String name=msd.getName();
			if(name != null ){
				sb.append("/");
				sb.append(name);
			}
		}	
		
		return sb.toString();
	}
	public static  ExtendedXMLBuilder addLink(AppContext conn,ExtendedXMLBuilder hb,ServeDataProducer producer, List<String> args,String text){
		hb.open("a");
		try {
			hb.attr("href", conn.getService(ServletService.class).encodeURL(getURL(conn, producer, args)));
		} catch (Exception e) {
			conn.error(e,"Error making URL");
		}
		hb.clean(text);
		hb.close();
		return hb;
	}
}