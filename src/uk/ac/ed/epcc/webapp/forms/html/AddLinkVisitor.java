// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.html;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.forms.result.BackResult;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.ConfirmTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.CustomPageResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;
import uk.ac.ed.epcc.webapp.forms.swing.UnsupportedResultException;
import uk.ac.ed.epcc.webapp.servlet.ServeDataServlet;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;

/** FormResultVisitor that generates a single html button form for the target result.
 * 
 * Note that this is used to implement the equivalent operation in the ContentBuilder interface so
 * I can't just call the appropriate method
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: AddLinkVisitor.java,v 1.7 2014/09/15 14:30:18 spb Exp $")

public class AddLinkVisitor implements WebFormResultVisitor {
    private final ExtendedXMLBuilder hb;
    private final AppContext conn;
    private final String text;
    public AddLinkVisitor(AppContext c, ExtendedXMLBuilder hb,String text){
    	conn=c;
    	this.hb=hb;
    	this.text=text;
    }
    private String encodeURL(String url){
    	ServletService serv = conn.getService(ServletService.class);
    	if( serv != null){
    		return serv.encodeURL(url);
    	}
    	return url;
    }
	public <T, K> void visitChainedTransitionResult(
			ChainedTransitionResult<T, K> res) throws Exception {
		TransitionServlet.addLink(conn, hb, res, text);
	}

	public <T, K> void visitConfirmTransitionResult(
			ConfirmTransitionResult<T, K> res) throws Exception {
		visitChainedTransitionResult(res);

	}

	public void visitForwardResult(ForwardResult res) throws Exception {
		if( res.getAttr() != null ){
			throw new UnsupportedResultException("Cannot link with forward attributes");
		}
		hb.open("a");
		hb.attr("href", encodeURL(res.getURL()));
		hb.clean(text);
		hb.close();

	}

	

	public void visitRedirectResult(RedirectResult res) throws Exception {
		hb.open("a");
		hb.attr("href", encodeURL(res.getURL()));
		hb.clean(text);
		hb.close();
	}

	public void visitMessageResult(MessageResult res) throws Exception {
		throw new UnsupportedResultException("Cannot use MessageResult for link");
	}

	public void visitServeDataResult(ServeDataResult res) throws Exception{
		ServeDataServlet.addLink(conn, hb, res.getProducer(), res.getArgs(), text);
	}
	public void visitBackResult(BackResult res) throws Exception {
		throw new UnsupportedResultException("Cannot use BackResult for link");
		
	}
	
	
	public void visitCustomPage(CustomPageResult res) throws Exception {
		throw new UnsupportedResultException("Cannot use CustomPageResult for Button");
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor#visitErrorFormResult(uk.ac.ed.epcc.webapp.forms.result.ErrorFormResult)
	 */
	public void visitErrorFormResult(ErrorFormResult res) throws Exception {
		throw new UnsupportedResultException("Cannot use CustomPageResult for Link");
	}
	

}