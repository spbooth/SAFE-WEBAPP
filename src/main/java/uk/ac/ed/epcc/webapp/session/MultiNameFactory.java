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

import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.GenericBinaryFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.NameFinder;
import uk.ac.ed.epcc.webapp.model.data.*;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;

/** A factory for multi value {@link AppUser} names
 * @author Stephen Booth
 *
 */
public class MultiNameFactory<N extends MultiNameFactory.Name, AU extends AppUser> extends DataObjectFactory<N> implements NameFinder<N> {

	/**
	 * 
	 */
	private static final String PERSON = "Person";
	/**
	 * 
	 */
	private static final String NAME = "Name";

	public MultiNameFactory(AppContext conn, String table) {
		setContext(conn, table);
	}
	
	public class Name extends DataObject{

		/**
		 * @param r
		 */
		protected Name(Record r) {
			super(r);
		}
		
		public String getName() {
			return record.getStringProperty(NAME);
		}
		public void setName(String name) {
			record.setProperty(NAME, name);
		}
		
		public void setPerson(AU person) {
			record.setProperty(PERSON, person.getID());
		}
		public AU getPerson() {
			return (AU) getContext().getService(SessionService.class).getLoginFactory().find(record.getNumberProperty(PERSON));
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected N makeBDO(Record res) throws DataFault {
		return (N) new  Name(res);
	}

	

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c, String table) {
		TableSpecification spec = new TableSpecification();
		spec.setField(NAME, new StringFieldType(false, null, c.getIntegerParameter(table+".max_name", 128)));
		spec.setField(PERSON, c.getService(SessionService.class).getLoginFactory().getReferenceFieldType());
		try {
			spec.new Index("name_index", true, NAME);
		} catch (InvalidArgument e) {
			c.getService(LoggerService.class).getLogger(getClass()).error("Error making index",e);
		}
		return spec;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ParseFactory#getCanonicalName(java.lang.Object)
	 */
	@Override
	public String getCanonicalName(N object) {
		return object.getName();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#validateNameFormat(java.lang.String)
	 */
	@Override
	public void validateNameFormat(String name) throws ParseException {
		
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#findFromString(java.lang.String)
	 */
	@Override
	public N findFromString(String name) {
		try {
			return find(getStringFinderFilter(name),true);
		} catch (DataException e) {
			getLogger().error("Error lookin up nanme", e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#makeFromString(java.lang.String)
	 */
	@Override
	public N makeFromString(String name) throws DataFault, ParseException {
		N result = findFromString(name);
		if( result != null) {
			return result;
		}
		result = makeBDO();
		result.setName(name);
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#getStringFinderFilter(java.lang.String)
	 */
	@Override
	public SQLFilter<N> getStringFinderFilter(String name) {
		
		return new SQLValueFilter<N>(res, NAME, name);
	}
    @Override
    public GenericBinaryFilter<N> hasCanonicalNameFilter(){
    	// all entries have names
    	return new GenericBinaryFilter<N>( true);
    }
	
	public SQLFilter<AU> getPersonFilter(String name){
		return getDestFilter(getStringFinderFilter(name), PERSON, getContext().getService(SessionService.class).getLoginFactory());
	}
	
	public SQLFilter<AU> hasNameFilter(){
		return getDestFilter(hasCanonicalNameFilter(), PERSON, getContext().getService(SessionService.class).getLoginFactory());
	}
	
	public Set<String> getNames(AU person){
		Set<String> names = new LinkedHashSet<>();
		try(FilterResult<N> r = getResult(new ReferenceFilter<N, AU>(this, PERSON, person))) {
			for(N name : r ) {
				names.add(name.getName());
			}
		} catch (DataFault e) {
			getLogger().error("Error getting names", e);
		}
		return names;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#getDataCache()
	 */
	@Override
	public DataCache<String, N> getDataCache(boolean auto_create) {
		return new DataCache<String, N>() {

			@Override
			protected N find(String key) throws DataException {
				if( auto_create) {
					try {
						return makeFromString(key);
					} catch ( ParseException e) {
						throw new DataFault("Bad name",e);
					}
				}
				return findFromString(key);
			}
		};
	}
}
