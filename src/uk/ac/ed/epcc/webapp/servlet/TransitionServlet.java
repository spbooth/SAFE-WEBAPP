// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.forms.exceptions.FatalTransitionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.BackResult;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AnonymousTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.DefaultingTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryCreator;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Servlet to do generic transitions.
 * 
 * This is a central access point for transitions on servlets.
 * The {@link TransitionFactory} and Target are encoded in the servlet path to give them a unique URL.
 * Optionally other parameters like the transition-key may also be encoded there.
 *  This allows jsp pages invoked by forward to have the same URL as their target
 *  so they can omit the action attribute. This is particularly useful
 *  when using a {@link PathTransitionProvider}
 * 
 * 
 * Transitions do their own access control so this only extends {@link SessionServlet}
 * 
 * @author spb
 * @param <K> key type
 * @param <T> Target type
 */
@uk.ac.ed.epcc.webapp.Version("$Id: TransitionServlet.java,v 1.107 2015/09/22 14:10:16 spb Exp $")
@WebServlet(name="TransitionServlet",urlPatterns=TransitionServlet.TRANSITION_SERVLET+"/*")
public  class TransitionServlet<K,T> extends WebappServlet {

	public static final String VIEW_TRANSITION = "ViewTransition.";
	public static final String TRANSITION_PROVIDER_PREFIX = "TransitionProvider";
	public static final Feature TRANSITION_TRANSACTIONS = new Feature("transition.transactions", true, "Use database transaction within transitions");
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//public static final String TARGET_ID = "TargetID";
	//public static final String TRANSITION_PROVIDER_PARAM = "type";
	public static final String TRANSITION_SERVLET = "/TransitionServlet";
	public static final String TRANSITION_PROVIDER_ATTR = "TransitionProvider";
	public static final String TRANSITION_KEY_ATTR = "Transition";
	public static final String TARGET_ATTRIBUTE = "Target";
	@SuppressWarnings("unchecked")
	@Override
 	public void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn) {
		try{
		// first find the transition provider
		ServletService servlet_service = conn.getService(ServletService.class);
	
		Map<String,Object> params = servlet_service.getParams();
		LinkedList<String> path = servlet_service.getArgs();
		Logger log = conn.getService(LoggerService.class).getLogger(getClass());
		log.debug("In TransitionServlet");
		FormResult o = null;
		TransitionFactory<K,T> tp = null;
		if( path.size() > 0){
			tp = (TransitionFactory<K, T>) getProviderFromName(conn, path.removeFirst());
		}
		if( tp == null ){
        	log.warn("No transition provider");
        	message(conn,req,res,"invalid_input");
			return;
        }
        log.debug("provider is "+tp.getClass().getCanonicalName());
        req.setAttribute(TRANSITION_PROVIDER_ATTR, tp);
        SessionService<?> sess = getSessionService(conn,params);
        if( ! (tp instanceof AnonymousTransitionFactory) && ( sess == null || ! sess.haveCurrentUser())){
        	sessionError(conn,sess,req, res);
        	return;
        }
        T target = getTarget(conn,tp,path) ;
        // A null target is allowed with some transitions e.g. select/create
        // so null targets need to be checked for later.
        // set as attribute for scripts we forward to.
        if( target == null){
			req.removeAttribute(TARGET_ATTRIBUTE);
		}else{
			req.setAttribute(TARGET_ATTRIBUTE, target);
		}
		K key = null;
		key = (K) req.getAttribute(TRANSITION_KEY_ATTR);
		if( key == null ){
			String name = (String) params.get(TRANSITION_KEY_ATTR);
			if( name != null ){
				key = tp.lookupTransition(target,name);
				if( key == null ){
					conn.error("Transition key lookup failed for "+name+" provider="+tp.getTargetName());
					message(conn, req, res, "invalid_input");
					return;
				}
			}
		}
		log.debug("transition="+key);
		if( key == null){
			if( target != null && tp instanceof ViewTransitionFactory){
				log.debug("Redirecting to View page");
				ViewTransitionFactory<K, T> vtp = (ViewTransitionFactory<K, T>)tp;
				if( vtp.canView(target, sess)){
					// Note we use ChainedTransitionResult not a ViewTransitionResult as the latter
					// would redirect back to this servlet and e
					handleFormResult(conn, req, res, new ChainedTransitionResult<T, K>(vtp, target,null));
				}else{
					message(conn, req, res, "access_denied");
				}
				return;
			}
			// IndexTransitionProvider can generate an index
			if( target == null && tp instanceof IndexTransitionFactory){
				key = ((IndexTransitionFactory<K, T>)tp).getIndexTransition();
			}else if( target != null && tp instanceof DefaultingTransitionFactory){
				key = ((DefaultingTransitionFactory<K, T>)tp).getDefaultTransition(target);
			}
			if( key == null ){
				log.debug("No transition");
				message(conn, req, res, "invalid_input");
				return;
			}
			
		}
	    // this is the access control
		if( ! tp.allowTransition(conn,target,key)){
			message(conn, req, res, "access_denied");
        	return;
		}
		Transition<T> t=null;
		
		long start=0L,aquired=0L;
		long max_wait=conn.getLongParameter("max.transition.millis", 30000L);
		// Transitions should take place in a transaction
		// This keeps them atomic even on distributed infrastructure.
		DatabaseService serv = conn.getService(DatabaseService.class);
		if ( TRANSITION_TRANSACTIONS.isEnabled(conn)){
			serv.startTransaction();
		}
		try{
			start = System.currentTimeMillis();
			synchronized(tp.getClass()){
				aquired=System.currentTimeMillis();
				// to ensure consistency all modifications of target objects and the checks
				// that operations are valid, are protected by a lock.
				// We use the class of the TransitionProvider as the lock object to only
				// sequentialise operations on the same type of object. 
				t = tp.getTransition(target,key);
				if( t != null ){
					o = processTransition(conn, req, params, tp, key, target, t);
				}
			}
		
		}catch(TransitionException e){
			// Assume no roll-back needed unless and explict fatal exceptin It might be ok 
			// to  allways roll-back here.
			// and assume that the DB can perform a null roll-back cheaply.
			if( e instanceof FatalTransitionException){
				log.error("FatalTransitionException", e);
				serv.rollbackTransaction();
			}
			log.debug("transition exception", e);
			message(conn, req, res, "transition_error",  key, e.getMessage());
			return;
		}catch(Throwable tr){
			// assume this is bad and roll-back
			serv.rollbackTransaction();
			throw tr;
		}finally{
			// restore original mode (ususally auto-commit
			serv.stopTranaction();
			try{
				long now = System.currentTimeMillis();
				if((now-aquired) > max_wait){
					long secs = (now-aquired)/1000L;
					// This transition has taken a long time
					log.warn("Long transition "+secs+" seconds provider="+tp.getTargetName()+" target="+getLogTag(tp,target)+" key="+key);
				}else if( now-start > max_wait){
					// Long time waiting for lock
					long secs = (now-start)/1000L;
					log.warn("Blocked transition "+secs+" seconds provider="+tp.getTargetName()+" target="+getLogTag(tp,target)+" key="+key);
				}
			}catch(Throwable tr){
				log.error("Problem reporting transition timimgs",tr);
			}
		}
		if( o == null ){
			// sthing has gone wrong no FormResult
			if( t == null ){
				// never found a transition
				log.debug("Transition key did not resolve to a class");
				
			}else{
				log.debug("Visitor returned null FormResult");
			}
			message(conn, req, res, "invalid_input");
			return;
		}
		
		
		// handle the FormResult
		// current target should be set as attribute by default
		handleFormResult(conn,req,res,o);
		}catch(Throwable tr){
			conn.error(tr,"Exception caught in TransitionServlet");
		}
	}
	/** Extension point for missing authorisation
	 * @param conn
	 * @param sess
	 * @param req
	 * @param res
	 * @throws IOException 
	 */
	protected void sessionError(AppContext conn, SessionService sess,HttpServletRequest req, HttpServletResponse res) throws IOException {
		conn.getService(ServletService.class).requestAuthentication(sess);
		
	}
	protected FormResult processTransition(AppContext conn,
			HttpServletRequest req, Map<String, Object> params,
			TransitionFactory<K, T> tp, K key, T target, Transition<T> t)
			throws TransitionException {
		  return t.getResult(new ServletTransitionVisitor<K, T>(conn, req, key, tp, target, params));
	}
	/** Extension point to get the session including any custom login code based on the parameters.
	 * This allows sub-classes to that parse the parameters for login credentials
	 * @param conn
	 * @return
	 */
	protected SessionService getSessionService(AppContext conn, Map<String,Object> raw_params) {
		return conn.getService(SessionService.class);
	}
	
	
	/** method to retrieve the TransitionProvider from a string tag
 * If the tag contains a colon this is taken as a separator between two fields.
 * The first field is used to construct a {@link TransitionFactoryCreator} using {@link AppContext#makeObject(Class, String)}
 * and the second field is used as the parameter to {@link TransitionFactoryCreator#getTransitionProvider(String)}.
 * otherwise calls to  {@link AppContext#makeObject(Class, String)} are attempted with the following tag/type parameters
 * <ul>
 * <li> <b>TransitionProvider.</b><TargetName</b> looking for {link {@link TransitionFactory}.
 * <li> <i>TargetName</i> looking for {@link TransitionFactory}
 * <li> <i>TargetName</i> looking for {@link TransitionFactoryCreator}
 * </ul>
 * Sub-classes can override this method.
 * @param conn
 * @param type  TargetName of the TransitionProvider
 * @return {@link TransitionFactory}
 */
	
    public static TransitionFactory getProviderFromName(AppContext conn, String type) {
	TransitionFactory result=null;
	if( type==null || type.trim().length() ==0 ){
		return null;
	}
	int index = type.indexOf(TransitionFactoryCreator.TYPE_SEPERATOR);
	if( index > 0){
		// must be a parameterised TransitionProviderFactory
		String group = type.substring(0, index);
		String tag = type.substring(index+1);
		TransitionFactoryCreator<TransitionFactory> f = conn.makeObjectWithDefault(TransitionFactoryCreator.class,null, group);
		if( f != null ){
			result = f.getTransitionProvider(tag);
		}
	}else{
		result = conn.makeObjectWithDefault(TransitionFactory.class,null,TRANSITION_PROVIDER_PREFIX,type);
		if( result == null ){
			TransitionFactoryCreator<TransitionFactory> f = conn.makeObjectWithDefault(TransitionFactoryCreator.class,null, type);
			if( f != null ){
				result = f.getTransitionProvider(type);
			}
		}
	}
	
	// check targetName matches type
	assert(result == null || result.getTargetName().equals(type));
	
	return result;
}
    /** Static method for jsp pages to retrieve the TransitionProvider 
     * in a way compatible with the TransitionSerlvet
     * @param conn
     * @param req
     * @return TransitionProvider
     * @throws Exception
     */
	public static TransitionFactory getProvider(AppContext conn, HttpServletRequest req)throws Exception{
//		Map<String,Object> params = conn.getService(ServletService.class).getParams(req);
//		
		TransitionFactory tp = (TransitionFactory) req.getAttribute(TRANSITION_PROVIDER_ATTR);
//		if( tp == null ){
//			// really want to avoid using params if we can
//			String type = (String) params.get(TRANSITION_PROVIDER_PARAM);
//			tp = getProviderFromName(conn,type);
//		}
		return tp;
	}
 	
	
	
	
 	private String getLogTag(TransitionFactory<K, T> fac, T target){
 		if( target == null ){
 			return "null";
 		}
 		if( fac instanceof TransitionProvider){
 			return ((TransitionProvider<K, T>)fac).getID(target);
 		}
 		if( fac instanceof PathTransitionProvider){
 			LinkedList<String> path = ((PathTransitionProvider<K, T>)fac).getID(target);
 			StringBuilder sb = new StringBuilder();
 			for( String s : path){
 				sb.append('/');
 				sb.append(s);
 			}
 			return sb.toString();
 		}
 		return "???";
 	}
 	public static class GetTargetVisitor<T,K> implements TransitionFactoryVisitor<T,T, K>{
 		public GetTargetVisitor(LinkedList<String> path) {
			super();
			this.path = path;
		}

		private final LinkedList<String> path;

		public T visitTransitionProvider(TransitionProvider<K, T> prov) {
			final String target_id= path.removeFirst();
			
			if( target_id == null || target_id.length() == 0 ){
				// This is a index transition.
			
				return null;
			}
			return prov.getTarget(target_id);
		}

		public  T visitPathTransitionProvider(
				PathTransitionProvider<K, T> prov) {
			return  prov.getTarget(path);
		}
 	}
 	/** Extracts the Target form the URL and sets it as an attribute for any scripts we forward to.
 	 * 
 	 * @param conn
 	 * @param provider
 	 * @param path
 	 * @param req
 	 * @return
 	 */
	protected   T getTarget(AppContext conn,TransitionFactory<K,T> provider, LinkedList<String> path){
		if( path == null || path.size() == 0){
			return null;
		}
		GetTargetVisitor<T,K> vis = new GetTargetVisitor<T,K>(path);
		return provider.accept(vis);
	}
	/** Static method for jsp pages to use to retieve the target in a way compatible with the
	 * TransitionServlet
	 * @param <A> 
	 * @param <B> 
	 * @param conn 
	 * 
	 * @param provider
	 * @param req
	 * @return Transition target
	 */
	@SuppressWarnings("unchecked")
	public static <A,B> B getTarget(AppContext conn, TransitionFactory<A,B> provider, HttpServletRequest req){
		B target = null ;
        target =  (B) req.getAttribute(TARGET_ATTRIBUTE);
        return target;
	}
	protected void setTarget(HttpServletRequest req,T q){
		req.setAttribute(TARGET_ATTRIBUTE, q);
	}
	
	
	public static class GetIDVisitor<T,K> implements TransitionFactoryVisitor<String, T, K>{
		public GetIDVisitor(T target) {
			super();
			this.target = target;
		}

		private final T target;

		public String visitTransitionProvider(TransitionProvider<K, T> prov) {
			return prov.getID(target);
		}

		public String visitPathTransitionProvider(
				PathTransitionProvider<K, T> prov) {
			StringBuilder path = new StringBuilder();
			for(String pe : prov.getID(target)){
				path.append("/");
				path.append(pe);
			}
			return path.toString();
		}
	}
	/** Add a button to perform the required operation on the target
	 * @param <A> 
	 * @param <B> 
	 * @param c   
	 * @param hb HtmlBulder to modify
	 * @param tp TransitionProvider
	 * @param operation
	 * @param target
	 * @param text 
	 * @return modified HtmlBuilder
	 */
	public static <A,B, X extends ExtendedXMLBuilder> X addButton(AppContext c,X hb, TransitionFactory<A,B> tp, A operation, B target,String text ){
		return addButton(c, hb, tp, operation, target, text,null);
	}
	public static <A,B, X extends ExtendedXMLBuilder> X addButton(AppContext c,X hb, TransitionFactory<A,B> tp, A operation, B target,String text,String title ){
		String url = getURL(c, tp, target, null);
		hb.open("form");
        hb.attr("method", "post");
        ServletService serv = c.getService(ServletService.class);
         if (serv != null) {
            hb.attr("action",serv.encodeURL(url));
         } else {
        	hb.attr("action",url); 
         }
         // pass operation as param by preference
         if(operation != null){
         hb.open("input"); 
            hb.attr("type","hidden"); 
            hb.attr("name", TRANSITION_KEY_ATTR); 
            hb.attr("value", operation.toString()); 
 
         hb.close();
         }
         hb.open("input");
          hb.attr("type","submit");
          hb.attr("value", text);
          if( title != null && title.trim().length() > 0){
          	hb.attr("title", title);
          }
         hb.close();
        hb.close();
		return hb;
	}
	public static <A,B, X extends ExtendedXMLBuilder> X addButton(AppContext c,X hb, ChainedTransitionResult<B, A> next,String text ){
		return addButton(c, hb, next.getProvider(),next.getTransition(),next.getTarget(), text);
	}
	public static <A,B, X extends ExtendedXMLBuilder> X addButton(AppContext c,X hb, ChainedTransitionResult<B, A> next,String text,String title ){
		return addButton(c, hb, next.getProvider(),next.getTransition(),next.getTarget(), text,title);
	}
	/** Add a link to perform the required operation on the target
	 * @param <A> 
	 * @param <B> 
	 * @param c   
	 * @param hb HtmlBulder to modify
	 * @param tp TransitionProvider
	 * @param operation
	 * @param target
	 * @param text 
	 * @return modified HtmlBuilder
	 */
	public static <A,B, X extends  ExtendedXMLBuilder> X addLink(AppContext c,X hb, TransitionFactory<A,B> tp, A operation, B target,String text ){
	    // as the servlet uses getParams we can pass the parameters in the servlet path
		String url = getURL(c, tp, target, operation);
		hb.open("a");
		ServletService serv = c.getService(ServletService.class);
         if (serv != null) {
            hb.attr("href",serv.encodeURL(url.toString()));
         } else {
        	hb.attr("href",url.toString()); 
         }
         hb.clean(text);
         hb.close();
		return hb;
	}
	
	@SuppressWarnings("unchecked")
	public static  ExtendedXMLBuilder addLink(AppContext c, ExtendedXMLBuilder hb,ChainedTransitionResult next, String text){
		return addLink(c,hb,next.getProvider(),next.getTransition(),next.getTarget(),text);
	}
	/** Get the TransitionServlet URL for a given TransitionProvider and target.
	 * If the TransitionProvider implements ViewTransitionProvider this will be the view URL
	 * of the target. A page invoked by forward is already at this URL and need
	 * not provide an action attribute to forms.
	 * 
	 * @param <A>
	 * @param <B>
	 * @param conn
	 * @param tp
	 * @param target
	 * @return String URL
	 */
	public static <A,B> String getURL(AppContext conn, TransitionFactory<A, B> tp,B target){
		return getURL(conn, tp, target,null);
	}
	public static <A,B> String getURL(HttpServletRequest req,HttpServletResponse res,AppContext conn, TransitionFactory<A, B> tp,B target){
		return res.encodeRedirectURL(req.getContextPath()+getURL(conn, tp, target,null));
	}
	
	public static <A,B> String getURL(AppContext conn, TransitionFactory<A, B> tp,B target, A operation){
		StringBuilder url = new StringBuilder();
		url.append(TRANSITION_SERVLET);
		url.append("/");
		url.append(tp.getTargetName());		
		url.append("/");
		
		if( target != null ){
			GetIDVisitor< B,A> vis = new GetIDVisitor<B,A>(target);
			url.append(tp.accept(vis));
		}
		if( operation != null ){
			url.append("/");
			url.append(DefaultServletService.ARG_TERRMINATOR);
			url.append("/");
			url.append(TRANSITION_KEY_ATTR);
			url.append("=");
			url.append(operation.toString());
		}
		return url.toString();
	}
	/** Register the most recent view transition for this provider for use by the {@link BackResult}
	 * passing a null target clears the memory.
	 * @param <T>
	 * @param <K>
	 * @param sess
	 * @param tp
	 * @param target
	 */
	@SuppressWarnings("unchecked")
	public static <T,K> void recordView(SessionService<?> sess, ViewTransitionFactory<K, T> tp, T target){
		if( target == null ){
			sess.removeAttribute(VIEW_TRANSITION+tp.getTargetName());
		}else{
			if( tp instanceof TransitionProvider){
				sess.setAttribute(VIEW_TRANSITION+tp.getTargetName(), ((TransitionProvider<K, T>)tp).getID(target));
			}else if( tp instanceof PathTransitionProvider){
				sess.setAttribute(VIEW_TRANSITION+tp.getTargetName(), ((PathTransitionProvider<K, T>)tp).getID(target));
			}
		}
	}
	public static <T,K> boolean hasViewRecorded(SessionService<?> sess, ViewTransitionFactory<K, T> tp){
		return sess.getAttribute(VIEW_TRANSITION+tp.getTargetName()) != null;
	}
}