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
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;

/** An attribute type extends an object by compositions but with the data held in an additional table.
 * Each "Owner" has at most one {@link AttributeObject} from each table.
 * 
 * This is an abstract class. As the {@link AttributeObject} contains a reference to its Factory class the ultimate class can be made in inner class
 * of the factory.
 * 
 * 
 * @author spb
 * @param <O> Owner objects
 * @param <A> Attribute type
 *
 */

public abstract class AttributeObjectFactory<O extends DataObject,A extends AttributeObjectFactory.AttributeObject<O>> extends DataObjectFactory<A> {

	public static final String OWNER_FIELD="OwnerID";
	
	private final DataObjectFactory<O> owner_fac;
	public static class AttributeObject<O extends DataObject> extends DataObject implements Owned{
		private final AttributeObjectFactory<O, ?> fac;
		/**
		 * @param r
		 */
		protected AttributeObject(AttributeObjectFactory<O, ?> fac,Record r) {
			super(r);
			this.fac=fac;
		}
		/** get the Owner of this object
		 * 
		 * @return
		 * @throws DataException
		 */
		public O getOwner() throws DataException{
			return fac.getOwnerFactory().find(record.getIntProperty(OWNER_FIELD));
		}
		
		/** set the owner of the object. normally used when creating new attributes.
		 * 
		 * @param owner
		 */
		public void setOwner(O owner){
			record.setProperty(OWNER_FIELD, owner.getID());
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.Owned#getFactory()
		 */
		@Override
		public DataObjectFactory getFactory() {
			return fac;
		}
	}
	/** 
	 * 
	 */
	public AttributeObjectFactory(DataObjectFactory<O> owner_fac, String table) {
		this.owner_fac=owner_fac;
		setContext(owner_fac.getContext(), table);
	}
	@Override
	public Class<? super A> getTarget() {
		return AttributeObject.class;
	}
	
	public DataObjectFactory<O> getOwnerFactory(){
		return owner_fac;
	}

	/** Return {@link AttributeObject} for an owner or null
	 * if attribute not defined;
	 * 
	 * @param owner
	 * @return
	 * @throws DataException
	 */
	public A getAttribute(O owner) throws DataException{
		return find(new SQLValueFilter<A>(getTarget(), res, OWNER_FIELD, owner.getID()),true);
	}
	
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = new TableSpecification();
		spec.setField(OWNER_FIELD, new ReferenceFieldType(false, getOwnerFactory().getTag()));
		try {
			spec.new Index("OwnerIndex", true, OWNER_FIELD);
		} catch (InvalidArgument e) {
			c.getService(LoggerService.class).getLogger(getClass()).error("Error making index",e);
		}
		return spec;
	}
	

}