// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.history;

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.charts.PeriodSequencePlot;
import uk.ac.ed.epcc.webapp.charts.SetPlot;
import uk.ac.ed.epcc.webapp.charts.TimeChart;
import uk.ac.ed.epcc.webapp.charts.strategy.LabelledSetRangeMapper;
import uk.ac.ed.epcc.webapp.charts.strategy.RangeMapper;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpressionMatchFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.Index;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.CachedIndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.ReferenceFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.MultipleResultException;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterDelete;
import uk.ac.ed.epcc.webapp.model.data.iterator.DecoratingIterator;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedTypeProducer;
import uk.ac.ed.epcc.webapp.model.data.table.TableStructureDataObjectFactory;

/**
 * Class to generate History objects. As History objects are only manipulated
 * via the factory (they are logically part of a series rather than making sense
 * on their own) we make the History object an pseudo inner class of
 * HistoryFactory. The factory reference is explicit rather than a real inner
 * class to stop the history object being generic parameterised by itself.
 * <p>
 * Every
 * entry in a History table contains some fields from the tracked object (any
 * field without a corresponding field in the history table is not tracked.
 * There is also a field giving the ID of the peer object. This defaults to
 * PeerID but can be changed by overriding the getPeerName() method. There are
 * also 2 time fields giving the period over which the data is valid. These can
 * be either SQL time-type fields or numeric fields
 * 
 * @author spb
 * @param <P> type of peer
 * @param <H> 
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: HistoryFactory.java,v 1.126 2015/02/23 08:49:21 spb Exp $")


public class HistoryFactory<P extends DataObject,H extends HistoryFactory.HistoryRecord<P>> extends TableStructureDataObjectFactory<H> implements HistoryHandler<P> {
	

	public static final Feature HISTORY_CACHE_FEATURE = new Feature("history_cache",true,"should history factories cache the peer objects to save on lookups");


	/**
	 * Class Representing time history of and object.
	 * 
	 * 
	 * History information is stored as a series of time periods. a single
	 * record is used for the entire period the information remains unchanged
	 * the last entry in a sequence always has an end date in the far future
	 * when inserting a new value the end data of the previous last entry is set
	 * to the current time and a new record is added. This allows us to select
	 * the appropriate record for any time.
	 * <p>
	 * Each record stores the record number of the corresponding object and the
	 * time period information the sub-class stores the actual data. Where
	 * possible use the same name for data fields in the Peer object and the
	 * sub-classed history object. Then the makeHistory method can copy most of
	 * the fields from the peer object as a hashtable.
	 * <p>
	 * 
	 * 
	 * We use the convension that the The StartTime is included in the period
	 * but the End time is not.
	 * <p>
	 * History is an inner class of the Factory that manages the History.
	 * Normally History will be sub-classed for each different peer object but
	 * the basic History object is sufficient to record history.
	 * 
	 * To actually use the History infoirmation you can either subclass History
	 * to add methods or call getAsPeer or getRollback to transfer the data into
	 * a peer class object then access the data via the methods in the peer
	 * class.
	 * 
	 * @author s.booth
	 * @param <P> Type of peer object
	 */

	public static  class HistoryRecord<P extends DataObject> extends DataObject implements uk.ac.ed.epcc.webapp.model.history.History<P> {
        protected HistoryFactory<P,?> history_factory;
		public HistoryRecord(HistoryFactory<P,?> fac,Repository.Record res) {
			super(res);
			history_factory=fac;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.history.History#contains(java.util.Date)
		 */
		public final boolean contains(java.util.Date val) {
			if ((!val.before(getStartTimeAsDate()))  && val.before(getEndTimeAsDate())) {
				return true;
			}
			return false;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.history.History#getAsPeer()
		 */
		public final P getAsPeer() throws DataException {
			P peer = getPeer();
			peer.setContents(getMap());
			return peer;
		}

		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.history.History#getEndTimeAsDate()
		 */
		public final java.util.Date getEndTimeAsDate() {
			return record.getDateProperty(END_TIME_FIELD);
		}

		

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.history.History#getNew()
		 */
		public P getNew() throws DataFault {
			P peer = history_factory.getPeerFactory().makeBDO();
			peer.setContents(getMap());
			return peer;
		}

		

		final double getOverlapp(String plot_field, Date start, Date end) {
			Number n = record.getNumberProperty(plot_field);
			if (!overlapps(start, end) || n == null || start.equals(end)) {
				return 0.0;
			}

			// If we only overlapp with part of the region being queried then
			// return that fraction of the value, otherwise return the
			// value
			long o_start = start.getTime();
			long t_start = o_start;
			long o_end = end.getTime();
			long t_end=o_end;
			long p_start = getStartTimeAsDate().getTime();
			long p_end = getEndTimeAsDate().getTime();

			if (p_start > o_start)
				o_start = p_start;
			if (p_end < o_end)
				o_end = p_end;
			float frac = ((float) (o_end - o_start) / ((float) (t_end - t_start)));

			return frac * n.doubleValue();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.history.History#getPeer()
		 */
		public final P getPeer() throws DataException {
			return history_factory.getPeerProducer().find(getPeerID());
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.history.History#getPeerID()
		 */
		public final int getPeerID() {
			return record.getIntProperty(history_factory.getPeerName(), 0);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.history.History#getStartTimeAsDate()
		 */
		public final java.util.Date getStartTimeAsDate() {
			return record.getDateProperty(START_TIME_FIELD);
		}


		/**
		 * has the peer type changed from the Object
		 * 
		 * @param peer
		 *            DataObject to check
		 * @return boolean true if changed
		 */
		protected boolean hasChanged(P peer) {
			// generic implementation
			return !record.equals(peer.getMap());
		}
        public boolean matchIntegerProperty(String key, int val){
        	return val == record.getIntProperty(key, 0);
        }
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.history.History#overlapps(java.util.Date, java.util.Date)
		 */
		public final boolean overlapps(Date start, Date end) {
		
			return (getStartTimeAsDate().before(end) && getEndTimeAsDate().after(start));
		}

		protected final void setEndTime(java.util.Date val) {
			record.setProperty(END_TIME_FIELD, val);
		}


		private void setPeerID(int id) {
			record.setProperty(history_factory.getPeerName(), new Integer(id));

		}

		protected final void setStartTime(java.util.Date val) {
			record.setProperty(START_TIME_FIELD, val);
		}

		/**
		 * Initialise new History object from a Peer
		 * 
		 * @param peer
		 *            DataObject to act as peer
		 */
		void setup(P peer) {

			setPeerID(peer.getID());
			setStartTime(new Date());
			setEndTime(new Date(ENDTIME));
			// make sure we don't have a field clash
			Map<String,Object> hash = peer.getMap();
			hash.remove(history_factory.getPeerName());
			hash.remove(START_TIME_FIELD);
			hash.remove(END_TIME_FIELD);
			record.putAll(hash);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.history.History#terminate()
		 */
		public final void terminate() {

			setEndTime(new Date());
		}

		@Override
		public final void pre_commit(boolean dirty) throws DataFault {
			if( getEndTimeAsDate().before(getStartTimeAsDate())){
				throw new ConsistencyError("History object with reversed dates");
			}
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.history.History#release()
		 */
		@Override
		public void release(){
			history_factory=null;
			super.release();
		}
	}
    
	/**
	 * Filter for History objects selecting by peer object and date range
	 * 
	 * @author spb
	 * 
	 */
	public class HistoryFilter extends SQLAndFilter<H> {
		/**
		 * Create a HistoryFilter by specifying peer object and date range peer
		 * and start can be null to signify a wildcard
		 * 
		 * @param peer
		 *            DataObject peer
		 * @param start
		 *            Date start of period
		 * @param end
		 *            Date end of period
		 */
		public HistoryFilter(P peer, Date start, Date end) {
			this(start,end);
			if (peer != null) {
				if (isPeerType(peer)) {
					addFilter(new ReferenceFilter<H,P>(HistoryFactory.this,getPeerName(),peer));
				} else {
					throw new IllegalArgumentException(
							"Wrong peer type passed to History.Filter");
				}
			}
		}
		/** Select History objects by date range and a SQLFilter on the peer type.
		 * 
		 * @param peer_filter
		 * @param start
		 * @param end
		 */
		public HistoryFilter(SQLFilter<P> peer_filter, Date start, Date end) {
			this(start,end);
			addFilter(new RemoteFilter<P>(peer_filter,getPeerName(),getPeerFactory()));
		}
		/** Select all history objects in a date range.
		 * Start may be null to indicate a wild-card.
		 * @param start
		 * @param end
		 */
		public HistoryFilter(Date start, Date end) {
			super(HistoryFactory.this.getTarget());
			if (start != null) {
				if (end == null) {
					throw new IllegalArgumentException(
							"null end date in History.Filter");
				}
				addFilter(new TimeFilter(START_TIME_FIELD,MatchCondition.LE,end));
				addFilter(new TimeFilter(END_TIME_FIELD,MatchCondition.GT,start));
			}
		}
	}

	
	
	

	/**
	 * Maps history objects to display classes for Timechart graphs. handles the
	 * basic overlap logic but not the set mapping.
	 * 
	 * @see TimeChart
	 * @author spb
	 * 
	 */
	public  class Mapper implements RangeMapper<H> {

		private final String plot_field; // field we are going to plot
		public Mapper(String field) {
			plot_field = field;
		}
    
		public boolean after(H h, Date point) {
			// We want greater than or equals comparison to avoid overlap
			return !h.getStartTimeAsDate().before(point);
		}

		public boolean before(H h, Date point) {
			return h.getEndTimeAsDate().before(point);
		}

		public float getOverlapp(H h, Date start, Date end) {
		
			return (float) h.getOverlapp(plot_field, start, end);

		}

		public boolean overlapps(H h, Date start, Date end) {
			return h.overlapps(start, end);
		}

	}
	/** Converts History objects into Peer object representation
	 * 
	 * @author spb
	 *
	 */
    public class PeerIterator extends DecoratingIterator<P,HistoryRecord<P>>{

		@Override
		public P next() {
			HistoryRecord<P> i = nextInput();
			try {
				return i.getAsPeer();
			} catch (DataException e) {
				i.getContext().error(e,"Error getting peer");
				return null;
			}
		}

		public PeerIterator(Iterator<? extends HistoryRecord<P>> i) {
			super(i);
		}
    	
    }
	static protected long ENDTIME = java.lang.Long.MAX_VALUE;

	protected static final String START_TIME_FIELD = "StartTime";

	protected static final String END_TIME_FIELD = "EndTime";

	
	
	
	protected IndexedTypeProducer getPeerReference(){
		FieldInfo info = res.getInfo(getPeerName());
		if( info == null ){
			return null;
		}
		return  (IndexedTypeProducer) info.getTypeProducer(); 
	}
	private DataObjectFactory<P> peer_factory=null;
	@SuppressWarnings("unchecked")
	protected DataObjectFactory<P> getPeerFactory(){
		if( peer_factory == null){
			setPeerFactory((DataObjectFactory<P>) getPeerReference().getProducer());
		}
		return peer_factory;
	}
	// Note this is not the factory as it may be wrapped in a cache.
	private IndexedProducer<P> peer_producer=null;
    public IndexedProducer<P> getPeerProducer(){
    	if( peer_producer == null ){
    		DataObjectFactory<P> fac = getPeerFactory();
    		if( HISTORY_CACHE_FEATURE.isEnabled(getContext())){
    		    peer_producer = new CachedIndexedProducer<P>(fac);
    	    }else{
    	    	peer_producer = fac;
    	    }
    	}
    	return peer_producer;
    }
	/**
	 * Standard Constructor for HistoryFactory HistoryFactory contains a ref to
	 * its peer factory. We use a constructor like this to encourage
	 * HistoryFactories to be produced from existing PeerFactories. This is
	 * especially important If the PeerFactory wants to hold a reference to the
	 * HistoryFactory e.g.
	 *  {@link uk.ac.ed.epcc.webapp.model.data.LinkManager}
	 * 
	 * @param fac
	 * @param table table to store data
	 */
	public HistoryFactory(DataObjectFactory<P> fac,String table) {
		peer_factory=fac; // need to cache this NOW as getDefaultTableSpecification will retreive it in sub-classes.
		AppContext c = fac.getContext();
		
    	setContext(c, table);
		
	}
	@SuppressWarnings("unchecked")
	protected void setPeerFactory(DataObjectFactory<P> fac) {
		peer_factory=fac;
		if( ! res.hasTypeProducer(getPeerName())){
			res.addTypeProducer(new IndexedTypeProducer(getContext(),getPeerName(), fac));
		}
	}
	/** Constructor for Stand alone history factories. 
	 * The peer factory needs to be resolved from the meta-data or config.
	 * 
	 * @param c
	 * @param table
	 * 
	 */
	public HistoryFactory(AppContext c, String table) {
    		setContext(c, table);
	}
	@Override
	public TableSpecification getDefaultTableSpecification(AppContext c,String homeTable){
		TableSpecification spec = new TableSpecification("HistoryID");
		spec.setField(getPeerName(), new IntegerFieldType());
		spec.setField(START_TIME_FIELD, new DateFieldType(true, null));
		spec.setField(END_TIME_FIELD, new DateFieldType(true,null));
		try{
		Index search = spec.new Index("SearchIndex", false, END_TIME_FIELD, START_TIME_FIELD);
		Index finder = spec.new Index("PeerIndex", false, getPeerName(), END_TIME_FIELD, START_TIME_FIELD);
		}catch(Exception e){
			c.getService(LoggerService.class).getLogger(getClass()).error("Error making index",e); 
		}
		
		return spec;
	}
	
	
	/**
	 * @param gm
	 * @param iter
	 * @param tc
	 * @param other_thresh
	 * @return Chart.Plot
	 * @throws Exception 
	 */
	public SetPlot addChart(LabelledSetRangeMapper<H> gm, Iterator<H> iter,
			TimeChart tc, float other_thresh) throws Exception {
		return addChart(gm, iter, tc, other_thresh,0);
	}
	@SuppressWarnings("unchecked")
	public SetPlot addChart(LabelledSetRangeMapper<H> gm, Iterator<H> iter,
			TimeChart tc, float other_thresh,int max_plot) throws Exception {

		Vector<String> labels = gm.getLabels();
		// create dataset, don't add labels yet as labels
		// vector may grow as data added
		PeriodSequencePlot ds = tc.addAreaGraph(labels.size());


		tc.addDataIterator(ds,gm, iter);
		labels = gm.getLabels();
		ds.setLegends( labels
				.toArray(new String[labels.size()]));
		tc.sortSets(ds,other_thresh,max_plot);
		ds.doConvertToStacked();
		return ds;
	}

	/**
	 * calculates the time the current data should expire. defaults to ENDTIME
	 * but can be overridden to implement data we want to time expire
	 * 
	 * @param peer
	 * @param start
	 * @return long
	 */
	protected Date expireTime(P peer, Date start) {
		return new Date(ENDTIME);
	}   
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.history.HistoryHandler#find(P, java.util.Date)
	 */

	
	public final H find(P peer, Date time)
			throws uk.ac.ed.epcc.webapp.jdbc.exception.DataException,
			IllegalArgumentException {

		if (peer == null || !isPeerType(peer)) {
			throw (new IllegalArgumentException("Wrong peer type passed"));
		}
		SQLAndFilter<H> fil =new  SQLAndFilter<H>(getTarget());
		fil.addFilter(new ReferenceFilter<H,P>(this, getPeerName(),peer));
		fil.addFilter(new TimeFilter(START_TIME_FIELD,MatchCondition.LE,time));
		fil.addFilter(new TimeFilter(END_TIME_FIELD,MatchCondition.GT,time));
		H current=null;
		try{
		  current = find(fil);
		}catch(MultipleResultException e){
		  // Somehow we have multiple matching records. sequence is corrupt
	      Logger log = getContext().getService(LoggerService.class).getLogger(getClass());
	      log.error("Corrupt history sequence duplicate records",e);
	      // Try to fix this set of records.
	      fixSeries(peer, new FilterIterator(fil));
	      // Try again
		  current = find(fil);	
			
		}
		return  current;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.history.HistoryHandler#getIterator(P, java.util.Date, java.util.Date)
	 */
	public Iterator<H> getIterator(P peer, Date start, Date end)
			throws DataFault {
		return new FilterIterator(new HistoryFilter(peer, start, end));
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.history.HistoryHandler#getIterator(uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter, java.util.Date, java.util.Date)
	 */
	public Iterator<H> getIterator(SQLFilter<P> peer, Date start, Date end)
	throws DataFault {
		return new FilterIterator(new HistoryFilter(peer, start, end));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.history.HistoryHandler#getIterator(java.util.Date, java.util.Date)
	 */
	public Iterator<H> getIterator(Date start, Date end) throws DataFault {
		return new FilterIterator(new HistoryFilter( start, end));
	}

	/**
	 * Name of the field containing the peer id This must match the equivalent
	 * methid in the History type.
	 * 
	 * @return String containing field name
	 */
	public String getPeerName() {
		return "PeerID";
	}
    
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.history.HistoryHandler#isPeerType(uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	public final boolean isPeerType(DataObject peer) {
		return getPeerFactory().isMine(peer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository)
	 */
	@Override
	protected DataObject makeBDO(Repository.Record res) throws DataFault {
		/*
		 * We can use a generic history object where we don't need any special
		 * behaviour
		 * 
		 */
		return new HistoryRecord<P>(this,res);
	}
	@Override
	public Class<? super H> getTarget(){
		return HistoryRecord.class;
	}

	/**
	 * make History object from peer
	 * 
	 * @param peer
	 *            DataObject to record.
	 * @return history object
	 * @throws ConsistencyError
	 * @throws DataFault
	 */
	protected final HistoryRecord<P> makeHistory(P peer)
			throws ConsistencyError, DataFault {
		// default implementation is to initialise from Hashtable
		// this picks up any field the two tables have in common.
		HistoryRecord<P> h = makeBDO();

		// intialise the timestamps.
		h.setup(peer);
		return h;
	}

	private final Date now() {
		return new Date();
	}

	
	@Override
	protected List<OrderClause> getOrder() {
		List<OrderClause> order = super.getOrder();
		order.add(res.getOrder(START_TIME_FIELD, false));
		return order;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.history.HistoryHandler#purge(P)
	 */
	public void purge(P o) throws DataFault {
		if (!isPeerType(o)) {
			throw new ConsistencyError("Object does not match peer type");
		}
		for (Iterator it = getIterator(o, null, null); it.hasNext();) {
			HistoryRecord h = (HistoryRecord) it.next();
			h.delete();
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.history.HistoryHandler#terminate(P)
	 */
	public final synchronized void terminate(P peer)
			throws IllegalArgumentException, DataException {

		Logger log = getLogger();
		// log.info("History.Factory.terminate() ");

		HistoryRecord tail = null;
		try {
			tail = find(peer, now());
			tail.terminate();
			tail.commit();
		} catch (DataNotFoundException e) {
			log.info("Matching History object not found");
		}

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.history.HistoryHandler#update(P)
	 */
	public final synchronized HistoryRecord<P> update(P peer)
			throws IllegalArgumentException, ConsistencyError, DataException {

		if (peer == null) {
			throw new IllegalArgumentException(
					"Null peer passed to History.update");
		}
		// Logger log = getLogger();
		// log.info("History.Factory.update() ");

		// Reset previous end value if it exists. Use the new_value start time
		// so
		// regions match up exactly
		HistoryRecord<P> tail = null;
		try {
			tail = find(peer, now());
		} catch (uk.ac.ed.epcc.webapp.jdbc.exception.DataException e) {
			// log.info("Matching History object not found");
		}
		if (tail == null || tail.hasChanged(peer)) {

			// log.info("Making new object");
			// make a new entry
			HistoryRecord<P> new_value = makeHistory(peer);
			new_value.commit();
			// log.info("Made new object");
			if (tail != null) {
				tail.setEndTime(new_value.getStartTimeAsDate());
				tail.commit();
			}
			tail = new_value;
		}

		// now update the end Time, we rely on commit not
		// making edit if the endTime is unchanged.
		if (tail != null) {
			tail.setEndTime(expireTime(peer, now()));
			tail.commit();
		}
		return tail;

	}
	/** fix a corrupt history sequence, ensure there are no overlapping
	 * records and fix and reversed timestamps and spurious splits.
	 * 
	 * @param peer
	 * @param start 
	 * @param end 
	 * @throws DataException 
	 */
	public void fixSeries(P peer,Date start, Date end) throws DataException{
		Iterator<H> it = getIterator(peer,start,end);
		fixSeries(peer, it);		
	}
	/** fix a corrupt history sequence, ensure there are no overlapping
	 * records and fix and reversed timestamps and spurious splits.
	 * @param peer
	 * @param it
	 * @throws DataFault
	 * @throws DataException
	 */
	protected void fixSeries(P peer, Iterator<H> it) throws DataFault,
			DataException {
		HistoryRecord<P> h=null;
		HistoryRecord<P> prev=null;
		Logger log = getLogger();
		int changes=0;
		int deletes=0;
		log.debug("fix sequence for "+peer.getClass().getCanonicalName()+":"+peer.getID());
		
		
		
		// To avoid problems due to chunked fetches we need
		// to 
		while(it.hasNext()){
			
				h=it.next();
				if(h.getEndTimeAsDate().before(h.getStartTimeAsDate())){
					log.debug("reversed timestamps for "+h.getID());
					h.setEndTime(expireTime(peer, h.getStartTimeAsDate()));
					h.commit();
					changes++;
				}
				if( prev == null ){
					log.debug("Set prev");
					prev=h;
				}else{
					if( ! prev.getEndTimeAsDate().after(h.getStartTimeAsDate())){
						if( prev.getEndTimeAsDate().equals(h.getStartTimeAsDate())){
							// abut as they are supposed to
							  P p = h.getAsPeer();
							  if( ! prev.hasChanged(p)){
								  // extend range
								  prev.setEndTime(h.getEndTimeAsDate());
								  changes++;
						    	  log.debug("Extend and delete");
						    	  deletes++;
						    	  //h.delete();
						    	  // Can't delete as this will break the chunking code and
						    	  // make some records appear multile times in the sequence.
						    	  // mark for deletion by setting start=end
						    	  h.setStartTime(h.getEndTimeAsDate());
						    	  h.commit();
						    	  
							  }else{
								// don't overlap
									prev.commit();
									prev=h;
							  }
						}else{
							// there is a missing gap
							// don't overlap
							prev.commit();
							prev=h;
						}
					}else{
						log.debug("records overlapp "+prev.getStartTimeAsDate()+":"+prev.getEndTimeAsDate()+" "+h.getStartTimeAsDate()+":"+h.getEndTimeAsDate());
					    P p = h.getAsPeer();
					    if( prev.hasChanged(p)){
					    	// values different
					    	prev.setEndTime(h.getStartTimeAsDate());
					    	prev.commit();
					    	changes++;
					    	prev=h;
					    	log.debug("values changed");
					    }else{
					    	if( h.getEndTimeAsDate().after(prev.getEndTimeAsDate())){
					    		prev.setEndTime(h.getEndTimeAsDate());
					    		changes++;
					    		log.debug("Extend range");
					    	}
					    	log.debug("delete duplicate");
					    	deletes++;
					    	//h.delete();
					    	h.setStartTime(h.getEndTimeAsDate());
					    	h.commit();
					    }
					}
				}
			
		}
		if( prev != null ){
			prev.commit();
		}
		if( deletes > 0 ){
			SQLAndFilter<H> delete_fil = new SQLAndFilter<H>(getTarget());
			delete_fil.addFilter(new ReferenceFilter<H,P>(HistoryFactory.this,getPeerName(),peer));
			delete_fil.addFilter(new SQLExpressionMatchFilter<H, Date>(getTarget(), 
					(SQLExpression<Date>)res.getDateExpression(getTarget(), START_TIME_FIELD),
					(SQLExpression<Date>)res.getDateExpression(getTarget(), END_TIME_FIELD)));
			FilterDelete<H> reaper = new FilterDelete<H>(res);
			int actual = reaper.delete(delete_fil);
			log.debug("Actual deletes="+actual+" expected="+deletes);
		}
		if( changes > 0 ){
			log.debug(getClass().getCanonicalName()+".fixSeries "+peer.getIdentifier()+" changes="+changes+" deletes="+deletes);
		}
	}
	/** close gaps in history sequence over a time range.
	 * 
	 * @param peer
	 * @param start
	 * @param end
	 * @throws DataException
	 */
	public void closeGaps(P peer,Date start, Date end) throws DataException{
		Iterator<H> it = getIterator(peer,start,end);
		HistoryRecord<P> h=null;
		HistoryRecord<P> prev=null;
		Logger log = getLogger();
		String name=peer.getIdentifier();
		int changes=0;
		int deletes=0;
		DateFormat df = DateFormat.getDateTimeInstance();
		log.debug("close gaps for "+peer.getClass().getCanonicalName()+":"+peer.getID());
		while(it.hasNext()){
			
				h=it.next();
				if( prev != null ){
					//log.debug(name+" ("+df.format(prev.getStartTimeAsDate())+","+df.format(prev.getEndTimeAsDate())+") ("+df.format(h.getStartTimeAsDate())+","+df.format(h.getEndTimeAsDate())+")");
					Date rec_start = h.getStartTimeAsDate();
					Date prev_end = prev.getEndTimeAsDate();
					if( prev_end.before(rec_start)){
						log.debug("Extend "+name+" "+df.format(prev_end)+"->"+df.format(rec_start));
						prev.setEndTime(rec_start);
						prev.commit();
						
						changes++;
					}
				}
				prev=h;
			
		}
		if( prev != null ){
			prev.commit();
		}
		if( changes > 0 ){
			log.debug(getClass().getCanonicalName()+".closeGaps "+peer.getIdentifier()+" changes="+changes);
		}
	}
	@Override
	protected void postCreateTableSetup(AppContext c, String table) {
		// additional side effects needed.
		setPeerFactory(peer_factory);
	}

}