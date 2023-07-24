package uk.ac.ed.epcc.webapp.model.data.forms;

import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.content.UIProvider;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.Identified;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.factory.FormBuilder;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.*;
/** An interface that can be added to a {@link DataObjectUpdateFormFactory} to convert it into
 * a {@link FormCreator}. This is to help support common customised superclasses for differet types of 
 * {@link DataObjectFormFactory} 
 * 
 * @author Stephen Booth
 * @See {@link CreateTransitionInterface}
 *
 * @param <BDO>
 */
public interface CreatorInterface<BDO extends DataObject> extends FormCreator, CreateTemplate<BDO>, FormBuilder {
	
	default public void buildCreationForm(String type_name,Form f) throws Exception {
		buildCreationForm(f);
	}
	@Override
	default public void buildCreationForm(Form f) throws Exception {
		if( buildForm(f,getInitialFixtures(),getCreationDefaults()) ) {
			customiseCompleteCreationForm(f);
			addActions(f);
		}
		customiseCreationForm(f);
		for(TableStructureContributer comp : getFactory().getTableStructureContributers()){
			if( comp instanceof CreateCustomizer){
				((CreateCustomizer)comp).customiseCreationForm(f);
			}
		}
	}
   
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.CreateTemplate#preCommit(BDO, uk.ac.ed.epcc.webapp.model.data.forms.Form)
	 */
	@Override
	default public  void preCommit(BDO dat, Form f) throws DataException, ActionException {
		for(CreateCustomizer comp : getFactory().getComposites(CreateCustomizer.class)){
			comp.preCommit(dat, f);
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.CreateTemplate#postCreate(BDO, uk.ac.ed.epcc.webapp.model.data.forms.Form)
	 */
	@Override
	default public void postCreate(BDO dat, Form f) throws Exception {
		for(CreateCustomizer comp : getFactory().getComposites(CreateCustomizer.class)){
			comp.postCreate(dat, f);
		}
	}
	
	@Override
	default public FormResult getResult(BDO dat, Form f) {
		Object thing = getTypeName();
		if( dat instanceof UIGenerator || dat instanceof UIProvider || dat instanceof Identified) {
			thing = dat;
		}
		MessageResult res = new MessageResult("object_created",getTypeName(),thing);
		
		return res;
	}
	default public String getTypeName() {
		return getFactory().getTag();
	}
	
	default public HashMap getInitialFixtures() {
		return null;
	}
	default public Map<String,Object> getCreationDefaults(){
		return null;
	}
}
