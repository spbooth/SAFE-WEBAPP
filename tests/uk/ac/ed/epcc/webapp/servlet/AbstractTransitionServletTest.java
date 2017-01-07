//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.junit.Before;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.TestDataHelper;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.MapForm;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.CustomPage;
import uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.CustomFormContent;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraContent;
import uk.ac.ed.epcc.webapp.forms.transition.FormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.NavigationProvider;
import uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.ShowDisabledTransitions;
import uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TitleTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.mock.MockRequest;
import uk.ac.ed.epcc.webapp.mock.MockResponse;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.model.data.XMLDataUtils;
import uk.ac.ed.epcc.webapp.session.SessionService;
import static org.junit.Assert.*;
/** A Customised version {@link ServletTest} for testing the
 * {@link TransitionServlet}. Many different operations use {@link Transition}s
 * this can be used to build high level tests of them.
 * 
 * @author spb
 * @param <K> 
 * @param <T> 
 *
 */

public abstract class AbstractTransitionServletTest extends ServletTest {

	
	@Before
	public void setConfig() throws ServletException{
		servlet=new TransitionServlet();
		MockServletConfig config = new MockServletConfig(serv_ctx, "TransitionServlet");
		servlet.init(config);
	}
	
	
	/**
	 * 
	 */
	public AbstractTransitionServletTest() {
		// TODO Auto-generated constructor stub
	}

	/** setup the request for a transition. 
	 * The request is populated with the default form parameters
	 * (equivalent to the initial state of the transition form).
	 * Non default Form parameters need to be added separately before calling
	 * {@link #runTransition()}.
	 * 
	 * @param provider
	 * @param key
	 * @param target
	 * @throws TransitionException 
	 */
	public <K,T> void setTransition(TransitionFactory<K, T> provider, K key, T target) throws TransitionException{
		
		req.servlet_path="TransitionServlet";
		StringBuilder path = new StringBuilder();
		path.append(provider.getTargetName());
		TransitionFactory<K,T> prov2 = TransitionServlet.getProviderFromName(getContext(), provider.getTargetName());
		assertEquals("Provider targetName does not generate same class",provider.getClass(), prov2.getClass());
		assertEquals("generated provider does not produce same target name", provider.getTargetName(),prov2.getTargetName());
		if( target != null ){
			if( provider instanceof TransitionProvider){
				path.append("/");
				path.append(((TransitionProvider<K,T>)provider).getID(target));
			}else if( provider instanceof PathTransitionProvider){
				for(String p : ((PathTransitionProvider<K, T>)provider).getID(target)){
					path.append("/");
					path.append(p);
				}
			}
		}
		
		req.path_info=path.toString();
		if( key != null ){
			req.params.put(TransitionServlet.TRANSITION_KEY_ATTR, key.toString());
			Transition<T> transition = provider.getTransition(target, key);
			if(transition != null &&  (transition instanceof BaseFormTransition || transition instanceof TargetLessTransition)){
				req.params.put("Transition", key.toString());
				req.params.put("transition_form", "true");
				req.params.put("form_url", "/scripts/transition.jsp");
				MapForm f = new MapForm(getContext());
				if( transition instanceof BaseFormTransition){
					BaseFormTransition ft = (BaseFormTransition)transition;
					ft.buildForm(f, target, getContext());
					f.addStringMap(req.params);
				}else if( transition instanceof TargetLessTransition){
					((TargetLessTransition)transition).buildForm(f, getContext());
					f.addStringMap(req.params);
				}
				
			}
		}
	}
	
	/** invoke the servlet with the current state of the request object
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	public void runTransition() throws ServletException, IOException{
		doPost();
	}
	
	/** Check that the result is consistent with a chained transition.
	 * This should always be a form tranition of some sort not a view transition.
	 * 
	 * this call also sets up the expected transition (using {@link #resetRequest()} {@link #setTransition(TransitionFactory, Object, Object)} so the test 
	 * also call this next transition if desired.
	 * 
	 * @param provider
	 * @param key
	 * @param target
	 * @throws TransitionException
	 */
	public <K,T> void checkTransitionForward(TransitionFactory<K, T> provider, K key, T target) throws TransitionException{
		assertNotNull(provider);
		assertNotNull(key);
		Transition t = provider.getTransition(target, key);
		assertNotNull(t);
		assertTrue( t instanceof BaseFormTransition || t instanceof TargetLessTransition);
		assertEquals(t instanceof TargetLessTransition, target == null);
		checkAttributes(provider, key, target);
		checkForward("/scripts/transition.jsp");

		// Setup the transition for next operation
		resetRequest();
		setTransition(provider, key, target);
		return;
	}

