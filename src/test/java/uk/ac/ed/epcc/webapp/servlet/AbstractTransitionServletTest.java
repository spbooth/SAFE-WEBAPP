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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.ServletException;

import org.junit.Before;

import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.SetParamVisitor;
import uk.ac.ed.epcc.webapp.forms.SimpleFormTextGenerator;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.html.BaseHTMLForm;
import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ConfirmTransition;
import uk.ac.ed.epcc.webapp.forms.transition.CustomFormContent;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraContent;
import uk.ac.ed.epcc.webapp.forms.transition.NavigationProvider;
import uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.ShowDisabledTransitions;
import uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TitleTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** A Customised version {@link ServletTest} for testing the
 * {@link TransitionServlet}. Many different operations use {@link Transition}s
 * this can be used to build high level tests of them.
 * 
 * @author spb 
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
		setTransition(provider, key, target,true);
	}
	public <K,T> void setTransition(ChainedTransitionResult<T, K> result) throws TransitionException {
		setTransition(result.getProvider(),result.getTransition(),result.getTarget());
	}
	public <K,T> void setTransition(TransitionFactory<K, T> provider, K key, T target, boolean from_form) throws TransitionException{
		resetRequest();
		req.servlet_path="TransitionServlet";
		StringBuilder path = new StringBuilder();
		path.append(provider.getTargetName());
		TransitionFactory<K,T> prov2 = TransitionServlet.getProviderFromName(getContext(), provider.getTargetName());
		assertNotNull("Provider not registered "+provider.getTargetName(),prov2);
		assertEquals("Provider targetName="+provider.getTargetName()+" does not generate same class",provider.getClass(), prov2.getClass());
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
		if( from_form) {
		String csrf = ((TransitionServlet)servlet).getCRSFToken(ctx, provider, key, target);
		if( csrf != null) {
			req.params.put(TransitionServlet.TRANSITION_CSRF_ATTR, csrf);
		}
		if( key != null ){
			req.params.put(TransitionServlet.TRANSITION_KEY_ATTR, key.toString());
			Transition<T> transition = provider.getTransition(target, key);
			if(transition != null &&  (transition instanceof BaseFormTransition || transition instanceof TargetLessTransition)){
				req.params.put("Transition", key.toString());
				if( populateForm()) {
					// Set the default form parameters and hidden parameters
					req.params.put("transition_form", "true");
					HTMLForm f = new HTMLForm(getContext(),new ChainedTransitionResult<T, K>(provider, target, key));

					if( transition instanceof BaseFormTransition){
						BaseFormTransition ft = (BaseFormTransition)transition;
						ft.buildForm(f, target, getContext());

					}else if( transition instanceof TargetLessTransition){
						((TargetLessTransition)transition).buildForm(f, getContext());

					}
					if( f.getTargetStage() > 0 ) {
						req.params.put(BaseHTMLForm.FORM_STAGE_INPUT, Integer.toString(f.getTargetStage()));
					}
					f.addStringMap(req.params);
				}
			}
		}
		}else {
			// setup for post before from is shown
			if( key != null ){
				req.params.put(TransitionServlet.TRANSITION_KEY_ATTR, key.toString());
			}
		}
	}
	
	/** invoke the servlet with the current state of the request object
	 * checks 
	 * @throws ServletException
	 * @throws IOException
	 */
	public void runTransition() throws ServletException, IOException{
		runTransition(0);
	}
	/**
	 * 
	 * @param expected_stages number of expected additional transaction stages (-ve for allow any)
	 * @throws ServletException
	 * @throws IOException
	 */
	public void runTransition(int expected_stages) throws ServletException, IOException{
		doPost();
		if( expected_stages >= 0 ) {
			DatabaseService db = ctx.getService(DatabaseService.class);
			if( db != null ) {
				assertEquals("Number of transaction stages in transition",expected_stages, db.transactionStage());
			}
		}
		
	}
	
	

	@Override
	public <K, T> void checkRedirectToTransition(TransitionFactory<K, T> fac,
			K key, T target) throws TransitionException {
		super.checkRedirectToTransition(fac, key, target);
		// Setup the transition for next operation
		resetRequest();
		setTransition(fac, key, target);
		req.method="GET";
		return;
	}
	
	/** check for a non-bookmarkable chained transition.
	 * Note this should always be a form transition.
	 * 
	 * These are normally used where one servlet of form transition selects the target for a. If the first transition
	 * changes state then the transition should really have used a redirect.
	 * 
	 * This sets up a new request so effectively moves the test state from showing the form to
	 * posting the next operation. For multi-stage forms the form state is copied from the cached values
	 * in the request to the post params.
	 * 
	 * @see ChainedTransitionResult#useURL()
	 * @param fac
	 * @param key
	 * @param target
	 * @throws TransitionException
	 */
	public <K, T> void checkForwardToTransition(TransitionFactory<K, T> fac,
			K key, T target) throws TransitionException {
		super.checkForwardToTransition(fac,key,target);
		// Setup the transition for next operation
		// we don't call resetRequest as
		Map<String,Object> multi_stage = (Map<String, Object>) req.getAttribute(BaseHTMLForm.FORM_STATE_ATTR);
		resetRequest();
		setTransition(fac, key, target);
		if( multi_stage != null) {
			// copy over the current form state 
			req.params.putAll(multi_stage);
			req.attr.remove("Params");
		}
		return;
	}
	
	/** verify that the transition resulted in a form error on a specific parameter.
	 * 
	 * 
	 * @param param 
	 * @param error
	 */
	public void checkError(String param, String error){
		checkError("/scripts/transition.jsp", param,error);
	}
	/** Check that transition resulted in a form error with the specified parameters
	 * missing 
	 * 
	 * @param params
	 */
	public void checkMissing(String ... params) {
		checkForward("/scripts/transition.jsp");
		Collection<String> missing = HTMLForm.getMissing(req);
		for(String p : params) {
			assertTrue("Param "+p+" should be missing",missing.contains(p));
		}
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
	 * Request parameters are pre-set to the default values in the form to 
	 * simulate browser behaviour
	 * 
	 * @param normalize_transform
	 * @param expected_xml
	 * @throws Exception 
	 */
	public <K,T> void checkFormContent(String normalize_transform, String expected_xml) throws Exception{
		checkFormContent(normalize_transform, expected_xml, true);
	}
	/** Generates a XML (mostly HTML) representation of the 
	 * contents of the transition form page that can be directly 
	 * influenced by the transform. This is then (optionally) put through a
	 * normalisation XLST transform to remove time dependent 
	 * output and compared with a file of expected output.
	 * 
	 * Optionally the request parameters are pre-set to the default parameters of 
	 * the Form to simulate browser behaviour
	 * 
	 * @param normalize_transform
	 * @param expected_xml
	 * @param set_params
	 * @throws Exception 
	 */
	public <K,T> void checkFormContent(String normalize_transform, String expected_xml,boolean set_params) throws Exception{
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
		 assertNotNull("No transition found for key "+key,key);
		 assertTrue("Access denied",factory.allowTransition(getContext(), target, key));
		
		 if( factory instanceof TitleTransitionFactory){
			 // could do this for all transitions but
			 // would need to update results for all non Title factories
			builder.open("Title");
				builder.clean(TransitionServlet.getPageTitle(factory, key, target));
			builder.close();
			builder.open("PageHeading");
				builder.clean(TransitionServlet.getPageHeader(factory, key, target));
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
		 assertNotNull("Transition not null",t);
		 
		 HTMLForm f = new HTMLForm(getContext(),new ChainedTransitionResult<T, K>(factory, target, key));
		 f.setFormID("transition_");
		 f.setFormTextGenerator(new SimpleFormTextGenerator(getContext(), factory.getFormTag(key),factory.getTargetName()));
		 
		 if( t instanceof BaseFormTransition ){
		 	BaseFormTransition ft = (BaseFormTransition) t;
		 	ft.buildForm(f,target,getContext());
		 }else if( t instanceof TargetLessTransition ){
		 	TargetLessTransition tlt = (TargetLessTransition) t;
		 	((TargetLessTransition)t).buildForm(f,getContext());
		 }
		 if( t instanceof ExtraContent ){
			 builder.open("ExtraContent");
			 	((ExtraContent)t).getExtraHtml(builder, getContext().getService(SessionService.class), target,f);
			 builder.close();
		 }
		 builder.open("Form");
		 if( t instanceof CustomFormContent ){
			 ((CustomFormContent)t).addFormContent(builder, getContext().getService(SessionService.class), f, target);
		 }else{
			 builder.addFormTable(getContext(), f);
			 builder.addActionButtons(f);
		 }
		 builder.close();
		 if( set_params) {
			 // copy the form defaults into the request
			 SetParamVisitor vis = new SetParamVisitor(req.params);
			 for(Field ff : f) {
				 if( ff.getValue() != null ){
					ff.getInput().accept(vis); 
				 }
			 }
			 req.removeAttribute(DefaultServletService.PARAMS_KEY_NAME);
		 }
        
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
	 * This works in the context of the current request so if called after running
	 * a stage of a multi-stage transition it will use the request cached form state.
	 * 
	 * @param normalize_transform
	 * @param expected_xml
	 * @return view target
	 * @throws Exception 
	 */
	public <K,T> Object checkViewContent(String normalize_transform, String expected_xml) throws Exception{
		HtmlBuilder builder = new HtmlBuilder();
		builder.setValidXML(true);
		builder.open("view_page");
		
		TransitionFactory<K, T> tp = (TransitionFactory<K, T>) req.getAttribute(TransitionServlet.TRANSITION_PROVIDER_ATTR);
		if( tp == null) {
			String name = req.path_info;
			if( name.contains("/")){
				name = name.substring(0, name.indexOf('/'));
			}
			tp = TransitionServlet.getProviderFromName(getContext(), name);
		}
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
			String text = provider.getText(key);
			// for backwards compatability supress if same as value
			if( text.equals(key.toString())) {
				text=null;
			}
			if( provider.showTransition(getContext(),target,key) ){
				builder.open("active");
				  builder.attr("value", key.toString());
				  String help=provider.getHelp(key);
				  if( help != null ){
					builder.attr("help",help);
				  }
				builder.clean(text);
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
					  builder.clean(text);
					builder.close();
					}
				}
			}
		}
		builder.close();
		// for test backwards compat only add if content exists
		HtmlBuilder bottom = (HtmlBuilder) builder.getNested();
		bottom.open("bottom");
		  HtmlBuilder inner = (HtmlBuilder) bottom.getNested();
		  provider.getBottomContent(inner,target,session_service);
		  if( inner.hasContent()) {
			  inner.appendParent();
			  bottom.close();
			  bottom.appendParent();
		  }
		builder.close();
		 
		 
		String content = builder.toString();
		 checkContent(normalize_transform, expected_xml, content);
		 return target;
	}
	/** Select which direct transition from a {@link ConfirmTransition} is to be run
	 * 
	 * @param yes_no
	 */
	public void setConfirmTransition(boolean yes_no){
		setAction(yes_no ? ConfirmTransition.YES : ConfirmTransition.NO);
	}
	
	protected boolean populateForm() {
		return true;
	}
}