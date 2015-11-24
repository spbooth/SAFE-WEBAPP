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
@uk.ac.ed.epcc.webapp.Version("$Id: AddButtonVisitor.java,v 1.10 2015/08/25 15:12:22 spb Exp $")

public class AddButtonVisitor implements WebFormResultVisitor {
    private final ExtendedXMLBuilder hb;
    private final AppContext conn;
    private final String text;
    private final String title;
    public AddButtonVisitor(AppContext c, ExtendedXMLBuilder hb,String text){
    	this(c,hb,text,null);
    }
    public AddButtonVisitor(AppContext c, ExtendedXMLBuilder hb,String text,String title){
    	conn=c;
    	this.hb=hb;
    	this.text=text;
    	this.title=title;
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
		TransitionServlet.addButton(conn, hb, res, text,title);
	}

	public <T, K> void visitConfirmTransitionResult(
			ConfirmTransitionResult<T, K> res) throws Exception {
		visitChainedTransitionResult(res);

	}

	public void visitForwardResult(ForwardResult res) throws Exception {
		if( res.getAttr() != null ){
			throw new UnsupportedResultException("Cannot  with forward attributes");
		}
		hb.open("form");
		hb.attr("action", encodeURL(res.getURL()));
		  hb.open("input");
		    hb.attr("type", "submit");
		    hb.attr("value",text);
	        if( title != null && title.trim().length() > 0){
	            	hb.attr("title", title);
	        }
		  hb.close();
		hb.close();

	}

	

	public void visitRedirectResult(RedirectResult res) throws Exception {
		hb.open("form");
		hb.attr("action", encodeURL(res.getURL()));
		  hb.open("input");
		    hb.attr("type", "submit");
		    hb.attr("value",text);
	        if( title != null && title.trim().length() > 0){
	            	hb.attr("title", title);
	        }
		  hb.close();
		hb.close();
	}

	public void visitMessageResult(MessageResult res) throws Exception {
		throw new UnsupportedOperationException("Cannot use MessageResult for button");
	}

	public void visitServeDataResult(ServeDataResult res) throws Exception{
		hb.open("form");
		hb.attr("action", encodeURL(ServeDataServlet.getURL(conn, res.getProducer(), res.getArgs())));
		  hb.open("input");
		    hb.attr("type", "submit");
		    hb.attr("value",text);
		  hb.close();
		hb.close();

	}
	public void visitBackResult(BackResult res) throws Exception {
		throw new UnsupportedResultException("Cannot use BackResult for button");
		
	}
	
	public void visitCustomPage(CustomPageResult res) throws Exception {
		throw new UnsupportedResultException("Cannot use CustomPageResult for Button");
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor#visitErrorFormResult(uk.ac.ed.epcc.webapp.forms.result.ErrorFormResult)
	 */
	public void visitErrorFormResult(ErrorFormResult res) throws Exception {
		throw new UnsupportedResultException("Cannot use ErrorFormResult for Button");
	}
	

}