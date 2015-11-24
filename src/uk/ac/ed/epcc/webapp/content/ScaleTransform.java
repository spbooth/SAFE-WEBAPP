// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.content;

/** A {@link Transform} to perform simple numerical scaling.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class ScaleTransform implements Transform {
	public ScaleTransform(double sc) {
		super();
		this.scale = sc;
	}

	private final double scale;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.Transform#convert(java.lang.Object)
	 */
	@Override
	public Object convert(Object old) {
		if( old != null && old instanceof Number){
			return scale * ((Number)old).doubleValue();
		}
		return old;
	}
}
