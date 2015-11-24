// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.servlet.navigation;

import uk.ac.ed.epcc.webapp.servlet.ServletService;

/** An {@link ExactNode} that includes the calling page in the target URL
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class PageNode extends ExactNode {

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.Node#getTargetURL(uk.ac.ed.epcc.webapp.servlet.ServletService)
	 */
	@Override
	public String getTargetURL(ServletService servlet_service) {
		
		return servlet_service.encodeURL(getTargetPath()+"?page="+servlet_service.encodePage());
	}

}
