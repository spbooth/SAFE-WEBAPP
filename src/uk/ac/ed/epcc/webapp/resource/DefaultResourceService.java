// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Version;
/** DefaultResoruceService
 * USe ClassLoader resource location with fall-back to file-system
 * 
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: DefaultResourceService.java,v 1.2 2014/09/15 14:30:34 spb Exp $")

public class DefaultResourceService implements ResourceService, Contexed {

	private final AppContext conn;
	
	public DefaultResourceService(AppContext c){
		conn=c;
	}

	/** Though not clear from the javadoc classloaders seem not to want
	 * a leading slash in the path name.
	 * 
	 * Double slashes also seem to give problems in rare edge cases
	 * so remove them just to be on the safe side.
	 * @param name
	 * @return
	 */
	static final String mapForClassloader(String name){
		while( name.startsWith("/")){
			name =  name.substring(1);
		}
		while( name.contains("//")){
			name = name.replaceAll("//", "/");
		}
		return name;
	}
	
	public URL getResource(String name) {
		if( name == null){
			return null;
		}
		//Logger log = conn.getService(LoggerService.class).getLogger(getClass());
		URL result = getClass().getClassLoader().getResource(mapForClassloader(name));
		//System.out.println("lookup of "+name+" returns "+result);
		if( result != null ){
			return result;
		}
		File f = new File(name);
		if( f.exists() && f.canRead()){
			//System.out.println("file exists");
			try {
				return f.toURI().toURL();
			} catch (MalformedURLException e) {
				
			}
		}
		return null;
	}

	
	public InputStream getResourceAsStream(String name) throws FileNotFoundException {
		//Logger log = conn.getService(LoggerService.class).getLogger(getClass());
		
		String mapped = mapForClassloader(name);
		//log.debug("In default getResoruceAsStream("+name+"->"+mapped+")");
		InputStream stream=  getClass().getClassLoader().getResourceAsStream(mapped);
		if( stream != null ){
			//log.debug("get of "+name+" returns "+stream);
			return stream;
		}
		File f = new File(name);
		if( f.exists() && f.canRead()){
			//log.debug("file exists");
			
				return new FileInputStream(f);
			
		}else{
			//log.debug("file not found or unreadable");
		}
		return null;
	}

	
	public void cleanup() {

	}


	public AppContext getContext() {
		return conn;
	}

	public Class<ResourceService> getType() {
		return ResourceService.class;
	}

}