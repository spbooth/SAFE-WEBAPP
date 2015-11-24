// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.charts;

import java.util.Comparator;

@uk.ac.ed.epcc.webapp.Version("$Id: ListSorter.java,v 1.3 2014/09/15 14:30:12 spb Exp $")
/**
 * ListSorter sorts an array of indecies according to the values in an array
 * 
 * @author spb
 * 
 */
public class ListSorter implements Comparator<Number> {
	double data[];

	/**
	 * @param dat
	 * 
	 */
	public ListSorter(double dat[]) {
		super();
		data = dat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Number o1, Number o2) {
		int i =  o1.intValue();
		int j =  o2.intValue();
		double res = data[i] - data[j];
		if (res < 0)
			return -1;
		if (res > 0)
			return 1;
		return i - j;
	}

}