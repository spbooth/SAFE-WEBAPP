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
import uk.ac.ed.epcc.webapp.model.data.Removable;
import uk.ac.ed.epcc.webapp.model.data.Repository;

/** Simple class to hold Text data in a table.
 *
 * For historical reasons the text is stored in a encoded form.
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
	/** 
	 * 
	 * @param input
	 * @return String
	 */
	 private String encode(String input){
	    	StringBuilder sb = new StringBuilder();
	    	for(int i=0;i<input.length();i++){
	    		char c = input.charAt(i);
	    		switch(c){
	    		case '>': sb.append("&g"); break;
	    		case '<': sb.append("&l"); break;
	    		case '"': sb.append("&q"); break;
	    		case '&': sb.append("&&"); break;
	    		case '\'': sb.append("&s"); break;
	    		default: sb.append(c);
	    		}
	    	}
	    	return sb.toString();
	    }
	 /** reverse a encode operation
	  * 
	  * @param input
	  * @return String
	  */
	    private String decode(String input){
	    	StringBuilder sb = new StringBuilder();
	    	for(int i=0;i<input.length();i++){
	    		char c = input.charAt(i);
	    		if( c == '&'){
	    			i++;
	    			c = input.charAt(i);
	    			switch(c){
	    			case 'g': sb.append('>'); break;
	    			case 'l': sb.append('<'); break;
	    			case 'q': sb.append('"'); break;
	    			case '&': sb.append('&'); break;
	    			case 's': sb.append('\''); break;
	    			default: ;
	    			}
	    		}else{
	    			sb.append(c);
	    		}
	    	}
	    	return sb.toString();
	    }
	public String getText(){
		return decode(record.getStringProperty(TEXT));
	}
	public void setText(String s){
		record.setProperty(TEXT, encode(s));
	}
	public void remove() throws DataException {
		delete();
	}
   
}