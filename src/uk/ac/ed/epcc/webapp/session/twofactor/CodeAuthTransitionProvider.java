//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.session.twofactor;



import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AnonymousTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.DefaultingTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraContent;
import uk.ac.ed.epcc.webapp.forms.transition.TitleTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.SimpleTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserTransitionProvider;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** {@link TransitionProvider} to validate two factor authentication codes.
 * 
 * This could be combined with the normal {@link AppUserTransitionProvider} but as it works anonymously its safer
 * to keep the logic seperate.
 * @author Stephen Booth
 *
 */
public class CodeAuthTransitionProvider<A extends AppUser> extends SimpleTransitionProvider<A, TransitionKey<A>> implements AnonymousTransitionFactory<TransitionKey<A>, A>, TitleTransitionFactory<TransitionKey<A>, A>, DefaultingTransitionFactory<TransitionKey<A>, A>{

	/**
	 * @param c
	 * @param fac
	 * @param target_name
	 */
	public CodeAuthTransitionProvider(AppContext c) {
		super(c, c.getService(SessionService.class).getLoginFactory(), "AuthenticationCode");
		addTransition(AUTHENTICATE, new AuthenticateTransition());
	}

	public static final TransitionKey AUTHENTICATE = new TransitionKey<>(AppUser.class, "Authenticate");
	
	public class AuthenticateTransition extends AbstractFormTransition<A> implements ExtraContent<A>{

		/**
		 * 
		 */
		private static final String CODE_FIELD = "Code";

		public class ProcessAction<T> extends FormAction{
			/**
			 * @param comp
			 */
			public ProcessAction(SessionService<A> sess,CodeAuthComposite<A, T> comp, A target) {
				super();
				this.sess=sess;
				this.comp = comp;
				this.target=target;
			}
			private final SessionService<A> sess;
			private final CodeAuthComposite<A,T> comp;
			private final A target;
			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
			 */
			@Override
			public FormResult action(Form f) throws ActionException {
				boolean ok = comp.verify(target,(T) f.get(CODE_FIELD));
				comp.authenticated(ok);
				TwoFactorHandler<A> handler = new TwoFactorHandler<>(sess);
				FormResult result = handler.completeTwoFactor(ok);
				if( ! ok ) {
					// result will be null
				}
				return result;
			}
			
		}
		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, A target, AppContext conn) throws TransitionException {
			SessionService<A> sess = conn.getService(SessionService.class);
			CodeAuthComposite<A,?> comp = (CodeAuthComposite<A,?>) sess.getLoginFactory().getComposite(CodeAuthComposite.class);
			if( comp != null ) {
				// We validate the code in the action because we want the option
				// to rate-limit this operation to prevent brute forcing.
				f.addInput(CODE_FIELD, "Verification code", comp.getInput());
				f.addAction("Submit", new ProcessAction<>(sess, comp,target));
			}
			
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.ExtraContent#getExtraHtml(uk.ac.ed.epcc.webapp.content.ContentBuilder, uk.ac.ed.epcc.webapp.session.SessionService, java.lang.Object)
		 */
		@Override
		public <X extends ContentBuilder> X getExtraHtml(X cb, SessionService<?> op, A target) {
			CodeAuthComposite<A,?> comp = (CodeAuthComposite<A,?>) op.getLoginFactory().getComposite(CodeAuthComposite.class);
			if( comp != null) {
				comp.addExtra(cb);
			}
			return cb;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#allowTransition(uk.ac.ed.epcc.webapp.AppContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean allowTransition(AppContext c, A target, TransitionKey<A> key) {
		SessionService sess = c.getService(SessionService.class);
		if( sess == null || ! sess.haveCurrentUser() || sess.isCurrentPerson(target)) {
			// allow if anonymous (we are doing a login) or the current user
			// we are authenticating an operation.
			CodeAuthComposite<A,?> comp = (CodeAuthComposite<A,?>) sess.getLoginFactory().getComposite(CodeAuthComposite.class);
			if( comp != null) {
				
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getSummaryContent(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object)
	 */
	@Override
	public <X extends ContentBuilder> X getSummaryContent(AppContext c, X cb, A target) {
		return cb;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TitleTransitionFactory#getTitle(java.lang.Object, java.lang.Object)
	 */
	@Override
	public String getTitle(TransitionKey<A> key, A target) {
		return "Two factor authentication";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TitleTransitionFactory#getHeading(java.lang.Object, java.lang.Object)
	 */
	@Override
	public String getHeading(TransitionKey<A> key, A target) {
		return getTitle(key, target);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.DefaultingTransitionFactory#getDefaultTransition(java.lang.Object)
	 */
	@Override
	public TransitionKey<A> getDefaultTransition(A target) {
		return AUTHENTICATE;
	}

	

}
