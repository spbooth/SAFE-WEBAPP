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
			if( ctx != null) {
				URL result = ctx.getResource(mapName(name));
				if( result != null){
					//log.debug("Servlet GetResource("+name+") returns "+result);
					return result;
				}
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
		try {
			if( ctx != null ) {
				//Logger log = getContext().getService(LoggerService.class).getLogger(getClass());
				InputStream result = ctx.getResourceAsStream(mapName(name));
				//log.debug("servlet getResourceAsStream("+name+") returns "+result);
				if( result != null ){
					return result;
				}
			}
		}catch(Exception e){
			getContext().error(e,"Error in ServletContext.getResource");
		}
		//log.debug("revert to super.getResourceAsStream");
		return super.getResourceAsStream(name);
	}

	@Override
	public void cleanup() {

	}

}