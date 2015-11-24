// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data;



/** varient of DataObject where HTML is allowed in the String fields
 * 
 * @author spb
 *
 */
@Deprecated
public abstract class HtmlDataObject extends DataObject {

	
	protected HtmlDataObject(Repository.Record record) {
		super(record);
		record.getRepository().setAllowHtml(true);
	}
	

}