// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.forms.transition;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.session.SessionService;
/**
 * @author spb
 * @param <T> target type
 * @param <K> key type 
 * @param <X> type of {@link ViewTransitionProvider}
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class ViewTransitionFactoryInterfaceTestImpl<T,K,X extends ViewTransitionFactoryDataProvider<K,T>> implements ViewTransitionFactoryInterfaceTest<T,K,X> {

	protected final X provider;
	/**
	 * 
	 */
	public ViewTransitionFactoryInterfaceTestImpl(X provider) {
		this.provider=provider;
	}
	
	public void testCanView() throws Exception{
		ViewTransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			assertTrue(fac.canView(target, provider.getAllowedUser(target)));
			SessionService<?> forbiddenUser = provider.getForbiddenUser(target);
			if( forbiddenUser != null ){
				assertFalse(fac.canView(target, forbiddenUser));
			}
		}
	}

	
	
	public void testGetTopContent() throws Exception{
		ViewTransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			HtmlBuilder builder = new HtmlBuilder();
			fac.getTopContent(builder, target, provider.getAllowedUser(target));
			builder.toString();
		}
	}
	
	public void testGetLogContent() throws Exception{
		ViewTransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			HtmlBuilder builder = new HtmlBuilder();
			fac.getLogContent(builder, target, provider.getAllowedUser(target));
			builder.toString();
		}
	}
	
	
	public void testGetHelp() throws Exception{
		ViewTransitionFactory<K, T> fac = provider.getTransitionFactory();
		for(T target : provider.getTargets()){
			for(K key : fac.getTransitions(target)){
				// Just checking for exceptions
				fac.getHelp(key);
			}
		}
	}
}
