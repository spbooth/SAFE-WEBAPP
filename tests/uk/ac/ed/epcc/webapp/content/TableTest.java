/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;




import org.junit.Test;


public class TableTest {

	@Test
	public void testTable() {
		Table t = new Table();
		assertFalse(t.hasData());
	}

	/**
	 * Tests the {@link Table#Table(Table)} constructor. 
	 */
	@Test
	public void testTableCopy() {
		// creates a new table
		Table<Integer, Double> origTable = new Table<Integer, Double>();
		Integer[] col_keys = {0, 1};
		Double[] row_keys = {0.0, -0.1};
		String[] values = {"testValue1", "testValue2"};
		origTable.put(col_keys[0], row_keys[0], values[0]);
		origTable.put(col_keys[1], row_keys[1], values[1]);
		// copies the table using the copy constructor
		Table<Integer, Double> copiedTable = new Table<Integer, Double>(origTable);
		// asserts that the copied table has the same keys and values
		assertEquals("Wrong number of rows.", 2, copiedTable.nRows());
		assertEquals("Wrong number of columns.", 2, copiedTable.nCols());
		assertEquals("Wrong value in copied table.", values[0], copiedTable.get(col_keys[0], row_keys[0]));
		assertEquals("Wrong value in copied table.", values[1], copiedTable.get(col_keys[1], row_keys[1]));
	}
	
	/**
	 * Tests the {@link Table#add(Table)} method.
	 */
	@Test
	public void testAddTable() {
		// creates two new tables
		// table1
		Table<String, Integer> table1 = new Table<String, Integer>();
		table1.put("Col1", 1, "value1");
		// table2
		Table<String, Integer> table2 = new Table<String, Integer>(table1);
		// adds table2 to table1
		table1.add(table2);
		// asserts table1 structure
		assertEquals("Wrong number of rows.", 1, table1.nRows());
		assertEquals("Wrong number of columns.", 1, table1.nCols());
		assertEquals("Wrong value in table1.", "value1", table1.get("Col1", 1));
		// replaces value in table2
		table2.put("Col1", 1, "value2");
		// adds table2 to table1
		table1.add(table2);
		// asserts table1 value
		assertEquals("Wrong value in table1.", "value2", table1.get("Col1", 1));
		// adds new row to table2
		table2.put("Col1", 2, "value3");
		// adds table2 to table1
		table1.add(table2);
		// asserts table1 structure
		assertEquals("Wrong number of rows.", 2, table1.nRows());
		assertEquals("Wrong number of columns.", 1, table1.nCols());
		assertEquals("Wrong value in table1.", "value2", table1.get("Col1", 1));
		assertEquals("Wrong value in table1.", "value3", table1.get("Col1", 2));
	}
	
	/**
	 * Tests the {@link Table#addAttribute(Object, Object, String, String)} method.
	 */
	@Test
	public void testAddAttribute() {
		Table<Integer, Integer> t = new Table<Integer, Integer>();
		t.addAttribute(0, 1, "test_name", "test_value");
		assertEquals("Wrong single cell attribute.", "test_value", t.getAttributes(0, 1).get("test_name"));
	}
	
	/**
	 * Tests the {@link Table#addColAttribute(Object, String, String)} method.
	 */
	@Test
	public void testAddColAttribute() {
		Table<String, Integer> t = new Table<String, Integer>();
		t.put("col1", 1, "value1");
		t.put("col1", 2, "value2");
		t.addColAttribute("col1", "test_attr_name", "test_attr_value");
		assertEquals("Wrong single cell attribute.", "test_attr_value", t.getAttributes("col1", 1).get("test_attr_name"));
		assertEquals("Wrong single cell attribute.", "test_attr_value", t.getAttributes("col1", 2).get("test_attr_name"));
	}
	
