// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.forms.transition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;

import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;

import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;


/**
 * @author spb
 * @param <T> 
 * @param <K> 
 * @param <X> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class TransitionFactoryInterfaceTestImpl<T,K,X extends TransitionFactoryDataProvider<K,T>> implements TransitionFactoryInterfaceTest<T,K,X> {

	protected final X provider;
	public TransitionFactoryInterfaceTestImpl(X test){
		this.provider = test;
	}
	
	
	public void testGetTransitions() throws Exception{
		TransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			Set<K> keys = fac.getTransitions(target);
			assertNotNull(keys);
			assertTrue("Zero valid transitions", keys.size() > 0);
		}
	}
	
	public void testGetTransition() throws Exception{
		TransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			Set<K> keys = fac.getTransitions(target);
			for( K key : keys){
				Transition t = fac.getTransition(target, key);
				assertNotNull(t);
				if( fac.allowTransition(provider.getContext(), target, key)){
					t.getResult(new TestTransitionVisitor<T>(provider.getContext(),target));
				}
			}
		}
	}

	public void testLookupTransition() throws Exception{
		TransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			Set<K> keys = fac.getTransitions(target);
			for( K key : keys){
				assertEquals(key, fac.lookupTransition(target, key.toString()));
			}
			assertNull(fac.lookupTransition(target, "BorisTheSpider"));
		}
	}
	
	
	public void testAllowTransition() throws Exception{
		TransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			Set<K> keys = fac.getTransitions(target);
			for( K key : keys){
				// Just checking for exceptions at the moment
				boolean allow = fac.allowTransition(provider.getContext(), target, key);
			}
		}
	}
	
	public void testGetTargetName(){
		TransitionFactory<K, T> fac = provider.getTransitionFactory();
		AppContext  c = provider.getContext();
		assertEquals(fac.getClass(), TransitionServlet.getProviderFromName(c, fac.getTargetName()).getClass());
	}
	
	
	public void testGetSummaryContentHTML() throws Exception{
		TransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			HtmlBuilder builder = new HtmlBuilder();
			fac.getSummaryContent(provider.getContext(), builder, target);
			builder.toString();
		}
		
	}
	
	
	
	public void testFormCreation() throws Exception{
		TransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			for( K key : fac.getTransitions(target)){
				Transition t = fac.getTransition(target, key);
				if( t instanceof FormTransition){
					Form f = new HTMLForm(fac.getContext());
					((FormTransition) t).buildForm(f, target, fac.getContext());
				}
			}
		}
	}
}
