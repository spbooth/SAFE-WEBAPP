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
package uk.ac.ed.epcc.webapp.model.log;

import javax.activation.DataSource;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.BlobType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamDataWrapper;





public class FileData extends DataObject implements Removable {
    public static final String DEFAULT_TABLE = "FileData";
	private static final String DATA = "Data";
	private static final String MIME_TYPE = "MimeType";
	private static final String NAME = "Name";
	
    public FileData(AppContext conn, int id) throws DataException{
    	super(getRecord(conn,DEFAULT_TABLE,id));
    }
	
    public FileData(AppContext context) {
		super(getRecord(context,DEFAULT_TABLE));
	}

	/**
	 * @param res
	 */
	public FileData(Record res) {
		super(res);
	}

	public void setData(MimeStreamData msd) {
    	record.setProperty(MIME_TYPE,msd.getContentType());
    	record.setProperty(NAME,msd.getName());
    	record.setProperty(DATA, msd);
    }
	public FileData copy() throws DataFault{
		FileData dat = new FileData(getContext());
		dat.setData(getData());
		dat.commit();
		return dat;
	}
    public MimeStreamData getData() throws DataFault{
    	return new MimeStreamDataWrapper(record.getStreamDataProperty(DATA),record.getStringProperty(MIME_TYPE),record.getStringProperty(NAME));
    }
	public DataSource getDataSource() throws DataFault {
		return (DataSource) getData();
	}
	public void remove() throws DataException {
		delete();
		
	}
	public static TableSpecification getDefaultTableSpecification(){
		TableSpecification spec = new TableSpecification("FileDataID");
		spec.setField(MIME_TYPE, new StringFieldType(true, null, 127));
		spec.setField(NAME, new StringFieldType(true, null, 255));
		spec.setField(DATA, new BlobType());
		return spec;
	}
}