//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.model;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.content.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.inputs.BooleanInput;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.GenericBinaryFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link Composite} that generates named filters based on boolean
 * database fields. 
 * 
 * This is a generic way of adding per-object configuration that is only used to 
 * control relationship clauses.
 * <p>
 * The list of fields/filter-names is given in the config parameter;
 * <b><i>factory-tag</i>.config_namefilters</b>. 
 * <p>
 * A form label for the field can be set using the property:
 * <b><i>factory-tag</i>.<i>field</i>.label</b>
 * <p>
 * Editing the fields can be made role dependent by setting the parameter
 * <b><i>factory-tag</i>.<i>field</i>.edit_role</b> to the required global role name.
 * <p>
 * The default value (to use when no database field present can be set using
 * <b><i>factory-tag</i>.<i>field</i>.default</b> (false if unspecified).
 * @author spb
 *
 */
public class ConfigNamedFilterComposite<BDO extends DataObject> extends Composite<BDO, ConfigNamedFilterComposite> implements NamedFilterProvider<BDO> , SummaryContributer<BDO>{

	LinkedHashSet<String> names = new LinkedHashSet<>();
	/**
	 * @param fac
	 */
	public ConfigNamedFilterComposite(DataObjectFactory<BDO> fac,String comp_tag) {
		super(fac,comp_tag);
		String name_list = getContext().getInitParameter(fac.getConfigTag()+".config_namefilters");
		if( name_list != null ) {
			for( String name : name_list.split("\\s*,\\s*")) {
				names.add(name);
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider#getNamedFilter(java.lang.String)
	 */
	@Override
	public BaseFilter<BDO> getNamedFilter(String name) {
		if( useName(name) ) {
			if( getRepository().hasField(name)) {
				return new SQLValueFilter<>(getRepository(), name, Boolean.TRUE);
			}else {
				return new GenericBinaryFilter<BDO>( getDefault(name));
			}
		}
		return null;
	}
	
	public boolean hasNamedFilter(BDO target,String name) {
		if( useName(name)) {
			return getRecord(target).getBooleanProperty(name, getDefault(name));
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider#addFilterNames(java.util.Set)
	 */
	@Override
	public void addFilterNames(Set<String> filter_names) {
		for(String name : names) {
			filter_names.add(name);
		}
	}

	public void setNamedFilter(BDO obj,String name, boolean value) throws InvalidArgument {
		if( useName(name)) {
			getRecord(obj).setOptionalProperty(name, value);
		}else {
			throw new InvalidArgument("Not a configured filter: "+name);
		}
	}

	public boolean useName(String name) {
		return names.contains(name);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#getType()
	 */
	@Override
	protected Class<? super ConfigNamedFilterComposite> getType() {
		return ConfigNamedFilterComposite.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#modifyDefaultTableSpecification(uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification, java.lang.String)
	 */
	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		for(String name : names) {
			spec.setField(name, new BooleanFieldType(true, getDefault(name)),getContext().getBooleanParameter(getFactory().getConfigTag()+"."+name+".optional", false));
		}
		
		return spec;
	}

	

	private String getLabel(String name) {
		// compatible with DataObjectFormFactory
		return getContext().getInitParameter(getFactory().getConfigTag()+"."+name+".label");
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addSelectors(java.util.Map)
	 */
	@Override
	public Map<String, Selector> addSelectors(Map<String, Selector> selectors) {
		Selector<BooleanInput> s = BooleanInput::new;
		for(String name : names) {
			selectors.put(name, s);
		}
		return selectors;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addSuppress(java.util.Set)
	 */
	@Override
	public Set<String> addSuppress(Set<String> suppress) {
		SessionService serv = getContext().getService(SessionService.class);
		for(String name : names) {
			String role = getContext().getInitParameter(getFactory().getConfigTag()+"."+name+".edit_role");
			if( role != null && ! serv.hasRole(role) ) {
				suppress.add(name);
			}
		}
		return suppress;
	}
	
	public boolean getDefault(String name) {
		return getContext().getBooleanParameter(getFactory().getConfigTag()+"."+name+".default", false);
	}

	@Override
	public void addAttributes(Map<String, Object> attributes, BDO target) {
		for(String name : names) {
			if( getRepository().hasField(name) && getContext().getBooleanParameter(getFactory().getConfigTag()+"."+name+".show_attribute", false)) {
				attributes.put(getLabel(name), hasNamedFilter(target, name));
			}
		}
		
	}

}
