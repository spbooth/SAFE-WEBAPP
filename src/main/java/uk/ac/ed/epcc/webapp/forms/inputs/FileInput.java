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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;

/**
 * A file upload type input. The value of this input should be a class
 * implementing the StreamData interface.
 * 
 * Normally the underlying DB will have a limit on the maximum upload size so add this check to the input
 * @author spb
 * 
 * 
 */


public class FileInput extends AbstractInput<StreamData> {
	/**
	 * 
	 */
	public FileInput() {
		super();
		addValidator(new FieldValidator<StreamData>() {
			
			@Override
			public void validate(StreamData sd) throws FieldException {
				long length = sd.getLength();
				if( max_upload > 0 && length > max_upload){
					throw new ValidateException("Upload size greater than allowed maximum "+max_upload);
				}
				
			}
		});
	}
	// pattern for accepted mime types
	private String accept=null;
    long max_upload=0;
    /** Set the maximum upload size for this input.
     * 
     * @param val max upload size in bytes
     * @return previous value
     */
    public long setMaxUpload(long val){
    	long old = max_upload;
    	max_upload=val;
    	return old;
    }
	
	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitFileInput(this);
	}
	/**
	 * @return the accept
	 */
	public String getAccept() {
		return accept;
	}
	/**
	 * @param accept the accept to set
	 */
	public void setAccept(String accept) {
		this.accept = accept;
	}
	@Override
	public boolean isEmpty() {
		StreamData value = getValue();
		return ( value == null || value.getLength() == 0L);
	}

}