//| Copyright - The University of Edinburgh 2020                            |
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

import javax.servlet.http.HttpServletRequest;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;

/**
 * @author Stephen Booth
 *
 */
public class RequestMimeStreamData extends AbstractContexed implements MimeStreamData {

	private final HttpServletRequest req;
	/**
	 * 
	 */
	public RequestMimeStreamData(AppContext conn, HttpServletRequest req) {
		super(conn);
		this.req=req;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.stream.StreamData#getLength()
	 */
	@Override
	public long getLength() {
		
		return req.getContentLengthLong();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.stream.StreamData#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		
		try {
			return req.getInputStream();
		} catch (IOException e) {
			getLogger().error("Error getting stream",e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.stream.StreamData#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		throw new UnsupportedOperationException("getOutputStream not supported for RequestStreamData");
	}

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData#getContentType()
	 */
	@Override
	public String getContentType() {
		return req.getContentType();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData#getName()
	 */
	@Override
	public String getName() {
		return null;
	}

}
