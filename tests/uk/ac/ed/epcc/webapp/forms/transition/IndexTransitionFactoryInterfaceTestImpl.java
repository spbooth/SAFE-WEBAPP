// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.forms.transition;

import static org.junit.Assert.assertEquals;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
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
