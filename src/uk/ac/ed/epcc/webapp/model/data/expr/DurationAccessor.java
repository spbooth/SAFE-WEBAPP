// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.expr;

import java.util.Date;

import uk.ac.ed.epcc.webapp.jdbc.expr.Accessor;
import uk.ac.ed.epcc.webapp.model.data.Duration;
@uk.ac.ed.epcc.webapp.Version("$Id: DurationAccessor.java,v 1.9 2014/09/15 14:30:30 spb Exp $")


public class DurationAccessor<R> implements Accessor<Duration,R> {
  private final Accessor<Date,R> start, end;
  public DurationAccessor(Accessor<Date,R> start, Accessor<Date,R> end){
	  this.start=start;
	  this.end=end;
  }
public Duration getValue(R r) {

	return new Duration(start.getValue(r),end.getValue(r));
}
public Class<? super Duration> getTarget() {
	return Duration.class;
}
@Override
public String toString() {
	return "Duration("+start.toString()+","+end.toString()+")";
}
public boolean canSet() {
	
	return false;
}
public void setValue(R r, Duration value) {
	throw new UnsupportedOperationException("Set not supported");
	
}
}