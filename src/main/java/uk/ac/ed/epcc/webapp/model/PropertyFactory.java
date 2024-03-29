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
package uk.ac.ed.epcc.webapp.model;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.NoSpaceFieldValidator;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraContent;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.data.forms.Updater;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Factory for Configuration {@link Property}s stored in the database.
 * 
 * @author spb
 *
 */

public class PropertyFactory extends DataObjectFactory<Property> {
    private static final int MAX_NAME_LENGTH = 255;
	/**
	 * 
	 */
	private static final int MAX_PROP_LENGTH = 4096;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#getSelectors()
	 */
	@Override
	protected Map<String, Selector> getSelectors() {
		Map<String, Selector> selectors = super.getSelectors();
		
		selectors.put(Property.NAME, new Selector() {

			@Override
			public Input getInput() {
				TextInput name_input = new TextInput();
				name_input.setSingle(true);
				name_input.setTrim(true);
				name_input.setBoxWidth(64);
				name_input.setMaxResultLength(MAX_NAME_LENGTH);
				name_input.addValidator(new NoSpaceFieldValidator());
				return name_input;
			}
			
		});
		// default input may forbid html but this should be allowed in props
		
		selectors.put(Property.VALUE, new Selector() {

			@Override
			public Input getInput() {
				TextInput prop_input = new TextInput();
				prop_input.setBoxWidth(64);
				prop_input.setMaxResultLength(MAX_PROP_LENGTH);
				prop_input.setSingle(true);
				return prop_input;
			}
			
		});
		return selectors;
	}
	public PropertyFactory(AppContext c, String table){
    	initContext(c,table);
    }
    public PropertyFactory(AppContext c){
    	String table = c.getInitParameter("database.properties");
    	if( table != null ){
    		initContext(c, table);
    	}
    }
    private void initContext(AppContext c, String table){
			setContext(c,table);
    }
    @Override
    protected TableSpecification getDefaultTableSpecification(AppContext c,String table){
    	TableSpecification s = new TableSpecification();
		s.setField(Property.NAME, new StringFieldType(false,null,MAX_NAME_LENGTH));
		s.setField(Property.VALUE, new StringFieldType(false,null,255));
		try {
			s.new Index("name_key", true, Property.NAME);
		} catch (InvalidArgument e) {
			c.error(e,"Failed to create name key");
		}
		return s;
    }
	@Override
	protected Property makeBDO(Record res) throws DataFault {
		return new Property(res);
	}
    public void loadProperties(Properties props){
    	try {
    		Logger log= getContext().getService(LoggerService.class).getLogger(getClass());
			for(Property p: all()){
				String name=p.getName().trim();
				String value=p.getValue().trim();
				log.debug("setting "+name+"->"+value);
				props.setProperty(name, value);
			}
		} catch (DataFault e) {
			getLogger().error("Error loading properties from "+getTag(),e);
		}
    }
    public Property findByName(String name) throws DataException{
    	return find(new SQLValueFilter<>(res, Property.NAME, name),true);
    }
    /** Save properties to the database.
     * 
     * @param p
     * @throws DataException 
     */
    public void saveProperties(Properties p) throws DataException{
    	for(Enumeration e = p.propertyNames(); e.hasMoreElements();){
    		String name = (String) e.nextElement();
    		String value=p.getProperty(name);
    		setProperty(name, value);	
    	}
    }
    public void setProperty(String name, String value) throws DataException,
    DataFault {
    	Property prop = findByName(name);
    	if( value == null ){
    		if( prop != null ){
    			prop.delete();
    		}
    	}else{
    		if( prop == null ){
    			prop = makeBDO();
    			prop.setName(name);
    		}
    		prop.setValue(value);
    		prop.commit();
    	}
    }
	@Override
	protected List<OrderClause> getOrder() {
		
		List<OrderClause> order = super.getOrder();
		order.add(res.getOrder(Property.NAME,false));
		return order;
	}
	public class PropertyUpdater extends Updater<Property> implements ExtraContent<Property>{

		public PropertyUpdater() {
			super(PropertyFactory.this);
		}

		@Override
		public <X extends ContentBuilder> X getExtraHtml(X cb, SessionService<?> op, Property target) {
			String u = target.getUnderlyingValue();
			if( u == null ) {
				cb.addText("Property only exists in the database");
			}else if( u.equals(target.getValue())) {
				cb.addText("Underlying value is the same as the database value");
			}else {
				ExtendedXMLBuilder text = cb.getText();
				text.clean("Underlying value is:");
				text.open("b");
				text.clean(u);
				text.close();
				text.appendParent();
			}
			return cb;
		}
		
	}
	@Override
	public FormUpdate<Property> getFormUpdate(AppContext c) {
		return new PropertyUpdater();
	}
}