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
package uk.ac.ed.epcc.webapp.model.far.response;

import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalInput;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.CustomFormContent;
import uk.ac.ed.epcc.webapp.forms.transition.FormTransition;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.far.AbstractPartTransitionProvider;
import uk.ac.ed.epcc.webapp.model.far.PageManager;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.PageManager.Page;
import uk.ac.ed.epcc.webapp.model.far.PartManager.Part;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.SectionManager;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 * @param <D> 
 * @param <R> 
 *
 */

public class ResponseTransitionProvider<D extends DynamicForm,R extends Response<D>> extends
	AbstractPartTransitionProvider<ResponseTarget<D, R>,ResponseTransitionKey<D, R>>  {

	private final ResponseManager<R, D> manager;
	
	public class ChainedResult extends ChainedTransitionResult<ResponseTarget<D, R>, ResponseTransitionKey<D, R>>{
		public ChainedResult(ResponseTarget<D,R> target, ResponseTransitionKey<D,R> key){
			super(ResponseTransitionProvider.this,target,key);
		}
	}
	public class EditResult extends ChainedResult{
		public EditResult(ResponseTarget<D,R> target){
			super(target,EDIT);
		}
	}
	
	public final ResponseTransitionKey<D, R> EDIT = new ResponseTransitionKey<D, R>("Edit", "Edit questions in this section"){

		@Override
		public boolean allow(ResponseTarget<D, R> target, SessionService<?> sess) {
			return super.allow(target, sess) && target.getPart() instanceof Section;
		}
		
	};
	public final ResponseTransitionKey<D, R> PREV = new ResponseTransitionKey<D, R>("<<<", "Go to previous"){

		@Override
		public boolean allow(ResponseTarget<D, R> target, SessionService<?> sess) {
			try {
				return super.allow(target, sess) && (target.getSibling(false) != null);
			} catch (DataFault e) {
				getLogger().error("Problem in allow", e);
				return false;
			}
		}
		
	};
	public final ResponseTransitionKey<D, R> UP = new ResponseTransitionKey<D, R>("Up", "Go to parent"){

		@Override
		public boolean allow(ResponseTarget<D, R> target, SessionService<?> sess) {
				return super.allow(target, sess) && (target.getParent() != null);
		}
		
	};
	public final ResponseTransitionKey<D, R> NEXT = new ResponseTransitionKey<D, R>(">>>", "Go to next"){

		@Override
		public boolean allow(ResponseTarget<D, R> target, SessionService<?> sess) {
			try {
				return super.allow(target, sess) && (target.getSibling(true) != null);
			} catch (DataFault e) {
				getLogger().error("Problem in allow", e);
				return false;
			}
		}
		
	};
	
	public final ResponseTransitionKey<D, R> SUBMIT = new ResponseTransitionKey<D, R>("Submit", "Submit form response"){

		@Override
		public boolean allow(ResponseTarget<D, R> target, SessionService<?> sess) {
			try {
				R rsp = target.getResponse();
				return super.allow(target, sess) && (target.getSibling(true) == null) && rsp.validate() && rsp.canEdit(sess);
			} catch (DataFault e) {
				getLogger().error("Problem in allow", e);
				return false;
			} catch (DataException e) {
				getLogger().error("Problem in allow", e);
				return false;
			}
		}
		
	};
	
	public class EditSectionTransition extends AbstractFormTransition<ResponseTarget<D, R>> implements CustomFormContent<ResponseTarget<D,R>>, FormTransition<ResponseTarget<D,R>>{

		public class CancelAction extends FormAction{
			/**
			 * @param target
			 */
			public CancelAction(ResponseTarget<D, R> target) {
				super();
				this.target = target;
			}

			private final ResponseTarget<D,R> target;

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
			 */
			@Override
			public FormResult action(Form f) throws ActionException {
				return new ViewResult(target.getParent());
			}

			@Override
			public String getHelp() {
				return "Cancel form submition and view parent";
			}
		}
		public class EditAction extends FormAction{
			/**
			 * @param target
			 */
			public EditAction(ResponseTarget<D, R> target) {
				super();
				this.target = target;
			}

			private final ResponseTarget<D,R> target;

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
			 */
			@Override
			public FormResult action(Form f) throws ActionException {
				try{
					R response = target.getResponse();
					Section s = (Section) target.getPart();
					SectionManager factory = (SectionManager) s.getFactory();
					QuestionManager man = (QuestionManager) factory.getChildManager();
					for( Question q : man.getParts(s)){
						response.setData(q, f.get(q.getName()));
					}
					Section next_sec = factory.getSibling(s, true);
					if( next_sec != null ){
						return new ChainedResult(new ResponseTarget<D, R>(response, next_sec),EDIT);
					}
					Page owner = s.getOwner();
					Page next_page = ((PageManager)owner.getFactory()).getSibling(owner, true);
					if( next_page != null ){
						Section first = factory.getFirst(next_page);
						if( first != null ){
							return new ChainedResult(new ResponseTarget<D, R>(response, first), EDIT);
						}
					}
					return new ChainedResult(new ResponseTarget<D, R>(response, owner),null);
				}catch(Exception e){
					throw new ActionException("Internal error",e);
				}
			}

			@Override
			public String getHelp() {
				return "Submit answers and go to next section";
			}
			
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, ResponseTarget<D, R> target,
				AppContext conn) throws TransitionException {
			try{
				R response = target.getResponse();
				Section s = (Section) target.getPart();
				QuestionManager man = (QuestionManager) s.getFactory().getChildManager();
				for( Question q : man.getParts(s)){
					Input input = q.getInput();
					if( input instanceof OptionalInput){
						((OptionalInput)input).setOptional(true);
					}
					input.setValue(response.getData(q));
					f.addInput(q.getName(), q.getQuestionText(), input);
				}
				f.addAction("Submit", new EditAction(target));
			}catch(Exception e){
				getLogger().error("Error building form",e);
				throw new TransitionException("Internal error");
			}
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.CustomFormContent#addFormContent(uk.ac.ed.epcc.webapp.content.ContentBuilder, uk.ac.ed.epcc.webapp.session.SessionService, uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object)
		 */
		@Override
		public <X extends ContentBuilder> X addFormContent(X cb,
				SessionService<?> op, Form f, ResponseTarget<D, R> target) {
			R response = target.getResponse();
			Section s = (Section) target.getPart();
			Page p = s.getOwner();
			SectionEditVisitor<X> vis = new SectionEditVisitor<X>(cb, op, response, f, s);
			vis.visitPage(p);
			return cb;
		}
		
		
		
	}
	public class SiblingTransition extends AbstractDirectTransition<ResponseTarget<D,R>>{
		/**
		 * @param up
		 */
		public SiblingTransition(boolean up) {
			super();
			this.up = up;
		}

		public final boolean up;

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(ResponseTarget<D, R> target, AppContext c)
				throws TransitionException {
			try {
				return new ViewResult(target.getSibling(up));
			} catch (DataFault e) {
				getLogger().error("Error getting sibling transition",e);
				throw new TransitionException("Internal error");
			}
		}	
	}
	public class ParentTransition extends AbstractDirectTransition<ResponseTarget<D,R>>{
		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(ResponseTarget<D, R> target, AppContext c)
				throws TransitionException {
				return new ViewResult(target.getParent());
		}	
	}
	
	public class SubmitTransition extends AbstractDirectTransition<ResponseTarget<D,R>>{
		
		public SubmitTransition() {
			super();
		}
		
				
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(ResponseTarget<D, R> target, AppContext c) 
				throws TransitionException {
			
			try {
				return target.getResponse().submit();
			} catch (Exception e) {
				getLogger().error("Error doing submission transition",e);
				throw new TransitionException("Internal error");
			}
		}	
	}
	
	public ResponseTransitionProvider(String target_name,ResponseManager<R,D> manager){
		super(target_name,manager.getManager());
		this.manager=manager;
		addTransition(PREV, new SiblingTransition(false));
		addTransition(UP, new ParentTransition());
		addTransition(EDIT, new EditSectionTransition());
		addTransition(NEXT, new SiblingTransition(true));
		addTransition(SUBMIT, new SubmitTransition());
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#allowTransition(uk.ac.ed.epcc.webapp.AppContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean allowTransition(AppContext c, ResponseTarget<D, R> target,
			ResponseTransitionKey<D, R> key) {
		return key.allow(target, c.getService(SessionService.class));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getSummaryContent(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object)
	 */
	@Override
	public <X extends ContentBuilder> X getSummaryContent(AppContext c, X cb,
			ResponseTarget<D, R> target) {
		return cb;
	}

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#canView(java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public boolean canView(ResponseTarget<D, R> target, SessionService<?> sess) {
		return target.getResponse().canView(sess);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProvider#getTarget(java.util.LinkedList)
	 */
	@Override
	public ResponseTarget<D, R> getTarget(LinkedList<String> id) {
		try {
			R response = manager.find(Integer.parseInt(id.pop()));
			Part part = getTarget(response.getForm(), form_manager.getChildManager(), id);
			return new ResponseTarget<D, R>(response, part);
		} catch (Exception e) {
			getLogger().error("Error getting target",e);
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProvider#getID(java.lang.Object)
	 */
	@Override
	public LinkedList<String> getID(ResponseTarget<D, R> target) {
		LinkedList<String> result = new LinkedList<String>();
		getID(result,target.getPart());
		result.pop();  // pop form-id we will replace with response.
		result.push(Integer.toString(target.getResponse().getID()));
		return result;
	}



	@Override
	public <X extends ContentBuilder> X getLogContent(X cb,
			ResponseTarget<D, R> target, SessionService<?> sess) {
		Part p = target.getPart();
		cb.addHeading(2, target.getResponse().getDescriptor());
		cb.addHeading(2, p.getTypeName() + ": " + p.getName());
		EditSectionVisitor<X> vis = new EditSectionVisitor<X>(cb, sess,p instanceof Page ? this: null,target.getResponse());
		target.getPart().visit(vis);		
		return cb;
	}

}