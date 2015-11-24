// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.mock;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Part;

import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class MockPart  implements Part {

	public String name;
	private Map<String,String> headers = new HashMap<String, String>();
	public ByteArrayMimeStreamData data = new ByteArrayMimeStreamData();
	
	public MockPart(String name){
		this.name=name;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.Part#delete()
	 */
	@Override
	public void delete() throws IOException {
		
	}

	

	/* (non-Javadoc)
	 * @see javax.servlet.http.Part#getHeader(java.lang.String)
	 */
	@Override
	public String getHeader(String arg0) {
		
		return headers.get(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.Part#getHeaderNames()
	 */
	@Override
	public Collection<String> getHeaderNames() {
		return headers.keySet();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.Part#getHeaders(java.lang.String)
	 */
	@Override
	public Collection<String> getHeaders(String arg0) {
		return headers.values();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.Part#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return data.getInputStream();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.Part#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.Part#getSize()
	 */
	@Override
	public long getSize() {
		return data.getLength();
	}

	public void setName(String name){
		this.name=name;
	}
	public void setFileName(String name){
		data.setName(name);
		headers.put("content-disposition","filename=\""+name+"\"");
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.Part#write(java.lang.String)
	 */
	@Override
	public void write(String arg0) throws IOException {
		
	}



	/* (non-Javadoc)
	 * @see javax.servlet.http.Part#getContentType()
	 */
	@Override
	public String getContentType() {
		return data.getContentType();
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MockPart other = (MockPart) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (headers == null) {
			if (other.headers != null)
				return false;
		} else if (!headers.equals(other.headers))
			return false;
		return true;
	}

}
