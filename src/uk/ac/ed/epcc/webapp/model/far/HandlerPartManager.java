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
package uk.ac.ed.epcc.webapp.model.far;

import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.ClassInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalListInputWrapper;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.far.handler.FormHandler;
import uk.ac.ed.epcc.webapp.model.far.handler.PartConfigFactory;

/**
 * @author spb
 * @param <O> 
 * @param <X> 
 * @param <H> 
 *
 */

public abstract class HandlerPartManager<O extends PartOwner, X extends FormHandler,H extends HandlerPartManager.HandlerPart<O,X>> extends PartManager<O,H> {


	/**
	 * @param form_manager
	 * @param owner_fac
	 * @param part_tag
	 */
	public HandlerPartManager(DynamicFormManager<?> form_manager, PartOwnerFactory<O> owner_fac, String part_tag) {
		super(form_manager, owner_fac, part_tag);
	}

	/**
	 * 
	 */
	public static final String HANDLER_TYPE_FIELD = "HandlerType";
	
	public abstract static class HandlerPart<O extends PartOwner, X extends FormHandler> extends PartManager.Part<O> implements UIGenerator{

		
		@Override
		public void makeConfigForm(Form f) {
			try {
				getHandler().buildConfigForm(f);
			} catch (Exception e) {
				getLogger().error("Problem building config form",e);
			}
		}

		/**
		 * @param r
		 */
		protected HandlerPart(HandlerPartManager manager,Record r) {
			super(manager,r);
		}
		
		
		private X handler = null;
		@SuppressWarnings("unchecked")
		public final X getHandler() throws Exception{
			if( handler == null ){
				AppContext conn = getContext();
				String handlerTag = getHandlerTag();
				if( handlerTag == null || handlerTag.length() == 0){
					return null;
				}
				Class<? extends X> clazz = conn.getClassDef(((HandlerPartManager)getFactory()).getHandlerClass(), handlerTag);
				handler =  conn.makeObject(clazz);
			}
			return handler;
		}
		

		@Override
		public Map<String, Object> getInfo() {
			Map<String, Object> info = super.getInfo();
			info.put("Handler", getHandlerTag());
			return info;
		}

		protected final String getHandlerTag() {
			return record.getStringProperty(HANDLER_TYPE_FIELD);
		}
		final void setHandlerTag(String tag){
			record.setProperty(HANDLER_TYPE_FIELD, tag);
		}
		
		@Override
		public boolean hasConfig() {
			try {
				if( ! super.hasConfig()){
					return false;
				}
				X h = getHandler();
				if( h == null ){
					return false;
				}
				return h.hasConfig();
			} catch (Exception e) {
				getLogger().error("Error checking for config",e);
				return false;
			}
		}

		
	}
	
	
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = super.getDefaultTableSpecification(c, table);
		spec.setField(HANDLER_TYPE_FIELD, new StringFieldType(! requireHandler(), "", 128));
		return spec;
	}
	@Override
	protected PartConfigFactory<O, H> makeConfigFactory() {
		return new PartConfigFactory<>(this);
	}
	@Override
	protected Map<String, Object> getSelectors() {
		Map<String, Object> selectors = super.getSelectors();
		ListInput i = new ClassInput<>(getContext(), getHandlerClass());
		if( ! requireHandler()){
			i = new OptionalListInputWrapper(i);
		}
		selectors.put(HANDLER_TYPE_FIELD, i );
		return selectors;
	}
	
	@Override
	public H duplicate(O new_owner, H original) throws DataFault {
		H duplicate = super.duplicate(new_owner, original);
		duplicate.setHandlerTag(original.getHandlerTag());
		return duplicate;
	}
	protected abstract Class<X> getHandlerClass();
	
	protected boolean requireHandler(){
		return true;
	}
}