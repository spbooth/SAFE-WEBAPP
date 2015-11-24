// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;


public abstract class ParseMultiInput<V,I extends Input> extends MultiInput<V,I> implements
		ParseMapInput {
	public final <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitParseMultiInput(this);
	}
}