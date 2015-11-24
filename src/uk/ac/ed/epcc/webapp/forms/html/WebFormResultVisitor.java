package uk.ac.ed.epcc.webapp.forms.html;

import uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor;
/** Additional FormResults that only make sense in a web deployment.
 * 
 * @author spb
 *
 */
public interface WebFormResultVisitor extends FormResultVisitor {
	public void visitForwardResult(ForwardResult res) throws Exception;
	public void visitRedirectResult(RedirectResult res) throws Exception;
	public void visitErrorFormResult(ErrorFormResult res)throws Exception;
}
