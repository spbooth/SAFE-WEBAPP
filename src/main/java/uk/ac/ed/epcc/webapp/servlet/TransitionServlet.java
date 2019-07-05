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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CleanupService;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.HourTransform;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.exceptions.FatalTransitionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.html.DirectOperationResultVisitor;
import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;
import uk.ac.ed.epcc.webapp.forms.result.BackResult;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AnonymousTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.DefaultingTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.DirectTargetlessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.DirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.FormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TitleTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryCreator;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryFinder;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.ValidatingFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.exception.ForceRollBack;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.timer.TimerService;

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

@WebServlet(name="TransitionServlet",urlPatterns=TransitionServlet.TRANSITION_SERVLET+"/*")
public  class TransitionServlet<K,T> extends WebappServlet {

	/**
	 * 
	 */
	private static final String START_TRANSACTION_TIMER = "StartTransaction";
	/**
	 * 
	 */
	private static final String AQUIRE_LOCK_TIMER = "AquireProviderLock";
	/**
	 * 
	 */
	private static final String DATABASE_TRANSACTION_TIMER = "DatabaseTransaction";
	public static final String VIEW_TRANSITION = "ViewTransition.";
	public static final Feature TRANSITION_TRANSACTIONS = new Feature("transition.transactions", true, "Use database transaction within transitions");
	
