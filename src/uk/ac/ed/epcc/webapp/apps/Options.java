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
package uk.ac.ed.epcc.webapp.apps;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * <p>
 * Stores a set of options and provides methods to parse command line arguments
 * and convert them into option instances based on the options in this object.
 * Like {@link Option}, <code>Options</code> stores everything a set of options
 * could be, and {@link Options.Instance} stores the set of option instances
 * from an actual parse of a command line. The {@link Options.Instance} object's
 * behaviour is determined by the constraints placed by it's related
 * {@link Options} object and the {@link Option}s it contains. See
 * {@link Options.Instance#parse} for a complete description on how arguments
 * are parsed into options. If an object has a long name, it must be different
 * to the long names of all other options in this object. The same is true for
 * short names.
 * </p>
 * 
 * @author jgreen4
 * 
 */

public class Options {

	
	/**
	 * All options stored by their id. This map will have set of values (ie no two
	 * options in the values set will be the same
	 */
	private Map<String, Option> optionsById = new HashMap<String, Option>();
	/**
	 * All options stored by their name. Since options can have more than one
	 * name, the values set may contain the same option more than once.
	 */
	private Map<String, Option> optionsByName = new HashMap<String, Option>();

	/**
	 * Adds an option to this object
	 * 
	 * @param opt
	 *          The option to add
	 * @return This <code>Options</code> object
	 * @throws IllegalArgumentException
	 *           If this objct already contains an option with the same long or
	 *           short name as <code>opt</code>
	 */
	public Options addOption(Option opt) throws IllegalArgumentException {

		// Make sure an option with the same long name is not already present
		if (opt.hasLongName() && this.optionsByName.containsKey(opt.getLongName()))
			throw new IllegalArgumentException("Cannot add option '"
					+ opt.getLongName()
					+ "' - an option with this name has already been added");

		// Make sure an option with the same short name is not already present
		if (opt.hasShortName()
				&& this.optionsByName.containsKey(opt.getShortName()))
			throw new IllegalArgumentException("Cannot add option '"
					+ opt.getShortName()
					+ "' - an option with this name has already been added");

		// Add the option to the relevant lists
		if (opt.hasLongName())
			this.optionsByName.put(opt.getLongName(), opt);
		if (opt.hasShortName())
			this.optionsByName.put(opt.getShortName(), opt);

		this.optionsById.put(opt.getId(), opt);

		return this;
	}

	/**
	 * Returns an unmodifiable collection of all the options in this object. There
	 * will be no duplicates
	 * 
	 * @return All the options in this object
	 */
	public Collection<Option> getOptions() {
		return this.optionsById.values();
	}

	/**
	 * Returns the specified option, or <code>null</code> if this object does not
	 * contain an option by this name. The name is expected to have one or two
	 * hyphens at the beginning. These will be stripped. This method will work if
	 * the hyphens are not present so long as the name itself doesn't start with
	 * hyphens. In that case, the hyphens will be erroneously stripped.
	 * 
	 * @param argName
	 *          The name of the argument to return. The name is expected to start
	 *          with hyphens
	 * @return The option with the specified name
	 */
	public Option getOption(String argName) {
		if (argName == null)
			return null;

		argName = stripHyphens(argName);
		return this.optionsByName.get(argName);
	}

	/**
	 * Tests to see if the specified option is present. The name is expected to
	 * have one or two hyphens at the beginning. These will be stripped. This
	 * method will work if the hyphens are not present so long as the name itself
	 * doesn't start with hyphens. In that case, the hyphens will be erroneously
	 * stripped.
	 * 
	 * @param argName
	 *          The name of the argument to return. The name is expected to start
	 *          with hyphens
	 * @return The option with the specified name
	 */
	public boolean hasOption(String argName) {
		argName = stripHyphens(argName);

		return this.optionsByName.containsKey(argName);
	}
	/**
	 * Returns a new instance of this options set that can store option instances
	 * from a parsed command line
	 * 
	 * @return An instance holding version of this options set.
	 */
	public Options.Instance newInstance() {
		return new Options.Instance();
	}

	/**
	 * Returns a new instance of this options set containing the option instances
	 * defined in the arguments passed in. Parsing will occur using the
	 * constraints applied by this objects
	 * 
	 * @param args
	 *          the command line arguments to parse
	 * @return An instance holding version of this options set containing the
	 *         parsed arguments
	 */
	public Options.Instance newInstance(String args[]) {
		Options.Instance instance = new Options.Instance();
		instance.parse(args);
		return instance;
	}

	/**
	 * @return A list of all options supported by this object, formatted in a
	 *         command line printable format with desctiptions of the options
	 *         included
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final String NEW_LINE = System.getProperty("line.separator");
		StringBuilder buf = new StringBuilder();

		for (Option opt : this.optionsById.values()) {
			boolean hadShortName = false;

			buf.append("  ");
			if (opt.hasShortName()) {
				buf.append("-").append(opt.getShortName());
				hadShortName = true;
			}

			if (opt.hasLongName()) {
				if (hadShortName)
					buf.append(", ");

				buf.append("--").append(opt.getLongName());
			}

			buf.append(NEW_LINE).append("\t").append(opt.getDescription());
			buf.append(NEW_LINE).append(NEW_LINE);
		}

		return buf.toString();
	}

	/*
	 * ##########################################################################
	 * PRIVATE METHODS
	 * ##########################################################################
	 */

	
	/**
	 * Convenience method for stripping leading hyphens off a string. At most only
	 * two hyphens are stripped off. Therefore, if the string starts with
	 * <em>n</em> hyphens, <em>n-2</em> hyphens will be present at the beginning
	 * of the returned string (or 0 if only one hyphen was present at the
	 * beginning of <code>s</code>
	 * 
	 * @param s
	 *          The string to strip hyphens from
	 * @return <code>s</code> with one/two hyphens stripped of if <code>s</code>
	 *         began with one/two hyphens
	 */
	private static String stripHyphens(String s) {
		if (s.startsWith("--"))
			return s.substring(2);
		else if (s.startsWith("-"))
			return s.substring(1);
		else
			return s;
	}

	/*
	 * ##########################################################################
	 * INNER CLASSES
	 * ##########################################################################
	 */

	/**
	 * An instance of an <code>Options</code> object contains all options found in
	 * arguments on a command line. The choice of options is taken from the set of
	 * options <code>Options</code> presents. All constraints applied by
	 * <code>Options</code> and the <code>Option</code>s it contains are used when
	 * extracting options from command line arguments
	 */
	public class Instance {
		private Map<Option, Option.Instance> options;

		/**
		 * Constructs a new empty <code>Instance</code> of the related
		 * <code>Options</code> object.
		 * 
		 */
		private Instance() {
			this.options = new HashMap<Option, Option.Instance>();
		}

		/**
		 * Tests to see if the specified option has an instance in this object
		 * 
		 * @param opt
		 *          The option to check for
		 * @return <code>true</code> if there is an instance of the specified option
		 *         in this object. <code>false</code> otherwise.
		 */
		public boolean containsOption(Option opt) {
			return this.options.containsKey(opt);
		}

		/**
		 * Tests to see if the specified option has an instance in this object. The
		 * option may be specified by long name or short name
		 * 
		 * @param name
		 *          The long or short name of the option to look for
		 * @return <code>true</code> if there is an instance of the specified option
		 *         in this object. <code>false</code> otherwise.
		 */
		public boolean containsOption(String name) {
			Option opt = optionsByName.get(name);
			if (opt == null)
				return false;
			else
				return this.options.containsKey(opt);
		}

		/**
		 * Returns the instance of the specified option that is stored in this
		 * <code>Options</code> instance, or <code>null</code> if there is no
		 * instance of the specified option here.
		 * 
		 * @param opt
		 *          The option to check for
		 * @return The instance of <code>opt</code>, or <code>null</code> if
		 *         <code>opt</code> doesn't have an instance stored here.
		 */
		public Option.Instance getOption(Option opt) {
			return this.options.get(opt);
		}

		/**
		 * Returns the instance of the specified option that is stored in this
		 * <code>Options</code> instance, or <code>null</code> if there is no
		 * instance of the specified option here. The option may be specified by
		 * long name or short name
		 * 
		 * @param name
		 *          The long or short name of the option to look for
		 * @return The instance of <code>opt</code>, or <code>null</code> if
		 *         <code>opt</code> doesn't have an instance stored here.
		 */
		public Option.Instance getOption(String name) {
			Option opt = optionsByName.get(name);
			if (opt == null)
				return null;
			else
				return this.options.get(opt);
		}

		/**
		 * <p>
		 * Parses a set of arguments, extracts options from them and stores them in
		 * this instance. All arguments that are not options are returned in the
		 * order they were discovered. Options are identified by the following
		 * properties:
		 * </p>
		 * 
		 * <ul>
		 * <li>An argument beginning with <em>--</em> (double hyphen) is processed
		 * as an option specified by it's long name.</li>
		 * <li>An argument beginning with <em>-</em> (hyphen) is processed as an
		 * option or several options specified by it's/their short name.</li>
		 * <li>The arguments <em>-</em> and <em>--</em> are not treated as options.
		 * They are treated as non-option arguments.</li>
		 * </ul>
		 * 
		 * <p>
		 * Once an option is identified, it is processed in the following way:
		 * </p>
		 * <ul>
		 * <li>If the argument holds an option's long name:</li>
		 * <ul>
		 * <li>If it a value separator, it's long name is taken to be the argument
		 * string up to but not including the value separator. It's value is the
		 * argument string from the first character after the value separator to the
		 * end of the argument</li>
		 * <li>If the argument doesn't contain a value separator, the entire
		 * argument is taken to be the option's long name</li>
		 * </ul>
		 * <li>If the argument holds and option's short name:</li>
		 * <ul>
		 * <li>If it contains contains a value separator, it's short name is taken
		 * to be first non hyphen character in the argument.</li>
		 * <ul>
		 * <li>If the second character is a value separator, the value of the option
		 * is the rest of the argument after the value separator.</li>
		 * <li>If the value separator occurs later in the argument, the value of the
		 * option is taken to be the argument string from immediately after the
		 * second character to the end of the argument. Using this mechanism,
		 * property key-value pairs can be set.</li>
		 * </ul>
		 * <li>If there is no value separator, each character of the argument after
		 * the first hyphen is taken as the short name of an option</li> <li>
		 * If the option takes an argument and the argument but no value separator
		 * was present, the next argument is taken to be the value.</li> </ul>
		 * </ul></ul>
		 * <p>
		 * <b>Example 1</b>: Below are options, <em>a</em>, <em>b</em> and
		 * <em>c</em>. <em>a</em> and <em>c</em> take arguments. <em>b</em> does
		 * not. <blockquote> -abc arg1 arg2 arg3 arg4</blockquote> This produces the
		 * following result:
		 * </p>
		 * <blockquote> a = <em>arg1</em><br/>
		 * b (no value)<br/>
		 * c = <em>art2</em> </br>remaining arguments = arg3 and arg4</blockquote>
		 * <p>
		 * <b>Example 2</b>: Below are options, <em>P</em>, <em>assign-arg</em> and
		 * <em>flag</em>. All take arguments except <em>flag</em>. <blockquote>
		 * -Pprop=value --assign-arg=val arg1 --flag arg 2 </blockquote> This
		 * produces the following result:
		 * </p>
		 * <blockquote> P = <em>prop=value</em><br/>
		 * assign-arg = <em>val</em><br/>
		 * flag (no value) <br/>
		 * remaining arguments = <em>arg1</em> and <em>arg2</em></blockquote>
		 * 
		 * 
		 * @param arguments
		 *          The arguments to parse and extract options and values from
		 * @return A list of all arguments that are not options or values, in the
		 *         same order they appeared in the specified arguments
		 * @throws IllegalArgumentException 
		 */
		public LinkedList<String> parse(String[] arguments)
				throws IllegalArgumentException {
			LinkedList<String> argQueue = new LinkedList<String>();
			// add all arguments to the queue
			for (String arg : arguments){
				argQueue.add(arg);
			}
			return parse(argQueue);
			
		}
		public LinkedList<String> parse(LinkedList<String> argQueue){
			LinkedList<String> nonOptionArgs = new LinkedList<String>();

			
			
			// Process all args
			while (argQueue.isEmpty() == false) {
				String arg = argQueue.poll();

				if (arg.equals("-") || arg.equals("--")) {
					// terminate option processing
					nonOptionArgs.addAll(argQueue);
					argQueue.clear();
				} else if (arg.startsWith("--")) {
					// process --opt as option with name 'opt'
					arg = stripHyphens(arg);
					this.processOption(false,arg, argQueue);
				} else if (arg.startsWith("-")) {
					// Option specified by short name
					arg = stripHyphens(arg);
					boolean append=false;
					if(arg.length() > 1){
						// we have a value appended to the arg
						argQueue.addFirst(arg.substring(1));
						arg = arg.substring(0, 1);
						append=true;
					}
					processOption(append,arg, argQueue);
				} else {
					// add all remaining args to the returned args
					nonOptionArgs.add(arg);
					nonOptionArgs.addAll(argQueue);
					argQueue.clear();
				}
			}

			return nonOptionArgs;
		}

		/**
		 * Makes sure all required options have instances in this
		 * <code>Options</code> instance.
		 * 
		 * @throws IllegalStateException
		 *           If a required option does not have an instance in this
		 *           <code>Options</code> instance.
		 */
		public void validate() throws IllegalStateException {
			for (Option opt : optionsById.values())
				if (opt.isRequired() && this.options.containsKey(opt) == false)
					throw new IllegalStateException("required option '" + opt.getId()
							+ "' was not present");
		}

		// PRIVATE METHODS ########################################################

		/**
		 * Fetches the instance of the specified option if it already exists. If it
		 * doesn't: make a new one, add it to the set of instances and return it.
		 * 
		 * @param opt
		 *          The option whose instance should be returned
		 * @return The instance of <code>opt</code>
		 */
		private Option.Instance getOptionInstance(Option opt) {
			Option.Instance optInstance = this.options.get(opt);
			if (optInstance == null) {
				optInstance = opt.newInstance();
				this.options.put(opt, optInstance);
			}

			return optInstance;
		}

	
		/**
		 * Adds and instance of the specified option to this object and assigns a
		 * value to it if appropriate. The value may be taken from <code>args</code>
		 * depending on whether the option takes arguments and if
		 * <code>opString</code> contains a value separator.
		 * 
		 * @param appended_arg the first argument was appended to the single char optstring
		 * @param optString
		 *          The string that identifies the option. 
		 * @param args
		 * @throws IllegalArgumentException
		 *           If the option is unknown, or and value is provided but the
		 *           option does not take values or cannot take any more values
		 */
		private void processOption(boolean appended_arg, String optString, Queue<String> args)
				throws IllegalArgumentException {
			

				Option opt = optionsByName.get(optString);
				if (opt == null)
					throw new IllegalArgumentException("Unknown option '" + optString
							+ "'");

				/*
				 * Get the instance of the option - creating it if it does not already
				 * exist
				 */
				Option.Instance optInstance = this.getOptionInstance(opt);
				if (opt.hasValue()) {
					if (args.isEmpty())
						throw new IllegalArgumentException("Option '" + optString
								+ "' takes an argument but none was found");

					optInstance.addValue(args.poll());
				}else if( appended_arg ){
					// something wrong here we were not expecting an arg
					throw new IllegalArgumentException("Option `"+optString
							+"' has unexpected argument `"+args.poll()+"`");
				}
			}
	}
}