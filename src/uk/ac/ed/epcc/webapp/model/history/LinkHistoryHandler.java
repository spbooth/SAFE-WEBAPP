// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.history;

import java.util.Date;

import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.IndexedLinkManager;
import uk.ac.ed.epcc.webapp.model.history.HistoryFactory.HistoryRecord;

public interface LinkHistoryHandler<L extends Indexed, R extends Indexed, T extends IndexedLinkManager.Link<L, R>> extends Contexed,HistoryHandler<T>{

	public IndexedLinkManager<T,L,R> getLinkManager();
	
	public abstract SQLFilter<? extends HistoryRecord<T>> getHistoryFilter(L left,
			R right, Date start, Date end) throws DataException;

	public abstract boolean canLeftJoin();

	public abstract boolean canRightJoin();
	
	public boolean isValid();

}