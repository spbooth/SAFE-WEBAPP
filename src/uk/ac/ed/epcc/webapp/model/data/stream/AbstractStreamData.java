// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.data.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

public abstract class AbstractStreamData implements StreamData {

	public final void read(InputStream in) throws DataFault {
		OutputStream out = getOutputStream();
		if( out == null){
			throw new DataFault("Cannot get output stream");
		}
		int i;
		try {
			while((i=in.read())!=-1){
				out.write(i);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			throw new DataFault("error reading from stream",e);
		}
	
	}

	public final void write(OutputStream out) throws DataFault {
		InputStream in = getInputStream();
		if( in == null){
			throw new DataFault("Cannot get input stream");
		}
		int i;
		try {
			while((i=in.read())!=-1){
				out.write(i);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			throw new DataFault("error writing to stream",e);
		}
		
	}

	
}