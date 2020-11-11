//| Copyright - The University of Edinburgh 2020                            |
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
package uk.ac.ed.epcc.webapp.model.data;


import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;

/**
 * @author Stephen Booth
 *
 */
public interface DataObjectSelector<B extends DataObject> extends Selector<DataObjectItemInput<B>> {

	/** Generate a new selector with the valid selection further restricted by an additional filter.
	 * 
	 * @param fil
	 * @return
	 */
	public DataObjectSelector<B> narrowSelector(BaseFilter<B> fil);
	/** Generate a new selector with the valid selection further restricted by an additional filter.
	 * Allow explict contol of the restrict option.
	 * 
	 * @param fil
	 * @param new_restrict 
	 * @return
	 */
	public DataObjectSelector<B> narrowSelector(BaseFilter<B> fil,boolean new_restrict);
}
