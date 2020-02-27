//| Copyright - The University of Edinburgh 2018                            |
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

/** An {@link InputVisitor} that detects if the value of the input is forced.
 * This will update the input to the forced value as a side effect.
 * @author Stephen Booth
 *
 */
public class IsForcedVisitor implements InputVisitor<Boolean> {

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitBinaryInput(uk.ac.ed.epcc.webapp.forms.inputs.BinaryInput)
	 */
	@Override
	public Boolean visitBinaryInput(BinaryInput checkBoxInput) throws Exception {
		return false;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitParseMultiInput(uk.ac.ed.epcc.webapp.forms.inputs.ParseMultiInput)
	 */
	@Override
	public <V, I extends Input> Boolean visitParseMultiInput(ParseMultiInput<V, I> multiInput) throws Exception {
		return visitMultiInput(multiInput);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitMultiInput(uk.ac.ed.epcc.webapp.forms.inputs.MultiInput)
	 */
	@Override
	public <V, I extends Input> Boolean visitMultiInput(MultiInput<V, I> multiInput) throws Exception {
		for(String sub_key : multiInput.getSubKeys()){
			Input i = multiInput.getInput(sub_key);
			if( ! ((Boolean)i.accept(this))) {
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitListInput(uk.ac.ed.epcc.webapp.forms.inputs.ListInput)
	 */
	@Override
	public <V, T> Boolean visitListInput(ListInput<V, T> listInput) throws Exception {
		
		if( listInput.getCount() == 1 ) {
			// only one valid option
			T value = listInput.getItems().next();
			listInput.setItem(value);
			return true;
		}
		return false;
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitRadioButtonInput(uk.ac.ed.epcc.webapp.forms.inputs.ListInput)
	 */
	@Override
	public <V, T> Boolean visitRadioButtonInput(ListInput<V, T> listInput) throws Exception {
		return visitListInput(listInput);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitLengthInput(uk.ac.ed.epcc.webapp.forms.inputs.LengthInput)
	 */
	@Override
	public Boolean visitLengthInput(LengthInput input) throws Exception {
		// A max length of zero or less implies no limit
//		if( input.getMaxResultLength() < 1) {
//			return false;
//		}
		return false;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitUnmodifyableInput(uk.ac.ed.epcc.webapp.forms.inputs.UnmodifiableInput)
	 */
	@Override
	public Boolean visitUnmodifyableInput(UnmodifiableInput input) throws Exception {
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitFileInput(uk.ac.ed.epcc.webapp.forms.inputs.FileInput)
	 */
	@Override
	public Boolean visitFileInput(FileInput input) throws Exception {
		
		return false;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitPasswordInput(uk.ac.ed.epcc.webapp.forms.inputs.PasswordInput)
	 */
	@Override
	public Boolean visitPasswordInput(PasswordInput input) throws Exception {
		return false;
	}
   
	
	
}
