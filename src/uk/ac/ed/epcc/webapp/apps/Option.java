// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.apps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Represents a command line option. See {@link Options} to see how options are
 * processed. This class essentially holds information about each option. The
 * inner class {@link Option.Instance} holds a specific instance of an option on
 * the command line. As such, it holds the value or values an option has once it
 * has been set on the command line.
 * </p>
 * <p>
 * All setter methods return the object that was used to call them. This allows
 * setter methods to be chained together on one line. For example: <blockquote>
 * myOpt.setArgNum(1).setRequired(true).setValueSeparator('+'); </blockquote>
 * </p>
 * <p>
 * Options have either a long name, a short name or both. The long name must be
 * at least two characters long. The short name must be only one character long
 * although this class enforces this by only allowing the short name to be set
 * using a <code>char</code>. Both names can contain only legal characters. The
 * legal characters are specified by the {@link #validateName} method
 * </p>
 * 
 * @author jgreen4
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: Option.java,v 1.5 2014/09/15 14:30:11 spb Exp $")

public class Option implements Serializable {
	/**
	 * Used to indicate a character argument (such as short name or value
	 * separator) has no value
	 */
	public static final char UNSET = '\u0000';
	/**
	 * Used to indicate the option takes an unlimited number of arguments
	 * (strictly speaking, the upper limit is set by Integer.MAX_VALUE)
	 */
	public static final int MULTIPLE_ARGUMENTS = Integer.MAX_VALUE;
	/**
	 * The option's short name
	 */
	private String shortName;
	/**
	 * The option's long name
	 */
	private String longName;
	/**
	 * A short description of the purpose of the option
	 */
	private String description;
	/**
	 * Indicates whether or not the option must be present on the command line
	 */
	private boolean required;
	/**
	 * The number of values the option may take. Options that act as flags take no
	 * arguments
	 */
	private int valNum = 0;
	/**
	 * If an option takes multiple values, the values may be specified as one
	 * argument with a value separator separating the values in the argument.
	 */
	private char valueSeparator = ',';

	/**
	 * Constructs a new <code>Option</code> with the specified short name and
	 * description. The option will have no long name and take no arguments.
	 * @param container
	 *          An <code>Options</code> object that this object will be added to
	 *          after construction. Can be <code>null</code>, in which case this
	 *          option won't be added to a <code>Options</code> object after
	 *          construction 
	 * @param shortName
	 *          The short name of the option, or Option.UNSET if this option
	 *          should not have a short name
	 * @param description
	 *          A brief (one line) description of what the option does
	 * @exception IllegalArgumentException
	 *              if <code>shortName</code> is an illegal character.
	 * @exception NullPointerException
	 *              if <code>shortName</code> is the {@link NullPointerException}
	 *              character or Option.UNSET
	 * @see #validateName
	 */
	public Option(Options container, char shortName, String description)
			throws IllegalArgumentException, NullPointerException {
		this(container, shortName, null, false, description);
	}

	/**
	 * Constructs a new <code>Option</code> with the specified short name and
	 * description. The option will have no long name and take one argument if
	 * <code>hasArg</code> is <code>true</code> or nor arguments if it is
	 * <code>false</code>. To allow the option to have more than one argument, use
	 * one of the setter. methods after construction.
	 * @param container
	 *          An <code>Options</code> object that this object will be added to
	 *          after construction. Can be <code>null</code>, in which case this
	 *          option won't be added to a <code>Options</code> object after
	 *          construction 
	 * @param shortName
	 *          The short name of the option
	 * @param hasArg
	 *          Whether or not the option takes a value.
	 * @param description
	 *          A brief (one line) description of what the option does
	 * @exception IllegalArgumentException
	 *              if <code>shortName</code> is an illegal character.
	 * @exception NullPointerException
	 *              if <code>shortName</code> is the <code>null</code> character
	 *              or Option.UNSET
	 * @see #validateName
	 */
	public Option(Options container, char shortName, boolean hasArg,
			String description) throws IllegalArgumentException {
		this(container, shortName, null, hasArg, description);
	}

	/**
	 * Constructs a new <code>Option</code> with the specified long name and
	 * description. The option will have no short name and take no arguments.
	 * @param container
	 *          An <code>Options</code> object that this object will be added to
	 *          after construction. Can be <code>null</code>, in which case this
	 *          option won't be added to a <code>Options</code> object after
	 *          construction
	 * @param longName
	 *          The long name of the option.
	 * @param description
	 *          A brief (one line) description of what the option does.
	 * @exception IllegalArgumentException
	 *              if <code>longName</code> contains illegal characters or is
	 *              less than two characters long.
	 * @exception NullPointerException
	 *              if <code>longName</code> is the <code>null</code>.
	 * @see #validateName
	 */
	public Option(Options container, String longName, String description)
			throws IllegalArgumentException, NullPointerException {
		this(container, UNSET, longName, false, description);
	}

	/**
	 * Constructs a new <code>Option</code> with the specified long name name and
	 * description. The option will have no short name and take one argument if
	 * <code>hasArg</code> is <code>true</code> or nor arguments if it is
	 * <code>false</code>. To allow the option to have more than one argument, use
	 * one of the setter methods after construction.
	 * 
	 * @param container
	 *          An <code>Options</code> object that this object will be added to
	 *          after construction. Can be <code>null</code>, in which case this
	 *          option won't be added to a <code>Options</code> object after
	 *          construction
	 * @param longName
	 *          The long name of the option.
	 * @param hasArg
	 *          Whether or not the option takes a value.
	 * @param description
	 *          A brief (one line) description of what the option does.
	 * @exception IllegalArgumentException
	 *              if <code>longName</code> contains illegal characters or is
	 *              less than two characters long.
	 * @exception NullPointerException
	 *              if <code>longName</code> is the <code>null</code>.
	 * @see #validateName
	 */
	public Option(Options container, String longName, boolean hasArg,
			String description) throws IllegalArgumentException {
		this(container, UNSET, longName, hasArg, description);
	}

	/**
	 * Constructs a new <code>Option</code> with the specified short name, long
	 * name and description. The option will take no arguments.
	 * 
	 * @param container
	 *          An <code>Options</code> object that this object will be added to
	 *          after construction. Can be <code>null</code>, in which case this
	 *          option won't be added to a <code>Options</code> object after
	 *          construction
	 * @param shortName
	 *          The short name of the option, or Option.UNSET if this option
	 *          should not have a short name. In this case, <code>longName</code>
	 *          cannot be <code>null</code>.
	 * @param longName
	 *          The long name of the option (can be <code>null</code> so long as
	 *          short name is specified).
	 * @param description
	 *          A brief (one line) description of what the option does.
	 * @exception IllegalArgumentException
	 *              if <code>shortName</code> or <code>longName</code> contain
	 *              illegal characters, or if long name is less than two
	 *              characters long.
	 * @exception NullPointerException
	 *              if <code>longName</code> is the <code>null</code> or if
	 *              <code>shortName</code> is the <code>null</code> character or
	 *              Option.UNSET
	 * @see #validateName
	 */
	public Option(Options container, char shortName, String longName,
			String description) throws IllegalArgumentException {
		this(container, shortName, longName, false, description);
	}

	/**
	 * Constructs a new <code>Option</code> with the specified short name, long
	 * name name and description. The option will take one argument if
	 * <code>hasArg</code> is <code>true</code> or nor arguments if it is
	 * <code>false</code>. To allow the option to have more than one argument, use
	 * one of the setter methods after construction.
	 * 
	 * @param container
	 *          An <code>Options</code> object that this object will be added to
	 *          after construction. Can be <code>null</code>, in which case this
	 *          option won't be added to a <code>Options</code> object after
	 *          construction
	 * @param shortName
	 *          The short name of the option, or Option.UNSET if this option
	 *          should not have a short name In this case, <code>longName</code>
	 *          cannot be <code>null</code>
	 * @param longName
	 *          The long name of the option (can be <code>null</code> so long as
	 *          short name is specified)
	 * @param hasArg
	 *          Whether or not the option takes a value.
	 * @param description
	 *          A brief (one line) description of what the option does
	 * @exception IllegalArgumentException
	 *              if <code>shortName</code> or <code>longName</code> contain
	 *              illegal characters, or if long name is less than two
	 *              characters long.
	 * @exception NullPointerException
	 *              if <code>longName</code> is the <code>null</code> or if
	 *              <code>shortName</code> is the <code>null</code> character or
	 *              Option.UNSET
	 * @see #validateName
	 */
	public Option(Options container, char shortName, String longName,
			boolean hasArg, String description) throws IllegalArgumentException,
			NullPointerException {

		String shortNameString;
		if (shortName == UNSET)
			shortNameString = null;
		else
			shortNameString = Character.toString(shortName);

		if (shortNameString == null) {
			if (longName == null) {
				throw new NullPointerException(
						"one of longName or shortName must be non-null");
			}
		} else {
			validateName(shortNameString);
		}

		if (longName != null) {
			if (longName.length() < 2) {
				throw new IllegalArgumentException(
						"Long names for options must be at least two characters long");
			} else {
				validateName(longName);
			}
		}

		this.shortName = shortNameString;
		this.longName = longName;

		if (hasArg)
			this.valNum = 1;
		else
			this.valNum = 0;

		this.description = description == null ? "" : description;

		if (container != null)
			container.addOption(this);
	}

	/**
	 * Disables the use of a value separator. No attempt will be made to break an
	 * argument of a multi-valued option into many values
	 * 
	 * @return This <code>Option</code>
	 */
	public Option disableValueSeparator() {
		this.valueSeparator = UNSET;
		return this;
	}

	/**
	 * @return a brief description of this option.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Returns the long name for this option if it is set. If it isn't, returns
	 * the short name. This method is useful if one needs a single name or
	 * identifier to denote this option.
	 * 
	 * @return An appropriate name for this option.
	 */
	public String getId() {
		if (this.longName == null)
			return this.shortName;
		else
			return this.longName;
	}

	/**
	 * @return This option's long name.
	 */
	public String getLongName() {
		return this.longName;
	}

	/**
	 * Returns the maximum number of values this option can take. If there is no
	 * limit on the number of values, {@link Option#MULTIPLE_ARGUMENTS} will be
	 * returned.
	 * 
	 * @return The maximum number of arguments this option can take.
	 */
	public int getNumVals() {
		return valNum;
	}

	/**
	 * @return This option's short name.
	 */
	public String getShortName() {
		return this.shortName;
	}

	/**
	 * Returns the character used to denote a separation in values for this option
	 * in an argument. If this option does not have a value separator,
	 * {@link Option#UNSET} is returned. Even if this option only takes one value
	 * or no values at all, a value separator may still be set. However, in these
	 * cases it will never be used.
	 * 
	 * @return The value separator used in arguments.
	 */
	public char getValueSeparator() {
		return valueSeparator;
	}

	/**
	 * @return <code>true</code> if this option takes one or more values.
	 *         <code>false</code> otherwise.
	 */
	public boolean hasValue() {
		return valNum > 0;
	}

	/**
	 * @return <code>true</code> if this option takes more than one value.
	 *         <code>false</code> otherwise.
	 */
	public boolean hasValues() {
		return this.valNum > 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}

	/**
	 * @return <code>true</code> if this option has a long name.
	 *         <code>false</code> otherwise.
	 */
	public boolean hasLongName() {
		return this.longName != null;
	}

	/**
	 * @return <code>true</code> if this option has a short name.
	 *         <code>false</code> otherwise.
	 */
	public boolean hasShortName() {
		return this.shortName != null;
	}

	/**
	 * Returns whether or not this option has a value separator. Even if this
	 * option only takes one value or no values at all, a value separator may
	 * still be set. However, in these cases it will never be used.
	 * 
	 * @return <code>true</code> if this option has a value separator.
	 *         <code>false</code> otherwise.
	 */
	public boolean hasValueSeparator() {
		return this.valueSeparator != UNSET;
	}

	/**
	 * @return <code>true</code> if this option must be set on the command line.
	 *         <code>false</code> otherwise
	 */
	public boolean isRequired() {
		return this.required;
	}

	/**
	 * Generates a new instance of this option. The instance can be populated with
	 * values, however it will conform to the constraints applied by this option.
	 * 
	 * @return An instance of this option
	 * @see Option.Instance
	 */
	public Option.Instance newInstance() {
		return new Option.Instance();
	}

	/**
	 * Sets the number of values this option can take. If this option can take
	 * many options, use {@link Option#MULTIPLE_ARGUMENTS} as the value.
	 * 
	 * @param num
	 *          The number of values this option can take
	 * @return This <code>Option</code>
	 * @throws IllegalArgumentException
	 *           if <code>num</code> is less than zero
	 */
	public Option setNumValues(int num) throws IllegalArgumentException {
		if (num < 0)
			throw new IllegalArgumentException(
					"Number of values cannot be less than zero.  Value received = " + num);

		this.valNum = num;
		return this;
	}

	/**
	 * Sets a short description for this option
	 * 
	 * @param description
	 *          The description
	 * @return This <code>Option</code>
	 */
	public Option setDescription(String description) {
		if (description == null)
			this.description = "";
		else
			this.description = description;
		return this;
	}

	/**
	 * Allows this option to have multiple arguments. There is no limit set on the
	 * maximum number of arguments, although technically, there cannot be more
	 * than the maximum value stored in an <code>int</code>
	 * 
	 * @return This <code>Option</code>
	 */
	public Option setMultipleArgs() {
		return this.setNumValues(Integer.MAX_VALUE);
	}

	/**
	 * Sets whether or not this option must be set on the command line
	 * 
	 * @param required
	 *          Whether or not this option must be set on the command line
	 * @return This <code>Option</code>
	 */
	public Option setRequired(boolean required) {
		this.required = required;
		return this;
	}

	/**
	 * Sets the value separator that separates values in an argument assigned to
	 * this option. The character used is not checked in any way so technically,
	 * any character could be used. Obviously, characters such as the line feed
	 * character or system bell character are not the best choices for value
	 * separator, however this object allows them to be used as value separators.
	 * 
	 * @param sep
	 *          the value separator to use when assigning an argument to this
	 *          option
	 * @return This <code>Option</code>
	 */
	public Option setValueSeparator(char sep) {
		this.valueSeparator = sep;
		return this;
	}

	/**
	 * Two options are considered equal if they have the same long name and short
	 * name
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o instanceof Option == false)
			return false;

		Option opt = ((Option) o);

		return opt.getShortName().equals(this.getShortName())
				&& opt.getLongName().equals(this.getLongName());
	}

	/**
	 * Returns one of the names of this option.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getId();
	}

	/*
	 * ##########################################################################
	 * STATIC METHODS
	 * ##########################################################################
	 */

	/**
	 * Checks a name to make sure it contains legal name characters. Legal name
	 * characters are all characters that are legal as part of a Java identifier,
	 * plus the characters '?', '@' and '-'.
	 * 
	 * @param name
	 *          The name to check
	 * @throws IllegalArgumentException
	 *           If the name contains an illegal character
	 * @see Character#isJavaIdentifierPart
	 */
	static void validateName(String name) throws IllegalArgumentException {
		char[] chars = name.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (!(Character.isJavaIdentifierPart(c) || c == '?' || c == '@' || c == '-')) {
				String errMessage;
				if (name.length() == 1)
					errMessage = "illegal name: ";
				else
					errMessage = "name contains illegal character: ";

				errMessage += "'" + c + "'";
				throw new IllegalArgumentException(errMessage);
			}
		}
	}

	/*
	 * ##########################################################################
	 * INNER CLASSES
	 * ##########################################################################
	 */

	/**
	 * An <code>Instance</code> of an <code>Option</code> contains values assigned
	 * to an option. Objects of this class make sure they adhere to constraints
	 * applied to their related <code>Option</code>.
	 */
	public class Instance {
		/**
		 * The values this option takes
		 */
		private List<String> values = new ArrayList<String>();

		/**
		 * Adds a value to this option instance. If the option's value separator is
		 * set and this option takes multiple argument, the argument will be
		 * separated into one or more values using the value separator.
		 * 
		 * @param argument
		 *          The value or values to assign to this option instance
		 * @return This option instance
		 * @throws IllegalArgumentException
		 *           If the value cannot be added to the option because the option
		 *           cannot take more values or because the option doesn't take
		 *           values at all
		 */
		public Option.Instance addValue(String argument)
				throws IllegalArgumentException {
			// this Option has a separator character
			if (hasValues() && hasValueSeparator()) {
				String separator = Character.toString(getValueSeparator());

				String[] values = argument.split(separator);
				for (String val : values)
					this.add(val);
			} else {
				this.add(argument);
			}

			return this;
		}

		/**
		 * @return The identity of the option this object is an instance of
		 * @see Option#getId()
		 */
		public String getName() {
			return getId();
		}

		/**
		 * Returns the value of an option if it has one, or null if none have been
		 * set. If this option takes multiple values, the first value is returned.
		 * 
		 * @return This option's value or <code>null</code> if none was set or this
		 *         option does not take values
		 */
		public String getValue() {
			if (this.hasNoValues())
				return null;
			else
				return values.get(0);
		}

		/**
		 * Returns the <em>n</em>th value of an option where <em>n</em> is set by
		 * the variable <code>index</code>. If this option has no values, null is
		 * returned. The first value is at <code>index=0</code>
		 * 
		 * 
		 * @param index
		 *          The number of the value to return
		 * @return This option's value or <code>null</code> if this option does not
		 *         take values
		 * @throws IndexOutOfBoundsException
		 *           If this option instance takes values but there is as many as
		 *           <code>index</code> of them.
		 */
		public String getValue(int index) throws IndexOutOfBoundsException {
			if (hasValue() == false)
				return null;
			else
				return values.get(index);
		}

		/**
		 * @return All values this option instance has, or <code>null</code> if this
		 *         option instance does not take values
		 */
		public List<String> getValues() {
			if (hasValue() == false)
				return null;
			else
				return Collections.unmodifiableList(this.values);
		}

		/**
		 * @return <code>true</code> if no values have been set in this option
		 *         instance. <code>false</code> otherwise.
		 */
		public boolean hasNoValues() {
			return this.values.isEmpty();
		}

		/*
		 * ########################################################################
		 * PRIVATE METHODS
		 * ########################################################################
		 */

		private void add(String value) throws IllegalArgumentException {
			if ((valNum > 0) && (values.size() > (valNum - 1))) {
				String errorMessage = "Cannot add value " + value + " to option '"
						+ getId() + "' ";
				if (hasValues() == false)
					errorMessage += "does not take arguments";
				else if (getNumVals() == 1)
					errorMessage += "can only takes 1 argument";
				else
					errorMessage += "can only takes " + getNumVals() + " arguments";
				throw new IllegalArgumentException(errorMessage);
			}

			values.add(value);
		}

	}
}