	/**
	 * Tests the {@link Table#addMap(Object, Map)} method.
	 */
	@Test
	public void testAddMap() {
		Table<String, Integer> t = new Table<String, Integer>();
		Map<Integer, Object> m = new HashMap<Integer, Object>();
		m.put(1, "value1");
		m.put(2,  2);
		t.addMap("col1", m);
		// asserts the table structure
		assertEquals("Wrong number of rows.", 2, t.nRows());
		assertEquals("Wrong number of columns.", 1, t.nCols());
		assertEquals("Wrong value in table.", "value1", t.get("col1", 1));
		assertEquals("Wrong value in table.", 2, t.get("col1", 2));
	}
	
	/**
	 * Tests the {@link Table#addPercentCol(Object, Object)} method.
	 */
	@Test
	public void testAddPercentCol() {
		Table<String, Integer> t = new Table<String, Integer>();
		t.put("col1", 1, 20);
		t.put("col1", 2, 40);
		t.put("col1", 3, 40);
		t.addPercentCol("col1", "col1_percent");
		// assert the values in the newly added column
		assertEquals("Wrong percentage in added column.", 0.2, t.get("col1_percent", 1));
		assertEquals("Wrong percentage in added column.", 0.4, t.get("col1_percent", 2));
		assertEquals("Wrong percentage in added column.", 0.4, t.get("col1_percent", 3));
	}
	
	/**
	 * Tests the {@link Table#addTotalToCol(Object, Object)} and
	 * {@link Table#addTotalToCol(int, Object)} methods.
	 */
	@Test
	public void testAddTotalToCol() {
		Table<String, Integer> t = new Table<String, Integer>();
		t.put("col1", 1, 20);
		t.put("col1", 2, 40);
		t.put("col1", 3, 40);
		t.addTotalToCol("col1", 4);
		assertEquals("Wrong total.", 100.0, t.get("col1", 4));
		// removes row with total
		t.removeRow(4);
		// adds total again using the other method
		t.addTotalToCol(0, 4);
		assertEquals("Wrong total.", 100.0, t.get("col1", 4));
	}
	
	/**
	 * Tests the {@link Table#colOperation(Object, Operator, Object, Object)} method.
	 */
	@Test
	public void testColOperation() {
		Table<String, Integer> t = new Table<String, Integer>();
		t.put("col1", 1, 20);
		t.put("col1", 2, 30);
		t.put("col2", 1, 0);
		t.put("col2", 2, -30);
		// test ADD
		t.colOperation("colADD", Operator.ADD, "col1", "col2");
		assertEquals("Wrong colOperation ADD.", 20, t.get("colADD", 1));
		assertEquals("Wrong colOperation ADD.", 0, t.get("colADD", 2));
		// test SUB
		t.colOperation("colSUB", Operator.SUB, "col1", "col2");
		assertEquals("Wrong colOperation SUB.", 20, t.get("colSUB", 1));
		assertEquals("Wrong colOperation SUB.", 60, t.get("colSUB", 2));
		// test MUL
		t.colOperation("colMUL", Operator.MUL, "col1", "col2");
		assertEquals("Wrong colOperation MUL.", 0, t.get("colMUL", 1));
		assertEquals("Wrong colOperation MUL.", -900, t.get("colMUL", 2));
		// test DIV
		t.colOperation("colDIV", Operator.DIV, "col1", "col2");
		assertEquals("Wrong colOperation DIV.", Double.POSITIVE_INFINITY, t.get("colDIV", 1));
		assertEquals("Wrong colOperation DIV.", -1.0, t.get("colDIV", 2));
		// test not numbers
		Table<String, String> t2 = new Table<String, String>();
		t2.put("col1", "row1", "value");
		t2.put("col2", "row1", "value");
		t2.colOperation("result_col", Operator.ADD, "col1", "col2");
		assertEquals("Wrong colOperation for strings.", "value", t2.get("result_col", "row1"));
	}
	
