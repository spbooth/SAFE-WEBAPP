// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.content;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import uk.ac.ed.epcc.webapp.model.TemplateFinder;

//import uk.ac.hpcx.HpcxMain;

/**
 * Parameterised text files.
 * 
 * TemplateFile created (05/12/1999)
 * 
 * This class provides a method for reading files known as templates which can
 * contain tokens of the form %%token_name%%. These tokens may be replaced with
 * values set with setProperty() and will appear in place of the tokens in the
 * output when the file is written to a Writer or PrintStream using write().
 * 
 * For example, to set the title in a simple template we would do:
 * 
 * <pre>
 * TemplateFile tf = TemplateFile.getTemplateFile(&quot;template.html&quot;); // Load the page template
 * tf.setProperty(&quot;page_title&quot;, &quot;Our Page&quot;); // Apply the property
 * tf.write(output_writer); // When finished, write the template
 * </pre>
 * 
 * Properties may also be given default values in the template file, like so:
 * %%property=default_value%%
 * <p>
 * 
 * 
 * 
 * 
 * Regions <br>
 * Regions within a template file are marked with the syntax: <br>
 * <region> := <begin term> [text] <end term> <br>
 * <begin term> := "%%begin{" <region_name> ("," ("true"|"false")) "}%%" <br>
 * <end term> := "%%end{" (<region_name>) "}%%" <br>
 * <region_name> := ('0..9' | 'a..z' | 'A..Z' | '_' | '-')+ <br>
 * 
 * e.g.: <br>
 * %%begin{region1}%% text in region1 %%end{region1}%% <br>
 * or <br>
 * %%begin{region2, true}%% text in region2 %%end{}%% <br>
 * 
 * where <region_name> is an alphanumeric (or '_' and '-')
 * <p>
 * Regions are *disabled* by default and may be enabled with: setRegionEnabled(<region_name>,
 * <boolean value>);
 * <p>
 * Regions may be nested (though not overlapping) and can contain the usual
 * substitution tokens.
 * <p>
 * 
 * <pre>
 *  Document Structure:
 *   TemplateFile:
 *     Region: top_region (usually name == null)
 *       Region*: &lt;sub_region&gt;
 *       Property*: &lt;property&gt;
 *         String
 *         TemplateFile
 *         Object[]
 * </pre>
 * 
 * FUTURE: Retire cached template files which haven't been accessed for a while
 * (idea: make the getTemplateFile method clean up every so often - it's
 * synchronized so shouldn't pose a problem)
 * <p>
 * Possibly add a thread which is activated every second to reload modified
 * cached templates rather than checking their modification time each time they
 * are accessed.. might be faster in some cases.
 * <p>
 * Region pasting:
 * <p>
 * Add location types and allow regions to be pasted into them e.g. In HTML
 * document:
 * 
 * <pre>
 *   %%begin{thing}%%Thing: %%thing_value%% %%end{thing}%%
 *   '
 *   %%some_region%%
 * </pre>
 * 
 * In Java code:
 * 
 * <pre>
 *  template_file.setProperty(&quot;thing_value&quot;, &quot;SomeThing&quot;);
 * 
 *  TemplateFile region1 = template_file.getTemplateRegion(&quot;region_1&quot;));
 *  region1.setProperty(&quot;thing_value&quot;, &quot;SomeThing&quot;);
 *  
 *  template_file.setProperty(&quot;some_region&quot;, region1);
 *  
 *  TemplateFile email = ...
 *  
 *  email.setRegionEnabled(&quot;name_section&quot;, true);
 *  
 *  
 *  Vector user_accounts = new Vector();
 *  for(int i=0; i&lt;num_user_accounts
 *  TemplateFile user_account_region = email.getRegion(&quot;user_account_region&quot;);
 *  user_account_region.setProperty(&quot;username&quot;, jhdkjd
 *  user_accounts.add(user_account_region);
 *  }
 *  
 *  email.setProperty(&quot;user_accounts&quot;, user_accounts);
 * </pre>
 * 
 * Improve Memory Allocation:
 * 
 * By requesting a template file and then handing it back to the template file
 * manager (which at some stage 'cleans' the returned template files) fewer
 * template files will need to be created and destroyed.
 * @see TemplateFinder
 * @author Andrew Murdoch
 */
@uk.ac.ed.epcc.webapp.Version("$Id: TemplateFile.java,v 1.5 2015/07/15 13:55:32 spb Exp $")


