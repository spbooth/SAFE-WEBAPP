//| Copyright - The University of Edinburgh 2013                            |
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
package uk.ac.ed.epcc.webapp.content;


public class DefaultTransform implements Transform {
	Object default_entry;

	public DefaultTransform(Object def) {
		default_entry = def;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.hpcx.Table.Transform#convert(java.lang.Object)
	 */
	public Object convert(Object old) {
		if (old == null) {
			return default_entry;
		}
		return old;
	}

}