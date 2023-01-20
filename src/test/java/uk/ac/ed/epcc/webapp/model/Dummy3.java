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
/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model;

import java.util.Iterator;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataCache;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.FilterResult;
import uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.NullFieldFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** class instantiating the standard test table.
 * 
 * @author spb
 *
 */
public class Dummy3 extends DataObject {
	public static final String NAME = "Name";
	public static final String MANDATORY ="Mandatory";
	
	public static final String PERSON_ID = "PersonID";
	public static final String DEFAULT_TABLE = "Test3";
	

	public Dummy3(Repository.Record res) {
		super(res);
	}

	public Dummy3(AppContext ctx) {
		super(getRecord(ctx,DEFAULT_TABLE));
	}

	public String getName(){
		return record.getStringProperty(NAME);
	}
	
	public void setName(String n){
		record.setProperty(NAME, n);
	}
	
	public void setPerson(AppUser person){
		record.setProperty(PERSON_ID, person.getID());
	}
	public AppUser getPerson(){
		return (AppUser) getContext().getService(SessionService.class).getLoginFactory().find(record.getNumberProperty(PERSON_ID));
	}
    public static class Factory extends DataObjectFactory<Dummy3> implements AccessRoleProvider<AppUser, Dummy3>, NamedFilterProvider<Dummy3>, NameFinder<Dummy3>{
    	 /**
		 * 
		 */
		private static final String SELF = "self";
		/**
		 * 
		 */
		
		
         public class StringFilter extends SQLValueFilter<Dummy3>{
         	public StringFilter(String s){
         		super(res,NAME,s);
         	}
         }
		public Factory(AppContext c) {
			setContext(c,DEFAULT_TABLE);
		}
        public long count(SQLFilter<Dummy3> f) throws DataException{
        	return getCount(f);
        }
		@Override
		protected Dummy3 makeBDO(Repository.Record res) throws DataFault {
			return new Dummy3(res);
		}
    	public void nuke() throws DataFault{
    		for(Iterator it = getAllIterator(); it.hasNext();){
    			DataObject o = (DataObject) it.next();
    			o.delete();
    		}
    	}
		
	
		
		public FilterResult<Dummy3> getWithFilter() throws DataFault{
			AndFilter<Dummy3>fil = new AndFilter<>(getTag());
			return getResult(fil);
			
		}
		@Override
		protected TableSpecification getDefaultTableSpecification(AppContext c,
				String table) {
			TableSpecification spec = new TableSpecification();
			spec.setField(NAME, new StringFieldType(true, "", 32));
			
			spec.setField(MANDATORY, new StringFieldType(false, "Junk", 32));
		    spec.setField(PERSON_ID, new ReferenceFieldType( c.getService(SessionService.class).getLoginFactory().getTag()));
			
			return spec;
		}
		public Set<String> getNullFields(){
			// expose for testing
			return getNullable();
		}
		public boolean fieldExists(String name){
			return res.hasField(name);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider#hasRelationFilter(uk.ac.ed.epcc.webapp.session.SessionService, java.lang.String)
		 */
		@Override
		public BaseFilter<Dummy3> hasRelationFilter( String role,AppUser user) {
			if( role.equals(SELF)){
				return new SQLValueFilter<>( res, PERSON_ID, user.getID());
			}
			return null;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider#personInRelationFilter(uk.ac.ed.epcc.webapp.session.SessionService, java.lang.String, uk.ac.ed.epcc.webapp.model.data.DataObject)
		 */
		@Override
		public BaseFilter<AppUser> personInRelationFilter(SessionService<AppUser> sess, String role, Dummy3 target) {
			if( role.equals(SELF)){
				return sess.getLoginFactory().getFilter(target.getPerson());
			}
			return null;
		}
		
		@Override
		public SQLFilter<AppUser> personInRelationToFilter(SessionService<AppUser> sess, String role, SQLFilter<Dummy3> fil) {
			if( role.equals(SELF)){
				return getDestFilter(fil,PERSON_ID,sess.getLoginFactory());
			}
			return null;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider#getNamedFilter(java.lang.String)
		 */
		@Override
		public BaseFilter<Dummy3> getNamedFilter(String name) {
			if( name.equals("CalledTest1")){
				return new SQLValueFilter<>(res, NAME, "Test1");
			}
			return null;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider#addFilterNames(java.util.Set)
		 */
		@Override
		public void addFilterNames(Set<String> names) {
			names.add("CalledTest1");
			
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider#providesRelationship(java.lang.String)
		 */
		@Override
		public boolean providesRelationship(String role) {
			return role.equals(SELF);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.ParseFactory#getCanonicalName(java.lang.Object)
		 */
		@Override
		public String getCanonicalName(Dummy3 object) {
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
		public Dummy3 findFromString(String name) {

			try {
				return find(getStringFinderFilter(name),true);
			} catch (DataException e) {
				getLogger().error("Error in fingFromString", e);
				return null;
			}
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.NameFinder#makeFromString(java.lang.String)
		 */
		@Override
		public Dummy3 makeFromString(String name) throws DataFault, ParseException {
			
			return null;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.NameFinder#getStringFinderFilter(java.lang.String)
		 */
		@Override
		public SQLFilter<Dummy3> getStringFinderFilter(String name) {
			return new StringFilter(name);
		}
		@Override
		public SQLFilter<Dummy3> hasCanonicalNameFilter(){
			return new NullFieldFilter<Dummy3>(res, NAME, false);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.NameFinder#getDataCache()
		 */
		@Override
		public DataCache<String, Dummy3> getDataCache(boolean auto_create) {
			return new DataCache<String, Dummy3>() {
				
				@Override
				protected Dummy3 find(String key) throws DataException {
					if( auto_create ) {
						try {
							return makeFromString(key);
						} catch (ParseException e) {
							throw new DataFault("Bad name", e);
						}
					}
					return findFromString(key);
				}
			};
		}
		
		@Override
		public void addRelationships(Set<String> roles) {
			roles.add(SELF);
			
		}
    }
}