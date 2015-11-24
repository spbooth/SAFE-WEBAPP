// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.forms.transition;

import static org.junit.Assert.assertEquals;

/**
 * @author spb
 * @param <T> 
 * @param <K> 
 * @param <X> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
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
