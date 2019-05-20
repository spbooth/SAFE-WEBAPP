//| Copyright - The University of Edinburgh 2012                            |
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

public abstract class TransitionFactoryInterfaceTestImpl<T,K,X extends TransitionFactoryDataProvider<K,T>> implements TransitionFactoryInterfaceTest<T,K,X> {

	protected final X provider;
	public TransitionFactoryInterfaceTestImpl(X test){
		this.provider = test;
	}
	
	
	@Override
	public void testGetTransitions() throws Exception{
		TransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			Set<K> keys = fac.getTransitions(target);
			assertNotNull(keys);
			assertTrue("Zero valid transitions", keys.size() > 0);
		}
	}
	
	@Override
	public void testGetTransition() throws Exception{
		TransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			Set<K> keys = fac.getTransitions(target);
			for( K key : keys){
				Transition t = fac.getTransition(target, key);
				assertNotNull(t);
				if( fac.allowTransition(provider.getContext(), target, key)){
					t.getResult(new TestTransitionVisitor<>(provider.getContext(),target));
				}
			}
		}
	}

	@Override
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
	
	
	@Override
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
	
	@Override
	public void testGetTargetName(){
		TransitionFactory<K, T> fac = provider.getTransitionFactory();
		AppContext  c = provider.getContext();
		assertEquals(fac.getClass(), TransitionServlet.getProviderFromName(c, fac.getTargetName()).getClass());
	}
	
	
	@Override
	public void testGetSummaryContentHTML() throws Exception{
		TransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			HtmlBuilder builder = new HtmlBuilder();
			fac.getSummaryContent(provider.getContext(), builder, target);
			builder.toString();
		}
		
	}
	
	
	
	@Override
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