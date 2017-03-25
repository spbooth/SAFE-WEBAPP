//| Copyright - The University of Edinburgh 2015                            |
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
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.inputs.AutoComplete;
import uk.ac.ed.epcc.webapp.forms.inputs.AutocompleteTextInput;
import uk.ac.ed.epcc.webapp.forms.inputs.BinaryInput;
import uk.ac.ed.epcc.webapp.forms.inputs.BoundedInput;
import uk.ac.ed.epcc.webapp.forms.inputs.FileInput;
import uk.ac.ed.epcc.webapp.forms.inputs.FormatHintInput;
import uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.LengthInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.MultiInput;
import uk.ac.ed.epcc.webapp.forms.inputs.MultipleInput;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalInput;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseMultiInput;
import uk.ac.ed.epcc.webapp.forms.inputs.PasswordInput;
import uk.ac.ed.epcc.webapp.forms.inputs.PatternInput;
import uk.ac.ed.epcc.webapp.forms.inputs.PreSelectInput;
import uk.ac.ed.epcc.webapp.forms.inputs.RangedInput;
import uk.ac.ed.epcc.webapp.forms.inputs.UnitInput;
import uk.ac.ed.epcc.webapp.forms.inputs.UnmodifiableInput;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.preferences.Preference;

public class EmitHtmlInputVisitor implements InputVisitor<Object>{
	/**
	 * 
	 */
	private static final String HTML_TEXTAREA_MAX_ROWS = "html.textarea.max_rows";
	/**
	 * 
	 */
	private static final Preference USE_HTML5_FEATURE = new Preference("html5", true,"use html5 input types");
	private static final Preference ESCAPE_UNICODE_FEATURE = new Preference("html.input.escape_unicode",false,"Escape high code point characters in input values");
	AppContext conn;
	private SimpleXMLBuilder hb;
	private boolean use_post;
	private boolean use_html5;
	private boolean use_required=true;
	private Map post_params;
	private InputIdVisitor id_vis;
	private Object radio_selector=null;
	public EmitHtmlInputVisitor(AppContext conn,SimpleXMLBuilder hb, boolean use_post, Map post_params,String prefix){
		this.conn=conn;
		this.hb=hb;
		this.use_post=use_post;
		this.post_params=post_params;
		this.id_vis = new InputIdVisitor(prefix);
		use_html5 = conn==null || USE_HTML5_FEATURE.isEnabled(conn);
		
	}
	private void constantHTML(SimpleXMLBuilder hb,UnmodifiableInput input) {
		String label = input.getLabel();
		if( label.trim().contains("\n")){
			hb.open("pre");
			hb.clean(label);
			hb.close();
		}else{
			hb.clean( label);
		}
	}
	
	public void setRadioTarget(Object o){
		radio_selector=o;
	}

	public void setUseRequired(boolean use_required){
		this.use_required=use_required;
	}
	private void  emitBinaryHTML(SimpleXMLBuilder hb, boolean use_post, BinaryInput input,
			String param) {

		String checked = null;
		if (use_post) {
			if (input.getChecked().equals(param)) {
				checked = "checked";
			}
		} else {
			if (input.isChecked()) {
				checked = "checked";
			}
		}
		hb.open("input");
		try {
			String id = (String) input.accept(id_vis);
			if( id != null){
				hb.attr("id",id);
			}
		} catch (Exception e) {
			conn.error(e,"Error getting id");
		}
		hb.attr("type","checkbox");
		hb.attr("name",input.getKey());
		hb.attr("value",input.getChecked());
		if( checked != null){
		  hb.attr(checked,null);
		}
		hb.attr("class","input");
		hb.close();

	}
	
	

