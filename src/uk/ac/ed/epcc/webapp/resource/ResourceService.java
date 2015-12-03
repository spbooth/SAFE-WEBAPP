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