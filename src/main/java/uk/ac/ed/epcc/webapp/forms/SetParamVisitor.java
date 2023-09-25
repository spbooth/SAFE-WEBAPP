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
package uk.ac.ed.epcc.webapp.forms;

import java.util.Iterator;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.BinaryInput;
import uk.ac.ed.epcc.webapp.forms.inputs.FileInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.LengthInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.LockedInput;
import uk.ac.ed.epcc.webapp.forms.inputs.MultiInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseMultiInput;
import uk.ac.ed.epcc.webapp.forms.inputs.PasswordInput;
import uk.ac.ed.epcc.webapp.forms.inputs.UnmodifiableInput;

/** An {@link InputVisitor} that adds the inputs contents to a map
 * equivalent to the form post parameters
 * @author Stephen Booth
 *
 */
public class SetParamVisitor implements InputVisitor<Object> {
	@Override
	public <V> Object visitLockedInput(LockedInput<V> l) throws Exception {
		return l.getNested().accept(this);
	}

	private Map<String,Object> params;

	public SetParamVisitor(Map<String,Object> params) {
		this.params=params;
	}
	@Override
	public Object visitBinaryInput(BinaryInput checkBoxInput) throws Exception {
		if( checkBoxInput.isChecked()) {
			params.put(checkBoxInput.getKey(), checkBoxInput.getChecked());
		}
		return null;
	}
	@Override
	public <V, I extends Input> Object visitParseMultiInput(ParseMultiInput<V, I> multiInput) throws Exception {
		params.putAll(multiInput.getMap());
		return null;
	}
	@Override
	public <V, I extends Input> Object visitMultiInput(MultiInput<V, I> multiInput) throws Exception {
		for(Iterator<I> it = multiInput.getInputs(); it.hasNext();){
			I i = it.next();
			i.accept(this);
		}
		return null;
	}
	@Override
	public <V, T> Object visitListInput(ListInput<V, T> listInput) throws Exception {

		V value = listInput.getValue();
		if( value != null ){
			params.put(listInput.getKey(), listInput.getTagByValue(value));
		}
		return null;
	}
	@Override
	public <V, T> Object visitRadioButtonInput(ListInput<V, T> listInput) throws Exception {
		return visitListInput(listInput);
	}
	@Override
	public Object visitLengthInput(LengthInput input) throws Exception {
		return visitBaseInput(input);
	}
	@Override
	public Object visitUnmodifyableInput(UnmodifiableInput input) throws Exception {
		return null;
	}
	@Override
	public Object visitFileInput(FileInput input) throws Exception {
		return null;
	}
	@Override
	public Object visitPasswordInput(PasswordInput input) throws Exception {
		return visitBaseInput(input);
	}

	private <T> Object visitBaseInput(Input<T> input) throws ParseException{ 
		T value = input.getValue();
		if( value != null ){
			params.put(input.getKey(),input.getString(value));
		}
		return null;
	}
}
