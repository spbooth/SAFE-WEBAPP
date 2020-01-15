//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.action.ConfirmMessage;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionValidationException;
import uk.ac.ed.epcc.webapp.forms.html.ErrorFormResult;
import uk.ac.ed.epcc.webapp.forms.html.ErrorProcessingFormAction;
import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.ConfirmTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTransitionVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.FormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.ValidatingFormTransition;
import uk.ac.ed.epcc.webapp.logging.Logger;


public class ServletTransitionVisitor<K,T> extends AbstractTransitionVisitor<K,T>{
 		
 		private final HttpServletRequest req;
 		
 		private final Map params;
 
 	
 		public ServletTransitionVisitor(AppContext c, HttpServletRequest request,
 		K tag, TransitionFactory<K, T> tp, T target, Map params){
 			super(c,tag,tp,target);
 			
 			this.req=request;
 			this.params=params;
 		}
 		@SuppressWarnings("unchecked")
		public FormResult doBaseFormTransition(BaseFormTransition<T> ft) throws TransitionException{
			if( target == null ){
 				throw new TransitionException("No target specified");
 			}
			String transition_form = (String) params.get("transition_form");
			
			if( transition_form == null){
				// have to go the the transition page to
				// show the form
				// but its easier if all main forms post to here first
				return new ChainedTransitionResult<>(provider,target,tag);
			}
//			 actually process the results of a transition
			HTMLForm f = new HTMLForm(conn);
		
		    ft.buildForm(f, target, conn);
		    try{
		    	FormAction shortcut = f.getShortcutAction(params);
		    	if( shortcut != null ){
		    		ConfirmMessage confirm_action = shortcut.getConfirmMessage(f);
		    		if( confirm_action != null ){
		    			FormResult result = confirmTransition(req, conn, provider, tag, target,confirm_action.getMessage(),confirm_action.getArgs());
						if( result != null ){
							return result;
						}
						
					}
		    		return shortcut.action(f);
		    	}
		    	if (! f.parsePost(req)){
		    		// Not all ok
		    		FormAction action = f.getAction(f.locateAction(params));
		    		if( action instanceof ErrorProcessingFormAction){
		    			return ((ErrorProcessingFormAction<T, K>)action).processError(getContext(), f, provider, target, tag, HTMLForm.getMissing(req), HTMLForm.getErrors(req));
		    		}
		    		return new ErrorFormResult<>(provider, target, tag, HTMLForm.getErrors(req), HTMLForm.getMissing(req));
		    		
		    	}

		    	ConfirmMessage confirm_action = f.mustConfirm(params);

				if( confirm_action != null ){
					FormResult result = confirmTransition(req, conn, provider, tag, target,confirm_action.getMessage(),confirm_action.getArgs());
					if( result != null ){
						return result;
					}
				}
			
				return  f.doAction(params);
		    }catch( TransitionValidationException te){
		    	if( te.getField() != null ) {
		    		HTMLForm.addFieldError(te.getField(), te.getMessage(), req);
		    	}else {
		    		// show these as general form errors re-displaying form.
		    		HTMLForm.addGeneralError(te.getMessage(), req);
		    	}
				return new ErrorFormResult<>(provider, target, tag, HTMLForm.getErrors(req), HTMLForm.getMissing(req));
			}catch(TransitionException e){
				// if it is a transition exception we have a message for the user
				throw e;
			}catch(Exception e){
				getLogger().error("Error performing FormTransition",e);
				throw new TransitionException("Operation failed");
			}
		}
		@Override
		@SuppressWarnings("unchecked")
		public FormResult doTargetLessTransition(TargetLessTransition<T> ft) throws TransitionException{
			String transition_form = (String) params.get("transition_form");
			
			if( transition_form == null){
				// have to go the the transition page to
				// show the form
				// but its easier if all main forms post to here first
				return new ChainedTransitionResult<>(provider,null,tag);
			}
//			 actually process the results of a transition
			HTMLForm f = new HTMLForm(conn);
		
		    ft.buildForm(f, conn);
		    try{
		    FormAction shortcut = f.getShortcutAction(params);
		    if( shortcut != null ){
	    		ConfirmMessage confirm_action = shortcut.getConfirmMessage(f);
	    		if( confirm_action != null ){
					FormResult result = confirmTransition(req, conn, provider, tag, target,confirm_action.getMessage(),confirm_action.getArgs());
					if( result != null ){
						return result;
					}
	    		}
	    		return shortcut.action(f);
	    	}
			if (! f.parsePost(req)){
				return new ErrorFormResult<>(provider, target, tag, HTMLForm.getErrors(req), HTMLForm.getMissing(req)); 
			}
			
				ConfirmMessage confirm_action = f.mustConfirm(params);
				if( confirm_action != null ){
					FormResult result = confirmTransition(req, conn, provider, tag, target,confirm_action.getMessage(),confirm_action.getArgs());
					if( result != null ){
						return result;
					}
				}

				// convention here is that null return indicates an error.
				return  f.doAction(params);
		    }catch( TransitionValidationException te){
		    	if( te.getField() != null ) {
		    		HTMLForm.addFieldError(te.getField(), te.getMessage(), req);
		    	}else {
		    		// show these as general form errors re-displaying form.
		    		HTMLForm.addGeneralError(te.getMessage(), req);
		    	}
				return new ErrorFormResult<>(provider, target, tag, HTMLForm.getErrors(req), HTMLForm.getMissing(req));
			}catch(TransitionException e){
				// if it is a transition exception we have a message for the user
				throw e;	
			}catch(Exception e){
				getLogger().error("Error performing TargetLessTransition",e);
				throw new TransitionException("Operation failed");
			}
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor#doFormTransition(uk.ac.ed.epcc.webapp.forms.transition.FormTransition)
		 */
		@Override
		public FormResult doFormTransition(FormTransition<T> t)
				throws TransitionException {
			return doBaseFormTransition(t);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor#doValidatingFormTransition(uk.ac.ed.epcc.webapp.forms.transition.ValidatingFormTransition)
		 */
		@Override
		public FormResult doValidatingFormTransition(
				ValidatingFormTransition<T> t) throws TransitionException {
			return doBaseFormTransition(t);
		}
		/** confirm a transition along lines of {@link WebappServlet} confirm method.
		 * 
		 * @param req
		 * @param conn
		 * @param tp
		 * @param operation
		 * @param target
		 * @param type 
		 * @param args 
		 * @return FormResult or null if processing to continue.
		
		 */
		public FormResult confirmTransition(HttpServletRequest req,  AppContext conn, TransitionFactory<K,T> tp, K operation, T target,String type,Object args[]) {
			Logger log = getLogger();
			log.debug("Process confirm: "+type);
			String yes = req.getParameter("yes");
	    	String no = req.getParameter("no");
	    	if( no != null ){
	    		log.debug("aborted");
	    		return new MessageResult("aborted");
	    	}
	    	if( yes != null ){
	    		log.debug("confirmed");
	    		// continue with processing
	    		return null;
	    	}
	    	log.debug("show page");
			// need to show page
			return new ConfirmTransitionResult<>(tp,target,operation,type,args);
		}
 	}