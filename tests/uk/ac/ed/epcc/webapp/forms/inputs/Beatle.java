// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.forms.inputs;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class Beatle {

	
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if( obj == null ){
			return false;
		}
		return obj.getClass().equals(getClass());
	}
	public Beatle(){
		
	}
	public static class Ringo extends Beatle{
		
	}
	public static class Paul extends Beatle{
		
	}
	public static class John extends Beatle{
		
	}
	public static class George extends Beatle{
		
	}
	public static class Mick{
		
	}
}
