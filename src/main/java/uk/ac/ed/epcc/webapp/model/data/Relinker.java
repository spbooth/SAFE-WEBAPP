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

import java.util.Enumeration;
import java.util.Properties;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterUpdate;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;

/** Class for relinking references.
 * 
 * 
 * This works at the {@link Repository} level and identifies references via properties. It attempts to find all fields that
 * point to a particular record and rewrite them to point to a different record instead. The operation is intended for database refactoring (specifically merge operations)
 * rather than production use.
 * @author spb
 *
 */

public class Relinker{

	/**
	 * 
	 */
	public Relinker() {
		
	}

	
	public void relink(Repository res, Record src, Record dest) throws DataFault, ConsistencyError{
		String home = res.getTag();
		
		AppContext conn = res.getContext();
		String alt_tag = res.getTable();
		
		// references have reference.param_name.field=remote  parameters
		// rolled tables with param_name != tag_name won't be found
		ConfigService config = conn.getService(ConfigService.class);
		Properties props = config.getServiceProperties();
		Enumeration<?> p = props.propertyNames();
		while( p.hasMoreElements()){
			String name = p.nextElement().toString();
			if( name.startsWith(Repository.REFERENCE_PREFIX) ){
				String prop = props.getProperty(name);
				if(prop.equals(home) || prop.equals(alt_tag)){

					// found one
					String val = name.substring(Repository.REFERENCE_PREFIX.length());
					int pos = val.indexOf(".");
					if( pos < 0 ){
						continue;
					}
					String field=val.substring(pos+1);
					String src_tag=val.substring(0, pos);
					Repository src_rep = Repository.getInstance(conn, src_tag);
					FilterUpdate update = new FilterUpdate(src_rep);
					if( ! src_rep.hasField(field)){
						continue;
					}
					FieldExpression<Number,DataObject>expr = src_rep.getNumberExpression(DataObject.class, Number.class, field);
					update.update(expr, dest.getID(), new SQLValueFilter<>(DataObject.class, src_rep, field, src.getID()));
				}
			}
		}
	}
}