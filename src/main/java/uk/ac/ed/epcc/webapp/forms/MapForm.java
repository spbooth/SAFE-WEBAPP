//| Copyright - The University of Edinburgh 2011                            |
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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.action.ConfirmMessage;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.*;
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.logging.Logger;


/** Form that can take its inputs as a Map
 * 
 * @author spb
 *
 */


public class MapForm extends BaseForm {

	public static final String GENERAL_ERROR = "general";
	protected String action_name = null;
	
	
	
	public MapForm(AppContext c) {
		super(c);
	}
	
	/**
	 * parse a map containing the POST parameters for this Form. Fill in the Per
	 * field errors and return parse status. Parse post may be called in a
	 * Servlet to interpret a POST or by a form when displaying errors. Even
	 * though we have been passed the missing fields and the error map on an
	 * error display we re-parse the params to initialise the state of the
	 * correct fields.
	 * 
	 * @param errors A {@link Map} or errors keyed by field or {@link #GENERAL_ERROR}
	 * @param params
	 *            These may be Strings from POST parameters or objects from
	 *            default values
	 * @param skip_null skip null inputs if true.
	 * @return boolean true if all OK
	 * 
	 */
	public boolean parsePost(Map<String,String> errors, Map<String,Object> params,boolean skip_null) {
		boolean ok = true;
		for (Iterator<String> it = getFieldIterator(); it.hasNext();) {
			String key = it.next();
			Field f =  getField(key);
			try {
				parseInput(f.getInput(),params,skip_null);
			} catch (FieldException e) {
				if (errors != null) {
					log.info("parse failed for "+key,e);
					// error message for user
					errors.put(key, e.getMessage());
				}
				ok = false;
			}
		}
		return ok;
	}
	/**
	 * Validate the form storing error information. Any field that already has
	 * an error recorded is assumed to have failed the parse stage and is
	 * skipped
	 * 
	 * 
	 * @param missing_fields
	 * @param errors
	 * @return boolean true if no errors
	 */
	public boolean validate(Collection<String> missing_fields, Map<String,String> errors) {
		boolean ok = true;
		Logger log = getLogger();
		for (Iterator<String> it = getFieldIterator(); it.hasNext();) {
			String key = it.next();
			//log.debug("field " + key);
			if (errors == null || !errors.containsKey(key)) {
				Field f =  getField(key);
				try {
 					f.validate();
				} catch (MissingFieldException e) {
					log.debug("missing " + key,e);
					// missing field
					if (missing_fields != null) {
						missing_fields.add(key);
					}
					ok = false;
				} catch (FieldException e) {
					log.debug("FieldException " + key,e);
					if (errors != null) {
						// error message for user
						errors.put(key, e.getMessage());
					}
					ok = false;
				}
			}

		}
		try {
			if (ok) {
				for(FormValidator v : validators){
					try {
						v.validate(this);
					}catch( ConfirmException e) {
						additional_confirm=new ConfirmMessage(e.getConfirm(), new String[] {e.getMessage()});
					}
					//log.debug("validator " + ok);

				}
			}
		} catch (ValidateException e) {
			ok = false;
			log.debug("form validator failed with exception",e);
			if( errors != null ){
			  String field = e.getField();
			  if( field == null ) {
				  errors.put(GENERAL_ERROR, e.getMessage());
			  }else{
				  errors.put(field,e.getMessage());
			  }
			}
		}
		return ok;
	}
	
	/**
	 * parse a Map of parameters.
	 * @param input
	 * @param params
	 *            Map of values. These may be String representation from POST or
	 *            Objects from default values.
	 * @param skip_null if true ignore any unset parameter
	 * @throws FieldException
	 */
	@SuppressWarnings("unchecked")
	private  void parseInput(Input input, Map<String,Object> params,boolean skip_null) throws FieldException {
		if(params == null ){
			return;
		}
		ParseVisitor vis = new ParseVisitor(params, skip_null);
		
		try {
			input.accept(vis);
		}catch(FieldException fe){
			throw fe;
		} catch (Exception e) {
			throw new ParseException("Invalid input",e);
		}
	}