public class TemplateFile {
	// Value may be a String or another TemplateFile
	class Property implements Cloneable {
		String name;

		int value_index; // Where to find in the local TemplateFile element
							// values

		int position;
	}

	// Description of marked regions within file
	class Region implements Cloneable {
		String name;

		int enabled_index;

		int start = -1;

		int end = -1;

		// elements contains properties and regions
		Vector<Object> elements = new Vector<Object>();

		Region() {
		}

		Region(String name, boolean enabled, int start, int end) {
			this.name = name;
			this.start = start;
			this.end = end;
		}

		public String toString(TemplateFile this_template) {
			String stuff = "";
			for (int i = 0; i < elements.size(); i++) {
				if (elements.elementAt(i) instanceof Region) {
					stuff += elements.elementAt(i).toString();
				}
			}

			return ""
					+ "Region: {"
					+ "name: "
					+ name
					+ ", "
					+ "enabled: "
					+ ((Boolean) this_template.template_values[enabled_index])
							.booleanValue() + ", " + "start: " + start + ", "
					+ "end: " + end + ", " + "[" + stuff + "]" + "}";
		}
	}

	// Enables or disables debugging (shows stuff in output)
	static boolean DEBUG = false;

	// Template File Managing

	// This stores the accessed template files - the problem is
	// synchronisation when multiple threads want files, e.g.
	// finding and adding a new file at the same time.
	// An additional problem will be ensuring consistency while
	// removing a file from the stored list

	static final String token = "%%";

	static final int token_length = token.length();

//	static Vector<TemplateFile> template_files = new Vector<TemplateFile>();

	// This is null in all templates but the original cached ones
	// Stores all the Regions and Properties for a TemplateFile
	// (In cached version stored only the default values)
	// The top_region be cannot stored because is not named.
	// (this is good because we don't want it to be found)
	Hashtable<String,Object> template_elements;

	// The only thing to vary between individual pages (non-cached versions)
	// Contains the values of each element, e.g.
	// Region (Boolean), Property values(Object) and so on.
	// (The index is got from the Region or Property)
	Object[] template_values;

	// Name of the template file
	String filename;

	// Holds all of template file, including tokens
	String file_contents;

	// Date is updated when the object is created (e.g. cloned)
	Date last_accessed_date = new Date();

	// The file the template was loaded from
//	File file = null;

	// time the file was last modified
	long file_last_modified;

	// This is the root region of all pages - all other regions are added to it
	// or its children
	Region top_region;

	// Properties (refer to ordered token position)

