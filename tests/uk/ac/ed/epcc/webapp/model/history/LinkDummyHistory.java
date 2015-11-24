/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.history;

import java.util.Iterator;

import uk.ac.ed.epcc.webapp.model.Dummy1;
import uk.ac.ed.epcc.webapp.model.Dummy2;
import uk.ac.ed.epcc.webapp.model.LinkDummy;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

public class LinkDummyHistory extends LinkHistoryManager<Dummy1,Dummy2,LinkDummy.DummyLink,HistoryFactory.HistoryRecord<LinkDummy.DummyLink>> {

	public LinkDummyHistory(LinkDummy fac) {
		super(fac,"LinkDummyHistory");
		
	}

	
	public void nuke() throws DataFault{
		for(Iterator it = getAllIterator(); it.hasNext();){
			HistoryRecord o = (HistoryRecord) it.next();
			o.delete();
		}
	}
}
