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
/*
 + * Created on 09-Jan-2004
 *
 */
package uk.ac.ed.epcc.webapp.content;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import uk.ac.ed.epcc.webapp.NumberOp;

/**
 * 
 * 
 * A generic representation of a table that we can easily change into html/xml
 * or other text.
 * 
 * Table can be sparse and is essentially a Hashtable of Hashtables. Entries can
 * be any type of object The table class maintains a list of keys determining
 * the order which rows/cols are presented. by default this reflects the order
 * the elements were added to the table. but there are special functions for
 * handling subclasses of Number (summing cols/rows etc). Summary lines like
 * totals can be inserted as Strings to prevent them being inluded in futher
 * summation etc.
 * 
 * @author spb
 * @param <C> 
 * @param <R> 
 */

public class Table<C, R> {

	/**
	 * A column of the table
	 * 
	 * @see Table
	 * @author spb
	 * 
	 */
	public class Col {
		private Map<R, Object> data;

		private String name; // name to be used when printing
		private final C col_key;
		private Table.Formatter<C, R> format = null;
		private Map<String, String> attr = null; // XML attributes to apply to
													// all col elements
		private Map<R, Map<String, String>> element_attr = null; // per element
																	// attributes

		public void clear(){
			data.clear();
			format=null;
			if( attr != null){
				attr.clear();
				attr=null;
			}
			if( element_attr != null ){
				element_attr.clear();
				element_attr=null;
			}
		}
		private Col(C key) {
			this.col_key = key;
			if (key instanceof String) {
				setName((String) key);
			} else {
				setName(key.toString());
			}
			data = new HashMap<R, Object>();
			cols.put(key, this);
			if ((keyName == null || !keyName.equals(key))
					&& !col_keys.contains(key)) {
				col_keys.add(key);
			}
		}

		private Col(C myname, Map<R, Object> h) {
			this(myname);
			data = new HashMap<R, Object>(h);
		}

		public Object add(R key, Number value) {
			Number old = getNumber(key);
			if (old == null) {
				return put(key, value);
			} else {
				return put(key, NumberOp.add(old, value));
			}
		}public Object combine(Operator op,R key, Number value) {
			Number old = getNumber(key);
			if (old == null) {
				return put(key, value);
			} else {
				return put(key, op.operate(old, value));
			}
		}
		
		/** add contents of a different col
		 * 
		 * @param other
		 */
		public void combine(Operator op,Col other){
			for(R row : other.data.keySet()){
				Object val = other.get(row);
				// Only add a value if there is one, Hashtable doesn't like
				// nulls
				if (val != null) {
					if (val instanceof Number) {
						combine(op,row, (Number) val);

					} else {
						put(row, val);
					}

				}
			}
			if (format == null) {
				// pick up side effects.
				setFormat(other.format);
			}
			
		}
		/** merge contents of a different col
		 * 
		 * @param other
		 */
		public void merge(Col other){
			for(R row : other.data.keySet()){
				Object val = other.get(row);
				// Only add a value if there is one, Hashtable doesn't like
				// nulls
				if (val != null) {
						put(row, val);
				}
			}	
			if (format == null) {
				// pick up side effects.
				setFormat(other.format);
			}
			
		}

		/**
		 * Add per element attribute
		 * 
		 * @param key
		 * @param name
		 * @param value
		 */
		public void addAttribute(R key, String name, String value) {
			if (element_attr == null) {
				element_attr = new HashMap<R, Map<String, String>>();
			}
			Map<String, String> my_attr = element_attr.get(key);
			if (my_attr == null) {
				my_attr = new HashMap<String, String>();
				element_attr.put(key, my_attr);
			}
			my_attr.put(name, value);
		}

		/**
		 * add attribute for all col keys
		 * 
		 * @param name
		 * @param value
		 */
		public void addAttribute(String name, String value) {
			if (attr == null) {
				attr = new HashMap<String, String>();
			}
			attr.put(name, value);
		}

		// /** Add a row containing the total of the column with key "Total"
		// *
		// *
		// */
		// public void addTotal() {
		// addTotal("Total");
		// }
		/**
		 * Add a row containing the sum of the column
		 * 
		 * @param key
		 *            Object key for row
		 * @return the total calculated.
		 */
		public Double addTotal(R key) {
			Double total = sum();
			add(key, total);
			return total;
		}

		

		/**
		 * Get the object at the specified row
		 * 
		 * @param key
		 * @return Object or null
		 */
		public Object get(Object key) {
			return data.get(key);
		}

		/**
		 * return all attributes set for the current key
		 * 
		 * @param key
		 * @return Map
		 */
		public Map<String, String> getAttributes(R key) {
			Map<String, String> result = new HashMap<String, String>();
			if (attr != null) {
				result.putAll(attr);
			}
			if (element_attr != null) {
				Map<String, String> my_attr = element_attr.get(key);
				if (my_attr != null) {
					result.putAll(my_attr);
				}
			}
			return result;
		}

		protected Map<R, Object> getData() {
			return new HashMap<R, Object>(data);
		}

		public Table.Formatter<C, R> getFormat() {
			return format;
		}

