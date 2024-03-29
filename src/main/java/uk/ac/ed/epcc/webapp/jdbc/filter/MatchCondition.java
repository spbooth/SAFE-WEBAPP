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
/** match clauses for Filter comparisons
 * 
 */
package uk.ac.ed.epcc.webapp.jdbc.filter;


/** Different types of comparison operation.
 * 
 * @author spb
 *
 */
public enum MatchCondition{
	
	LT("<") {

		@Override
		public <T> boolean compare(T a, T b) {
			if( a instanceof Comparable && a.getClass().isAssignableFrom(b.getClass())){
				return ((Comparable<T>)a).compareTo(b) < 0;
			}else if( a instanceof Number && b instanceof Number){
				return ((Number)a).doubleValue() < ((Number)b).doubleValue();
			}
			return false;
		}
		
	},
	LE("<=") {

		@Override
		public <T> boolean compare(T a, T b) {
			if( a instanceof Comparable && a.getClass().isAssignableFrom(b.getClass())){
				return ((Comparable<T>)a).compareTo(b) <= 0;
			}else if( a instanceof Number && b instanceof Number){
				return ((Number)a).doubleValue() <= ((Number)b).doubleValue();
			}
			// can still check for equals
			return a.equals(b);
		}
		
	},
	GE(">=") {

		@Override
		public <T> boolean compare(T a, T b) {
			if( a instanceof Comparable && a.getClass().isAssignableFrom(b.getClass())){
				int compareTo = ((Comparable<T>)a).compareTo(b);
				return compareTo >= 0;
			}else if( a instanceof Number && b instanceof Number){
				return ((Number)a).doubleValue() >= ((Number)b).doubleValue();
			}
			// can still check for equals
			return a.equals(b);
		}
		
	},
	GT(">") {
		@Override
		public <T> boolean compare(T a, T b) {
			if( a instanceof Comparable && a.getClass().isAssignableFrom(b.getClass())){
				return ((Comparable<T>)a).compareTo(b) > 0;
			}else if( a instanceof Number && b instanceof Number){
				return ((Number)a).doubleValue() > ((Number)b).doubleValue();
			}
			return false;
		}
	},
	NE("!=") {
		@Override
		public <T> boolean compare(T a, T b) {
			if( a instanceof Number && b instanceof Number){
				return ((Number)a).doubleValue() != ((Number)b).doubleValue();
			}
			return ! a.equals(b);
		}
	};
	private final String pattern;

	MatchCondition(String pattern){
		this.pattern=pattern;
	}
	/** Get the SQL comparison operation.
	 * 
	 * @return SQL match operator
	 */
	public String match(){
		return pattern;
	}
	/** compare two values using the {@link MatchCondition}.
	 * Some tests only make sense if the targets implement {@link Comparable}.
	 * 
	 * @param a
	 * @param b
	 * @return boolean
	 */
	public abstract <T>  boolean compare(T a, T b);
	
	public static String getRegexp(){
		   StringBuilder sb = new StringBuilder();
		   for( MatchCondition k : values()){
			   if( sb.length() > 0){
				   sb.append("|");
			   }
			   sb.append("(?:");
			   sb.append(k.match());
			   sb.append(")");
		   }
		   return sb.toString();
	   }
}