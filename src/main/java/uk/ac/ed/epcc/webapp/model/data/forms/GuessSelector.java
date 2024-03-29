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
package uk.ac.ed.epcc.webapp.model.data.forms;

import java.util.Iterator;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.SuggestedItemInput;

/** A {@link Selector} that pre-populates an input to a valid value if it can.
 * (ie a valid item if the input is a {@link SuggestedItemInput}.)
 * This is intended for optional fields where we want to guess a value rather than defaulting
 * to no value. A value should only be guessed if the set of suggested values is less than a threshold.
 * A {@link SuggestedItemInput} may generate a large list of all valid inputs and it is unhelpful to
 * just suggest the first of these.
 * A zero or negative threshold means guess in all cases.
 * 
 * 
 * @author Stephen Booth
 *
 */
public class GuessSelector<T extends Input> extends AbstractContexed implements Selector<T> {
	/**
	 * @param nested
	 */
	public GuessSelector(AppContext c,Selector<T> nested, int threshold) {
		super(c);
		this.nested = nested;
		this.threshold=threshold;
	}

	private final Selector<T> nested;
	private final int threshold;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#getInput()
	 */
	@Override
	public T getInput() {
		T input = nested.getInput();
		if( input instanceof SuggestedItemInput) {
			SuggestedItemInput listInput = (SuggestedItemInput)input;
			if( threshold <= 0 || listInput.getCount() < threshold) {
				Iterator it = listInput.getItems();
				if( it.hasNext()) {
					listInput.setItem(it.next());
					if(it instanceof AutoCloseable) {
						try {
							((AutoCloseable)it).close();
						} catch (Exception e) {
							getLogger().error("Error closing iterator",e);
						}
					}
				}
			}
		}
		return input;
	}
}
