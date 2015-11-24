package uk.ac.ed.epcc.webapp.forms.result;


/** A {@link FormResult} that directly implements {@link CustomPage} to create content.
 * 
 * @author spb
 *
 */
public abstract class CustomPageResult implements CustomPage, FormResult {

	
	public void accept(FormResultVisitor vis) throws Exception {
		vis.visitCustomPage(this);

	}


}