	public <K, T> void checkAttributes(TransitionFactory<K, T> provider, K key,
			T target) {
		assertNotNull(provider);
		TransitionFactory transitionFactory = (TransitionFactory)req.getAttribute(TransitionServlet.TRANSITION_PROVIDER_ATTR);
		assertNotNull(transitionFactory);
		assertEquals(provider.getTargetName(), transitionFactory.getTargetName());
		if( key == null ){
			assertNull(req.getAttribute(TransitionServlet.TRANSITION_KEY_ATTR));
		}else{
			assertEquals(key, req.getAttribute(TransitionServlet.TRANSITION_KEY_ATTR));
		}
		if( target == null ){
			assertNull(req.getAttribute(TransitionServlet.TARGET_ATTRIBUTE));
		}else{
			assertEquals(target, req.getAttribute(TransitionServlet.TARGET_ATTRIBUTE));
		}
	}
	
	/** check that the result is consistent with a forward to the view_target page. 
	 * Normally this should not be needed in tests as most operations should be written 
	 * to use a redirect to the canonical object URL to generate the view page.
	 * You could call this after {@link #checkViewRedirect(ViewTransitionFactory, Object)}
	 * to verify the servlet does the correct thing with a view url but this would be testing the servlet not
	 * the transition class.
	 * @param provider
	 * @param target
	 */
	public <K,T> void checkViewForward(ViewTransitionFactory<K, T> provider, T target){
		checkAttributes(provider, null, target);
		checkForward("/scripts/view_target.jsp");
	}
	
	/** This is the normal view result. These should always redirect to the canonical url to
	 * generate a view page
	 * @param provider
	 * @param target
	 * @throws TransitionException 
	 */
	public <K,T> void checkViewRedirect(ViewTransitionFactory<K, T> provider, T target) throws TransitionException{
		checkRedirectToTransition(provider, null, target);
	}

	@Override
	public <K, T> void checkRedirectToTransition(TransitionFactory<K, T> fac,
			K key, T target) throws TransitionException {
		super.checkRedirectToTransition(fac, key, target);
		// Setup the transition for next operation
		resetRequest();
		setTransition(fac, key, target);
		return;
	}
	
	/** check for a non-bookmarkable chained transition.
	 * Note this should always be a form transition.
	 * 
	 * These are normally used where one form transition selects the target for another. If the first transition
	 * changes state then the transition should really have used a redirect.
	 * 
	 * @see ChainedTransitionResult#useURL()
	 * @param fac
	 * @param key
	 * @param target
	 * @throws TransitionException
	 */
	public <K, T> void checkForwardToTransition(TransitionFactory<K, T> fac,
			K key, T target) throws TransitionException {
		if( target != null ){
			assertEquals(target, req.getAttribute(TransitionServlet.TARGET_ATTRIBUTE));
		}
		assertNotNull(key);
		assertEquals(key, req.getAttribute(TransitionServlet.TRANSITION_KEY_ATTR));
		assertEquals(fac, req.getAttribute(TransitionServlet.TRANSITION_PROVIDER_ATTR));
		checkForward("/scripts/transition.jsp");
		resetRequest();
		setTransition(fac, key, target);
		return;
	}
	
	/** verify that the transition resulted in a form error.
	 * 
	 * @param param
	 * @param error
	 */
	public void checkError(String param, String error){
		checkError("/scripts/transition.jsp", param,error);
	}
	
