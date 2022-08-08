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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.jdbc.config.DataBaseConfigService;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Retirable;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;

/** Configuration property stored in the DataBase
 * The name field should be unique.
 * @author spb
 *
 */


public class Property extends DataObject implements Retirable{

	static final String VALUE = DataBaseConfigService.VALUE;
	static final String NAME = DataBaseConfigService.NAME;
	@Override
	public void post_commit(boolean changed) throws DataFault {
		if(changed){
			AppContext c = getContext();
			c.getService(ConfigService.class).clearServiceProperties();	
		}
	}
	protected Property(Record r) {
		super(r);
	}
    public String getName(){
    	return record.getStringProperty(NAME);
    }
    public String getValue(){
    	return record.getStringProperty(VALUE);
    }
    public String getUnderlyingValue() {
    	ConfigService u = getUnderlyingService();
    	if( u != null ) {
    		return u.getServiceProperties().getProperty(getName());
    	}
    	return null;
    }

    private ConfigService getUnderlyingService() {
    	ConfigService c = getContext().getService(ConfigService.class);
    	while( c != null ) {
    		if( c instanceof DataBaseConfigService) {
    			return c.getNested();
    		}else {
    			c=c.getNested();
    		}
    	}
    	return null;
    }
    public void setName(String name){
    	record.setProperty(NAME, name);
    }
    public void setValue(String value){
    	record.setProperty(VALUE, value);
    }
    @Override
	public String getIdentifier(int max){
    	String tag="";
    	String u = getUnderlyingValue();
    	if( u == null ) {
    		tag="[D]";
    	}else if( u.equals(getValue())) {
    		tag="[U]";
    	}else {
    		tag="[M]";
    	}
    	return getName()+tag;
    }
	public boolean canRetire() {
		return true;
	}
	public void retire() throws Exception {
		AppContext c = getContext();
		delete();
		c.getService(ConfigService.class).clearServiceProperties();
	}
}