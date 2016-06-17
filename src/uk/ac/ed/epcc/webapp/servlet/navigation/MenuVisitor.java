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
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
/** A {@link Visitor} that generates the menu HTML
 * 
 * @author spb
 *
 */
public class MenuVisitor implements Visitor{
	private final AppContext conn;
	private final HtmlBuilder builder;
	public static final Feature MULTI_LEVEL_MENU_FEATURE=new Feature("navigation.multi_level_menu",false,"Should navigation menus support multiple levels");
	public MenuVisitor(AppContext conn,HtmlBuilder builder){
		this.conn=conn;
		this.builder=builder;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.Visitor#visitContainer(uk.ac.ed.epcc.webapp.servlet.navigation.NodeContainer)
	 */
	@Override
	public void visitContainer(NodeContainer container) {
		List<Node> children = container.getChildren();
		boolean top = (! (container instanceof Node));
		boolean recurse = top || MULTI_LEVEL_MENU_FEATURE.isEnabled(conn);
		if( children != null && ! children.isEmpty()){
			// do ul explicitly so we can add attry
			// merge lower levels to work round IE11 bug
			
			builder.open("ul");
			
			if( top){
				//builder.attr("role", "menubar");
			    builder.attr("id","navbar");
				// at top id goes on the list for sub-nodes it is the node itself
				String id = container.getID();
				if( id != null ){
					builder.attr("id",id);
				}
			}else{
				//builder.attr("role","menu");
				//builder.attr("aria-hidden","true");
				String id = container.getID();
				if( id != null ){
					builder.attr("id",id+"_list");
				}
			}
			
			for(Node n : children){
				if( recurse ){
					n.accept(this);
				}else{
					appendLinear(n);
				}
			}
			
			builder.close();
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.Visitor#visitNode(uk.ac.ed.epcc.webapp.servlet.navigation.Node)
	 */
	@Override
	public void visitNode(Node node){
		visitNode(node,true);
	}
	
	public void visitNode(Node node,boolean recurse) {
		boolean top = ! (node.getParent() instanceof Node);
		builder.open("li");
		//builder.attr("role","menuitem");
		//if( top ){
		//	builder.attr("aria-haspopup","true");
		//}
		String targetPath = node.getTargetPath(conn);
		ServletService servlet_service = conn.getService(ServletService.class);
		//if( node.matches(servlet_service) ){
		//	builder.attr("class", "match");
		//}
		
		String class_string = "";
		//class_string = "menu-item";
		//if( top && ! node.isEmpty()){
		//	class_string = "menu-parent";
		//}
		
		String display_class = node.getDisplayClass(conn);
		if( display_class != null ){
			if( class_string.isEmpty()){
				class_string = display_class;
			}else{
				class_string = class_string +" "+ display_class;
			}
		}
		if( ! class_string.isEmpty()){
			builder.attr("class",class_string);
		}
		String id = node.getID();
		if( id != null){
			builder.attr("id",id);
		}
		if( targetPath != null ){
			
			builder.open("a");
				builder.attr("href", node.getTargetURL(servlet_service));
				char key = node.getAccessKey(conn);
				if( key != 0 ){
					builder.attr("accesskey",String.valueOf(key));
				}
				String image = node.getImage();
				if( image == null ){
					builder.clean(node.getMenuText(conn));
				}else{
					builder.open("img");
					  builder.attr("src", servlet_service.encodeURL(image));
					  builder.attr("alt", node.getMenuText(conn));
					builder.close();
				}
			builder.close();
		}else{
			
			if( top){
				builder.attr("tabindex","0");  // want to tab to item itself for menubar if no link
			}
			builder.addText(node.getMenuText(conn));
		}
		if( recurse &&  ! node.isEmpty()){
			visitContainer(node);
		}
		builder.close(); // close node
	}
	/** adds a node and all its children without nesting 
	 * 
	 * @param n
	 */
	public void appendLinear(Node n){
		visitNode(n, false);
		if( ! n.isEmpty()){
			for(Node child : n.getChildren()){
				appendLinear(child);
			}
		}
	}
	
}