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
package uk.ac.ed.epcc.webapp.apps;

import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.logging.Logger;

/** Interface for classes that implement gridsafe sub-commands
 * 
 * @author spb
 *
 */
public interface Command extends Contexed {
   /** Run the command
    * 
    * @param args
    */
   public void run(LinkedList<String> args) ;
   /** One line description of the operation
    * 
    * @return description String
    */
   public String description();
   /** Get the help text.
    * 
    * @return help text
    */
   public String help();
   
   public default Logger getLogger() {
	   return Logger.getLogger(getContext(),getClass());
   }
}