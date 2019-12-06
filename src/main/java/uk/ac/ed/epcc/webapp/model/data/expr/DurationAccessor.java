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
package uk.ac.ed.epcc.webapp.model.data.expr;

import java.util.Date;

import uk.ac.ed.epcc.webapp.jdbc.expr.Accessor;
import uk.ac.ed.epcc.webapp.model.data.Duration;



public class DurationAccessor<R> implements Accessor<Duration,R> {
  private final Accessor<Date,R> start, end;
  public DurationAccessor(Accessor<Date,R> start, Accessor<Date,R> end){
	  this.start=start;
	  this.end=end;
  }
@Override
public Duration getValue(R r) {

	return new Duration(start.getValue(r),end.getValue(r));
}
@Override
public Class<Duration> getTarget() {
	return Duration.class;
}
@Override
public String toString() {
	return "Duration("+start.toString()+","+end.toString()+")";
}
@Override
public boolean canSet() {
	
	return false;
}
@Override
public void setValue(R r, Duration value) {
	throw new UnsupportedOperationException("Set not supported");
	
}
}