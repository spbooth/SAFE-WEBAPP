//| Copyright - The University of Edinburgh 2016                            |
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

/** A trivial sub-class of {@link FieldNameFinder}.
 * 
 * This is to allow more than one {@link FieldNameFinder} to be installed within the same class.
 * @author spb
 *
 */
public class TertiaryNameFinder<AU extends AppUser> extends RemoteFieldNameFinder<AU, TertiaryNameFinder> {

	/**
	 * @param factory
	 * @param realm
	 */
	public TertiaryNameFinder(AppUserFactory factory, String realm) {
		super(factory, realm);
	}

}
