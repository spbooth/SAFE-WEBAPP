//| Copyright - The University of Edinburgh 2011                            |
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
import uk.ac.ed.epcc.webapp.content.Button;
import uk.ac.ed.epcc.webapp.content.Link;
import uk.ac.ed.epcc.webapp.content.Transform;
import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** A Table.Transform that converts objects to a ViewTransition
 * The object can be either the transition target type or an {@link IndexedReference} to that type.
 * @author spb
 *
 * @param <K>
 * @param <T>
 */


public class ViewTransitionFormat<K,T> implements Transform{
    protected final ViewTransitionFactory<K, T> provider;
    protected final Class<T> clazz;
    protected final boolean use_link;
    protected final Transform label_fmt;
    public ViewTransitionFormat(Class<T> clazz,ViewTransitionFactory<K, T> provider,Transform label_fmt,boolean use_link){
    	this.clazz=clazz;
    	this.provider=provider;
    	this.use_link=use_link;
    	this.label_fmt=label_fmt;
    }
	@Override
	public Object convert(Object old) {
		if( old != null ){
			Object trial=old;
			Class have = trial.getClass();
			if( ! clazz.isAssignableFrom(have) && trial instanceof IndexedReference ){
				IndexedReference ref = (IndexedReference) trial;
				if( ! ref.isNull()){
					// see if the target object matches.
					trial = ref.getIndexed(provider.getContext());
					have = trial.getClass();
				}
			}
			if( clazz.isAssignableFrom(have)){
			@SuppressWarnings("unchecked")
			T target = (T) trial;
			if( provider.canView(target, provider.getContext().getService(SessionService.class))){
				ViewTransitionResult<T, K> result = new ViewTransitionResult<>(provider, target);
				// get the text from the original object. 
				// we assume the nested transform matches the class being passed
				if( use_link ){
					return new Link(provider.getContext(),getLabel(old),result);
				}else{
					return new Button(provider.getContext(),getLabel(old),result);
				}
			}
			}
		}
		return getLabel(old);
	}
    public String getLabel(Object dat){
    	if( dat == null){
    		return null;
    	}
    	if( label_fmt != null ){
    		Object convert = label_fmt.convert(dat);
    		if( convert == null ){
    			return null;
    		}
			return convert.toString();
    	}
    	return String.valueOf(dat);
    }
}