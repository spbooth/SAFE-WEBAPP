//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.forms.inputs;

/** A {@link ListInput} where the items, tags and values are all the same String
 * 
 * 
 * @author Stephen Booth
 *
 */
public abstract class StringListInput extends SimpleListInput<String> {

	
	/**
	 * 
	 */
	public StringListInput() {
		
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#getText(java.lang.Object)
	 */
	@Override
	public String getText(String item) {
		return item;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ItemInput#getItembyValue(java.lang.Object)
	 */
	@Override
	public final String getItemByTag(String value) {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#getTagByItem(java.lang.Object)
	 */
	@Override
	public final String getTagByItem(String item) {
		return item;
	}

	
}
