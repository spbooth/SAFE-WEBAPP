// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.transition;


/** A {@link TransitionFactory} that can customise the title text
 * 
 * 
 * @author spb
 * 
 * @param <K> key type
 * @param <T> target type
 */
public interface TitleTransitionFactory<K, T> {

	/** Get page title
	 * 
	 * @param key
	 * @param target
	 * @return String title
	 */
	public String getTitle(K key,T target);
	/** Get page heading
	 * 
	 * @param key
	 * @param target
	 * @return String heading
	 */
	public String getHeading(K key,T target);
}