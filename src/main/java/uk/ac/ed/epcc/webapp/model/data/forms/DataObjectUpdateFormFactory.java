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
package uk.ac.ed.epcc.webapp.model.data.forms;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jfree.data.xy.DefaultTableXYDataset;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.content.UIProvider;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.Identified;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionValidationException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.*;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A default superclass for implementing {@link UpdateTemplate}
 * @author spb
 *
 * @param <BDO>
 */
public abstract  class DataObjectUpdateFormFactory<BDO extends DataObject> extends DataObjectFormFactory<BDO> implements UpdateTemplate<BDO>{

	/**
	 * 
	 */
	private static final String UN_RETIRE = " UnRetire ";
	/**
	 * 
	 */
	public static final String RETIRE = " Retire ";
	/**
	 * 
	 */
	public static final String UPDATE = " Update ";

	/**
	 * @param fac
	 */
	public DataObjectUpdateFormFactory(DataObjectFactory fac) {
		super(fac);
	}

	/**
	 * Perform target specific customisation of an update Form. For example we
	 * can replace Inputs with read-only fields keeping the field in the form
	 * for information but disabling input.Note that this is called in addition
	 * to the basic customiseForm call.
	 * 
	 * Default behaviour is to lock fields based on  the 
	 * <b>form.lock.<em>table</em>.<em>field</em></b> property.
	 * 
	 * Editing can be permitted for certs roles of the
	 * <b>form.edit_field_role.<em>table</em>.<em>field</em></b> property is set
	 * 
	 * @param f
	 *            Form to be modified
	 * @param o
	 *            Target Object.
	 */
	public void customiseUpdateForm(Form f, BDO o) {
		AppContext c = getContext();
		String tag = factory.getConfigTag();
		for(Iterator<String> it = f.getFieldIterator(); it.hasNext();){
			String name = it.next();
			if( c.getBooleanParameter("form.lock."+tag+"."+name, false)){
				String edit_role = c.getInitParameter("form.edit_field_role."+tag+"."+name,null);
				if( edit_role==null || ! c.getService(SessionService.class).hasRoleFromList(edit_role.split(","))){
					f.getField(name).lock();
				}
			}
		}
	}

	public HashMap getFixtures(BDO dat) {
		HashMap fixtures = new HashMap();
		if( dat != null) {
			Map data = dat.getMap();
			for(String field : getSupress()) {
				Object value = data.get(field);
				if( value != null) {
					fixtures.put(field,value);
				}
			}
		}
		return fixtures;
	}
	public final void buildUpdateForm(Form f, BDO dat, SessionService<?> operator)
			throws DataFault {
				
			    boolean complete = buildForm(f,getFixtures(dat), dat == null ? getCreationDefaults(): getUpdateDefaults(dat));
			
			    // set values before customise as it is common
				// for the customise methods to lock fields
				// and the values need to be in place before then.
			    //
			   
				for(TableStructureContributer<BDO> contrib : factory.getTableStructureContributers()){
					contrib.customiseUpdateForm(f, dat, operator);
				}
				
				customiseUpdateForm(f, dat);
				
				if( complete ) {
					customiseCompleteUpdateForm(f, dat);
					f.addAction(getUpdateActionName(), new UpdateAction<>(this, dat));
					addRetireAction( f, dat, operator);
				}
				
				
			}

	/** Get a set of default values for the form fields 
	 * 
	 * By default, this is all the existing values of the object, but can be over-ridden
	 * if you are adding additional Fields 
	 * @return Map of defaults
	 */
	protected Map<String, Object> getUpdateDefaults(BDO dat) {
		return dat.getMap();
	}

	/** Similar to {@link #customiseUpdateForm(Form, DataObject)} but only called when the form is complete
	 * 
	 * @param f
	 * @param o
	 */
	public void customiseCompleteUpdateForm(Form f, BDO o) {
		
	}
	
	
	protected String getUpdateActionName() {
		return UPDATE;
	}

	/**
	 * @param f
	 * @param dat
	 * @param operator
	 */
	protected void addRetireAction(Form f, BDO dat, SessionService<?> operator) {
		if (dat instanceof Retirable && ( ! (dat instanceof RestrictedRetirable)  || ((RestrictedRetirable)dat).allowRetire(operator))){
			Retirable retirable = (Retirable) dat;
			if( retirable.useAction()) {
				if(retirable.canRetire()) {
					f.addAction(RETIRE, new RetireAction(getTypeName(), dat));
				}else if( dat instanceof UnRetirable && ((UnRetirable)dat).canRestore()){
					f.addAction(UN_RETIRE, new UnRetireAction(getTypeName(), dat));
				}
			}
		}
	}
	@Override
	public void preCommit(BDO dat,Form f,Map<String,Object> orig) throws DataException, TransitionValidationException{
		for(UpdateContributor<BDO> comp : getFactory().getComposites(UpdateContributor.class)) {
			comp.preCommit(dat, f, orig);
		}
	}
	@Override
	public void postUpdate(BDO o, Form f, Map<String,Object> origs,boolean changed)
			throws DataException {
		compositesPostUpdate(o, f, origs,changed);
	}

	/**
	 * @param o
	 * @param f
	 * @param origs
	 * @throws DataException
	 */
	protected void compositesPostUpdate(BDO o, Form f, Map<String, Object> origs, boolean changed) throws DataException {
		for(TableStructureContributer c : factory.getTableStructureContributers()){
			c.postUpdate(o, f, origs, changed);
		}
	}

	@Override
	public FormResult getResult(BDO dat, Form f) {
		Object thing = getTypeName();
		if( dat instanceof UIGenerator || dat instanceof UIProvider || dat instanceof Identified) {
			thing = dat;
		}
		return new MessageResult("object_updated",getTypeName(),thing);
	}
	protected String getTypeName() {
		return getFactory().getTag();
	}

}