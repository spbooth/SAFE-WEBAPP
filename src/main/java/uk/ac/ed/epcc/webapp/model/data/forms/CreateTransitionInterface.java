package uk.ac.ed.epcc.webapp.model.data.forms;

import java.util.HashMap;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.factory.FormBuilder;
import uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.CreateAction;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFormFactory;

/** An interface that can be added to a {@link DataObjectUpdateFormFactory} to convert it into
 * a create transition. This is to help support common customised superclasses for differett types of 
 * {@link DataObjectFormFactory}
 * @author Stephen Booth
 * @see CreatorInterface
 * @param <BDO>
 */
public interface CreateTransitionInterface<BDO extends DataObject> extends TargetLessTransition<BDO>, CreateTemplate<BDO>, Contexed, FormBuilder{

	public String getTypeName();
	@Override
	public default void buildForm(Form f, AppContext c) throws TransitionException {
		try {
			
			if( buildForm(f,getInitialFixtures())) {
				setAction(f);
			}
			customiseCreationForm(f);
			for(CreateCustomizer comp : getFactory().getComposites(CreateCustomizer.class)){
				comp.customiseCreationForm(f);		
			}
		} catch (Exception e) {
			getContext().error(e,"Error creating object");
			throw new TransitionException("Error creating object");
		}
		
	}
	public default void setAction(Form f) {
		f.addAction("Create", new CreateAction<>(getTypeName(), getActionText(),this));
	}
	/** Override the text for the create button
	 * 
	 * @return object added as button content
	 */
	default public Object getActionText() {
		return null;
	}
	default public HashMap getInitialFixtures() {
		return null;
	}
	

	
	
	
}
