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
package uk.ac.ed.epcc.webapp;

import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/** Interface for objects that can provide an AppContext.
 * 
 * When creating objects that implement this interface
 * an {@link AppContext} will look for 
 * constructor of the form
 * <code>Constructor(AppContext conn)</code>
 * and/or 
 * <code>Constructor(AppContext conn, String tag)</code>
 * 
 * so these constructor signatures should be provided if possible.
 * @author spb
 *
 */
public interface Contexed extends ContextProvider {
   default public AppContext getContext() {
	   return AppContext.getContext();
   }
   default public Logger getLogger() {
	   return getContext().getService(LoggerService.class).getLogger(getClass());
   }
}