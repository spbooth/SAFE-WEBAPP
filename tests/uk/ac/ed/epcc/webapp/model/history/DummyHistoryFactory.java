/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
/**
 * 
 */
package uk.ac.ed.epcc.webapp.model.history;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.table.DoubleFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.LongFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.Dummy1;

public class DummyHistoryFactory extends HistoryFactory<Dummy1,HistoryFactory.HistoryRecord<Dummy1>>{
	 public static final String DEFAULT_TABLE="TestHistory";
	public DummyHistoryFactory(AppContext c) {
		super(new Dummy1.Factory(c),DEFAULT_TABLE);
	}
	@Override
	public TableSpecification getDefaultTableSpecification(AppContext c,
			String homeTable) {
		// TODO Auto-generated method stub
		TableSpecification spec = super.getDefaultTableSpecification(c, homeTable);
		spec.setField(Dummy1.NAME, new StringFieldType(true, "", 32));
		spec.setField(Dummy1.NUMBER, new DoubleFieldType(true, 0.0));
		spec.setField(Dummy1.UNSIGNED, new LongFieldType(true, 0L));
		return spec;
	}
	
}