	/** An {@link InputVisitor} that parses input from a {@link Map} of parameters.
	 * This allows {@link MultiInput}s to access values targeted at sub-inputs.
	 * 
	 */
	public static class ParseVisitor implements InputVisitor<Object>{
		/**
		 * @param params
		 * @param skip_null
		 */
		public ParseVisitor(Map<String, Object> params, boolean skip_null) {
			super();
			this.params = params;
			this.skip_null = skip_null;
		}
		private final Map<String,Object> params;
		private final boolean skip_null;
		@SuppressWarnings("unchecked")
		private  void parseMultiInput(MultiInput input, Map<String,Object> params,boolean skip_null)
				throws FieldException {
			if(params == null ){
				return;
			}
			// for convenience try a global parse if and only if global data exists
			// null values cannot be handled for global values.
			// handy on command line where the multi-input also implements parseInput
			// or when forms are populated from a script.
			if( ! defaultParseInput(input, params, true) ){
				// no global data look at the sub-inputs.
				for (Iterator<Input> it = input.getInputs(); it.hasNext();) {
					
					try {
						it.next().accept(this);
					}catch(FieldException fe){
						throw fe;
					} catch (Exception e) {
						throw new ParseException("Invalid input",e);
					}
				}
				input.validateInner();
			}
		}
		/** default parse behaviour (use parameter corresponding to input key)
		 * 
		 * @param <J>
		 * @param input  Input
		 * @param params  form params
		 * @param skip_null (ignore null values if true).
		 * @return true if data found
		 * @throws FieldException
		 */
		private <J> boolean defaultParseInput(Input<J> input, Map params,boolean skip_null)
		throws FieldException {
			Object data = params.get(input.getKey());
			if( data == null ) {
				if(! skip_null) {
					input.setNull();
				}
			}else if (data instanceof String && input instanceof ParseInput) {
				((ParseInput) input).parse((String) data);
				return true;
			} else {
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
					return true;
			}
			return false;

		}
		

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitBinaryInput(uk.ac.ed.epcc.webapp.forms.inputs.BinaryInput)
		 */
		@Override
		public Object visitBinaryInput(BinaryInput checkBoxInput)
				throws Exception {
			Object o = params.get(checkBoxInput.getKey());
			if( o == null ) {
				checkBoxInput.setChecked(false);
			}else {
				if( o instanceof String) {
					checkBoxInput.setChecked(checkBoxInput.getChecked().equals(o));
				}else {
					// last ditch fallback
					checkBoxInput.setValue(checkBoxInput.convert(o));
				}
			}
			return null;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitParseMultiInput(uk.ac.ed.epcc.webapp.forms.inputs.ParseMultiInput)
		 */
		@Override
		public <V, I extends Input> Object visitParseMultiInput(
				ParseMultiInput<V, I> multiInput) throws Exception {
			// should present as a multi-input by default so parse map accordingly
			return visitMultiInput(multiInput);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitMultiInput(uk.ac.ed.epcc.webapp.forms.inputs.MultiInput)
		 */
		@Override
		public <V, I extends Input> Object visitMultiInput(
				MultiInput<V, I> multiInput) throws Exception {
			parseMultiInput(multiInput, params, skip_null);
			return null;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitListInput(uk.ac.ed.epcc.webapp.forms.inputs.ListInput)
		 */
		@Override
		public <V, T> Object visitListInput(ListInput<V, T> listInput)
				throws Exception {
			Object o = params.get(listInput.getKey());
			if( o == null) {
				if( ! skip_null) {
					listInput.setNull();
				}
			}else {
				if( o instanceof String) {
					listInput.setItem(listInput.getItemByTag((String) o));
				}else {
					listInput.setValue(listInput.convert(o));
				}
			}
			return null;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitRadioButtonInput(uk.ac.ed.epcc.webapp.forms.inputs.ListInput)
		 */
		@Override
		public <V, T> Object visitRadioButtonInput(ListInput<V, T> listInput)
				throws Exception {
			visitListInput(listInput);
			return null;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitLengthInput(uk.ac.ed.epcc.webapp.forms.inputs.LengthInput)
		 */
		@Override
		public Object visitLengthInput(LengthInput input) throws Exception {
			defaultParseInput(input, params, skip_null);
			return null;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitUnmodifyableInput(uk.ac.ed.epcc.webapp.forms.inputs.UnmodifiableInput)
		 */
		@Override
		public Object visitUnmodifyableInput(UnmodifiableInput input)
				throws Exception {
			return null;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitFileInput(uk.ac.ed.epcc.webapp.forms.inputs.FileInput)
		 */
		@Override
		public Object visitFileInput(FileInput input) throws Exception {
			// We skip-null on file inputs so that existing
			// data is not removed if no replacement file has been
			// uploaded. Of course this means we can't remove a pre-populated data object.
			//TODO consider adding remove tick-box
			defaultParseInput(input, params, true);
			return null;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitPasswordInput(uk.ac.ed.epcc.webapp.forms.inputs.PasswordInput)
		 */
		@Override
		public Object visitPasswordInput(PasswordInput input) throws Exception {
			defaultParseInput(input, params, skip_null);
			return null;
		}
		
	}
	
	
	
	

	/** Set the name to use for the action parameter
	 * defaults to action
	 * 
	 * @param name
	 */
	public void setActionName(String name) {
		action_name=name;
	}
    public String getActionName() {
    	return action_name;
    }
	public String locateAction(Map<String,Object> params) throws ActionException{
		for (String name : getActionNames()) {
			if( action_name != null ){
				String val = (String) params.get(action_name);
				if( val != null && val.trim().equals(name.trim())){
					return name;
				}
			}else{
				//log.debug("looking for action <" + name + "> " + params.size());
				if (params.get(name) != null || params.get(name.trim()) != null) {
					//log.debug("found action");
					return name;
				}
			}
		}
		// check for explicit but illegal name
		if( action_name != null ){
			String val = (String) params.get(action_name);
			if( val != null  && val.trim().length() > 0){
				throw new ActionException("Unexpected action "+val);
			}
		}

		String defname = getSingleActionName();
		if( defname != null ){
			// BUG in IE8 with single text field. No action param sent.
			// take this a default fall-back for any map form.
			return defname;
		}
		return null;
	}
	/**
	 * perform any action requested for this form.
	 * 
	 * @param params
	 *            Map of form parameters
	 * @return result Object from the action
	 * @throws FieldException
	 * @throws ActionException
	 */
	public FormResult doAction(Map<String,Object> params) throws FieldException, ActionException {
		//Logger log = getContext().getLogger();
		String found_action = locateAction(params);
		
		if( found_action != null ){
			return doAction(found_action);
		}
		
		throw new ActionException("No matching action found");
	}

	/** return the {@link FormAction} for non validating actions.
	 * These ignore the form state as this is intended for
	 * cancel/retire  actions though we still may want the
	 * action to request confirmation.
	 * 
	 * @param params
	 * @return FormAction or null
	 * @throws FieldException
	 * @throws ActionException
	 */
	public FormAction getShortcutAction(Map<String,Object> params) throws FieldException,
			ActionException {
				//Logger log = getContext().getLogger();
				for (String name : getActionNames()) {
					//log.debug("looking for action <" + name + "> " + params.size());
					if (params.get(name) != null || params.get(name.trim()) != null) {
						FormAction a = getAction(name);
						if( ! a.getMustValidate()){
							return a;
						}
					}
				}
				return null;
			}
	/** return a map of values.
	 * 
	 * This is used in tests to extract the default values from a form. 
	 * 
	 * @return {@link Map}
	 */
	public Map<String,Object> addStringMap(Map<String,Object> map){
		for (Iterator<String> it = getFieldIterator(); it.hasNext();) {
			String key = it.next();
			Field f =  getField(key);
			Object val = f.getValue();
			if (val != null) {
				setValues(map, f.getInput());
			}
		}
		return map;
	}
	
	
	private void setValues(Map<String,Object> result ,Input i){
		SetParamVisitor vis = new SetParamVisitor(result);
		try {
			i.accept(vis);
		} catch (Exception e) {
			log.error("Error setting map", e);
		}
		
	}
}