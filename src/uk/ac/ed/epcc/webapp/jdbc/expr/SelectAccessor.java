// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;


@uk.ac.ed.epcc.webapp.Version("$Id: SelectAccessor.java,v 1.2 2014/09/15 14:30:24 spb Exp $")


public class SelectAccessor<T,R> implements Accessor<T,R> {
    private final Class<? super T> target;
    private final Accessor<T,R> accessors[];
    
    public SelectAccessor(Class<? super T> target, Accessor<T,R> accessors[]){
    	this.target=target;
    	this.accessors=accessors;
    }
	
	public Class<? super T> getTarget() {
		return target;
	}

	
	public T getValue(R r) {
		for(Accessor<T,R> a: accessors){
			T val = a.getValue(r);
			if( val != null ){
				return val;
			}
		}
		return null;
	}
	
	public boolean canSet() {
		
		return false;
	}
	public void setValue(R r, T value) {
		throw new UnsupportedOperationException("Set not supported");
		
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Select(");
		boolean seen=false;
		for(Accessor<T,R> a: accessors){
			if( seen ){
				sb.append(",");
			}
			seen=true;
			sb.append(a.toString());
		}
		sb.append(")");
		return sb.toString();
	}

}