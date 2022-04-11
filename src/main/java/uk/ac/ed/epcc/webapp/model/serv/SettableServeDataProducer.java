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
package uk.ac.ed.epcc.webapp.model.serv;

import java.util.List;

import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;

public interface SettableServeDataProducer extends ServeDataProducer {

	/** Store a MimeStreamData object generating a new path location
	 * 
	 * 
	 * @param data Data to add
	 * @return path to stored data
	 */
	public List<String> setData(MimeStreamData data);

	@Override
	default boolean isExternalContent(List<String> path) {
		// Settable implies internally generated content.
		return false;
	}
	

	
}