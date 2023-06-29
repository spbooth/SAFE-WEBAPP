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

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.logging.Logger;



public class BlobStreamData implements StreamData {
	Blob blob;

	AppContext c;

	public BlobStreamData(AppContext conn, Blob b) {
		super();
		blob = b;
		c=conn;
	}

	public long getLength()  {
		try {
			return blob.length();
		} catch (SQLException e) {
			Logger.getLogger(getClass()).error("Failed to get length from Blob",e);
			return 0L;
		}
	}

	public InputStream getInputStream()  {
		try {
			return blob.getBinaryStream();
		} catch (SQLException e) {
			c.getService(DatabaseService.class).logError("Failed to get stream from blob", e);
			return null;
		}
	}

	public OutputStream getOutputStream()  {
		try {
			blob.truncate(0L);
			return blob.setBinaryStream(0);
		} catch (SQLException e) {
			c.getService(DatabaseService.class).logError("Failed to get stream from blob", e);
			return null;
		}
	}

	

	
	
}