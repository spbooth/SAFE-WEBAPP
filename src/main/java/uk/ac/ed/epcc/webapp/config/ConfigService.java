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
package uk.ac.ed.epcc.webapp.config;

import java.util.Properties;

import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.Contexed;

/** Interface for configuration service 
 * 
 * A configurations service is usually a pre-requisite for any other service 
 * so these are usually explicitly added to an AppContext when the AppContext is created
 * @author spb
 *
 */
public interface ConfigService extends Contexed, AppContextService<ConfigService>{
	/** return the set of properties defined by the service 
	 * 
	 * @return Properties
	 */
  public Properties getServiceProperties();
  /** clear any cached properties. This will cause the 
   * properties to be re-read the next time getServiceProperties is called.
   * Implementations are permitted not to cache data in which case this is a No-op.
   */
  public void clearServiceProperties();
  
  /** Set a property 
   * This method is for making persistent changes to the configuration. if the service is not capable of 
   * making persistent changes it should forward the request to a nested service or throw an
   * {@link UnsupportedOperationException}
   * 
   * @param name
   * @param value
   * @throws UnsupportedOperationException
   */
  public void setProperty(String name, String value) throws UnsupportedOperationException;
  /** register a ConfigServiceListener
   * 
   * @param listener
   */
  public void addListener(ConfigServiceListener listener);
  
  
}