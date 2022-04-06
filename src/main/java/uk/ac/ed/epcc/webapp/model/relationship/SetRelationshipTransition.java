package uk.ac.ed.epcc.webapp.model.relationship;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryFinder;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;

public class SetRelationshipTransition<X extends DataObject,A extends AppUser> extends AbstractFormTransition<X> {
	public SetRelationshipTransition(Relationship<A, X> rel) {
		super();
		this.rel = rel;
	}
	private final Relationship<A, X> rel;
	@Override
	public void buildForm(Form f, X target, AppContext conn) throws TransitionException {
		SessionService<A> sess = conn.getService(SessionService.class);
		f.addInput("person", "Person", sess.getLoginFactory().getInput(true)); // restricted input
		f.addAction("EditRoles", new FormAction() {
			
			@Override
			public FormResult action(Form f) throws ActionException {
				A user = (A) f.getItem("person");
				Relationship.Link<A, X> l;
				try {
					l = rel.make(user, target);
				} catch (Exception e) {
					throw new ActionException("Error making link", e);
				}
				
				RelationshipTransitionProvider prov = TransitionFactoryFinder.getTransitionFactory(conn, null, rel.getTag());
				if( prov == null ) {
					throw new ActionException("TransitionProvider not registered");
				}
				return new ChainedTransitionResult<>(prov, l,RelationshipTransitionProvider.EDIT );
			}
		});
		
	}

}
