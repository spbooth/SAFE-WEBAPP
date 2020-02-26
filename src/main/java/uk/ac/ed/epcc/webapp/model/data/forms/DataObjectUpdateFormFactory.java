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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.content.UIProvider;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.Identified;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFormFactory;
import uk.ac.ed.epcc.webapp.model.data.RestrictedRetirable;
import uk.ac.ed.epcc.webapp.model.data.Retirable;
import uk.ac.ed.epcc.webapp.model.data.TableStructureContributer;
import uk.ac.ed.epcc.webapp.model.data.UnRetirable;
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

	public final void buildUpdateForm(String type_name, Form f, BDO dat, SessionService<?> operator)
			throws DataFault {
				HashMap fixtures = new HashMap();
				if( dat != null) {
					Map data = dat.getMap();
					for(String field : getSupress()) {
						fixtures.put(field,data.get(field));
					}
				}
			    boolean complete = buildForm(f,fixtures);
				//customiseForm(f);
			    
			    // set values before customise as it is common
				// for the customise methods to lock fields
				// and the values need to be in place before then.
			    //
			    // if buildForm is creating a multi-stage form then some of
			    // the fields may already be locked and should NOT
			    // be updated by the following
				f.setContents(getDefaults());
				if( dat != null ){
					//this should never be called with dat null except from a unit test
				   f.setContents(dat.getMap());
				}
				for(TableStructureContributer<BDO> contrib : factory.getTableStructureContributers()){
					contrib.customiseUpdateForm(f, dat, operator);
				}
				
				customiseUpdateForm(f, dat);
				
				if( complete ) {
					customiseCompleteUpdateForm(f, dat);
					f.addAction(getUpdateActionName(), new UpdateAction<>(type_name,this, dat));
					addRetireAction(type_name, f, dat, operator);
				}
				
				
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
	 * @param type_name
	 * @param f
	 * @param dat
	 * @param operator
	 */
	protected void addRetireAction(String type_name, Form f, BDO dat, SessionService<?> operator) {
		if (dat instanceof Retirable && ( ! (dat instanceof RestrictedRetirable)  || ((RestrictedRetirable)dat).allowRetire(operator))){
			if(((Retirable) dat).canRetire()) {
				f.addAction(RETIRE, new RetireAction(type_name, dat));
			}else if( dat instanceof UnRetirable && ((UnRetirable)dat).canRestore()){
				f.addAction(UN_RETIRE, new UnRetireAction(type_name, dat));
			}
		}
	}

	@Override
	public void postUpdate(BDO o, Form f, Map<String,Object> origs)
			throws DataException {
		compositesPostUpdate(o, f, origs);
	}

	/**
	 * @param o
	 * @param f
	 * @param origs
	 * @throws DataException
	 */
	protected void compositesPostUpdate(BDO o, Form f, Map<String, Object> origs) throws DataException {
		for(TableStructureContributer c : factory.getTableStructureContributers()){
			c.postUpdate(o, f, origs);
		}
	}

	@Override
	public FormResult getResult(String typeName, BDO dat, Form f) {
		Object thing = typeName;
		if( dat instanceof UIGenerator || dat instanceof UIProvider || dat instanceof Identified) {
			thing = dat;
		}
		return new MessageResult("object_updated",typeName,thing);
	}

}