// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.servlet.navigation;

import uk.ac.ed.epcc.webapp.servlet.ServletService;

/** a {@link Node} that matches all locations below the target.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.4 $")
public class ParentNode extends Node {

	/**
	 * 
	 */
	public ParentNode() {
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.Node#matches(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public boolean matches(ServletService serv) {
		
		String targetPath = getTargetPath();
		if( targetPath == null ){
			return false;
		}
		targetPath.replace("//", "/"); // normalise
		String mypath = serv.encodePage();
		mypath = mypath.replace("//", "/"); // normalise 
		return mypath.startsWith(targetPath);
	}

}
