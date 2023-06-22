// Copyright - The University of Edinburgh 2015
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.model.data.Duration;

/** A Set of non overlapping time periods
 * 
 * @author spb
 *
 */
public class TimePeriods implements Iterable<TimePeriods.Period>{
	Set<Period> set;
	long min=-1L;
	long max=-1L;
    public TimePeriods(){
    	set = new TreeSet<>();
    }
	public TimePeriods(Date start_date, Date end_date) {
		this();
		add(new Period(start_date,end_date));
	}
	public TimePeriods(TimePeriod p) {
		this(p.getStart(),p.getEnd());
	}
	public void add(TimePeriods t){
		if( t == this){
			return;
		}
		for(Period p: t.set){
			add(p);
		}
	}
	
	public void subtract(TimePeriods t){
		if( t == this){
			empty();
			return;
		}
		for(Period p: t.set){
			subtract(p);
		}
	}
	public void add(final Period q){
		// make sure we don't corrupt q it might be part of
		// other sets
		Period p =   q.copy();
		if( min==-1L || p.start <min){
			min=p.start;
		}
		if( max==-1L || p.end >max){
			max=p.end;
		}
		for(Iterator<Period> it = set.iterator(); it.hasNext(); ){
			Period t = it.next();
			if(p.touches(t)){
				it.remove();
				if(t.start < p.start){
					p.start=t.start;
				}
				if(t.end > p.end){
					p.end=t.end;
				}
			}
		}
		set.add(p);
		//checkConsistancy();
	}
	public void add(TimePeriod single) {
		add(new Period(single));
	}
	public void subtract(final Period p){
		if( max != -1L && p.end >= max && p.start < max){
			max = p.start;
		}
		if( min != -1L && p.start <= min && p.end > min ){
			min = p.end;
		}
		if( min!=-1L && max!=-1L && max < min){
			empty();
			return;
		}
		//System.out.println("subtracting "+p.start+" "+p.end);
		HashSet<Period> tmp= new HashSet<>();
		for(Iterator<Period> it = set.iterator(); it.hasNext(); ){
			Period t = it.next();
			if( p.overlapps(t)){
				//System.out.println("overlap with "+t.start+" "+t.end);
				it.remove();
				if( t.start < p.start){
					//System.out.println("after start");
					if(t.end <= p.end){
						tmp.add(new Period(t.start,p.start,t.tag));
					}else{
						tmp.add(new Period(p.end,t.end,t.tag));
						tmp.add(new Period(t.start,p.start,t.tag));
					}
				}else{
					//System.out.println("before start");
					if( t.end > p.end){
						tmp.add(new Period(p.end,t.end,t.tag));
					}else{
						// T removed completely
					}
				}
			}
		}
		set.addAll(tmp);
		//checkConsistancy();
	}
	public void subtract(TimePeriod single) {
		subtract(new Period(single));
	}
	private void empty() {
		min=-1L;
		max=-1L;
		set.clear();
	}
	public boolean overlapps(TimePeriods p){
		for( Period a: set){
			for(Period b: p.set){
				if( a.overlapps(b)){
					return true;
				}
			}
		}
		return false;
	}
	
	
	public TimePeriods intersection(TimePeriods in) {
		TimePeriods result = new TimePeriods();
		for( Period p : in.set) {
			for( Period p2 : set) {
				Period i = p2.intersection(p);
				if( i != null ) {
					result.add(i);
				}
			}
		}
		return result;
	}
	public TimePeriods copy() {
		TimePeriods c = new TimePeriods();
		c.add(this);
		return c;
	}
	/** A single time period
	 * Strictly speaking the end date is not included in the period but the start date is
	 * @author spb
	 *
	 */
	public final static class Period implements TimePeriod, Comparable<TimePeriod>{
	    private long start;
		private long end;
		private String tag=null;
		public Period(TimePeriod p) {
			this(p.getStart(),p.getEnd());
		}
		public Period(Date start, Date end){
			if( start == null ){
				throw new IllegalArgumentException("Illegal constructor arguments for Period null start");
			}
			if( end == null ){
				throw new IllegalArgumentException("Illegal constructor arguments for Period null end");
			}
			if( start.after(end)){
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				throw new IllegalArgumentException("Illegal constructor arguments for Period "+df.format(start)+" "+df.format(end));
			}
			this.start=start.getTime();
			this.end=end.getTime();
		}
		public Period(Date start, Date end, String tag){
			this(start,end);
			setTag(tag);
		}
		public Period copy() {
			return new Period(start,end,tag);
		}
		private Period(long start, long end,String tag) {
			this.start=start;
			this.end=end;
			this.tag = tag;
		}
		public boolean overlapps(Period p){
			return p.start < end && p.end > start;
		}
		public boolean touches(Period p){
			return p.start <= end && p.end >= start;
		}
		public Period intersection(Period p) {
			if( ! overlapps(p)) {
				return null;
			}
			return new Period(p.start < start ? start : p.start, p.end < end ? p.end : end , null );
		}
		
