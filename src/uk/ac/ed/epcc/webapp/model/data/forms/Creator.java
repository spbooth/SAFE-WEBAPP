// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.CreateAction;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFormFactory;
import uk.ac.ed.epcc.webapp.model.data.TableStructureContributer;

/** Default FormCreator for this Factory
 * 
 * @author spb
 * @see CreateTransition
 * @param <BDO> type we are creating
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: Creator.java,v 1.9 2015/10/26 10:06:58 spb Exp $")

public class Creator<BDO extends DataObject> extends DataObjectFormFactory<BDO> implements FormCreator, CreateTemplate<BDO>{
	/**
	 * @param dataObjectFactory
	 */
	public Creator(DataObjectFactory<BDO> dataObjectFactory) {
		super(dataObjectFactory);
		
	}
	public void buildCreationForm(String type_name,Form f) throws Exception {
		buildForm(f);
		setAction(type_name,f);
		customiseCreationForm(f);
		for(TableStructureContributer comp : getFactory().getTableStructureContributers()){
			if( comp instanceof CreateCustomizer){
				((CreateCustomizer)comp).customiseCreationForm(f);
			}
		}
	}
    public void setAction(String type_name,Form f) {
    	f.addAction(" Create ", new CreateAction<BDO>(type_name,this));
    }
	
	/**
	 * Perform target specific customisation of a creation Form. For example
	 * adding a special validator. Note that this is called in addition to the
	 * basic {@link #customiseForm(Form)} call
	 * 
	 * @param f
	 *            Form to be modified
	 * @throws Exception 
	 */
	public void customiseCreationForm(Form f) throws Exception {

	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.CreateTemplate#preCommit(BDO, uk.ac.ed.epcc.webapp.model.data.forms.Form)
	 */
	public  void preCommit(BDO dat, Form f) throws DataException {
		for(CreateCustomizer comp : getFactory().getComposites(CreateCustomizer.class)){
			comp.preCommit(dat, f);
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.CreateTemplate#postCreate(BDO, uk.ac.ed.epcc.webapp.model.data.forms.Form)
	 */
	public void postCreate(BDO dat, Form f) throws Exception {
		for(CreateCustomizer comp : getFactory().getComposites(CreateCustomizer.class)){
			comp.postCreate(dat, f);
		}
	}
	
	public FormResult getResult(String type_name,BDO dat, Form f) {
		MessageResult res = new MessageResult("object_created",type_name);
		
		return res;
	}
	public String getConfirm(Form f) {
		return null;
	}

}