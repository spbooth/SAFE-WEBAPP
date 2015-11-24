// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.swing;

import javax.swing.JFrame;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

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
@uk.ac.ed.epcc.webapp.Version("$Id: SwingTransitionHandler.java,v 1.2 2014/09/15 14:30:22 spb Exp $")

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

		}
		return start;
	}

	public AppContext getContext() {
		return conn;
	}
}