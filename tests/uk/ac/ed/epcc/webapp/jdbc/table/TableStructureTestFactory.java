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
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** A concrete example of a {@link TableStructureDataObjectFactory} for testing.
 * @author spb
 *
 */

public class TableStructureTestFactory extends DataObjectFactory<TableStructureTestFactory.TableStructureTestObject> {

	/**
	 * 
	 */
	private static final String NAME = "Name";
	/**
	 * 
	 */
	public static final String SECRET_IDENTITY = "SecretIdentity";
	/**
	 * 
	 */
	public static final String DEFAULT_TABLE = "TableTest";
	public static class TableStructureTestObject extends DataObject{
		/**
		 * @param r
		 */
		protected TableStructureTestObject(Record r) {
			super(r);
		}
		public void setName(String name) {
			record.setProperty(NAME, name);
		}
		public void setSecretIdentity(String val) {
			record.setOptionalProperty(SECRET_IDENTITY, val);
		}
	}
	
	/**
	 * 
	 */
	public TableStructureTestFactory(AppContext c) {
		setContext(c, DEFAULT_TABLE);
	}

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new TableStructureTestObject(res);
	}


	@Override
	public Class<? super TableStructureTestObject> getTarget() {
		return TableStructureTestObject.class;
	}


	public boolean hasField(String f){
		return res.hasField(f);
	}
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = new TableSpecification();
		spec.setField(NAME, new StringFieldType(true, "Alice", 48));
		spec.setOptionalField(SECRET_IDENTITY, new StringFieldType(true, "SuperLightningBabe", 48));
		return spec;
	}

}