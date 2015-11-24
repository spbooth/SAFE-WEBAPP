package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.html.ErrorFormResult;
import uk.ac.ed.epcc.webapp.forms.html.ForwardResult;
import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.html.WebFormResultVisitor;
import uk.ac.ed.epcc.webapp.forms.result.BackResult;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.ConfirmTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.CustomPage;
import uk.ac.ed.epcc.webapp.forms.result.CustomPageResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;
import uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer;
import uk.ac.ed.epcc.webapp.session.SessionService;

@uk.ac.ed.epcc.webapp.Version("$Id: ServletFormResultVisitor.java,v 1.11 2015/08/19 14:44:05 spb Exp $")

public class ServletFormResultVisitor implements WebFormResultVisitor{
	private AppContext conn;
	private HttpServletRequest req; 
	private HttpServletResponse res;
	public ServletFormResultVisitor(AppContext conn,HttpServletRequest req, HttpServletResponse res){
		this.conn=conn;
		this.req=req;
		this.res=res;
	}
	public <T,K> void visitChainedTransitionResult(ChainedTransitionResult<T,K> ctr) throws ServletException, IOException {
		TransitionFactory<K, T> provider = ctr.getProvider();
		T target = ctr.getTarget();
		K key = ctr.getTransition();
		
		if( ctr.useURL()){
			// Note ViewTransitionResult gets caught here
			conn.getService(ServletService.class).redirect(TransitionServlet.getURL(conn, provider, target,key));
			return;
		}
		req.setAttribute(TransitionServlet.TRANSITION_PROVIDER_ATTR, provider);
		req.setAttribute(TransitionServlet.TRANSITION_KEY_ATTR, key);
		
		req.setAttribute(TransitionServlet.TARGET_ATTRIBUTE, target);
		if( key == null ){
			// assume view transition for a ViewTransitionProvider
			// The TransitionServlet uses a chain with a null target to 
			conn.getService(ServletService.class).forward("/scripts/view_target.jsp");
		}else{
		    // assume a form transition
			conn.getService(ServletService.class).forward("/scripts/transition.jsp");
		}
		return;
	}
	public void visitForwardResult(ForwardResult fr) throws ServletException, IOException {
		   Map<String,Object> attr = fr.getAttr();
		   if( attr != null ){
			   for(String name : attr.keySet()){
				   req.setAttribute(name, attr.get(name));
			   }
		   }
		   conn.getService(ServletService.class).forward(fr.getURL());
		   return;
	}
	public void visitMessageResult(MessageResult mr) throws ServletException, IOException {
		 WebappServlet.messageWithArgs(conn, req, res, mr.getMessage(),mr.getArgs());
		   return;
	}
	public void visitRedirectResult(RedirectResult red) throws IOException {
		conn.getService(ServletService.class).redirect(red.getURL());
		
	}
	
	public <T, K> void visitConfirmTransitionResult(
			ConfirmTransitionResult<T, K> res) throws Exception {
		req.setAttribute(WebappServlet.CONFIRM_POST_URL, req.getContextPath()+TransitionServlet.getURL(conn, res.getProvider(),res.getTarget(),res.getTransition()));
		req.setAttribute(WebappServlet.CONFIRM_TYPE,res.getType());
		conn.getService(ServletService.class).forward(WebappServlet.SCRIPTS_CONFIRM_JSP);
		return;
	}
	public  void visitServeDataResult(ServeDataResult sdr) throws Exception {
		// each ServeData has a unique url 
		// redirect to that url
		Logger log = getLogger();
		log.debug("ServeDataResult "+sdr.getClass().getCanonicalName());
		ServeDataProducer producer = sdr.getProducer();
		getLogger().debug("producer "+producer.getClass().getCanonicalName());
		conn.getService(ServletService.class).redirect(ServeDataServlet.getURL(conn,producer,sdr.getArgs()));
		
	}
	public void visitBackResult(BackResult res) throws Exception {
		SessionService<?> sess = conn.getService(SessionService.class);
		Object target_id =  sess.getAttribute(TransitionServlet.VIEW_TRANSITION+res.provider.getTargetName());
		if( target_id == null){
			res.fallback.accept(this);
			return;
		}
		req.setAttribute(TransitionServlet.TRANSITION_PROVIDER_ATTR, res.provider);
		req.setAttribute(TransitionServlet.TRANSITION_KEY_ATTR, null);
		if( res.provider instanceof TransitionProvider){
			req.setAttribute(TransitionServlet.TARGET_ATTRIBUTE, ((TransitionProvider)res.provider).getTarget((String)target_id));
		}else if( res.provider instanceof PathTransitionProvider){
			req.setAttribute(TransitionServlet.TARGET_ATTRIBUTE, ((PathTransitionProvider)res.provider).getTarget((LinkedList)target_id));
		}else{
			// unsupported type of factory
			res.fallback.accept(this);
			return;
		}
		conn.getService(ServletService.class).forward("/scripts/view_target.jsp");
	}
	
	public void visitCustomPage(CustomPageResult res) throws Exception {
		req.setAttribute(CustomPage.CUSTOM_PAGE_TAG, res);
		conn.getService(ServletService.class).forward("/scripts/view_custom_page.jsp");
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.html.WebFormResultVisitor#visitErrorFormResult(uk.ac.ed.epcc.webapp.forms.html.ErrorFormResult)
	 */
	public void visitErrorFormResult(ErrorFormResult result) throws Exception {
		// Assumes errors have already been set in response
		req.setAttribute(TransitionServlet.TRANSITION_KEY_ATTR, result.getKey());
		HTMLForm.doFormError(conn, req, res);
	}
	public Logger getLogger(){
		return conn.getService(LoggerService.class).getLogger(getClass());
	}
	
}