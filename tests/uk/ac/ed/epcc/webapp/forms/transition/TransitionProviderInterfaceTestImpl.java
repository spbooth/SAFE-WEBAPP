// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.forms.transition;



import static org.junit.Assert.*;
/**
 * @author spb
 * @param <T> 
 * @param <K> 
 * @param <X> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class TransitionProviderInterfaceTestImpl<T,K,X extends TransitionFactoryDataProvider<K,T>> extends
		TransitionFactoryInterfaceTestImpl<T, K, X> implements TransitionProviderInterfaceTest<T,K,X>{

	/**
	 * @param test
	 */
	public TransitionProviderInterfaceTestImpl(X test) {
		super(test);
	}

	
	public void testGetID() throws Exception{
			TransitionProvider<K, T> fac = (TransitionProvider<K, T>) provider.getTransitionFactory();
			for(T target : provider.getTargets()){
				assertEquals(target, fac.getTarget(fac.getID(target)));
			}
	}
	
	
	public void testVisitor(){
		TestTransitionFactoryVisitor<Object, T, K> vis = new TestTransitionFactoryVisitor<Object, T, K>(true);
		provider.getTransitionFactory().accept(vis);
	}
}
