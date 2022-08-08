package uk.ac.ed.epcc.webapp.model.relationship;

import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.BooleanInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.transition.SimpleTransitionProvider;
import uk.ac.ed.epcc.webapp.model.relationship.Relationship.Link;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** A {@link TransitionProvider} for editing {@link Relationship} roles.
 * This must be registered using the same name as the construction tag of the {@link Relationship}
 * 
 * @author Stephen Booth
 *
 */
public class RelationshipTransitionProvider extends SimpleTransitionProvider<Relationship.Link, RelationshipKey> {

	public static final String SET_ROLES_RELATIONSHIP = "SetRoles";

	public static final RelationshipKey EDIT = new RelationshipKey("Edit");
	public RelationshipTransitionProvider(AppContext c, String target_name) {
		super(c, c.makeObject(Relationship.class, target_name), target_name);
		addTransition(EDIT, new SetRoleTransition());
	}

	public Relationship<?,?> getRelationship() {
		return (Relationship) getProducer();
	}
	@Override
	public boolean allowTransition(AppContext c, Link target, RelationshipKey key) {
		if( target == null ) {
			return false;
		}
		SessionService sess = c.getService(SessionService.class);
		try {
			Relationship<?, ?> r = getRelationship();
			if( r == null) {
				return false;
			}
			return sess.hasRelationship(r.getRightFactory(), target.getTarget(), SET_ROLES_RELATIONSHIP,false);
		} catch (DataException e) {
			getLogger().error("Error checking permissions", e);
			return false;
		}
	}

	public class SetRoleTransition extends AbstractFormTransition<Relationship.Link>{

		@Override
		public void buildForm(Form f, Link target, AppContext conn) throws TransitionException {
			Relationship<?,?> rel = getRelationship();
			Set<String> relationships = rel.getSettableRelationships();
			for(String role : relationships) {
				BooleanInput i = new BooleanInput();
				f.addInput(role, role, i);
				f.put(role, target.hasRole(role));
			}
			f.addAction("Update", new FormAction() {
				
				@Override
				public FormResult action(Form f) throws ActionException {
					for(String role: relationships) {
						target.setRole(role, (Boolean) f.get(role));
					}
					try {
						target.commit();
						return new MessageResult("relationship_updated", target.getUser(),target.getTarget());
					}catch(DataException e) {
						throw new ActionException("Error setting roles", e);
					}
				}
			});
		}
		
	}
}
