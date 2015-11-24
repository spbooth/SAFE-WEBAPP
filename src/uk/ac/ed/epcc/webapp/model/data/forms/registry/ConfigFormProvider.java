// Copyright - The University of Edinburgh 2011
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
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A FormProviderRegistry for DataObjectFactories that is configured via properties.
 * Configuration properties are:
 * <ul> 
 * <li><em>tag</em><b>.tables</b> Comma seperated list of table names to manage</li>
 * <li><em>tag</em><b>.title</b> Name of registry</li>
 * <li><b>class.</b><em>table-name</em> Class of table
 * <li><em>tag</em><b>.</b><em>table-name</em><b>.name</b> Name to use for table (defaults to <em>table-name</em>).
 * </ul>
 * Update/create priviledge is given to users via the (<b>Update</b>|<b>Create</b>)(<em>tag</em>|<em>table-name</em>)
 * role.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ConfigFormProvider.java,v 1.3 2014/09/15 14:30:32 spb Exp $")

public class ConfigFormProvider extends FormFactoryProviderRegistry {
	private final String tag;
	private  Map<String,FormFactoryProvider> map = new HashMap<String,FormFactoryProvider>();
	private Logger log;
	@SuppressWarnings("unchecked")
	public ConfigFormProvider(AppContext conn, String tag){
		super(conn);
		log=conn.getService(LoggerService.class).getLogger(getClass());
		this.tag=tag;
		log.debug("tag="+tag);
		// Now build the map
		// tables is slightly a misnomer as we can have any class tag here
		String list = conn.getInitParameter(tag+".tables", "");
		log.debug("list="+list);
		for(String entry_tag : list.split(",")){
			entry_tag=entry_tag.trim();
			String name = conn.getInitParameter(tag+"."+entry_tag+".name", entry_tag);
			log.debug("entry_tag="+entry_tag+" name="+name);
			if( entry_tag.length() > 0 ){
			
				IndexedProducer prod = conn.makeObject(IndexedProducer.class, entry_tag);
				if( prod != null){
					log.debug(entry_tag+" produced "+prod.getClass().getCanonicalName());
				   if( prod instanceof DataObjectFactory && ! ((DataObjectFactory)prod).isValid()){
					   continue;
				   }
					
				   new ConfigFormFactoryProvider(name, prod.getClass(), entry_tag,new ConfigPolicy(name),prod);
				}
			}
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
	public class ConfigPolicy implements FormPolicy{
		public ConfigPolicy(String name){
			this.name = name;
		}
		private final String name;
		public boolean canCreate(SessionService p) {
			if( getContext().isFeatureOn("all.Create"+name)){
				return true;
			}
			return p.hasRole(SessionService.ADMIN_ROLE)||p.hasRole("Create"+tag)||p.hasRole("Create"+name);
		}

		public boolean canUpdate(SessionService p) {
			if( getContext().isFeatureOn("all.Update"+name)){
				return true;
			}
			return p.hasRole(SessionService.ADMIN_ROLE)||p.hasRole("Update"+tag)||p.hasRole("Update"+name);
		}
	}

    public class ConfigFormFactoryProvider<F extends IndexedProducer<T>&Contexed,T extends Indexed> extends IndexedFormEntry<F, T>{

		public ConfigFormFactoryProvider(String name, Class<? extends F> c,
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
		public ConfigFormFactoryProvider(String name, Class<? extends F> c,
				String tag, FormPolicy policy,F fac) {
			this(name,c,tag,policy);
			cached_fac=fac;
		}
		
    }
  
}