	/**
	 * Parses a <code>String</code> into the TemplateFile
	 * 
	 * @param file_contents
	 *            The <code>String</code> to be parsed
	 */
	public TemplateFile(String file_contents) {

		// Store a copy of the contents without the tokens
		StringBuilder buffer = new StringBuilder();

		Hashtable<String,Object> elements_hashtable = new Hashtable<String,Object>();

		Vector<Object> element_values = new Vector<Object>();
		// Now scan for valid properties and regions and record them
		int position = 0; // position in current unparsed contents
		{

			int index = 0; // eventual index into parsed file contents

			// Initialise region stack
			top_region = new Region();
			top_region.start = 0;
			element_values.add(Boolean.TRUE);

			top_region.enabled_index = element_values.size() - 1;

			Stack<Region> stack = new Stack<Region>();
			stack.push(top_region);

			while (true) { // Is ok, we can break out on the percent searches

				// Find first '%'
				int first_percents = file_contents.indexOf(token, position);
				if (first_percents == -1)
					break; // Normal termination condition

				// Find second '%'
				int second_percents = file_contents.indexOf(token,
						first_percents + token_length);
				if (second_percents == -1)
					break; // Actually a syntax error

				// Check property is valid,
				// e.g. only a-z, A-Z, 0-9, '_', '-' in subsitution names
				// Only things of format ('begin'|'end')'{'<name>'}' in region
				// delimiters

				buffer
						.append(file_contents.substring(position,
								first_percents));
				index += first_percents - position;

				position = second_percents + token_length;

				if (isValidLabel(file_contents, first_percents + token_length,
						second_percents)) {
					// Ok, this is a valid property name
					Property prop = new Property();

					// If this property has a default value, record it.
					int equals_position = file_contents.indexOf('=',
							first_percents + token_length);
					String property_name = null;
					String property_value = null;
					if ((equals_position != -1)
							&& (equals_position < second_percents)) {
						property_value = file_contents.substring(
								equals_position + 1, second_percents);
						property_name = file_contents.substring(first_percents
								+ token_length, equals_position);
					} else {
						//property_value = null;
						property_name = file_contents.substring(first_percents
								+ token_length, second_percents);
					}

					prop.name = property_name; // still required?
					prop.position = index;

					Object existing_prop = elements_hashtable
							.get(property_name);
					// There is a possible problem if non-Properties have
					// Property names
					if ((existing_prop != null)
							&& (existing_prop instanceof Property)) {
						// Just point to same value as existing property of this
						// name
						prop.value_index = ((Property) existing_prop).value_index;
						// Set the default value if present
						if (property_value != null) {
							element_values.setElementAt(property_value,
									prop.value_index);
						}
					} else {
						// Otherwise add a new value for this property
						element_values.add(property_value);
						prop.value_index = element_values.size() - 1;
						elements_hashtable.put(property_name, prop);
					}

					(stack.peek()).elements.addElement(prop);
				} else {
					// It might be a region
					// String region_string =
					// file_contents.substring(first_percents+token_length,
					// second_percents);
					if (file_contents.charAt(second_percents - 1) == '}') {
						if (file_contents.startsWith("begin{", first_percents
								+ token_length)) {
							int start_pos = first_percents + token_length
									+ ("begin{".length());

							// Check for default region enabled status
							int comma_pos = file_contents.indexOf(',',
									start_pos);
							int label_end = (comma_pos > 0) ? Math.min(
									comma_pos + 1, second_percents)
									: second_percents;

							if (isValidLabel(file_contents, start_pos,
									label_end - 1)) {
								// It's a valid start region delimiter, with
								// valid name
								Region region = new Region();
								region.name = file_contents.substring(
										start_pos, label_end - 1);
								region.start = index;

								// Work out if this Region is enabled by default
								// or not
								boolean enabled;
								if ((comma_pos != -1)
										&& (comma_pos < second_percents)
										&& file_contents.substring(
												comma_pos + 1,
												second_percents - 1).trim()
												.equalsIgnoreCase("true")) {
									enabled = true;
								} else {
									enabled = false;
								}

								// Have we got this one already?
								Object existing_region = elements_hashtable
										.get(region.name);
								if ((existing_region != null)
										&& (existing_region instanceof Region)) {
									// This region name already exists
									int enabled_index = ((Region) existing_region).enabled_index;

									// Only change the default if it is true now
									if (enabled)
										element_values.set(enabled_index,
												Boolean.TRUE);

									// Remember the index
									region.enabled_index = enabled_index;
								} else {
									// This is a new region

									// Record the value
									element_values.add(enabled ? Boolean.TRUE
											: Boolean.FALSE);
									// Remember the index of it
									region.enabled_index = element_values
											.size() - 1;

									// Put the region in the hashtable
									elements_hashtable.put(region.name, region);
								}

								// Add this region as a child of the current
								// parent
								(stack.peek()).elements
										.addElement(region);

								// Become the new parent region for this
								// interval
								stack.push(region);
							}
						}
						if (file_contents.startsWith("end{", first_percents
								+ token_length)) {
							int start_pos = first_percents + token_length
									+ "end{".length();

					//		if (true || isValidLabel(file_contents, start_pos,
					//				second_percents - 1)) {
								// It's a valid end region delimiter, with valid
								// name
								// We need to find if the "start{" exists and
								// finish it
								String name = file_contents.substring(
										start_pos, second_percents - 1);

								// Make sure we are ending the correct region
								if ((name.length() == 0)
										|| (stack.peek()).name
												.equals(name)) {
									(stack.peek()).end = index;
									stack.pop();
								} else {
									(stack.peek()).end = index;
									stack.pop();
									// Error!!!
								}
						//	}
						}
					}
				}
			}
		}

		buffer.append(file_contents.substring(position));
		top_region.end = buffer.length();

		template_values = element_values.toArray();
		template_elements = elements_hashtable;
		this.file_contents = buffer.toString();
	}

	// Creates a copy of the original TemplateFile
	protected TemplateFile(TemplateFile template) {

		// Make a copy of the template

		filename = template.getFilename();
		file_contents = template.file_contents;

		// Clone the regions (including properties, etc)
		top_region = template.top_region;
		template_elements = template.template_elements;
		template_values = new Object[template.template_values.length];

		// Copy the default values across
		System.arraycopy(template.template_values, 0, template_values, 0,
				template.template_values.length);

		// Set the original template's last accessed date (to now)
		template.last_accessed_date.setTime(System.currentTimeMillis());
	}

