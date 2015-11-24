// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
@uk.ac.ed.epcc.webapp.Version("$Id: CalendarFieldInput.java,v 1.3 2015/03/11 14:58:00 spb Exp $")


public class CalendarFieldInput extends IntegerSetInput {
  private static final Integer[] field_list=new Integer[]{Calendar.SECOND,Calendar.MINUTE,Calendar.HOUR,Calendar.DAY_OF_MONTH,Calendar.WEEK_OF_YEAR,Calendar.MONTH,Calendar.YEAR};
  public CalendarFieldInput(){
	  super(field_list);
  }
  /** Constructor limiting the finest resolution field presented.
   * 
   * @param finest_field
   */
  public CalendarFieldInput(int finest_field){
	  super(getSet(finest_field));
  }
@Override
public String getText(Integer item) {
	if( item == null ){
		return "None";
	}
	switch(item.intValue()){
	case Calendar.SECOND: return "Seconds";
	case Calendar.MINUTE: return "Minutes";
	case Calendar.HOUR: return "Hours";
	case Calendar.DAY_OF_MONTH: return "Days";
	case Calendar.WEEK_OF_YEAR: return "Weeks";
	case Calendar.MONTH: return "Months";
	case Calendar.YEAR: return "Years";
	default: return "Calendar field "+item.intValue();
	}
}

@Override
public void parse(String v) throws ParseException {
	try{
		super.parse(v);
	}catch(ParseException e){
		// check text forms 
		for(Iterator<Integer> it =getItems(); it.hasNext();){
			Integer item = it.next();
			if( getText(item).equalsIgnoreCase(v)){
				setItem(item);
				return;
			}
		}
		throw e;
	}
}
private static Set<Integer> getSet(int max){
	LinkedHashSet<Integer> result = new LinkedHashSet<Integer>(field_list.length);
	for(Integer i : field_list){
		if( i <= max ){
			result.add(i);
		}
	}
	return result;
}
}