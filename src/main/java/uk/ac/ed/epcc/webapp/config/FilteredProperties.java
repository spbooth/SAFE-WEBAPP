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
package uk.ac.ed.epcc.webapp.config;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import uk.ac.ed.epcc.webapp.AppContext;



/**
 * <p>
 * Simple class to help prepare properties for a particular application.
 * Different applications set properties via the {@link AppContext}'s
 * {@link ConfigService}. To differentiate them from other properties, these
 * properties usually start with some kind of identifier (for example, all
 * properties starting with <em>class.</em> hold values of classes that can be
 * loaded at runtime). This object takes either a <code>Properties</code> object
 * or and <code>AppContext</code> (from which it extract's the configuration
 * properties) and populates itself with all the properties whose keys start
 * with the specified base string. Once constructed, this object will hold all
 * the properties specific to the application that created it.
 * <code>FilteredProperties</code> also features the concept of a <em>mode</em>.
 * The idea is that an application will function differently depending on which
 * <em>mode</em> it is in. Properties may be set on a <em>mode</em> by
 * <em>mode</em> basis or a global basis (applies to all modes).
 * </p>
 * <p>
 * <code>FilteredProperties</code> assumes the property is defined in the
 * following form.
 * 
 * <blockquote> <em>base.mode.prop_key</em> </blockquote>
 * 
 * The base is stripped out during construction so the property keys in a
 * <code>FilteredProperties</code> object don't contain the base. When searching
 * for properties, <code>FilteredProperties</code> checks to see if the property
 * is set for the specified mode. If there isn't one specifically set, or
 * <em>mode</em> has not been set, the property with the key specified is
 * returned. For example suppose we construct a <code>FilteredProperties</code>
 * object with the current properties (in an object called <em>myProperties</em>
 * ):
 * 
 * <blockquote> myapp.prop1 = val1 <br/>
 * myapp.mode1.prop2 = val2 <br/>
 * myapp.mode2.prop2 = val3 </blockquote>
 * 
 * using the constructor
 * 
 * <blockquote> FilteredProperties props = new FilteredProperties(myProperties,
 * "myapp", "mode2"); </blockquote>
 * 
 * In this case, the following statements would be true:
 * 
 * <blockquote> props.getProperty("prop1") == "val1";<\br>
 * props.getProperty("prop2") == "val3";<\br> </blockquote>
 * </p>
 * 
 * @author jgreen4
 * 
 */


public class FilteredProperties extends Properties {

	/**
	 * All properties in the <code>Properties</code> object or
	 * <code>AppContext</code> provided during construction that had property keys
	 * starting beginning with this string were extracted by this filter.
	 */
	private String base;
	/**
	 * The mode to use when fetching properties. Can be <code>null</code>
	 */
	private String mode;
	/**
	 * The original properties that this filter extracted properties from
	 */
	private Properties orig;

	

	

	/**
	 * Constructs a new <code>FilteredProperties</code> based on extracting all
	 * properties from the provided <code>Properties</code> object whose keys
	 * begin with the specified <code>base</code> string. The base string along
	 * with the dot that should follow it will be stripped off all property keys.
	 * The mode is not set.
	 * 
	 * @param properties
	 *          The <code>AppContext</code> to extract properties from
	 * @param base
	 *          All properties that begin with this string will be extracted
	 */
	public FilteredProperties(Properties properties, String base) {
		this(properties, base, null);
	}

