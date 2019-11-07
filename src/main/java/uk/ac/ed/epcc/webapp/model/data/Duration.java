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
package uk.ac.ed.epcc.webapp.model.data;

import java.util.Date;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;

/**
 * <p>
 * A class to represent durations of time. Java does not have such a notion in a
 * simple form - the java Duration object is quite complex, allowing durations
 * in months and years that can't necessarily be compared. This is a simple
 * implementation that just wraps around a <code>long</code>.
 * 
 * Most constructors for these objects  work in seconds. 
 * Seconds were chosen primarily because the SI unit of time is a
 * second, so it seems as good a choice as any. Many durations stored in various
 * formats are accurate to at least seconds but not necessarily any more.
 * 
 * Internally the value is stored in milliseconds but can be overridden
 * {@link Duration} extends {@link Number} and the numerical value is milliseconds.
 * 
 * This is the value used when storing {@link Duration}s to the database. However by default  If you wish to persist objects in custom units a 
 * {@link DurationFieldValue} with a custom resolution must be installed in the factory. So if the 
 *  holding a number of milliseconds. 
 * This object can persist to the database (where it will be stored as a number of milliseconds) end extends
 * {@link Number} (again representing a number of milliseconds).
 * </p>
 * <p>
 * Though the numerical value is held in milliseconds the accuracy of the 
 * duration is not guaranteed to anything finer than a
 * second.
 * Currently, the implementation stores durations in milliseconds so will be
 * accurate to the millisecond. As
 * with any measurement, this object will only be as accurate as the values
 * given to it. If the accuracy of a duration is less than a second during
 * construction, it will never be more accurate.
 * </P>
 * 
 * 
 * @author jgreen4
 * 
 */


public class Duration extends Number implements Comparable<Duration> {

	/**
	 * The ratio of seconds to value stored (1:SCALE). Changing this value will
	 * change the accuracy of the object (larger numbers => greater accuracy).
	 */
	public static final long SCALE = 1000;

	/**
	 * SCALE represented as a double. DO NOT ALTER THIS VARIABLE. Alter SCALE
	 * instead
	 */
	private static final double SCALE_DOUBLE = (double) SCALE;
	/**
	 * SCALE represented as a float. DO NOT ALTER THIS VARIABLE. Alter SCALE
	 * instead
	 */
	private static final float SCALE_FLOAT = (float) SCALE;
	/**
	 * SCALE represented as an int. DO NOT ALTER THIS VARIABLE. Alter SCALE
	 * instead
	 */
	private static final int SCALE_INT = (int) SCALE;
	/**
	 * SCALE represented as a long. DO NOT ALTER THIS VARIABLE. Alter SCALE
	 * instead
	 */
	private static final long SCALE_LONG = (long) SCALE;

	private final long value;

	/**
	 * Constructs a new <code>Duration</code> based on the specified time given in
	 * seconds. Seconds is picked as the unit for the constructor because it's the
	 * SI unit for time
	 * 
	 * @param value
	 *          The duration measured in seconds.
	 */
	public Duration(Number value) {
		this(value,SCALE);
	}
	/**
	 * Constructs a new <code>Duration</code> based on the specified time in some unit.
	 * 
	 * @param value
	 *          The duration measured.
	 * @param scale The number of milliseconds per unit of the provided value
	 */
	public Duration(Number value, long scale) {
		if( value == null) {
			this.value = -1;
		} else if (value instanceof Long || value instanceof Integer){
			this.value = value.longValue() * scale;
		}else{
			this.value = (long) (value.doubleValue() * scale);
		}
	}
	/**
	 * Constructs a new <code>Duration</code> using the date provided by assuming
	 * the date is actually intended to represent a duration.
	 * <code>java.util.Date</code> measures it's time from the start of January
	 * 1st 1970 so this is assumed to be the star time of the duration. If this
	 * assumption is incorrect, it may be more appropriate to use
	 * {@link #Duration(Date, Date)} instead.
	 * 
	 * @param durationSince1970
	 *          The duration, starting at 00:00:00.000 on January first 1970 and
	 *          lasting until the date specified.
	 */
	public Duration(Date durationSince1970) {
		this.value = durationSince1970.getTime();
	}

