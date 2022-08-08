package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.forms.Form;

public class Action implements UIGenerator {

	public Action(Form f, String action) {
		super();
		this.f = f;
		this.action = action;
	}



	private final Form f;
	private final String action;
	
	

	@Override
	public ContentBuilder addContent(ContentBuilder builder) {
		builder.addActionButton(f, action);
		return builder;
	}

}
