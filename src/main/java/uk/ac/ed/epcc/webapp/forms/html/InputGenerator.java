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
package uk.ac.ed.epcc.webapp.forms.html;

import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder;
import uk.ac.ed.epcc.webapp.content.XMLGenerator;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;

public class InputGenerator implements XMLGenerator{
	private final boolean use_post;
	private final Map post_params;
	private final Input i;
	private final AppContext conn;
	private final boolean optional;
	public InputGenerator(AppContext conn,Input i, boolean use_post,Map post_params){
		this(conn,i,use_post,post_params,false);
	}
	public InputGenerator(AppContext conn,Input i, boolean use_post,Map post_params,boolean optional){
		this.i=i;
		this.use_post=use_post;
		this.post_params=post_params;
		this.conn=conn;
		this.optional=optional;
	}
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
		EmitHtmlInputVisitor vis = new EmitHtmlInputVisitor(conn,optional,(ExtendedXMLBuilder)builder, use_post, post_params,null);
		try {
			i.accept(vis);
		} catch (Exception e) {
			conn.error(e,"Error formatting input");
		}
		return builder;
	}
}