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
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;

/** A Input that decorated a String Input with a file-upload button
 * @author spb
 *
 */

public class FileUploadDecorator extends MultiInput<String,Input> implements OptionalInput{
	

	private ParseAbstractInput<String> master;
	private FileInput file;
	private boolean optional=false;
	public FileUploadDecorator(ParseAbstractInput<String> input){
		master=input;
		addInput("Text", input);
		file = new FileInput();
		optional=input.isOptional();
		// Need to be optional when generating html as this
		// may be validated in browser
		master.setOptional(true);
		file.setOptional(true);
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
		if( v == null || v instanceof String ){
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
		String val = master.getValue();
		if( val != null && val.trim().length() > 0){
			return val;
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
	public void validate() throws FieldException {
		String value = master.getValue();
		if( value == null || value.trim().length()==0 ){
			// promote to master
			setValue(getValue());
		}
		master.setOptional(optional);
		master.validate();
		master.setOptional(true);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.OptionalInput#isOptional()
	 */
	public boolean isOptional() {
		return optional;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.OptionalInput#setOptional(boolean)
	 */
	public void setOptional(boolean opt) {
		optional=opt;
	}
}