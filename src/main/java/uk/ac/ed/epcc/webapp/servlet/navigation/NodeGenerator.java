//| Copyright - The University of Edinburgh 2015                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
package uk.ac.ed.epcc.webapp.servlet.navigation;

import javax.servlet.http.HttpServletRequest;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.content.UIGenerator;

/** Adds the contents of {@link NodeContainer}
 * Assumed to be passed a {@link HtmlBuilder}
 * @author spb
 *
 */

public class NodeGenerator implements UIGenerator {


	/**
	 * @param conn 
	 * @param n
	 * @param req 
	 */
	public NodeGenerator(AppContext conn,NodeContainer n,HttpServletRequest req) {
		super();
		this.conn=conn;
		this.n = n;
		this.request=req;
	}
	private final AppContext conn;
	private final NodeContainer n;
	private final HttpServletRequest request;

	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
	 */
	@Override
	public ContentBuilder addContent(ContentBuilder cb) {
		MenuVisitor vis = new MenuVisitor(conn, (HtmlBuilder) cb);
		n.accept(vis);
		return cb;
	}
	
}