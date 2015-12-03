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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.HtmlPrinter;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.BinaryInput;
import uk.ac.ed.epcc.webapp.forms.inputs.FileInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.LengthInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.MultiInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseMapInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseMultiInput;
import uk.ac.ed.epcc.webapp.forms.inputs.PasswordInput;
import uk.ac.ed.epcc.webapp.forms.inputs.UnmodifiableInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;


/** Form that can take its inputs as a Map
 * 
 * @author spb
 *
 */


public class MapForm extends BaseForm {

	public static final String GENERAL_ERROR = "general";
	protected String action_name = null;
	private Logger log;
	public MapForm(AppContext c) {
		super(c);
		log = c.getService(LoggerService.class).getLogger(getClass());
	}
	
	/**
	 * parse a map containing the POST parameters for this Form. Fill in the Per
	 * field errors and return parse status. Parse post may be called in a
	 * Servlet to interpret a POST or by a form when displaying errors. Even
	 * though we have been passed the missing fields and the error map on an
	 * error display we re-parse the params to initialise the state of the
	 * correct fields.
	 * 
	 * @param errors
	 * @param params
	 *            These may be Strings from POST parameters or objects from
	 *            default values
	 * @param skip_null skip null inputs if true.
	 * @return boolean true if all OK
	 * @throws FieldException
	 * 
	 */
	protected boolean parsePost(Map<String,String> errors, Map<String,Object> params,boolean skip_null) {
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
		Logger log = getContext().getService(LoggerService.class).getLogger(getClass());
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
					log.debug("exception " + key,e);
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
					v.validate(this);
					//log.debug("validator " + ok);

				}
			}

		} catch (ValidateException e) {
			ok = false;
			log.debug("form validator failed with exception",e);
			if( errors != null ){
			  errors.put(GENERAL_ERROR, e.getMessage());
			}
		}
		return ok;
	}
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
				parseInput( it.next(), params,skip_null);
			}
		}
	}
	/**
	 * parse a Map of parameters.
	 * @param input
	 * @param params
	 *            Map of values. These may be String representation from POST of
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

	public class ParseVisitor implements InputVisitor<Object>{
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
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitBinaryInput(uk.ac.ed.epcc.webapp.forms.inputs.BinaryInput)
		 */
		@Override
		public Object visitBinaryInput(BinaryInput checkBoxInput)
				throws Exception {
			defaultParseInput(checkBoxInput, params, skip_null);
			return null;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitParseMultiInput(uk.ac.ed.epcc.webapp.forms.inputs.ParseMultiInput)
		 */
		@Override
		public <V, I extends Input> Object visitParseMultiInput(
				ParseMultiInput<V, I> multiInput) throws Exception {
			parseMapInput(multiInput, params);
			return null;
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
			defaultParseInput(listInput, params, skip_null);
			return null;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitRadioButtonInput(uk.ac.ed.epcc.webapp.forms.inputs.ListInput)
		 */
		@Override
		public <V, T> Object visitRadioButtonInput(ListInput<V, T> listInput)
				throws Exception {
			defaultParseInput(listInput, params, skip_null);
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
	
	private void parseMapInput(ParseMapInput input, Map<String, Object> params) throws FieldException{
		input.parse(params);
		
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
		if (data instanceof String && input instanceof ParseInput) {
			((ParseInput) input).parse((String) data);
			return true;
		} else {
			if( ! skip_null || data != null ){
				//Note the check-boxes need to parse null as a result
				//so we can't skip null data
				// also don't want to ignore an input that has been cleared
				// so html usually does not set skip_null
				// however in the command line form it is generally better to 
				// use the default values where they exist
				input.setValue(input.convert(data));
				return true;
			}
			return false;
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
		for (Iterator<String> it = getActionNames(); it.hasNext();) {
			String name = it.next();
			if( action_name != null ){
				String val = (String) params.get(action_name);
				if( val != null && val.trim().equals(name.trim())){
					return doAction(name);
				}
			}else{
				//log.debug("looking for action <" + name + "> " + params.size());
				if (params.get(name) != null || params.get(name.trim()) != null) {
					//log.debug("found action");
					return doAction(name);
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
			return doAction(defname);
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
				for (Iterator<String> it = getActionNames(); it.hasNext();) {
					String name = it.next();
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
		if(i instanceof ParseMapInput){
			ParseMapInput c = (ParseMapInput) i;
			Map<String,Object> map = c.getMap();
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				String value = map.get(key).toString();
				result.put(key, value);
			}
		}else if (i instanceof MultiInput) {
			MultiInput c = (MultiInput) i;
			for (Iterator it = c.getInputs(); it.hasNext();) {
				Input t = (Input) it.next();
				addValue(result, t);
			}
		} else {
			addValue(result, i);
		}
	}
	
	private void addValue(Map<String,Object> map, Input i){
		if( i.getValue() == null ){
			return;
		}
		if( i instanceof ParseInput){
			map.put(i.getKey(), ((ParseInput)i).getString());
		}else{
			map.put(i.getKey(), i.getValue().toString());
		}
	}
}