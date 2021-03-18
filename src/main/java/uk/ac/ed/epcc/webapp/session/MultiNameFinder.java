//| Copyright - The University of Edinburgh 2020                            |
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
package uk.ac.ed.epcc.webapp.session;

import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.FalseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.session.MultiNameFactory.Name;

/** An {@link AppUserNameFinder} where multiple name mappings are held in a seperate table
 * 
  * Configuration parameters:
 * <ul>
 * <li> <em><b>NameFinder.</b>realm<b>.label</b></em> label defaults to realm.</li> 
 * <li> <em><b>NameFinder.</b>realm<b>.table</b></em> construction tag for a {@link MultiNameFactory} to hold the names</li> 

 * 
 * @author Stephen Booth
 *
 */
public class MultiNameFinder<AU extends AppUser, X extends MultiNameFinder> extends AppUserNameFinder<AU, X> {
	protected static final String PROPERTY_PREFIX = "NameFinder.";

	/**
	 * @param factory
	 * @param realm
	 */
	public MultiNameFinder(AppUserFactory<AU> factory, String realm) {
		super(factory, realm);
	}
	
	private MultiNameFactory<Name, AU> name_fac=null;
	private MultiNameFactory<Name, AU> getNameFactory() throws Exception{
		if( name_fac == null ) {
			name_fac = getContext().makeObject(MultiNameFactory.class, getContext().getInitParameter(PROPERTY_PREFIX+getRealm()+".table", getRealm()));
		}
		return name_fac;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ParseFactory#getCanonicalName(java.lang.Object)
	 */
	@Override
	public String getCanonicalName(AU object) {
		try {
			Set<String> names = getNameFactory().getNames(object);
			if(names.isEmpty()) {
				return null;
			}
			return names.iterator().next();
		}catch(Exception e) {
			getLogger().error("Error getting canonical name", e);
		}
		return null;
	}

	@Override
	public String getNameLabel() {
		return getContext().getInitParameter(PROPERTY_PREFIX+getRealm()+".label", getRealm());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#getStringFinderFilter(java.lang.Class, java.lang.String)
	 */
	@Override
	public SQLFilter<AU> getStringFinderFilter(String name) {
		MultiNameFactory<Name, AU> f;
		try {
			f = getNameFactory();
			return f.getPersonFilter(name);
		} catch (Exception e) {
			getLogger().error("Error getting filer", e);
		}
		return new FalseFilter<AU>((Class<AU>) getFactory().getTarget());
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#active()
	 */
	@Override
	public boolean active() {
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#setName(uk.ac.ed.epcc.webapp.session.AppUser, java.lang.String)
	 */
	@Override
	public void setName(AU user, String name) {
		try {
			MultiNameFactory<Name, AU> f = getNameFactory();
			Name n = f.makeFromString(name);
			n.setPerson(user);
			n.commit();
		}catch(Exception e) {
			getLogger().error("Error setting name", e);
		}
		
	}

	@Override
	public Set<String> getAllNames(AU user) {
		try {
			return getNameFactory().getNames(user);
		} catch (Exception e) {
			getLogger().error("Error getting all names", e);
			return new LinkedHashSet<String>();
		}
	}

}
