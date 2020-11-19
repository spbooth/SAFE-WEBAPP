//| Copyright - The University of Edinburgh 2020                            |
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
package uk.ac.ed.epcc.webapp.model.cron;

import uk.ac.ed.epcc.webapp.model.ClassificationFactory;
import uk.ac.ed.epcc.webapp.model.data.FieldValuePatternArgument;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Retirable;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.TransientDataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterUpdate;
import uk.ac.ed.epcc.webapp.model.data.filter.NullFieldFilter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.content.DateTransform;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.content.Transform;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.Classification;

/**
 * @author Stephen Booth
 *
 */
public class LockFactory extends ClassificationFactory<LockFactory.Lock> {
	
	/**
	 * 
	 */
	private static final String LOCKS_TABLE = "Locks";

	
	
	
	/**
	 * 
	 */
	private static final String LOCK_FIELD = "Lock";
	
	private static final String LAST_LOCK_FIELD = "LastLock";

	public static LockFactory getFactory(AppContext conn) {
		return conn.makeObject(LockFactory.class, LOCKS_TABLE);
	}
	public LockFactory(AppContext conn) {
		super();
		setContext(conn, LOCKS_TABLE);
	}
	public class Lock extends Classification implements Retirable{
		private boolean holding=false;
		/**
		 * @param res
		 * @param fac
		 */
		protected Lock(Record res) {
			super(res, LockFactory.this);
		}
		/** is the lock taken
		 * 
		 */
		public boolean isLocked() {
			return wasLockedAt() != null;
		}

		/** when was the lock taken.
		 * returns null if lock not taken
		 * 
		 * @return {@link Date}
		 */
		public Date wasLockedAt() {
			return record.getDateProperty(LOCK_FIELD);
		}
		/** when was the lock last taken (may not be locked now)
		 * 
		 * @return
		 */
		public Date lastLocked() {
			return record.getDateProperty(LAST_LOCK_FIELD);
		}
		/** Is THIS instance holding the lock
		 * 
		 * @return
		 */
		public boolean isHolding() {
			return holding;
		}
		/** attempt to take the lock
		 * 
		 * @return true if lock taken
		 * @throws DataFault
		 */
		public boolean takeLock() throws DataFault {
			if( holding) {
				throw new ConsistencyError("Already locked "+getName());
			}
			if( isLocked()) {
				// locked by other request already
				return false;
			}
			DatabaseService db = getContext().getService(DatabaseService.class);
			
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			Date now = time.getCurrentTime();
			Repository res = record.getRepository();
			FilterUpdate<Lock> update = new FilterUpdate<LockFactory.Lock>(res);
			SQLAndFilter<Lock> fil = new SQLAndFilter<Lock>(Lock.class,getFilter(this),new NullFieldFilter<Lock>(Lock.class, res, LOCK_FIELD, true));
			try {
				int i = update.update(fil, 
						new FieldValuePatternArgument<Date,Lock>(res.getDateExpression(Lock.class, LOCK_FIELD), now),
						new FieldValuePatternArgument<Date,Lock>(res.getDateExpression(Lock.class, LAST_LOCK_FIELD), now)
						);
				if( i != 1 ) {
					// other thread got there first
					return false;
				}
				record.setProperty(LOCK_FIELD, now);
				setDirty(LOCK_FIELD, false);

				record.setProperty(LAST_LOCK_FIELD, now);
				setDirty(LAST_LOCK_FIELD, false);

				holding=true;
				
				return true;
			}catch(TransientDataFault e) {
				// transient error treat as failed to get lock
				return false;
			}
		}
		/** release the lock
		 * 
		 * @throws DataFault
		 */
		public void releaseLock() throws DataFault {
			if( ! holding ) {
				throw new ConsistencyError("Not holding lock "+getName());
			}
			// loop  to retry transient failures
			for(int t=0 ; t < 100 ; t++) {
				try {
					// save any other changes
					commit();
					Repository res = record.getRepository();
					FilterUpdate<Lock> update = new FilterUpdate<LockFactory.Lock>(res);
					SQLAndFilter<Lock> fil = new SQLAndFilter<Lock>(Lock.class,getFilter(this),new NullFieldFilter<Lock>(Lock.class, res, LOCK_FIELD, false));
					int i = update.update(res.getDateExpression(Lock.class, LOCK_FIELD), null, fil);
					if( i != 1 ) {
						// How did this happen
						throw new ConsistencyError("Failed to remove lock "+getName());
					}
					record.setProperty(LOCK_FIELD, null);
					setDirty(LOCK_FIELD, false);
					holding=false;
					return ;

				}catch(TransientDataFault e) {
					getLogger().warn("Re-try unlock "+getName(), e);
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e1) {
						
					}
				}
			}
			getLogger().error("Failed to unlock "+getName());
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.Retirable#canRetire()
		 */
		@Override
		public boolean canRetire() {
			return true;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.Retirable#retire()
		 */
		@Override
		public void retire() throws Exception {
			delete();
		}
	}

	@Override
	public TableSpecification getDefaultTableSpecification(AppContext c, String homeTable) {
		TableSpecification spec = super.getDefaultTableSpecification(c, homeTable);
		spec.setField(LOCK_FIELD, new DateFieldType(true, null));
		spec.setField(LAST_LOCK_FIELD, new DateFieldType(true, null));
		return spec;
	}

	@Override
	protected Set<String> getSupress() {
		Set<String> supress = super.getSupress();
		supress.add(LOCK_FIELD);
		return supress;
	}

	@Override
	protected Lock makeBDO(Record res) throws DataFault {
		return new Lock(res);
	}

	@Override
	public Class<Lock> getTarget() {
		return Lock.class;
	}
	public Table getTable() {
		Table t = new Table();
		try {
			for(Lock l : all()) {
				t.put("Name", l, l.getName());
				t.put("Description", l, l.getDescription());
				t.put("Locked since", l, l.wasLockedAt());
				t.put("Last locked", l, l.lastLocked());
			}
		} catch (DataFault e) {
			getLogger().error("Error building lock table", e);
		}
		Transform f = new DateTransform(SimpleDateFormat.getDateTimeInstance());
		t.setColFormat("Locked since", f);
		t.setColFormat("Last locked", f);
		return t;
	}

}
