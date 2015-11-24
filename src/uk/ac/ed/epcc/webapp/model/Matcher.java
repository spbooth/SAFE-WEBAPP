package uk.ac.ed.epcc.webapp.model;

import uk.ac.ed.epcc.webapp.Indexed;

/** interfaces for fuzzy matching of String names;
 * 
 * @author spb
 *
 */
public interface Matcher extends Indexed{
	public boolean matches(String name);
}
