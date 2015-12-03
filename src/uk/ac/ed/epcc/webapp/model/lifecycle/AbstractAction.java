//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.model.lifecycle;


/**
 * @author spb
 *
 * @param <R>
 */
public class AbstractAction<R> implements ActionListener<R>{

	/**
	 * 
	 */
	public AbstractAction() {
		super();
	}

	public boolean allow(R target, boolean throw_reason) throws LifeCycleException {
		return true;
	}

	public void action(R target) {
		
	}

}