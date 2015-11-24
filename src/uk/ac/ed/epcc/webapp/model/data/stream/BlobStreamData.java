// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
@uk.ac.ed.epcc.webapp.Version("$Id: BlobStreamData.java,v 1.2 2014/09/15 14:30:32 spb Exp $")


public class BlobStreamData implements StreamData {
	Blob blob;

	AppContext c;

	public BlobStreamData(AppContext conn, Blob b) {
		super();
		blob = b;
		c=conn;
	}

	public long getLength()  {
		try {
			return blob.length();
		} catch (SQLException e) {
			c.error(e,"Failed to get length from Blob");
			return 0L;
		}
	}

	public InputStream getInputStream()  {
		try {
			return blob.getBinaryStream();
		} catch (SQLException e) {
			c.error(e,"Failed to get stream from blob");
			return null;
		}
	}

	public OutputStream getOutputStream()  {
		try {
			return blob.setBinaryStream(0);
		} catch (SQLException e) {
			c.error(e,"Failed to get stream from blob");
			return null;
		}
	}

	public void read(InputStream in) throws DataFault {
		OutputStream out = getOutputStream();
		int i;
		try {
			while ((i = in.read()) != -1) {
				out.write(i);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			throw new DataFault("error reading from stream", e);
		}

	}

	public void write(OutputStream out) throws DataFault {
		InputStream in = getInputStream();
		int i;
		try {
			while ((i = in.read()) != -1) {
				out.write(i);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			throw new DataFault("error writing to stream", e);
		}

	}
}