	/** This is a security control. It is intended to prevent a malicious web-page from including
	 * image links that will be automatically fetched causing un-approved side effects.
	 * Real form transitions will always show the form page first which submits via post.
	 * Index and view transitions are always assumed to be safe and may be presented as links.
	 * Other direct transitions will not be accessible via a link unless the key implements
	 * {@link ViewTransitionKey} and reports the transition as safe.
	 * 
	 */
	public static final Feature MODIFY_ON_POST_ONLY= new Feature("transition.modify_on_post_only",true,"Only allow modification via post operations");
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//public static final String TARGET_ID = "TargetID";
	//public static final String TRANSITION_PROVIDER_PARAM = "type";
	public static final String TRANSITION_SERVLET = "/TransitionServlet";
	public static final String TRANSITION_PROVIDER_ATTR = "TransitionProvider";
	public static final String TRANSITION_KEY_ATTR = "Transition";
	public static final String TRANSITION_CSRF_ATTR ="TransitionCRSF";
	public static final String TARGET_ATTRIBUTE = "Target";
	@SuppressWarnings("unchecked")
	@Override
 	public void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn) {
		try{
			if( ! enabled(conn)) {
				res.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
				return;
			}
		// first find the transition provider
		ServletService servlet_service = conn.getService(ServletService.class);
		TimerService timer_service = conn.getService(TimerService.class);
	
		HTMLForm.setFormUrl(req, "/scripts/transition.jsp");
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
        SessionService<?> sess = getSessionService(conn,tp,params);
        if( ! (tp instanceof AnonymousTransitionFactory) && ( sess == null || ! sess.haveCurrentUser())){
        	sessionError(conn,sess,req, res);
        	return;
        }
        if( ! checkOrigin(conn,req, res)) {
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
					getLogger(conn).error("Transition key lookup failed for "+name+" provider="+tp.getTargetName());
					message(conn, req, res, "invalid_input");
					return;
				}
			}
		}
		// expected crsf token is set here for use by 
		// transition and view pages
		String crsf = getCRSFToken(conn,tp, key, target);
		if( crsf != null ) {
			log.debug("CRSF is "+crsf);
			req.setAttribute(TRANSITION_CSRF_ATTR, crsf);
		}else {
			req.removeAttribute(TRANSITION_CSRF_ATTR);
		}
		boolean non_modifying=false; // allow get method
		if( key == null && target == null && tp instanceof IndexTransitionFactory){
			// IndexTransitionProvider can generate an index
			key = ((IndexTransitionFactory<K, T>)tp).getIndexTransition();
			non_modifying=(key != null); // non modifying usually
		}
		if( key == null && target != null && tp instanceof DefaultingTransitionFactory){
			// if we have a target maybe there is a default transition for it.
			key = ((DefaultingTransitionFactory<K, T>)tp).getDefaultTransition(target);
			non_modifying = (key != null); // non modifying usually
		}
		if( key == null){
			if( target != null && tp instanceof ViewTransitionFactory){
				log.debug("Redirecting to View page");
				ViewTransitionFactory<K, T> vtp = (ViewTransitionFactory<K, T>)tp;
				if( vtp.canView(target, sess)){
					// Note we use ChainedTransitionResult not a ViewTransitionResult as the latter
					// would redirect back to this servlet and e
					handleFormResult(conn, req, res, new ChainedTransitionResult<>(vtp, target,null));
				}else{
					message(conn, req, res, "access_denied");
				}
				return;
			}else{
				log.debug("No transition");
				message(conn, req, res, "invalid_input");
				return;
			}
		}
		// must have a key here.
		if( key instanceof ViewTransitionKey) {
			non_modifying = ((ViewTransitionKey<T>)key).isNonModifying(target);
		}
		log.debug("transition="+key);
	    // this is the access control
		if( ! tp.allowTransition(conn,target,key)){
			message(conn, req, res, "access_denied");
        	return;
		}
		Transition<T> t = tp.getTransition(target,key);
		if( t != null ){
			// check for trivial FormResults that don't need a lock
			o = t.getResult(getShortcutVisitor(conn, params, tp, target, key));
			if( o == null){
				log.debug("No shortcut result");
				if( ! (non_modifying || req.getMethod().equalsIgnoreCase("POST"))) {
					getLogger(conn).error("Modify not from POST");
					if( MODIFY_ON_POST_ONLY.isEnabled(conn)) {
						res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			        	return;
					}
				}
				// crsf check. If get is allowed it is explicitly non-modifying and
				// potentially book-markable.
				if( crsf != null && ! non_modifying) {
					// potential modifying with a set token
					String submitted_crsf = (String) params.get(TRANSITION_CSRF_ATTR);
					if( submitted_crsf == null || ! crsf.equals(submitted_crsf)) {
						//Failed
						log.warn("CRSF token mis-match "+submitted_crsf+"!="+crsf);
						sess.logOut();
						message(conn, req, res, "crsf_check_failed");
						return;
					}
				}
				long start=0L,aquired=0L;
				long max_wait=conn.getLongParameter("max.transition.millis", 30000L);
				if( timer_service != null){
					timer_service.startTimer(AQUIRE_LOCK_TIMER);
				}
				start = System.currentTimeMillis();
				// to ensure consistency all access/modifications of target objects and the checks
				// that operations are valid, are protected by a lock.
				// We use the class of the TransitionProvider as the lock object to only
				// sequentialise operations on the same type of object. 
				// Synchronize outside the database transaction. If the operation is not transaction
				// safe then we want to ensure the transaction is committed before allowing a second
				// transaction to start. 
				
				// This would still go wrong on distributed infrastructure 
				// depending on the isolation enforced the transaction might take a database lock 
				// which could also block and resolve this.
				synchronized(tp.getClass()){
					if( timer_service != null){
						timer_service.stopTimer(AQUIRE_LOCK_TIMER);
					}
					// Transitions should take place in a transaction
					// This helps keeps them atomic even on distributed infrastructure.
					DatabaseService serv = conn.getService(DatabaseService.class);
					boolean use_transactions = serv != null && TRANSITION_TRANSACTIONS.isEnabled(conn) && ! non_modifying;
					if (use_transactions){
						if(timer_service != null){
							timer_service.startTimer(DATABASE_TRANSACTION_TIMER);
							timer_service.startTimer(START_TRANSACTION_TIMER);
						}
						serv.startTransaction();
						if(timer_service != null){
							timer_service.stopTimer(START_TRANSACTION_TIMER);
						}
						// re-query for the target within the transaction to ensure consistent state
						if( target != null && target instanceof DataObject){
							((DataObject)target).release(); // remove from cache
						}
						target = getTarget(conn,tp,path);
						if( target != null ){
							req.setAttribute(TARGET_ATTRIBUTE, target);
						}
						if( ! tp.allowTransition(conn,target,key)){
							message(conn, req, res, "access_denied");
				        	return;
						}
						t = tp.getTransition(target,key); // transition may depend on target e.g confirm transition
					}
					try{

						aquired=System.currentTimeMillis();
						


						if( t != null ){
							o = processTransition(conn, req, params, tp, key, target, t);
						}


					}catch(TransitionException e){
						// Assume no roll-back needed unless and explicit fatal exception It might be ok 
						// to  allways roll-back here.
						// and assume that the DB can perform a null roll-back cheaply.
						if( e instanceof FatalTransitionException){
							// Normally we would log before throwing this to inform the user of failure
							// however to be safe log again.
							log.error("FatalTransitionException", e);
							if (use_transactions){
								serv.rollbackTransaction();
								log.warn("Rolling back transaction in TransitionServlet");
								CleanupService clean = conn.getService(CleanupService.class);
								if( clean != null ) {
									// remove queued actions
									clean.reset();
								}
								
							}
						}
						// These are typically user errors
						log.info("transition exception", e);
						message(conn, req, res, "transition_error",  key, e.getMessage());
						return;
					}catch(Exception|ForceRollBack tr){
						if (use_transactions){
							// assume this is bad and roll-back
							serv.rollbackTransaction();
							log.warn("Rolling back transaction in TransitionServlet");
							CleanupService clean = conn.getService(CleanupService.class);
							if( clean != null ) {
								// remove queued actions
								clean.reset();
							}
						}
						if( tr instanceof ForceRollBack) {
							serv.closeRetainedClosables();
							message(conn, req, res, "force_rollback",  key, tr.getMessage());
							return;
						}else {
							throw tr;
						}
					}finally{
						if (use_transactions){
							// restore original mode (usually auto-commit)
							serv.stopTransaction();
							if(timer_service != null){
								timer_service.stopTimer(DATABASE_TRANSACTION_TIMER);
							}
						}
						try{
							long now = System.currentTimeMillis();
							if((now-aquired) > max_wait){
								long secs = (now-aquired)/1000L;
								// This transition has taken a long time
								log.warn("Long transition "+secs+" seconds ("+HourTransform.toHrsMinSec(secs)+") provider="+tp.getTargetName()+" target="+getLogTag(tp,target)+" key="+key);
							}else if( now-start > max_wait){
								// Long time waiting for lock
								long secs = (now-start)/1000L;
								log.warn("Blocked transition "+secs+" seconds ("+HourTransform.toHrsMinSec(secs)+") provider="+tp.getTargetName()+" target="+getLogTag(tp,target)+" key="+key);
							}
						}catch(Exception tr){
							log.error("Problem reporting transition timimgs",tr);
						}
					}
				}
			}else{
				log.debug("shortcut result generated");
			}
		}
		if( o == null ){
			// something has gone wrong no FormResult
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
		}catch(Exception tr){
			getLogger(conn).error("Exception caught in TransitionServlet",tr);
			try {
				message(conn, req, res, "internal_error");
			}catch(Exception e){
				getLogger(conn).error("Exception sending message in TransitionServlet",e);
			}
		}
	}
	protected TransitionVisitor<T> getShortcutVisitor(AppContext conn, Map<String, Object> params,
			TransitionFactory<K, T> tp, T target, K key) {
		return new ShortcutServletTransitionVisitor<>(conn, key, tp, target, params);
	}
	/** Extension point for missing authorisation
	 * @param conn
	 * @param sess
	 * @param req
	 * @param res
	 * @throws IOException 
	 * @throws ServletException 
	 */
	protected void sessionError(AppContext conn, SessionService sess,HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		conn.getService(ServletService.class).requestAuthentication(sess);
		
	}
	protected FormResult processTransition(AppContext conn,
			HttpServletRequest req, Map<String, Object> params,
			TransitionFactory<K, T> tp, K key, T target, Transition<T> t)
					throws Exception {
		FormResult result = t.getResult(new ServletTransitionVisitor<>(conn, req, key, tp, target, params));
		if( result != null) {
			// execute any direct transitions we can.
			// This avoids getting a submit-only form when direct transitions chain
			// and places a chain of direct transitions into a single transaction
			DirectOperationResultVisitor vis = new DirectOperationResultVisitor(conn);
			result.accept(vis);
			return vis.getFinalResult();
		}
		return null;
	}
	/** Extension point to get the session including any custom login code based on the parameters and the {@link TransitionFactory}
	 * This allows sub-classes to that parse the parameters for login credentials or look for annotations on the {@link TransitionFactory}.
	 * @param conn
	 * @param tp
	 * @param raw_params
	 * @return
	 */
	protected SessionService getSessionService(AppContext conn, TransitionFactory<K,T> tp,Map<String,Object> raw_params) {
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
	return new TransitionFactoryFinder(conn).getProviderFromName(type);
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
			this.path = new LinkedList<>(path); // non destructive as we may need to re-aquire target within lock
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
		GetTargetVisitor<T,K> vis = new GetTargetVisitor<>(path);
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
	public static String getCrsfToken(AppContext conn, HttpServletRequest req) {
		return (String) req.getAttribute(TRANSITION_CSRF_ATTR);
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
			boolean seen=false;
			for(String pe : prov.getID(target)){
				if( seen ) {
					path.append("/");
				}else {
					seen=true;
				}
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
		return addButton(c, hb, tp, operation, target, text, title,false);
	}
	public static <A,B, X extends ExtendedXMLBuilder> X addButton(AppContext c,X hb, TransitionFactory<A,B> tp, A operation, B target,String text,String title, boolean new_tab ){
		if( operation == null ) {
			// maybe view, for the moment assume a default transition will be passed explicitly
			if( tp instanceof ViewTransitionFactory) {
				if( ! ((ViewTransitionFactory<A, B>)tp).canView(target, c.getService(SessionService.class))){
					return hb;
				}
			}else {
				return hb;
			}
		}else if( ! tp.allowTransition(c, target, operation)) {
			// not allowed
			return hb;
		}
		String url = getURL(c, tp, target, null);
		hb.open("form");
        hb.attr("method", "post");
        ServletService serv = c.getService(ServletService.class);
         if (serv != null) {
            hb.attr("action",serv.encodeURL(url));
         } else {
        	hb.attr("action",url); 
         }
         if( new_tab) {
				hb.attr("formtarget","_blank");
		 }
         // pass operation as param by preference
         if(operation != null){

        	 // Only really need this for direct transitions
        	 CrsfTokenService crsf_serv = c.getService(CrsfTokenService.class);
        	 if( crsf_serv != null ) {
        		 boolean add_token=true;
        		 if( operation instanceof ViewTransitionKey && ((ViewTransitionKey<B>)operation).isNonModifying(target)) {
        			 add_token=false;
        		 }
        		 if( add_token) {
        			 String crsf = crsf_serv.getCrsfToken(tp, target);
        			 if( crsf != null ) {
        				 hb.open("input"); 
        				 hb.attr("type","hidden"); 
        				 hb.attr("name", TRANSITION_CSRF_ATTR); 
        				 hb.attr("value", crsf); 

        				 hb.close();
        			 }
        		 }
        	 }
        	 hb.open("input"); 
        	   hb.attr("type","hidden"); 
        	   hb.attr("name", TRANSITION_KEY_ATTR); 
        	   hb.attr("value", operation.toString()); 
        	 hb.close();
         }
         hb.open("input");
          hb.addClass("input_button");
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
	public static <A,B, X extends  ExtendedXMLBuilder> X addLink(AppContext c,X hb, TransitionFactory<A,B> tp, A operation, B target,String text, String hover,boolean new_tab ){
		// do some explicit null checks, had some non-reproducable null pointer exceptions once
		if( tp == null) {
			c.getService(LoggerService.class).getLogger(TransitionServlet.class).error("Bad arguement for addLink",new InvalidArgument("No transition provider"));
			hb.clean(text);
			return hb;
		}
	    // as the servlet uses getParams we can pass the parameters in the servlet path
		String url = getURL(c, tp, target, operation);
		// Check that MODIFY_ON_POST check will pass
		if( operation != null ) {
			if( (! (operation instanceof ViewTransitionKey)) || ! ((ViewTransitionKey) operation).isNonModifying(target)) {
				// Not permitted to link to a direct transition should use a post form.
				// generate an error if we display the link don't wait
				// for user to click on it.
				try {
					Transition<B> transition = tp.getTransition(target, operation);
					if( transition == null ) {
						throw new InvalidArgument("Null transition returned for "+tp.getTargetName()+" "+operation);
					}
					transition.getResult(new TransitionVisitor<B>() {

						@Override
						public FormResult doDirectTransition(DirectTransition<B> t) throws TransitionException {
							throw new TransitionException("Direct Transition");
						}

						@Override
						public FormResult doDirectTargetlessTransition(DirectTargetlessTransition<B> t)
								throws TransitionException {
							throw new TransitionException("DirectTargetless Transition");
						}

						@Override
						public FormResult doFormTransition(FormTransition<B> t) throws TransitionException {
							// form transition will display form page
							return null;
						}

						@Override
						public FormResult doValidatingFormTransition(ValidatingFormTransition<B> t)
								throws TransitionException {
							return null;
						}

						@Override
						public FormResult doTargetLessTransition(TargetLessTransition<B> t) throws TransitionException {
							return null;
						}
					});
				} catch (Exception e) {
					c.getService(LoggerService.class).getLogger(TransitionServlet.class).error("Link to modifying transition "+url,e);
					// just show link text and return.
					hb.clean(text);
					return hb;
				}
				
			}
		}
		hb.open("a");
		ServletService serv = c.getService(ServletService.class);
         if (serv != null) {
            hb.attr("href",serv.encodeURL(url.toString()));
         } else {
        	hb.attr("href",url.toString()); 
         }
         if( hover != null && ! hover.isEmpty() && ! hover.equals(text)){
        	 hb.attr("title", hover);
         }
         if( new_tab ) {
        	 hb.attr("target", "_blank");
         }
         hb.clean(text);
         hb.close();
		return hb;
	}
	
	@SuppressWarnings("unchecked")
	public static  ExtendedXMLBuilder addLink(AppContext c, ExtendedXMLBuilder hb,ChainedTransitionResult next, String text){
		return addLink(c,hb,next.getProvider(),next.getTransition(),next.getTarget(),text,null,false);
	}
	@SuppressWarnings("unchecked")
	public static  ExtendedXMLBuilder addLink(AppContext c, ExtendedXMLBuilder hb,ChainedTransitionResult next, String text,String hover){
		return addLink(c,hb,next.getProvider(),next.getTransition(),next.getTarget(),text,hover,false);
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
		return getURL(req,res,conn, tp, target,null);
	}
	public static <A,B> String getURL(HttpServletRequest req,HttpServletResponse res,AppContext conn, TransitionFactory<A, B> tp,B target, A operation){
		return res.encodeRedirectURL(req.getContextPath()+getURL(conn, tp, target,operation));
	}
	public static <A,B> String getURL(AppContext conn, TransitionFactory<A, B> tp,B target, A operation){
		StringBuilder url = new StringBuilder();
		url.append(TRANSITION_SERVLET);
		url.append("/");
		url.append(tp.getTargetName());		
		url.append("/");
		
		if( target != null ){
			GetIDVisitor< B,A> vis = new GetIDVisitor<>(target);
			url.append(tp.accept(vis));
		}
		if( operation != null ){
			if( tp instanceof DefaultingTransitionFactory) {
				A def = ((DefaultingTransitionFactory<A, B>)tp).getDefaultTransition(target);
				if( def != null && operation.equals(def)) {
					// Don't need to specify
					return url.toString();
				}
			}
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
	public static <T,K> String getPageTitle(TransitionFactory<K, T> tp, K key, T target) {
		if( tp instanceof TitleTransitionFactory){
	    	TitleTransitionFactory ttp = (TitleTransitionFactory)tp;
	    	return ttp.getTitle(key, target);
	    }else{
	    	String type=tp.getTargetName();
	    	AppContext conn = tp.getContext();
			String type_title = conn.getInitParameter("transition_title."+type,type);
	    	String service_name = conn.getInitParameter("service.name", "");
	    	String action = key.toString();
	    	if( type==null) type="";
			return service_name+" "+deCamel(action)+" "+type_title;
	    }
	}
	public static <T,K> String getPageHeader(TransitionFactory<K, T> tp, K key, T target) {
		if( tp instanceof TitleTransitionFactory){
			TitleTransitionFactory ttp = (TitleTransitionFactory)tp;
			return ttp.getHeading(key, target);
		}else{
			AppContext conn = tp.getContext();
			String type=tp.getTargetName();
			String action = key.toString();
			String type_title = conn.getInitParameter("transition_title."+type,type);
			return deCamel(action)+" "+type_title;
		}
	}
	private static String deCamel(String name) {
		boolean isLower=false;
		StringBuilder sb = new StringBuilder();
		for( int i=0 ; i< name.length(); i++) {
			Character ch = name.charAt(i);
			if( Character.isUpperCase(ch) && isLower) {
				sb.append(" ");
			}
			isLower=Character.isLowerCase(ch);
			sb.append(ch);
		}
		return sb.toString();
	}
	/** generate a CRSF token to be included in form posts.
	 * 
	 * Returning a null value disables the check.
	 * 
	 * @param fac
	 * @param key
	 * @param target
	 * @return token or null
	 */
	protected String getCRSFToken(AppContext conn,TransitionFactory<K,T> fac, K key, T target) {
		CrsfTokenService serv = conn.getService(CrsfTokenService.class);
		if( serv != null ) {
			return serv.getCrsfToken(fac, target);
		}
		return null;
	}
	/** check headers for csrf etc.
	 * 
	 * @param req
	 * @param res
	 * @return true to continue processing
	 */
	public boolean checkOrigin(AppContext conn,HttpServletRequest req, HttpServletResponse res) {
//		String origin = req.getHeader("Origin");
//		Logger logger = getLogger(conn);
//		logger.debug("origin is "+origin);
//		String referer = req.getHeader("Referer");
//		logger.debug("referer is "+referer);
		return true;
	}
	
	/** Extension point to allow sub-classes to enable/disable the servlet
	 * 
	 * @return
	 */
	protected boolean enabled(AppContext conn) {
		return true;
	}
}