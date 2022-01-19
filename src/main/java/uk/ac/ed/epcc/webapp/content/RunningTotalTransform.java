package uk.ac.ed.epcc.webapp.content;
/** A {@link Transform} that returns a running total of
 *  {@link Number} values seen by the transform.
 *  
 * 
 * @author Stephen Booth
 *
 */
public class RunningTotalTransform implements Transform {

	public double running=0.0;
	public RunningTotalTransform() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object convert(Object old) {
		if( old instanceof Number) {
			running += ((Number)old).doubleValue();
			return Double.valueOf(running);
		}
		return old;
	}

}
