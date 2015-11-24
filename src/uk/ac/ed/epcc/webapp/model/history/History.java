// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.history;

import java.util.Date;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

public interface History<P extends DataObject> {

	public abstract boolean contains(java.util.Date val);

	/**
	 * Get the Peer object and restore state to that of the History object.
	 * This means any fields not stored in the History table will be
	 * initialised. Note that the changes still need to be comitted if you
	 * want the database to be rolled back.
	 * 
	 * @return peer DataObject
	 * @throws DataException
	 */
	public abstract P getAsPeer() throws DataException;

	public abstract java.util.Date getEndTimeAsDate();

	/**
	 * Return a new uncommited peer-class object with the initial contents
	 * taken from the history object.
	 * 
	 * @return peer-class DataObject
	 * @throws DataFault
	 */
	public abstract P getNew() throws DataFault;

	/**
	 * Get the current version of the Peer object.
	 * 
	 * @return DataObject the Peer
	 * @throws DataException
	 */
	public abstract P getPeer() throws DataException;

	public abstract int getPeerID();

	public abstract java.util.Date getStartTimeAsDate();

	public abstract boolean overlapps(Date start, Date end);

	/**
	 * Indicate peer object has been deleted
	 * 
	 * probably want to obtain this object using Factory.update
	 * 
	 */
	public abstract void terminate();

	public abstract void release();
	
	public boolean matchIntegerProperty(String key, int val);
}