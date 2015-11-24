// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.apps;

import java.util.LinkedList;
import uk.ac.ed.epcc.webapp.Contexed;

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
}