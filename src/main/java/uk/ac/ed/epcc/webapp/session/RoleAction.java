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
package uk.ac.ed.epcc.webapp.session;

import java.util.Iterator;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.inputs.BinaryInput;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;

public class RoleAction<U extends AppUser> extends FormAction{
	U p ;
    String type_name;
	public RoleAction(String type_name,U dat) {
		this.type_name=type_name;
		p=dat;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MessageResult action(Form f) throws uk.ac.ed.epcc.webapp.forms.exceptions.ActionException {
		for(Iterator<String> it = f.getFieldIterator(); it.hasNext();){
			String key=it.next();
			BinaryInput i = (BinaryInput) f.getInput(key);
			try{
				SessionService serv = p.getContext().getService(SessionService.class);
				serv.setRole(p, key, i.isChecked());
			
			}catch(Exception e){
				p.getContext().error(e,"Error modifying role");
			}
		}
		return new MessageResult("roles_updated",p);
	}
	
}