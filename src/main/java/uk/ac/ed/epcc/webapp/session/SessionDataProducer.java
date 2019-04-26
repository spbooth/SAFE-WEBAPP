//| Copyright - The University of Edinburgh 2011                            |
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
package uk.ac.ed.epcc.webapp.session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.Reference;
import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.model.serv.SettableServeDataProducer;
/** A serveDataProducer that stores the data in the Session
 * This is stores as a serialised byte array to avoid any possibility of a class-loader leak.
 * 
 * @author spb
 *
 */


public class SessionDataProducer implements SettableServeDataProducer {
	
	private static final String _DATA = "_DATA_";
	private static final String _COUNTER = "_COUNTER";
	private static final Feature SOFT_REFERENCE_FEATURE = new Feature("SessionDataProducer.use_reference",true,"Use a soft reference to reduce the risk of memory exhausion");
	private final AppContext conn;
	private final SessionService session_service;
	private final String storage_tag;
	public SessionDataProducer(AppContext conn,String tag){
		this.conn=conn;
		session_service=conn.getService(SessionService.class);
		if(session_service == null ){
			throw new ConsistencyError("No session service");
		}
		this.storage_tag=tag;
	}
	private int getNext(){
		if(session_service == null ){
			throw new ConsistencyError("No session service");
		}
		String counter_attr = getTag()+_COUNTER;
		Integer counter = (Integer) session_service.getAttribute(counter_attr);
		if( counter == null ){
			counter = new Integer(1); // start at 1 so DB and session behave similarly in tests 
		}
		int id = counter.intValue();
		session_service.setAttribute(counter_attr, Integer.valueOf(id+1));
		return id;
	}
	
	public MimeStreamData find(int id) throws DataException {
		if(session_service == null ){
			throw new ConsistencyError("No session service");
		}
		Object attribute = session_service.getAttribute(getTag()+_DATA+id);
		if( attribute == null ){
			return null;
		}
		if( attribute instanceof Reference){
			attribute = ((Reference)attribute).get();
		}
		if(attribute instanceof SerialisableSoftReference){
			attribute= ((SerialisableSoftReference)attribute).getData();
		}
		byte data[] = (byte[]) attribute;
		if( data == null || data.length == 0){
			return null;
		}
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			return (ByteArrayMimeStreamData) ois.readObject();
		} catch (Exception e) {
			throw new DataFault("Error deserialising data",e);
		}
		
	}

	

	
	

	public String getTag() {
		return storage_tag;
	}

	
	

	public AppContext getContext() {
		return conn;
	}
	public MimeStreamData getData(SessionService user, List<String> path) throws Exception {
		return find(Integer.parseInt(path.get(0)));
	}
	public List<String> setData(MimeStreamData data) {
		int next = getNext();
		ByteArrayMimeStreamData msd;
		try {
			msd = new ByteArrayMimeStreamData(data);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(stream);
			oos.writeObject(msd);
			oos.close();
			Object attribute = stream.toByteArray();
			if( SOFT_REFERENCE_FEATURE.isEnabled(conn)){
				int max_len = conn.getIntegerParameter("SessionDataProducer.max_serialise",0);
				SerialisableSoftReference<byte[]> ref = new SerialisableSoftReference<>((byte[])attribute);
				if( max_len > 0 && data.getLength() > max_len){
					ref.setForceNullOnSerialise(true);
				}
				attribute = ref;
			}
			session_service.setAttribute(getTag()+_DATA+next, attribute);
			LinkedList<String> res = new LinkedList<>();
			res.addFirst(Integer.toString(next));
			return res;
		} catch (Exception e) {
			conn.getService(LoggerService.class).getLogger(getClass()).error("Error serialising data",e);
			return null;
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer#getDownloadName(uk.ac.ed.epcc.webapp.session.SessionService, java.util.List)
	 */
	@Override
	public String getDownloadName(SessionService user, List<String> path) throws Exception {
		MimeStreamData data = getData(user, path);
		if( data == null ){
			return null;
		}
		return data.getName();
	}
	

}