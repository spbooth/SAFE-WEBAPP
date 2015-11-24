// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.fileupload.FileItem;

import uk.ac.ed.epcc.webapp.AppContext;
@uk.ac.ed.epcc.webapp.Version("$Id: FileItemStreamData.java,v 1.3 2015/08/13 18:25:47 spb Exp $")

/** A wrapper for a {@link FileItem} that converts it into a {@link StreamData}
 * 
 * @author spb
 *
 */
public class FileItemStreamData extends AbstractStreamData implements
MimeStreamData {
	FileItem item;
	AppContext conn;
	public FileItemStreamData(AppContext conn,FileItem i){
		item=i;
		this.conn=conn;
	}
	public String getContentType()  {
		return item.getContentType();
	}
	public String getName()  {
		return item.getName();
	}
	public long getLength()  {
		return item.getSize();
	}
	public InputStream getInputStream()  {
		try {
			return item.getInputStream();
		} catch (IOException e) {
			conn.error(e,"Error getting Strem from item");
			return null;
		}
	}

	public OutputStream getOutputStream()  {
		throw new UnsupportedOperationException("setDataStream not supported for FileItemStremData");
	}

}