package uk.ac.ed.epcc.webapp.model.data.forms;

import java.util.HashMap;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.factory.FormBuilder;
import uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFormFactory;

/** An interface that can be added to a {@link DataObjectUpdateFormFactory} to convert it into
 * a create transition. This is to help support common customised superclasses for differet types of 
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
				customiseCompleteCreationForm(f);
				addActions(f);
			}
			customiseCreationForm(f);
			for(CreateCustomizer comp : getFactory().getComposites(CreateCustomizer.class)){
				comp.customiseCreationForm(f);		
			}
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("Error creating object",e);
			throw new TransitionException("Error creating object");
		}
		
	}
	
	
	default public HashMap getInitialFixtures() {
		return null;
	}
	

	
	
	
}
