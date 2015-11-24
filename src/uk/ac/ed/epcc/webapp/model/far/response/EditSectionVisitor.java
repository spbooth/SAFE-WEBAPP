// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far.response;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link ContentVisitor} that adds edit buttons to sections.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
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
