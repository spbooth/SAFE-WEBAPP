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