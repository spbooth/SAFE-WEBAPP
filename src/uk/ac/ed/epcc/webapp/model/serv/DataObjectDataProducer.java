//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.model.serv;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.table.BlobType;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterDelete;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamDataWrapper;
import uk.ac.ed.epcc.webapp.session.SessionDataProducer;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** A {@link SettableServeDataProducer} implemented as a database table.
 * It is intended to be a straightforward replacement for {@link SessionDataProducer}
 * trading off database space and overhead for memory in the user session.
 * Note that if sessions are being persisted to the database then the
 * {@link SessionDataProducer} will also consume database space though this will automatically be reclaimed 
 * at the end of the session.
 * 
 * It can also be extended to provide more sophisticated access control
 * Each item of data belongs to a single user and expires after a specified date
 * @see DataProducerHeartbeatListener
 * @author spb
 *
 * @param <D>
 */
public class DataObjectDataProducer<D extends DataObjectDataProducer.MimeData> extends DataObjectFactory<D>
		implements SettableServeDataProducer {
	private static final String CLEANED_ATTR = "CLEANED_ATTR";
	private static final String DATA = "Data";
	private static final String MIME_TYPE = "MimeType";
	private static final String NAME = "Name";
	private static final String EXPIRES_FIELD = "Expires";
	private static final String OWNER_ID ="PersonId";
	/**
	 * 
	 */
	private static final String SERV_DATA_LIFETIME_MONTHS_PROP = "serv_data.lifetime.months";

	public DataObjectDataProducer(AppContext c, String tag) {
		super();
		setContext(c,tag);
		
		
	}

	public TableSpecification getDefaultTableSpecification(AppContext c, String tag){
		TableSpecification spec = new TableSpecification("ServeDataID");
		spec.setField(MIME_TYPE, new StringFieldType(true, null, 127));
		spec.setField(NAME, new StringFieldType(true, null, 255));
		spec.setField(DATA, new BlobType());
		spec.setField(EXPIRES_FIELD, new DateFieldType(true, null));
		spec.setField(OWNER_ID, new ReferenceFieldType(c.getService(SessionService.class).getLoginFactory().getTag()));
		return spec;
	}

	public void clean(){
		if( res.hasField(EXPIRES_FIELD)){
			FilterDelete<D> del = new FilterDelete<D>(res);
			try {
				del.delete(new SQLValueFilter<D>(getTarget(),res, EXPIRES_FIELD,MatchCondition.LT, new Date()));
			} catch (DataFault e) {
				getContext().error(e,"Error deleting old data");
			}
		}
	}
	public static class MimeData extends DataObject {

	
		protected MimeData(Record r) {
			super(r);
		}

		public void setData(MimeStreamData data){
			record.setProperty(NAME, data.getName());
			record.setProperty(MIME_TYPE, data.getContentType());
			record.setProperty(DATA, data);
			touch();
		}		
		public MimeStreamData getData() throws DataFault{
		    return new MimeStreamDataWrapper(record.getStreamDataProperty(DATA),record.getStringProperty(MIME_TYPE),record.getStringProperty(NAME));
		}
		
		public boolean allow(SessionService<?> user){
			return user.getCurrentPerson().getID() == record.getIntProperty(OWNER_ID,0);
		}
		
		public void touch(){
			try{
			AppContext conn = getContext();
			int live_months = conn.getIntegerParameter(SERV_DATA_LIFETIME_MONTHS_PROP, 1);
			if( live_months > 0 ){
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, live_months);
				record.setProperty(EXPIRES_FIELD, cal.getTime());
				SessionService<?> sess = conn.getService(SessionService.class);
				record.setProperty(OWNER_ID, sess.getCurrentPerson().getID());	
				commit();
			}
			}catch(Exception e){
				getLogger().error("Error setting expire date",e);
			}
		}
	}
	

	
	public MimeStreamData getData(SessionService user, List<String> path)
			throws Exception {
		// Auto clean the table once per user session.
		if( user.getAttribute(CLEANED_ATTR) == null){
			clean();
			user.setAttribute(CLEANED_ATTR, "yes");
		}
		MimeData d = find(Integer.parseInt(path.get(0)));
		if( d == null || ! d.allow(user)){
			return null;
		}
		d.touch();
		return d.getData();
	}

	

	
	public List<String> setData(MimeStreamData data) {
		try {
			MimeData obj = makeBDO();
			obj.setData(data);
			obj.commit();
			LinkedList<String> result = new LinkedList<String>();
			result.addFirst(Integer.toString(obj.getID()));
			return result;
		} catch (DataFault e) {
			getContext().error(e,"Error making MimeData");
			return null;
		}
	}



	@Override
	protected MimeData makeBDO(Record res) throws DataFault {
		
		AppContext conn = getContext();
		int live_months = conn.getIntegerParameter("serv_data.lifetime.months", 1);
		if( live_months > 0 ){
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, live_months);
			res.setProperty(EXPIRES_FIELD, cal.getTime());
		}
		SessionService<?> sess = conn.getService(SessionService.class);
		res.setProperty(OWNER_ID, sess.getCurrentPerson().getID());	
		MimeData data = new MimeData(res);
		return data;
	}

}