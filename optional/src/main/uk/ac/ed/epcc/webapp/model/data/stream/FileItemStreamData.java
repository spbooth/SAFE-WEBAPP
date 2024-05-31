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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.fileupload.FileItem;

import uk.ac.ed.epcc.webapp.AppContext;


/** A wrapper for a {@link FileItem} that converts it into a {@link StreamData}
 * 
 * @author spb
 *
 */
public class FileItemStreamData  implements
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