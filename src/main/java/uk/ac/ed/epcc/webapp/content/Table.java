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
import java.util.*;

import uk.ac.ed.epcc.webapp.EmptyIterable;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.NumberOp;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;

/**
 * 
 * 
 * A generic representation of a table that we can easily change into html/xml
 * or other text.
 * 
 * Table can be sparse and is essentially a Map of Maps. Entries can
 * be any type of object The table class maintains a list of keys determining
 * the order which rows/cols are presented. by default this reflects the order
 * the elements were added to the table. but there are special functions for
 * handling subclasses of Number (summing cols/rows etc). Summary lines like
 * totals can be inserted as Strings to prevent them being included in further
 * summation etc.
 * 
 * Columns can be arranged in groups (groups use the same key type columns and must not
 * duplicate any of the keys used by the columns). Formatting instructions can be applied
 * to all columns in the group. The columns of a group can be packed together. It is also possible to
 * show the groups as an additional table header row.
 * 
 * @author spb
 * @param <C> type (or common supertype) of column keys
 * @param <R> type (or common supertype) of row keys
 */

public class Table<C, R> {

	/**
	 * A column of the table.
	 * 
	 * Optionally a name may be specified for the column that is used in formatting rather than
	 * the column key. Unlike the key column names do not need to be unique.
	 * 
	 * @see Table
	 * @author spb
	 * 
	 */
	public class Col {
		private Map<R, Object> data;

		private String name; // optional name to be used when printing
		private final C col_key;
		private Table.Formatter<C, R> format = null;
		private Map<String, String> attr = null; // XML attributes to apply to
													// all col elements
		private Map<R, Map<String, String>> element_attr = null; // per element
																	// attributes

		private boolean dedup =false;
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
			data = new HashMap<>();
			cols.put(key, this);
			if ((keyName == null || !keyName.equals(key))
					&& !col_keys.contains(key)) {
				col_keys.add(key);
			}
		}

		private Col(C myname, Map<R, Object> h) {
			this(myname);
			data = new HashMap<>(h);
		}