	/**
	 * Tests the {@link Table#rowOperation(Object, Operator, Object, Object)} method.
	 */
	@Test
	public void testRowOperation() {
		Table<Integer, String> t = new Table<Integer, String>();
		t.put(1, "row1", 5);
		t.put(2, "row1", 50);
		t.put(1, "row2", 2);
		t.put(2, "row2", 20);
		// test ADD
		t.rowOperation("rowADD", Operator.ADD, "row1", "row2");
		assertEquals("Wrong rowOperation ADD.", 7, t.get(1, "rowADD"));
		assertEquals("Wrong rowOperation ADD.", 70, t.get(2, "rowADD"));
		// test SUB
		t.rowOperation("rowSUB", Operator.SUB, "row1", "row2");
		assertEquals("Wrong rowOperation SUB.", 3, t.get(1, "rowSUB"));
		assertEquals("Wrong rowOperation SUB.", 30, t.get(2, "rowSUB"));
		// test MUL
		t.rowOperation("rowMUL", Operator.MUL, "row1", "row2");
		assertEquals("Wrong rowOperation MUL.", 10, t.get(1, "rowMUL"));
		assertEquals("Wrong rowOperation MUL.", 1000, t.get(2, "rowMUL"));
		// test DIV
		t.rowOperation("rowDIV", Operator.DIV, "row1", "row2");
		assertEquals("Wrong rowOperation DIV.", 2.5, t.get(1, "rowDIV"));
		assertEquals("Wrong rowOperation DIV.", 2.5, t.get(2, "rowDIV"));
		// test not numbers
		Table<String, String> t2 = new Table<String, String>();
		t2.put("col1", "row1", "value");
		t2.put("col1", "row2", "value");
		t2.rowOperation("result_row", Operator.SUB, "row1", "row2");
		assertEquals("Wrong rowOperation for strings.", "value", t2.get("col1", "result_row"));
	}
	
	/**
	 * Tests the {@link Table#containsCol(Object)} method.
	 */
	@Test
	public void testContainsCol() {
		Table<String, Integer> t = new Table<String, Integer>();
		t.getCol("testCol");
		assertTrue("Wrong value of containsCol.", t.containsCol("testCol"));
	}
	
	/**
	 * Tests the {@link Table#removeCol(Object)} method.
	 */
	@Test
	public void testRemoveCol() {
		Table<String, Integer> t = new Table<String, Integer>();
		t.getCol("col1");
		t.getCol("col2");
		t.removeCol("col1");
		assertFalse("Bug in removeCol operation.", t.containsCol("col1"));
	}
	
	/**
	 * Tests the {@link Table#sortCols(Comparator)} method.
	 */
	@Test
	public void testSortCols() {
		Table<String, Integer> t = new Table<String, Integer>();
		t.getCol("z");
		t.getCol("N");
		t.getCol("a");
		t.sortCols(String.CASE_INSENSITIVE_ORDER);
		assertEquals("Wrong column order.", 0, t.getColumNames().get(0).compareTo("a"));
		assertEquals("Wrong column order.", 0, t.getColumNames().get(1).compareTo("N"));
		assertEquals("Wrong column order.", 0, t.getColumNames().get(2).compareTo("z"));
	}
	
	/**
	 * Tests the {@link Table#sumCol(Object)} method.
	 */
	@Test
	public void testSumCol() {
		Table<String, Integer> t = new Table<String, Integer>();
		t.put("col1", 1, 100);
		t.put("col1", 2, "abc");
		t.put("col1", 3, 200);
		assertEquals("Wrong sumCol.", 300.0, t.sumCol("col1"), 0.0);
	}
	
	/**
	 * Tests the {@link Table#setWarning(Object, boolean)} and {@link Table#getWarning(Object)} methods. 
	 */
	@Test
	public void testWarning() {
		Table<String, Integer> t = new Table<String, Integer>();
		t.put("col1", 1, 100);
		t.put("col1", 2, -100);
		t.put("col1", 3, 100);
		t.setWarning(2, true);
		assertTrue("Wrong warning flag.", t.getWarning(2));
	}
	
