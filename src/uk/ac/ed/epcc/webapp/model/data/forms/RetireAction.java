// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Retirable;

/** Form action to retire an object implementing the Retirable interface
 * 
 * @author spb
 * @param <BDO> type we are creating
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: RetireAction.java,v 1.7 2015/08/11 14:25:20 spb Exp $")

	public class RetireAction<BDO extends DataObject & Retirable> extends FormAction {

	    private final BDO dat;
	    private final String type_name;
		public RetireAction(String type_name, BDO r) {
			setMustValidate(false);
			// This lets us customise the retire message for a particular type
			String confirm = r.getContext().getInitParameter("retire.confirm."+type_name.replace(" ", "_"), "retire");
			setConfirm(confirm);
			this.type_name=type_name;
			this.dat=r;
		}

		@Override
		public FormResult  action(Form f) throws ActionException {
			
			if (dat.canRetire()) {
				try {
					dat.getContext().getService(LoggerService.class).getLogger(getClass()).info("Retiring object "+dat.getIdentifier());
					dat.retire();
					return new MessageResult("object_retired",type_name);
				} catch (Exception e) {
					dat.getContext().error(e, "error retiring object");
					throw new ActionException("Error retiring object");
				}
			}else{
				throw new ActionException("Not in retirable state");
			}
		}
	}