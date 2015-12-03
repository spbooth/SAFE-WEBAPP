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