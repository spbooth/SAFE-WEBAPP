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
package uk.ac.ed.epcc.webapp.model.far;

import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor;
import uk.ac.ed.epcc.webapp.model.data.transition.AbstractViewTransitionFactory;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.PartManager.Part;

/**
 * @author spb
 *
 */

public abstract class AbstractPartTransitionProvider<T,K extends TransitionKey<T>> extends AbstractViewTransitionFactory<T, K>
		implements PathTransitionProvider<K, T> {
	
	private final String target_name;
	protected final DynamicFormManager<?> form_manager;
	
	
	public AbstractPartTransitionProvider(String target_name,DynamicFormManager man){
		super(man.getContext());
		this.target_name=target_name;
		this.form_manager=man;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getTargetName()
	 */
	@Override
	public final String getTargetName() {
		return target_name;
	}

	

	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#accept(uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor)
	 */
	@Override
	public final  <R> R accept(TransitionFactoryVisitor<R, T, K> vis) {
		return vis.visitPathTransitionProvider(this);
	}

	public static Part getTarget(PartOwner owner, PartOwnerFactory<?> fac,LinkedList<String> path){
		String id = path.pop();
		if( id == null || fac == null){
			return null;
		}
		Part here =null;
		try {
			here = (Part) fac.find(Integer.parseInt(id));
		} catch (Exception e) {
			return null;
		}
		if( here == null || path.peekFirst() == null){
			return here;
		}
		if( ! here.getOwner().equals(owner)){
			return null;
		}
		PartManager childManager = fac.getChildManager();
		if( childManager == null ){
			return here;
		}
		return getTarget(here, childManager, path);
	}
	
	
	public static LinkedList<String> getID(LinkedList<String> path, Part part){
		// We include the ids of all parents in the path
		// This is actually more information than we strictly need but it 
		// makes it easier to reference ancestors by editing the url.
		// also easier to include additional levels into the heirarcy.
		path.push(Integer.toString(part.getID()));
		PartOwner owner = part.getOwner();
		if( owner instanceof Part){
			return getID(path, ((Part)owner));
		}else{
			path.push(Integer.toString(owner.getID()));
		}
		return path;
	}
	

	protected <X extends ContentBuilder> X addBreadcrumb(X cb, PartOwner owner){
		if( owner instanceof DynamicForm){
			DynamicForm form = (DynamicForm) owner;
			DynamicFormTransitionProvider provider = form_manager.getDynamicFormProvider();
			cb.addLink(getContext(), form.getName(), provider.new ViewResult(form));
		}else if( owner instanceof Part){
			addBreadcrumb(cb, ((Part)owner).getOwner());
			if( cb instanceof HtmlBuilder){
				((HtmlBuilder)cb).nbs();
			}
			((Part) owner).addContent(cb);
		}
		return cb;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((form_manager == null) ? 0 : form_manager.hashCode());
		result = prime * result
				+ ((target_name == null) ? 0 : target_name.hashCode());
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
		AbstractPartTransitionProvider other = (AbstractPartTransitionProvider) obj;
		if (form_manager == null) {
			if (other.form_manager != null)
				return false;
		} else if (!form_manager.equals(other.form_manager))
			return false;
		if (target_name == null) {
			if (other.target_name != null)
				return false;
		} else if (!target_name.equals(other.target_name))
			return false;
		return true;
	}
	
	
}