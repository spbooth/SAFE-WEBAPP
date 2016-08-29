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
package uk.ac.ed.epcc.webapp.jdbc.filter;

/**
 * @author spb
 *
 */
public enum FilterCombination {
	AND{

		@Override
		public String getCombiner() {
			return "AND";
		}

		@Override
		public String getDefault() {
			return "true";
		}
		
	},
	OR{

		@Override
		public String getCombiner() {
			return "OR";
		}

		@Override
		public String getDefault() {
			return "false";
		}
		
	};
	/** Get the SQL keyword to combine two clauses
	 * 
	 * @return
	 */
	public abstract String getCombiner();
	/** Get the default value for an empty filter.
	 * 
	 * @return
	 */
	public abstract String getDefault();
}
