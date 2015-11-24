// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.result;


/** All FormActions return a FormResult
 * FormResults are lightweight command classes that encode the response to the form
 * These need interpreted by the surrounding framework for example a text message is displayed 
 * differently in a web, gui or command line context.
 * Some form results only make sense in a restricted set of contexts. For example the web navigation results.
 * The {@link FormResultVisitor} can be extended to handle these additional result types.
 * Functionality can be promoted to additional contexts by replacing these with explicit FormResult sub-classes 
 * handled by the {@link FormResultVisitor}.
 * 
 * 
 * By using the visitor pattern we make it easier to identify what {@link FormResult} types need to be implemented in each context. 
 * 
 * 
 * @author spb
 *
 */
public interface FormResult {
	public void accept(FormResultVisitor vis) throws Exception;

}