// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.table.TableStructureDataObjectFactory;

/** A concrete example of a {@link TableStructureDataObjectFactory} for testing.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class TableStructureTestFactory extends TableStructureDataObjectFactory<TableStructureTestFactory.TableStructureTestObject> {

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
