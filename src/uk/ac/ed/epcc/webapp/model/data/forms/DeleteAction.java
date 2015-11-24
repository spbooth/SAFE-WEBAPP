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

/** {@link FormAction} to delete an object.
 * 
 * normally only used as part of test/development
 * 
 * @author spb
 * @param <BDO> type we are deleting.
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: DeleteAction.java,v 1.2 2014/09/15 14:30:31 spb Exp $")

	public class DeleteAction<BDO extends DataObject> extends FormAction {

	    private final BDO dat;
	    private final String type_name;
		public DeleteAction(String type_name, BDO r) {
			setMustValidate(false);
			setConfirm("delete");
			this.type_name=type_name;
			this.dat=r;
		}

		@Override
		public FormResult  action(Form f) throws ActionException {
			
		
				try {
					dat.getContext().getService(LoggerService.class).getLogger(getClass()).info("Deleting object "+dat.getIdentifier());
					if( dat instanceof Retirable){
						if(((Retirable)dat).canRetire()){
							((Retirable)dat).retire();
						}
					}
					dat.delete();
					return new MessageResult("object_deleted",type_name);
				} catch (Exception e) {
					dat.getContext().error(e, "error deleting object");
					throw new ActionException("Error deleting object");
				}
			
		}
	}