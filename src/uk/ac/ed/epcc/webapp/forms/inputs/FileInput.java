// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
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
@uk.ac.ed.epcc.webapp.Version("$Id: FileInput.java,v 1.2 2014/09/15 14:30:19 spb Exp $")

public class FileInput extends AbstractInput<StreamData> {
    long max_upload=0;
    /** Set the maximum upload size for this input.
     * 
     * @param val max uplaod size in bytes
     * @return previous value
     */
    public long setMaxUpload(long val){
    	long old = max_upload;
    	max_upload=val;
    	return old;
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.AbstractInput#validate(boolean)
	 */
	@Override
	public void validate() throws FieldException {
		super.validate();
		
			Object o = getValue();
			if( o == null ){
				// must be optional
				return;
			}
			if (o instanceof StreamData ) {
				StreamData sd = (StreamData)o;
				if( max_upload > 0 && sd.getLength() > max_upload){
					throw new ValidateException("Upload size greater than allowed maximum "+max_upload);
				}
				return;
			}
			
			throw new ConsistencyError("Bad Type for FileInput, not multipart form?");

	}
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitFileInput(this);
	}

}