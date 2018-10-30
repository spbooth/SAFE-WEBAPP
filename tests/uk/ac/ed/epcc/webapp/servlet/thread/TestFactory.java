// Copyright - The University of Edinburgh 2016
package uk.ac.ed.epcc.webapp.servlet.thread;

import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;

/**
 * @author spb
 *
 */
public class TestFactory extends DataObjectFactory<TestFactory.TestData> {

	/**
	 * 
	 */
	private static final String DATA_FIELD = "Data";

	public TestFactory(AppContext conn){
		super();
		setContext(conn, "TestData");
	}
	
	public class TestData extends DataObject{

		/**
		 * @param r
		 */
		protected TestData(Record r) {
			super(r);
		}
		public int getData(){
			return record.getIntProperty(DATA_FIELD);
		}
		public void setData(int val){
			record.setProperty(DATA_FIELD, val);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new TestData(res);
	}
	
	public Class<TestData> getTarget(){
		return TestData.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#getDefaultTableSpecification(uk.ac.ed.epcc.webapp.AppContext, java.lang.String)
	 */
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c, String table) {
		TableSpecification spec = new TableSpecification("TestID");
		spec.setField(DATA_FIELD, new IntegerFieldType(false, 0));
		return spec;
	}
}
