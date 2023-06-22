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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms;

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFormFactory;

/** A TargetLessTransition for creating DataObjects.
 * This class extends DataObjectFormFactory directly rather than wrapping a Creator object
 * making it easier to customise.
 * @author spb
 * @see Creator
 *
 * @param <BDO>
 */
public abstract  class CreateTransition<BDO extends DataObject> extends DataObjectFormFactory<BDO> implements CreateTransitionInterface<BDO>{
    private final String name;
	protected CreateTransition(String name,DataObjectFactory<BDO> fac) {
		super(fac);
		this.name=name;
	}
	@Override
	public String getTypeName() {
		return name;
	}
	
}