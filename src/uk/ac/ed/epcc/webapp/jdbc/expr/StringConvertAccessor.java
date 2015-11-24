// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;


@uk.ac.ed.epcc.webapp.Version("$Id: StringConvertAccessor.java,v 1.2 2014/09/15 14:30:24 spb Exp $")


public class StringConvertAccessor<T,R> implements Accessor<String,R> {

	protected Accessor<T,R> a;

	public StringConvertAccessor(Accessor<T,R> acc) {
		a=acc;
	}

	public Class<? super String> getTarget() {
		return String.class;
	}

	public String getValue(R r) {
	    T temp = a.getValue(r);
	    if( temp != null ){
	    	return temp.toString();
	    }
		return null;
	}
	public boolean canSet() {
		
		return false;
	}
	public void setValue(R r, String value) {
		throw new UnsupportedOperationException("Set not supported");
		
	}
    @Override
	public String toString(){
    	StringBuilder sb = new StringBuilder();
    	sb.append("String(");
    	if (a instanceof SQLExpression) {
			((SQLValue) a).add(sb, true);
		} else {
			sb.append(a.toString());
		}
    	sb.append(")");
    	return sb.toString();
    }
}