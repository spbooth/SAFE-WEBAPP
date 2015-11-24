// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.exec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Thread to consume an {@link InputStream}.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class InputStreamThread extends Thread {
	

	/**
	 * @param stream
	 */
	public InputStreamThread(InputStream stream) {
		super();
		this.stream = stream;
		result = new ByteArrayOutputStream();
	}

	private final InputStream stream;
	private final ByteArrayOutputStream result;

	@Override
	public void run() {
		byte[] buffer = new byte[512];
		int bytes_read = 0;
		try {
			while( (bytes_read = stream.read(buffer, 0, 512)) != -1){
				result.write(buffer, 0, bytes_read);
			}
		} catch (IOException e) {
			return;
			
		}
		
		
	}
	
	public byte[] getBytes(){
		return result.toByteArray();
	}
	
	public String getString(){
		return result.toString();
	}
}
