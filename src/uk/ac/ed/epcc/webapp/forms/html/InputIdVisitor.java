// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.forms.html;

import java.util.Iterator;

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
@uk.ac.ed.epcc.webapp.Version("$Id: InputIdVisitor.java,v 1.3 2015/03/19 22:18:51 spb Exp $")
public class InputIdVisitor implements InputVisitor<String> {
	private String prefix;
	/**
	 * 
	 */
	public InputIdVisitor(String prefix) {
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
		return prefix+listInput.getKey();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitRadioButtonInput(uk.ac.ed.epcc.webapp.forms.inputs.ListInput)
	 */
	public <V, T> String visitRadioButtonInput(ListInput<V, T> listInput)
			throws Exception {
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

}
