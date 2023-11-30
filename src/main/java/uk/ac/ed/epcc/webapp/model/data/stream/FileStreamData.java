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
package uk.ac.ed.epcc.webapp.model.data.stream;

import java.io.*;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.Logger;
/** StreamData Object wrapping a file.
 * 
 * @author spb
 *
 */


public class FileStreamData implements StreamData {

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
			getLogger().error("Failed to open file for input",e);
			return null;
		}
	}
	Logger getLogger() {
		return Logger.getLogger(conn,getClass());
	}

	public OutputStream getOutputStream() {
		try {
			return new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			getLogger().error("Failed ot open file for output",e);
			return null;
		}
		
	}

	

}