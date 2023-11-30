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
package uk.ac.ed.epcc.webapp.forms.swing;

import javax.swing.JFrame;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.logging.Logger;

/** This class implements transitions using a series dialog windows
 * 
 * The assumption is that the main program invokes a starting transition 
 * (for example by a button press) 
 * This class then processes the transition sequence until either a leaf transition
 * such as a message is encountered or an un-handlable FormResult such as a navigation result is generated.
 * 
 * @author spb
 *
 */


public class SwingTransitionHandler implements Contexed{
	private final AppContext conn;
	private final JFrame parent;
	public SwingTransitionHandler(JFrame parent,AppContext conn){
		this.parent=parent;
		this.conn=conn;
	}
	
	public FormResult process(FormResult start) throws Exception{
		SwingFormResultVisitor vis = new SwingFormResultVisitor(parent, conn);
		try{
			while(start != null ){
				vis.reset();
				start.accept(vis);
				start=vis.getNextResult();
			}
		}catch(UnsupportedResultException e){
			Logger log = Logger.getLogger(conn,getClass());
			log.error("Unsupported result", e);
		}
		return start;
	}

	public AppContext getContext() {
		return conn;
	}
}