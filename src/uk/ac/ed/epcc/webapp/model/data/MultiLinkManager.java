// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;

import java.util.*;

/**
 * MultiLinkManager is a superclass for handling Linkage objects that link
 * multiple tables. This is more general that the 2-way LinkManager but harder
 * to use as a result, instead of specifying right and left peers as method
 * arguments you need to construct a Template and set the peer classes in the
 * Template This class could implement auto_history and the join optimisations
 * used by LinkManager but currently does not.
 * 
 * @author spb
 * @param <M> type of MultiLink object
 * 
 */
public abstract class MultiLinkManager<M extends MultiLinkManager.MultiLink> extends DataObjectFactory<M> {
	public abstract
@uk.ac.ed.epcc.webapp.Version("$Id: MultiLinkManager.java,v 1.28 2014/09/15 14:30:29 spb Exp $")
 static class MultiLink extends DataObject {
		private Map<String,DataObject> peers = null;
        private MultiLinkManager man;
		public MultiLink(MultiLinkManager man, Repository.Record res) {
			super(res);
			this.man=man;
		}

		
		/**
		 * get a peer object
		 * 
		 * @param key
		 *            String field name for peer.
		 * @return DataObject
		 * @throws DataException
		 */
		protected DataObject getPeer(String key) throws DataException {
			DataObject peer = null;
			if (peers != null) {
				peer = peers.get(key);
			}
			if (peer == null) {
				// get from factory and cache
				DataObjectFactory fac = (DataObjectFactory) man.factories.get(key);
				peer = fac.find(record.getNumberProperty(key));
				setPeer(key, peer);
			}
			return peer;
		}

		/**
		 * cache the Peer object in the MultiLink this sets the property if an
		 * unitialised MultiLink
		 * 
		 * @param key
		 * @param o
		 */
		protected void setPeer(String key, DataObject o) {
			if (peers == null) {
				peers = new HashMap<String,DataObject>();
			}
			DataObjectFactory fac = (DataObjectFactory) man.factories.get(key);
			if (!fac.isMine(o)) {
				throw new ClassCastException(
						"Illegal type passed to MultiLinkManager.setPeer");
			}
			Number num = record.getNumberProperty(key);
			if (num == null) {
				record.setProperty(key, o.getID());
			}
			peers.put(key, o);
		}

		/**
		 * extension point for Link subclasses this method is called when new
		 * records are created to initialise subclass fields to sensible default
		 * values
		 * 
		 * @throws DataException
		 * @throws DataFault
		 * 
		 */
		protected abstract void setup() throws DataFault, DataException;
	}

	/**
	 * Template is a class that specified the peer objects to be used when
	 * making a selection
	 * 
	 * @author spb
	 * 
	 */
	public class Template {
		private Map<String,DataObject> map;

		public Template() {
			map = new HashMap<String,DataObject>();
		}

		/**
		 * does this DataObject match the template This method also initialises
		 * the Peer cache
		 * 
		 * @param o
		 *            DataOBject
		 * @return boolean true on match
		 */
		public boolean accept(MultiLink o) {
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key =  it.next();
				DataObject d = map.get(key);
				if (o.record.getIntProperty(key, 0) != d.getID()) {
					return false;
				}
				o.setPeer(key, d);
			}
			return true;
		}

		public void addPeer(DataObject peer) {
			String table = peer.getFactoryTag();
			if (table_to_key.containsKey(table)) {
				map.put(table_to_key.get(table), peer);
			} else {
				throw new ConsistencyError("No coresponding factory for peer");
			}
		}

		/** Get a filter corresponding to the Template
		 * 
		 * @return SQLFilter
		 */
		public SQLFilter<M> getFilter(){
			SQLAndFilter<M> fil = new SQLAndFilter<M>(getTarget());
			for(String key : map.keySet()){
				DataObject d = map.get(key);
				fil.addFilter(new ReferenceFilter<M,DataObject>(MultiLinkManager.this,key,d));
			}
			return fil;
		}

		/**
		 * Does this template fully specify all peers
		 * 
		 * @return boolean
		 */
		public boolean isComplete() {
			return map.size() == factories.size();
		}

		/**
		 * set Peers from a template
		 * 
		 * @param o
		 */
		public void setPeers(MultiLink o) {
			for (Iterator it = map.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				DataObject d = map.get(key);
				o.setPeer(key, d);
			}
		}
	}

	//TODO change to IndexedProducer
	private Map<String,DataObjectFactory> factories;

	private Map<String,String> table_to_key;

	/**
	 * Basic Constructor subclasses should
	 * 
	 * @param c
	 */
	protected MultiLinkManager(AppContext ctx,String homeTable) {
   	 	
     		setContext(ctx, homeTable);
     	
		factories = new HashMap<String,DataObjectFactory>();
		table_to_key = new HashMap<String,String>();
	}
	@Override
	public TableSpecification getDefaultTableSpecification(AppContext ctx,
  			String homeTable) {
		// need to extend
		 TableSpecification spec = new TableSpecification();
		 return spec;
	 }
	/**
	 * Method for sub-class to specify a link field and Factory. Normally called
	 * withing the sub-class constructor
	 * 
	 * @param key
	 * @param fac
	 */
	protected void addFactory(String key, DataObjectFactory fac) {
		if (res.hasField(key)) {
			factories.put(key, fac);
			table_to_key.put(fac.getTag(), key);
		} else {
			throw new ConsistencyError("Invalid field " + key
					+ "for MultiLinkFactory");
		}
	}

	/**
	 * get an iterator over all MultiLinks matching the specified tempalte
	 * 
	 * @param t
	 *            Template
	 * @return Iterator
	 * @throws DataFault
	 */
	public Iterator<M> getIterator(Template t) throws DataFault {
		return new FilterIterator(t.getFilter());
	}

	/**
	 * Find a MultiLink object corresponding to a complete Tempalte
	 * 
	 * @param t
	 *            Tamplate
	 * @return MultiLink or null if it does not exist
	 * @throws DataException
	 */
	public M getLink(Template t) throws DataException {
		M res = null;
		if (!t.isComplete()) {
			throw new ConsistencyError(
					"getLink called with incomplete template");
		}
		try {
			res =  find(t.getFilter());
			t.setPeers(res); // initialise the caches
		} catch (DataNotFoundException e) {
			res = null;
		}
		return res;
	}

	/**
	 * Find a MultiLink or create it, if it does not exist.
	 * 
	 * @param t
	 * @return MultiLink
	 * @throws DataException
	 */
	public MultiLink makeLink(Template t) throws DataException {
		MultiLink res = getLink(t);
		if (res == null) {
			// make the object
			res = makeBDO();
			t.setPeers(res);
			res.setup();
			res.commit();
		}
		return res;
	}
	@Override
	public Class<? super M> getTarget() {
		return MultiLink.class;
	}
	

}