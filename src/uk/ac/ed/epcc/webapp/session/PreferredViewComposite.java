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
package uk.ac.ed.epcc.webapp.session;

import java.util.Map;

import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;

/**
 * @author James Perry
 *
 */
public class PreferredViewComposite<AU extends AppUser> extends AppUserComposite<AU, PreferredViewComposite> {
	public static final String PREFERRED_VIEW = "PreferredView";

	public PreferredViewComposite(AppUserFactory<AU> fac) {
		super(fac);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#getType()
	 */
	@Override
	protected Class<? super PreferredViewComposite> getType() {
		return PreferredViewComposite.class;
	}

	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setField(PREFERRED_VIEW, new StringFieldType(true, null, 256));
		return spec;
	}

	@Override
	public Map<String, String> addTranslations(Map<String, String> labels) {
		labels.put(PREFERRED_VIEW, "URL of preferred SAFE view (for inclusion in emails)");
		return labels;
	}

	public String getPreferredView(AU person) {
		return getRecord(person).getStringProperty(PREFERRED_VIEW);
	}
	
	public void setPreferredView(AU person, String prefview) {
		getRecord(person).setProperty(PREFERRED_VIEW, prefview);
	}
}