		public Object add(R key, Number value) {
			Number old = getNumber(key);
			if (old == null) {
				return put(key, value);
			} else {
				return put(key, NumberOp.add(old, value));
			}
		}
		public Object combine(Operator op,R key, Number value) {
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
		public void combine(Operator op,Table<?,R>.Col other){
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
			mergeFormatting(other);
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
			mergeFormatting(other);
			
		}
		/** merge name and format from donor table
		 * unless this is already set locally
		 * 
		 * @param other
		 */
		public void mergeFormatting(Table<?,?>.Col other) {
			if (format == null) {
				if( other.format != null && other.format instanceof TransformFormatter) {
					TransformFormatter tf = (TransformFormatter) other.format;
					setFormat(tf.getTransform());
				}
			}
			mergeName(other);
		}
		/** merge name  from donor table
		 * unless this is already set locally
		 * 
		 * @param other
		 */
		public void mergeName(Table<?,?>.Col other) {
			if( name == null ) {
				setName(other.name);
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
				element_attr = new HashMap<>();
			}
			Map<String, String> my_attr = element_attr.get(key);
			if (my_attr == null) {
				my_attr = new HashMap<>();
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
				attr = new HashMap<>();
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
			Map<String, String> result = new HashMap<>();
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
			return new HashMap<>(data);
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
			return Double.valueOf(sum);
		}

		// Transform a column
		public void transform(Collection<R> e, Transform t) {

			for (R key : e) {
				Object value = data.get(key);
				data.put(key, t.convert(value));
			}
		}
		public void transform(Collection<R> e, Transform t,Col dest) {

			for (R key : e) {
				Object value = data.get(key);
				dest.put(key, t.convert(value));
			}
		}
		public void transform(Transform t) {
			// transforms hidden values as well note
			transform(data.keySet(), t);
		}
		public void transform(Transform t, Col dest) {
			// transforms hidden values as well note
			transform(data.keySet(), t,dest);
		}
		public boolean isEmpty() {
			return data.isEmpty();
		}
		/** If all values in the column
		 * indexed by the specified keys are the same return 
		 * that value otherwise null;
		 * @return
		 */
		public Object getCommon(Collection<R> keys) {
			Object result = null;
			for( R key : keys) {
				Object val = get(key);
				if( val == null) {
					// either all values are null or they differ either way
					// return null;
					return null;
				}
				if( result == null) {
					result = val;
				}else {
					if( ! result.equals(val)) {
						return null;
					}
				}
			}
			return result;
		}
		/** Do we want duplicated values to be shown as a single merged cell.
		 * This is a formatting hint and may be ignored.
		 * 
		 * @return
		 */
		public boolean isDedup() {
			return dedup;
		}
		/** Request that duplicated values be shown as a single merged cell.
		 * This is a formatting hint and may be ignored.
		 * 
		 * @param dedup
		 */
		public void setDedup(boolean dedup) {
			this.dedup = dedup;
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
		
		@Override
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
	 * If a Formatter only acts on the actual values it should probably be A {@link Transform}
	 * which can be embedded in a {@link TransformFormatter}
	 * 
	 * Note a Formatter can customise XML or UI generation by returning an
	 * object that implements either {@link XMLGenerator} or {@link UIGenerator}
	 * 
	 * @author spb
	 * @param <C> type of column key
	 * @param <R> type of row key
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
	 * @param <C> type of column key
	 * 
	 */
	public static class Sorter<C> implements Comparator {
		private C keys[];
		private int reverse = 1;
		private Comparator comp[];
		private Table<C,?> t;

		public Sorter(C sort_keys[], Comparator comp[], Table<C,?> tt,
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

		@Override
		@SuppressWarnings("unchecked")
		public int compare(Object o1, Object o2) {
			for (int i = 0; i < keys.length; i++) {
				Comparator cmp = null;
				if (comp != null && comp.length > i && comp[i] != null) {
					cmp = comp[i];
				}
				if( t.hasColumnGroup(keys[i])) {
					for(C k : t.getColumnGroup(keys[i])) {
						Table.Col c = t.getCol(k);
						
						int diff = compareCol(c, cmp, o1, o2);
						if( diff != 0) {
							return diff;
						}
					}
				}else {
					Table.Col c = t.getCol(keys[i]);
					
					int diff = compareCol(c, cmp, o1, o2);
					if( diff != 0) {
						return diff;
					}
				}
			}
			// order by keys if data the same
			return reverse * compareNonNull(null, o1, o2);
		}
		
		public int compareCol(Table.Col c, Comparator cmp, Object o1, Object o2) {
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
				if (cmp != null) {
					v = cmp.compare(oa, ob);
				} else {
					v = compareNonNull(c.format, oa, ob);
				}
				if (v != 0) {
					return reverse * v;
				}
			}
			return 0;
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
	 * @param <C> type of column key
	 * @param <R> type of row key
	 * 
	 */
	public static class TransformFormatter<C, R> implements Formatter<C, R> {
		private final Transform t;

		public TransformFormatter(Transform t) {
			this.t = t;
		}

	
		@Override
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
	
	/** Optional grouping of columns for formatting operations
	 * 
	 */
	private Map<C,Set<C>> column_groups = null;
	private Map<C,C> key_to_group = null;

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
	private boolean printGroups = false;
	private String id;
	public Table() {
		cols = new HashMap<>();
		row_keys = new LinkedList<>();
		col_keys = new LinkedList<>();

	}

	/**
	 * Construct a duplicate of the supplied table
	 * 
	 * @param t
	 */
	@SuppressWarnings("unchecked")
	public Table(Table<C, R> t) {
		cols = (HashMap<C, Col>) t.cols.clone();
		row_keys = new LinkedList<>(t.row_keys);
		col_keys = new LinkedList<>(t.col_keys);
		if (t.keyName != null) {
			keyName = t.keyName;
		}
		if (t.warnings != null) {
			warnings = new HashSet<>(t.warnings);
		}
		if (t.highlight != null) {
			highlight = new HashSet<>(t.highlight);
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
	/** Add contents of on existing table 
	 * 
	 * @param t
	 */
	public void add(Table<C, R> t) {
		addRows(t);
		for (C col : t.col_keys) {
			Col c = getCol(col);
			Col src = t.getCol(col);
			c.combine(Operator.ADD,src);
		}
	}
	

	/** Add contents from donor table transforming the row keys
	 * 
	 * @param key_transform {@link Transform} to map donor rows to local rows
	 * @param donor {@link Table}
	 */
	public <X> void addTable(Transform key_transform,Table<C,X> donor ) {
		for(X donor_row : donor.row_keys) {
			R new_row = (R) key_transform.convert(donor_row);
			for (C col : donor.col_keys) {
				Col dest = getCol(col);
				Object val = donor.get(col, donor_row);
				if( val != null) {
					if( val instanceof Number) {
						dest.combine(Operator.ADD, new_row, (Number) val);
					}else {
						dest.put(new_row, val);
					}
				}
			}
		}
	}
	/** Add contents from donor table taking the keys to use from a Column in
	 * the donor table. This can be used to create a table which merges rows
	 * based on the value in a col.
	 * The key_col is not imported from the donor.
	 * 
	 * @param key_col column in donor table containing row key values to use
	 * @param donor {@link Table}
	 */
	public <X> void addTable(C key_col,Table<C,X> donor ) {


		Map<X,R> mapping = (Map<X, R>) donor.getCol(key_col).data;
		for (C col : donor.col_keys) {
			Col dest = getCol(col);
			Table<C,X>.Col donor_col = donor.getCol(col);
			Formatter<C, X> format = donor_col.getFormat();
			if( format instanceof TransformFormatter) {
				dest.setFormat(((TransformFormatter)format).getTransform());
			}
			for(X donor_row : donor.row_keys) {
				R new_row = mapping.get(donor_row);
				if( new_row != null) {	
					Object val = donor.get(col, donor_row);
					if( val != null) {
						if( val instanceof Number) {
							dest.combine(Operator.ADD, new_row, (Number) val);
						}else {
							dest.put(new_row, val);
						}
					}
				}
			}
		}

	}
	/** Add rows (including formatting) from a table. 
	 * @param t
	 */
	public <X> void addRows(Table<X, R> t) {
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
	public void addCategoryTotals(C sum_col, C cat_Col, Transform cat_to_key,
			C label_col, Boolean highlight) {
		if (cat_Col == null || sum_col == null) {
			return;
		}
		
		// Unlike most formatting operations we don't add catagory totals unless the
		// column already exists
		Col category = cols.get(cat_Col);
		Col values = cols.get(sum_col);
		if (category == null ||  sum_col == null) {
			return;
		}
		if( values != null ) {
			addCategoryTotals(sum_col, cat_to_key, label_col, highlight, category, values);
		}else{
			for(C g : getColumnGroup(sum_col) ) {
				values = cols.get(g);
				if( values != null) {
					addCategoryTotals(g, cat_to_key, label_col, highlight, category, values);
				}
			}
		}
	}

	/**
	 * @param sum_col
	 * @param cat_to_key
	 * @param label_col
	 * @param highlight
	 * @param category
	 * @param values
	 */
	private void addCategoryTotals(C sum_col, Transform cat_to_key, C label_col, Boolean highlight, Col category,
			Col values) {
		// we insert category sum after last element of the category
		// so create a row order to define the list.
		HashMap<R, R> last = new HashMap<>();
		HashMap<R, Number> sums = new HashMap<>();
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
		}
		if (highlight != null) {
			for (R sumkey : last.keySet()) {
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

			@Override
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
		if( hasColumnGroup(col)) {
			for(C g : getColumnGroup(col)) {
				Col c = getCol(g);
				c.addAttribute(name, value);
			}
		}else {
			Col c = getCol(col);
			c.addAttribute(name, value);
		}
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
		if (sum == null || sum.isNaN() || sum.isInfinite() || sum.doubleValue() <= 0.0) {
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
						Double.valueOf(n.doubleValue() / sum.doubleValue()));
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
	
	/** Add a Map as a table row using the map keys
	 * to set the columns
	 * 
	 * @param key  Row key
	 * @param data  Row data
	 */
	public void addRow(R key, Map<C,Object> data) {
		for(Map.Entry<C, Object> e : data.entrySet()) {
			put(e.getKey(),key,e.getValue());
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
		if( hasColumnGroup(col_name)) {
			for(C g : getColumnGroup(col_name)) {
				Col c = getCol(g);
				c.addTotal(key);
			}
			return null;
		}else {
			Col c = getCol(col_name);
			return c.addTotal(key);
		}
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
		return new Vector<>(col_keys);
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

		Hashtable<String, Number> h = new Hashtable<>();

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
		return (List<C>) new LinkedList<>(col_keys);
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
		return (List<R>) new LinkedList<>(row_keys);
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
	/** Should column groups be shown as an additional level of column headers
	 * 
	 * @return
	 */
	public boolean printGroups() {
		return column_groups != null && ! column_groups.isEmpty() && printGroups;
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
		return new Vector<>(row_keys);
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
			if( col_keys.contains(after)) {
				col_keys.remove(after);
				int pos = col_keys.indexOf(first);

				col_keys.add(pos + 1, after);
			}else if (column_groups.containsKey(after)) {
				// add the entire group
				for(C after2 : getColumnGroup(after)) {
					setColAfter(first, after2);
				}
			}
		}
	}

	public Table.Formatter<C, R> setColFormat(C col_name,
			Table.Formatter<C, R> t) {

		if( hasColumnGroup(col_name)) {
			for(C g : getColumnGroup(col_name)) {
				Col c = getCol(g);
				c.setFormat(t);
			}
			return null;
		}else {
			// make col
			Col c = getCol(col_name);
			return c.setFormat(t);
		}
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
		col_keys = new LinkedList<>(v);
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
			highlight = new HashSet<>();
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

	public void setPrintGroups(boolean printGroups) {
		this.printGroups=printGroups;
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
			warnings = new HashSet<>();
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
	 * Sort columns according to a specified comparator
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
		// make sure that there is always some defined order
		// Indexed or hash order if nothing else.
		// Ideally we would de-refeference IndexedReferences here
		// but that would need an AppContext added to Table
		sortRows(new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				if( o1 instanceof Comparable && o2 instanceof Comparable) {
					return ((Comparable)o1).compareTo(o2);
				}else if( o1 instanceof Indexed && o2 instanceof Indexed) {
					return ((Indexed)o1).getID() - ((Indexed)o2).getID();
				}else {
					return o1.hashCode() - o2.hashCode();
				}
			}
			
		});

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
		sortRows(new Table.Sorter<>(keys, this, reverse));
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
		TextTableFormatter< C, R> fmt = new TextTableFormatter<>(null,this);
		fmt.add(sb);
		return sb.toString();
	}

	/** Apply a {@link Transform} to a column selected by name.
	 * 
	 * @param col_name
	 * @param t
	 */
	public void transformCol(C col_name, Transform t) {
		if( hasColumnGroup(col_name)) {
			for( C g : getColumnGroup(col_name)) {
				Col c = getCol(g);
				c.transform(row_keys, t);
			}
		}else {
			Col c = getCol(col_name);
			c.transform(row_keys, t);
		}
	}
	public void transformCol(C col_name,Transform t, C dest) {
		Col src_col  = getCol(col_name);
		Col dest_col = getCol(dest);
		src_col.transform(t, dest_col);
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
	/** Remove all rows based on values in a column
	 * row is removed if <b>col-value <i>Condition</i> value = true</b>
	 * @param col    Column to query
	 * @param cond   {@link MatchCondition} to compare
	 * @param value  value
	 */
	public <T>void thresholdRows(C col, MatchCondition cond, T value ){
		thresholdRows(col, cond, value, null);
	}
	/** Remove all rows based on values in a column
	 * row is removed if <b>col-value <i>Condition</i> value = true</b>
	 * @param col    Column to query
	 * @param cond   {@link MatchCondition} to compare
	 * @param merge  optional row to add numeric values from removed rows too
	 * @param value  value
	 */
	public <T>void thresholdRows(C col, MatchCondition cond, T value ,R merge){
		Col c  = getCol(col);
		if( c != null ) {
			Set<R> remove = new LinkedHashSet<>();
			for(R row : row_keys) {
				Object val = c.get(row);
				if( val != null) {
					if( cond == null ) {
						if( val.equals(value) ) {
							remove.add(row);
						}
					}else if( cond.compare(val, value)) {
						remove.add(row);
					}
				}
			}
			if( merge != null) {
				remove.remove(merge); // make sure we are not removing the destination
			}
			for(R row : remove) {
				if( merge != null) {
					for(Col c2 : cols.values()) {
						Number n = c2.getNumber(row);
						if( n != null) {
							c2.add(merge, n);
						}
					}
				}
				removeRow(row);
			}
			remove.clear();
		}
	}
	
	
	public C getGroup(C col) {
		if( key_to_group != null) {
			return key_to_group.get(col);
		}
		return null;
	}
	/** Add a column to a column-group
	 * 
	 * @param group
	 * @param col
	 * @throws InvalidArgument
	 */
	public void addToGroup(C group, C col) throws InvalidArgument {
		if( cols.containsKey(group) ) {
			throw new InvalidArgument("Bad group "+group.toString());
		}
		if( column_groups == null) {
			column_groups= new HashMap<>();
			key_to_group = new HashMap<>();
		}
		C old_group = key_to_group.get(col);
		if( old_group != null) {
			Set<C> old_g = column_groups.get(old_group);
			if( old_g != null) {
				old_g.remove(col);
			}
		}
		key_to_group.put(col, group);
		Set<C> g = column_groups.get(group);
		if( g == null ) {
			g = new LinkedHashSet<>();
			column_groups.put(group, g);
		}
		if( column_groups.containsKey(col)) {
			throw new InvalidArgument("Cannot add group to group");
		}
		g.add(col);
	}
	/** Get the columns belonging to a column groun
	 * 
	 * @param name  key for the column group
	 * @return
	 */
	public Iterable<C> getColumnGroup(C name){
		if( column_groups == null) {
			return new EmptyIterable<>();
		}
		Set<C> group = column_groups.get(name);
		if( group == null ) {
			return new EmptyIterable<>();
		}
		return Collections.unmodifiableSet(group);
	}
	/** get all the defined Column Groups
	 * 
	 * @return
	 */
	public Iterable<C> getColumnGroups(){
		if( column_groups == null ) {
			return new EmptyIterable<>();
		}
		return Collections.unmodifiableSet(column_groups.keySet());
	}
	public boolean hasColumnGroup(C name) {
		return column_groups != null && column_groups.containsKey(name);
	}
	/** place all members of a column group after the first member of the group.
	 * 
	 * If column names are generated dynamically then the first entry will
	 * be in the correct column position but subsequent rows may place additional 
	 * column names at the end. This operation packs them into a contiguous group.
	 * 
	 * 
	 * @param name
	 */
	public void packColumnGroup(C name) {
		if( hasColumnGroup(name)) {
			C prev = null;
			for(C x : getColumnGroup(name)) {
				if( prev != null) {
					setColAfter(prev, x);
				}
				prev = x;
			}
			
		}
	}
	/** For all column groups.
	 * place member columns after the first member of the group.
	 * 
	 * If column names are generated dynamically then the first entry will
	 * be in the correct column position but subsequent rows may place additional 
	 * column names at the end. This operation packs them into contiguous groups.
	
	 */
	public void packAllColumnGroups() {
		if( column_groups != null) {
			for(Set<C> s : column_groups.values()) {
				C prev = null;
				for(C x : s) {
					if( prev != null) {
						setColAfter(prev, x);
					}
					prev = x;
				}
			}
		}
	}
	/** Extract a single column table containing values that are the same in all rows of a 
	 * column. Each sutch column becomes a row of the extracted table and the column
	 * is removed.
	 * Method returns null if no columns are extracted or the table only has one row.
	 * 
	 * @return Table or null
	 */
	public Table<String,C> extractCommonColums(){
		if( nRows() < 2) {
			return null;
		}
		
		Table<String,C> result = new Table<String, C>();
		// we are going to be modifying the column list so
		// take a copy
		for( C c : new LinkedList<C>(col_keys)) {
			Col col = getCol(c);
			if( col.isEmpty()) {
				result.put("Value", c, null);
				removeCol(c);
			}else {
				Object common = col.getCommon(row_keys);
				if( common != null) {
					result.put("Value", c, common);
					removeCol(c);
				}
			}
		}
		if( result.hasData()) {
			return result;
		}
		return null;
	}
	
	
	private Object caption=null;
	/** get an object to use as a table caption
	 * 
	 * @return
	 */
	public Object getCaption() {
		return caption;
	}

	/** set an object to use as a table caption;
	 * 
	 * @param caption
	 */
	public void setCaption(Object caption) {
		this.caption = caption;
	}
}