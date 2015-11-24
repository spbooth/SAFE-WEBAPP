// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.util.Date;

@uk.ac.ed.epcc.webapp.Version("$Id: MillisecondAccessor.java,v 1.2 2014/09/15 14:30:23 spb Exp $")


public class MillisecondAccessor<R> implements Accessor<Long,R>{
	private final Accessor<Date,R> a;
	public MillisecondAccessor(Accessor<Date,R> a){
		this.a=a;
	}
	public Long getValue(R r) {
		return Long.valueOf(a.getValue(r).getTime());
	}
	public Class<? super Long> getTarget() {
		return Long.class;
	}
	@Override
	public String toString(){
		return "Millis("+a.toString()+")";
	}
	public boolean canSet() {
		
		return false;
	}
	public void setValue(R r, Long value) {
		throw new UnsupportedOperationException("Set not supported");
		
	}
}