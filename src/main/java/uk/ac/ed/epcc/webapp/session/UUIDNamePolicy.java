//| Copyright - The University of Edinburgh 2019                            |
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

import java.util.UUID;

/** A {@link GlobalNamePolicy} that assigns a random UUID for the {@link AppUser}
 * 
 * These can safely be anonymised without risk of re-issue so aresuitable for
 * ids sent to external providers.
 * 
 * @author Stephen Booth
 *
 */
public class UUIDNamePolicy<AU extends AppUser> extends GlobalNamePolicy<AU, UUIDNamePolicy> {

	/**
	 * @param factory
	 * @param realm
	 */
	public UUIDNamePolicy(AppUserFactory<AU> factory) {
		super(factory, "UUID");
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.GlobalNamePolicy#getName(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public String getName(AU p) {
		UUID id = UUID.randomUUID();
		return id.toString();
	}

	@Override
	protected int defaultFieldLength() {
		return 36;
	}

	@Override
	public String getNameLabel() {
		// supress by default but can still enable with property
		return getContext().getInitParameter(PROPERTY_PREFIX+getRealm()+".label", null);
	}

	

}
