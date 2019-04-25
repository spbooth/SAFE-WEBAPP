//| Copyright - The University of Edinburgh 2019                            |
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLOrFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterDelete;
import uk.ac.ed.epcc.webapp.model.data.filter.NullFieldFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;

/**
 * @author Stephen Booth
 *
 * @param <A>
 */
public abstract class AbstractRequestFactory<A extends AppUser,R extends AbstractRequestFactory.AbstractRequest>
		extends DataObjectFactory<R> {

	/**
	 * @param user_fac
	 */
	protected AbstractRequestFactory(AppUserFactory<A> user_fac) {
		super();
		this.user_fac = user_fac;
	}

	protected static final String USER_ID = "UserID";
	protected static final String EXPIRES = "Expires";
	static final String TAG="Tag";
	protected final AppUserFactory<A> user_fac;
	
	public static class AbstractRequest<A extends AppUser> extends DataObject{
		protected final AppUserFactory<A> user_fac;
		/**
		 * @param r
		 */
		protected AbstractRequest(AppUserFactory<A> user_fac,Record r) {
			super(r);
			this.user_fac=user_fac;
		}
		public final A getUser(){
			return getUserFactory().find(record.getNumberProperty(USER_ID));
		}
		public final AppUserFactory<A> getUserFactory(){
			return user_fac;
		}
		
		public final boolean expired() {
			if ( record.getRepository().hasField(EXPIRES)) {
				Date d = record.getDateProperty(EXPIRES);
				if( d != null) {
					CurrentTimeService time = getContext().getService(CurrentTimeService.class);
					return d.before(time.getCurrentTime());
				}
			}
			return false;
		}
		public final String getTag(){
			return record.getStringProperty(TAG);
		}
	}


	public final void purge() throws DataFault {
		if( res.hasField(EXPIRES)) {
			FilterDelete<R> del = new FilterDelete<>(res);
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			Date d = time.getCurrentTime();
			SQLOrFilter<R> fil = new SQLOrFilter<>(getTarget());
			fil.addFilter(new SQLValueFilter<>(getTarget(), res, EXPIRES,MatchCondition.LT, d));
			fil.addFilter(new NullFieldFilter<>(getTarget(), res, EXPIRES, true));
			del.delete(fil);
		}
	}
	public R findByTag(String tag) throws DataException{
		return find(new SQLValueFilter<>(getTarget(),res,TAG,tag),true );
	}
	public final String makeTag(int id,String seed) {
		Logger log = getContext().getService(LoggerService.class).getLogger(getClass());
		StringBuilder input = new StringBuilder();
		input.append(seed);
		RandomService serv = getContext().getService(RandomService.class);
		input.append(serv.randomString(64));
		
		log.debug("Input is "+input.toString());
		try {
			// obfuscate the tag
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(input.toString().getBytes());
			StringBuilder output= new StringBuilder();
			output.append(id); // make sure different users can't have a tag clash.
			output.append("-");
			byte tag[]=digest.digest();
			for(int i=0;i<tag.length;i++){
				output.append(Integer.toString(tag[i]-Byte.MIN_VALUE, 36));
			}
			log.debug("output is"+output.toString());
			return output.toString();
		} catch (NoSuchAlgorithmException e) {
			getContext().error(e,"Error making digest");
			return input.toString();
		}
	}

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c, String table) {
		TableSpecification spec = new TableSpecification();
		spec.setField(USER_ID, new IntegerFieldType());
		
		spec.setField(EXPIRES, new DateFieldType(true, null));
		spec.setField(TAG, new StringFieldType(false, "", 256));
		return spec;
	}
	public final AppUserFactory<A> getAppUserFactory() {
		return user_fac;
	}
}