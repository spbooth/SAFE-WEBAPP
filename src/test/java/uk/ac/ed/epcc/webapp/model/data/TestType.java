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
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.FieldType;

/**
 * @author Stephen Booth
 *
 */
public class TestType extends BasicType<TestType.TestValue> {

	/**
	 * @param field
	 */
	protected TestType(String field) {
		super(field);
	}

	public class TestValue extends BasicType.Value{

		/**
		 * @param tag
		 * @param name
		 */
		protected TestValue(String tag, String name) {
			super(TestType.this,tag, name);
		}
		
	}
	
	public static final TestType monsters = new TestType("Monsters");
	
	public static final TestValue DRACULA = monsters.new TestValue("D","Dracula");
	public static final TestValue WOLFMAN = monsters.new TestValue("W","Wolfman");
	public static final TestValue BLOB = monsters.new TestValue("B","Blob");
	static {
		monsters.lock();
	}
}
