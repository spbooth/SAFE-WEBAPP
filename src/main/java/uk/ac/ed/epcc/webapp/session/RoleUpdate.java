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
package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.factory.StandAloneFormUpdate;
import uk.ac.ed.epcc.webapp.forms.inputs.CheckBoxInput;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;




public  class RoleUpdate<U extends AppUser> implements Contexed, StandAloneFormUpdate<U>, IndexedProducer<U>{
   
	private static final String PERSON = "RoleOwner";
	AppUserFactory<U> fac;
    SessionService<U> serv;
    @SuppressWarnings("unchecked")
	public RoleUpdate(AppContext conn){
    	serv=conn.getService(SessionService.class);
    	fac =serv.getLoginFactory();
    }
	@Override
	public void buildSelectForm(Form f, String label, U dat) {
		f.addInput(PERSON, "Role owner", fac.getInput());
	}

	
	
	@Override
	public void buildUpdateForm(Form f, U dat,SessionService<?> operator) throws DataFault {
		for(String role : serv.getStandardRoles()){
			CheckBoxInput i = new CheckBoxInput("Y","N");
			i.setChecked(dat!=null && serv.explicitRole(dat, role));
			f.addInput(role, role, i);
		}
		f.addAction("Update", new RoleAction<>(dat));
	}

	@Override
	@SuppressWarnings("unchecked")
	public U getSelected(Form f) {
		//DataObjectItemInput<U> i = (DataObjectItemInput<U>) f.getInput(PERSON);
		return (U) f.getItem(PERSON);
	}

	@Override
	public AppContext getContext() {
		return fac.getContext();
	}
	

@Override
public U find(int id) throws DataException {
	return fac.find(id);
}

@Override
public IndexedReference<U> makeReference(U obj) {
	return fac.makeReference(obj);
}
@Override
public IndexedReference<U> makeReference(int id) {
	return fac.makeReference(id);
}
@Override
public boolean isMyReference(IndexedReference ref) {
	return fac.isMyReference(ref);
}
@Override
public U find(Number o) {
	return fac.find(o);
}
@Override
public Integer getIndex(U value) {
	return fac.getIndex(value);
}
/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer#getID(uk.ac.ed.epcc.webapp.Indexed)
 */
@Override
public String getID(U obj) {
	return fac.getID(obj);
}





}