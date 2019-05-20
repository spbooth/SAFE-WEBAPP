//| Copyright - The University of Edinburgh 2014                            |
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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.table.NumberFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author spb
 *
 */

public class DurationDataObjectFactory extends DataObjectFactory<DurationDataObjectFactory.DurationObject> {

	
	public DurationDataObjectFactory(AppContext conn){
		setContext(conn, "DurationTable");
	}
/**
	 * 
	 */
	private static final String DURATION = "Duration";

@Override
	public Class<DurationObject> getTarget() {
		return DurationObject.class;
	}

	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new DurationObject(res);
	}

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = new TableSpecification("DurationID");
		spec.setField(DURATION, new NumberFieldType<>(Duration.class, true, null));
		return spec;
	}

public static class DurationObject extends DataObject{

	/**
	 * @param r
	 */
	protected DurationObject(Record r) {
		super(r);
	}
	
	public void setDuration(Duration d){
		record.setProperty(DURATION, d);
	}
	
	public Duration getDuration(){
		return new Duration(record.getLongProperty(DURATION),1L);
	}
}
	
}