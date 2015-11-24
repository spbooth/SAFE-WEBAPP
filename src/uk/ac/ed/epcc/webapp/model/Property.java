// Copyright - The University of Edinburgh 2011
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
@uk.ac.ed.epcc.webapp.Version("$Id: Property.java,v 1.13 2014/09/15 14:30:28 spb Exp $")

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
    public void setName(String name){
    	record.setProperty(NAME, name);
    }
    public void setValue(String value){
    	record.setProperty(VALUE, value);
    }
    @Override
	public String getIdentifier(int max){
    	return getName();
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