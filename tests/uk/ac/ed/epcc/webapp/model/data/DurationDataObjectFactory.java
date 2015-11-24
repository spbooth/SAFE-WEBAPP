// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.table.NumberFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DurationDataObjectFactory.DurationObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class DurationDataObjectFactory extends DataObjectFactory<DurationDataObjectFactory.DurationObject> {

	
	public DurationDataObjectFactory(AppContext conn){
		setContext(conn, "DurationTable");
	}
/**
	 * 
	 */
	private static final String DURATION = "Duration";

@Override
	public Class<? super DurationObject> getTarget() {
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
		spec.setField(DURATION, new NumberFieldType<Duration>(Duration.class, true, null));
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
