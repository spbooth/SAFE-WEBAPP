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