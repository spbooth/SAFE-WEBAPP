// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.log;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryCreator;
/** Top level TransitionProviderFactory that locates the Item TransitionProvider
 * by going through the LogOwner 
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: LogTransitionProviderFactory.java,v 1.8 2014/09/15 14:30:34 spb Exp $")

public class LogTransitionProviderFactory implements
		TransitionFactoryCreator<LogTransitionProvider>, Contexed{

	private final AppContext conn;
	public LogTransitionProviderFactory(AppContext c){
		conn=c;
	}
	public LogTransitionProvider getTransitionProvider(String tag) {
		LogOwner<?> owner = conn.makeObject(LogOwner.class, tag);
		if( owner != null){
			return owner.getLogFactory().getTransitionProvider();
		}
		return null;
	}
	public AppContext getContext() {
		return conn;
	}

}