//| Copyright - The University of Edinburgh 2018                            |
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

import uk.ac.ed.epcc.webapp.model.Classification;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;

public class PreferedView extends Classification{
	private static final String SERVICE_SAF_URL = "service.saf.url";
	private static final String SERVICE_NAME = "service.name";
	private static final String SERVICE_SAFE_DOCUMENTATION = "service.safe-documentation";
	
	private static final String SAFE_URL = "SafeURL";
	private static final String DOCUMENTATION_URL = "DocumentationURL";

	/**
	 * @param res
	 */
	protected PreferedView(Record res) {
		super(res);
		// TODO Auto-generated constructor stub
	}
	public void addEmailParams(Map<String, String> params) {
		params.put(SERVICE_NAME, getName());
		String safeUrl = record.getStringProperty(SAFE_URL);
		if (safeUrl != null) params.put(SERVICE_SAF_URL,  safeUrl);
		String docUrl = record.getStringProperty(DOCUMENTATION_URL);
		if (docUrl != null) params.put(SERVICE_SAFE_DOCUMENTATION, docUrl);
	}
	
}