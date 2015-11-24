// Copyright - The University of Edinburgh 2011
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
@uk.ac.ed.epcc.webapp.Version("$Id: TextData.java,v 1.9 2014/09/15 14:30:34 spb Exp $")

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