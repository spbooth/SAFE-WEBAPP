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
package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.NumberOp;

/**
 * A supported binary operators on objects. usually used to define operations on {@link Table}s.
 * 
 * If both arguments are number/null
 * these mostly map onto the operations in {@link NumberOp}. The exception is the DIV
 * DIV operator that promotes to double rather than preserving the behaviour of integer types.
 *  
 * 
 * MIN and MAX
 * will use the {@link Comparable} interface if possible. Otherwise if the first
 * argument is not null it will be returned, otherwise the second.
 * 
 * 
 * @author spb
 * 
 */
public enum Operator {

	ADD {
		@Override
		public Object operate(Object a, Object b) {

			if ((a == null || a instanceof Number)
					&& (b == null || b instanceof Number)) {
				return NumberOp.add((Number) a, (Number) b);
			}

			if (a == null) {
				return b;
			}
			return a;
		}
	},
	SUB {
		@Override
		public Object operate(Object a, Object b) {
			if ((a == null || a instanceof Number)
					&& (b == null || b instanceof Number)) {

				return NumberOp.sub((Number) a, (Number) b);
			}

			if (a == null) {
				return b;
			}
			return a;

		}
	},
	MUL {
		@Override
		public Object operate(Object a, Object b) {
			if ((a == null || a instanceof Number)
					&& (b == null || b instanceof Number)) {

				return NumberOp.mult((Number) a, (Number) b);
			}

			if (a == null) {
				return b;
			}
			return a;
		}
	},
	DIV {
		@Override
		public Object operate(Object a, Object b) {
			if (a instanceof Number
					&&  b instanceof Number) {
				// A NumberOp.div would give an integer result when dividing integers
				// for general operate better to promote to double.
				return Double.valueOf(((Number)a).doubleValue()/((Number)b).doubleValue());
				//return NumberOp.div((Number) a, (Number) b);
			}

			if (a == null) {
				return b;
			}
			return a;
			
		}
	},
	MIN {
		@Override
		public Object operate(Object a, Object b) {
			if ((a == null || a instanceof Number)
					&& (b == null || b instanceof Number)) {

				return NumberOp.min((Number) a, (Number) b);
			}
			if( a instanceof Comparable && b instanceof Comparable){
				if(((Comparable)a).compareTo(b)<= 0 ){
					return a;
				}else{
					return b;
				}
			}
			if (a == null) {
				return b;
			}
			return a;

			// return Double.valueOf(a.doubleValue()/b.doubleValue());
		}
	},
	MAX {
		@Override
		public Object operate(Object a, Object b) {
			if ((a == null || a instanceof Number)
					&& (b == null || b instanceof Number)) {

				return NumberOp.max((Number) a, (Number) b);
			}
			if( a instanceof Comparable && b instanceof Comparable){
				if(((Comparable)a).compareTo(b)>= 0 ){
					return a;
				}else{
					return b;
				}
			}

			if (a == null) {
				return b;
			}
			return a;

		}
	},
	AVG {
		@Override
		public Object operate(Object a, Object b) {
			if ((a == null || a instanceof Number)
					&& (b == null || b instanceof Number)) {

				return NumberOp.average((Number) a, (Number) b);
			}

			if (a == null) {
				return b;
			}
			return a;
		}
	},
	MEDIAN {
		@Override
		public Object operate(Object a, Object b) {
			if ((a == null || a instanceof Number)
					&& (b == null || b instanceof Number)) {

				return NumberOp.median((Number) a, (Number) b);
			}

			if (a == null) {
				return b;
			}
			return a;
		}
	},
	MERGE {
		@Override
		public Object operate(Object a, Object b) {
			if (a == null) {
				return b;
			}
			return a;
		}
	};

	abstract public Object operate(Object a, Object b);
}