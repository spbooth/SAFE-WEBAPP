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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;

/** Simple class to hold Text data in a table.
 * We want to store this information uncorrupted as it might contain
 * scripts etc so encode anything the Repository class might not like in this class
 * 
 * @author spb
 *
 */


public class TextData extends DataObject implements Removable {

	public static final String DEFAULT_TABLE = "TextData";
	private static final String TEXT = "Text";
	public TextData(AppContext context, int i) throws DataException {
		super(getRecord(context,DEFAULT_TABLE,i));
	}
	public TextData(AppContext context) {
		super(getRecord(context,DEFAULT_TABLE));
	}
	public TextData(Repository.Record rec){
		super(rec);
	}
	public static TableSpecification getTableSpecification(){
		TableSpecification spec = new TableSpecification("TextID");
		spec.setField(TEXT, new StringFieldType(true, null, 4096));
		return spec;
	}
	public String getText(){
		return record.getEncodedProperty(TEXT);
	}
	public void setText(String s){
		record.setEncodedProperty(TEXT, s);
	}
	public void remove() throws DataException {
		delete();
	}
   
}