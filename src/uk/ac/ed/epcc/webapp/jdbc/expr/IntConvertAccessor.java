// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;


@uk.ac.ed.epcc.webapp.Version("$Id: IntConvertAccessor.java,v 1.2 2014/09/15 14:30:23 spb Exp $")


public class IntConvertAccessor<T,R> implements Accessor<Integer,R> {

	protected Accessor<T,R> a;

	public IntConvertAccessor(Accessor<T,R> acc) {
		a=acc;
	}

	public Class<? super Integer> getTarget() {
		return Integer.class;
	}

	public Integer getValue(R r) {
	    T temp = a.getValue(r);
	    if( temp != null ){
	    	if( temp instanceof Number ){
	    		return Integer.valueOf(((Number)temp).intValue());
	    	}
	    	if( temp instanceof String){
	    		return Integer.parseInt((String)temp);
	    	}
	    }
		return null;
	}
    @Override
	public String toString(){
    	StringBuilder sb = new StringBuilder();
    	sb.append("String(");
		sb.append(a.toString());
    	sb.append(")");
    	return sb.toString();
    }
    public boolean canSet() {
    	
    	return false;
    }
    public void setValue(R r, Integer value) {
    	throw new UnsupportedOperationException("Set not supported");
    	
    }
}