	// Creates a copy of the supplied template (minus set properties)
	// but the region supplied as the top_region.
	protected TemplateFile(TemplateFile template, Region region) {

		// Make a copy of the template without token values filled in
		filename = "(cloned, no file)";
		file_contents = template.file_contents;

		// Insert the region as the top_region (includes properties, etc)
		top_region = region;

		// Set the static region/property information
		template_elements = template.template_elements;
		template_values = new Object[template.template_values.length];

		// Copy the default values across
		System.arraycopy(template.template_values, 0, template_values, 0,
				template.template_values.length);
	}

	String getFilename() {
		return filename;
	}

	// Gets the value of a property (if it exists)
	public Object getProperty(String name) {
		Object element = template_elements.get(name);
		if ((element != null) && (element instanceof Property)) {
			return template_values[((Property) element).value_index];
		} else {
			// getContext().getLogger().info("template missing property
			// "+getFilename()+" prop "+name);
			return null;
		}
	}

	// Get the names of all the properties
	Enumeration getPropertyNames() {
		return template_elements.keys();
	}

	public Set<String> propertyNames(){
		return template_elements.keySet();
	}
	/**
	 * Fetches a representation of the TemplateFile which appears to contain
	 * only the region named.
	 * 
	 * @param region_name
	 *            Name of the region
	 * @return TemplateFile
	 */
	public TemplateFile getTemplateRegion(String region_name) {
		// Find region of this name
		Object region = template_elements.get(region_name);

		// Return null if we didnt't find it
		if ((region == null) || !(region instanceof Region)) {
			if (DEBUG)
				throw new RuntimeException("Could not find region: "
						+ region_name);
			else
				return null;
		}

		// And turn the region into a TemplateFile if we found it
		return new TemplateFile(this, (Region) region);
	}

	
	/**
	 * Provides a formatted description of the <code>TemplateFile</code>
	 * including region structure, properties and current values.
	 * 
	 * @param out
	 * @throws IOException
	 */
	public void prettyPrint(Writer out) throws IOException {
		prettyPrintRegion(out, top_region, 0);
		out.flush();
	}

	protected void prettyPrintRegion(Writer out, Region region, int d)
			throws IOException {
		for (int j = 0; j < d; j++)
			out.write(" ");
		out
				.write("Region: "
						+ ((region.name != null) ? ("\"" + region.name + "\"")
								: "null")
						+ (((Boolean) template_values[region.enabled_index])
								.booleanValue() ? " enabled" : "")
						+ " range: (" + region.start + ", " + region.end + ")"
						+ "\n");

		for (int i = 0; i < region.elements.size(); i++) {
			Object element = region.elements.elementAt(i);

			if (element == null) {
				// Property prop = ((Property)element);
				for (int j = 0; j < d + 2; j++)
					out.write(" ");
				out.write("null");
				continue;
			}
			if (element instanceof Property) {
				Property prop = ((Property) element);
				for (int j = 0; j < d + 2; j++)
					out.write(" ");
				Object value = template_values[prop.value_index];
				out.write("Property: "
						+ ((prop.name != null) ? ("\"" + prop.name + "\"")
								: "null") + " value=");
				if (value == null)
					out.write("null");
				else {
					if (value instanceof String)
						out.write("\"" + ((String) value) + "\"");
					else
						out.write("Object:" + value.getClass().getName());
				}
				out.write(" position: " + prop.position + "\n");
				continue;
			}

			if (element instanceof Region) {
				Region sub_region = ((Region) element);
				prettyPrintRegion(out, sub_region, d + 2);
				continue;
			}

			if (true) { // doesn't match anything.. who knows what it is?
				for (int j = 0; j < d + 2; j++)
					out.write(" ");
				out.write("Unknown: " + element.getClass().getName() + "\n");
			}
		}
	}

	/**
	 * Sets a set of properties from a hashtable
	 * 
	 * @param h
	 *            Hashtable containing properties.
	 */
	public void setProperties(Map h) {
		if( h != null ){
		for (Object key : h.keySet()) {
			setProperty(key.toString(), h.get(key));
		}

		}
	}

