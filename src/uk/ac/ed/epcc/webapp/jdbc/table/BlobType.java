// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.jdbc.table;
@uk.ac.ed.epcc.webapp.Version("$Id: BlobType.java,v 1.2 2014/09/15 14:30:25 spb Exp $")


public class BlobType extends FieldType<Object> {
	public BlobType(){
		super(Object.class,true,null);
	}

	@Override
	public void accept(FieldTypeVisitor vis) {
		vis.visitBlobType(this);
	}
}