	/**
	 * Constructs a new <code>FilteredProperties</code> based on extracting all
	 * properties from the provided <code>Properties</code> object whose keys
	 * begin with the specified <code>base</code> string. The base string along
	 * with the dot that should follow it will be stripped off all property keys.
	 * When fetching properties, properties that begin with the specified
	 * <code>mode</code> will take presidence.
	 * 
	 * @param properties
	 *          The <code>AppContext</code> to extract properties from
	 * @param base
	 *          All properties that begin with this string will be extracted
	 * @param mode
	 *          The default mode.
	 */
	public FilteredProperties(Properties properties, String base, String mode) {
		this.base = base;
		this.mode = mode;
		this.orig = properties;

		// +1 to remove the extra '.'
		final int baseLen = base.length() + 1;

		Enumeration propEnum = properties.propertyNames();

		if (mode == null || mode.trim().length()==0) {
			/*
			 * No mode. Just add all properties with keys starting with the specified
			 * base (striping the base off
			 */

			while (propEnum.hasMoreElements()) {
				String key = propEnum.nextElement().toString();

				if (key.startsWith(base)) {
					String filteredKey = key.substring(baseLen);
					this.setProperty(filteredKey, properties.getProperty(key));
				}
			}
		} else {
			/*
			 * Strip properties with key starting with specified base. Moded
			 * properties take precedence.
			 */

			final String baseAndMode = base + "." + mode;
			final int baseAndModeLen = baseAndMode.length() + 1;

			while (propEnum.hasMoreElements()) {
				String key = propEnum.nextElement().toString();

				if (key.startsWith(base)) {
					String filteredKey;

					if (key.startsWith(baseAndMode)) {
						// moded property
						filteredKey = key.substring(baseAndModeLen);
					} else {
						// unmoded property
						filteredKey = key.substring(baseLen);

						/*
						 * If property has already been set, it must be the moded one. That
						 * takes presidence over unmoded properties so we don't add it
						 */
						if (this.getProperty(filteredKey) != null) {
							continue;
						}
					}

					this.setProperty(filteredKey, properties.getProperty(key));
				}
			}
		}
	}

	/**
	 * Returns the default mode being used by this object.
	 * 
	 * @return the default mode being used by this object
	 * @see #getProperty(String)
	 */
	public String getMode() {
		return this.mode;
	}

	/**
	 * <p>
	 * If <code>filteredKey</code> is a property key for this properties object,
	 * this method returns what the corresponding key that would be used to
	 * extract the same property from the original properties used to construct
	 * this object. The original key would have started with this object's
	 * <code>base</code> and may have contained the <code>mode</code>. This method
	 * only makes sense for values that are keys of this object and properties
	 * that were extracted from the original <code>Properties</code> object
	 * provided at construction time (if an <code>AppContext</code> was provided,
	 * the <code>Properties</code> object would have been extracted from it via
	 * it's <code>ConfigService</code>.
	 * </p>
	 * <p>
	 * If the underlying properties contained in the original
	 * <code>Properties</code> object have been removed, this method will fail to
	 * produce the correct result. It is intended to provide more useful
	 * information for error messages and may not be reliable for other purposes.
	 * </p>
	 * 
	 * @param filteredKey
	 *          A key for this <code>Properties</code> object pointing to a
	 *          property extracted from the original <code>Properties</code>
	 *          object.
	 * @return The key for the same property residing in the original
	 *         <code>Properties</code> object. <code>null</code> can be returned
	 *         if <code>filteredKey</code> is not a key for this
	 *         <code>Properties</code> object or the property extracted from the
	 *         original <code>Properties</code> object has since been removed.
	 */
	public String getOriginalKey(String filteredKey) {
		String origKey;

		origKey = this.base + "." + this.mode + "." + filteredKey;
		if (this.orig.containsKey(origKey))
			return origKey;

		origKey = this.base + "." + filteredKey;
		if (this.orig.getProperty(origKey) == null)
			return null;
		else
			return origKey;
	}
	
	public Iterable<String> names() {
		return new Iterable<String>() {

			@Override
			public Iterator iterator() {
				return nameIterator();
			}
			
		};
	}

