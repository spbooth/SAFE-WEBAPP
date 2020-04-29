//| Copyright - The University of Edinburgh 2013                            |
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
package uk.ac.ed.epcc.webapp.forms.html;

import java.util.Iterator;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.inputs.BinaryInput;
import uk.ac.ed.epcc.webapp.forms.inputs.FileInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.LengthInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.MultiInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseMultiInput;
import uk.ac.ed.epcc.webapp.forms.inputs.PasswordInput;
import uk.ac.ed.epcc.webapp.forms.inputs.UnmodifiableInput;

/** Visitor to derive the HTML id string for an input
 * @author spb
 *
 */

public class InputIdVisitor implements InputVisitor<String> {
	private AppContext conn;
	private String prefix;
	private boolean optional;
	/**
	 * 
	 */
	public InputIdVisitor(AppContext conn,boolean optional,String prefix) {
		    this.conn=conn;
			this.optional=optional;
			this.prefix=prefix;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitBinaryInput(uk.ac.ed.epcc.webapp.forms.inputs.BinaryInput)
	 */
	public String visitBinaryInput(BinaryInput checkBoxInput) throws Exception {
		if( checkBoxInput == null || prefix == null){
			return null;
		}
		return prefix+checkBoxInput.getKey();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitMultiInput(uk.ac.ed.epcc.webapp.forms.inputs.MultiInput)
	 */
	public <V, I extends Input> String visitMultiInput(
			MultiInput<V, I> multiInput) throws Exception {
		if( multiInput == null || prefix==null){
			return null;
		}
		// match to first input.
		Iterator<I> it = multiInput.getInputs();
		if(it.hasNext()){
			return (String) it.next().accept(this);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitListInput(uk.ac.ed.epcc.webapp.forms.inputs.ListInput)
	 */
	public <V, T> String visitListInput(ListInput<V, T> listInput)
			throws Exception {
		if( listInput == null || prefix==null){
			return null;
		}
		if( listInput.getCount() == 0) {
			return null;
		}
		boolean forced=(! optional) &&(listInput.getCount() == 1);
		if( forced && EmitHtmlInputVisitor.LOCK_FORCED_LIST.isEnabled(conn) ) {
			return null;
		}
		
		return prefix+listInput.getKey();
	}

	private Object radio_selector=null;
	public void setRadioTarget(Object o){
		radio_selector=o;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitRadioButtonInput(uk.ac.ed.epcc.webapp.forms.inputs.ListInput)
	 */
	public <V, T> String visitRadioButtonInput(ListInput<V, T> listInput)
			throws Exception {
		if( radio_selector == null || listInput == null || prefix == null ) {
			return null;
		}
		String tag = listInput.getTagByItem((T) radio_selector);
		if( tag != null) {
			return prefix+listInput.getKey()+tag;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitLengthInput(uk.ac.ed.epcc.webapp.forms.inputs.LengthInput)
	 */
	public String visitLengthInput(LengthInput input) throws Exception {
		if(input == null || prefix == null){
			return null;
		}
		return prefix+input.getKey();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitUnmodifyableInput(uk.ac.ed.epcc.webapp.forms.inputs.UnmodifiableInput)
	 */
	public String visitUnmodifyableInput(UnmodifiableInput input)
			throws Exception {
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitFileInput(uk.ac.ed.epcc.webapp.forms.inputs.FileInput)
	 */
	public String visitFileInput(FileInput input) throws Exception {
		if( input == null || prefix==null){
			return null;
		}
		return prefix+input.getKey();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitPasswordInput(uk.ac.ed.epcc.webapp.forms.inputs.PasswordInput)
	 */
	public String visitPasswordInput(PasswordInput input) throws Exception{
		if( input == null || prefix==null){
			return null;
		}
		return prefix+input.getKey();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitParseMultiInput(uk.ac.ed.epcc.webapp.forms.inputs.ParseMultiInput)
	 */
	@Override
	public <V, I extends Input> String visitParseMultiInput(
			ParseMultiInput<V, I> multiInput) throws Exception {
		return visitMultiInput(multiInput);
	}

	public String normalise(String raw) {
		if( raw == null ) {
			return null;
		}
		return raw.replace('.', '_'); // jquery does not like periods in ids 
	}
}