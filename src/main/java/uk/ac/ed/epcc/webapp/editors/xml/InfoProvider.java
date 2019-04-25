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
package uk.ac.ed.epcc.webapp.editors.xml;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.TypeInfo;

public interface InfoProvider {

	public abstract Map<LinkedList<String>, Set<String>> getErrors();

	public Set<String> getError(LinkedList<String> path);
	
	public abstract Map<LinkedList<String>, TypeInfo> getTypes();

	public TypeInfo getTypeInfo(LinkedList<String> path);
}