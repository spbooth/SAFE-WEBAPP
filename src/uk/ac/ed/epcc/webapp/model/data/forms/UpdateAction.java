// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms;

import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataObject;

/**	
 * FormAction class to create the target DataObject from a form
 * 
 * @author spb
 * @param <BDO> type we are updating
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: UpdateAction.java,v 1.2 2015/07/16 12:11:06 spb Exp $")

public class UpdateAction<BDO extends DataObject> extends FormAction {
	/**
 * 
 */
protected final UpdateTemplate<BDO> updater;
	protected BDO dat;
    protected String type_name;
	public UpdateAction(String type_name,UpdateTemplate<BDO> u, BDO dat) {
		updater = u;
		this.dat = dat;
		this.type_name=type_name;
	}

	@Override
	public FormResult action(Form f) throws ActionException {

		try {
			AppContext conn = dat.getContext();
			Map<String,Object> orig=dat.getMap();
			dat.formUpdate(f);
			boolean changed = dat.commit();
			if (changed) {
				postUpdate(dat,f,orig);
			}
			return updater.getResult(type_name, dat, f);
			
		} catch (DataException e) {
			throw new ActionException("Update failed", e);
		}
	}
	public void postUpdate(BDO dat,Form f,Map<String,Object> orig) throws DataException {
		updater.postUpdate(dat,f,orig);
	}

}