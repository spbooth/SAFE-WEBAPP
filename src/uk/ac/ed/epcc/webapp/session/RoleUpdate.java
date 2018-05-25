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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.factory.StandAloneFormUpdate;
import uk.ac.ed.epcc.webapp.forms.inputs.BinaryInput;
import uk.ac.ed.epcc.webapp.forms.inputs.CheckBoxInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;




public  class RoleUpdate<U extends AppUser> implements Contexed, StandAloneFormUpdate<U>, IndexedProducer<U>{
    public static final String ROLE_LIST_CONFIG = "role_list";
	private static final String PERSON = "RoleOwner";
	AppUserFactory<U> fac;
    SessionService<U> serv;
    @SuppressWarnings("unchecked")
	public RoleUpdate(AppContext conn){
    	serv=conn.getService(SessionService.class);
    	fac =serv.getLoginFactory();
    }
	public void buildSelectForm(Form f, String label, U dat) {
		f.addInput(PERSON, "Role owner", fac.getInput());
	}

	public Set<String> getRoles(){
		Set<String> result = new LinkedHashSet<String>();
		for(String s :fac.getContext().getExpandedProperty(ROLE_LIST_CONFIG, SessionService.ADMIN_ROLE).split(",")){
			result.add(s);
		}
		return result;
	}
	
	public void buildUpdateForm(String type_name,Form f, U dat,SessionService<?> operator) throws DataFault {
		for(String role : getRoles()){
			CheckBoxInput i = new CheckBoxInput("Y","N");
			i.setChecked(dat!=null && serv.canHaveRole(dat, role));
			f.addInput(role, role, i);
		}
		f.addAction("Update", new RoleAction<U>(type_name,dat));
	}

	@SuppressWarnings("unchecked")
	public U getSelected(Form f) {
		ItemInput<U> i = (ItemInput<U>) f.getInput(PERSON);
		return i.getItem();
	}

	public AppContext getContext() {
		return fac.getContext();
	}
	

public static class RoleAction<U extends AppUser> extends FormAction{
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
		return new MessageResult("object_updated",type_name,type_name);
	}
	
}


public U find(int id) throws DataException {
	return fac.find(id);
}
public Class<? super U> getTarget() {
	return fac.getTarget();
}
public IndexedReference<U> makeReference(U obj) {
	return fac.makeReference(obj);
}
public IndexedReference<U> makeReference(int id) {
	return fac.makeReference(id);
}
public boolean isMyReference(IndexedReference ref) {
	return fac.isMyReference(ref);
}
public U find(Number o) {
	return fac.find(o);
}
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