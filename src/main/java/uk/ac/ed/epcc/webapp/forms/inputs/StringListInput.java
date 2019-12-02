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

/** A {@link ListInput} where the items tags and values are the same String
 * 
 * 
 * @author Stephen Booth
 *
 */
public abstract class StringListInput extends AbstractInput<String> implements ListInput<String, String> {

	
	/**
	 * 
	 */
	public StringListInput() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#accept(uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor)
	 */
	@Override
	public final <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ItemInput#setItem(java.lang.Object)
	 */
	@Override
	public final void setItem(String item) {
		setValue(item);
	}

	public final String getItem() {
		return getValue();
	}
	

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#getTagByValue(java.lang.Object)
	 */
	@Override
	public final String getTagByValue(String value) {
		return value;
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
	public final String getItembyValue(String value) {
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
