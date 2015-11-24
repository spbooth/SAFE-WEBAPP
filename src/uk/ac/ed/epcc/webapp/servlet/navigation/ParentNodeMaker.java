// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.servlet.navigation;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.FilteredProperties;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class ParentNodeMaker extends AbstractNodeMaker  {

	/**
	 * @param conn 
	 * 
	 */
	public ParentNodeMaker(AppContext conn) {
		super(conn);
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.NodeMaker#makeNode(java.lang.String, uk.ac.ed.epcc.webapp.config.FilteredProperties)
	 */
	@Override
	public Node makeNode(String name, FilteredProperties props) {
		return new ParentNode();
	}

}
