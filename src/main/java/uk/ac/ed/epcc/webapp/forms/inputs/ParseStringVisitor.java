package uk.ac.ed.epcc.webapp.forms.inputs;



import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.MapForm.ParseVisitor;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

/** An {@link InputVisitor} to parse a single String value.
 * @see ParseVisitor
 * 
 * @author Stephen Booth
 *
 */
public class ParseStringVisitor implements InputVisitor<Object> {
	private final String value;
	public ParseStringVisitor(String input) {
		this.value=input;
	}
	
	
	private <J> J defaultParse(Input<J> input, String data,boolean skip_null) throws ParseException {
		if( input instanceof ParseInput) {
			((ParseInput<J>)input).parse(data);
		} else {
			if( ! skip_null || data != null ){
				//Note the check-boxes need to parse null as a result
				//so we can't skip null data
				// also don't want to ignore an input that has been cleared
				// so html usually does not set skip_null
				// however in the command line form it is generally better to 
				// use the default values where they exist
				try {
					input.setValue(input.convert(data));
				} catch (TypeException e) {
					throw new ParseException("Illegal type conversion", e);
				}
			}
		}
		return input.getValue();
		
	}
	@Override
	public Object visitBinaryInput(BinaryInput checkBoxInput) throws Exception {
		
		return defaultParse(checkBoxInput, value, false);
	}
	@Override
	public <V, I extends Input> Object visitParseMultiInput(ParseMultiInput<V, I> multiInput) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put(multiInput.getKey(), value);
		multiInput.parse(map);
		return multiInput.getValue();
	}
	@Override
	public <V, I extends Input> Object visitMultiInput(MultiInput<V, I> multiInput) throws Exception {
		
		return defaultParse(multiInput, value, true);
	}
	@Override
	public <V, T> Object visitListInput(ListInput<V, T> listInput) throws Exception {
		return defaultParse(listInput, value, true);
	}
	@Override
	public <V, T> Object visitRadioButtonInput(ListInput<V, T> listInput) throws Exception {
		return defaultParse(listInput, value, true);
	}
	@Override
	public Object visitLengthInput(LengthInput input) throws Exception {
		return defaultParse(input, value, true);
	}
	@Override
	public Object visitUnmodifyableInput(UnmodifiableInput input) throws Exception {
		return null;
	}
	@Override
	public Object visitFileInput(FileInput input) throws Exception {
		return defaultParse(input, value, true);
	}
	@Override
	public Object visitPasswordInput(PasswordInput input) throws Exception {
		return defaultParse(input, value, true);
	}

}
