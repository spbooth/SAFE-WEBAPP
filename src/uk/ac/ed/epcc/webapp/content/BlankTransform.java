package uk.ac.ed.epcc.webapp.content;

/** a {@link Transform} that generates an empty string for all values.
 * 
 * @author spb
 *
 */
public class BlankTransform implements Transform {

	public Object convert(Object old) {
		return "";
	}

	

}
