// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.stream;


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * An implementation of MimeStreamData based on ByteArrayStreamData
 * 
 * @author spb
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ByteArrayMimeStreamData.java,v 1.4 2015/07/21 21:23:37 spb Exp $")

public class ByteArrayMimeStreamData extends ByteArrayStreamData implements
		MimeStreamData , Externalizable{
	private String type;

	private String name;

	public ByteArrayMimeStreamData() {
		super();
	}

	public ByteArrayMimeStreamData(MimeStreamData other) throws DataFault{
		this();
		if( other == null){
			return;
		}
		setName(other.getName());
		setMimeType(other.getContentType());
		read(other.getInputStream());
	}
	public ByteArrayMimeStreamData(byte[] dat) throws DataFault {
		super(dat);
	}

	public String getContentType() {
		return type;
	}

	public String getName()  {
		return name;
	}

	public void setMimeType(String s) {
		type = s;
	}

	public void setName(String s) {
		name = s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ByteArrayMimeStreamData other = (ByteArrayMimeStreamData) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		if( name == null || name.length() ==0){
			out.writeInt(0);
		}else{
			out.writeInt(name.length());
			out.writeObject(name);
		}
		if( type == null || type.length() ==0){
			out.writeInt(0);
		}else{
			out.writeInt(type.length());
			out.writeObject(type);
		}
		super.writeExternal(out);
		
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		int namelen = in.readInt();
		if( namelen > 0 ){
			name=(String) in.readObject();
		}
		int typelen = in.readInt();
		if( typelen > 0 ){
			type=(String) in.readObject();
		}
		super.readExternal(in);
	}

}