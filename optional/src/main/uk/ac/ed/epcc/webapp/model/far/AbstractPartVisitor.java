//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.model.far;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.far.PartManager.Part;

/** An abstract superclass for {@link PartVisitor}s.
 * 
 * 
 * @author spb
 *
 * @param <X>
 */
public abstract class AbstractPartVisitor<X> implements PartVisitor<X>{

	protected final AppContext conn;
	protected final Logger log;

	/**
	 * 
	 */
	public AbstractPartVisitor(AppContext conn) {
		super();
		this.conn=conn;
		this.log = Logger.getLogger(conn,getClass());
	}

	public <O extends PartOwner> void visitOwner(PartOwnerFactory<O> my_manager, O owner) throws DataFault {
		PartManager<O,?> manager = my_manager.getChildManager();
		if( manager != null ){
			for(Part child : manager.getParts(owner)){
				child.visit(this);
			}
		}
	}

}