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
package uk.ac.ed.epcc.webapp.model.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** A {@link ResultMapper} that generates a boolean true/false if the filter matches anything
 * 
 * @author spb
 *
 */
public class ExistsMapper implements ResultMapper<Boolean>{

	
	public boolean setQualify(boolean qualify) {
		return false;
	}

	
	public Boolean makeObject(ResultSet rs) throws DataFault {
		try {
			return rs.getBoolean(1);
		} catch (SQLException e) {
			throw new DataFault("Error in EXISTS",e);
		}
	}

	
	public Boolean makeDefault() {
		return false;
	}

	
	public String getTarget() {
		return " EXISTS( SELECT 1 ";
	}

	
	public List<PatternArgument> getTargetParameters(
			List<PatternArgument> list) {
		return list;
	}

	
	public String getModify() {
		return ")";
	}


	public List<PatternArgument> getModifyParameters(
			List<PatternArgument> list) {
		return list;
	}

	
	public SQLFilter getRequiredFilter() {
		return null;
	}
	
}