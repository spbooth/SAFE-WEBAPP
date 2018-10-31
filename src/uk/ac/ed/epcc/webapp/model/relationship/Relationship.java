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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.relationship;

import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableContentProvider;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;


/** Link class that encodes a relationship between an AppUser and a
 * domain object.
 * 
 * Possible roles are encoded as boolean fields in the table.
 * Unless overridden in a sub-class. If the table is auto_created the initial set of roles are set in
 * <b>relationship.<em>table</em>.create_roles</b> as a comma separated list.
 * The property <b>reference.<em>table</em>.TargetID</b> must be set to the config tag
 * of the domain object.
 * 
 * 
 * @author spb
 *
 * @param <A>
 * @param <B>
 */


public class Relationship<A extends AppUser,B extends DataObject> extends 
         AbstractRelationship<A,B,Relationship.Link<A,B>> implements TableContentProvider{
    
	// This is the default field sub-classes may use a different value
	private static final String TARGET_ID = "TargetID";
	private static final String PERSON_ID = "PersonID";

	@SuppressWarnings("unchecked")
	public Relationship(AppContext c,String tag){
    	this(c,tag,c.makeObject(DataObjectFactory.class, c.getInitParameter("reference."+tag+"."+TARGET_ID)),TARGET_ID);
    }
	
	/** Extension constructor to allow sub-classes to set factory and field
	 * 
	 */
	
	protected Relationship(AppContext c,String tag, DataObjectFactory<B> b_fac, String field) {
		super(c,tag,PERSON_ID,b_fac,field);
	}
	/** Constructor to allow sub-classes to set factory
	 * 
	 * @param c
	 * @param tag
	 * @param target_fac
	 */
	
	protected Relationship(AppContext c, String tag,DataObjectFactory<B> target_fac){
		this(c,tag,target_fac,TARGET_ID);
	}
    
    @Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,String table,
			IndexedProducer<A> leftFac, String leftField,
			IndexedProducer<B> rightFac, String rightField) {
		
		TableSpecification spec = super.getDefaultTableSpecification(c, table,leftFac, leftField, rightFac,
						rightField);


		for(String r : getDefaultRoles(c, table)){
			spec.setField(r, new BooleanFieldType(false,false));
		}

		return spec;
	}
    /** generate a list of the default roles to create when bootstrapping a table.
     * 
     * 
     * @param c
     * @param table
     * @return
     */
    protected String[] getDefaultRoles(AppContext c, String table){
    	String roles = c.getInitParameter("relationship."+table+".create_roles");
    	if( roles == null || roles.trim().length() == 0 ){
    		return new String[0];
    	}
    	return roles.trim().split("\\s*,\\s*");
    }

	public static class Link<A extends AppUser,B extends DataObject> extends AbstractRelationship.Link<A,B>{

		protected Link(Relationship< A, B> arg0, Record arg1) {
			super(arg0, arg1);
		}

		@Override
		protected void setup() throws DataFault, DataException {
		}
		@Override
		public boolean hasRole(String role){
			return record.getBooleanProperty(role, false);
		}
		@Override
		public void setRole(String role, boolean value){
			record.setProperty(role, value);
		}
    	
    }

	@Override
	protected Link<A,B> makeBDO(Record res) throws DataFault {
		return new Link<>(this,res);
	}


	/**
	 * @param role
	 * @return
	 * @throws UnknownRelationshipException 
	 */
	@Override
	protected SQLFilter<Link<A, B>> getFilterFromRole(String role) throws UnknownRelationshipException {
		if( ! res.hasField(role)){
			throw new UnknownRelationshipException(role);
		}
		return new SQLValueFilter<>(getTarget(),res,role,Boolean.TRUE);
	}
		
	
	
	public static void makeTable(AppContext conn,
			String my_table, String peer_table) throws DataFault {
		assert(peer_table != null);
		assert(my_table != null);
		TableSpecification s = new TableSpecification();
		s.setField(PERSON_ID, new IntegerFieldType()); // int ok as factory got from session service
		s.setField(TARGET_ID, new ReferenceFieldType(peer_table));
		try {
			s.new Index("Link", true, PERSON_ID, TARGET_ID);
		} catch (InvalidArgument e) {
			conn.error(e,"Error making index");
			return;
		}
		// best we can do
		DataBaseHandlerService handler = conn.getService(DataBaseHandlerService.class);
		if( handler != null ){
			handler.createTable(my_table, s);
		}
		
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.safe.accounting.db.RelationshipProvider#getRoles()
	 */
	@Override
	public Set<String> getRelationships(){
		Set<String> result = new HashSet<>();
		for( String s: res.getFields()){
			if( res.getInfo(s).isBoolean()){
				result.add(s);
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.TableContentProvider#addSummaryContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
	 */
	@Override
	public void addSummaryContent(ContentBuilder cb) {
		cb.addHeading(3,"Roles");
		cb.addList(getRelationships());
	}
}