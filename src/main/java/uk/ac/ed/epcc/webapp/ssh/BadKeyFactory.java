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
package uk.ac.ed.epcc.webapp.ssh;

import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.*;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLOrFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.*;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.Index;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Removable;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.NullFieldFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;


/** Simple class to implement a table of forbidden ssh keys
 * 
 * 
 * @author Stephen Booth
 *
 */
public class BadKeyFactory extends DataObjectFactory<BadKeyFactory.BadKey> implements FieldValidator<String>, TableTransitionContributor{
	private final AuthorizedKeyValidator val; 
	public BadKeyFactory(AppContext conn) {
		setContext(conn, "BadKeys");
		val = new AuthorizedKeyValidator(conn);
	}
	@Override
	protected Map<String, Selector> getSelectors() {
		Map<String, Selector> selectors = super.getSelectors();
		selectors.put(PUBLIC_KEY,()->{
			return new SimpleKeyInput(getContext());
		});
		return selectors;
	}

	/**
	 * 
	 */
	private static final String PUBLIC_KEY = "PublicKey";
	private static final String FINGERPRINT = "FingerPrint";


	@Override
	protected BadKey makeBDO(Record res) throws DataFault {
		return new BadKey(res);
	}

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c, String table) {
		TableSpecification spec = new TableSpecification("KeyID");
		//spec.setField("PersonID", c.getService(SessionService.class).getLoginFactory().getReferenceFieldType());
		spec.setField(PUBLIC_KEY, new StringFieldType(true, null, 4096));
		spec.setField(FINGERPRINT,new StringFieldType(true, null, 64));
		try {
			Index i = spec.new Index("key_index",false,FINGERPRINT);
		} catch (InvalidArgument e) {
			getLogger().error("Bad index", e);
		}
		return spec;
	}

	public class BadKey extends DataObject implements Removable{

		
		/**
		 * @param r
		 */
		protected BadKey(Record r) {
			super(r);
		}
		
		public String getKey() {
			return record.getStringProperty(PUBLIC_KEY);
		}
		
		public void setKey(String key) throws ParseException, NoSuchAlgorithmException {
			AuthorizedKeyValidator val = new AuthorizedKeyValidator(getContext());
			key = val.normalise(key);
			record.setProperty(PUBLIC_KEY, key);
			record.setOptionalProperty(FINGERPRINT, val.fingerprint2(key));
		}
		
		public String getFingerprint() {
			String f = record.getStringProperty(FINGERPRINT,null);
			if( f == null ) {
				try {
					f = val.fingerprint2(getKey());
				} catch (Exception e) {
					getLogger().error("Error making fingerprint",e);
				}
			}
			return f;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.Removable#remove()
		 */
		@Override
		public void remove() throws Exception {
			delete();
		}
	}
	
	/** Should the specified key be allowed
	 * 
	 * @param key
	 * @return
	 * @throws DataException 
	 * @throws ParseException 
	 */
	public boolean allow(String key) throws DataException, ParseException {
		if( key ==null) {
			return false;
		}
		
		key = val.normalise(key);
		return ! exists(getKeyFilter(key));
	}
	public SQLFilter<BadKey> getKeyFilter(String key) {
		SQLAndFilter<BadKey> fil = new SQLAndFilter<BadKey>(getTag());
		if( res.hasField(PUBLIC_KEY)) {
			fil.addFilter(new SQLValueFilter<BadKey>(res, PUBLIC_KEY, key));
		}
		if( res.hasField(FINGERPRINT)) {
			try {
				fil.addFilter(
						new SQLOrFilter<BadKey>(getTag(),
								new SQLValueFilter<BadKey>( res, FINGERPRINT, val.fingerprint2(key)),	
								new NullFieldFilter<BadKey>( res, FINGERPRINT, true)
								));
			} catch (Exception e) {
				getLogger().error("Error filter on fingerprint", e);
			}
		}
		
		return fil;
	}

	public void forbid(String key) throws ParseException, DataFault, DataException, NoSuchAlgorithmException, ValidateException {
		if( allow(key)) {
				val.validate(key);
				BadKey b = makeBDO();
				b.setKey(key);
				b.commit();
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.FieldValidator#validate(java.lang.Object)
	 */
	@Override
	public void validate(String data) throws FieldException {
		try {
			if( allow(data)) {
				return;
			}
		} catch (DataException e) {
			getLogger().error("Error in bad key lookup",e);
		}
		throw new ValidateException("Key found in forbidden list");
	}
	
	public void reFingerprint() throws DataFault {
		if( ! res.hasField(FINGERPRINT)) {
			return;
		}
		for(Iterator<BadKey>it = all().iterator(); it.hasNext();) {
			BadKey k = it.next();
			try {
				String key = k.getKey();
				val.validate(key);
				k.setKey(key);
				k.commit();	
			}catch(Exception e) {
				getLogger().error("Error fingerprinting key ", e);
				it.remove();
			}
		}
	}
	public static final TableTransitionKey REFINGERPRINT = new AdminOperationKey( "ReFingerprint", "Regenerate key fingerprints");
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.TableTransitionContributor#getTableTransitions()
	 */
	@Override
	public Map<TableTransitionKey, Transition> getTableTransitions() {
		Map<TableTransitionKey, Transition> result = new LinkedHashMap<TableTransitionKey, Transition>();
		result.put(REFINGERPRINT,new AbstractDirectTransition() {

			@Override
			public FormResult doTransition(Object target, AppContext c) throws TransitionException {
				try {
					reFingerprint();
				} catch (Exception e) {
					getLogger().error("Error regenerating fingerprint",e);
				}
				return new RedirectResult("/main.jsp");
			}
		});
		return result;
	}
}
