//| Copyright - The University of Edinburgh 2016                            |
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

import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
/** A {@link Visitor} that generates the menu HTML as a set of buttons
 * 
 * @author spb
 *
 */
public class ButtonVisitor implements Visitor{
	private final AppContext conn;
	private final HtmlBuilder builder;
	private boolean active=false;
	private final boolean skip_landingpage;
	private final String target_name;
	public ButtonVisitor(AppContext conn,HtmlBuilder builder, String target_name,boolean skip_landingpage){
		this.conn=conn;
		this.builder=builder;
		this.target_name=target_name;
		this.skip_landingpage=skip_landingpage;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.Visitor#visitContainer(uk.ac.ed.epcc.webapp.servlet.navigation.NodeContainer)
	 */
	@Override
	public void visitContainer(NodeContainer container) {
		List<Node> children = container.getChildren();
		
		if( children != null && ! children.isEmpty()){
			for(Node n : children){
				n.accept(this);
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.Visitor#visitNode(uk.ac.ed.epcc.webapp.servlet.navigation.Node)
	 */
	@Override
	public void visitNode(Node node) {
		boolean target_node = target_name.equals(node.getID());
		if( target_node){
			active=true;
		}else{
			if(active ){
				if( skip_landingpage && node.useLandingPage(conn) ){
					// Skip nested menu and landingpages
					return;
				}
				String path = node.getTargetPath(conn);
				if( path == null || path.isEmpty()){
					// Skip nested menus and placeholders
					return;
				}
				builder.addButton(conn, node.getMenuText(conn),node.getHelpText(), new RedirectResult(path));
			}
		}
		if(  ! node.isEmpty()){
			visitContainer(node);
		}
		if( target_node){
			active=false;
		}
	}
	
}