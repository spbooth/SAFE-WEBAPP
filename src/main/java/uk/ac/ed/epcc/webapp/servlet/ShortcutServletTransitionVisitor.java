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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTransitionVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.DirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.FormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.ValidatingFormTransition;

/** A cut-down version of the {@link ServletTransitionVisitor} that generates those {@link FormResult}s
 * that can be generated without taking a lock, for example the redirect to the form jsp. Otherwise the
 * visitor returns null.
 * 
 * @author spb
 *
 * @param <K>
 * @param <T>
 */
public class ShortcutServletTransitionVisitor<K,T> extends AbstractTransitionVisitor<K,T>{
 		
 		
 		/** hidden parameter to indicate this is a direct post from the view_target page.
 		 * 
 		 */
 		public static final String FROM_VIEW_PARAM = "from_view";
		private final Map params;
 
 	
 		public ShortcutServletTransitionVisitor(AppContext c, 
 		K tag, TransitionFactory<K, T> tp, T target, Map params){
 			super(c,tag,tp,target);
 			this.params=params;
 		}
 		
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
			return null;
		    
		}
		
		@Override
		public FormResult doTargetLessTransition(TargetLessTransition<T> ft) throws TransitionException{
			String transition_form = (String) params.get("transition_form");
			
			if( transition_form == null){
				// have to go the the transition page to
				// show the form
				// but its easier if all main forms post to here first
				return new ChainedTransitionResult<>(provider,null,tag);
			}
			return null;
			

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
		@Override
		public FormResult doDirectTransition(DirectTransition<T> t) throws TransitionException {
			if( tag instanceof ViewTransitionKey && ((ViewTransitionKey<T>)tag).isNonModifying(target) && params.containsKey(FROM_VIEW_PARAM)) {
				// send redirect to a bookmarkable url
				return new ChainedTransitionResult<T, K>(provider,target,tag){
					@Override
					public boolean useURL() {
						return true;
					}
					
				};
			}
			// DirectTransitions take a target so may have side effects.
			return null;
		}
		
 	}