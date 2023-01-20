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
package uk.ac.ed.epcc.webapp.model.far.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.ReferenceFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterDelete;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.far.PartManager;
import uk.ac.ed.epcc.webapp.model.far.PartOwner;
import uk.ac.ed.epcc.webapp.model.far.PartManager.Part;

/** Holds the question configuration parameters.
 * These are stored as named strings associated with a {@link Part}. 
 * @author spb
 * @param <O> 
 * @param <P> 
 *
 */

public class PartConfigFactory<O extends PartOwner,P extends Part<O>> extends DataObjectFactory<PartConfigFactory.Config> {

	/**
	 * 
	 */
	private static final String PART_ID_FIELD = "PartID";
	/**
	 *   
	 */
	private static final String VALUE_FIELD = "Value";
	/**
	 * 
	 */
	private static final String NAME_FIELD = "Name";
	private final PartManager<O,P> manager;
	
	public PartConfigFactory(PartManager<O,P> manager){
		this.manager=manager;
		setContext(manager.getContext(), manager.getTag()+"Config");
	}
	public static class Config<P extends Part> extends DataObject{

		/**
		 * @param r
		 */
		protected Config(Record r) {
			super(r);

		}
		public String getName(){
			return record.getStringProperty(NAME_FIELD);
		}
		public String getValue(){
			return record.getStringProperty(VALUE_FIELD);
		}
		public void setName(String name){
			record.setProperty(NAME_FIELD, name);
		}
		public void setValue(String value){
			record.setProperty(VALUE_FIELD, value);
		}
		public void setOwner(P part){
			record.setProperty(PART_ID_FIELD, part.getID());
		}
	}
	
	public Map<String,Object> getValues(P owner) throws DataFault{
		Map<String,Object> result = new HashMap<>();
		for( Config<P> c : getResult(getPartFilter(owner))){
			result.put(c.getName(), c.getValue());
		}
		return result;
	}
	public Config makeEntry(P owner, String name) throws DataException{
		Config<P> result;
		SQLAndFilter<Config> fil = getNamePartFilter(owner, name);
		result = find(fil,true);
		if( result == null ){
			result = makeBDO();
			result.setName(name);
			result.setOwner(owner);
		}
		return result;
	}
	/**
	 * @param owner
	 * @param name
	 * @return
	 */
	private SQLAndFilter<Config> getNamePartFilter(P owner, String name) {
		return new SQLAndFilter<>(getTag(), getPartFilter(owner),getNameFilter(name));
	}
	/**
	 * @param name
	 * @return
	 */
	private SQLValueFilter<Config> getNameFilter(String name) {
		return new SQLValueFilter<>(res, NAME_FIELD, name);
	}

	public void setValues(P owner,Map<String,Object> values) throws DataException{
		for(String name : values.keySet()){
			Config<P> entry = makeEntry(owner, name);
			entry.setValue(values.get(name).toString());
			entry.commit();
		}
		// now check for deletes.
		FilterDelete<Config> delete = new FilterDelete<>(res);
		Map<String,Object> old_values = getValues(owner);
		for(String name : old_values.keySet()){
			if( ! values.containsKey(name)){
				delete.delete(getNamePartFilter(owner, name));
			}
		}
	}
	/**
	 * @param owner
	 * @return
	 */
	private ReferenceFilter<Config, P> getPartFilter(P owner) {
		return new ReferenceFilter<>(this, PART_ID_FIELD, owner);
	}
	
	public void clearAll(P owner) throws DataFault{
		FilterDelete<Config> delete = new FilterDelete<>(res);
		delete.delete(getPartFilter(owner));
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected Config makeBDO(Record res) throws DataFault {
		return new Config(res);
	}




	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = new TableSpecification("ConfigID");
		spec.setField(NAME_FIELD, new StringFieldType(false, null, 128));
		spec.setField(VALUE_FIELD, new StringFieldType(false, null, 128));
		spec.setField(PART_ID_FIELD, new ReferenceFieldType(manager.getTag()));
		
		try {
			spec.new Index("PartIndex",false,PART_ID_FIELD,NAME_FIELD);
		} catch (InvalidArgument e) {
			getLogger().error("Error making index",e);
		}
		return spec;
	}
	@Override
	protected List<OrderClause> getOrder() {
		List<OrderClause> order = super.getOrder();
		order.add(res.getOrder(PART_ID_FIELD, false));
		order.add(res.getOrder(NAME_FIELD, false));
		return order;
	}
}