// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.servlet.navigation;

import uk.ac.ed.epcc.webapp.servlet.ServletService;

/** A Node representing an external URL
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.4 $")
public class ExternalNode extends Node {

	/**
	 * 
	 */
	public ExternalNode() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.Node#matches(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public boolean matches(ServletService serv) {
		return false;
	}

	@Override
	public String getTargetURL(ServletService service)  {
		return getTargetPath();
	}

}