	/**
	 * Tests the {@link Table#setHighlight(Object, boolean)} and {@link Table#getHighlight(Object)} methods.
	 */
	@Test
	public void testHighlight() {
		Table<String, Integer> t = new Table<String, Integer>();
		t.put("col1", 1, 100);
		t.put("col1", 2, -100);
		t.put("col1", 3, 100);
		t.setHighlight(1, true);
		t.setHighlight(3, true);
		assertTrue("Wrong highlight flag.", t.getHighlight(1));
		assertTrue("Wrong highlight flag.", t.getHighlight(3));
		t.setHighlight(1, false);
		t.setHighlight(3, false);
		assertFalse("Wrong highlight flag.", t.getHighlight(1));
		assertFalse("Wrong highlight flag.", t.getHighlight(3));
	}
	
	/**
	 * Tests the {@link Table#increment(Object, Object, int)} method.
	 */
	@Test
	public void testIncrement() {
		Table<String, Integer> t = new Table<String, Integer>();
		t.put("col1", 1, 100);
		t.increment("col1", 1, 10);
		assertEquals("Wrong increment.", 110, t.get("col1",  1));
	}
	
	/**
	 * Tests the {@link Table#setColAfter(Object, Object)} method.
	 */
	@Test
	public void testSetColAfter() {
		Table<String, Integer> t = new Table<String, Integer>();
		t.getCol("col1");
		t.getCol("col2");
		t.getCol("col3");
		t.setColAfter("col3", "col1");
		// expected column order col2, col3, col1
		assertEquals("Wrong column order.", 0, t.getColumNames().get(0).compareTo("col2"));
		assertEquals("Wrong column order.", 0, t.getColumNames().get(1).compareTo("col3"));
		assertEquals("Wrong column order.", 0, t.getColumNames().get(2).compareTo("col1"));
	}
	
	/**
	 * Tests the {@link Table#transformCol(Object, uk.ac.ed.epcc.webapp.content.Table.SetRangeMapper)}
	 * and {@link Table#transformCol(int, uk.ac.ed.epcc.webapp.content.Table.SetRangeMapper)} methods.
	 */
	@Test
	public void testTransformCol() {
		Table<String, Object> t = new Table<String, Object>();
		// tests the Default transform
		t.put("col1", 1, null);
		t.transformCol("col1", new DefaultTransform(333));
		assertEquals("Wrong Default transform.", 333, t.get("col1", 1));
		// tests the BlankTransform
		t.transformCol(0, new BlankTransform());
		assertEquals("Wrong BlankTransform.", "", t.get("col1", 1));
	}
	
	/**
	 * Tests the {@link Table#transformKeys(Object, uk.ac.ed.epcc.webapp.content.Table.SetRangeMapper)} method.
	 */
	@Test
	public void testTransformKeys() {
		Table<String, String> t = new Table<String, String>();
		t.put("col1", null, 0);
		t.put("col1", "row2", 0);
		t.transformKeys("col2", new DefaultTransform("row1"));
		assertEquals("Wrong transformed key.", "row1", t.get("col2", null));
		assertEquals("Wrong transformed key.", "row2", t.get("col2", "row2"));
	}
	
	/**
	 * Tests the {@link Table#setRow(Object, int)} method.
	 */
	@Test
	public void testSetRow() {
		Table<String, String> t = new Table<String, String>();
		t.put("col1", "row1", 100);
		t.put("col1", "row2", 200);
		t.setRow("row2", 0);
		assertEquals("Wrong row order.", "row2", t.getRows().get(0));
		assertEquals("Wrong row order.", "row1", t.getRows().get(1));
	}
	
	/**
	 * Tests the {@link Table#setRowAfter(Object, Object)} method.
	 */
	@Test
	public void testSetRowAfter() {
		Table<String, String> t = new Table<String, String>();
		t.put("col1", "row1", 100);
		t.put("col1", "row2", 200);
		t.setRowAfter("row1", "row2");
		assertEquals("Wrong row order.", "row2", t.getRows().get(0));
		assertEquals("Wrong row order.", "row1", t.getRows().get(1));
	}
	
