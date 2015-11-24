// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.servlet.navigation;

import uk.ac.ed.epcc.webapp.servlet.ServletService;

/** a {@link Node} which matches the target url exactly.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.3 $")
public class ExactNode extends Node {

	/**
	 * 
	 */
	public ExactNode() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.Node#matches(javax.servlet.http.HttpServletRequest)
	 */
	public boolean matches(ServletService serv) {
		
		String targetPath = getTargetPath();
		if( targetPath == null ){
			return false;
		}
		return targetPath.equals(serv.encodePage());
	}

}
