// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp;

import java.util.Comparator;
/** Comparator for code strings.
 * compares length of code in preference to value of string.
 * 
 * @author spb
 *
 */
public class CodeComparator implements Comparator<String> {

	public int compare(String arg0, String arg1) {
		if( arg0 == null ){
			return 1;
		}
		if( arg1 == null )
		{
			return -1;
		}
		int ret = arg0.substring(0, 1).compareTo(arg1.substring(0,1));
		if( ret == 0 ){
			int l0=arg0.length();
			int l1=arg1.length();
			ret=l0-l1;
			if( ret == 0){
			  ret = arg0.compareTo(arg1);
			}
		}
		return ret;
	}

}