	/**
	 * Tests the {@link Table#setRowLast(Object)} method.
	 */
	@Test
	public void testSetRowLast() {
		Table<String, String> t = new Table<String, String>();
		t.put("col1", "row1", 100);
		t.put("col1", "row2", 200);
		t.setRowLast("row1");
		assertEquals("Wrong row order.", "row2", t.getRows().get(0));
		assertEquals("Wrong row order.", "row1", t.getRows().get(1));
	}
	
	/**
	 * Tests the {@link Table#rowToString(Object)} method.
	 */
	@Test
	public void testRowToString() {
		Table<String, String> t = new Table<String, String>();
		t.put("col1", "row1", 100);
		t.put("col2", "row1", 200);
		t.rowToString("row1");
		assertTrue("String conversion did not work.", t.get("col1", "row1") instanceof String);
	}
	
	/**
	 * Tests the {@link Table#setCol(Object, int)} method.
	 */
	@Test
	public void testSetCol() {
		Table<String, String> t = new Table<String, String>();
		t.put("col1", "row1", 100);
		t.put("col2", "row1", 200);
		t.setCol("col1", 1);
		assertEquals("Wrong column order.", "col2", t.getColumNames().get(0));
		assertEquals("Wrong column order.", "col1", t.getColumNames().get(1));
	}
	
	/**
	 * Tests the {@link Table#setColLast(Object)} method.
	 */
	@Test
	public void testSetColLast() {
		Table<String, String> t = new Table<String, String>();
		t.put("col1", "row1", 100);
		t.put("col2", "row1", 200);
		t.setColLast("col1");
		assertEquals("Wrong column order.", "col2", t.getColumNames().get(0));
		assertEquals("Wrong column order.", "col1", t.getColumNames().get(1));
	}
	
	/**
	 * Tests the {@link Table#setColName(Object, String)} method.
	 */
	@Test
	public void testSetColName() {
		Table<String, String> t = new Table<String, String>();
		t.getCol("col1");
		t.setColName("col1", "col1_name");
		assertEquals("Wrong column name.", "col1_name", t.getCol("col1").getName());
	}
	
	/**
	 * Tests the {@link Table#setColumns(java.util.List)} method.
	 */
	@Test
	public void testSetColumns() {
		Table<String, String> t = new Table<String, String>();
		LinkedList<String> columns = new LinkedList<String>();
		columns.add("col1");
		t.setColumns(columns);
		assertEquals("Wrong column name.", "col1", t.getColumNames().get(0));
		assertTrue("Missing column.", t.hasCol("col1"));
		assertTrue("Missing column.", t.containsCol("col1"));
		assertEquals("Wrong numbner of columns.", 1, t.nCols());
	}
	
	/**
	 * Tests the {@link Table#hasRow(Object)} method.
	 */
	@Test
	public void testHasRow() {
		Table<String, String> t = new Table<String, String>();
		t.put("col1", "row1", 100);
		assertTrue("Missing row.", t.hasRow("row1"));
		t.removeRow("row1");
		assertFalse("Bug in removeRow method.", t.hasRow("row1"));
	}
	
	/**
	 * Tests the methods {@link Table#setKeyTransform(Transform)} and {@link Table#getKeyText(Object)}.
	 */
	@Test
	public void testSetKeyTransform() {
		Table<String, String> t = new Table<String, String>();
		t.put("col1", "row1", 100);
		t.put("col1", "row2", 200);
		Hashtable<String,String> keyToKeyTextMap = new Hashtable<String,String>();
		keyToKeyTextMap.put("row1", "row1_label");
		keyToKeyTextMap.put("row2", "row2_label");
		t.setKeyTransform(new MapTransform(keyToKeyTextMap));
		assertEquals("row1_label", t.getKeyText("row1"));
		assertEquals("row2_label", t.getKeyText("row2"));
	}
	
