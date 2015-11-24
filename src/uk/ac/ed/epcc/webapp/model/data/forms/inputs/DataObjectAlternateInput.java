// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.inputs.AlternateItemInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseInput;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
@uk.ac.ed.epcc.webapp.Version("$Id: DataObjectAlternateInput.java,v 1.3 2014/09/15 14:30:31 spb Exp $")

public class DataObjectAlternateInput<BDO extends DataObject,I extends DataObjectItemInput<BDO>  & ParseInput<Integer> > extends AlternateItemInput<Integer,BDO> implements DataObjectItemInput<BDO>{

	public BDO getDataObject() {
		return getItem();
	}

	

}