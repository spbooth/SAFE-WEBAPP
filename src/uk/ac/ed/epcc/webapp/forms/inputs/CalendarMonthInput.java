// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Calendar;

@uk.ac.ed.epcc.webapp.Version("$Id: CalendarMonthInput.java,v 1.2 2014/09/15 14:30:19 spb Exp $")


public class CalendarMonthInput extends IntegerSetInput {
	static final String names[]={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	
	private final Calendar c;
  public CalendarMonthInput(){
	  super(new Integer[]{Calendar.JANUARY,Calendar.FEBRUARY,Calendar.MARCH,Calendar.APRIL,Calendar.MAY,Calendar.JUNE,Calendar.JULY,Calendar.AUGUST,Calendar.SEPTEMBER,Calendar.OCTOBER,Calendar.NOVEMBER,Calendar.DECEMBER});
	  c = Calendar.getInstance();
	  c.setTimeInMillis(0L);
  }

@Override
public String getText(Integer item) {
	c.set(Calendar.MONTH, item.intValue());
	int month = c.get(Calendar.MONTH);
	return names[month];
}
}