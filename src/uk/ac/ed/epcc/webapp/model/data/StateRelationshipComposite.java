//| Copyright - The University of Edinburgh 2016                            |
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

import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.inputs.BooleanInput;
import uk.ac.ed.epcc.webapp.jdbc.filter.AbstractAcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A Composite that adds boolean field to an object. The value of this field determines if {@link SessionService}s have
 * a corresponding relationship with the target.
 * 
 * This is used in AND combination with other relationships to allow the other relationship to
 * be activated on a per-target basis.
 * 
 * The class can be sub-classed to include multiple instances for different fields as it
 * registers under its actual class.
 * @author spb
 *
 */
public class StateRelationshipComposite<U extends AppUser,BDO extends DataObject, X extends StateRelationshipComposite> extends Composite<BDO, X> implements AccessRoleProvider<U, BDO>{

	private final String field;
	/**
	 * @param fac
	 */
	protected StateRelationshipComposite(DataObjectFactory<BDO> fac,String field) {
		super(fac);
		this.field=field;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#getType()
	 */
	@Override
	protected Class<? super X> getType() {
		return (Class<? super X>) getClass();
	}

	public class Filter extends AbstractAcceptFilter<BDO>{

		/**
		 * @param target
		 */
		protected Filter() {
			super((Class<BDO>) DataObject.class);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
		 */
		@Override
		public boolean accept(BDO o) {
			return getRecord(o).getBooleanProperty(field,getDefault());
		}
		
	}
	public class PersonFilter<U extends AppUser> extends AbstractAcceptFilter<U>{

		private final BDO target;
		public PersonFilter(BDO target){
			super((Class<U>) AppUser.class);
			this.target=target;
		}
		

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
		 */
		@Override
		public boolean accept(U o) {
			return getRecord(target).getBooleanProperty(field,getDefault());
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider#hasRelationFilter(uk.ac.ed.epcc.webapp.session.SessionService, java.lang.String)
	 */
	@Override
	public BaseFilter<BDO> hasRelationFilter( String role,U u) {
		if( role.equals(field)){
			return new Filter();
		}
		return null;
	}

	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		 spec.setOptionalField(field, new BooleanFieldType(false, getDefault()));
		return spec;
	}

	/**
	 * @return
	 */
	protected boolean getDefault() {
		return getContext().getBooleanParameter("StateRelationship.default."+field, true);
	}

	@Override
	public Map<String, String> addTranslations(Map<String, String> translations) {
		String trans = getContext().getInitParameter("StateRelationship.label."+field);
		if( trans != null){
			translations.put(field, trans);
		}
		return translations;
	}

	@Override
	public Map<String, Object> addSelectors(Map<String, Object> selectors) {
		selectors.put(field,new BooleanInput());
		return selectors;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider#personInRelationFilter(uk.ac.ed.epcc.webapp.session.SessionService, java.lang.String, uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public BaseFilter<U> personInRelationFilter(SessionService<U> sess, String role, BDO target) {
		if( target== null){
			return null;
		}
		return new PersonFilter<U>(target);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider#providesRelationship(java.lang.String)
	 */
	@Override
	public boolean providesRelationship(String role) {
		return role.equals(field);
	}

}