	public Duration(Date start, Date end) {
		if( start == null || end == null ) {
			throw new ConsistencyError("Null duration bound "+start+" "+end);
		}
		this.value = end.getTime() - start.getTime();
	}

	/**
	 * Constructs a new <code>Duration</code> based on the specified time given in
	 * seconds. Seconds is picked as the unit for the constructor because it's the
	 * SI unit for time
	 * 
	 * @param value
	 *          The duration measured in seconds.
	 */
	public Duration(double value) {
		this.value = (long) (value * SCALE_DOUBLE);
	}

	/**
	 * Constructs a new <code>Duration</code> based on the specified time given in
	 * seconds. Seconds is picked as the unit for the constructor because it's the
	 * SI unit for time
	 * 
	 * @param value
	 *          The duration measured in seconds.
	 */
	public Duration(float value) {
		this.value = (long) (value * SCALE_FLOAT);
	}

	/**
	 * Constructs a new <code>Duration</code> based on the specified time given in
	 * seconds. Seconds is picked as the unit for the constructor because it's the
	 * SI unit for time
	 * 
	 * @param value
	 *          The duration measured in seconds.
	 */
	public Duration(int value) {
		this.value = (long) (value * SCALE_INT);
	}

	/**
	 * Constructs a new <code>Duration</code> based on the specified time given in
	 * seconds. Seconds is picked as the unit for the constructor because it's the
	 * SI unit for time
	 * 
	 * @param value
	 *          The duration measured in seconds.
	 */
	public Duration(long value) {
		this.value = value * SCALE_LONG;
	}
	
	/**
	 * This duration is equal to the other object if that object is a
	 * <code>Duration</code> object and lasts for exactly the same length of time
	 * as this duration
	 * 
	 * @param o
	 *          The object for comparision
	 * 
	 * @return <code>true</code> if the other object is a <code>Duration</code>
	 *         that lasts exactly as long as this duration
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Duration)
			return this.equals((Duration) o);
		else
			return false;
	}

	/**
	 * @param duration 
	 * @return boolean
	 * @see #equals(Object)
	 */
	public boolean equals(Duration duration) {
		return this.value == duration.value;
	}

	/**
	 * Converts this duration into a measurement of time in milliseconds. This
	 * method guarantees millisecond precision but not millisecond accuracy.
	 * 
	 * @return This duration measured in milliseconds as a <code>long</code>.
	 */
	public long getMilliseconds() {
		return this.value;
	}
	
	/** Get the duration in a specified unit.
	 * 
	 * @param resolution  size of unit in milliseconds.
	 * @return
	 */
	public long getTime(long resolution){
		return this.value/resolution;
	}

	/**
	 * Converts this duration into a measurement of time in seconds.
	 * 
	 * @return This duration measured in seconds as a <code>long</code>.
	 */
	public long getSeconds() {
		return this.value / SCALE_LONG;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (int) this.value;
	}

	/**
	 * Test to see if this <code>Duration</code> is less than the specified
	 * duration
	 * 
	 * @param duration
	 *          The duration for comparison
	 * @return <code>true</code> if this duration is less than the specified
	 *         duration. <code>false</code> if it is the same or longer.
	 */
	public boolean isShorterThan(Duration duration) {
		return this.value > duration.value;
	}

	/**
	 * Test to see if this <code>Duration</code> is more than the specified
	 * duration
	 * 
	 * @param duration
	 *          The duration for comparison
	 * @return <code>true</code> if this duration is more than the specified
	 *         duration. <code>false</code> if it is the same or less.
	 */
	public boolean isLongerThan(Duration duration) {
		return this.value < duration.value;
	}

