// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.data.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import uk.ac.ed.epcc.webapp.AppContext;
/** StreamData Object wrapping a file.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: FileStreamData.java,v 1.2 2014/09/15 14:30:33 spb Exp $")

public class FileStreamData extends AbstractStreamData {

	private File file;
	AppContext conn;
	public FileStreamData(AppContext conn,File f){
		this.conn=conn;
		this.file=f;
	}
	public long getLength() {
		return file.length();
	}

	public InputStream getInputStream() {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			conn.error(e,"Failed to open file for input");
			return null;
		}
	}

	public OutputStream getOutputStream() {
		try {
			return new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			conn.error(e,"Failed ot open file for output");
			return null;
		}
		
	}

	

}