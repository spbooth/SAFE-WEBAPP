// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.time;

import java.util.Date;

@uk.ac.ed.epcc.webapp.Version("$Id: RegularSplitPeriod.java,v 1.2 2014/09/15 14:30:36 spb Exp $")


public class RegularSplitPeriod extends SplitPeriod {
	private final int nsplit;
	public RegularSplitPeriod(Date start,Date end,int nsplit){
		super(makeSplits(start, end, nsplit));
		this.nsplit=nsplit;
	}
	public static long[] makeSplits(Date start, Date end, int nsplit) {
		long splits[];
		long start_time=start.getTime();
		long end_time=end.getTime();
		splits = new long[nsplit + 1];
		long step = (end_time - start_time) / nsplit;
		assert(step>0L);
		splits[0] = start_time;
		splits[nsplit] = end_time;
		for (int i = 1; i < nsplit; i++) {
			splits[i] = splits[i - 1] + step;
		}
		return splits;
	}
	public int getNsplit() {
		return nsplit;
	}
}