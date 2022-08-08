//| Copyright - The University of Edinburgh 2011                            |
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
package uk.ac.ed.epcc.webapp.model.history;

import java.util.Date;

import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.IndexedLinkManager;
import uk.ac.ed.epcc.webapp.model.data.HistoryFactory.HistoryRecord;

public interface LinkHistoryHandler<L extends Indexed, R extends Indexed, T extends IndexedLinkManager.Link<L, R>> extends Contexed,HistoryHandler<T>{

	public IndexedLinkManager<T,L,R> getLinkManager();
	
	public abstract SQLFilter<? extends HistoryRecord<T>> getHistoryFilter(L left,
			R right, Date start, Date end) throws DataException;

	public abstract boolean canLeftJoin();

	public abstract boolean canRightJoin();
	
	public boolean isValid();

}