// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.resource;

import java.io.InputStream;
import java.net.URL;

import uk.ac.ed.epcc.webapp.AppContextService;

/** Service for locating resources.
 * By default these will map onto the equivalent Classloader 
 * methods but the use of a service allows other mechanisms
 * without the need to install a custom Classloader 
 * 
 * @author spb
 *
 */
public interface ResourceService extends AppContextService<ResourceService>{
	/** get an identifying URL for the resource
	 * A null result implies the resource cannot be located
	 * @param name
	 * @return URL
	 */
	public URL getResource(String name);
	/** get an InputStream to read the resource.
	 * 
	 * @param name
	 * @return InputStream
	 * @throws Exception s
	 */
	public InputStream getResourceAsStream(String name) throws Exception;
}