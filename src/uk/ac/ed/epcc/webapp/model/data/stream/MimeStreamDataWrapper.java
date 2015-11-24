// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.stream;

import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
/** Class to convert a StreamData to a MimeStreamData
 * Can also be used to override the mime type of a MimeStreamData
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: MimeStreamDataWrapper.java,v 1.2 2014/09/15 14:30:33 spb Exp $")

public class MimeStreamDataWrapper implements MimeStreamData, DataSource {
    StreamData sd;
    String mime;
    String name;
    public MimeStreamDataWrapper(StreamData sd,String mime,String name){
    	this.sd=sd;
    	this.mime=mime;
    	this.name=name;
    }
    public MimeStreamDataWrapper(MimeStreamData msd){
    	this.sd=msd;
    	this.mime=msd.getContentType();
    	this.name=msd.getName();
    }
	public String getContentType() {
		return mime;
	}

	public String getName(){
		return name;
	}

	public long getLength() {
		return sd.getLength();
	}

	public InputStream getInputStream(){
		return sd.getInputStream();
	}

	public OutputStream getOutputStream()  {
		return sd.getOutputStream();
	}

	public void read(InputStream in) throws DataFault {
		sd.read(in);

	}

	public void write(OutputStream out) throws DataFault {
		sd.write(out);
	}

}