	/**
	 * Test to see if this <code>Duration</code> measures a negative time
	 * interval. In other words, the duration is less than zero seconds in length
	 * 
	 * @return <code>true</code> if the length of this duration is less than 0
	 *         seconds. <code>false</code> if it is zero or positive
	 */
	public boolean isNegative() {
		return this.value < 0l;
	}

	/**
	 * Test to see if this <code>Duration</code> measures a positive time
	 * interval. In other words, the duration is less than zero seconds in length
	 * 
	 * @return <code>true</code> if the length of this duration is greater or
	 *         equal to 0 seconds. <code>false</code> if it is negative
	 */
	public boolean isPositive() {
		return this.value >= 0l;
	}

	/**
	 * Returns this duration represented as a string in seconds. If this duration
	 * is accurate to less than one second, the value will be represented a a
	 * floating point number. If not, the value will be represented as an integer
	 */
	@Override
	public String toString() {
		/*
		 * If one really felt like optimising, one could do some stylish byte
		 * shifting here
		 */

		long returnVal = this.value / SCALE_LONG;

		if (returnVal * 1000 == this.value) {
			return Long.toString(returnVal);
			
		} else {
			return Double.toString(this.value / SCALE_DOUBLE);
			
		}
	}

	/*
	 * ##########################################################################
	 * OPERATIONS
	 * ##########################################################################
	 */

	/**
	 * Returns a new <code>Duration</code> object that is equivalent to this
	 * duration and the specified duration added together. Nether this object nor
	 * <code>duration</code> will be altered by this method
	 * 
	 * @param duration
	 *          The <code>Duration</code> that will be added with this
	 *          <code>Duration</code> to form a new <code>Duration</code>.
	 * @return A new <code>Duration</code> that is the sum of this <code>Duration</code>
	 * and <code>Duration</code>
	 */
	public Duration add(Duration duration) {
		return new Duration(this.value + duration.value,1L);
	}

	/**
	 * Returns a new <code>Duration</code> object that is equivalent to this
	 * duration multiplied by the specified factor.
	 * 
	 * @param factor
	 *          The quantity to multiply the length of this duration with
	 * @return A new <code>Duration</code> that is <em>factor</em> times bigger
	 *         than this <code>Duration</code>.
	 */
	public Duration multiply(long factor) {
		return new Duration(this.value * factor,1L);
	}

	/**
	 * Returns a new <code>Duration</code> object that is equivalent to this
	 * duration multiplied by the specified factor. TThe double argument allows
	 * <code>factor</code> to be less than zero, allowing division to be
	 * indirectly performed
	 * 
	 * @param factor
	 *          The quantity to multiply the length of this duration with
	 * @return A new <code>Duration</code> that is <em>factor</em> times bigger
	 *         than this <code>Duration</code>.
	 */
	public Duration multiply(double factor) {
		return new Duration(this.value * factor,1L);
	}

	/**
	 * @return the negative form of this <code>Duration</code>.
	 */
	public Duration negate() {
		return new Duration(this.value * -1l,1L);
	}

	/**
	 * Returns a new <code>Duration</code> object that is equivalent to this
	 * <code>Duration</code> with the specified <code>Duration</code> subtracted
	 * from it. Nether this object nor <code>duration</code> will be altered by
	 * this method
	 * 
	 * @param duration
	 *          The <code>Duration</code> that will be subtracted from this
	 *          <code>Duration</code> to form a new <code>Duration</code>.
	 * @return A new <code>Duration</code> that is equivalent to this <code>Duration</code>
	 * less <code>duration</code>
	 */
	public Duration subtract(Duration duration) {
		return new Duration(this.value - duration.value,1);
	}
	
	@Override
	public double doubleValue() {
		return (double)value;
	}
	
	@Override
	public float floatValue() {
		return (float)value;
	}
	
	@Override
	public int intValue() {
		return (int)value;
	}
	
	@Override
	public long longValue() {
		return value;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Duration o) {
		long mine = longValue();
		long his = o.longValue();
		if( mine > his){
			return 1;
		}else if( mine == his ){
			return 0;
		}else{
			return -1;
		}
	}

}