	public void checkGeneralError(String error){
		checkError(HTMLForm.GENERAL_ERROR,error);
	}
	/** Generates a XML (mostly HTML) representation of the 
	 * contents of the transition form page that can be directly 
	 * influenced by the transform. This is then (optionally) put through a
	 * normalisation XLST transform to remove time dependent 
	 * output and compared with a file of expected output.
	 * 
	 * 
	 * @param normalise_transform
	 * @param expected
	 * @throws Exception 
	 */
	public <K,T> void checkFormContent(String normalize_transform, String expected_xml) throws Exception{
		HtmlBuilder builder = new HtmlBuilder();
		builder.setValidXML(true);
		builder.open("transition_page");
		String name = req.path_info;
		if( name.contains("/")){
			name = name.substring(0, name.indexOf('/'));
		}
		TransitionFactory<K, T> factory = TransitionServlet.getProviderFromName(getContext(), name);
		assertNotNull(factory);
		T target= TransitionServlet.getTarget(getContext(),factory,req);
		if( target == null ){
			// do things the hard way as per TransitionsServlet
			ServletService serv = ctx.getService(ServletService.class);
			LinkedList<String> args = serv.getArgs();
			args.removeFirst();
			target = ((TransitionServlet<K,T>)servlet).getTarget(getContext(),factory,args);
		}
		K key = (K) req.getAttribute(TransitionServlet.TRANSITION_KEY_ATTR);
		if( key == null ){
			key = (K) factory.lookupTransition(target, req.getParameter(TransitionServlet.TRANSITION_KEY_ATTR));
		 }
		 assertNotNull(key);
		 if( factory instanceof TitleTransitionFactory){
			 TitleTransitionFactory ttf = (TitleTransitionFactory) factory;
			builder.open("Title");
				builder.clean(ttf.getTitle(key, target));
			builder.close();
			builder.open("PageHeading");
				builder.clean(ttf.getHeading(key, target));
			builder.close();
		 }
		 if( factory instanceof NavigationProvider){
			 NavigationProvider<K, T> np = (NavigationProvider<K, T>) factory;
			 builder.open("TopNavigation");
			 	np.getTopNavigation(builder, target);
			 builder.close();
			 
		 }
		 if( target != null ){
			 builder.open("SummaryContent");
			 	factory.getSummaryContent(getContext(), builder, target);
			 builder.close();
		 }
		 Transition t = factory.getTransition(target,key);
		 assertNotNull(t);
		 if( t instanceof ExtraContent ){
			 builder.open("ExtraContent");
			 	((ExtraContent)t).getExtraHtml(builder, getContext().getService(SessionService.class), target);
			 builder.close();
		 }
		 HTMLForm f = new HTMLForm(getContext());
		 if( t instanceof BaseFormTransition ){
		 	BaseFormTransition ft = (BaseFormTransition) t;
		 	ft.buildForm(f,target,getContext());
		 }else if( t instanceof TargetLessTransition ){
		 	TargetLessTransition tlt = (TargetLessTransition) t;
		 	((TargetLessTransition)t).buildForm(f,getContext());
		 }
		 builder.open("Form");
		 if( t instanceof CustomFormContent ){
			 ((CustomFormContent)t).addFormContent(builder, getContext().getService(SessionService.class), f, target);
		 }else{
			 builder.addFormTable(getContext(), f);
			 builder.addActionButtons(f);
		 }
		 builder.close();
        
		 if( factory instanceof NavigationProvider){
			 NavigationProvider<K, T> np = (NavigationProvider<K, T>) factory;
			 builder.open("BottomNavigation");
			 np.getBottomNavigation(builder, target);
			 builder.close();

		 }
		 builder.close();
		 String xml = builder.toString();
		 checkContent(normalize_transform, expected_xml, xml);
		 
	}
	/** Generates a XML (mostly HTML) representation of the 
	 * contents of the view page that can be directly 
	 * influenced by the transform. This is then (optionally) put through a
	 * normalisation XLST transform to remove time dependent 
	 * output and compared with a file of expected output.
	 * 
	 * 
	 * @param normalise_transform
	 * @param expected
	 * @return view target
	 * @throws Exception 
	 */
	public <K,T> Object checkViewContent(String normalize_transform, String expected_xml) throws Exception{
		HtmlBuilder builder = new HtmlBuilder();
		builder.setValidXML(true);
		builder.open("view_page");
		String name = req.path_info;
		if( name.contains("/")){
			name = name.substring(0, name.indexOf('/'));
		}
		TransitionFactory<K, T> tp = TransitionServlet.getProviderFromName(getContext(), name);
			Object target =   TransitionServlet.getTarget(getContext(),tp,req); // This looks in the attribute
		    if( target == null ){
		    	// do things the hard way as per TransitionsServlet
		    	ServletService serv = ctx.getService(ServletService.class);
		    	LinkedList<String> args = serv.getArgs();
		    	args.removeFirst();
		    	target = ((TransitionServlet)servlet).getTarget(getContext(),tp,args);
		    }
			
			assertNotNull(tp);
			assertNotNull(target);
			assertTrue(tp instanceof ViewTransitionFactory); 
		   
		    ViewTransitionFactory provider = (ViewTransitionFactory) tp;
		    SessionService session_service = getContext().getService(SessionService.class);
		    assertTrue( provider.canView(target,session_service));
		    
		    

		if( tp instanceof TitleTransitionFactory){
		    builder.open("Title");	
			TitleTransitionFactory ttp = (TitleTransitionFactory)tp;
			builder.clean(ttp.getTitle(null, target));
			builder.close();
		}else{
			
			builder.open("Title");	
			String type=provider.getTargetName();
		    String type_title = getContext().getInitParameter("transition_title."+type,type);
			builder.clean("View "+type_title);
			builder.close();
		}
	    TransitionServlet.recordView(session_service,provider,target);
		builder.open("top");
		  provider.getTopContent(builder,target,session_service);
		builder.close();
		builder.open("log");
		  provider.getLogContent(builder,target,session_service);
		builder.close();
		builder.open("buttons");
		for(Object key : provider.getTransitions(target)){
			if( provider.allowTransition(getContext(),target,key) ){
				builder.open("active");
				  builder.attr("value", key.toString());
				  String help=provider.getHelp(key);
				  if( help != null ){
					builder.attr("help",help);
				  }
				builder.close();
			}else{
				if( provider instanceof ShowDisabledTransitions){
					if(((ShowDisabledTransitions)provider).showDisabledTransition(ctx, target, key)){
					builder.open("disabled");
					  builder.attr("value",key.toString());
					  String help=provider.getHelp(key);
					  if( help != null ){
						builder.attr("help",help+" (disabled)");
					  }
					builder.close();
					}
				}
			}
		}
		builder.close();
		
		builder.close();
		 
		 
		String content = builder.toString();
		 checkContent(normalize_transform, expected_xml, content);
		 return target;
	}
	
	
}