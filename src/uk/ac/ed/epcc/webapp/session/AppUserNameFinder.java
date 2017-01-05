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
package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.NameFinder;
import uk.ac.ed.epcc.webapp.model.ParseFactory;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;

/** An {@link Composite} that implements the {@link NameFinder} logic for an {@link AppUserFactory}.
 * 
 * These are included by composition in the {@link AppUserFactory} allowing multiple name schemes to be supported at the same time.
 * {@link AppUserNameFinder}s where {@link #userVisible()} return false have to be explicitly requests and are not
 * included in the {@link AppUserFactory} {@link NameFinder} implementation. 
 * 
 * Though the normal operation of a {@link AppUserNameFinder} is to locate existing database records it is also possible to
 * to have it query an external directory and lazily create database records. 
 * 
 * @author spb
 * @param <AU> 
 * @param <X> 
 *
 */

public abstract class AppUserNameFinder<AU extends AppUser, X extends AppUserNameFinder> extends AppUserComposite<AU, X> implements ParseFactory<AU>{
	private final String realm;
	/**
	 * @param factory {@link AppUserFactory} we are adding finder to.
	 * @param realm   The realm to implement (can be prefixed by <b>Namefinder.</b>)
	 */
	public AppUserNameFinder(AppUserFactory<AU> factory,String realm) {
		super(factory);
		if( realm.contains(".")){
			// realm names should be unqualified so strip of any qualifying prefix.
			realm=realm.substring(realm.lastIndexOf('.'));
		}
		this.realm=realm;
	}

	
	/** Get a user presented label asking for the login name of the supported type. If this method returns null or an empty string
	 * then the finder may still be included in a user supplied lookup but will not be acknowledged in the form label.
	 * 
	 * @return
	 */
	public abstract String getNameLabel();
	
	
	/** Get the realm this finder applies to.
	 * 
	 * @return
	 */
	public final String getRealm(){
		return realm;
	}
	/** get a filter than locates the target object from a String.
	 * 
	 * This should use the same logic as {@link #findFromString(String)}
	 * 
	 * @param name
	 * @return
	 */
	public abstract SQLFilter<AU> getStringFinderFilter(Class<? super AU> target, String name);
	
	
	/** Checks if sufficient database fields exist to use this {@link AppUserNameFinder}
	 * An inactive finder still modifies the table specification but won't be retreivable as a realm.
	 * 
	 * @return
	 */
	public abstract boolean active();
	/** Set the name of an object for this realm (If supported)
	 * 
	 * @param user
	 * @param name
	 */
	public abstract  void setName(AU user, String name);

	/** Is this a name we might expect the user to know or an internal generated id.
	 * 
	 * @return
	 */
	public boolean userVisible(){
		return true;
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ParseFactory#findFromString(java.lang.String)
	 */
	@Override
	public AU findFromString(String name) {
		if( name == null || name.trim().isEmpty()){
			return null;
		}
		try {
			AU user = getFactory().find(getStringFinderFilter(getFactory().getTarget(), name),true);
			return user;
		} catch ( DataNotFoundException dne){
			return null;
		} catch (DataException e) {
			getLogger().error("Error in name lookup",e);
			return null;
		}
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#getType()
	 */
	@Override
	protected Class<? super X> getType() {
		return (Class<? super X>) getClass();
	}
	
}