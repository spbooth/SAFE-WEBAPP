// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.transition;


/** TransitionProvider that can customise the title text
 * 
 * 
 * @author spb
 * 
 * @param <K> key type
 * @param <T> target type
 */
public interface TitleTransitionProvider<K, T> extends TitleTransitionFactory<K, T> ,TransitionProvider<K, T>{

	
}