// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.history;

import java.util.Date;
import java.util.Iterator;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

public interface HistoryHandler<P extends DataObject> {

	/**
	 * Find History by peer and date
	 * 
	 * @param peer
	 * @param time
	 * @return History
	 * @throws uk.ac.ed.epcc.webapp.jdbc.exception.DataException
	 * @throws IllegalArgumentException 
	 * 
	 */

	public abstract History<P> find(P peer, Date time)
			throws uk.ac.ed.epcc.webapp.jdbc.exception.DataException,
			IllegalArgumentException;

	public abstract Iterator<? extends History<P>> getIterator(P peer, Date start,
			Date end) throws DataFault;

	public abstract Iterator<? extends History<P>> getIterator(SQLFilter<P> peer,
			Date start, Date end) throws DataFault;

	public abstract Iterator<? extends History<P>> getIterator(Date start, Date end) throws DataFault;

	/**
	 * is peer of right type
	 * 
	 * @param peer
	 *            a DataObject to check
	 * @return boolean true if type matches
	 */
	public abstract boolean isPeerType(DataObject peer);

	/**
	 * Delete all history records corresponding to a specified peer object.
	 * 
	 * @param o
	 *            DataObject peer
	 * @throws DataFault
	 */
	public abstract void purge(P o) throws DataFault;

	/**
	 * Terminates the history for a given peer object
	 * 
	 * 
	 * updates the quota history to reflect the current status of the quota.
	 * 
	 * @param peer
	 *            Peer object whose history will be terminated
	 * @throws IllegalArgumentException
	 * @throws DataException
	 */
	public abstract void terminate(P peer) throws IllegalArgumentException,
			DataException;

	/**
	 * Updates quota history.
	 * 
	 * updates the quota history to reflect the current status of the quota.
	 * 
	 * @param peer
	 * @return History
	 * @throws IllegalArgumentException
	 * @throws ConsistencyError
	 * @throws DataException
	 */
	public abstract History<P> update(P peer)
			throws IllegalArgumentException, ConsistencyError, DataException;

	public String getPeerName();
}