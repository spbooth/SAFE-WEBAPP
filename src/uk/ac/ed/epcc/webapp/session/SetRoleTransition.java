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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.CheckBoxInput;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;

/**
 * @author Stephen Booth
 *
 */
public class SetRoleTransition<U extends AppUser> extends AbstractFormTransition<U> {

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
	 */
	@Override
	public void buildForm(Form f, U dat, AppContext conn) throws TransitionException {
		SessionService<U> serv = conn.getService(SessionService.class);
		for(String role : serv.getStandardRoles()){
			CheckBoxInput i = new CheckBoxInput("Y","N");
			i.setChecked(dat!=null && serv.canHaveRole(dat, role));
			f.addInput(role, role, i);
		}
		f.addAction("Update", new RoleAction<U>("Person role",dat));

	}

}
