// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.data.forms;


import java.util.Iterator;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFormFactory;
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
	private static final String UPDATE = " Update ";

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

	@SuppressWarnings("unchecked")
	public final void buildUpdateForm(String type_name, Form f, BDO dat, SessionService<?> operator)
			throws DataFault {
			    buildForm(f);
				customiseForm(f);
				for(TableStructureContributer<BDO> contrib : factory.getTableStructureContributers()){
					contrib.customiseUpdateForm(f, dat, operator);
				}
				f.setContents(getDefaults());
				f.addAction(UPDATE, new UpdateAction<BDO>(type_name,this, dat));
				if (dat instanceof Retirable ){
					if(((Retirable) dat).canRetire()) {
						f.addAction(" Retire ", new RetireAction(type_name, dat));
					}else if( dat instanceof UnRetirable && ((UnRetirable)dat).canRestore()){
						f.addAction(" UnRetire ", new UnRetireAction(type_name, dat));
					}
				}
				customiseUpdateForm(f, dat);
				if( dat != null ){
					//this should never be called with dat null except from a unit test
				   f.setContents(dat.getMap());
				}
			}

	public void postUpdate(BDO o, Form f, Map<String,Object> origs)
			throws DataException {

	}

	public FormResult getResult(String typeName, BDO dat, Form f) {
		return new MessageResult("object_updated",typeName);
	}

}