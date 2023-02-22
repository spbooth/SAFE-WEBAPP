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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.Button;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.factory.EditFormBuilder;
import uk.ac.ed.epcc.webapp.forms.factory.EditTransition;
import uk.ac.ed.epcc.webapp.forms.inputs.FileUploadDecorator;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ConfirmTransition;
import uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProvider;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.PartManager.Part;
import uk.ac.ed.epcc.webapp.model.far.handler.PartConfigFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link PathTransitionProvider} for editing the components of a {@link DynamicForm}
 * @author spb
 *
 */

public class PartPathTransitionProvider<O extends PartOwner,T extends PartManager.Part<O>> extends AbstractPartTransitionProvider<T, PartTransitionKey<T>>
		implements PathTransitionProvider<PartTransitionKey<T>, T> {
	/**
	 * 
	 */
	private static final String MOVE_DOWN_COL = "\u21d3";
	/**
	 * 
	 */
	private static final String MOVE_UP_COL = "\u21d1";
	public static final PartTransitionKey CREATE = new PartTransitionKey("Add","Add a child element") {

		@Override
		public boolean allow(Part target, SessionService sess) {
			if( target == null ){
				return false;
			}
			DynamicForm f = target.getForm();
			if( f.canEdit(sess)  && ! f.isActive()){
				if( target.getFactory().getChildManager() == null ){
					// bottom factory of the stack.
					return false;
				}
				return true;
			}
			return false;
		}
	};
	public static final PartTransitionKey DELETE = new EditKey("Delete", "Remove this part and all its contents");
	public static final PartTransitionKey PREV = new CheckSiblingKey("<<<", "Go to previous sibling",false);
	public static final PartTransitionKey NEXT = new CheckSiblingKey(">>>","Go to next sibling",true);
	public static final PartTransitionKey MOVE_UP = new CheckSiblingKey("Move up", "Swap position with previous sibling",false);
	public static final PartTransitionKey MOVE_DOWN = new CheckSiblingKey("Move down","Swap position with next sibling",true); 
	public static final PartTransitionKey EDIT = new EditKey("Update", "Edit/update this part");
	public static final PartTransitionKey PARENT = new ViewKey("Parent", "Go to parent object");
	public static final PartTransitionKey DOWNLOAD = new ViewKey("Download", "Download this part as XML");
	public static final PartTransitionKey UPLOAD = new EditKey("Upload", "Upload a XML description and add to this part");

	public static final PartTransitionKey CONFIG = new EditKey("Configure", "Edit configuration"){
		public boolean allow(Part target, SessionService sess) {
			return super.allow(target,sess) && target.hasConfig();
		}
		
	};
	/** A {@link PartTransitionKey} for edit operations on {@link Part}s of a new {@link DynamicForm}.
	 * @author spb
	 *
	 */
	private static class EditKey extends PartTransitionKey {
		/**
		 * @param name
		 * @param help
		 */
		private EditKey(String name, String help) {
			super(name, help);
		}

		@Override
		public boolean allow(Part target, SessionService sess) {
			if( target == null ){
				return false;
			}
			DynamicForm f = target.getForm();
			if( f.canEdit(sess)  && ! f.isActive()){
				return true;
			}
			return false;
		}
	}
	/** A {@link PartTransitionKey} for view operations on a {@link DynamicForm}.
	 * @author spb
	 *
	 */
	private static class ViewKey extends PartTransitionKey {
		/**
		 * @param name
		 * @param help
		 */
		private ViewKey(String name, String help) {
			super(name, help);
		}

		@Override
		public boolean allow(Part target, SessionService sess) {
			if( target == null ){
				return false;
			}
			DynamicForm f = target.getForm();
			if( f.canView(sess)  ){
				return true;
			}
			return false;
		}
	}
	
	/**
	 * @author spb
	 *
	 */
	public static final class CheckSiblingKey extends PartTransitionKey {
		private final boolean go_up;
		/**
		 * @param name
		 * @param help
		 */
		public CheckSiblingKey(String name, String help, boolean go_up) {
			super(name, help);
			this.go_up=go_up;
		}

		@Override
		public boolean allow(Part target, SessionService sess) {
			if( target == null ){
				return false;
			}
			DynamicForm f = target.getForm();
			if( f.canView(sess) ){
				Part sibling=null;
				try {
					sibling = target.getFactory().getSibling(target, go_up);
				} catch (DataFault e) {
					sess.getContext().getService(LoggerService.class).getLogger(getClass()).error("Error getting sibling",e);
				}
				return sibling != null ;
			}
			return false;
		}
	}
	public class GotoSiblingTransition extends AbstractDirectTransition<T>{
		public GotoSiblingTransition(boolean go_up) {
			super();
			this.go_up = go_up;
		}

		private final boolean go_up;

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(T target, AppContext c)
				throws TransitionException {
			try {
				return getPartManager(target).getSibling(target, go_up).getViewResult();
			} catch (Exception e) {
				getLogger().error("Problem doing sibling navigation",e);
				throw new TransitionException("Internal error");
			}
		}
	}
	public class GotoParentTransition extends AbstractDirectTransition<T>{
		public GotoParentTransition() {
			super();
		}

		

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(T target, AppContext c)
				throws TransitionException {
			try {
				return target.getOwner().getViewResult();
			} catch (Exception e) {
				getLogger().error("Problem doing parent navigation",e);
				throw new TransitionException("Internal error");
			}
		}
	}
	public class SwapSiblingTransition extends AbstractDirectTransition<T>{
		public SwapSiblingTransition(boolean go_up) {
			super();
			this.go_up = go_up;
		}

		private final boolean go_up;

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(T target, AppContext c)
				throws TransitionException {
			try {
				Part sibling = getPartManager(target).getSibling(target, go_up);
				int sib_order = sibling.getSortOrder().intValue();
				int target_order = target.getSortOrder().intValue();
				sibling.setSortOrder(target_order);
				target.setSortOrder(sib_order);
				sibling.commit();
				target.commit();
				return target.getOwner().getViewResult();
			} catch (Exception e) {
				getLogger().error("Problem doing permutation",e);
				throw new TransitionException("Internal error");
			}
		}
	}
	public class CreateChildTransition extends AbstractFormTransition<T>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, T target,
				AppContext conn) throws TransitionException {
			try {
				target.getFactory().getChildManager().getChildCreator(target).buildCreationForm("Part", f);
			} catch (Exception e) {
				getLogger().error("Problem building form",e);
				throw new TransitionException("internal error");
			}
		}
		
	}
	public class DeleteChildTransition extends AbstractDirectTransition<T>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(T target, AppContext c)
				throws TransitionException {
			PartOwner owner = target.getOwner();
			try {
				getPartManager(target).deletePart(target);
			} catch (DataFault e) {
				getLogger().error("Problem deleting record", e);
				throw new TransitionException("Internal error");
			}
			return owner.getViewResult();
		}
		
	}
	public class EditPartTransition extends EditTransition<T>{

		public class CancelAction extends FormAction{
			public CancelAction(T target) {
				super();
				this.target = target;
				setMustValidate(false);
			}
			private final T target;
			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
			 */
			@Override
			public FormResult action(Form f) throws ActionException {
				return target.getViewResult();
			}
			
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.factory.EditTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, T dat, AppContext c) throws TransitionException {
			super.buildForm(f, dat, c);
			f.addAction("Cancel", new CancelAction(dat));
		}

		
		public EditPartTransition() {
			super("Part");
			
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.factory.EditTransition#getUpdate(uk.ac.ed.epcc.webapp.AppContext, java.lang.Object)
		 */
		@Override
		public EditFormBuilder<T> getUpdate(AppContext c, T dat) {
			// This will be a StandAloneFormUpdate so will implement EditFormBuilder
			return (EditFormBuilder<T>) dat.getFactory().getFormUpdate(c);
		}
		
	}
	public class ConfigTransition extends AbstractFormTransition<T>{
		public class ConfigAction extends FormAction{
			/**
			 * @param config_fac
			 * @param target
			 */
			public ConfigAction(PartConfigFactory<?, T> config_fac, T target) {
				super();
				this.config_fac = config_fac;
				this.target = target;
			}

			private final PartConfigFactory<?, T> config_fac;
			private final T target;

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
			 */
			@Override
			public FormResult action(Form f) throws ActionException {
				Map<String,Object> map = new LinkedHashMap<>();
				for(Iterator<String> it = f.getFieldIterator() ; it.hasNext();){
					String field = it.next();
					Object val = f.get(field);
					if( val != null ){
						map.put(field,val);
					}
				}
				try {
					config_fac.setValues(target, map);
				} catch (DataException e) {
					throw new ActionException("Problem setting config", e);
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
			PartConfigFactory<?, T> config_fac = getPartManager(target).getConfigFactory();
			target.makeConfigForm(f);
			try {
				f.setContents(config_fac.getValues(target));
			} catch (DataFault e) {
				getLogger().error("problem setting old values",e);
			}
			f.addAction("Update", new ConfigAction(config_fac, target));
		}
		
	}
	
	public class DownloadTransition extends AbstractDirectTransition<T>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(T target, AppContext c) throws TransitionException {
			LinkedList<String> args = getID(target);
			return new ServeDataResult(target.getFactory().form_manager, args);

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
					reader.read((PartOwnerFactory<T>) target.getFactory(), target, (String)f.get(DATA));
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
	public PartPathTransitionProvider(String target_name,DynamicFormManager man){
		super(target_name,man);
		
		addTransition(PREV, new GotoSiblingTransition(false));
		addTransition(MOVE_UP, new SwapSiblingTransition(false));
		addTransition(CREATE, new CreateChildTransition());
		addTransition(EDIT, new EditPartTransition());
		addTransition(CONFIG, new ConfigTransition());
		addTransition(DOWNLOAD, new DownloadTransition());
		addTransition(UPLOAD, new AddXMLTransition());
		addTransition(PARENT, new GotoParentTransition());
		addTransition(DELETE, new ConfirmTransition<>("Do you wish to delete this element and all its children?", new DeleteChildTransition(), new ViewTransition()));
		addTransition(MOVE_DOWN, new SwapSiblingTransition(true));
		addTransition(NEXT, new GotoSiblingTransition(true));
		
	}

	

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#allowTransition(uk.ac.ed.epcc.webapp.AppContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean allowTransition(AppContext c, T target,
			PartTransitionKey<T> key) {
		return key.allow(target, c.getService(SessionService.class));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getSummaryContent(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object)
	 */
	@Override
	public <X extends ContentBuilder> X getSummaryContent(AppContext c, X cb,
			T target) {
		cb.addHeading(2, getPartManager(target).getPartTag()+" "+target.getName());
		
		return cb;
	}

	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProvider#getTarget(java.util.LinkedList)
	 */
	@Override
	public T getTarget(LinkedList<String> id) {
		LinkedList<String> path = new LinkedList<>(id);
		DynamicForm root;
		try {
			root = (DynamicForm) form_manager.find(Integer.parseInt(path.pop()));
		} catch (Exception e) {
			return null;
		}
		if( root == null){
			return null;
		}
		return (T) getTarget(root,form_manager.getChildManager(),path);
	}

	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProvider#getID(java.lang.Object)
	 */
	@Override
	public LinkedList<String> getID(T target) {
		LinkedList<String> id = new LinkedList<>();
		return getID(id,target);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#canView(java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public boolean canView(T target, SessionService<?> sess) {
		return target.getForm().canView(sess);
	}


	@Override
	public <X extends ContentBuilder> X getTopContent(X cb, T target,
			SessionService<?> sess) {
		return addBreadcrumb(cb, target.getOwner());
	}

	@Override
	public <X extends ContentBuilder> X getLogContent(X cb, T target,
			SessionService<?> sess) {
		cb.addHeading(2, target.getSpacedName());
		Table t = new Table();
		Map<String,Object> info = target.getInfo();
		if( info.size() > 0 ){
			t.addMap("Values", info);
		}
		PartConfigFactory<O, T> config = getPartManager(target).getConfigFactory();
		if( config != null ){
			try {
				t.addMap("Values", config.getValues(target));
			} catch (DataFault e) {
				getLogger().error("error getting config settings");
			}
		}
		if( t.hasData()){
			t.setKeyName("Property");
			cb.addColumn(getContext(), t, "Values");
		}
		Table tc=null;
		try {
			tc = getPartManager(target).getChildTable(target);
		} catch (DataFault e) {
			getLogger().error("Error making child table",e);
		}
		if( tc != null ){
			String childTypeName = getPartManager(target).getChildTypeName()+"s";
			cb.addHeading(3, childTypeName);
			if( tc.hasData()){
				cb.addTable(getContext(), tc);
			}else{
				
				cb.addText("No "+childTypeName);
			}
		}
		return cb;
	}





	/**
	 * @param target
	 * @return
	 */
	protected PartManager<O, T> getPartManager(T target) {
		return (PartManager<O, T>) target.getFactory();
	}
	
	public void addMoveButtons(Table t,T part,SessionService<?> sess){
		
		if ( MOVE_UP.allow(part, sess)){
			t.put(MOVE_UP_COL, part, new Button(getContext(), "\u21d1", new ChainedTransitionResult<T, PartTransitionKey<T>>(this, part, MOVE_UP)));
		}else{
			t.put(MOVE_UP_COL, part, "");
		}
		if ( MOVE_DOWN.allow(part, sess)){
			t.put(MOVE_DOWN_COL, part, new Button(getContext(), "\u21d3", new ChainedTransitionResult<T, PartTransitionKey<T>>(this, part, MOVE_DOWN)));
		}else{
			t.put(MOVE_DOWN_COL, part, "");
		}
		t.getCol(MOVE_UP_COL).addAttribute("class", "button-col");
		t.getCol(MOVE_DOWN_COL).addAttribute("class", "button-col");
	}
	
	
}