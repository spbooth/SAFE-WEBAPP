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
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
/**
 * @author spb
 *
 */

public class IndexTransitionFactoryInterfaceTestImpl<T,K,X extends TransitionFactoryDataProvider<K,T>> implements IndexTransitionFactoryInterfaceTest<T,K,X>{

	protected final X provider;
	/**
	 * 
	 */
	public IndexTransitionFactoryInterfaceTestImpl(X provider) {
		this.provider=provider;
	}
	
	
	public void testIndexTransition() throws TransitionException{
		IndexTransitionFactory<K, T> fac = (IndexTransitionFactory<K, T>) provider.getTransitionFactory();
		
		K key = fac.getIndexTransition();
		if( key != null ){
			assertEquals(key, fac.lookupTransition(null, key.toString()));
			Transition t = fac.getTransition(null, key);
			TestTransitionVisitor<T> vis = new TestTransitionVisitor<T>(provider.getContext(), null);
			t.getResult(vis);
			
		}
	}

}