	/**
	 * Convenience property name iterator method. <code>Iterator</code> objects
	 * are nicer than <code>Enumeration</code> objects. This method wraps the
	 * <code>Enumeration</code> object generated for iterating over property names
	 * in this object in an <code>Iterator</code> to make using it easier.
	 * 
	 * @return An iterator that spans all the property names in this object.
	 */
	public Iterator<String> nameIterator() {
		return new Iterator<String>() {
			Enumeration<?> enumeration = FilteredProperties.this.propertyNames();

			public boolean hasNext() {
				return enumeration.hasMoreElements();
			}

			public String next() {
				return enumeration.nextElement().toString();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	/**
	 * Check for a boolean parameter yes/on/true or no/off/false
	 * 
	 * @param name
	 *            String name of paameter
	 * @param def
	 *            boolean default if it does not exist.
	 * @return boolean
	 */
	public boolean getBooleanProperty(String name, boolean def) {
		String parm = getProperty(name);
		if (parm == null) {
			return def;
		}
		boolean res = def;
		parm = parm.trim();
		if (parm.equalsIgnoreCase("yes") || parm.equalsIgnoreCase("on")
				|| parm.equalsIgnoreCase("true")) {
			res = true;
		}
		if (parm.equalsIgnoreCase("no") || parm.equalsIgnoreCase("off")
				|| parm.equalsIgnoreCase("false")) {
			res = false;
		}
		return res;
	}

	
	
	public double getDoubleProperty(String name, double def) {
		String parm = getProperty(name);
		if (parm == null) {
			return def;
		}
		double res = def;
		try{
			res = Double.parseDouble(parm.trim());
		} catch (NumberFormatException e) {
			
		}
		return res;
	}
	public int getIntegerProperty(String name, int def) {
		String parm = getProperty(name);
		if (parm == null) {
			return def;
		}
		int res = def;
		try {
			res = Integer.parseInt(parm.trim());
		} catch (NumberFormatException e) {
			
		}
		return res;
	}
	public long getLongProperty(String name, long def) {
		String parm = getProperty(name);
		if (parm == null) {
			return def;
		}
		long res = def;
		try {
			res = Long.parseLong(parm.trim());
		} catch (NumberFormatException e) {
			
		}
		return res;
	}
	/*
	 * ##########################################################################
	 * STATIC METHODS
	 * ##########################################################################
	 */

	/**
	 * Attempts to return a property value from the specified
	 * <code>Properties</code> object with the name <em>base.mode</em>. If such a
	 * property does not exist, the property value with name <em>base</em> is
	 * returned
	 * 
	 * @param properties
	 *          The <code>Properties</code> object to fetch the value from
	 * @param base
	 *          The base of the property
	 * @param mode
	 *          The optional mode of the property
	 * @return The property value whose key is <em>base.mode</em> if it exists. If
	 *         it doesn't, the property value whose key is <em>base</em>. If that
	 *         doesn't exist, <code>null</code> is returned.
	 */
	public static String getProperty(Properties properties, String base,
			String mode) {
		return getProperty(properties, base, mode, null, null);
	}

	/**
	 * Attempts to return a property value from the specified
	 * <code>Properties</code> object with the name <em>base.mode.key</em>. If
	 * such a property does not exist, the property value with name
	 * <em>base.key</em> is returned.
	 * 
	 * @param properties
	 *          The <code>Properties</code> object to fetch the value from
	 * @param base
	 *          The base of the property
	 * @param mode
	 *          The optional mode of the property
	 * @param key
	 *          The end string of the property
	 * @return The property value whose key is <em>base.mode.key</em> if it
	 *         exists. If it doesn't, the property value whose key is
	 *         <em>base.key</em>. If that doesn't exist, <code>null</code> is
	 *         returned.
	 */
	public static String getProperty(Properties properties, String base,
			String mode, String key) {
		return getProperty(properties, base, mode, key, null);
	}

	/**
	 * Attempts to return a property value from the specified
	 * <code>Properties</code> object with the name <em>base.mode.key</em>. If
	 * such a property does not exist, the property value with name
	 * <em>base.key</em> is returned if it exists. It doesn't,
	 * <code>defaultVal</code> is returned.
	 * 
	 * @param properties
	 *          The <code>Properties</code> object to fetch the value from
	 * @param base
	 *          The base of the property
	 * @param mode
	 *          The optional mode of the property
	 * @param key
	 *          The end string of the property
	 * @param defaultVal
	 *          The value to return if no property could be found
	 * @return The property value whose key is <em>base.mode.key</em> if it
	 *         exists. If it doesn't, the property value whose key is
	 *         <em>base.key</em>. If that doesn't exist, the property value with
	 *         key <code>defaultVal</code> is returned.
	 */
	public static String getProperty(Properties properties, String base,
			String mode, String key, String defaultVal) {
		if (key == null || key == "")
			key = "";
		else
			key = "." + key;

		String val = properties.getProperty(base + "." + mode + key);
		if (val == null)
			val = properties.getProperty(base + key);
		if (val == null)
			return defaultVal;
		else
			return val;
	}
}