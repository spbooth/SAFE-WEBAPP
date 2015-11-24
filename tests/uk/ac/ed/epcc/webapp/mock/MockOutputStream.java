// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class MockOutputStream extends ServletOutputStream {
	ByteArrayOutputStream inner = new ByteArrayOutputStream();
	/**
	 * 
	 */
	public MockOutputStream() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		inner.write(b);
	}

	public String toString(){
		return inner.toString();
	}
}
