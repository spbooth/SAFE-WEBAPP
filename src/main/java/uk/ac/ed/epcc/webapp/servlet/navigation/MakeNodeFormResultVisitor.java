package uk.ac.ed.epcc.webapp.servlet.navigation;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.html.ErrorFormResult;
import uk.ac.ed.epcc.webapp.forms.html.ExternalRedirectResult;
import uk.ac.ed.epcc.webapp.forms.html.ForwardResult;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.html.WebFormResultVisitor;
import uk.ac.ed.epcc.webapp.forms.result.BackResult;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.ConfirmTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.CustomPageResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;
/** A {@link FormResultVisitor} that generates a Node based on the result.
 *  
 * @author Stephen Booth
 *
 */
public class MakeNodeFormResultVisitor extends AbstractContexed implements WebFormResultVisitor {

	public MakeNodeFormResultVisitor(AppContext conn) {
		super(conn);
	}

	private Node result=null;
	public Node getNode() {
		return result;
	}
			
	
	
	@Override
	public <T, K> void visitChainedTransitionResult(ChainedTransitionResult<T, K> res) throws Exception {
		ExactNode node = new ExactNode();
		node.setTargetPath(TransitionServlet.getURL(getContext(), res.getProvider(), res.getTarget(), res.getTransition()));
		result = node;

	}

	@Override
	public <T, K> void visitConfirmTransitionResult(ConfirmTransitionResult<T, K> res) throws Exception {


	}

	@Override
	public void visitMessageResult(MessageResult res) throws Exception {
	

	}

	@Override
	public void visitServeDataResult(ServeDataResult res) throws Exception {
	

	}

	@Override
	public void visitBackResult(BackResult res) throws Exception {
		// Menus are static use the fallback
		if( res.fallback != null) {
			res.fallback.accept(this);
		}
	}

	@Override
	public void visitCustomPage(CustomPageResult res) throws Exception {
		
	}

	

	@Override
	public void visitForwardResult(ForwardResult res) throws Exception {
		ExactNode node = new ExactNode();
		node.setTargetPath(res.getURL());
		result = node;

	}

	@Override
	public void visitRedirectResult(RedirectResult res) throws Exception {
		ExactNode node = new ExactNode();
		node.setTargetPath(res.getURL());
		result = node;
	}

	@Override
	public void visitErrorFormResult(ErrorFormResult res) throws Exception {
		

	}

	@Override
	public void visitExternalRedirectResult(ExternalRedirectResult res) throws Exception {
		ExternalNode ext = new ExternalNode();
		ext.setTargetPath(res.getRedirect().toString());
		result = ext;

	}

}
