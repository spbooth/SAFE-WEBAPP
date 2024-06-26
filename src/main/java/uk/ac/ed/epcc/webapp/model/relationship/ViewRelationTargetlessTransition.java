//| Copyright - The University of Edinburgh 2017                            |
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
package uk.ac.ed.epcc.webapp.model.relationship;

import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.SetInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTargetLessTransition;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

/** A transition to show all people with a specific Relationship to any target
 * @author spb
 *
 */
public class ViewRelationTargetlessTransition<X extends DataObject> extends AbstractTargetLessTransition<X> {

	/**
	 * 
	 */
	private static final String RELATIONSHIP_FIELD = "Relationship";
	private final DataObjectFactory<X> fac;
	public ViewRelationTargetlessTransition(DataObjectFactory<X> fac) {
		super();
		this.fac = fac;
	}
	public Set<String> getRelationships(){
		LinkedHashSet<String> rels = new LinkedHashSet<>();
		for(String r : fac.getContext().getInitParameter(fac.getConfigTag()+".relationship_list","").split("\\s*,\\s*")){
			rels.add(r);
		}
		if( fac instanceof RelationshipProvider) {
			rels.addAll(((RelationshipProvider)fac).getRelationships());
		}
		for(RelationshipProvider prov : fac.getComposites(RelationshipProvider.class)) {
			rels.addAll(prov.getRelationships());
		}
		return rels;
	}
	public class ShowAction extends FormAction{
		public ShowAction() {
			super();
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
		 */
		@Override
		public FormResult action(Form f) throws ActionException {
			return new RelationshipList<>(fac.getContext(), (String)f.get(RELATIONSHIP_FIELD),fac, null);
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
	 */
	@Override
	public void buildForm(Form f, AppContext conn) throws TransitionException {
		SetInput<String> r = new SetInput<>(getRelationships());
		f.addInput(RELATIONSHIP_FIELD,"Relationship", r);
		f.addAction("View", new ShowAction());
		
		
	}

}
