//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.model.far.response;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link ContentVisitor} that adds edit buttons to sections.
 * @author spb
 *
 */

public class EditSectionVisitor<X extends ContentBuilder> extends ContentVisitor<X> {

	
	private final ResponseTransitionProvider prov;
	
	
	/**
	 * 
	 */
	public EditSectionVisitor(X cb, SessionService sess,ResponseTransitionProvider<?,?> provider,Response<?> response) {
		super(cb,sess,response);
		this.prov=provider;
	}

	
	public <C extends ContentBuilder> C visitSection(C builder, Section s) throws Exception{
		builder = super.visitSection(builder, s);
		if( prov != null && response.canEdit(sess) ){
			// go straight to edit transition from page view
			ContentBuilder buttons =  builder.getPanel("action_buttons");
			buttons.addButton(s.getContext(), "Edit", prov.new EditResult(new ResponseTarget(response, s)));
			buttons.addParent();
		}
		return builder;
	}
	
}