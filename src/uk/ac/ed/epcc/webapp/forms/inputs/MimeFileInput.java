// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;
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
@uk.ac.ed.epcc.webapp.Version("$Id: MimeFileInput.java,v 1.2 2014/09/15 14:30:20 spb Exp $")

public class MimeFileInput extends FileInput {

	@Override
	public void validate() throws FieldException {
		super.validate();
		
		StreamData sd = getValue();
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

}