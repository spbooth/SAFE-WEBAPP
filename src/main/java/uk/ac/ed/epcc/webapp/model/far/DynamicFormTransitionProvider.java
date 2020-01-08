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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.FileUploadDecorator;
import uk.ac.ed.epcc.webapp.forms.inputs.NoSpaceFieldValidator;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.forms.inputs.UnusedNameInput;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ConfirmTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraTargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.forms.CreateTransition;
import uk.ac.ed.epcc.webapp.model.data.transition.AbstractViewTransitionProvider;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
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

	/** Key for targetless edit transitions.
	 * @author spb
	 *
	 */
	private static final class DynamicFormTargetlessKey<T extends DynamicForm> extends DynamicFormTransitionKey<T> {
		/**
		 * @param name
		 * @param help
		 */
		private DynamicFormTargetlessKey(String name, String help) {
			super(name, help);
		}

		@Override
		public boolean allow(DynamicForm target, SessionService<?> sess) {
			return target == null;
		}
	}
	/** key for form editing transitions.
	 * 
	 * @author spb
	 *
	 * @param <T>
	 */
	private static final class DynamicFormComposeKey<T extends DynamicForm> extends DynamicFormTransitionKey<T> {
		/**
		 * @param name
		 * @param help
		 */
		private DynamicFormComposeKey(String name, String help) {
			super(name, help);
		}

		@Override
		public boolean allow(DynamicForm target, SessionService<?> sess) {
			return target != null && target.isNew();
		}
	}
	/** key for transitions on active/released forms.
	 * 
	 * @author spb
	 *
	 * @param <T>
	 */
	private static final class DynamicFormActiveKey<T extends DynamicForm> extends DynamicFormTransitionKey<T> {
		/**
		 * @param name
		 * @param help
		 */
		private DynamicFormActiveKey(String name, String help) {
			super(name, help);
		}

		@Override
		public boolean allow(DynamicForm target, SessionService<?> sess) {
			return target != null && target.isActive();
		}
	}
	/** key for transitions on any forms.
	 * 
	 * @author spb
	 *
	 * @param <T>
	 */
	private static final class DynamicFormAnyKey<T extends DynamicForm> extends DynamicFormTransitionKey<T> {
		/**
		 * @param name
		 * @param help
		 */
		private DynamicFormAnyKey(String name, String help) {
			super(name, help);
		}

		@Override
		public boolean allow(DynamicForm target, SessionService<?> sess) {
			return target != null;
		}
	}
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
	public static final DynamicFormTransitionKey CREATE = new DynamicFormTargetlessKey("Create", "Create a new Dynamic form");
	public static final DynamicFormTransitionKey INDEX = new DynamicFormTargetlessKey("Index","List existing Dynamic Forms"); 
	public static final DynamicFormTransitionKey ADD = new DynamicFormComposeKey("Add","Add a child page"); 
	
	public static final DynamicFormTransitionKey RENEW = new DynamicFormActiveKey("Renew","Change the form status to New");
	
	public static final DynamicFormTransitionKey ACTIVATE = new DynamicFormComposeKey("Activate","Change the form status to Active");
	
	public static final DynamicFormTransitionKey RETIRE = new DynamicFormActiveKey("Retire","Change the form status to Retire");
	public static final DynamicFormTransitionKey CLONE = new DynamicFormAnyKey<>("Clone", "Generate a complete copy of this form in New state");
	public static final DynamicFormTransitionKey DOWNLOAD = new DynamicFormAnyKey<>("Download", "Download a XML description of this form");
	public static final DynamicFormTransitionKey UPLOAD = new DynamicFormComposeKey("Upload","Upload a XML description and add to this form"); 
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
			UnusedNameInput input = new UnusedNameInput((DynamicFormManager) getFactory());
			input.setTrim(true);
			input.addValidator(new NoSpaceFieldValidator());
			selectors.put(DynamicFormManager.NAME_FIELD,input);
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
				
				
				cb.addHeading(2, manager.getTag() + " List");
				
				cb.addHeading(3, "New");
				LinkedList<Linker> new_list = new LinkedList<>();
				for(T f : manager.getNew()){
					new_list.add(new Linker(f));
				}
				if( new_list.size() > 0){
					cb.addList(new_list);
				}else{
					cb.addText("No new forms");
				}
				
				cb.addHeading(3, "Active");
				LinkedList<Linker> active_list = new LinkedList<>();
				for(T f : manager.getActive()){
					active_list.add(new Linker(f));
				}
				if( active_list.size() > 0){
					cb.addList(active_list);
				}else{
					cb.addText("No active forms");
				}
				
				cb.addHeading(3, "Retired");
				LinkedList<Linker> retired_list = new LinkedList<>();
				for(T f : manager.getRetired()){
					retired_list.add(new Linker(f));
				}
				if( retired_list.size() > 0){
					cb.addList(retired_list);
				}else{
					cb.addText("No retired forms");
				}
			}catch(Exception e){
				getLogger().error("Exception making index",e);
			}
			return cb;
		}
		
	}
	public class DuplicateTransition extends AbstractFormTransition<T>{
		public class DuplicateAction extends FormAction{

			public DuplicateAction(T original) {
				super();
				this.original = original;
			}
			private final T original;
			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
			 */
			@Override
			public FormResult action(Form f) throws ActionException {
				String name = (String) f.get(DynamicFormManager.NAME_FIELD);
				T duplicate;
				try {
					duplicate = manager.makeBDO();
					duplicate.setName(name);
					duplicate.commit();
					
					DuplicateVisitor vis = new DuplicateVisitor(duplicate);
					vis.visitOwner(manager, original);
					
					return duplicate.getViewResult();
				} catch (DataException e) {
					throw new ActionException("Error in duplicate", e);
				}
			}
			
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, T target, AppContext conn) throws TransitionException {
			UnusedNameInput input = new UnusedNameInput(manager);
			input.setTrim(true);
			input.addValidator(new NoSpaceFieldValidator());
			f.addInput(DynamicFormManager.NAME_FIELD, "New Form Name", input);
			f.addAction("Clone", new DuplicateAction(target));
			
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
	
	public class AddXMLTransition extends AbstractFormTransition<T>{

		/**
		 * 
		 */
		private static final String DATA = "data";

		public class XMLUploadAction extends FormAction{
			public XMLUploadAction(T target) {
				super();
				this.target = target;
			}
			private final T target;
			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
			 */
			@Override
			public FormResult action(Form f) throws ActionException {
				XMLFormReader<T, PartOwnerFactory<T>> reader = new XMLFormReader<>(getContext());
				try {
					reader.read(target.getManager(), target, (String)f.get(DATA));
				} catch (Exception e) {
					throw new ActionException("Error parsing upload",e);
				} 
				return target.getViewResult();
			}
			
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, T target, AppContext conn)
				throws TransitionException {
			TextInput input = new TextInput();
			input.setMaxResultLength(8*1024*1024);
			f.addInput(DATA, "Upload XML Data", new FileUploadDecorator(input));
			f.addAction("Add", new XMLUploadAction(target));
		}
		
	}
	public class DownloadTransition extends AbstractDirectTransition<T>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(T target, AppContext c) throws TransitionException {
			LinkedList<String> args = new LinkedList<>();
			args.add(Integer.toString(target.getID()));
			return new ServeDataResult(manager, args);
		}
		
	}
	public DynamicFormTransitionProvider(String target_name,DynamicFormManager<T> manager) {
		super(manager.getContext());
		this.target_name=target_name;
		this.manager=manager;
		addTransition(CREATE, new Creator());
		addTransition(INDEX, new IndexTransition());
		addTransition(ADD, new CreateChildTransition());
		addTransition(DOWNLOAD, new DownloadTransition());
		addTransition(UPLOAD, new AddXMLTransition());
		addTransition(CLONE, new DuplicateTransition());
		addTransition(RENEW, new ConfirmTransition<>("Re-editing a active form may corrupt existing responses. Consider retiring and cloning the form instead. Do you really want to re-edit?", new RenewTransition(),new ViewTransition()));
		addTransition(ACTIVATE, new ActivateTransition());
		addTransition(RETIRE, new RetireTransition());
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#canView(java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public boolean canView(T target, SessionService<?> sess) {
		if( target == null ){
			return manager.canEdit(sess);
		}
		return target.canView(sess);
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
		SessionService service = c.getService(SessionService.class);
		return key.allow(target, service) && manager.canEdit(service);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getSummaryContent(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object)
	 */
	@Override
	public <X extends ContentBuilder> X getSummaryContent(AppContext c, X cb,
			T target) {
		if( target != null){
			cb.addHeading(2, target.getName());
		}
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
		cb.addLink(getContext(), manager.getTag() + " List", new ChainedTransitionResult<T, DynamicFormTransitionKey<T>>(this, null, INDEX));
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
		
		DynamicFormManager<T> tm = manager;
		
		PartManager pm = manager.getChildManager();
		if( pm != null ){
			try {
				Table t = pm.getPartTable(target);
				if( t.hasData()){
					AppContext c = getContext();
										
					cb.addHeading(3,"Form pages");
					cb.addTable(c, t);
								
				}else{
					cb.addText("No content");
				}
			} catch (DataFault e) {
				getLogger().error("Error getting child table",e);
			}
		}
		
		
		return cb;
	}
	
	
	
	
    public class RenewTransition extends AbstractDirectTransition<T>{
		
		@Override
		public FormResult doTransition(T target, AppContext c) 
				throws TransitionException {
			
			try {
				return target.renew();
			} catch (Exception e) {
				getLogger().error("Error doing renew transition",e);
				throw new TransitionException("Internal error");
			}
			
		}
		
	}
public class ViewTransition extends AbstractDirectTransition<T>{
		
		@Override
		public FormResult doTransition(T target, AppContext c) 
				throws TransitionException {
			
			try {
				return target.getViewResult();
			} catch (Exception e) {
				getLogger().error("Error doing renew transition",e);
				throw new TransitionException("Internal error");
			}
			
		}
		
	}
    
    public class ActivateTransition extends AbstractDirectTransition<T>{
		
		@Override
		public FormResult doTransition(T target, AppContext c) 
				throws TransitionException {
			
			try {
				return target.activate();
			} catch (Exception e) {
				getLogger().error("Error doing activate transition",e);
				throw new TransitionException("Internal error");
			}
	
		}
	
	}
        
    
    public class RetireTransition extends AbstractDirectTransition<T>{
		
		@Override
		public FormResult doTransition(T target, AppContext c) 
				throws TransitionException {
			
			try {
				return target.retire();
			} catch (Exception e) {
				getLogger().error("Error doing retire transition",e);
				throw new TransitionException("Internal error");
			}
			
		}
	
	}
        
}