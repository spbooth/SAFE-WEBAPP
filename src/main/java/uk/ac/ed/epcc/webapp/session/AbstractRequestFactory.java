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
package uk.ac.ed.epcc.webapp.session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLOrFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
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

/** Abstract class for building request links (with expiry time).
 * 
 * If the {@link AppUser} the link is for is known in advance use {@link AbstractUserRequestFactory} 
 * 
 * @author Stephen Booth
 *
 * @param <R> type of request object.
 */
public abstract class AbstractRequestFactory<R extends AbstractRequestFactory.AbstractRequest>
		extends DataObjectFactory<R> {

	protected static final String EXPIRES = "Expires";
	protected static final String TAG = "Tag";

	public static class AbstractRequest extends DataObject{
			
			/**
			 * @param r
			 */
			protected AbstractRequest(Record r) {
				super(r);
			}
			
			public final boolean expired() {
				if ( record.getRepository().hasField(EXPIRES)) {
					Date d = expires();
					if( d != null) {
						CurrentTimeService time = getContext().getService(CurrentTimeService.class);
						return d.before(time.getCurrentTime());
					}
				}
				return false;
			}

			public Date expires() {
				return record.getDateProperty(EXPIRES);
			}
			
			public final String getTag(){
				return record.getStringProperty(TAG);
			}
		}

	/**
	 * 
	 */
	public AbstractRequestFactory() {
		super();
	}

	/** remove any time expired requests
	 * 
	 * @throws DataFault
	 */
	public final void purge() throws DataFault {
		if( res.hasField(EXPIRES)) {
			
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			Date d = time.getCurrentTime();
			SQLOrFilter<R> fil = new SQLOrFilter<>(getTag());
			fil.addFilter(new SQLValueFilter<>( res, EXPIRES,MatchCondition.LT, d));
			fil.addFilter(new NullFieldFilter<>( res, EXPIRES, true));
			purge(fil);
		}
	}

	/** remove expired reqeusts based on a {@link SQLFilter}
	*/
	protected void purge(SQLFilter<R> fil) throws DataFault {
		FilterDelete<R> del = new FilterDelete<>(res);
		del.delete(fil);
	}

	/** locate a request object by tag.
	 * 
	 * @param tag
	 * @return
	 * @throws DataException
	 */
	public R findByTag(String tag) throws DataException {
		return find(new SQLValueFilter<>(res,TAG,tag),true );
	}

	/** Generate a new tag used to create the request url.
	 * This always contains a random value and an explicit integer id.
	 * 
	 * The random value is generated from a hash of both a random number generator and a Seed value.
	 * The seed value should come from the context of the request. It is intended to mitigate against attacks against the random number generator.
	 * Both by making it harder to determine random number state by  generating URLs and to guess urls (without also guessing/knowing the seed value).
	 * 
	 * The id value specifies an object the request is in respect to. For example a user account being changed. This is to reduce the
	 * possibility of a tag clash. 
	 * @param id - Integer id of object owning the request.
	 * @param seed - a String based on the context of the request. 
	 * @return
	 */
	public final String makeTag(int id, String seed) {
		Logger log = getContext().getService(LoggerService.class).getLogger(getClass());
		StringBuilder input = new StringBuilder();
		input.append(seed);
		RandomService serv = getContext().getService(RandomService.class);
		input.append(serv.randomString(64));
		
		log.debug("Input is "+input.toString());
		try {
			// obfuscate the tag
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
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
		
		spec.setField(EXPIRES, new DateFieldType(true, null));
		spec.setField(TAG, new StringFieldType(false, "", 256));
		return spec;
	}

}