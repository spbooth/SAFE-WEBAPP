// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms;

import uk.ac.ed.epcc.webapp.editors.xml.DomTransitionProvider.EditTransition;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.forms.factory.StandAloneFormUpdate;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;

/** default {@link FormUpdate} for DataObjects
 * 
 * @see UpdateTransition
 * @see EditTransition
 * @author spb
 * @param <BDO> type we are updating
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: Updater.java,v 1.2 2015/04/13 10:32:33 spb Exp $")

public class Updater<BDO extends DataObject> extends DataObjectUpdateFormFactory<BDO> implements StandAloneFormUpdate<BDO>, UpdateTemplate<BDO>{

	
	/** Form key used for object selections
	 * 
	 */
	public static final String TARGET = "Target";
	/**
	 * @param dataObjectFactory
	 * @param req requesting person
	 */
	public Updater(DataObjectFactory<BDO> dataObjectFactory) {
		super(dataObjectFactory);
	}

	public void buildSelectForm(Form f, String label, BDO dat) {
		Input<Integer> i = getSelectInput();

		f.addInput(TARGET, label, i);
		if (dat != null && factory.isMine(dat)) {
		    i.setValue(new Integer(dat.getID()));
		}
	}

	public DataObjectItemInput<BDO> getSelectInput() {
		return factory.getInput();
	}
	@SuppressWarnings("unchecked")
	public BDO getSelected(Form f) {
		ItemInput<BDO> i = (ItemInput<BDO>) f.getInput(TARGET);
		return i.getItem();
	}
	
}