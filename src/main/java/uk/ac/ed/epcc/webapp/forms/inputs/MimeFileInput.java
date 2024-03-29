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
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;

/** FileInput that does not allow html uploads
 * (where mime type is known)
 * 
 * @author spb
 *
 */


public class MimeFileInput extends FileInput {

	/**
	 * 
	 */
	public MimeFileInput() {
		super();
		addValidator(new FieldValidator<StreamData>() {
			
			@Override
			public void validate(StreamData sd) throws FieldException {
				if( sd instanceof MimeStreamData){
					MimeStreamData msd = (MimeStreamData)sd;
				if( msd != null ){
					String type = msd.getContentType();
					if( type == null || type.toLowerCase().contains("html")){
						throw new ValidateException("Unsupported mime type");
					}
				}
				}
				
			}
		});
	}

}