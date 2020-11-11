//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
package uk.ac.ed.epcc.webapp.forms.html;

import uk.ac.ed.epcc.webapp.AbstractContexed;
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


public class AddLinkVisitor extends AbstractContexed implements WebFormResultVisitor {
    private final ExtendedXMLBuilder hb;
    private final String text;
    private final String title;
    public boolean new_tab=false;
    public AddLinkVisitor(AppContext c, ExtendedXMLBuilder hb,String text,String title){
    	super(c);
    	this.hb=hb;
    	this.text=text;
    	this.title=title;
    }
    private String encodeURL(String url){
    	ServletService serv = getContext().getService(ServletService.class);
    	if( serv != null){
    		return serv.encodeURL(url);
    	}
    	return url;
    }
	public <T, K> void visitChainedTransitionResult(
			ChainedTransitionResult<T, K> res) throws Exception {
		TransitionServlet.addLink(getContext(),hb,res.getProvider(),res.getTransition(),res.getTarget(),text,title,new_tab);
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
		if( title != null && ! title.isEmpty()){
			hb.attr("title", title);
		}
		hb.attr("href", encodeURL(res.getURL()));	
		if( new_tab) {
			hb.attr("target","_blank");
		}
		
		hb.clean(text);
		hb.close();

	}

	

	public void visitRedirectResult(RedirectResult res) throws Exception {
		hb.open("a");
		if( title != null && ! title.isEmpty()){
			hb.attr("title", title);
		}
		hb.attr("href", encodeURL(res.getURL()));
		if( new_tab) {
			hb.attr("target","_blank");
		}
		hb.clean(text);
		hb.close();
	}

	public void visitExternalRedirectResult(ExternalRedirectResult res) throws Exception {
		hb.open("a");
		if( title != null && ! title.isEmpty()){
			hb.attr("title", title);
		}
		hb.attr("href", res.getRedirect().toASCIIString());
		if( new_tab) {
			hb.attr("target","_blank");
		}
		hb.clean(text);
		hb.close();
	}
	public void visitMessageResult(MessageResult res) throws Exception {
		throw new UnsupportedResultException("Cannot use MessageResult for link");
	}

	public void visitServeDataResult(ServeDataResult res) throws Exception{
		ServeDataServlet.addLink(getContext(), hb, res.getProducer(), res.getArgs(), text);
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