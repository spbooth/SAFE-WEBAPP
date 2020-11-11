//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.Part;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;

/**
 * @author spb
 *
 */

public class PartStreamData implements
		MimeStreamData {
	private static final String CONTENT_DISPOSITION = "content-disposition";
    private static final String CONTENT_DISPOSITION_FILENAME = "filename";
    
	/**
	 * @param conn
	 * @param part
	 */
	public PartStreamData(AppContext conn, Part part) {
		super();
		this.conn = conn;
		this.part = part;
	}

	private final AppContext conn;
	private final Part part;
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.stream.StreamData#getLength()
	 */
	@Override
	public long getLength() {
		return part.getSize();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.stream.StreamData#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		try {
			return part.getInputStream();
		} catch (IOException e) {
			conn.getService(LoggerService.class).getLogger(getClass()).error("error getting input stream from part",e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.stream.StreamData#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		throw new UnsupportedOperationException("setDataStream not supported for FileItemStremData");
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData#getContentType()
	 */
	@Override
	public String getContentType() {
		return part.getContentType();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData#getName()
	 */
	@Override
	public String getName() {
		
	        for (String cd : part.getHeader(CONTENT_DISPOSITION).split(";")) {
	            if (cd.trim().startsWith(CONTENT_DISPOSITION_FILENAME)) {
	                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
	            }
	        }
	        return null;
	   
	}

}