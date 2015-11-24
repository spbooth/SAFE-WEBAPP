// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: DefaultTableTransitionRegistry.java,v 1.4 2014/09/15 14:30:26 spb Exp $")
public class DefaultTableTransitionRegistry<X extends TableStructureTransitionTarget> extends AbstractTableRegistry {

	/**
	 * 
	 */
	public DefaultTableTransitionRegistry(Repository res,TableSpecification spec) {
		addTransitionSource(new GeneralTransitionSource<X>(res));
		if( spec != null ){
			addTransitionSource(new TableSpecificationTransitionSource<X>(res, spec));
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.TableTransitionRegistry#getTableTransitionSummary(uk.ac.ed.epcc.webapp.content.ContentBuilder, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	public void getTableTransitionSummary(ContentBuilder hb,
			SessionService operator) {
		return;

	}

}
