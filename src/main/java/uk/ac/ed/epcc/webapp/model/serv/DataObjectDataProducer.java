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
import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.table.BlobType;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.AnonymisingFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterDelete;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamDataWrapper;
import uk.ac.ed.epcc.webapp.session.AppUser;
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
		implements SettableServeDataProducer, AnonymisingFactory {
	/** ID for anonymous access
	 * 
	 */
	private static final int ANONYMOUS_ID = -1;

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

	@Override
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
			FilterDelete<D> del = new FilterDelete<>(res);
			try {
				CurrentTimeService time = getContext().getService(CurrentTimeService.class);
				del.delete(new SQLValueFilter<>(res, EXPIRES_FIELD,MatchCondition.LT, time.getCurrentTime()));
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
			// Sometimes get charset/name arguments in the content-type.
			// sacrifice these if they push us over the allowed field length
			String contentType = data.getContentType();
			FieldInfo info = record.getRepository().getInfo(MIME_TYPE);
			while( contentType.contains(";") && contentType.length() > info.getMax()) {
				contentType = contentType.substring(0, contentType.lastIndexOf(';'));
			}
			record.setProperty(MIME_TYPE, contentType);
			record.setProperty(DATA, data);
			AppContext conn = getContext();
			int live_months = conn.getIntegerParameter("serv_data.lifetime.months", 1);
			if( live_months > 0 ){
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, live_months);
				record.setProperty(EXPIRES_FIELD, cal.getTime());
			}
			// default anonymous access allows public access
			int owner_id = ANONYMOUS_ID;
			SessionService<?> sess = conn.getService(SessionService.class);
			if( sess != null ){
			
				AppUser currentPerson = sess.getCurrentPerson();
				if( currentPerson != null ){
					owner_id = currentPerson.getID();
				}
			}
			record.setProperty(OWNER_ID, owner_id);	
			touch();
		}		
		public MimeStreamData getData() throws DataFault{
		    return new MimeStreamDataWrapper(record.getStreamDataProperty(DATA),getMimeType(),getName());
		}

		public String getMimeType() {
			return record.getStringProperty(MIME_TYPE);
		}

		/**
		 * @return
		 */
		public String getName() {
			return record.getStringProperty(NAME);
		}
		
		public boolean allow(SessionService<?> user){
			int owner_id = record.getIntProperty(OWNER_ID,0);
			if( user == null || ! user.haveCurrentUser() ){
				return owner_id == ANONYMOUS_ID;
			}
			return user.getCurrentPerson().getID() == owner_id;
		}
		
		public void touch(){
			try{
			AppContext conn = getContext();
			int live_months = conn.getIntegerParameter(SERV_DATA_LIFETIME_MONTHS_PROP, 1);
			if( live_months > 0 ){
				Calendar cal = Calendar.getInstance();
				cal.setTime(conn.getService(CurrentTimeService.class).getCurrentTime());
				cal.add(Calendar.MONTH, live_months);
				record.setProperty(EXPIRES_FIELD, cal.getTime());
				SessionService<?> sess = conn.getService(SessionService.class);
				if( sess != null && sess.haveCurrentUser()){
					record.setProperty(OWNER_ID, sess.getCurrentPerson().getID());	
				}else{
					record.setProperty(OWNER_ID, ANONYMOUS_ID);
				}
				commit();
			}
			}catch(Exception e){
				getLogger().error("Error setting expire date",e);
			}
		}
	}
	

	public MimeData getMimeData(SessionService user, List<String> path) throws Exception{
		// Auto clean the table once per user session.
		if( user != null && user.getAttribute(CLEANED_ATTR+getTag()) == null){
			clean();
			user.setAttribute(CLEANED_ATTR+getTag(), "yes");
		}
		MimeData d = find(Integer.parseInt(path.get(0)));
		if( d == null || ! d.allow(user)){
			return null;
		}
		d.touch();
		return d;
	}
	@Override
	public MimeStreamData getData(SessionService user, List<String> path)
			throws Exception {
		MimeData d = getMimeData(user, path);
		if( d == null ){
			return null;
		}
		return d.getData();
	}

	@Override
	public String getDownloadName(SessionService user, List<String> path) throws Exception{
		MimeData d = getMimeData(user, path);
		if( d == null ){
			return null;
		}
		return d.getName();
	}
	

	
	@Override
	public List<String> setData(MimeStreamData data) {
		try {
			MimeData obj = makeBDO();
			obj.setData(data);
			obj.commit();
			LinkedList<String> result = new LinkedList<>();
			result.addFirst(Integer.toString(obj.getID()));
			return result;
		} catch (DataFault e) {
			getContext().error(e,"Error making MimeData");
			return null;
		}
	}



	@Override
	protected D makeBDO(Record res) throws DataFault {
		
		
		MimeData data = new MimeData(res);
		return (D) data;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.AnonymisingFactory#anonymise()
	 */
	@Override
	public void anonymise() throws DataFault {
		FilterDelete<D> del = new FilterDelete<>(res);
		del.delete(null);
		
	}

}