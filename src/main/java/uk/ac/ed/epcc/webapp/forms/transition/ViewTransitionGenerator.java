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
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.timer.TimeClosable;

/** An adapter that implements {@link UIGenerator} for the target of {@link ViewTransitionFactory}
 * 
 * This is intended to allow the target to implement {@link UIProvider} without adding
 * excessive dependencies to the {@link ViewTransitionFactory}. It constructs the {@link ViewTransitionFactory}
 * dynamically to allow configuration based sub-classing
 * @author spb
 *
 */
public class ViewTransitionGenerator<X> implements UIGenerator, Comparable<ViewTransitionGenerator<X>>{

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((transition_tag == null) ? 0 : transition_tag.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ViewTransitionGenerator other = (ViewTransitionGenerator) obj;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (transition_tag == null) {
			if (other.transition_tag != null)
				return false;
		} else if (!transition_tag.equals(other.transition_tag))
			return false;
		return true;
	}

	private final AppContext conn;
	private final String transition_tag;
	private final X target;
	private String text;
	private String title;
	public ViewTransitionGenerator(AppContext conn,String transition_tag,X target, String text){
		this(conn,transition_tag,target,text,null);
	}
	public ViewTransitionGenerator(AppContext conn,String transition_tag,X target, String text,String title){
		this(conn,transition_tag,target);
		this.text=text;
		this.title=title;
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
		try(TimeClosable time=new TimeClosable(conn, ()->"ViewTranditionGenerator."+transition_tag)){
			SessionService sess = conn.getService(SessionService.class);
			TransitionFactory<?, X> fac =new TransitionFactoryFinder(conn).getProviderFromName(transition_tag);
			// Check for current user so as not to add links in emails for public viewable objects
			if( fac != null && sess != null && sess.haveCurrentUser() && fac instanceof ViewTransitionFactory && ((ViewTransitionFactory)fac).canView(target, sess)){
				builder.addLink(conn, toString(), title,new ViewTransitionResult((ViewTransitionFactory)fac, target));
			}else{
				builder.getSpan().clean(toString()).appendParent();
			}
			return builder;
		}
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ViewTransitionGenerator<X> arg0) {
		return toString().compareTo(arg0.toString());
	}

	public String toString(){
		if( text == null){
			if( target instanceof Identified){
				text = ((Identified)target).getIdentifier();
			}else{
				text=target.toString();
			}
		}
		return text;
	}
}
