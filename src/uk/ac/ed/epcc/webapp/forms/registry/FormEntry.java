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
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.jdbc.filter.GetListFilterVisitor;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.forms.registry.SummaryContentProvider;



	/** FormEntry holds a reference to a Factory class that is capable of being manipulated via the form interfaces
	 * This type is intended to provide an entry in lists of classes that can be modified via forms.
	 * Any type that we wish to access via the generic forms code (as opposed to explicit jsp/servlets for that type)
	 * needs to be registered here. Each type is marked if update and or create forms should be presented.
	 * Some types have their own mechanisms for creation/update
	 * The create/update information in encapsulated in a policy object to allow fine grained access control.
	 * <p>
	 * FormEntry objects are often created as static instances and also recorded in a static collection. The register method is 
	 * overridden to record the static instances in the collection. 
	 * For this reason the FormEntry does <em>not</em> hold an AppContext reference or cache the Factory object.
	 * FormRegistry instances created to access the collection.
	 * This way we can refer to a particular FormType 
	 * via the static instance and the ObjectServlet can create FormRegistry instances using AppContext.getFactory
	 * Organising the FormTypes into collections reduces the amount of additional configuration as only the FormRegistries need
	 * to be identified by a property name.   
	 *<p>
	 *
	 *This mechanism assumes a default update/create form for each factory class. It would be better to re-write
	 *so that a factory can have many methods that can produce form classes and identify these by annotations. 
	 * moving the access conditions to an annotation as well might be useful changing this type to a register of
	 * factory classes.
	 * @author spb
	 * @param <F> Factory type
	 * @param <T> Target type
	 *
	 */
	public abstract class FormEntry<F extends Contexed,T> extends AbstractFormFactoryProvider<F,T> implements SummaryContentProvider<T>{
		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.forms.registry.SummaryContentProvider#getSummaryContent(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object)
		 */
		@Override
		public <X extends ContentBuilder> X getSummaryContent(AppContext c, X cb, T target) {
			try {
			F fac = getFactory(c);
			if( fac instanceof SummaryContentProvider) {
				cb = ((SummaryContentProvider<T>)fac).getSummaryContent(c, cb, target);
			}
			}catch(Throwable t) {
				c.getService(LoggerService.class).getLogger(getClass()).error("Error makings SummaryContent", t);
			}
			return cb;
		}

		private final Class<? extends F> f;
		protected final String config_tag;
		@Override
		protected F getFactory(AppContext c) throws Exception{
		
				F res=null;
				if( config_tag == null ){
					if( allowNullTag(c)){
						res = c.makeObject(f);
					}
				}else{
					res= c.makeObject(f,config_tag);
				}
				return res;
		}
		
		public boolean allowNullTag(AppContext c){
			return true;
		}
	    

		@Override
		protected boolean disabled(AppContext c){
			return c.getBooleanParameter("registry.disable."+f.getCanonicalName(), false);
		}
		protected FormEntry(String name,Class<? extends F> c, FormPolicy policy){
			this(name,c,null,policy);
		}
		
		protected FormEntry(String name,Class<? extends F> c, String config_tag, FormPolicy policy){
			super(name,policy);
			this.f=c;
			this.config_tag=config_tag;
		}

}