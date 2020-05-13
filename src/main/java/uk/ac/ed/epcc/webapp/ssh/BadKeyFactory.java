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

import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.Index;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.IndexField;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;


/** Simple class to implement a table of forbidden ssh keys
 * 
 * 
 * @author Stephen Booth
 *
 */
public class BadKeyFactory extends DataObjectFactory<BadKeyFactory.BadKey> implements FieldValidator<String>{
	
	public BadKeyFactory(AppContext conn) {
		setContext(conn, "BadKeys");
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

	@Override
	public Class<BadKey> getTarget() {
		return BadKey.class;
	}

	@Override
	protected BadKey makeBDO(Record res) throws DataFault {
		return new BadKey(res);
	}

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c, String table) {
		TableSpecification spec = new TableSpecification("KeyID");
		//spec.setField("PersonID", c.getService(SessionService.class).getLoginFactory().getReferenceFieldType());
		spec.setField(PUBLIC_KEY, new StringFieldType(true, null, 4096));
		try {
			Index i = spec.new Index("key_index",true);
			i.addField(new IndexField(PUBLIC_KEY, 64));
		} catch (InvalidArgument e) {
			getLogger().error("Bad index", e);
		}
		return spec;
	}

	public class BadKey extends DataObject{

		
		/**
		 * @param r
		 */
		protected BadKey(Record r) {
			super(r);
		}
		
		public String getKey() {
			return record.getStringProperty(PUBLIC_KEY);
		}
		
		public void setKey(String key) throws ParseException {
			AuthorizedKeyValidator val = new AuthorizedKeyValidator();
			record.setProperty(PUBLIC_KEY, val.normalise(key));
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
		AuthorizedKeyValidator val = new AuthorizedKeyValidator();
		key = val.normalise(key);
		return ! exists(new SQLValueFilter<BadKeyFactory.BadKey>(getTarget(), res, PUBLIC_KEY, key));
	}

	public void forbid(String key) throws ParseException, DataFault, DataException {
		if( allow(key)) {
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
}
