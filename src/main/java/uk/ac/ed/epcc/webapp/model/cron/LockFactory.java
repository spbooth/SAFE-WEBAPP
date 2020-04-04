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
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Retirable;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterUpdate;
import uk.ac.ed.epcc.webapp.model.data.filter.NullFieldFilter;

import java.util.Date;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
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
		
		public boolean isLocked() {
			return wasLockedAt() != null;
		}

		public Date wasLockedAt() {
			return record.getDateProperty(LOCK_FIELD);
		}
		public Date lastLocked() {
			return record.getDateProperty(LAST_LOCK_FIELD);
		}
		public boolean isHolding() {
			return holding;
		}
		public boolean takeLock() throws DataFault {
			if( holding) {
				throw new ConsistencyError("Alreay locked "+getName());
			}
			if( isLocked()) {
				// locked by other request alerady
				return false;
			}
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			Date now = time.getCurrentTime();
			Repository res = record.getRepository();
			FilterUpdate<Lock> update = new FilterUpdate<LockFactory.Lock>(res);
			SQLAndFilter<Lock> fil = new SQLAndFilter<Lock>(Lock.class,getFilter(this),new NullFieldFilter<Lock>(Lock.class, res, LOCK_FIELD, true));
			int i = update.update(res.getDateExpression(Lock.class, LOCK_FIELD), now, fil);
			if( i != 1 ) {
				// other thread got there first
				return false;
			}
			record.setProperty(LOCK_FIELD, now);
			setDirty(LOCK_FIELD, false);
			holding=true;
			
			record.setProperty(LAST_LOCK_FIELD, now);
			commit();
			return true;
		}
		
		public void releaseLock() throws DataFault {
			if( ! holding ) {
				throw new ConsistencyError("Not holding lock "+getName());
			}
			// save any other changes
			commit();
			Repository res = record.getRepository();
			FilterUpdate<Lock> update = new FilterUpdate<LockFactory.Lock>(res);
			SQLAndFilter<Lock> fil = new SQLAndFilter<Lock>(Lock.class,getFilter(this),new NullFieldFilter<Lock>(Lock.class, res, LOCK_FIELD, false));
			int i = update.update(res.getDateExpression(Lock.class, LOCK_FIELD), null, fil);
			if( i != 1 ) {
				// other thread got there first
				throw new ConsistencyError("Failed to remove lock");
			}
			record.setProperty(LOCK_FIELD, null);
			setDirty(LOCK_FIELD, false);
			holding=false;
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

}
