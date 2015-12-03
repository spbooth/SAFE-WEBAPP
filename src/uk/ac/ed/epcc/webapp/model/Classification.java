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

import java.security.Principal;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;




/**
Classification is used to implement classification tables where objects are grouped into 
a finite set of categories defined in a database table.
This is so common that its worth having a utility class to save on boilerplate code.
<p>
This class can be used directly (passing the table name to the constructor) or it can be subclassed
to improve type safety and specify a default table. 
<p>
The name value should be unique within the table.


Table entries rarely change and may be populated manually.
*/


public class Classification extends DataObject implements Principal, Comparable<Classification>{

    /**
	 * 
	 */
	public static final String SORT_ORDER = "SortOrder";
	public static final String DESCRIPTION = "Description";
	public static final String NAME = "Name";

	public String getName()     { return record.getStringProperty(NAME); }
    public void setName(String name){ record.setProperty(NAME, name); }
    public String getDescription()     { return record.getStringProperty(DESCRIPTION); }

    protected Classification(Repository.Record res){
    	super(res);
    }

    /* (non-Javadoc)
     * @see uk.ac.ed.epcc.webapp.model.data.DataObject#getIdentifier()
     */
    @Override
    public String getIdentifier(int max_length) {
    	String name=  getName();
    	String description = getDescription();
    	if( description != null && (name.length()+description.length()+2 < max_length)){
    		return name+": "+description;
    	}
    	return name;
    }    

    @Override
	public String toString(){
    	return getName();
    }
    /** Generate a default {@link TableSpecification} for a Classification table
     * 
     * @param c
     * @return TableSpecification
     */
    public static TableSpecification getTableSpecification(AppContext c){
    	TableSpecification s = new TableSpecification();
    	s.setField(NAME, new StringFieldType(false, null, c.getIntegerParameter("classifier.name.length", 32)));
    	s.setField(DESCRIPTION, new StringFieldType(true, null, c.getIntegerParameter("classifier.description.length", 255)));
    	s.setOptionalField(SORT_ORDER, new IntegerFieldType(false, 0));
    	try {
			s.new Index("name_key",true,NAME);
		} catch (InvalidArgument e) {
			c.error(e,"Error making classification key");
		}
    	return s;
    }
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Classification o) {
		int result = 0;
		// first compare for different factories
		result = getFactoryTag().compareTo(o.getFactoryTag());
		if( result == 0 ){
			if( record.getRepository().hasField(SORT_ORDER)){
				result = record.getIntProperty(SORT_ORDER, 0) - o.record.getIntProperty(SORT_ORDER, 0);
			}
		}
		if( result == 0 ){
			result = getName().compareTo(o.getName());
		}
		return result;
	}

}