// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.content;

@uk.ac.ed.epcc.webapp.Version("$Id: HourTransform.java,v 1.5 2014/09/15 14:30:14 spb Exp $")


public class HourTransform implements NumberTransform{
	private String default_value;
	public HourTransform(String def){
		default_value = def;
	}
	public HourTransform(){
		this("0:00:00");
	}
	 public static String toHrsMinSec(Number d) {
         String result = "";
         long value = d.longValue();
         if( value < 0L){
        	 result = result+"-";
        	 value = -value;
         }
         long hours = (long) (value / 3600L);
         result = result + hours + ":";
         long minutes = (long) (value % 3600L) / 60L;
         if (minutes < 10L)
             result = result + "0";
         result = result + minutes + ":";
         long seconds = (long) (value % (60L));
         if (seconds < 10L)
             result = result + "0";
         result = result + seconds;
         return result;
     }
	  public Object convert(Object old) {
          if (old != null) {

              if (old instanceof Number) {
                  return toHrsMinSec((Number) old);
              } else {
                  return old;
              }
          } else {
              return default_value;
          }
	  }
}