	/**
	 * Tests the method {@link Table#getString()}.
	 */
	@Test
	public void testGetString() {
		Table<String, String> t = new Table<String, String>();
		t.put("col1", "row1", 100);
		t.put("col1", "row2", 200);
		Hashtable<String,String> keyToKeyTextMap = new Hashtable<String,String>();
		keyToKeyTextMap.put("row1", "row1_label");
		keyToKeyTextMap.put("row2", "row2_label");
		t.setKeyTransform(new MapTransform(keyToKeyTextMap));
		t.setKeyName("label_names");
		assertEquals("row1_label\t100\t\nrow2_label\t200\t\n", t.getString());
	}
	
	@Test
	public void testAddCategoryTotalToCol() {
		Table<String,Object> t = new Table<String,Object>();
		t.put("Cat", 1, "A");
		t.put("Val",1 ,2.0);
		t.put("Cat", 2, "A");
		t.put("Val",2 ,3.0);
		t.put("Cat", 3, "B");
		t.put("Val",3 ,4.0);
		t.put("Cat", 4, "B");
		t.put("Val",4 ,5.0);
		Hashtable<String,String> sel = new Hashtable<String,String>();
		sel.put("A","SumA");
		sel.put("B","SumB");
		t.addCategoryTotalToCol("Val", "Cat", sel);
		assertEquals(new Double(5.0), t.get("Val","SumA"));
		assertEquals(new Double(9.0), t.get("Val","SumB"));
	}

	@Test
	public void testhasData(){
		Table<String,String> t = new Table<String,String>();
		assertFalse(t.hasData());
		t.put("Value","A",3.0);
		Number n = t.getNumber("Value", "A");
		assertEquals(n.doubleValue(), 3.0, 0.0);
		assertTrue(t.hasData());
	}
	
	@Test
	public void testGetHashTable() throws InvalidArgument{
		
		Table<String,Integer> t = new Table<String,Integer>();
		
		for( int i=0;i<10;i++){
			t.put("lab",i,"value-"+i);
			t.put("val",i,i);
		}
		t.put("lab",11,"junk");
		t.put("val",11,"junk");
		Map<String,Number> simple = t.getHashtable("val"); // should use lag as key
		Map<String,Number> comp = t.getHashtable("val", "lab");
		
		t.setKeyName("key");
		Map<String,Number> keymap = t.getHashtable("val"); // should use key values
		assertEquals(10, simple.size());
		assertEquals(10, comp.size());
		assertEquals(10, keymap.size());
		for(int i=0;i<10;i++){
			String lab = "value-"+i;
			
			assertEquals(i, simple.get(lab).intValue());
			assertEquals(i,comp.get(lab).intValue());
			assertEquals(i,keymap.get(""+i).intValue());
		}
		
	}
	
	@Test
	public void testSortRows(){
		Table<String,String> t = new Table<String,String>();
		t.put("Data","C", Long.valueOf(1000L));
		t.put("Data","D", Long.valueOf(1000L));
		t.put("Data","A", Double.valueOf(10.0));
		t.put("Data","B", Integer.valueOf(1));
		t.setKeyName("Key");
		t.sortRows();
		Iterator e = t.getRows().iterator();
		
		assertEquals("A", e.next());
		assertEquals("B", e.next());
		assertEquals("C", e.next());
		assertEquals("D", e.next());
		
		//System.out.println(t.getHTML());
	}
	
	@Test
	public void testSortRowsWithComparator(){
		Table<String,String> t = new Table<String,String>();
		t.put("Data","C", Long.valueOf(1000L));
		t.put("Data","D", Long.valueOf(1000L));
		t.put("Data","A", Double.valueOf(10.0));
		t.put("Data","B", Integer.valueOf(1));
		t.sortRows(new Comparator<String>() {

			public int compare(String o1, String o2) {
				return - o1.compareTo(o2);
			}
		});
		Iterator e = t.getRows().iterator();
		
		assertEquals("D", e.next());
		assertEquals("C", e.next());
		assertEquals("B", e.next());
		assertEquals("A", e.next());
		
	}
	
