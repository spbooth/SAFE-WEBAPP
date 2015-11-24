// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.time;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import uk.ac.ed.epcc.webapp.CalendarField;

public abstract class SplitPeriod extends Period implements SplitTimePeriod {
	/** Divisions in milliseconds
	 * 
	 */
    private final long splits[];
    protected SplitPeriod(long splits[]){
    	super(new Date(splits[0]),new Date(splits[splits.length-1]));
    	this.splits=splits;
    }
    
	


	
	public long[] getSplits(){
		return splits;
	}
	public Iterator<TimePeriod> getSubPeriodIterator(){
		return new PeriodIterator();
	}
	public TimePeriod[] getSubPeriods(){
		TimePeriod result[] = new TimePeriod[splits.length-1];
		for(int pos=0;pos<splits.length-1;pos++){
			result[pos]=new Period(new Date(splits[pos]),new Date(splits[pos+1]));
		}
		return result;
	}
@uk.ac.ed.epcc.webapp.Version("$Id: SplitPeriod.java,v 1.7 2014/09/15 14:30:36 spb Exp $")

	public class PeriodIterator implements Iterator<TimePeriod>{
        private int pos=0;
		public boolean hasNext() {
			return pos<(splits.length-1);
		}

		public Period next() {
			Period p =  new Period(new Date(splits[pos]),new Date(splits[pos+1]));
			pos++;
			return p;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	/** infer a sensible SplitPeriod form a start and end time
	 * 
	 * @param start start Date
	 * @param end   end Date
	 * @return SplitPeriod
	 */
	public static SplitPeriod getInstance(Date start,Date end){
		Calendar start_cal = Calendar.getInstance();
		start_cal.setTime(start);
		Calendar end_cal = Calendar.getInstance();
		end_cal.setTime(end);
		
		// find first field that differs
		int field=Calendar.MILLISECOND;
		CalendarField values[]=CalendarField.values();
		for(int i=0; i< values.length;i++){
			field=values[i].getField();
			if( start_cal.get(field) != end_cal.get(field)){
				break;
			}
		}
		// check the difference is sensible
		Calendar tmp=(Calendar) start_cal.clone();
		tmp.add(field, 100);
		if( tmp.before(end_cal)){
			// too many steps
			return new RegularSplitPeriod(start, end, 12);
		}
		return getInstance(start_cal, end_cal, field);
		
	}
	public static SplitPeriod getInstance(Calendar start_cal, Calendar end_cal, int field){
		return getInstance(start_cal, end_cal, field, 12);
	}
	public static SplitPeriod getInstance(Calendar start_cal, Calendar end_cal, int field, int target){
		// count number of steps to cover period.
		int count =0;
		int block=1;
		Calendar tmp = (Calendar) start_cal.clone();
		while( tmp.before(end_cal)){
			tmp.add(field, 1);
			count++;
		}
		if( target > 0){
			// rationalise to sensible count if supplied
			while(  count > target ){
				if( count % 3 == 0 && (count/3 > target/2)){
					// block by 3 if exact
					count /=3;
					block *=3;
				}else{
					// block by 2 
					count /= 2;
					block *= 2;
				}
			}
			if( count <= 1 ){
				return new RegularSplitPeriod(start_cal.getTime(), end_cal.getTime(), target);
			}
		}
		return new CalendarFieldSplitPeriod(start_cal, field, block, count);
	}
}