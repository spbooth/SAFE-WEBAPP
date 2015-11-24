// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.time;

import java.util.Calendar;
@uk.ac.ed.epcc.webapp.Version("$Id: CalendarFieldSplitPeriod.java,v 1.6 2014/09/15 14:30:36 spb Exp $")



public class CalendarFieldSplitPeriod extends SplitPeriod {
	private final int field;
	private final int count;
	private final int nsplit;
	private final Calendar cal;
	public CalendarFieldSplitPeriod(Calendar start, int field, int count, int nsplit) {
		super(makeSplits(start,field,count,nsplit));
		this.cal=start;
		this.field=field;
		this.count=count;
		this.nsplit=nsplit;
	}

	/**
	 * Instantiates a new split periodwhich start form the 'start' time, 
	 * and has 'count' splits of 'field' type. 
	 * 
	 * @param start the start time
	 * @param field the field of the time interval
	 * @param count the count the number of intervals
	 * @param nsplit 
	 * @return array of splits
	 */
	public static long[] makeSplits(Calendar start, int field, int count, int nsplit) {
		long splits[];
		if( nsplit < 1 ){
			nsplit=1;
		}
		if( count < 1){
			count=1;
		}
		
			Calendar tmp = Calendar.getInstance();
			splits = new long[nsplit + 1];
			tmp.setTime(start.getTime());
			for (int i = 0; i < nsplit + 1; i++) {
				splits[i] = tmp.getTime().getTime();
				tmp.add(field, count);
			}

		return splits;
	}

	public int getField() {
		return field;
	}

	public int getCount() {
		return count;
	}

	public int getNsplit() {
		return nsplit;
	}
	public Calendar getCalStart(){
		return cal;
	}
}