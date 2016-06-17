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

import uk.ac.ed.epcc.webapp.model.data.Duration;



/** Class for implementing operations on objects of type java.lang.Number
 * performing automatic type promotion as required.
 * 
 * @author spb
 *
 */
public class NumberOp {
    /** Should we use double for this Number type.
     * Any Number type we do not handle explicity
     * defaults to double.
     * In particular BigInteger.
     * 
     * @param n
     * @return
     */
	private static boolean useDouble(Number n){
    	if(n instanceof Double){
    		return true;
    	}
    	if( n instanceof Float || n instanceof Long || n instanceof Integer){
    		return false;
    	}
    	return true;
    }
	private static boolean useDuration(Number n){
		return n instanceof Duration;
	}
	private static boolean useAverageValue(Number n){
		return n instanceof AverageValue;
	}
	public static AverageValue average(Number a, Number b){
		double sum=0.0;
		long count=0L;
		if( a != null ){
			if( a instanceof AverageValue){
				AverageValue v = (AverageValue) a;
				sum += v.getSum();
				count += v.getCount();
			}else{
				count++;
				sum += a.doubleValue();
			}
		}
		
		if( b != null ){
			if( b instanceof AverageValue){
				AverageValue v = (AverageValue) b;
				sum += v.getSum();
				count += v.getCount();
			}else{
				count++;
				sum += b.doubleValue();
			}
		}
		return new AverageValue(sum, count);
	}
	
	public static Number add(Number a, Number b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		if( useDuration(a) || useDuration(b)){
			// use scale of 1 as this will be the unit returned by a Duration.longValue
			return new Duration(a.longValue()+b.longValue(),1L);
		}
		if ( useDouble(a) || useDouble(b) ){
			return Double.valueOf(a.doubleValue() + b.doubleValue());
		}
		if (a instanceof Float || b instanceof Float) {
			return Float.valueOf(a.floatValue() + b.floatValue());
		}
	
		if (a instanceof Long || b instanceof Long) {
			return Long.valueOf(a.longValue() + b.longValue());
		}
		return Integer.valueOf(a.intValue() + b.intValue());
	}
	public static Number sub(Number a, Number b) {
		if (a == null) {
			// negate b
			return negate(b);
		}
		if (b == null) {
			return a;
		}
		if( useDuration(a) || useDuration(b)){
			// use scale of 1 as this will be the unit returned by a Duration.longValue
			return new Duration(a.longValue()-b.longValue(),1L);
		}
		if ( useDouble(a) || useDouble(b) ){
			return Double.valueOf(a.doubleValue() - b.doubleValue());
		}
		if (a instanceof Float || b instanceof Float) {
			return Float.valueOf(a.floatValue() - b.floatValue());
		}
		if (a instanceof Long || b instanceof Long) {
			return Long.valueOf(a.longValue() - b.longValue());
		}
		return Integer.valueOf(a.intValue() - b.intValue());
	}
	public static Number mult(Number a, Number b) {
		if (a == null || b == null) {
			return null;
		}
		
		if( useDuration(a) ){
			// use scale of 1 as this will be the unit returned by a Duration.longValue
			// scale by double as this may be fractional
			return new Duration((long)(a.longValue()*b.doubleValue()),1L);
		}
		if( useDuration(b)){
			// use scale of 1 as this will be the unit returned by a Duration.longValue
			return new Duration((long)(b.longValue()*a.doubleValue()),1L);
		}
		if ( useDouble(a) || useDouble(b) ){
			return Double.valueOf(a.doubleValue() * b.doubleValue());
		}
		if (a instanceof Float || b instanceof Float) {
			return Float.valueOf(a.floatValue() * b.floatValue());
		}
		if (a instanceof Long || b instanceof Long) {
			return Long.valueOf(a.longValue() * b.longValue());
		}
		return Integer.valueOf(a.intValue() * b.intValue());
	}
	public static Number div(Number a, Number b) {
		if (a == null || b == null) {
			return null;
		}
		
		// We return always return zero for 0/0 rather than any of the other possibilities
		// mainly this is to get a valid charge fraction for property for failed/sub jobs
		if( useDuration(a) ){
			// use scale of 1 as this will be the unit returned by a Duration.longValue
			// only consider the first arg as a duration divide by duration makes
			// little sense.
			if( a.longValue() == 0L){
				return new Duration(0L);
			}
			return new Duration((long)(a.longValue()/b.doubleValue()),1L);
		}
		if ( useDouble(a) || useDouble(b) ){
			if( a.doubleValue() == 0.0){
				return Double.valueOf(0.0);
			}
			return Double.valueOf(a.doubleValue() / b.doubleValue());
		}
		if (a instanceof Float || b instanceof Float) {
			if( a.floatValue() == 0.0f){
				return Float.valueOf(0.0f);
			}
			return Float.valueOf(a.floatValue() / b.floatValue());
		}
		if (a instanceof Long || b instanceof Long) {
			if( a.longValue() == 0L){
				return Long.valueOf(0l);
			}
			return Long.valueOf(a.longValue() / b.longValue());
		}
		if( a.intValue() == 0){
			return Integer.valueOf(0);
		}
		return Integer.valueOf(a.intValue() / b.intValue());
	}
	public static Number min(Number a, Number b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		if( a.doubleValue() < b.doubleValue()){
			return a;
		}else{
			return b;
		}
	}
	public static Number max(Number a, Number b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		if( a.doubleValue() >= b.doubleValue()){
			return a;
		}else{
			return b;
		}
	}
	public static Number negate(Number a){
		if( a == null){
			return null;
		}
		if(useDuration(a)){
			//durations are the same backwards
			return a;
		}
		if( useDouble(a)){
			return new Double(- a.doubleValue());
		}
		if( a instanceof Float){
			return new Float(-a.floatValue());
		}
		if( a instanceof Long){
			return new Long(-a.longValue());
		}
		return new Integer(-a.intValue());
	}
}