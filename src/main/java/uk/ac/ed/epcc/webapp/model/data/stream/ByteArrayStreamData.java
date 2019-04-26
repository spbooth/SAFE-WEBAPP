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

import java.io.ByteArrayInputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * Implements StreamData holding the data locally in memory. The Object starts
 * empty and setDataStream must be used to initialise the contents.
 * 
 * @author spb
 * 
 */


public class ByteArrayStreamData extends AbstractStreamData implements StreamData, Externalizable {
	private ByteArrayOutputStream data;

	public ByteArrayStreamData() {
		data = null;
	}

	public ByteArrayStreamData(byte dat[]) throws DataFault {
		this();
		OutputStream o = getOutputStream();
		try {
			o.write(dat);
			o.close();
		} catch (IOException e) {
			throw new DataFault("error initialising ByteArrayDataStream", e);
		}

	}

	public long getLength()  {
		if (data != null) {
			return data.size();
		}
		return 0;
	}

	public byte[] getBytes(){
		if( data != null){
			return data.toByteArray();
		}
		return new byte[0];
	}
	public InputStream getInputStream()  {
		byte arr[];
		if (data != null) {
			arr = data.toByteArray();
		} else {
			arr = new byte[0];
		}
		return new ByteArrayInputStream(arr);
	}

	public OutputStream getOutputStream()  {
		data = new ByteArrayOutputStream();
		return data;
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (data == null) {
			return "";
		}
		return data.toString();
	}

	
	@Override
	public int hashCode() {
		return getBytes().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if( obj == null ){
			return false;
		}
		if( obj.getClass() != getClass()){
			return false;
		}
		return Arrays.equals(((ByteArrayStreamData)obj).getBytes(),getBytes());
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(getBytes());
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		byte[] data = (byte[]) in.readObject();
		OutputStream stream = getOutputStream();
		for( int i=0 ; i<data.length;i++){
			stream.write(data[i]);
		}
		stream.close();
		
	}
	

}