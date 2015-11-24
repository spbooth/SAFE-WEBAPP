// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionValidationException;
import uk.ac.ed.epcc.webapp.forms.html.ErrorFormResult;
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

@uk.ac.ed.epcc.webapp.Version("$Id: ServletTransitionVisitor.java,v 1.5 2015/02/03 15:36:11 spb Exp $")
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
				return new ChainedTransitionResult<T,K>(provider,target,tag);
			}
//			 actually process the results of a transition
			HTMLForm f = new HTMLForm(conn);
		
		    ft.buildForm(f, target, conn);
		    try{
		    	FormAction shortcut = f.getShortcutAction(params);
		    	if( shortcut != null ){
		    		FormResult result=null;
		    		String confirm_action = shortcut.getConfirm(f);
		    		if( confirm_action != null ){
						result = confirmTransition(req, conn, provider, tag, target,confirm_action,null);
						if( result != null ){
							return result;
						}
						
					}
		    		result = shortcut.action(f);
					if( result != null ){
						return result;
					}
		    	}
		    	if (! f.parsePost(req)){
		    		// Not all ok
		    		return new ErrorFormResult<T, K>(provider, target, tag, HTMLForm.getErrors(req), HTMLForm.getMissing(req));
		    	}

		    	String confirm_action = f.mustConfirm(params);

				if( confirm_action != null ){
					FormResult result = confirmTransition(req, conn, provider, tag, target,confirm_action,null);
					if( result != null ){
						return result;
					}
				}
			
				return  f.doAction(params);
		    }catch( TransitionValidationException te){
		    	// show these as general form errors re-displaying form.
				HTMLForm.addGeneralError(te.getMessage(), req);
				return new ErrorFormResult<T, K>(provider, target, tag, HTMLForm.getErrors(req), HTMLForm.getMissing(req));
			}catch(TransitionException e){
				// if it is a transition exception we have a message for the user
				throw e;
			}catch(Exception e){
				conn.error(e,"Error performing FormTransition");
				throw new TransitionException("Operation failed");
			}
		}
		@SuppressWarnings("unchecked")
		public FormResult doTargetLessTransition(TargetLessTransition<T> ft) throws TransitionException{
			String transition_form = (String) params.get("transition_form");
			
			if( transition_form == null){
				// have to go the the transition page to
				// show the form
				// but its easier if all main forms post to here first
				return new ChainedTransitionResult<T,K>(provider,null,tag);
			}
//			 actually process the results of a transition
			HTMLForm f = new HTMLForm(conn);
		
		    ft.buildForm(f, conn);
		    try{
		    FormAction shortcut = f.getShortcutAction(params);
		    if( shortcut != null ){
	    		String confirm_action = shortcut.getConfirm(f);
	    		if( confirm_action != null ){
					FormResult result = confirmTransition(req, conn, provider, tag, target,confirm_action,null);
					if( result != null ){
						return result;
					}
					result = shortcut.action(f);
					if( result != null ){
						return result;
					}
				}
	    	}
			if (! f.parsePost(req)){
				return new ErrorFormResult<T, K>(provider, target, tag, HTMLForm.getErrors(req), HTMLForm.getMissing(req)); 
			}
			
				String confirm_action = f.mustConfirm(params);
				if( confirm_action != null ){
					FormResult result = confirmTransition(req, conn, provider, tag, target,confirm_action,null);
					if( result != null ){
						return result;
					}
				}

				// convention here is that null return indicates an error.
				return  f.doAction(params);
		    }catch( TransitionValidationException te){
		    	// show these as general form errors re-displaying form.
				HTMLForm.addGeneralError(te.getMessage(), req);
				return new ErrorFormResult<T, K>(provider, target, tag, HTMLForm.getErrors(req), HTMLForm.getMissing(req));
			}catch(TransitionException e){
				// if it is a transition exception we have a message for the user
				throw e;	
			}catch(Exception e){
				conn.error(e,"Error performing TargetLessTransition");
				throw new TransitionException("Operation failed");
			}
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor#doFormTransition(uk.ac.ed.epcc.webapp.forms.transition.FormTransition)
		 */
		public FormResult doFormTransition(FormTransition<T> t)
				throws TransitionException {
			return doBaseFormTransition(t);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor#doValidatingFormTransition(uk.ac.ed.epcc.webapp.forms.transition.ValidatingFormTransition)
		 */
		public FormResult doValidatingFormTransition(
				ValidatingFormTransition<T> t) throws TransitionException {
			return doBaseFormTransition(t);
		}
		/** confirm a transition along lines of {@link SessionServlet} confirm method.
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
		public FormResult confirmTransition(HttpServletRequest req,  AppContext conn, TransitionFactory<K,T> tp, K operation, T target,String type,String args[]) {
			
			String yes = req.getParameter("yes");
	    	String no = req.getParameter("no");
	    	if( no != null ){
	    		return new MessageResult("aborted");
	    	}
	    	if( yes != null ){
	    		// continue with processing
	    		return null;
	    	}
			// need to show page
			return new ConfirmTransitionResult<T, K>(tp,target,operation,type,args);
		}
 	}