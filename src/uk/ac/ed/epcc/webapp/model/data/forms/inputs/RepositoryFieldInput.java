// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms.inputs;

import java.util.Iterator;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;
import uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
/** Input to select one of the fields of a Repository
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: RepositoryFieldInput.java,v 1.3 2014/09/15 14:30:31 spb Exp $")

public class RepositoryFieldInput implements ListInput<String,Repository.FieldInfo>{
    private final Repository res;
    private FieldInfo item=null;
    private String key=null;
    public RepositoryFieldInput(Repository res){
    	this.res=res;
    }
	public FieldInfo getItembyValue(String value) {
		return res.getInfo(value);
	}

	public Iterator<FieldInfo> getItems() {
		return res.getInfo().iterator();
	}
	public int getCount(){
		return res.getInfo().size();
	}

	public String getTagByItem(FieldInfo item) {
		if( item == null ){
			return null;
		}
		return item.getName(false);
	}

	public String getTagByValue(String value) {
		return value;
	}

	public String getText(FieldInfo item) {
		if( item == null ){
			return null;
		}
		return item.getName(false);
	}

	public String convert(Object v) throws TypeError {
		if( v instanceof String){
			return (String)v;
		}
		throw new TypeError();
	}

	public String getKey() {
		return key;
	}

	public String getPrettyString(String value) {
		return value;
	}

	public String getString(String value) {
		return value;
	}

	public String getValue() {
		return getTagByItem(item);
	}

	public void setKey(String key) {
		this.key=key;
	}

	public String setValue(String v) throws TypeError {
		String old = getValue();
		item=res.getInfo(v);
		return old;
	}

	public void validate() throws FieldException {
		if( item == null ){
			throw new MissingFieldException();
		}
	}

	public FieldInfo getItem() {
		return item;
	}

	public void setItem(FieldInfo item) {
		this.item=item;
	}
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}

}