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
package uk.ac.ed.epcc.webapp.model.data.forms;

import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.content.UIProvider;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.Identified;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.UnRetirable;

/** Form action to retire an object implementing the Retirable interface
 * 
 * @author spb
 * @param <BDO> type we are creating
 *
 */


	public class UnRetireAction<BDO extends DataObject & UnRetirable> extends FormAction {

	    private final BDO dat;
	    private final String type_name;
		public UnRetireAction(String type_name, BDO r) {
			setMustValidate(false);
			setConfirm("restore");
			Object t = "";
			if( r instanceof UIGenerator || r instanceof UIProvider || r instanceof Identified) {
				t = r;
			}
			setConfirmArgs(new Object[] {type_name, t});
			this.type_name=type_name;
			this.dat=r;
		}

		@Override
		public FormResult  action(Form f) throws ActionException {
			
			if (dat.canRestore()) {
				try {
					Logger.getLogger(dat.getContext(),getClass()).info("Un-Retiring object "+dat.getIdentifier());
					dat.restore();
					return new MessageResult("object_restored",type_name);
				} catch (Exception e) {
					Logger.getLogger(getClass()).error("error restoring object",e);
					throw new ActionException("Error retiring object");
				}
			}else{
				throw new ActionException("Not in restorable state");
			}
		}
	}