		/**
		 * Get the name of this Col
		 * 
		 * @return String
		 */
		public String getName() {
			return name;
		}

		/**
		 * Get the contents of an entry as a Number
		 * 
		 * @param key
		 * @return number or null
		 */
		public Number getNumber(R key) {
			Object n = get(key);
			if (n != null && n instanceof Number) {
				return (Number) n;
			}
			return null;
		}

		/**
		 * Get the contents as text, applying a NumberFormat if Object is a
		 * Number
		 * 
		 * @param nf
		 *            NumberFormat
		 * @param key
		 * @return String
		 */
		public String getText(NumberFormat nf, R key) {
			Object raw = get(key);
			Object n = raw;
			if (format != null) {
				n = format.convert(Table.this, col_key, key, n);
			}
			if (nf != null && n instanceof Number) {
				n = nf.format(n);
			}else if( n instanceof UIGenerator){
				TextContentBuilder tcb = new TextContentBuilder();
				((UIGenerator)n).addContent(tcb);
				return tcb.toString();
			}else if (n == null) {
				if (type_debug) {
					return " (null)";
				}
				return "";
			}
			if (type_debug) {
				return n.toString() + " (" + raw.getClass().getCanonicalName()
						+ ")";
			}
			return n.toString();
		}

		/**
		 * Get entry as text
		 * 
		 * @param key
		 * @return String
		 */
		public String getText(R key) {
			return getText(null, key);
		}

		/**
		 * Does this col have a transform set.
		 * 
		 * @return boolean
		 */
		public boolean hasFormat() {
			return format != null;
		}

		/**
		 * Increment the contents of an integer Entry
		 * 
		 * @param key
		 * @param val
		 * @return Previous contents
		 */
		public Object increment(R key, int val) {
			Number old = getNumber(key);
			return put(key, NumberOp.add(old, Integer.valueOf(val)));
		}

		@SuppressWarnings({ "unchecked" })
		public List<C> keys() {
			return (List<C>) col_keys.clone();
		}

		public int printWidth() {
			return printWidth(null);
		}

		public int printWidth(NumberFormat nf) {
			int wid = 0;
			for (R k : data.keySet()) {
				int w = getText(nf, k).length();
				if (w > wid) {
					wid = w;
				}
			}
			return wid;
		}

		public Object put(R key, Object value) {
			addRow(key);
			if (value == null) {
				Object old = data.get(key);
				data.remove(key);
				return old;
			}
			return data.put(key, value);
		}
		/** Remove row data from the column
		 * 
		 * @param key
		 */
		public void remove(R key){
			data.remove(key);
			if( element_attr != null){
				element_attr.remove(key);
			}
		}
		/**
		 * Add an entry immediatly after a specified row.
		 * 
		 * @param key
		 *            Key of new entry
		 * @param after
		 *            Key of previous entry
		 * @param value
		 *            Object to add
		 * @return previous value
		 */
		public Object putAfter(R key, R after, Object value) {
			int index = row_keys.indexOf(after);
			if (index == -1) {
				return put(key, value);
			}
			Object ret = data.put(key, value);
			// If new key add to keylist after specified object
			if (ret == null) {
				if (!row_keys.contains(key)) {
					row_keys.add(index + 1, key);
				}
			}
			return ret;

		}

		/**
		 * sets a Transform to be used to format a col for printing. This is
		 * applied as part of getText but the stored data is left unchanged
		 * 
		 * 
		 * 
		 * @param t
		 *            The formatting transform
		 * @return previous transform if any
		 */
		public Table.Formatter<C, R> setFormat(Table.Formatter<C, R> t) {
			Table.Formatter<C, R> old = format;
			format = t;
			if( format instanceof TransformFormatter && ((TransformFormatter)format).getTransform() instanceof NumberTransform){
				addAttribute("numeric", "true");
			}
			return old;
		}

		public Table.Formatter setFormat(Transform t) {
			if( t instanceof NumberTransform){
				addAttribute("numeric", "true");
			}
			return setFormat(new TransformFormatter<C, R>(t));
		}

		public void setName(String name) {
			this.name = name;
		}

		public Double sum() {
			return sum(data.keySet());
		}

		// Sum a column
		public Double sum(Set<R> e) {
			addAttribute("numeric", "true");
			double sum = 0.0;
			for (R key : e) {
				Number value = getNumber(key);
				if (value != null) {
					sum += value.doubleValue();
				}
			}
			return new Double(sum);
		}

		// Transform a column
		public void transform(Collection<R> e, Transform t) {

			for (R key : e) {
				Object value = data.get(key);
				data.put(key, t.convert(value));
			}
		}

		public void transform(Transform t) {
			// transforms hidden values as well note
			transform(data.keySet(), t);
		}

	}

	public static class NumberFormatGenerator implements XMLGenerator{
		public NumberFormatGenerator(NumberFormat nf, Number data) {
			super();
			this.nf = nf;
			this.data = data;
		}
		private final NumberFormat nf;
		private final Number data;
		