	@Test
	public void testMixedSort(){
		Table<String,String> t = new Table<String,String>();
		t.put("Data","A", Double.valueOf(10.0));
		t.put("Data","B", Integer.valueOf(1));
		t.put("Data","C", Long.valueOf(1000L));
		t.put("Data","D", Long.valueOf(1000L));
		t.put("Data2","A", Double.valueOf(1.0));
		t.put("Data2","B", Integer.valueOf(10));
		t.put("Data2","C", Long.valueOf(1000L));
		t.put("Data2","D", Long.valueOf(999L));
		t.sortRows(new String[] {"Data","Data2"}, false);
		System.out.println(t.toString());
		Iterator e = t.getRows().iterator();
		
		assertEquals("B", e.next());
		assertEquals("A", e.next());
		assertEquals("D", e.next());
		assertEquals("C", e.next());
		
		t.sortRows(new String[] {"Data","Data2"}, true);
		e = t.getRows().iterator();
		
		assertEquals("C", e.next());
		assertEquals("D", e.next());
		assertEquals("A", e.next());
		assertEquals("B", e.next());
	}
	
	/**
	 * Tests the method {@link Table#setCategoryRow(Object, Object, Transform, Transform)}.
	 */
	@Test
	public void testSetCategoryRow() {
		Table<String, Object> t = new Table<String, Object>();
		t.put("Cat", 1, "A");
		t.put("Val", 1, 2.0);
		t.put("Cat", 2, "A");
		t.put("Val", 2, 3.0);
		t.put("Cat", 3, "B");
		t.put("Val", 3, 4.0);
		t.put("Cat", 4, "B");
		t.put("Val", 4, 5.0);
		Hashtable<String,String> sel = new Hashtable<String,String>();
		sel.put("A", "CatA");
		sel.put("B", "CatB");
		Transform cat_to_key = new MapTransform(sel);
		Hashtable<String,String> sel2 = new Hashtable<String,String>();
		sel2.put("A", "CatA extra text");
		sel2.put("B", "CatB extra text");		
		Transform cat_to_val = new MapTransform(sel2);
		t.setCategoryRow("Val", "Cat", cat_to_key, cat_to_val);
		assertEquals("CatA extra text", t.get("Val", "CatA"));
		assertEquals("CatB extra text", t.get("Val", "CatB"));
	}
	
	/**
	 * Tests the printing of keys.
	 */
	@Test
	public void testPrintKeys() {
		Table<String, String> t = new Table<String, String>();
		t.put("col1", "row1", 100);
		t.setKeyName("keys_column");
		assertEquals("keys_column \tcol1\t\n\nrow1:\t100\t\n", t.toString());
	}
	
	/**
	 * Tests the method {@link Table#setFormat(Transform)}.
	 */
	@Test
	public void testSetFormat() {
		Table<String, String> t = new Table<String, String>();
		t.put("col1", "row1", 0.123);
		NumberFormatTransform f = new NumberFormatTransform(NumberFormat.getIntegerInstance(), "N/A");
		t.setFormat(f);
		assertEquals("true", t.getAttributes("col1", "row1").get("numeric"));
		Table<String, String>.Col col1 = t.getCol("col1");
		assertTrue(col1.hasFormat());
		assertEquals("0", col1.getText("row1"));
		assertEquals("N/A", col1.getText("row2"));
	}
	
	/**
	 * Tests the class {@link Table.NumberFormatGenerator}.
	 */
	@Test
	public void testNumberFormatGenerator() {
		Table.NumberFormatGenerator nfg1 = new Table.NumberFormatGenerator(NumberFormat.getIntegerInstance(), null);
		assertEquals("0", nfg1.toString());
		XMLPrinter printer = new XMLPrinter();
		printer.open("tag1");
		nfg1.addContent(printer);
		printer.close();
		assertEquals("<tag1 numeric='true' null='true'>0</tag1>", printer.toString());
		Table.NumberFormatGenerator nfg2 = new Table.NumberFormatGenerator(NumberFormat.getIntegerInstance(), 1.234);
		assertEquals("1", nfg2.toString());
		printer.clear();
		printer.open("tag2");
		nfg2.addContent(printer);
		printer.close();
		assertEquals("<tag2 numeric='true'>1</tag2>", printer.toString());
	}
	
