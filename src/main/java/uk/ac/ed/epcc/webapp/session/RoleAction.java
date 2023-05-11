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
import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.inputs.BinaryInput;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

public class RoleAction<U extends AppUser> extends FormAction{
	U p ;
	public RoleAction(U dat) {
		p=dat;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MessageResult action(Form f) throws uk.ac.ed.epcc.webapp.forms.exceptions.ActionException {
		LoggerService ls = p.getContext().getService(LoggerService.class);
		for(Iterator<String> it = f.getFieldIterator(); it.hasNext();){
			String key=it.next();
			BinaryInput i = (BinaryInput) f.getInput(key);
			try{
				SessionService serv = p.getContext().getService(SessionService.class);
				boolean old_val = serv.explicitRole(p, key);
				boolean new_val = i.isChecked();
				serv.setRole(p, key, new_val);
				if( old_val != new_val) {
					Map values=new LinkedHashMap();
					values.put("target", p.getIdentifier());
					values.put("old_value", old_val);
					values.put("new_value", new_val);
					values.put("role", key);
					ls.securityEvent("SetRole", serv,values);
				}
			
			}catch(Exception e){
				p.getContext().error(e,"Error modifying role");
			}
		}
		return new MessageResult("roles_updated",p);
	}
	
}