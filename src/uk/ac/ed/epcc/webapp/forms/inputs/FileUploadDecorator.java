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

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;

/** A Input that decorated a String Input with a file-upload button
 * @author spb
 *
 */

public class FileUploadDecorator extends ParseMultiInput<String,Input> implements  ParseInput<String>{
	

	private ParseAbstractInput<String> master;
	private FileInput file;
	
	public FileUploadDecorator(ParseAbstractInput<String> input){
		master=input;
		addInput("Text", input);
		file = new FileInput();
	
		if( input instanceof LengthInput){
			file.setMaxUpload(((LengthInput)input).getMaxResultLength());
		}
		addInput("File", file);
		setLineBreaks(true);
	}

	public Input<String> getMaster(){
		return master;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#convert(java.lang.Object)
	 */
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
		return master.convert(v);
	}

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.MultiInput#getValue()
	 */
	@Override
	public String getValue() {
		// Take first value.
		if( ! master.isEmpty()) {
			return master.getValue();
		}
		return convert(file.getValue());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.MultiInput#setValue(java.lang.Object)
	 */
	@Override
	public String setValue(String v) throws TypeError {
		String old = getValue();
		master.setValue(v);
		return old;
	}

	@Override
	public boolean requireAll() {
		return false;
	}

	@Override
	public void validate() throws FieldException {
		if( master.isEmpty() && ! file.isEmpty()) {
			master.setValue(convert(file.getValue()));
		}
		master.validate();
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ParseInput#parse(java.lang.String)
	 */
	@Override
	public String parseValue(String v) throws ParseException {
		return master.parseValue(v);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ParseMapInput#getMap()
	 */
	@Override
	public Map<String, Object> getMap() {
		Map<String,Object> map = new HashMap<>();
		map.put(master.getKey(),master.getString());
		map.put(file.getKey(), file.getValue());
		return map;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ParseMapInput#parse(java.util.Map)
	 */
	@Override
	public boolean parse(Map<String, Object> v) throws ParseException {
		
		boolean is_leaf=true;
		// First consider a global param-name
		Object data = v.get(getKey());
		if( data != null) {
			master.parse(convert(data));
			is_leaf=false;
		}
		// Parse master input (overrides a global value)
		Object text = v.get(master.getKey());
		if( text != null ) {
			master.parse(master.convert(text));
			is_leaf=true;
		}
		// finally override with any file-upload
		Object f = v.get(file.getKey());
		if( f != null ) {
			master.parse(convert(f));
			is_leaf=true;
		}
		return is_leaf;
	}
}