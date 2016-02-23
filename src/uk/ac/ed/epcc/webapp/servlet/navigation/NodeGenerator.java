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

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.model.data.iterator.DecoratingIterator;
import uk.ac.ed.epcc.webapp.servlet.ServletService;

/** Adds the contents of {@link NodeContainer}
 * Assumed to be passed a {@link HtmlBuilder}
 * @author spb
 *
 */

public class NodeGenerator implements UIGenerator {
	/**
	 * @param n
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

	private class NodeGeneratorIterator extends DecoratingIterator<NodeGenerator, Node>{

		/**
		 * @param i
		 */
		public NodeGeneratorIterator(Iterator<? extends Node> i) {
			super(i);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.iterator.DecoratingIterator#next()
		 */
		@Override
		public NodeGenerator next() {
			return new NodeGenerator(conn, nextInput(), request);
		}
		
	}
	private class NodeGeneratorIterable implements Iterable<NodeGenerator>{
		/**
		 * @param children
		 */
		public NodeGeneratorIterable(List<Node> children) {
			super();
			this.children = children;
		}

		private final List<Node> children;

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<NodeGenerator> iterator() {
			return new NodeGeneratorIterator(children.iterator());
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
	 */
	@Override
	public ContentBuilder addContent(ContentBuilder builder) {
		
		if( n instanceof Node ){
			Node node = (Node) n;
			String targetPath = node.getTargetPath();
			ServletService servlet_service = conn.getService(ServletService.class);
			if( node.matches(servlet_service) && builder instanceof HtmlBuilder){
				((HtmlBuilder)builder).attr("class", "match");
			}
			String display_class = node.getDisplayClass(conn);
			if( display_class != null ){
				((HtmlBuilder)builder).attr("class",display_class);
			}
			if( targetPath != null ){
				HtmlBuilder hb = (HtmlBuilder)builder;
				hb.open("a");
					hb.attr("href", node.getTargetURL(servlet_service));
					
					String image = node.getImage();
					if( image == null ){
						hb.clean(node.getMenuText(conn));
					}else{
						hb.open("img");
						hb.attr("src", servlet_service.encodeURL(image));
						hb.attr("alt", node.getMenuText(conn));
						hb.close();
					}
				hb.close();
			}else{
				builder.addText(node.getMenuText(conn));
			}
		}
		List<Node> children = n.getChildren();
		if( children != null && ! children.isEmpty()){
			builder.addList(new NodeGeneratorIterable(children));
		}
		return builder;
	}

}