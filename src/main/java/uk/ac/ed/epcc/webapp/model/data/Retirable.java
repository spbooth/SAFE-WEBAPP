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
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.model.lifecycle.ActionListener;

/**
 * Interface for DataObjects that support a Retire action
 * 
 * @author spb
 * 
 */
public interface Retirable {
	/**
	 * Is this object in a state that allows it to be retired.
	 * 
	 * @return boolean true if retire is possible
	 */
	public boolean canRetire();

	/**
	 * retire this object.
	 * 
	 * @throws Exception
	 */
	public void retire() throws Exception;
	
	/** Should retire be available as an action on the
	 * object edit form.
	 * 
	 * @return
	 */
	public default boolean useAction() {
		return true;
	}
	/**Get an optional object (usually a String or other object that can be added to display content) that should be presented to
	 * the user as warning before performing the operation.  Usually this would be included in a confirm dialog.
	 * Confirmation might be triggered by one of the {@link ActionListener}s returning a non-null value
	 * 
	 * @return
	 */
	public default Object getRetireWarning() {
		return null;
	}
}