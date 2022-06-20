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

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlFormPolicy;
import uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.preferences.Preference;
import uk.ac.ed.epcc.webapp.timer.TimeClosable;

public class EmitHtmlInputVisitor extends AbstractContexed implements InputVisitor<Object>{
	/**
	 * 
	 */
	private static final String HTML_TEXTAREA_MAX_ROWS = "html.textarea.max_rows";
	/**
	 * 
	 */
	private static final Preference USE_HTML5_FEATURE = new Preference("html5", true,"use html5 input types"); 
	private static final Preference ESCAPE_UNICODE_FEATURE = new Preference("html.input.escape_unicode",false,"Escape high code point characters in input values");
	private static final Feature USE_DATALIST = new Feature("html5.use_datalist",true,"Use html5 datalist syntax, disable to test the fallback mode (as if browser does not userstand datalist)");
	static final Feature LOCK_FORCED_LIST = new Feature("html.list_input.lock_forced",false,"Supress mandatory pull-down inputs with a single choice");
	private static final Feature MULTI_INPUT_TABLE = new Feature("html.multi_input.use_table",false,"Use layout tables for multi-input");
	private ExtendedXMLBuilder hb;
	private boolean use_post;
	private boolean use_html5;
	private boolean use_required=true;
	private boolean locked_as_hidden=false;
	private boolean optional=false;
	private Map post_params;
	
