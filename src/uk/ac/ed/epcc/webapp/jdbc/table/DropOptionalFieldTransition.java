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
package uk.ac.ed.epcc.webapp.jdbc.table;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;

public class DropOptionalFieldTransition<T extends DataObjectFactory> extends DropFieldTransition<T>{

	public DropOptionalFieldTransition() {
		super();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.DropFieldTransition#getFieldInput()
	 */
	@Override
	public <I extends Input<String> & ItemInput<FieldInfo>> I getFieldInput(T target) {
		TableSpecification spec = target.getTableSpecification();
		Map<String,FieldInfo> map = new LinkedHashMap<String, Repository.FieldInfo>();
		Set<String> optionalFieldNames = spec.getOptionalFieldNames();
		Set<String> fieldNames = spec.getFieldNames();
		Repository res = getRepository(target);
		for(String name : res.getFields()){
			// optional are marked optional or not in spec
			if( optionalFieldNames.contains(name) || ! fieldNames.contains(name)){
				map.put(name, res.getInfo(name));
			}
		}
		return (I) new OptionalFieldInput<FieldInfo>(res, false, map);
	}

	
	
}