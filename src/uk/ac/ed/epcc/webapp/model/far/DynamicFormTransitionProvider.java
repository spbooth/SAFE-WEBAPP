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

import java.util.LinkedList;
import java.util.Map;

import org.apache.batik.parser.PathParser;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.UnusedNameInput;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.CustomPageResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraTargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.forms.CreateTransition;
import uk.ac.ed.epcc.webapp.model.data.transition.AbstractViewTransitionProvider;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.PartManager.Part;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** {@link TransitionProvider} for manipulating {@link DynamicForm} objects.
 * 
 * @author spb
 *
 */

public class DynamicFormTransitionProvider<T extends DynamicForm> extends
		AbstractViewTransitionProvider<T, DynamicFormTransitionKey<T>> implements IndexTransitionProvider<DynamicFormTransitionKey<T>, T> {
	private final DynamicFormManager<T> manager;
	private final String target_name;

	public class Linker implements UIGenerator{
		public Linker(T target) {
			super();
			this.target = target;
		}

		private final T target;

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
		 */
		@Override
		public ContentBuilder addContent(ContentBuilder builder) {
			builder.addLink(target.getContext(), target.getName(), new ViewResult(target));
			return builder;
		}
	}
	public final DynamicFormTransitionKey<T> CREATE = new DynamicFormTransitionKey<T>("Create","Create a new Dynamic form") {

		@Override
		public boolean allow(DynamicForm target, SessionService<?> sess) {
			return target == null && manager.canEdit(sess);
		}
	};
	public final DynamicFormTransitionKey<T> INDEX = new DynamicFormTransitionKey<T>("Index","List existing Dynamic Forms") {

		@Override
		public boolean allow(DynamicForm target, SessionService<?> sess) {
			return target == null && manager.canEdit(sess);
		}
	};
	public final DynamicFormTransitionKey<T> ADD = new DynamicFormTransitionKey<T>("Add","Add a child page") {

		@Override
		public boolean allow(DynamicForm target, SessionService<?> sess) {
			return target != null && target.canEdit(sess) && ! target.isFrozen();
		}
	};
	
	public class Creator extends CreateTransition<T>{

		/**
		 * @param name
		 * @param fac
		 */
		protected Creator() {
			super("DynamicForm", manager);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.forms.CreateTemplate#getResult(java.lang.String, uk.ac.ed.epcc.webapp.model.data.DataObject, uk.ac.ed.epcc.webapp.forms.Form)
		 */
		@Override
		public FormResult getResult(String type_name, T dat, Form f) {
			return new ViewResult(dat);
		}

		@Override
		protected Map<String, Object> getSelectors() {
			Map<String, Object> selectors = super.getSelectors();
			selectors.put(DynamicFormManager.NAME_FIELD,new UnusedNameInput((DynamicFormManager) getFactory()));
			return selectors;
		}
		
	}
	public class IndexTransition extends AbstractTargetLessTransition<T> implements ExtraTargetLessTransition<T>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, AppContext c) throws TransitionException {
			f.addAction("New Form", new FormAction() {
				
				@Override
				public FormResult action(Form f) throws ActionException {
					return new ChainedTransitionResult<T, DynamicFormTransitionKey<T>>(DynamicFormTransitionProvider.this, null, CREATE);
				}
			});
			
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.ExtraContent#getExtraHtml(uk.ac.ed.epcc.webapp.content.ContentBuilder, uk.ac.ed.epcc.webapp.session.SessionService, java.lang.Object)
		 */
		@Override
		public <X extends ContentBuilder> X getExtraHtml(X cb,
				SessionService<?> op, T target) {
			try{
				cb.addHeading(2, "Active Dynamic Forms");
				LinkedList<Linker> list = new LinkedList<Linker>();
				for(T f : manager.getActive()){
					list.add(new Linker(f));
				}
				if( list.size() > 0){
					cb.addList(list);
				}else{
					cb.addText("No active dynamic forms");
				}
			}catch(Exception e){
				getLogger().error("Exception making index",e);
			}
			return cb;
		}
		
	}

	public class CreateChildTransition extends AbstractFormTransition<T>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, T target, AppContext conn)
				throws TransitionException {
			try {
				manager.getChildManager().getChildCreator(target).buildCreationForm("Part", f);
			} catch (Exception e) {
				getLogger().error("Problem building form",e);
				throw new TransitionException("Internal error");
			}
		}
		
	}
	public DynamicFormTransitionProvider(String target_name,DynamicFormManager<T> manager) {
		super(manager.getContext());
		this.target_name=target_name;
		this.manager=manager;
		addTransition(CREATE, new Creator());
		addTransition(INDEX, new IndexTransition());
		addTransition(ADD, new CreateChildTransition());
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#canView(java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public boolean canView(T target, SessionService<?> sess) {
		if( target == null ){
			return manager.canEdit(sess);
		}
		return target.canEdit(sess);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getTargetName()
	 */
	@Override
	public String getTargetName() {
		return target_name;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#allowTransition(uk.ac.ed.epcc.webapp.AppContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean allowTransition(AppContext c, T target,
			DynamicFormTransitionKey<T> key) {
		return key.allow(target, c.getService(SessionService.class));
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getSummaryContent(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object)
	 */
	@Override
	public <X extends ContentBuilder> X getSummaryContent(AppContext c, X cb,
			T target) {
		if( target != null){
			cb.addHeading(2, target.getName());
		}else{
			
		}
		cb.addText("Summary Content");
		return cb;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider#getTarget(java.lang.String)
	 */
	@Override
	public T getTarget(String id) {
		try {
			if( id == null ){
				return null;
			}
			return manager.find(Integer.parseInt(id));
		} catch (Exception e) {
			return null;
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider#getID(java.lang.Object)
	 */
	@Override
	public String getID(T target) {
		if( target == null ){
			return null;
		}
		return Integer.toString(target.getID());
	}
	@Override
	public <X extends ContentBuilder> X getTopContent(X cb, T target,
			SessionService<?> sess) {
		cb.addLink(getContext(), "Active form list", new ChainedTransitionResult<T, DynamicFormTransitionKey<T>>(this, null, INDEX));
		return cb;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionFactory#getIndexTransition()
	 */
	@Override
	public DynamicFormTransitionKey<T> getIndexTransition() {
		return INDEX;
	}
	@Override
	public <X extends ContentBuilder> X getLogContent(X cb, T target,
			SessionService<?> sess) {
		cb.addHeading(2, target.getName());
		PartManager pm = manager.getChildManager();
		if( pm != null ){
			
			
			try {
				Table t = pm.getPartTable(target);
				if( t.hasData()){
					cb.addHeading(3,"Form pages");
					cb.addTable(getContext(), t);
				}else{
					cb.addText("No content");
				}
			} catch (DataFault e) {
				getLogger().error("Error getting child table",e);
			}
		}
		return cb;
	}
	
	

}