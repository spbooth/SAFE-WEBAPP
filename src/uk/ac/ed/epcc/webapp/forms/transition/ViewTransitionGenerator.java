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
package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.content.UIProvider;
import uk.ac.ed.epcc.webapp.forms.Identified;
import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** An adapter that implements {@link UIGenerator} for the target of {@link ViewTransitionFactory}
 * 
 * This is intended to allow the target to implement {@link UIProvider} without adding
 * excessive dependencies to the {@link ViewTransitionFactory}. It constructs the {@link ViewTransitionFactory}
 * dynamically to allow configuration based sub-classing
 * @author spb
 *
 */
public class ViewTransitionGenerator<X> implements UIGenerator{

	private final AppContext conn;
	private final String transition_tag;
	private final X target;
	private String text;
	public ViewTransitionGenerator(AppContext conn,String transition_tag,X target, String text){
		this(conn,transition_tag,target);
		this.text=text;
	}
	public ViewTransitionGenerator(AppContext conn, String transition_tag, X target) {
		super();
		this.conn = conn;
		this.transition_tag = transition_tag;
		this.target = target;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
	 */
	@Override
	public ContentBuilder addContent(ContentBuilder builder) {
		if( text == null){
			if( target instanceof Identified){
				text = ((Identified)target).getIdentifier();
			}else{
				text=target.toString();
			}
		}
		SessionService sess = conn.getService(SessionService.class);
		TransitionFactory<?, X> fac =TransitionServlet.getProviderFromName(conn, transition_tag);
		if( fac != null && fac instanceof ViewTransitionFactory && ((ViewTransitionFactory)fac).canView(target, sess)){
			builder.addLink(conn, text, new ViewTransitionResult((ViewTransitionFactory)fac, target));
		}else{
			builder.getSpan().clean(text).appendParent();
		}
		return builder;
	}

}