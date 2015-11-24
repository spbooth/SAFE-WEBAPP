// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.servlet.resource;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletContext;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.resource.DefaultResourceService;
/** ResourceServlet that can also get resources from the {@link ServletContext}
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ServletResourceService.java,v 1.2 2014/09/15 14:30:35 spb Exp $")

public class ServletResourceService extends DefaultResourceService {
	

	private final ServletContext ctx;
	public ServletResourceService(AppContext conn,ServletContext c){
		super(conn);
		this.ctx=c;
	}
	@Override
	public URL getResource(String name) {
		//Logger log = getContext().getService(LoggerService.class).getLogger(getClass());
		try{
		URL result = ctx.getResource(mapName(name));
		if( result != null){
			//log.debug("Servlet GetResource("+name+") returns "+result);
			return result;
		}
		}catch(Exception e){
			getContext().error(e,"Error in ServletContext.getResource");
		}
		//log.debug("revert to super.getResource");
		return super.getResource(name);
	}
	private String mapName(String name){
		// ServletContext names need to be absolute
		if( ! name.startsWith("/")){
			return "/"+name;
		}
		return name;
	}

	@Override
	public InputStream getResourceAsStream(String name) throws FileNotFoundException {
		//Logger log = getContext().getService(LoggerService.class).getLogger(getClass());
		InputStream result = ctx.getResourceAsStream(mapName(name));
		//log.debug("servlet getResourceAsStream("+name+") returns "+result);
		if( result != null ){
			return result;
		}
		//log.debug("revert to super.getResourceAsStream");
		return super.getResourceAsStream(name);
	}

	@Override
	public void cleanup() {

	}

}