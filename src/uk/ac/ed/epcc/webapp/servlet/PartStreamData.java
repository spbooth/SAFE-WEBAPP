// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.Part;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.stream.AbstractStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class PartStreamData extends AbstractStreamData implements
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
