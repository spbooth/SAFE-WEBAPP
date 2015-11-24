// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTargetLessTransition;
import uk.ac.ed.epcc.webapp.model.data.forms.Creator;
/** A create transition that uses a {@link Creator} object
 * 
 * @author spb
 *
 * @param <X>
 */
public abstract class CreatorTransition<X> extends AbstractTargetLessTransition<X> {
	protected final String type_name;
	public CreatorTransition(String type_name){
		this.type_name=type_name;
	}
	public void buildForm(Form f, AppContext ctx) throws TransitionException {
		FormCreator c = getCreator(ctx);
		try {
			c.buildCreationForm(type_name,f);
		} catch (Exception e) {
			ctx.error(e,"Error building Creation form");
			throw new TransitionException("Internal error");
		}
	}

	public abstract FormCreator getCreator(AppContext c);
}