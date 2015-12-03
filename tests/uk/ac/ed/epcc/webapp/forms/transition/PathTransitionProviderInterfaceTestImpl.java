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

/**
 * @author spb
 * @param <T> 
 * @param <K> 
 * @param <X> 
 *
 */

public class PathTransitionProviderInterfaceTestImpl<T,K,X extends TransitionFactoryDataProvider<K,T>> extends
		TransitionFactoryInterfaceTestImpl<T, K, X> implements PathTransitionProviderInterfaceTest<T,K,X>{

	/**
	 * @param test
	 */
	public PathTransitionProviderInterfaceTestImpl(X test) {
		super(test);
	}

	
	public void testGetID() throws Exception{
			PathTransitionProvider<K, T> fac = (PathTransitionProvider<K, T>) provider.getTransitionFactory();
			for(T target : provider.getTargets()){
				assertEquals(target, fac.getTarget(fac.getID(target)));
			}
	}
	
	
	public void testVisitor(){
		TestTransitionFactoryVisitor<Object, T, K> vis = new TestTransitionFactoryVisitor<Object, T, K>(false);
		provider.getTransitionFactory().accept(vis);
	}
}