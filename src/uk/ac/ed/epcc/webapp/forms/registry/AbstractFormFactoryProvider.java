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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.registry;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreatorProducer;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdateProducer;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** 
 * This type is intended to provide a way of producing lists of classes that can be modified via forms.
 * Any type that we wish to access via the generic forms code (as opposed to explicit jsp/servlets for that type)
 * needs to be registered in one of the sub-classes. Each type is marked if update and or create forms should be presented.
 * Some types have their own mechanisms for creation/update
 * The create/update information in encapsulated in a policy object to allow fine grained access control.
 * 
 * FormEntry objects are usually created as static instances and also recorded in a static collection. The register method is 
 * overridden to record the static instances in the collection. 
 * FormRegistry instances created to access the collection.
 * This way we can refer to a particular entry
 * via the static instance and the ObjectServlet can create FormRegistry instances using AppContext.getFactory
 * Organising the FormTypes into collections reduces the amount of additional configuration as only the FormRegistries need
 * to be identified by a property name.   
 *
 *
 *This mechanism assumes a default update/create form for each factory class. It would be better to re-write
 *so that a factory can have many methods that can produce form classes and identify these by annotations. 
 * moving the access conditions to an annotation as well might be useful changing this type to a register of
 * factory classes.
 * @author spb
 * @param <F> 
 * @param <T> 
 *
 */
public abstract class AbstractFormFactoryProvider<F extends Contexed, T> implements Comparable,FormFactoryProvider<T> {
	private String name;
	private FormPolicy policy;
	public AbstractFormFactoryProvider(String name, FormPolicy policy) {
		this.name=name;
		this.policy=policy;
		register(FormFactoryProviderRegistry.cleanType(name));
	}
	/** register this object with its parent collection. This allows the object to
	 * be declared as a static constant in the registry
	 * 
	 * @param clean_tag
	 */
	protected abstract void register(String clean_tag);
	
	
	

	public String getName(){
		return name;
	}
	public boolean canUpdate(SessionService p){
		try {
			F factory = getFactory(p.getContext());
			if( factory instanceof FormUpdateProducer || factory instanceof FormUpdate){
				if( disabled(p.getContext())){
					return false;
				}
				if( factory instanceof FormUpdateProducer && ! ((FormUpdateProducer)factory).canUpdate(p)){
					return false;
				}
				return policy.canUpdate(p);
			}
		} catch (Exception e) {
			p.getContext().error(e,"Error making factory");
		}
		return false;
	}
	public boolean canCreate(SessionService p){
		try {
			F factory = getFactory(p.getContext());
			if( factory instanceof FormCreatorProducer || factory instanceof FormCreator){
				if( disabled(p.getContext())){
					return false;
				}
				if( factory instanceof FormCreatorProducer && ! ((FormCreatorProducer)factory).canCreate(p)){
					return false;
				}
				return policy.canCreate(p);
			}
		} catch (Exception e) {
			p.getContext().error(e,"Error making factory");
		}
		return false;
	}
	FormPolicy getPolicy(){
		return policy;
	}
	protected boolean disabled(AppContext c){
		return false;
	}
	/** Get the Factory  associated with this entry.
	 * 
	 * @param c
	 * @return
	 * @throws Exception
	 */
	protected abstract F getFactory(AppContext c) throws Exception;
	
	
	/** Get a FormCreator appropriate for this type and the current person
	 * 
	 * @param c AppContext
	 * @return FormCreator or null
	 * @throws Exception
	 */
	public FormCreator getFormCreator(AppContext c) throws Exception{
		if(canCreate(c.getService(SessionService.class))){
			Contexed o = getFactory(c);
			if( o == null){
				return null;
			}
			if( o instanceof FormCreatorProducer ){
				return ((FormCreatorProducer)o).getFormCreator(c);
			}
			// currently we assume the class implements FormCreator directly
			return (FormCreator) o;
		}else{
			return null;
		}
	}
	/** Get a FormUpdate appropriate for this type and the current person
	 * 
	 * @param c AppContest
	 * @return FormUpdate or null
	 * @throws DataFault
	 */
	@SuppressWarnings("unchecked")
	public FormUpdate<T> getFormUpdate(AppContext c) throws Exception{
		if(canUpdate(c.getService(SessionService.class))){
			F o = getFactory(c);
			if( o instanceof FormUpdateProducer){
				return ((FormUpdateProducer<T>)o).getFormUpdate(c);
			}
			// currently we assume the class implements FormUpdate directly
			return (FormUpdate<T>) o;
		}else{
			return null;
		}
	}
	public boolean targets(AppContext c, Object target) {
		
		// benefit of the doubt
		return true;
	}
	public int compareTo(Object o) {
		if( o != null && o.getClass() == getClass()){
			AbstractFormFactoryProvider x = (AbstractFormFactoryProvider) o;
			
			return getName().compareTo(x.getName());
		}
		return -1;
	}
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractFormFactoryProvider other = (AbstractFormFactoryProvider) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}