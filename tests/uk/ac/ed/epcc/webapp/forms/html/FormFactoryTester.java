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
/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.html;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.factory.FormFactory;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.forms.factory.StandAloneFormUpdate;
import uk.ac.ed.epcc.webapp.forms.factory.UpdateForm;
import uk.ac.ed.epcc.webapp.session.SessionService;

public class FormFactoryTester {
	AppContext ctx;
	public FormFactoryTester(AppContext c){
		ctx=c;
	}
@SuppressWarnings("unchecked")
public  void testFactory(FormFactory fac) throws Exception{
	if( fac instanceof FormCreator){
		  testCreate((FormCreator) fac);
	  }
	  if( fac instanceof FormUpdate){
		  testUpdate((FormUpdate) fac);
	  }
}
public void testCreate(FormCreator f){
	HTMLCreationForm c = new HTMLCreationForm("Test",f);
	  c.getHtmlForm(null);
	  
	  //System.out.println(form.getHtmlForm(null,null,new HashMap()));
}
@SuppressWarnings("unchecked")
public <T> void testUpdate(FormUpdate<T> f) throws Exception{
	  HTMLForm form = new HTMLForm(ctx);
	  UpdateForm<T> u = new UpdateForm<>("test",f);
	  u.buildSelectForm(form);
	  form.clear();
	  if( u instanceof StandAloneFormUpdate){
		  ((StandAloneFormUpdate)u).buildUpdateForm("Test",form,null,ctx.getService(SessionService.class));
	  }
	
	  
	  //f.buildUpdateForm(form,null);
}
}