		public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
			builder.attr("numeric", "true");
			if( data == null){
				builder.attr("null","true");
				builder.clean(nf.format(0.0));
			}else{
				builder.clean(nf.format(data));
			}
			return builder;
		}
		@Override
		public String toString() {
			// Want string representation to make sense
			if(data == null){
				return nf.format(0.0);
			}else{
				return nf.format(data);
			}
		}
		
	}
	/**
	 * Formatter; converts table entries when printing.
	 * 
	 * Note this also dereferences the table so it may also use the key values
	 * or other table cells when generating the result.
	 * 
	 * Note a Formatter can customise XML or UI generation by returning an
	 * object that implements either {@link XMLGenerator} or {@link UIGenerator}
	 * 
	 * @author spb
	 * @param <C> 
	 * @param <R> 
	 * 
	 */
	public static interface Formatter<C, R> {
		Object convert(Table<C, R> t, C col, R row, Object raw);
	}

	/**
	 * Implements a sort of rows based on the natural order of the various table
	 * cols. This will sort objects of the same type that implement Comparable
	 * or Numbers
	 * 
	 * @author spb
	 * @param <C> 
	 * 
	 */
	public static class Sorter<C> implements Comparator {
		private C keys[];
		private int reverse = 1;
		private Comparator comp[];
		private Table t;

		public Sorter(C sort_keys[], Comparator comp[], Table tt,
				boolean reverse) {

			keys = sort_keys;
			this.comp = comp;
			t = tt;
			if (reverse) {
				this.reverse = -1;
			}
		}

		public Sorter(C sort_keys[], Table tt) {
			this(sort_keys, null, tt, false);
		}

		public Sorter(C sort_keys[], Table tt, boolean reverse) {
			this(sort_keys, null, tt, reverse);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */

		@SuppressWarnings("unchecked")
		public int compare(Object o1, Object o2) {
			for (int i = 0; i < keys.length; i++) {
				Table.Col c = t.getCol(keys[i]);
				Object oa = c.get(o1);
				Object ob = c.get(o2);
				// null fields always sort to one end
				if (oa == null && ob != null) {
					return reverse;
				}
				if (oa != null && ob == null) {
					return -reverse;
				}
				if (oa != null && ob != null) {
					int v;
					if (comp != null && comp.length > i && comp[i] != null) {
						v = comp[i].compare(oa, ob);
					} else {
						v = compareNonNull(c.format, oa, ob);
					}
					if (v != 0) {
						return reverse * v;
					}
				}
			}
			// order by keys if data the same
			return reverse * compareNonNull(null, o1, o2);
		}

		@SuppressWarnings("unchecked")
		private int compareNonNull(Table.Formatter fmt, Object o1, Object o2) {
			Transform f = null;
			if (fmt instanceof TransformFormatter) {
				f = ((TransformFormatter) fmt).getTransform();
			}
			if (o1.getClass() == o2.getClass() && o1 instanceof Comparable) {
				return ((Comparable) o1).compareTo(o2);
			}
			if (o1 instanceof Number && o2 instanceof Number) {
				return Double.valueOf(((Number) o1).doubleValue()).compareTo(
						Double.valueOf(((Number) o2).doubleValue()));
			}
			if (f != null) {
				// compare formatted text
				String s1 = f.convert(o1).toString();
				String s2 = f.convert(o2).toString();
				return s1.compareTo(s2);
			}
			return o1.toString().compareTo(o2.toString());
		}
	}

	/**
	 * Adapter to convert a Transform into a Formatter
	 * 
	 * @author spb
	 * @param <C> 
	 * @param <R> 
	 * 
	 */
	public static class TransformFormatter<C, R> implements Formatter<C, R> {
		private final Transform t;

		public TransformFormatter(Transform t) {
			this.t = t;
		}

	
		public Object convert(Table<C, R> tab, C col, R row, Object raw) {
			if( t == null){
				return raw;
			}
			return t.convert(raw);
		}

		public Transform getTransform() {
			return t;
		}
	}

	private HashMap<C, Col> cols;

	// printed with this as the col header

	private LinkedList<R> row_keys = null; // order of the rows

	private LinkedList<C> col_keys = null; // order of the cols

	private String keyName = null; // if this is not null the row keys are shown
	private Transform keyTransform = null; // Transform for printing the row
											// keys;
	private Set<R> warnings = null;
	private Set<R> highlight = null;
	private boolean type_debug = false;
	private boolean printHeadings = true;
	private String id;
	public Table() {
		cols = new HashMap<C, Col>();
		row_keys = new LinkedList<R>();
		col_keys = new LinkedList<C>();

	}

	/**
	 * Construct a duplicate of the supplied table
	 * 
	 * @param t
	 */
	@SuppressWarnings("unchecked")
	public Table(Table<C, R> t) {
		cols = (HashMap<C, Col>) t.cols.clone();
		row_keys = new LinkedList<R>(t.row_keys);
		col_keys = new LinkedList<C>(t.col_keys);
		if (t.keyName != null) {
			keyName = t.keyName;
		}
		if (t.warnings != null) {
			warnings = new HashSet<R>(t.warnings);
		}
		if (t.highlight != null) {
			highlight = new HashSet<R>(t.highlight);
		}

	}
	/** Take a column from one table and add it as a row
	 *  to this one.
	 *  This is useful for turning a single column summary table for one object
	 *  into an index table showing many objects.
	 * @param row_key row to add
	 * @param col_key remote column to take
	 * @param t
	 */
	public <X> void addColumnAsRow(R row_key, X col_key, Table <X,C> t){
		for(C my_col_key : t.row_keys){
			put(my_col_key,row_key,t.get(col_key,my_col_key ));
		}
	}
	public void add(Table<C, R> t) {
		addRows(t);
		for (C col : t.col_keys) {
			Col c = getCol(col);
			Col src = t.getCol(col);
			c.combine(Operator.ADD,src);
			
			
		}
	}

	/** Add rows (including formatting) from a table. 
	 * @param t
	 */
	public void addRows(Table<C, R> t) {
		for (R row : t.row_keys) {
			if (row_keys != null) {
				if (!row_keys.contains(row)) {
					row_keys.add(row);
				}
			}
			if( t.getHighlight(row)){
				setHighlight(row, true);
			}
			if( t.getWarning(row)){
				setWarning(row, true);
			}
		}
	}

	/**
	 * add attribute to a single cell.
	 * 
	 * @param col
	 * @param row
	 * @param name
	 * @param value
	 */
	public void addAttribute(C col, R row, String name, String value) {
		Col c = getCol(col);
		c.addAttribute(row, name, value);
	}


	/**
	 * Generate a series of column sums grouped into categories based on the
	 * values of a different column.
	 * 
	 * @param sum_col
	 * @param cat_Col
	 * @param cat_to_key
	 *            Transform to convert the category value into the key
	 */
	public void addCategoryTotals(C sum_col, C cat_Col, Transform cat_to_key) {
		addCategoryTotals(sum_col, cat_Col, cat_to_key, null, null);
	}

	/**
	 * Generate a series of column sums grouped into categories based on the
	 * values of a different column. The cat_to_key Transform is used to
	 * generate a key for the Sum col from the category value. If the label_col
	 * value is non null the key value is also inserted into this col (to
	 * generate sum labels for tables where the key values are not shown)
	 * 
	 * @param sum_col
	 * @param cat_Col
	 * @param cat_to_key
	 * @param label_col
	 * @param highlight 
	 */
	@SuppressWarnings("unchecked")
	public void addCategoryTotals(C sum_col, C cat_Col, Transform cat_to_key,
			C label_col, Boolean highlight) {
		if (cat_Col == null || sum_col == null) {
			return;
		}
		HashMap<R, R> last = new HashMap<R, R>();
		HashMap<R, Number> sums = new HashMap<R, Number>();
		// we insert category sum after last element of the category
		// so create a row order to define the list.
		Col category = cols.get(cat_Col);
		Col values = cols.get(sum_col);
		if (category == null || values == null || sum_col == null) {
			return;
		}
		values.addAttribute("numeric", "true");
		for (R key : getRows()) {
			// If we have already generated this row-key as a summation line key
			// then assume we have multiple summation cols sharing the same
			// summation key so we skip this line
			if (!last.containsKey(key)) {
				Object thisCat = category.get(key);

				if (thisCat != null) {
					R sumkey;
					if (cat_to_key != null) {
						sumkey = (R) cat_to_key.convert(thisCat);
					} else {
						sumkey = (R) thisCat;
					}
					// are we summing this category
					if (sumkey != null) {
						// track all rows even if null
						last.put(sumkey, key); // last value seen
						Object thisValue = values.get(key);
						if (thisValue != null && thisValue instanceof Number) {
							Number val = (Number) thisValue;
							Number sum = NumberOp.add(sums.get(sumkey), val);
							sums.put(sumkey, sum);
						}
					}
				}
			}
		}
		// insert sums in sum_col after the last entry.
		// The sum might be null if all data was null so use last to ensure
		// there is
		// a total for every category
		for (R sumkey : last.keySet()) {
			Number value = sums.get(sumkey);
			putAfter(sum_col, sumkey, last.get(sumkey), value);
			if (label_col != null) {
				put(label_col, sumkey, sumkey);
			}
			if (highlight != null) {
				setHighlight(sumkey, highlight.booleanValue());
			}
		}
	}
	public void setCategoryRow(C target_col, C cat_Col, Transform cat_to_key,
			Transform cat_to_val) {
		if (cat_Col == null || target_col == null) {
			return;
		}
		
		// we insert category sum after last element of the category
		// so create a row order to define the list.
		Col category = cols.get(cat_Col);
		Col target = cols.get(target_col);
		if (category == null ) {
			return;
		}
		
		for (R key : getRows()) {
			
				Object thisCat = category.get(key);

				if (thisCat != null) {
					R sumkey;
					if (cat_to_key != null) {
						sumkey = (R) cat_to_key.convert(thisCat);
					} else {
						sumkey = (R) thisCat;
					}
					// are we summing this category
					if (sumkey != null) {
						target.put(sumkey,cat_to_val.convert(thisCat));
					}
				}
			}
		
	}

	public void addCategoryTotalToCol(C sum_col, C cat_Col) {
		addCategoryTotals(sum_col, cat_Col, new Transform() {

			public Object convert(Object old) {

				return old.toString() + " Total";
			}
		});
	}

	/**
	 * Generate sums of a column based on categories provided by a different
	 * column.
	 * 
	 * @param sum_col
	 *            name of col to be summed
	 * @param cat_Col
	 *            name of column specifying categories
	 * @param selection
	 *            map specifying categories to sum. keyed by the category and
	 *            defining the keys to use for the sums
	 */
	public void addCategoryTotalToCol(C sum_col, C cat_Col, Map selection) {
		addCategoryTotals(sum_col, cat_Col, new MapTransform(selection));
	}

	/**
	 * Add attribute to all cells in col
	 * 
	 * @param col
	 * @param name
	 * @param value
	 */
	public void addColAttribute(C col, String name, String value) {
		Col c = getCol(col);
		c.addAttribute(name, value);
	}

	

	/** Add a {@link Map} of data into a column
	 * If the data is a Number it is accumulated.
	 * 
	 * @param col_name
	 * @param data {@link Map} of data
	 */
	public void addMap(C col_name, Map<R, ?> data) {
		for (R key : data.keySet()) {
			Object thing = data.get(key);
			if (thing instanceof Number) {
				Number num = (Number) thing;
				addNumber(col_name, key, num);

			} else {
				put(col_name, key, thing);
			}
		}

	}
	/** Add a {@link Map} of data into a column
	 * If the data is a Number it is accumulated.
	 * 
	 * Row mapping translate the data keys into table row-keys
	 * if no mapping exists the data-key is used unchanged.
	 * 
	 * @param col_name
	 * @param row_mapping (optional row translations).
	 * @param data {@link Map} of data
	 */
	public void addMap(C col_name,Map<R,R> row_mapping, Map<R, ?> data) {
		for (R key : data.keySet()) {
			R row = row_mapping.get(key);
			if( row == null ){
				row=key;
			}
			Object thing = data.get(key);
			if (thing instanceof Number) {
				Number num = (Number) thing;
				addNumber(col_name, key, num);

			} else {
				put(col_name, key, thing);
			}
		}

	}

	public Object addNumber(C col_key, R row_key, Number value) {
		Object ret = null;
		Col col = getCol(col_key);
		ret = col.add(row_key, value);
		return ret;
	}

	/**
	 * Add a column containing the percentage of the values from an existing
	 * column against the total of that column
	 * 
	 * @param old_name
	 * @param new_name
	 */
	public void addPercentCol(C old_name, C new_name) {
		Col old_col = getCol(old_name);
		Double sum = old_col.sum();
		addPercentCol(old_name, new_name, sum);
	}

	/**
	 * add a column giving the percentage of a columns values against a
	 * specified total.
	 * 
	 * @param old_name
	 * @param new_name
	 * @param sum
	 */
	public void addPercentCol(C old_name, C new_name, Double sum) {
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMaximumFractionDigits(1);
		addPercentCol(old_name, new_name, sum, nf);
	}

	public void addPercentCol(C old_name, C new_name, Double sum,
			NumberFormat nf) {
		if (sum.doubleValue() <= 0.0) {
			return;
		}
		if (old_name == null || new_name == null) {
			return;
		}
		if (sum == null || sum.doubleValue() == 0.0) {
			return;
		}
		Col old_col = getCol(old_name);
		old_col.addAttribute("numeric", "true");
		Col new_col = getCol(new_name);

		new_col.setFormat(new NumberFormatTransform(nf));
		new_col.addAttribute("numeric", "true");

		for (R key : getRows()) {
			Number n = old_col.getNumber(key);
			if (n != null) {
				new_col.put(key,
						new Double(n.doubleValue() / sum.doubleValue()));
			}
		}
	}

	/**
	 * Add a column containing the percentage of the values from an existing
	 * column against the total of that column
	 * 
	 * @param old_name
	 * @param new_name
	 * @param nf
	 *            NumberFormat to format col
	 */
	public void addPercentCol(C old_name, C new_name, NumberFormat nf) {
		Col old_col = getCol(old_name);
		Double sum = old_col.sum();
		addPercentCol(old_name, new_name, sum, nf);
	}

	public void addRow(R key) {
		if (!row_keys.contains(key)) {
			row_keys.add(key);
		}
	}


	/**
	 * Add a totals entry to column
	 * 
	 * This is a noop if the specified col does not exist
	 * 
	 * @param col_name
	 *            String key of column to modify
	 * @param key
	 *            Object key to use for total
	 * @return total calculated or null if no column.
	 */
	public Double addTotalToCol(C col_name, R key) {
		Col c = cols.get(col_name);
		if (c != null) {
			return c.addTotal(key);
		}
		return null;
	}

	/**
	 * Add a totals entry to a column
	 * 
	 * @param col_index
	 *            int index of col
	 * @param key
	 *            key to use for Total
	 */
	public void addTotalToCol(int col_index, R key) {
		Col c = getCol(col_index);
		c.addTotal(key);
	}

	
	public void colOperation(C result_col, Operator op, C left_col, C right_col) {
		for (R key : getRows()) {
			final Number n1 = getNumber(left_col, key);
			final Number n2 = getNumber(right_col, key);
		    if( n1 == null && n2 == null ){
		    	// If both rows match but not numbers copy
		    	Object o1=get(left_col,key);
		    	Object o2=get(right_col,key);
		    	if( o1 != null && o2 != null && o1.equals(o2)){
		    		put(result_col,key,o1);
		    	}
		    }else{
		    	if( n1 != null & n2 != null ){
		    		put(result_col,
		    				key,
		    				op.operate(n1,
		    						n2));
		    	}
		    }
			
		}
	}
	public void rowOperation(R result_row, Operator op, R left_row, R right_row) {
		for (C key : getCols()) {
			final Number n1 = getNumber(key,left_row);
			final Number n2 = getNumber(key,right_row);
		    if( n1 == null && n2 == null ){
		    	// If both rows match but not numbers copy
		    	Object o1=get(key,left_row);
		    	Object o2=get(key,right_row);
		    	if( o1 != null && o2 != null && o1.equals(o2)){
		    		put(key,result_row,o1);
		    	}
		    }else{
		    	if( n1 != null && n2 != null ){
		    		put(key,
		    				result_row,
		    				op.operate(n1,
		    						n2));
		    	}
		    }
			
		}
	}

	@Deprecated
	public Collection<C> cols() {
		return new Vector<C>(col_keys);
	}

	/**
	 * Do we have a col of this name
	 * 
	 * @param col_name
	 * @return boolean
	 */
	public boolean containsCol(C col_name) {
		// Use col_keys as this is the structure modified by
		// removeCol
		return col_keys.contains(col_name);
	}

	public Object get(C col_key, R row_key) {
		Col col = cols.get(col_key);
		if (col == null) {
			return null;
		}
		return col.get(row_key);
	}

	public Map<String, String> getAttributes(C col_key, R row_key) {
		return getCol(col_key).getAttributes(row_key);
	}

	

	/**
	 * Find or create a col by key
	 * 
	 * @param col_key
	 * @return Col
	 */
	public Col getCol(C col_key) {

		Col res = cols.get(col_key);
		if (res == null) {
			res = new Col(col_key);
		}
		return res;
	}

	/**
	 * get a Col by numerical index
	 * 
	 * @param col_index
	 * @return Col
	 */
	private Col getCol(int col_index) {

		return cols.get(col_keys.get(col_index));
	}

	public Formatter getColFormat(C col_key) {
		return getCol(col_key).getFormat();
	}

	private Hashtable<String, Number> getColNumbersAsHashtable(Col col) {
		Col key_col = null;
		if (!printKeys()) {
			key_col = getCol(0);
		}
		return getColNumbersAsHashtable(key_col, col);
	}

	private Hashtable<String, Number> getColNumbersAsHashtable(Col key_col,
			Col col) {

		Hashtable<String, Number> h = new Hashtable<String, Number>();

		for (R row_key : getRows()) {

			String key;
			if (key_col == null) {
				key = row_key.toString();
			} else {
				key = key_col.getText(row_key);
			}
			Object value = col.get(row_key);

			if (key != null && value != null && value instanceof Number
					&& key.trim().length() > 0) {
				Number n = (Number) value;
				Number old = h.get(key);
				if (old != null) {
					h.put(key, NumberOp.add(n, old));
				} else {
					h.put(key, n);
				}

			}
		}
		return h;
	}

	/**
	 * Get a copy of the list of col keys.
	 * 
	 * @return List of col keys
	 */
	public List<C> getCols() {
		return (List<C>) new LinkedList<C>(col_keys);
	}

	@SuppressWarnings("unchecked")
	public List<C> getColumNames() {
		return (List<C>) col_keys.clone();
	}

	public Hashtable<String, Number> getHashtable(C col_key)
			throws InvalidArgument {
		Col col = cols.get(col_key);
		if (col == null) {
			throw new InvalidArgument("col key not found in table");
		}
		return getColNumbersAsHashtable(col);
	}

	/**
	 * get a hashtable of the contents of one Col keyed by the contents of
	 * another
	 * 
	 * @param col_key
	 * @param label_key
	 * @return hashtable
	 * @throws InvalidArgument
	 */
	public Hashtable<String, Number> getHashtable(C col_key, C label_key)
			throws InvalidArgument {
		if (label_key.equals(keyName)) {
			return getHashtable(col_key);
		}
		Col col = cols.get(col_key);
		Col lab = cols.get(label_key);
		if (col == null || lab == null) {
			throw new InvalidArgument("col key not found in table");
		}
		return getColNumbersAsHashtable(lab, col);
	}

	/**
	 * should we highlight this row.
	 * 
	 * @param row_key
	 * @return boolean
	 */
	public boolean getHighlight(R row_key) {
		return (highlight != null && highlight.contains(row_key));
	}

	
	public String getKeyName() {
		return keyName;
	}

	/**
	 * Convert a row key into printable text
	 * 
	 * @param key
	 * @return String
	 */
	public Object getKeyText(R key) {
		Object tmp = key;
		if (keyTransform != null) {
			tmp = keyTransform.convert(key);
		}
		return tmp;
	}

	public Number getNumber(C col_key, R row_key) {
		Col col = cols.get(col_key);
		if (col == null) {
			return null;
		}
		return col.getNumber(row_key);
	}

	/**
	 * Get a copy of the list of row keys.
	 * 
	 * @return list of row keys
	 */
	public List<R> getRows() {
		return (List<R>) new LinkedList<R>(row_keys);
	}

	/**
	 * get the table as a pure String
	 * 
	 * @return String
	 */
	public String getString() {
		String table = "";
		for (R row_key : getRows()) {

			if (printKeys()) {
				table += getKeyText(row_key) + "\t";
			}
			for (C key : getColumNames()) {
				table += getText(key, row_key);
				table += "\t";
			}
			table += "\n";
		}
		return table;
	}

	public String getText(C col_key, R row_key) {
		return getText(null, col_key, row_key);
	}

	public String getText(NumberFormat nf, C col_key, R row_key) {
		Col col = cols.get(col_key);
		if (col == null) {
			if (type_debug) {
				return " (null)";
			}
			return "";
		}
		return col.getText(nf, row_key);
	}

	/**
	 * should we mark this row with warning highlight.
	 * 
	 * @param row_key
	 * @return boolean
	 */
	public boolean getWarning(R row_key) {
		return (warnings != null && warnings.contains(row_key));
	}

	

	public boolean hasCol(String string) {
		return col_keys.contains(string);
	}

	public boolean hasColFormat(C col_name) {
		Col c = getCol(col_name);
		if (c == null) {
			return false;
		}
		return c.hasFormat();
	}

	public boolean hasData() {
		return nRows() > 0;

	}

	public boolean hasRow(R key) {
		return row_keys.contains(key);
	}

	/**
	 * increment a integer entry
	 * 
	 * @param col_key
	 * @param row_key
	 * @param value
	 * @return previous entry
	 */
	public Object increment(C col_key, R row_key, int value) {
		Col col = getCol(col_key);
		return col.increment(row_key, value);
	}

	public boolean isPrintHeadings() {
		return printHeadings;
	}

	public int nCols() {
		int n = col_keys.size();
		if (keyName != null) {
			n++;
		}
		return n;
	}

	public int nRows() {
		return row_keys.size();
	}

	/**
	 * Should the table keys be printed as part of the table. Ususally defaults
	 * to whether a keyname has been specifed
	 * 
	 * @return boolean
	 */
	public boolean printKeys() {
		return keyName != null && keyName.trim().length() > 0;
	}

	public Object put(C col_key, R row_key, Object value) {
		Object ret = null;
		Col col = getCol(col_key);

		ret = col.put(row_key, value);
		if (row_keys != null) {
			if (!row_keys.contains(row_key)) {
				row_keys.add(row_key);
			}
		}
		return ret;
	}

	public Object putAfter(C col_key, R row_key, R after, Object value) {
		Object ret = null;
		Col col = getCol(col_key);

		ret = col.putAfter(row_key, after, value);

		return ret;
	}

	/**
	 * remove a col from display data is retained and will return if key is
	 * replaced.
	 * 
	 * @param key
	 */
	public void removeCol(C key) {
		Col col = cols.get(key);
		if( col != null ){
			col.clear();
			cols.remove(key);
		}
		col_keys.remove(key);

	}

	public void removeRow(R key) {
		for(Col c : cols.values()){
			c.remove(key);
		}
		row_keys.remove(key);
	}

	@Deprecated
	public Collection<R> rows() {
		return new Vector<R>(row_keys);
	}

	/**
	 * convert all elements in a row to strings so they don't count against
	 * additional col totals
	 * 
	 * @param row_name
	 */
	public void rowToString(R row_name) {
		for (C col : col_keys) {
			put(col, row_name, getText(col, row_name).toString());
		}
	}

	/**
	 * Set col to specified position
	 * 
	 * @param key
	 * @param ind
	 */
	public void setCol(C key, int ind) {
		getCol(key);
		col_keys.remove(key);
		col_keys.add(ind, key);
	}

	/**
	 * set one col to follow another
	 * 
	 * @param first
	 *            Column we are to follow
	 * @param after
	 *            Column to move
	 */
	public void setColAfter(C first, C after) {
		// make sure col exists
		getCol(after);
		if (col_keys.contains(first)) {
			col_keys.remove(after);
			int pos = col_keys.indexOf(first);

			col_keys.add(pos + 1, after);
		}
	}

	public Table.Formatter<C, R> setColFormat(C col_name,
			Table.Formatter<C, R> t) {
		Col c = getCol(col_name);
		Table.Formatter<C, R> res = null;
		if (c != null) {
			res = c.setFormat(t);
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public Table.Formatter setColFormat(C col_name, Transform t) {
		if( t == null ){
			setColFormat(col_name, (TransformFormatter)null);
		}
		return setColFormat(col_name, new TransformFormatter<C, R>(t));
	}

	/**
	 * set col to last position
	 * 
	 * @param key
	 */
	public void setColLast(C key) {
		getCol(key);
		col_keys.remove(key);
		col_keys.add(key);
	}

	public void setColName(C key, String name) {
		Col c = getCol(key);
		c.setName(name);
	}

	public void setColumns(List<C> v) {
		col_keys = new LinkedList<C>(v);
	}

	/**
	 * Set a printing format for all columns
	 * 
	 * @param t
	 *            Transform to use
	 */
	public void setFormat(Table.Formatter<C, R> t) {
		for (C key : getColumNames()) {
			setColFormat(key, t);
		}
	}

	public void setFormat(Transform t) {
		setFormat(new TransformFormatter<C, R>(t));
	}

	/**
	 * mark a row to be highlighted
	 * 
	 * @param row_key
	 * @param value
	 */
	public void setHighlight(R row_key, boolean value) {
		if (highlight == null && value) {
			highlight = new HashSet<R>();
		}
		if (value) {
			highlight.add(row_key);
		} else {
			if (highlight != null) {
				highlight.remove(row_key);
			}
		}
	}

	/**
	 * Set the column name for the keys column this also causes the key column
	 * to be printed
	 * 
	 * @param keyName
	 *            String
	 */
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	/**
	 * Set a Transform used to print the keys.
	 * 
	 * @param t
	 */
	public void setKeyTransform(Transform t) {
		this.keyTransform = t;
	}

	public void setPrintHeadings(boolean printHeadings) {
		this.printHeadings = printHeadings;
	}

	/**
	 * set row to specified position
	 * 
	 * @param key
	 * @param ind
	 */
	public void setRow(R key, int ind) {
		if (ind < 0 || ind > row_keys.size()) {
			return;
		}
		row_keys.remove(key);
		row_keys.add(ind, key);
	}

	public void setRowAfter(R key, R after) {
		if (row_keys.contains(after) && row_keys.contains(key)) {
			row_keys.remove(key);
			int id = row_keys.indexOf(after);
			row_keys.add(id + 1, key);
		}
	}

	/**
	 * set row to last position
	 * 
	 * @param key
	 */
	public void setRowLast(R key) {
		row_keys.remove(key);
		row_keys.add(key);
	}

	/**
	 * add Java type information to output for debugging purposes.
	 * 
	 * @param flag
	 */
	public void setTypeDebug(boolean flag) {
		type_debug = flag;
	}

	/**
	 * mark a row to be highlighted
	 * 
	 * @param row_key
	 * @param value
	 */
	public void setWarning(R row_key, boolean value) {
		if (warnings == null && value) {
			warnings = new HashSet<R>();
		}
		if (value) {
			warnings.add(row_key);
		} else {
			if (warnings != null) {
				warnings.remove(row_key);
			}
		}
	}

	/**
	 * Sort rows according to a specified comparator
	 * 
	 * @param c
	 */
	@SuppressWarnings("unchecked")
	public void sortCols(Comparator c) {
		TreeSet s = new TreeSet(c);
		s.addAll(new LinkedList<Object>(col_keys));
		col_keys = new LinkedList(s);

	}
	/** sort rows using natural ordering of row keys
	 * 
	 */
	public void sortRows() {

		sortRows(null);

	}

	/**
	 * Sort Rows by the natural ordering of a set of column values. additional
	 * keys are only used if there is a tie from previous keys.
	 * 
	 * @param keys
	 *            Array of column keys
	 * @param reverse
	 *            boolean should order be reversed
	 */
	public void sortRows(C keys[], boolean reverse) {
		sortRows(new Table.Sorter<C>(keys, this, reverse));
	}

	/**
	 * Sort rows according to row keys using a specified comparator
	 * 
	 * @param c
	 */
	@SuppressWarnings("unchecked")
	public void sortRows(Comparator c) {
		TreeSet s = new TreeSet(c);
		s.addAll(new LinkedList<Object>(row_keys));
		row_keys = new LinkedList(s);

	}

	/** sum the numerical values in a column
	 * 
	 * @param key
	 * @return
	 */
	public double sumCol(C key) {
		return getCol(key).sum().doubleValue();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		TextTableFormatter< C, R> fmt = new TextTableFormatter<C, R>(this);
		fmt.add(sb);
		return sb.toString();
	}

	/** Apply a {@link Transform} to a column selected by name.
	 * 
	 * @param col_name
	 * @param t
	 */
	public void transformCol(C col_name, Transform t) {
		Col c = getCol(col_name);
		if (c != null) {
			c.transform(row_keys, t);
		}
	}

	/** apply a {@link Transform} to a column selected by index.
	 * 
	 * @param col_index
	 * @param t
	 */
	public void transformCol(int col_index, Transform t) {
		Col c = getCol(col_index);
		if (c != null) {
			c.transform(row_keys, t);
		}
	}
	/** Create a new column based on a {@link Transform} applied to the
	 * current row keys.
	 * 
	 * @param dest
	 * @param t
	 */
	public void transformKeys(C dest,Transform t){
		Col c = getCol(dest);
		for(R r : row_keys){
			c.put(r, t.convert(r));
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}