	protected void emitFileHTML(SimpleXMLBuilder hb,boolean use_post, FileInput input,
			String param) {
		String value = null;
		Object o = input.getValue();
		if( o != null ){
			if (o instanceof MimeStreamData) {
				// If we have the filename use it.
				value = ((MimeStreamData) o).getName();
			}else{
				value="uploaded-file";
			}
		}
		if( value != null ){
			hb.clean("("+value+") ");
		}
		hb.open("input");
		try {
			String id = (String) input.accept(id_vis);
			if( id != null){
				hb.attr("id",id);
			}
		} catch (Exception e) {
			conn.error(e,"Error getting id");
		}
		hb.attr("type","file");
		hb.attr("name",input.getKey());
		if (use_post && value != null) {
			hb.attr("value", value );
		}
		hb.attr("class","input");
		hb.close();
	}
	private  <X,T> void emitListHTML(SimpleXMLBuilder hb,boolean use_post, ListInput<X,T> input, String param) {
		assert(input!=null);
		Iterator<T> iter = input.getItems();
		if( iter.hasNext()){
		hb.open("select");
		try {
			String id = (String) input.accept(id_vis);
			if( id != null){
				hb.attr("id",id);
			}
		} catch (Exception e) {
			conn.error(e,"Error getting id");
		}
		hb.attr("name",input.getKey());
		hb.attr("class","input");
		boolean optional = input instanceof OptionalInput && ((OptionalInput)input).isOptional();
		if( use_html5 && use_required && ! optional){
			hb.attr("required",null);
		}
		if( optional ||
				(input instanceof PreSelectInput && ! ((PreSelectInput)input).allowPreSelect()) && input.getCount() > 1){
			// need ability to select nothing
			// a non optional pre-select input with only 1 valid answer always 
			// pre-selects.
			hb.open("option");
			hb.attr("value","");
			String unselected="Not Selected";
			if(input instanceof OptionalListInput){
				String override = ((OptionalListInput) input).getUnselectedText();
				if(override != null && override.trim().length() > 0){
					unselected = override;
				}
			}
			hb.clean(unselected);
			hb.close();
		}
		String def = null;
		if (use_post) {
			def = param;
		} else {
			X default_value = input.getValue();
			if (default_value != null) {
				def = input.getTagByValue(default_value);

			}
		}
		boolean seen_selected=false;
		for (; iter.hasNext();) {
			T current = iter.next();
			String tag = input.getTagByItem(current);
			hb.open("option");
			hb.attr("value", tag );
			if (def != null && def.equals(tag)) {
				hb.attr("selected",null);
				seen_selected=true;
			}
			String label = input.getText(current);
			hb.clean( label );
			hb.close();
			hb.clean("\n");

		}
		if( def != null && ! seen_selected ){
			// check for an out of band value
			T item = input.getItem();
			if( item != null ){
				String tag = input.getTagByItem(item);
				hb.open("option");
				hb.attr("value", tag);
				hb.attr("selected", null);
				String label = input.getText(item);
				hb.clean( label );
				hb.clean(" [not in default selection]");
				hb.close();
				hb.clean("\n");
			}
		}
		hb.close();
		}else{
			// no items !
			hb.open("span");
			hb.attr("class", "warn");
			hb.clean("No choices available");
			hb.close();
		}
	}
	private  <X,T> void emitRadioButtonListHTML(SimpleXMLBuilder hb,boolean use_post, ListInput<X,T> input, String param) {
		hb.open("div");
		
		hb.attr("class","radiobox");
		
		
		for (Iterator<T> iter = input.getItems(); iter.hasNext();) {
			T current = iter.next();
			emitRadioButtonHTML(hb, use_post,input, param, current);
			String label = input.getText(current);
			hb.clean( label );
			hb.open("br");
			hb.close();
			hb.clean("\n");

		}
		hb.close();
	}
	/**
	 * @param hb
	 * @param input
	 * @param def
	 * @param current
	 */
	private <T, X> void emitRadioButtonHTML(SimpleXMLBuilder hb,boolean use_post,
			ListInput<X, T> input, String param, T current) {
		String def = null;
		if (use_post) {
			def = param;
		} else {
			X default_value = input.getValue();
			if (default_value != null) {
				def = input.getTagByValue(default_value);

			}
		}
		// check current value is legal
		
		String tag = null;
		// can't use getTabByItem alone as this might not be in selected set.
		if( input.isValid(current)){
			tag = input.getTagByItem(current);
		}
		if( tag == null){
			return;	
		}
		hb.open("input");
		hb.attr("type","radio");
		hb.attr("name",input.getKey());
		
		hb.attr("value", tag );
		if (def != null && def.equals(tag)) {
			hb.attr("checked",null);
		}
		hb.close();
	}
	private void emitPasswordHTML(SimpleXMLBuilder hb,boolean use_post, PasswordInput input,
			String param) {
		String def = null;
		if (use_post) {
			def = param;
		} else {
			def = input.getString();
		}
		
		String id=null;
		try {
			id = (String) input.accept(id_vis);
		} catch (Exception e) {
			conn.error(e,"Error getting id");
		}
		emitTextParam(hb, input,input.getKey(),id, input.getBoxWidth(), input
				.getMaxResultLength(), input.getSingle(), true,def);
	
	}

	private void emitTextHTML(SimpleXMLBuilder hb,boolean use_post, LengthInput input,
			String param) {
		String def = null;
		if (use_post) {
			def = param;
		} else {
			def = input.getString();
		}
		String id=null;
		try {
			id = (String) input.accept(id_vis);
		} catch (Exception e) {
			conn.error(e,"Error getting id");
		}
		
		emitTextParam(hb, input,input.getKey(), id,input.getBoxWidth(), input
				.getMaxResultLength(), input.getSingle(), false,def);
		if( input instanceof UnitInput){
			String unit = ((UnitInput) input).getUnit();
			if (unit  != null) {
				hb.clean(" ");
				hb.clean(unit);
			}
		}
	}

