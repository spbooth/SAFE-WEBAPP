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

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.HistoryFactory;
import uk.ac.ed.epcc.webapp.model.data.Owned;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;




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


public class Classification extends DataObject implements Principal, Comparable<Classification>, Owned{

    @Override
	public String getName()     { return record.getStringProperty(ClassificationFactory.NAME); }
    public void setName(String name){ record.setProperty(ClassificationFactory.NAME, name); }
    public String getDescription()     { return record.getStringProperty(ClassificationFactory.DESCRIPTION); }
    public void setDescription(String desc){ record.setOptionalProperty(ClassificationFactory.DESCRIPTION, desc); }
    
    protected ClassificationFactory fac;
    
    protected Classification(Repository.Record res, ClassificationFactory fac){
    	super(res);
    	this.fac = fac;
    }

    /* (non-Javadoc)
     * @see uk.ac.ed.epcc.webapp.model.data.DataObject#getIdentifier()
     */
    @Override
    public String getIdentifier(int max_length) {
    	String name=  getName();
    	String description = getDescription();
    	if( description != null && ! description.trim().isEmpty() && (name.length()+description.length()+2 < max_length)){
    		return name+": "+description;
    	}
    	return name;
    }    

    @Override
	public String toString(){
    	return getName();
    }
    /* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Classification o) {
		int result = 0;
		// first compare for different factories
		result = getFactoryTag().compareTo(o.getFactoryTag());
		if( result == 0 ){
			if( record.getRepository().hasField(ClassificationFactory.SORT_ORDER)){
				result = record.getIntProperty(ClassificationFactory.SORT_ORDER, 0) - o.record.getIntProperty(ClassificationFactory.SORT_ORDER, 0);
			}
		}
		if( result == 0 ){
			result = getName().compareTo(o.getName());
		}
		return result;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObject#post_commit(boolean)
	 */
	@Override
	protected void post_commit(boolean changed) throws DataFault {
		if ((changed) && (fac != null)) {
			HistoryFactory hist_fac = fac.getHistoryFactory();
			if (hist_fac != null) {
				try {
					hist_fac.update(this);
				}
				catch (DataException ex) {
					getLogger().error("Error updating history table", ex);
				}
			}
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Owned#getFactory()
	 */
	@Override
	public final ClassificationFactory getFactory() {
		return fac;
	}
	
}