	/**
	 * Tests the method {@link Table.Col#printWidth()}.
	 */
	@Test
	public void testPrintWidth() {
		Table<String, Object> t = new Table<String, Object>();
		t.put("col1", "row1", 0.123);
		t.put("col1", "row2", "A");
		Table<String, Object>.Col col1 = t.getCol("col1");
		assertEquals(5,  col1.printWidth());
	}
	
	/**
	 * Tests the class {@link FormatDateTransform}.
	 */
	@Test
	public void testFormatDate() {
		FormatDateTransform fd = new FormatDateTransform(DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK));
		assertNull(fd.convert(null));
		assertEquals(123, fd.convert(123));
		assertEquals("01/01/70", fd.convert(new Date(0)));
	}
	
	/**
	 * Tests the method {@link Table#addCategoryTotalToCol(Object, Object)}.
	 */
	@Test
	public void testAddCategoryTotalToCol2() {
		Table<String, String> t = new Table<String, String>();
		t.put("ValueCol", "row1", 100);
		t.put("CategoryCol", "row1", "POSITIVE");
		t.put("ValueCol", "row2", -200);
		t.put("CategoryCol", "row2", "NEGATIVE");
		t.put("ValueCol", "row3", 300);
		t.put("CategoryCol", "row3", "POSITIVE");
		t.addCategoryTotalToCol("ValueCol", "CategoryCol");
		assertEquals(400, t.get("ValueCol", "POSITIVE Total"));
		assertEquals(-200, t.get("ValueCol", "NEGATIVE Total"));
	}
	
	/**
	 * Tests the method {@link Table#addPercentCol(Object, Object, NumberFormat)}.
	 */
	@Test
	public void testAddPercentCol2() {
		Table<String, String> t = new Table<String, String>();
		t.put("col1", "row1", 60);
		t.put("col1", "row2", 40);
		t.addPercentCol("col1", "col1percent", NumberFormat.getPercentInstance());
		assertTrue(t.hasColFormat("col1percent"));
		assertFalse(t.hasColFormat("col1"));
		assertEquals("60%", t.getCol("col1percent").getText("row1"));
		assertEquals("40%", t.getCol("col1percent").getText("row2"));
	}

	/**
	 * Tests the method {@link Table.Col#transform(Transform)}.
	 */
	@Test
	public void testTransform() {
		Table<String, String> t = new Table<String, String>();
		t.put("col1", "row1", 10);
		t.put("col1", "row2", 20);
		NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(Locale.UK);
		t.getCol("col1").transform(new NumberFormatTransform(currencyInstance));
		assertEquals(currencyInstance.getCurrency().getSymbol(Locale.UK)+"10.00", t.get("col1", "row1"));
		assertEquals(currencyInstance.getCurrency().getSymbol(Locale.UK)+"20.00", t.get("col1", "row2"));
	}
	
	/**
	 * Tests the method {@link Table#setColFormat(Object, Transform)}.
	 */
	@Test
	public void testSetColFormat() {
		Table<String, String> t = new Table<String, String>();
		t.put("col1", "row1", 10);
		t.put("col1", "row2", 20);
		NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(Locale.UK);
		t.setColFormat("col1", new NumberFormatTransform(currencyInstance));
		assertEquals(currencyInstance.getCurrency().getSymbol(Locale.UK)+"10.00", t.getCol("col1").getText(null, "row1"));
		assertEquals(currencyInstance.getCurrency().getSymbol(Locale.UK)+"20.00", t.getCol("col1").getText(null, "row2"));
	}
	
	/**
	 * Tests the method {@link Table.Col#add(Object, Number)}.
	 */
	@Test
	public void testAdd() {
		Table<String, String> t = new Table<String, String>();
		t.put("col1", "row1", 10);
		t.put("col1", "row2", null);
		t.getCol("col1").add("row2", 20);
		assertEquals(20, t.get("col1", "row2"));
		t.getCol("col1").add("row2", 100);
		assertEquals(120, t.get("col1", "row2"));
	}
}