	/**
	 * Sets all named tokens in the resource properties to be the resource
	 * values
	 * 
	 * @param resource
	 */
	public void setProperties(ResourceBundle resource) {
		Enumeration keys = resource.getKeys();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			setProperty(key, resource.getString(key));
		}
	}

	/**
	 * Sets the value of a property to a double precision floating point number.
	 * 
	 * @param property_name
	 *            Name of the property to be set
	 * @param value
	 *            New <code>double</code> value for the property
	 */
	public void setProperty(String property_name, double value) {
		setProperty(property_name, Double.toString(value));
	}

	// For convenience..
	/**
	 * Sets the value of a property to an integer.
	 * 
	 * @param property_name
	 *            Name of the property to be set
	 * @param value
	 *            New <code>integer</code> value for the property
	 */
	public void setProperty(String property_name, int value) {
		setProperty(property_name, Integer.toString(value));
	}

	/**
	 * Sets the value of a property
	 * 
	 * @param property_name
	 *            Name of the property to be set
	 * @param property_value
	 *            New value for the property
	 */

	public void setProperty(String property_name, Object property_value) {
		Object element = template_elements.get(property_name);
		if ((element != null) && (element instanceof Property)) {
			template_values[((Property) element).value_index] = property_value;
		}
	}

	/**
	 * Sets a region to be enabled/disabled (in fact sets all regions with that
	 * name)  Any spaces in the region_name parameter will be mapped to underscores 
	 * to produce a valid region name
	 * 
	 * @param region_name
	 *            Name of the region to be set
	 * @param enabled
	 *            <code>true</code> if region is to be enabled,
	 *            <code>false</code> to disable the region
	 */
	public void setRegionEnabled(String region_name, boolean enabled) {
		region_name= region_name.replaceAll("\\s", "_");
		Object element = template_elements.get(region_name);
		if ((element != null) && (element instanceof Region)) {
			if (enabled)
				template_values[((Region) element).enabled_index] = Boolean.TRUE;
			else
				template_values[((Region) element).enabled_index] = Boolean.FALSE;
		}
	}

	/**
	 * @return A <code>String</code> containing the current contents of the
	 *          TemplateFile. If there was a problem, <code>null</code> is
	 *          returned.
	 */
	@Override
	public String toString() {
		try {
			StringWriter strout = new StringWriter();
			write(strout);
			return strout.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Convenience method - outputs the TemplateFile with it's current values to
	 * the <code>OutputStream</code>.
	 * 
	 * @param out
	 * @throws IOException
	 */
	public void write(OutputStream out) throws IOException {
		PrintWriter writer = new PrintWriter(out);
		write(writer);
	}

	/**
	 * Outputs the TemplateFile with it's current values to the
	 * <code>Writer</code>.
	 * 
	 * @param out
	 * @throws IOException
	 */
	public void write(Writer out) throws IOException {
		if (DEBUG) {
			try {
				prettyPrint(out);
				out.write("\n\n");
			} catch (Exception e) {
				e.printStackTrace(new PrintWriter(out));

			}
		}
		writeRegion(out, top_region);

		out.flush();
	}

	/*
	 * Helper methods for writing
	 */
	protected void writeRegion(Writer out, Region region) throws IOException {
		int position = Math.max(region.start, 0);

		// Iterate through elements, writing them
		for (int i = 0; i < region.elements.size(); i++) {
			Object element = region.elements.elementAt(i);
			if (element instanceof Property) {
				Property prop = ((Property) element);

				out.write(file_contents, position, prop.position - position);

				writePropertyValue(out, template_values[prop.value_index]);

				position = prop.position;
			} else {
				if (element instanceof Region) {
					Region sub_region = ((Region) element);

					out.write(file_contents, position, Math.max(
							sub_region.start, 0)
							- position);

					// Recursively write sub-regions (if enabled)
					if (((Boolean) template_values[sub_region.enabled_index])
							.booleanValue()) {
						writeRegion(out, sub_region);
					}

					position = sub_region.end;
				}
			}
		}

		// No property in this region, just write the string
		out.write(file_contents, position, Math.min(region.end - position,
				file_contents.length() - position));
	}

	public static TemplateFile getFromString(String template_string) {
		// No caching is done for String values
		return new TemplateFile(template_string);
	}

	/**
	 * Returns a parsed <code>TemplateFile</code> based on the filename
	 * provided
	 * 
	 * @param filename The name of the desired <code>TemplateFile</code>
	 * @return An empty <code>TemplateFile</code> (only default values filled
	 *          in)
	 * @throws IOException
	 */
	public static synchronized TemplateFile getTemplateFile(String filename)
			throws IOException {

//		// Have we already got this one?
//		for (int i = 0; i < template_files.size(); i++) {
//			TemplateFile template_file = template_files
//					.elementAt(i);
//
//			if (filename.equals(template_file.getFilename())) {
//
//				File current_file = new File(filename);
//				// See if the file has been changed
//				if (template_file.file_last_modified < current_file
//						.lastModified()) {
//					// Is has been changed.. reload it and replace the previous
//					// one
//					TemplateFile new_template_file = loadFromFile(filename);
//					template_files.setElementAt(new_template_file, i);
//
//					// Return a copy of it (don't want it modified)
//					return new TemplateFile(new_template_file);
//				} else {
//					// Ok, got an existing one, return a fresh copy
//					return new TemplateFile(template_file);
//				}
//			}
//		}

		// Otherwise need to make a new TemplateFile and store it
		TemplateFile template_file = loadFromFile(filename);
//		template_files.addElement(template_file);
//
//		// Return a copy of it (don't want it modified)
//		return new TemplateFile(template_file);
		return template_file;
	}

	/*
	 * Useful methods for parsing
	 */
	static boolean isValidLabel(String str) {
		return isValidLabel(str, 0, str.length());
	}

	static boolean isValidLabel(String str, int start, int end) {
		for (int i = start; i < end; i++) {
			char ch = str.charAt(i);
			if (((ch < 'a') || (ch > 'z')) && ((ch < 'A') || (ch > 'Z'))
					&& ((ch < '0') || (ch > '9')) && (ch != '_') && (ch != '-')
					&& (ch != '.')) {
				// If some good characters followed by a '=' it's valid (and
				// includes a default value)
				if ((i > 0) && (ch == '='))
					return true;
				else
					return false;
			}
		}
		return true;
	}

	protected static TemplateFile loadFromFile(String filename)
			throws IOException {
		// Get the file details
		File file = new File(filename);
		long file_last_modified = file.lastModified();

		// Read the file contents
		String file_contents;
		{
			StringBuilder file_buffer = new StringBuilder();
			FileReader f = new FileReader(file);

			char buffer[] = new char[4096];
			int chars_read = f.read(buffer, 0, 4096);
			while (chars_read != -1) { // while not EOF
				file_buffer.append(buffer, 0, chars_read);

				chars_read = f.read(buffer, 0, 4096);
			}
			f.close();
			file_contents = file_buffer.toString();
		}

		TemplateFile template = new TemplateFile(file_contents);

		// Set file info
		template.filename = filename;
		template.file_last_modified = file_last_modified;

		return template;
	}

	protected static void writePropertyValue(Writer out, Object value)
			throws IOException {
		// Ignore null values
		if (value == null)
			return;

		// Write out value
		if (value instanceof String) {
			// Just a string, write it as standard
			out.write((String) value);
			return;
		}

		// Recurse into contained template
		if (value instanceof TemplateFile) {
			// Uh oh, need to recurse into this value
			((TemplateFile) value).write(out);
			return;
		}

		// Iterate over array of properties
		if (value instanceof Object[]) {
			Object objects[] = (Object[]) value;
			for (int i = 0; i < objects.length; i++) {
				if( i > 0 ){
					out.write(", ");
				}
				writePropertyValue(out, objects[i]);
			}
			return;
		}

		// Iterate over array of properties
		if( value instanceof Iterable){
			Iterable c = (Iterable) value;
			boolean seen=false;
			for(Object o : c){
				if( seen){
					out.write(", ");
				}
				writePropertyValue(out, o);
				seen=true;
			}
			return;
		}
		// Damn, it's something we don't know how to write
		throw new RuntimeException(
				"Invalid type of Object found in property values - "
						+ value.getClass().getName());
	}

	/*
	 * public static void main(String args[]) throws Exception {
	 * 
	 * 
	 * StringWriter string_write = new StringWriter();
	 *  // PrintWriter writer = new PrintWriter(System.out); PrintWriter writer =
	 * new PrintWriter(string_write);
	 * 
	 * long time1 = System.currentTimeMillis();
	 * 
	 * TemplateFile tf = getTemplateFile(args[0]); tf.write(System.out);
	 * 
	 * long time2 = System.currentTimeMillis(); System.out.println("time taken = " +
	 * (time2 - time1));
	 *  /* // for(int i=0; i<10000; i++) {
	 * 
	 * TemplateFile tf = getTemplateFile(args[0]); tf.setProperty("token_value",
	 * "BARGLE12"); TemplateFile tf2 = getTemplateFile(args[0]); //
	 * tf.setProperty("other_token", "oTHeR"); Object array[] = {"rat<",
	 * ">pang"}; tf2.setProperty("token_value", array); Vector v = new Vector();
	 * v.add("Toss"); v.add(tf2); tf.setProperty("token_value2", v); //
	 * tf.prettyPrintRegion(writer, tf.top_region, 0); tf.prettyPrint(writer);
	 * writer.flush(); writer.write("\n"); // tf.setRegionEnabled("argh", true);
	 * tf.setRegionEnabled("farg", true); // TemplateFile argh =
	 * tf.getTemplateRegion("argh"); // tf.setProperty("token_value2", argh);
	 * tf.write(writer); writer.flush();
	 */
	/*
	 * for(int i=0; i<10000; i++) { TemplateFile templ =
	 * getTemplateFile("VendorBankOfNumbers.html");
	 * 
	 * templ.setProperty("last_n_used_numbers_caption", "Some numbers");
	 * 
	 * TemplateFile regn = templ.getTemplateRegion("available_numbers_region");
	 * regn.setProperty("index", Integer.toString(i));
	 * templ.setProperty("available_numbers", regn);
	 *  // templ.write(writer); }
	 * 
	 * 
	 * long time2 = System.currentTimeMillis();
	 *  // System.out.println(string_write.toString()); // }
	 * 
	 * 
	 * System.out.println("time taken = " + (time2 - time1));
	 */
	// }

	// public static void main(String args[]) throws Exception {
	// TemplateFile tf = getTemplateFile(args[0]);
	//		
	// PrintWriter writer = new PrintWriter(System.out);
	// tf.prettyPrint(writer);
	// writer.close();
	// System.exit(0);
	// }
}
// History
// Andy - 15/12/1999
// - changed '%' symbol to '%%' (to prevent clashes with common HTML)
// Andy - 18/12/1999
// - fixed problem with arrays of templates and/or strings
// - clean up and small improvements (~15% faster)
// Andy - 04/01/2000
// - when cached templates are accessed, the file modification date is
// checked to see if they should be reloaded or not
// Andy - 13/01/2000
// - muchos grande changes - support for regions, which may be enabled
// or disabled.
// Andy - 16/01/2000
// - added optional default values for tokens, taking the form:
// %%<name>=<default value>%%
// Andy - 04/02/2000
// - now returns property values (as objects)
// Andy - ??/03/2000
// - added ability to accept Strings and make Templates from them (no caching)
// Andy - 22/03/2000
// - debugging.. would return name of file rather than file on first request
// Andy - 05/04/2000
// - Vectors are now accepted by setProperty() - they behave in the same ways as
// Object[] arrays.
// Andy - 06/04/2000
// - Regions may be returned as TemplateFiles in their own right, representing
// the enclosed sub-region. They may be subsequently reinserted into the
// original document containing them with no ill effects. (This makes code
// which normally uses other TemplateFiles as sub-regions much cleaner).
// Note: Never start a region in another region of the same name!
// Andy - 05/05/2000
// - Regions no longer need to have a name in the end term
// Andy - 05/05/2000
// - Region can now have a default enabled term, given by a boolean value
// after the region name and a comma.
// Andy - ??/05/2000
// - Revised method of storing region and property data, now makes setting
// values
// and copying Templates much faster (uses an Object array and a Hashtable)
// Andy - 29/05/2000
// - Debugging, bug where multiple property values have same name.
// This exposes a general problem of using a hashtable to address
// values which may be replicated.
// - getRegionTemplate() wasn't working properly, default properties were
// mangled
// Andy - 04/06/2000
// - Regions can now appear more than once, e.g.:
// %%begin{region1}%%once%%end{}% and %%begin{region1}%%twice%%end{}%%
// The method - setRegionEnabled("region1", true) will enabled both regions.
// Andy - 06/06/2000
// - Minor change, added setProperty(String, int) and setProperty(String,
// double) as
// convenience methods
// Andy - 30/06/2000
// - better javadoc style documentation and more logical layout of methods