	private String getParam(Input i){
		String param = "";
		if (post_params != null) {
			Object default_value = null;
			String key = i.getKey();
			if( key != null){
			  default_value = post_params.get(key);
			  // Check for renames this is to catch fixed name attributes passed
			  // from an auth plug-in only use if canonical name is null.
			  if( default_value == null && conn != null){
				  String alias = conn.getInitParameter("parameter_alias."+key);
				  if( alias != null ){
					  default_value = post_params.get(alias);
				  }
			  }
			}else{
				throw new ConsistencyError("Null key for "+i.getClass().getCanonicalName());
			}
			if (default_value != null) {
				if (default_value instanceof String) {
					param = (String) default_value;
				} else {
					param = default_value.toString();
				}
			}
		}
		return param;
	}
	public Object visitBinaryInput(BinaryInput input)
			throws Exception {
		emitBinaryHTML(hb, use_post, input, getParam(input));
		return null;
	}

	@SuppressWarnings("unchecked")
	public <V,T extends Input> Object visitMultiInput(MultiInput<V,T> input) throws Exception {
		if( input.hasLineBreaks()){
			hb.open("table");
			hb.attr("class","multi_input");
			for(String sub_key : input.getSubKeys()){
				hb.open("tr");
					if( input.hasSubLabels()){
						hb.open("td");
						String lab = input.getSubLabel(sub_key);
						if( lab != null ){
							hb.clean(lab);
						}
						hb.close();
					
					}
					hb.open("td");
					T i = input.getInput(sub_key);
					i.accept(this);
					hb.close();
				hb.close();
			}
			hb.close();
		}else{
			hb.open("span");
			hb.attr("class","multi_input");
			for(String sub_key : input.getSubKeys()){
				String lab = input.getSubLabel(sub_key);
				if( lab != null ){
					hb.clean(lab);
				}
				T i = input.getInput(sub_key);
				i.accept(this);
			}
			hb.close();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Object visitListInput(ListInput listInput) throws Exception {
		emitListHTML(hb,use_post, listInput, getParam(listInput));
		return null;
	}

	public Object visitLengthInput(LengthInput input) throws Exception {
		emitTextHTML(hb, use_post, input, getParam(input));
		return null;
	}

	public Object visitUnmodifyableInput(UnmodifiableInput input)
			throws Exception {
		constantHTML(hb,input);
		return null;
	}

	public Object visitFileInput(FileInput input) throws Exception {
		emitFileHTML(hb, use_post, input, getParam(input));
		return null;
	}
	public Object visitPasswordInput(PasswordInput input) throws Exception {
		emitPasswordHTML(hb, use_post, input, getParam(input));
		return null;
	}
	public <V, T> Object visitRadioButtonInput(ListInput<V, T> listInput)
			throws Exception {
		
		if( radio_selector == null ){
			emitRadioButtonListHTML(hb, use_post, listInput, getParam(listInput));
		}else{
			emitRadioButtonHTML(hb, use_post,listInput, getParam(listInput),(T) radio_selector);
		}
		return null;
	}
	/**
	 * output HTML for a text parameter field
	 * @param AppContext
	 * @param result 
	 * @param input
	 * @param name
	 *            name of control
	 * @param boxwid
	 *            maximum display width
	 * @param max_result_length
	 *            display length
	 * @param force_single
	 * @param default_value
	
	 */
	private void emitTextParam(SimpleXMLBuilder result,Input input,String name, String id, int boxwid, int max_result_length,
			boolean force_single, boolean force_password,String default_value) {
	
		String format_hint=null;
		if( input instanceof FormatHintInput){
			format_hint = ((FormatHintInput)input).getFormatHint();
		}
		boolean old_escape = result.setEscapeUnicode(! force_password && ESCAPE_UNICODE_FEATURE.isEnabled(conn));
		if (force_single || max_result_length <= 2 * boxwid) {
			int size = max_result_length;
			if (max_result_length > boxwid) {
				size = boxwid;
			}
			boolean autocomplete = input instanceof AutoComplete;

			if (autocomplete) {
				result.open("div");
				result.attr("class", "ui-widget");
			}
			
			result.open("input");
			if( id != null ){
				result.attr("id", id);
			}
			String type="text";
			if( use_html5 && input instanceof HTML5Input){
				String tmp = ((HTML5Input)input).getType();
				if( tmp != null ){
					type=tmp;
				}
			}
			if(force_password){
				type="password";
			}
			result.attr( "type",type);
			if( size > 0 ){
				result.attr("size",Integer.toString(size));
			}
			if( max_result_length > 0){
				result.attr("maxlength",Integer.toString(max_result_length));
			}
			result.attr("name", name );
			if (default_value != null && default_value.length() > 0) {
				result.attr("value",default_value);
			}
			result.attr("class","input");
			if ((autocomplete) && (use_html5)) {
				// add list attribute
				result.attr("list", name + "_list");
			}
			// Now for html verification
			if( use_html5){
				if( input instanceof PatternInput){
					String pattern = ((PatternInput)input).getPattern();
					if( pattern != null){
						result.attr("pattern",pattern);
					}
				}
				if( input instanceof BoundedInput){
					BoundedInput bounded = (BoundedInput) input;
					if( bounded.getType() != null){
						Object min = bounded.getMin();
						if( min != null ){
							result.attr("min",bounded.formatRange(min));
						}
						Object max = bounded.getMax();
						if( max != null){
							result.attr("max", bounded.formatRange(max));
						}
						if( input instanceof RangedInput){
							RangedInput ranged = (RangedInput)input;
							Number step = ranged.getStep();
							if( step == null ){
								result.attr("step", "any");
							}else{
								result.attr("step",ranged.formatRange(step));
							}
						}
					}
				}
				if( use_html5 && use_required ){
					if( ! (input instanceof OptionalInput) || ! ((OptionalInput) input).isOptional()){
						//Note we have to set
						// formnovalidate for non validating submit elements.
						result.attr("required",null);
					}
				}
				if( use_html5 && format_hint != null){
					result.attr("placeholder",format_hint);
				}
				if( use_html5 && input instanceof MultipleInput){
					if(((MultipleInput)input).isMultiple()){
						result.attr("multiple", "multiple");
					}
				}
			}
			result.close();
			
			if (autocomplete) {
				if (use_html5) {
					emitDataList(result, (AutoComplete) input, name);
				}
				result.close(); // close the <div>
			}
		} else {
			int rows = ((max_result_length + boxwid - 1) / boxwid);
			int size = (max_result_length + rows - 1) / rows;
			int max_text_rows = conn.getIntegerParameter(HTML_TEXTAREA_MAX_ROWS, 24);
		
			if (rows > max_text_rows) {
				rows = max_text_rows;
			}
			result.open("textarea");
			result.attr("rows",Integer.toString(rows));
			if( use_html5 ){
				result.attr("wrap","soft");
			}
			result.attr("cols",Integer.toString(size));
			result.attr("name", name );
			result.attr("class","input");
			if( id != null ){
				result.attr("id", id);
			}
			if( use_html5 && max_result_length > 0){
				// html5 allows maxlen in textarea
				result.attr("maxlength",Integer.toString(max_result_length));
			}
			if( use_html5 && use_required ){
				if( ! (input instanceof OptionalInput) || ! ((OptionalInput) input).isOptional()){
					//Note we have to set
					// formnovalidate for non validating submit elements.
					result.attr("required",null);
				}
			}
			if( use_html5 && format_hint != null){
				result.attr("placeholder",format_hint);
			}
			if (default_value != null && default_value.length() > 0) {
				result.clean(default_value);
			}else{
				result.clean("");
			}
			result.close();
		}
		if( format_hint != null && ! use_html5){
			result.clean(format_hint);
		}
		result.setEscapeUnicode(old_escape);
	}
	/**
	 * @param result
	 * @param input
	 * @param name
	 */
	private <T,V> void emitDataList(SimpleXMLBuilder result, AutoComplete<T,V> input, String name) {
		// add actual list of completions
		
		// can't put in noscritp as some browsers show all noscript contents as text.
		
		result.open("datalist");
		result.attr("id", name + "_list");
		// As susggested in the HTML standard if the datalist element is
		// not recognised can use a select with the same input name as the text input
		// if datalist is recognised then select element will have no effect
		result.open("select");
		result.attr("name", name);
		Set<T> suggestions = input.getSuggestions();
		if(suggestions!=null){
			for(T item : suggestions){
				String value = input.getValue(item);
				String text = input.getSuggestionText(item);
				result.open("option");
				result.attr("value", value);
				if( ! text.equals(value)){
					result.clean(text);
				}
				result.close();
			}
		}
		result.close(); // select
		result.close(); // datalist
		//result.close(); // noscript
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitParseMultiInput(uk.ac.ed.epcc.webapp.forms.inputs.ParseMultiInput)
	 */
	@Override
	public <V, I extends Input> Object visitParseMultiInput(
			ParseMultiInput<V, I> multiInput) throws Exception {
		return visitMultiInput(multiInput);
	}
	
}