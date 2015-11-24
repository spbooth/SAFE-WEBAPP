// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.content;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class FractionalFormatTransform implements Transform {

	/**
	 * 
	 */
	public FractionalFormatTransform() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.Transform#convert(java.lang.Object)
	 */
	public Object convert(Object old) {
		if( old instanceof Number || old == null){
			return new FractionalFormatGenerator((Number)old);
		}
		return old;
	}

}
