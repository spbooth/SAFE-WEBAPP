// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.log;

import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;

public interface LogTransitionProvider extends TransitionProvider<TransitionKey<LogFactory.Entry>, LogFactory.Entry>{

}