	private InputIdVisitor id_vis;
	private Object radio_selector=null;
	private boolean auto_focus=false;
	public EmitHtmlInputVisitor(AppContext conn,boolean optional,ExtendedXMLBuilder hb, boolean use_post, Map post_params,String prefix){
		super(conn);
		this.optional=optional;
		this.hb=hb;
		this.use_post=use_post;
		this.post_params=post_params;
		this.id_vis = new InputIdVisitor(conn,optional,prefix);
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
	public void setAutoFocus(boolean auto_focus) {
		this.auto_focus = auto_focus;
	}
	public boolean getLockedAsHidden() {
		return locked_as_hidden;
	}
	public void setLockedAsHidden(boolean locked_as_hidden) {
		this.locked_as_hidden = locked_as_hidden;
	}
	private void  emitBinaryHTML(ExtendedXMLBuilder hb, boolean use_post, BinaryInput input,
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
			String id = makeID(input);
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
		hb.addClass("input");
		hb.close();

	}
	protected String makeID(Input input) throws Exception {
		String raw = (String) input.accept(id_vis);
		return id_vis.normalise(raw);
	}
	
	

	protected void emitFileHTML(ExtendedXMLBuilder hb,boolean use_post, FileInput input,
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
			String id = makeID(input);
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
		String accept = input.getAccept();
		if( accept != null && accept.length() > 0) {
			hb.attr("accept", accept);
		}
		hb.addClass("input");
		hb.close();
	}
	private  <X,T> void emitListHTML(ExtendedXMLBuilder hb,boolean use_post, ListInput<X,T> input, String param) {
		assert(input!=null);
		boolean forced=! optional &&input.getCount() == 1;
		if( forced && LOCK_FORCED_LIST.isEnabled(conn) ) {
			Iterator<T> iter = input.getItems();
			T item = iter.next();
			hb.clean(input.getText(item));
			// only one option
			hb.open("input");
			hb.attr("type", "hidden");
			hb.attr("name",input.getKey());
			hb.attr("value",input.getTagByItem(item));
			hb.close();
			return;
		}
		
		Iterator<T> iter = input.getItems();
		
		if(iter != null &&  iter.hasNext()){
			hb.open("select");
			try {
				String id = makeID(input);
				if( id != null){
					hb.attr("id",id);
				}
			} catch (Exception e) {
				conn.error(e,"Error getting id");
			}
			hb.attr("name",input.getKey());
			hb.addClass("input");

			if( use_html5 && use_required && ! optional){
				hb.attr("required",null);
			}
			if( optional ||
					(input instanceof PreSelectInput && ! ((PreSelectInput)input).allowPreSelect()) && input.getCount() > 1 && (input.getValue() == null)){
				// need ability to select nothing
				// a non optional pre-select input with only 1 valid answer always 
				// pre-selects.
				// Non optional with an existing value does not need this either as it defaults to existing
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
			String grouplabel=null;
			boolean ingroup=false;
			boolean seen_selected=false;
			for (; iter.hasNext();) {
				T current = iter.next();
				String glabel = input.getGroup(current);
				if( ingroup && (glabel == null || ! glabel.equals(grouplabel))){
					// close group
					hb.close();
					ingroup=false;
				}
				if( glabel != null && ! glabel.equals(grouplabel)) {
					hb.open("optgroup");
					hb.attr("label", glabel);
					ingroup=true;
					grouplabel=glabel;
				}
				String tag = input.getTagByItem(current);
				hb.open("option");
				hb.attr("value", tag );
				String hover = input.getTooltip(current);
				if( hover != null ) {
					hb.attr("title", hover);
				}
				if ((def != null && def.equals(tag)) || forced) {
					hb.attr("selected",null);
					seen_selected=true;
				}
				String label = input.getText(current);
				hb.clean( label );
				hb.close();
				hb.clean("\n");

			}
			if( ingroup) {
				hb.close();
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
			hb.addClass( "warn");
			hb.clean("No choices available");
			hb.close();
		}
	}
	private  <X,T> void emitRadioButtonListHTML(ExtendedXMLBuilder hb,boolean use_post, ListInput<X,T> input, String param) {
		hb.open("div");
		
		hb.addClass("radiobox");
		
		
		for (Iterator<T> iter = input.getItems(); iter.hasNext();) {
			hb.open("div");
			hb.addClass("option");
			
			T current = iter.next();
			emitRadioButtonHTML(hb, use_post,input, param, current);
			String label = input.getText(current);
			if( HtmlFormPolicy.HTML_USE_LABEL_FEATURE.isEnabled(getContext())) {
				hb.open("label");
				try {
					id_vis.setRadioTarget(current);
					String id = makeID(input);
					if( id != null) {
						hb.attr("for",id);
					}
				} catch (Exception e) {
					getLogger().error("Error making radio button id", e);
				}finally {
					id_vis.setRadioTarget(null);
				}
				hb.clean(label);
				hb.close();
			}else {
				hb.clean( label );
				
			}
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
		if (use_post && param != null && ! param.isEmpty()) {
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
		String id = null;
		try {
			id_vis.setRadioTarget(current);
			id = makeID(input);
		}catch(Exception e) {
			getLogger().error("Error making radio button id", e);
		}finally {
			id_vis.setRadioTarget(null);
		}
		hb.open("input");
		hb.attr("type","radio");
		if( id != null) {
			hb.attr("id",id);
		}
		hb.attr("name",input.getKey());
		hb.attr("value", tag );
		if (def != null && def.equals(tag)) {
			hb.attr("checked",null);
		}
		hb.close();
	}
	private void emitPasswordHTML(ExtendedXMLBuilder hb,boolean use_post, PasswordInput input,
			String param) {
		String def = null;
		if (use_post) {
			def = param;
		} else {
			def = input.getString();
		}
		
		String id=null;
		try {
			id = makeID(input);
		} catch (Exception e) {
			conn.error(e,"Error getting id");
		}
		emitTextParam(hb, input,input.getKey(),id, input.getBoxWidth(), input
				.getMaxResultLength(), input.getSingle(), true,def);
	
	}

	private void emitTextHTML(ExtendedXMLBuilder hb,boolean use_post, LengthInput input,
			String param) {
		String def = null;
		if (use_post) {
			def = param;
		} else {
			def = input.getString();
		}
		String id=null;
		try {
			id = makeID(input);
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
		boolean saved = optional;
		try {
			// if we don't require all sub-inputs they must be shown as optional
			optional = optional || !input.requireAll();
		
		if( input.hasLineBreaks()){
			if( MULTI_INPUT_TABLE.isEnabled(getContext()) ) {
				hb.open("table");
				hb.addClass("multi_input");
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
				hb.open("div");
				hb.addClass("multi_input");
				if( input.hasSubLabels()) {
					// use grid layout to make 2 col
					// at time of writting all sub-label inputs are single line
					hb.addClass("sub_labels");
				}
				for(String sub_key : input.getSubKeys()){

					if( input.hasSubLabels()){
						hb.open("div");
						String lab = input.getSubLabel(sub_key);
						if( lab != null ){
							hb.clean(lab);
						}
						hb.close();

					}
					hb.open("div");
					T i = input.getInput(sub_key);
					i.accept(this);
					hb.close();
				}
				hb.close();
			}
		}else{
			hb.open("span");
			hb.addClass("multi_input");
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
		}finally {
			optional=saved;
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

	public Object visitLockedInput(LockedInput input) {
		constantHTML(hb,input);
		if( locked_as_hidden) {
			BaseHTMLForm.emitHiddenParam(hb, input.getNested());
		}
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
	private void emitTextParam(ExtendedXMLBuilder result,Input input,String name, String id, int boxwid, int max_result_length,
			boolean force_single, boolean force_password,String default_value) {
	
		String format_hint=null;
		if( input instanceof FormatHintInput){
			format_hint = ((FormatHintInput)input).getFormatHint();
		}
		boolean old_escape = result.setEscapeUnicode(! force_password && ESCAPE_UNICODE_FEATURE.isEnabled(conn));
		// max_result_length <= 0 is unlimited
		if (force_single || ( max_result_length > 0 && max_result_length <= 2 * boxwid)) {
			int size = max_result_length;
			if (max_result_length > boxwid) {
				size = boxwid;
			}
			boolean autocomplete = input instanceof AutoComplete && ((AutoComplete)input).useAutoComplete();
			boolean use_datalist = autocomplete && use_html5 && USE_DATALIST.isEnabled(conn);
			
			
			
			result.open("input");
			if( id != null ){
				result.attr("id", id);
			}
			if( use_html5 && auto_focus) {
				result.attr("autofocus",null);
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

			
			result.addClass("input");
			
			if (use_datalist) {
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
				if( use_html5 && use_required && (use_datalist || ! autocomplete)){
					if( ! optional){
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
					emitDataList(result, use_datalist,(AutoComplete) input, name,false);
				}
			}
		} else {
			int rows = ((max_result_length + boxwid - 1) / boxwid);
			int max_text_rows = conn.getIntegerParameter(HTML_TEXTAREA_MAX_ROWS, 24);
			if (max_result_length <= 0 || rows > max_text_rows) {
				rows = max_text_rows;
			}
			result.open("textarea");
			result.attr("rows",Integer.toString(rows));
			if( use_html5 ){
				result.attr("wrap","soft");
			}
			result.attr("cols",Integer.toString(boxwid));
			result.attr("name", name );
			result.addClass("input");
			if( id != null ){
				result.attr("id", id);
			}
			if( use_html5 && max_result_length > 0){
				// html5 allows maxlen in textarea
				result.attr("maxlength",Integer.toString(max_result_length));
			}
			if( use_html5 && use_required ){
				if( ! optional){
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
	private <T,V> void emitDataList(SimpleXMLBuilder result, boolean use_datalist, AutoComplete<T,V> input, String name,boolean wrap) {
		// add actual list of completions
		
		// can't put in noscritp as some browsers show all noscript contents as text.
		
		if( use_datalist){
			result.open("datalist");
			result.attr("id", name + "_list");
		}
		try(TimeClosable time = new TimeClosable(getContext(), () -> "datalist."+name)){
		// As susggested in the HTML standard if the datalist element is
		// not recognised can use a select with the same input name as the text input
		// if datalist is recognised then select element will have no effect
		if( wrap ) {
		result.open("select");
		result.attr("name", name);
			// Must have a not selected first entry
			result.open("option");
			result.attr("value", "");
			result.clean("Not selected");
			result.close();
		}
		Set<T> suggestions = input.getSuggestions();
		if(suggestions!=null){
			for(T item : suggestions){
				String value = input.getValue(item);
				if( value != null && ! value.isEmpty()) {
					String text = input.getSuggestionText(item).trim();
					value=value.trim();
					result.open("option");
					result.attr("value", value);
					if( ! text.equals(value)){
						result.clean(text);
					}
					result.close();
				}
			}
		}
		if( wrap ) {
			result.close(); // select
		}
		if( use_datalist){
			result.close(); // datalist
		}
		//result.close(); // noscript
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor#visitParseMultiInput(uk.ac.ed.epcc.webapp.forms.inputs.ParseMultiInput)
	 */
	@Override
	public <V, I extends Input> Object visitParseMultiInput(
			ParseMultiInput<V, I> multiInput) throws Exception {
		return visitMultiInput(multiInput);
	}
	@Override
	public <X> Object visitWrappedInput(WrappedInput<X> i) throws Exception {
		hb.open("div");
		String my_class = i.getWrapperClass();
		if( my_class != null ) {
			hb.addClass(my_class);
		}
		i.getNested().accept(this);
		hb.close();
		return null;
	}
	
}