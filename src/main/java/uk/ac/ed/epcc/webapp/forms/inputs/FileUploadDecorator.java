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
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;

/** A Input that decorated a String Input with a file-upload button
 * @author spb
 *
 */

public class FileUploadDecorator extends ParseMultiInput<String,Input> implements  ParseInput<String>{
	

	private ParseAbstractInput<String> parent;
	private FileInput file;
	
	public FileUploadDecorator(ParseAbstractInput<String> input){
		parent=input;
		addInput("Text", input);
		file = new FileInput();
	
		if( input instanceof LengthInput){
			file.setMaxUpload(((LengthInput)input).getMaxResultLength());
		}
		addInput("File", file);
		setLineBreaks(true);
	}

	public Input<String> getMaster(){
		return parent;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#convert(java.lang.Object)
	 */
	@Override
	public String convert(Object v) throws TypeError {
		if( v == null ){
			return (String) v;
		}
		if( v instanceof StreamData){
			StreamData data = (StreamData) v;
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			try {
				data.write(stream);
			} catch (Exception e) {
				throw new TypeError(e);
			}
			return stream.toString();
		}
		return parent.convert(v);
	}

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.MultiInput#getValue()
	 */
	@Override
	public String getValue() {
		// Take first value.
		if( ! parent.isEmpty()) {
			return parent.getValue();
		}
		return convert(file.getValue());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.MultiInput#setValue(java.lang.Object)
	 */
	@Override
	public String setValue(String v) throws TypeError {
		String old = getValue();
		parent.setValue(v);
		return old;
	}

	@Override
	public boolean requireAll() {
		return false;
	}

	@Override
	public void validateInner() throws FieldException {
		if( parent.isEmpty() && ! file.isEmpty()) {
			parent.setValue(convert(file.getValue()));
		}
		parent.validate();
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ParseInput#parse(java.lang.String)
	 */
	@Override
	public String parseValue(String v) throws ParseException {
		return parent.parseValue(v);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ParseMapInput#getMap()
	 */
	@Override
	public Map<String, Object> getMap() {
		Map<String,Object> map = new HashMap<>();
		String parent_string = parent.getString();
		if( parent_string != null ) {
			map.put(parent.getKey(),parent_string);
		}
		StreamData file_value = file.getValue();
		if( file_value != null ) {
			map.put(file.getKey(), file_value);
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ParseMapInput#parse(java.util.Map)
	 */
	@Override
	public boolean parse(Map<String, Object> v) throws ParseException {
		
		boolean is_leaf=true;
		boolean is_set=false;
		// First consider a global param-name
		Object data = v.get(getKey());
		if( data != null) {
			parent.parse(convert(data));
			is_leaf=false;
			is_set=true;
		}
		// Parse master input (overrides a global value)
		Object text = v.get(parent.getKey());
		if( text != null ) {
			parent.parse(parent.convert(text));
			is_leaf=true;
			is_set=true;
		}
		// finally override with any file-upload
		Object f = v.get(file.getKey());
		if( f != null ) {
			parent.parse(convert(f));
			is_leaf=true;
			is_set=true;
		}
		if( ! is_set ) {
			// no values have been seen at all
			parent.parse(null);
			is_leaf=true;
		}
		
		return is_leaf;
	}

}