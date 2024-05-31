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

import java.sql.Types;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.DoubleFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.FloatFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.LongFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
/** class to guess table specifications based on a existing Repository.
 * 
 * @author spb
 *
 */
public class SpecificationBuilder {
	public SpecificationBuilder() {
	}
	
	public TableSpecification getSpecification(DataObjectFactory fac){
		return getSpecification(fac.res);
	}
	public TableSpecification getSpecification(Repository res){
		TableSpecification spec = new TableSpecification(res.getUniqueIdName());
		for( String fields : res.getFields()){
			Repository.FieldInfo info = res.getInfo(fields);
			if( info.isString()){
				spec.setField(fields, new StringFieldType(info.getNullable(), info.getNullable() ? null : "", info.getMax()));
			}else if( info.isBoolean()){
				spec.setField(fields, new BooleanFieldType(info.getNullable(), false));
			}else if( info.isDate()){
				spec.setField(fields, new DateFieldType(info.getNullable(), null));
			}else if( info.isReference()){
				spec.setField(fields, new ReferenceFieldType(info.getNullable(), info.getReferencedTable(),info.isIndexed()));
			}else if( info.isNumeric()){
				int type = info.getType();
				if( type == Types.INTEGER){
					spec.setField(fields, new IntegerFieldType(info.getNullable(),info.getNullable() ? null : 0));
				}else if( type == Types.BIGINT){
					spec.setField(fields, new LongFieldType(info.getNullable(),info.getNullable() ? null : 0L));
				}else if( type == Types.FLOAT){
					spec.setField(fields, new FloatFieldType(info.getNullable(),info.getNullable() ? null : 0.0F));
				}else if( type == Types.DOUBLE){
					spec.setField(fields, new DoubleFieldType(info.getNullable(),info.getNullable() ? null : 0.0));
				}else{
					throw new ConsistencyError("Unsupported type "+type);
				}
			}else{
				throw new ConsistencyError("unknown field type");
			}
					
		}
		
		return spec;
	}

}