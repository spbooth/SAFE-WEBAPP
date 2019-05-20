/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/

package uk.ac.ed.epcc.webapp.model.history;

import uk.ac.ed.epcc.webapp.model.AnonymisingFactory;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterDelete;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;

public class PersonHistoryFactory<A extends AppUser> extends HistoryFactory<A,HistoryFactory.HistoryRecord<A>> implements AnonymisingFactory{
	   static String TABLE="PersonHistory";
	public PersonHistoryFactory(AppUserFactory<A> fac) {
		super(fac,TABLE);
		
	}
//TODO add default table spec
	@Override
	public void anonymise() throws DataFault {
		FilterDelete del = new FilterDelete<>(res);
		del.delete(null);
		
	}
}