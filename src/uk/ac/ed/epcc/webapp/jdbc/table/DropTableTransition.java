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
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;



public class DropTableTransition<T extends DataObjectFactory> extends
		AbstractDirectTransition<T> {
	AppContext conn;
	public DropTableTransition(AppContext conn){
		this.conn=conn;
	}
	public FormResult doTransition(T target,
			AppContext c) throws TransitionException {
		DataBaseHandlerService serv = conn.getService(DataBaseHandlerService.class);
		try{
		if( serv != null ){
			serv.deleteTable(target.getTag());
			return new TableListResult();
		}
		}catch(Exception e){
			conn.error(e,"Error dropping table");
		}
		return new MessageResult("internal_error");
	}

}