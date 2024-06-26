// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far.handler;

import java.util.Iterator;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager;
import uk.ac.ed.epcc.webapp.model.far.response.StringDataManager;
import uk.ac.ed.epcc.webapp.validation.MaxLengthValidator;

/**
 * @author michaelbareford
 *
 */
public class MultiIntegerHandler implements QuestionFormHandler<MultiInput> {

	public class MultiIntegerInput extends MultiInput<String,Input<Integer>> {

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#convert(java.lang.Object)
		 */
		@Override
		public String convert(Object v) throws TypeException {
			if( v == null) {
				return null;
			}
			if( v instanceof String){
				return (String) v;
			}
			throw new TypeException(v.getClass());
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.MultiInput#getValue()
		 */
		@Override
		public String getValue() {
			StringBuilder sb = new StringBuilder();
			boolean seen = false;
			for(Iterator<Input<Integer>> it = getInputs(); it.hasNext();){
				if( seen ){
					sb.append(",");
				}else{
					seen=true;
				}
				Input<Integer> input = it.next();
				if( input.getValue() != null ){
					sb.append(Integer.toString(input.getValue()));
				}
			}
			return sb.toString();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.MultiInput#setValue(java.lang.Object)
		 */
		@Override
		public String setValue(String v) throws TypeException {
			String old = getValue();
			Iterator<Input<Integer>> it = getInputs();
			if( v != null ){
				for(String s : v.split(",")){
					if( it.hasNext()){
						Input<Integer> input = it.next();
						try{
							Integer value = Integer.valueOf(s);
							input.setValue(value);
						}catch(NumberFormatException e){
							throw new TypeException("Not an integer", e);
						}
					}else{
						throw new TypeException("Too many fields");
					}
				}
			}
			while( it.hasNext() ){
				it.next().setValue(null);
			}
			return old;
		}

	}
	
	private IntegerHandler int_handler = new IntegerHandler();
	private static final String LABELS = "Labels";
	
	
	@Override
	public Class<MultiInput> getTarget() {
		return MultiInput.class;
	}

	@Override
	public void buildConfigForm(Form f) {
		int_handler.buildConfigForm(f);
		
		TextInput labels = new TextInput();
		labels.setSingle(false);
		labels.addValidator(new MaxLengthValidator(256));
		f.addInput(LABELS, "Labels", labels);
	}

	@Override
	public MultiInput parseConfiguration(Form f) {
		MultiIntegerInput multi_input = new MultiIntegerInput();
		
		String labels = (String) f.get(LABELS);
				
		String label_array[] = labels.split(",");
		for(String label : label_array){
			IntegerInput input = (IntegerInput) int_handler.parseConfiguration(f);
			multi_input.addInput(label, input);
		}
		
		return multi_input;
	}

	@Override
	public Class<? extends ResponseDataManager> getDataClass() {
		return StringDataManager.class;
	}

	@Override
	public boolean hasConfig() {
		return true;
	}

}
