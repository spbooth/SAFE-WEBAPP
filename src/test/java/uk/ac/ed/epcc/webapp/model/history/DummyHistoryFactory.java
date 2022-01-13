//| Copyright - The University of Edinburgh 2015                            |
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
import uk.ac.ed.epcc.webapp.model.data.HistoryFactory;

public class DummyHistoryFactory extends HistoryFactory<Dummy1,HistoryFactory.HistoryRecord<Dummy1>>{
	 public static final String DEFAULT_TABLE="TestHistory";
	public DummyHistoryFactory(AppContext c) {
		super(new Dummy1.Factory(c),DEFAULT_TABLE);
	}
	@Override
	public TableSpecification getDefaultTableSpecification(AppContext c,
			String homeTable) {
		
		TableSpecification spec = super.getDefaultTableSpecification(c, homeTable);
		spec.setField(Dummy1.NAME, new StringFieldType(true, "", 32));
		spec.setField(Dummy1.NUMBER, new DoubleFieldType(true, 0.0));
		spec.setField(Dummy1.UNSIGNED, new LongFieldType(true, 0L));
		return spec;
	}
	
}