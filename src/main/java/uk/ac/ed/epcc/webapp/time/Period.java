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
package uk.ac.ed.epcc.webapp.time;

import java.util.Date;

/** A period defines a period of time between two dates
 * Strictly speaking the Period only includes the end date so if periods form a sequence then 
 * the start of the following date will match the end of the previous date.
 * This convention is chosen to because in accounting systems the end-date is often more canonical than
 * the start date.
 * @author spb
 *
 */


public class Period implements TimePeriod {
  protected final Date start;
  protected final Date end; 
  public Date getStart(){
	  return start;
  }
  public Date getEnd(){
	  return end;
  }
  public Period(Date start,Date end){
	  assert(! end.before(start));
	  if( end.before(start)){
		  throw new IllegalArgumentException("Start and End not in order "+start.toString()+" "+end.toString());
	  }
	  this.start=start;
	  this.end=end;
  }
 
  public Period(TimePeriod period){
	  this(period.getStart(),period.getEnd());
  }
  public boolean contains(Date d){
	  return d.after(start) && ! d.after(end);
  }
  @Override
  public String toString(){
	  return "Period("+start.toString()+","+end.toString()+")";
  }
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((end == null) ? 0 : end.hashCode());
	result = prime * result + ((start == null) ? 0 : start.hashCode());
	return result;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	Period other = (Period) obj;
	if (end == null) {
		if (other.end != null)
			return false;
	} else if (!end.equals(other.end))
		return false;
	if (start == null) {
		if (other.start != null)
			return false;
	} else if (!start.equals(other.start))
		return false;
	return true;
}

}