		public boolean before(Period p){
			return end < p.start;
		}
		public boolean after(Period p){
			return start > p.end;
		}
		public boolean contains(Date d) {
			long point = d.getTime();
			return point >= start && point <= end;
		}
		public void setTag(String tag){
			this.tag=tag;
		}
		public String getTag(){
			return tag;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (end ^ (end >>> 32));
			result = prime * result + (int) (start ^ (start >>> 32));
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
			if (end != other.end)
				return false;
			if (start != other.start)
				return false;
			return true;
		}
		@Override
		public Date getStart() {
			return new Date(start);
		}
		@Override
		public Date getEnd() {
			return new Date(end);
		}
		@Override
		public int compareTo(TimePeriod o) {
			int c1 = getStart().compareTo(o.getStart());
			if( c1 == 0) {
				return getEnd().compareTo(o.getEnd());
			}
			return c1;
		}
		public String toString() {
			return "Period("+getStart()+","+getEnd()+(tag != null? " "+tag:"");
		}
	}
    /** total number of seconds in the set
     * 
     * @return long seconds
     */
	public long getSeconds(){
		return getMilliSeconds()/1000L;
	}
	public long getMilliSeconds() {
		long millis=0;
		for(Period p: set){
			millis += (p.end - p.start);
		}
		return millis;
	}
	public Date getMin() {
		return new Date(min);
	}
	public Date getMax() {
		return new Date(max);
	}
	public String dump(){
		StringBuilder sb = new StringBuilder();
		DateFormat df =DateFormat.getInstance();
		for(Period p: set){
			sb.append(df.format(p.start));
			sb.append("-");
			sb.append(df.format(p.end));
			if( p.tag != null){
				sb.append(" ");
				sb.append(p.tag);
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	/** return the number of distict periods in the set
	 * 
	 * @return int count of periods.
	 */
	public int getCount(){
		return set.size();
	}

	
	/** calculate the {@link Date} at which the
	 * threshold duration is exceeded in the time period.
	 * returns null if the threshold is not exceeded.
	 * 
	
	 */
	public Date getThresholdDate(Duration threshold) {
		long millis = threshold.getMilliseconds();
		long total = getMilliSeconds();
		if( millis >= total ) {
			return null;
		}
		TreeSet<Period> sorted = new TreeSet<Period>(set);
		for(Period p : sorted) {
			long milli_length = p.getEnd().getTime() - p.getStart().getTime();
			if( millis >= milli_length) {
				// not this period
				// strictly the end date is not included
				millis -= milli_length;
			}else {
				return new Date(p.getStart().getTime() + millis);
			}
			
		}
		return null;
	}
	
	public boolean isEmpty() {
		return set.isEmpty();
	}
	/** perform a consistency check of the internal state
	 * 
	 *
	 */
	public void checkConsistancy() {
		for(Period p : set){
			if( p.start< min || p.end > max){
				throw new ConsistencyError("Min/Max inconsistent with Periods");
			}
		}
		Period list[] = set.toArray(new Period[0]);
		for(int i=0;i<list.length-1;i++){
			for( int j=i+1;j<list.length;j++){
				if( list[i].touches(list[j])){
					throw new ConsistencyError("Internal periods overlap");
				}
			}
		}
	}
	@Override
	public Iterator<Period> iterator() {
		return set.iterator();
	}
	
}