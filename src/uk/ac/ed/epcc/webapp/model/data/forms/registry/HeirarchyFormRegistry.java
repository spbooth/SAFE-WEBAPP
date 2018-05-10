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
package uk.ac.ed.epcc.webapp.model.data.forms.registry;

import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.forms.registry.FormFactoryProvider;
import uk.ac.ed.epcc.webapp.forms.registry.FormFactoryProviderRegistry;
import uk.ac.ed.epcc.webapp.forms.registry.FormPolicy;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A FormProviderRegistry that automatically includes all tables whose implementation class is
 * a sub-class of the class defined by <b>class.root.</b><em>tag</em>.
 * <p>
 * The name used for the form defaults to the construction tag of the factory but can be changed by setting
 * <em>registry-tag</em><b>.</b><em>factory-tag</em><b>.name</b>
  <p>
 * Update/create privilege is given to users via the (<b>Update</b>|<b>Create</b>)(<em>tag</em>|<em>table-name</em>)
 * role.
 * <p>
 * Update/Create can be supressed for all users by setting config parameter (<em>tag</em>|<em>name</em>)<b>.(allow_create|allow_update)</b> to false.
 * @author spb
 *
 */


public class HeirarchyFormRegistry extends FormFactoryProviderRegistry {
	private final String tag;
	private  Map<String,FormFactoryProvider> map = new HashMap<String,FormFactoryProvider>();
	@SuppressWarnings("unchecked")
	public HeirarchyFormRegistry(AppContext conn, String tag){
		super(conn);
		this.tag=tag;
		String root_class = conn.getInitParameter("root.class."+tag);
		Class<? extends IndexedProducer> target = conn.getClassFromName(IndexedProducer.class, null,
				root_class);
		if( target != null){
		    Map<String, Class> class_map = conn.getClassMap(target);
			
				for(String entry_tag : class_map.keySet()){
					String name = conn.getInitParameter(tag+"."+entry_tag+".name", entry_tag);
					IndexedProducer prod = (IndexedProducer) conn.makeObject(class_map.get(entry_tag),entry_tag);
					if( prod != null){
						   if( prod instanceof DataObjectFactory && ! ((DataObjectFactory)prod).isValid()){
							   continue;
						   }
							
						   new HeirarchyFormFactoryProvider(name, prod.getClass(), entry_tag,new HeirarchyPolicy(name),prod);
						}
					
				}
		}else{
			getLogger().error("Failed to find root class for registry "+root_class);
		}
			
	}

	@Override
	public String getGroup() {
		return tag;
	}

	@Override
	protected Map getMap() {
		return map;
	}

	@Override
	public String getTitle() {
		return getContext().getInitParameter(tag+".title", tag);
	}
	public final class HeirarchyPolicy implements FormPolicy{
		public HeirarchyPolicy(String name) {
			super();
			this.name = name;
		}

		private final String name;
		public boolean canCreate(SessionService p) {
			AppContext conn = p.getContext();
			if( conn.getBooleanParameter(name+".allow_create", true) &&
					conn.getBooleanParameter(tag+".allow_create", true)	) {
				if( p.hasRole(SessionService.ADMIN_ROLE)||p.hasRole("Create"+tag)||p.hasRole("Create"+name)){
					return true;
				}
				String role_list=getContext().getInitParameter("sufficient_create_roles."+tag);
				if( role_list != null  ){
					if( p.hasRoleFromList(role_list.split(","))){
						return true;
					}
				}
			}
			return false;
		}

		public boolean canUpdate(SessionService p) {
			AppContext conn = p.getContext();
			if( conn.getBooleanParameter(tag+".allow_update", true) &&
				conn.getBooleanParameter(name+".allow_update", true)	
				) {
				if(p.hasRole(SessionService.ADMIN_ROLE)||p.hasRole("Update"+tag)||p.hasRole("Update"+name)){
					return true;
				}
				String role_list=getContext().getInitParameter("sufficient_update_roles."+tag);
				if( role_list != null  ){
					if( p.hasRoleFromList(role_list.split(","))){
						return true;
					}
				}
			}
			return false;
		}
	}
   
    public class HeirarchyFormFactoryProvider<F extends IndexedProducer<T>&Contexed,T extends Indexed> extends IndexedFormEntry<F, T>{

		public HeirarchyFormFactoryProvider(String name, Class<? extends F> c,
				String tag,FormPolicy policy) {
			super(name, c,tag, policy);
		}

		@Override
		protected void register(String tag) {
			map.put(tag, this);
		}
		//we can cache the factory in this sub-class as we never store these as statics
        F cached_fac=null;
		@Override
		protected F getFactory(AppContext c) throws Exception {
			if( cached_fac == null ){
				cached_fac = super.getFactory(c);
			}
			return cached_fac;
		}
		public HeirarchyFormFactoryProvider(String name, Class<? extends F> c,
				String tag, FormPolicy policy,F fac) {
			this(name,c,tag,policy);
			cached_fac=fac;
		}
		
    }
}