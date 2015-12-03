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
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/** Input for a list of tags that must 
 * resolve to objects assignable to a particular type.
 * 
 * @author spb
 *
 */
public class ObjectListInput extends TextInput {
	private final Class<?> target;
	private final AppContext conn;
	public ObjectListInput(AppContext conn,Class<?> target) {
		this(conn,target,false);
	}

	public ObjectListInput(AppContext conn,Class<?> target,boolean allow_null) {
		super(allow_null);
		this.conn=conn;
		this.target=target;
		setSingle(true);
	}

	@Override
	public void validate() throws FieldException {
		super.validate();
		String list = getValue();
		if( list != null && list.trim().length() > 0){
			for(String n : list.split("\\s*,\\s*")){
				if( conn.makeObjectWithDefault(target,null, n)==null){
					throw new ValidateException("tag "+n+" not a "+target.getCanonicalName());
				}
			}
		}
	}

}