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
import uk.ac.ed.epcc.webapp.servlet.ServletService;
/** A {@link Visitor} that generates the menu HTML as in-page content
 * 
 * @author spb
 *
 */
public class LandingPageVisitor implements Visitor{
	private final AppContext conn;
	private final HtmlBuilder builder;
	private boolean active=false;
	private boolean list_only=false;
	private int header_level=1;
	public boolean isListOnly() {
		return list_only;
	}
	public void setListOnly(boolean list_only) {
		this.list_only = list_only;
	}

	private final String target_name;
	public LandingPageVisitor(AppContext conn,HtmlBuilder builder, String target_name){
		this.conn=conn;
		this.builder=builder;
		this.target_name=target_name;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.Visitor#visitContainer(uk.ac.ed.epcc.webapp.servlet.navigation.NodeContainer)
	 */
	@Override
	public void visitContainer(NodeContainer container) {
		List<Node> children = container.getChildren();
		
		if( children != null && ! children.isEmpty()){
			// do explicitly so we can add attry
			if( active ){
				builder.open("ul");
			}
			for(Node n : children){
				n.accept(this);
			}
			if( active ){ 
				builder.close();
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.Visitor#visitNode(uk.ac.ed.epcc.webapp.servlet.navigation.Node)
	 */
	@Override
	public void visitNode(Node node) {
		boolean target_node = target_name != null && target_name.equals(node.getID());
		if( target_node){
			active=true;
			if( ! list_only) {
				builder.open("div");
				builder.attr("class","block");
				builder.addHeading(header_level, node.getMenuText(conn));
			}
		}else{
			if(active ){
				builder.open("li");
				String targetPath = node.getTargetPath(conn);
				ServletService servlet_service = conn.getService(ServletService.class);
				String id = node.getID();
				if( id != null){
					builder.attr("id",id+"_page_entry");
				}
				String display_class = node.getDisplayClass(conn);
				if( display_class != null ){
					builder.attr("class",display_class);
				}
				if( targetPath != null ){

					builder.open("a");
					builder.attr("href", node.getTargetURL(servlet_service));

					String image = node.getImage();
					if( image == null ){
						builder.clean(node.getMenuText(conn));
					}else{
						builder.open("img");
						builder.attr("src", servlet_service.encodeURL(image));
						builder.attr("alt", node.getMenuText(conn));
						builder.attr("title", node.getMenuText(conn));
						builder.close();
						String post = node.getPostImageText();
						if( post != null && ! post.isEmpty()) {
							builder.clean(post);
						}
					}
					builder.close();
				}else{
					builder.addText(node.getMenuText(conn));
				}
			}
		}
		if(  ! node.isEmpty()){
			visitContainer(node);
		}
		if( active){
			if( ! target_node || ! list_only) {
				builder.close(); // close node
			}
		}
		if( target_node){
			active=false;
		}
	}
	
}