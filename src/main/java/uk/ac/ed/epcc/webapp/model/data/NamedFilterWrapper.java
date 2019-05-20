//| Copyright - The University of Edinburgh 2017                            |
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
package uk.ac.ed.epcc.webapp.model.data;



import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter;
import uk.ac.ed.epcc.webapp.model.NameFinder;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedTypeProducer;

/** A wrapper for resolving named filters from a {@link DataObjectFactory}.
 * 
 * The factory itself and its {@link Composite}s are checked if they implement {@link NamedFilterProvider}
 * A name of the form <b><i>field_name</i>-><i>remote_name</i></b> indicates a remote filter should 
 * be constructed via field <i>field_name</i> and the same algorithm applied on the remote factory.
 * If the name starts with <b>name:</b> and the factory is a {@link NameFinder} then the remainder of
 * the name is passed to {@link NameFinder#getStringFinderFilter(String)}. 
 * Only references registered in the {@link Repository} can be de-referenced.
 * @author spb
 * @param <T> target type of filters
 *
 */
public class NamedFilterWrapper<T extends DataObject> extends AbstractContexed implements NamedFilterProvider<T> {

	/**
	 * 
	 */
	private static final String FILTER_DEREF = "->";
	/**
	 * 
	 */
	private static final String NAME_PREFIX = "name:";
	private final DataObjectFactory<T> fac;
	private final Set<String> missing=new HashSet<>();
	/**
	 * 
	 */
	public NamedFilterWrapper(DataObjectFactory<T> fac) {
		super(fac.getContext());
		this.fac=fac;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider#getNamedFilter(java.lang.String)
	 */
	@Override
	public BaseFilter<T> getNamedFilter(String name) {
		// We need to handle AND/OR combinations here for code that uses explicitly name-filter
		// when called from the SessionService relationship code the AND/OR combinations should
		// be parsed at that level
		if( name.contains(",")) {
			OrFilter<T> or = new OrFilter<>(fac.getTarget(), fac);
			for(String sub : name.split(",")) {
				or.addFilter(getNamedFilter(sub));
			}
			return or;
		}
		if( name.contains("+")) {
			AndFilter<T> and = new AndFilter<>(fac.getTarget());
			for(String sub : name.split("\\+")) {
				and.addFilter(getNamedFilter(sub));
			}
			return and;
		}
		int pos = name.indexOf(FILTER_DEREF);
		if( pos > 0 ){
			String remote=name.substring(0, pos);
			String remote_name = name.substring(pos+2);
			Repository res = fac.res;
			FieldInfo info = res.getInfo(remote);
			if( info != null && info.isReference()){
				TypeProducer prod = info.getTypeProducer();
				if( prod instanceof NamedFilterProvider){
					return ((NamedFilterProvider)prod).getNamedFilter(remote_name);
				}else if( prod instanceof IndexedTypeProducer){
					IndexedProducer ip = ((IndexedTypeProducer)prod).getProducer();
					if( ip instanceof DataObjectFactory){
						return getFilter((DataObjectFactory)ip, remote, remote_name);
					}
				}
			}else {
				getLogger().warn("Bad remote filter "+name);
			}
			return null;
		}
		pos = name.indexOf(".");
		if( pos > 0) {
			String provider=name.substring(0, pos);
			String provider_name = name.substring(pos+1);
			NamedFilterProvider<T> prov = fac.getContext().makeObject(NamedFilterProvider.class, provider);
			if( prov != null) {
				return prov.getNamedFilter(provider_name);
			}else {
				getLogger().error("Bad explicit NamedFilterProvider "+provider);
			}
			return null;
		}
		if( fac instanceof NameFinder && name.startsWith(NAME_PREFIX)) {
			return ((NameFinder)fac).getStringFinderFilter(name.substring(NAME_PREFIX.length()));
		}
		
		BaseFilter<T> result=null;
		if( fac instanceof NamedFilterProvider){
			result = ((NamedFilterProvider<T>)fac).getNamedFilter(name);
			if( result != null ){
				return result;
			}
		}
		for(NamedFilterProvider<T> nfp : fac.getComposites(NamedFilterProvider.class)){
			result = nfp.getNamedFilter(name);
			if( result != null){
				return result;
			}
		}
		if( missing.contains(name)) {
			return null;
		}
		try {
			missing.add(name);
			String defn = fac.getContext().getInitParameter("use_filter."+fac.getTag()+"."+name);
			if( defn != null) {
				return getNamedFilter(defn);
			}
		}finally {
			missing.remove(name);
		}
		//getLogger().warn("Bad named filter for "+fac.getTag()+" "+name);
		return null;
	};

	public <R extends DataObject> BaseFilter<T> getFilter(DataObjectFactory<R> remote_fac,String remote,String name){
		NamedFilterWrapper<R> nfp = new NamedFilterWrapper<>(remote_fac);
		BaseFilter<R> fil = nfp.getNamedFilter(name);
		if( fil != null ){
			return fac.getRemoteFilter(remote_fac, remote, fil);
		}else{
			getLogger().warn("Missing remote filter "+remote+"."+name);
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider#addFilterNames(java.util.Set)
	 */
	@Override
	public void addFilterNames(Set<String> names) {
		if( fac instanceof NamedFilterProvider){
			((NamedFilterProvider<T>)fac).addFilterNames(names);
			
		}
		for(NamedFilterProvider<T> nfp : fac.getComposites(NamedFilterProvider.class)){
			nfp.addFilterNames(names);
		}
		
	}
}
