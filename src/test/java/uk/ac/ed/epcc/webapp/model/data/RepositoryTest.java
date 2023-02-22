//| Copyright - The University of Edinburgh 2015                            |
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
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.DateInput;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.BlobType;
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.LongFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
import uk.ac.ed.epcc.webapp.model.data.Repository.IdMode;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;


public class RepositoryTest extends WebappTestBase {
	
	Repository res;
	


	
	@Before
	public void setUp() throws DataFault {
		TableSpecification spec = new TableSpecification("TestID");
		spec.setField("Name", new StringFieldType(true, "", 32));
		spec.setField("Longtext", new StringFieldType(true, null, 1000000000));
		spec.setField("Number", new IntegerFieldType(true, 0));
		spec.setField("Date", new DateFieldType(true, null));
		spec.setField("TruncatedDate", new DateFieldType(true, null,true));
		spec.setField("UnsignedInt", new LongFieldType(true, null));
		spec.setField("PersonID",new ReferenceFieldType("Person"));
		spec.setField("Boolean", new BooleanFieldType(false, true));
		spec.setField("Blob", new BlobType());
		try{
		spec.new Index("name_index", true, "Name");
		spec.new Index("number_date_index",false,"Number","Date");
		}catch(InvalidArgument e){
			throw new ConsistencyError("unexpected exception", e);
		}
		DataBaseHandlerService serv = ctx.getService(DataBaseHandlerService.class);
		serv.createTable("Test", spec);
		res = Repository.getInstance(ctx,"Test");
	}

	@Test
	public void testHasIndex() {
		assertTrue(res.hasIndex("name_index"));
		assertFalse(res.hasIndex("boris"));
	}
	@Test
	public void testIsUniqueName() {
		assertTrue(res.isUniqueIdName("TestID"));
		assertFalse(res.isUniqueIdName("Boris"));
	}
	/*
	 * Test method for 'uk.ac.ed.epcc.webapp.model.data.Repository.getUniqueIdName()'
	 */
	@Test
	public void testGetUniqueIdName() {
		assertEquals("TestID",res.getUniqueIdName());
	}

	@Test
	public void testCached() throws DataException{
		TableSpecification spec = new TableSpecification("CachedID");
		spec.setField("Name", new StringFieldType(true, "", 32));
		spec.setField("Number", new IntegerFieldType(true, 0));
		DataBaseHandlerService serv = ctx.getService(DataBaseHandlerService.class);
		serv.createTable("cached", spec);
		Repository cres = Repository.getInstance(ctx,"cached");
		
		assertTrue(cres.useID());
		assertTrue(cres.usesCache());
		
		
		Record rec = cres.new Record();
		rec.setProperty("Name", "Scroodge");
		rec.setProperty("Number", 12);
		rec.commit();
		
		int id = rec.getID();
		assertFalse(cres.isCached(id)); // cache only allocates on read
		
		Record copy = cres.new Record();
		Record copy2 = cres.new Record();
		copy.setID(id);
		assertTrue(cres.isCached(id));
		copy2.setID(id);
		assertTrue(cres.isCached(id));
		assertEquals("Scroodge", copy.getStringProperty("Name"));
		assertEquals(12, copy.getNumberProperty("Number"));
		assertEquals("Scroodge", copy2.getStringProperty("Name"));
		assertEquals(12, copy2.getNumberProperty("Number"));
		copy.setProperty("Number", 14);
		copy.commit();
		assertFalse(cres.isCached(id));
		assertEquals(12, rec.getNumberProperty("Number"));
		assertEquals(12, copy2.getNumberProperty("Number"));
		Record second = cres.new  Record();
		second.setID(id);
		assertEquals(14,second.getNumberProperty("Number"));
		assertTrue(cres.isCached(id));
		cres.clearCache();
		assertFalse(cres.isCached(id));
	}
	
	
	/*
	 * Test method for 'uk.ac.ed.epcc.webapp.model.data.Repository.getFields()'
	 */
	@Test
	public void testGetFields() {
		Set fields = res.getFields();
		assertEquals(fields.size(),9);
		Iterator it = fields.iterator();
		assertEquals(it.next(),"Name");
		assertEquals(it.next(),"Longtext");
		assertEquals(it.next(),"Number");
		assertEquals(it.next(),"Date");
		assertEquals(it.next(),"TruncatedDate");
		assertEquals(it.next(),"UnsignedInt");
		assertEquals(it.next(),"PersonID");
		assertEquals(it.next(),"Boolean");
		assertEquals(it.next(),"Blob");
		assertFalse(it.hasNext());
	}
	
	@Test
	public void testClearFields() {
		res.getFields();
		res.clearFields();
		assertFalse(res.hasMetaData());
		
		
		res.getInfo();
		assertTrue(res.hasMetaData());
		res.clearFields();
		assertFalse(res.hasMetaData());
		
		
		res.getInfo("Name");
		assertTrue(res.hasMetaData());
		res.clearFields();
		assertNull(res.getInfo(null));
		assertFalse(res.hasMetaData());
	}
	
	@Test
	public void TestRepositoryEquals() {
		Repository res1 = Repository.getInstance(ctx,"Snickers");
		Repository res2 = Repository.getInstance(ctx,"Bounty");
		Repository res3 = Repository.getInstance(ctx,"Bounty");
		
		assertFalse(res1.equals(null));
		
		FieldInfo info = res.getInfo("Number");
		assertFalse(res1.equals(info));
		
		assertFalse(res1.equals(res2));
		assertTrue(res1.equals(res1));
		assertTrue(res2.equals(res3));
	}
	
	@Test
	public void testForeignKeyDescriptor() {
		String fkey = Repository.getForeignKeyDescriptor(ctx, "Test", false);
		assertNotNull(fkey);
		fkey = Repository.getForeignKeyDescriptor(ctx, "Twix", false);
		assertNull(fkey);
	}
	
	@Test
	public void testFieldInfo(){
		FieldInfo info = res.getInfo("Number");
		assertNotNull(info);
		assertTrue(info.isNumeric());
		assertFalse(info.isString());
		assertNull(info.getReferencedTable());
		
		info = res.getInfo("PersonID");
		assertTrue(info.isNumeric());
		assertFalse(info.isString());
		assertFalse(info.isDate());
		assertEquals("Person",info.getReferencedTable());
	}

	@Test
	public void testGetIndexes(){
		Set<String> idx = res.getIndexNames();
		
		assertNotNull(idx);
		assertEquals(2, idx.size());
		assertTrue(idx.contains("name_index"));
		assertTrue(idx.contains("number_date_index"));
	}
	/*
	 * Test method for 'uk.ac.ed.epcc.webapp.model.data.Repository.hasField(Object)'
	 */
	@Test
	public void testHasField() {
		
		assertTrue(res.hasField("Name"));
		assertTrue(res.hasField("Number"));
		assertTrue(res.hasField("Date"));
		assertFalse(res.hasField("Fred"));
	}
	
	@Test
	public void testCreate() throws ConsistencyError, DataException{
		Record r = res.new Record();
		r.put("Name","fred");
		r.put("Number", new Integer(12));
		// date is default null this should be ok
		assertTrue(r.commit());
		// now check second commit does not change 
		assertFalse(r.commit());
		int id = r.getID();
		r.put("Name","boris");
		
		assertTrue(r.commit());
		
		// trivial change should not change 
	    r.put("Name","boris");
		assertFalse(r.commit());
		Record p = res.new Record();
		p.setID(id);
		assertEquals("boris",p.get("Name"));
		assertNull(p.get("Date"));
		p.delete();
		r.delete();
	}

	
	@Test
	public void testSpecifyID() throws DataException{
		Record r = res.new Record();
		
		r.setID(354, IdMode.IgnoreExisting);
		r.put("Name","fred");
		r.put("Number", new Integer(12));
		// date is default null this should be ok
		assertTrue(r.commit());
		assertEquals(354, r.getID());

		
		// now check second commit does not change 
		assertFalse(r.commit());
		r.put("Name","boris");
		
		assertTrue(r.commit());
		
		// trivial change should not change 
	    r.put("Name","boris");
		assertFalse(r.commit());
		Record p = res.new Record();
		p.setID(354);
		assertFalse(p.isDirty());
		assertEquals("boris",p.get("Name"));
		assertNull(p.get("Date"));
		p.delete();
		r.delete();
		
	}
	
	@Test
	public void testStringCreate() throws ConsistencyError, DataException{
		Record r = res.new Record();
		r.put("Name","fred");
		// for legacy reasons we might create a number field with a string
		r.put("Number", "12");
		assertTrue(r.commit());
		// now check second commit does not change 
		assertFalse(r.commit());
		int id = r.getID();
		r.put("Name","boris");
		
		assertTrue(r.commit());
		
		// trivial change should not change 
        r.put("Name","boris");
		assertFalse(r.commit());
		Record p = res.new Record();
		p.setID(id);
		assertEquals("boris",p.get("Name"));
		p.delete();
		r.delete();
		
	}
	@Test 
	public void testLongField() throws ConsistencyError, DataException {
		Record r = res.new Record();
		
		r.put("Longtext","fred");
		r.commit();
		
		Record p = res.new Record();
		p.setID(r.getID());
		assertEquals("fred",p.get("Longtext"));
		p.delete();
		r.delete();
	}
	
	@Test
	public void testLongString() {
		Record r = res.new Record();
		r.put("Name", "HEnryHooverDemontfordWombleKingHooplyFrooom");
		try {
			r.commit();
			fail("Expecting exception");
		}catch(DataFault e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@ConfigFixtures("truncate.properties")
	public void testTruncateLongString() throws DataException {
		assertTrue(res.getInfo("Name").isTruncate());
		Record r = res.new Record();
		String value = "HEnryHooverDemontfordWombleKingHooplyFrooom";
		r.put("Name", value);
	
		r.commit();
	
		
		int id = r.getID();
		Record r2 = res.new Record();
		r2.setID(id);
		Object trun = r2.getProperty("Name");
		System.out.println(trun);
		assertEquals(value.substring(0, 32), trun);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testDateField() throws ConsistencyError, DataException{
		// sql.date truncates time fields this is what the DB returns for a date field
		Date d = new java.sql.Date(65,11,12);
		
		Record r = res.new Record();
		r.put("Name","fred");
		r.put("Number", new Integer(12));
		r.put("Date", d);
		// date is default null this should be ok
		assertTrue(r.isDirty());
		assertTrue(r.commit());
		// now check second commit does not change 
		assertFalse(r.isDirty());
		assertFalse(r.commit());
		int id = r.getID();
		Record p = res.new Record();
		p.setID(id);
		assertEquals("fred",p.get("Name"));
		Number n = (Number) p.get("Number");
		Date d2 = (Date) p.getDateProperty("Date");
		
		assertEquals(12,n.intValue());
		System.out.println(d.toString());
		
		System.out.println(d2.toString());
      System.out.println(d.getClass().toString());
		
		System.out.println(d2.getClass().toString());
		assertEquals(d.getTime(), d2.getTime());
		// check we can set null
		r.put("Name", null);
		assertTrue(r.isDirty());
		assertTrue(r.commit());
		assertFalse(r.isDirty());
		assertNull(r.get("Name"));
		
		p = res.new Record();
		p.setID(id);
		assertFalse(p.isDirty());
		assertNull(p.get("Name"));
		p.delete();
		r.delete();
	}
	
	
	
	@Test
	public void testDateField2() throws ConsistencyError, DataException, ParseException{
		DateInput input = new DateInput();
		Date d = input.parseValue("2019-05-01");
		
		Record r = res.new Record();
		r.put("Name","fred");
		r.put("Number", new Integer(12));
		r.put("TruncatedDate", d);
		// date is default null this should be ok
		assertTrue(r.isDirty());
		assertTrue(r.commit());
		// now check second commit does not change 
		assertFalse(r.isDirty());
		assertFalse(r.commit());
		int id = r.getID();
		Record p = res.new Record();
		p.setID(id);
		assertEquals("fred",p.get("Name"));
		Number n = (Number) p.get("Number");
		Date d2 = (Date) p.getDateProperty("TruncatedDate");
		
		assertEquals(12,n.intValue());
		System.out.println(d.toString());
		
		System.out.println(d2.toString());
      System.out.println(d.getClass().toString());
		
		System.out.println(d2.getClass().toString());
		// check a save edit cycle keeps things unchanged
		assertEquals("2019-05-01", input.getString(d2));
		assertEquals(d.getTime(), d2.getTime());
		
	}
	@Test
	public void testBooleanField() throws DataException{
		Record r = res.new Record();
		
		r.put("Boolean", true);
		r.put("Name", "Y");
		assertTrue(r.isDirty());
		assertTrue(r.commit());
		int id = r.getID();
		
		Record p = res.new Record();
		p.setID(id);
		assertTrue(p.getBooleanProperty("Boolean"));
		assertFalse(p.getBooleanProperty("Norris", false));
		assertTrue(p.getBooleanProperty("Chuck", true));
		assertEquals("true",p.getStringProperty("Boolean"));
		assertTrue(p.getBooleanProperty("Name"));
	}
	
	@Test
	public void testBlobField() throws DataException{
		Record r = res.new Record();
		r.put("Name","Blobby");
		ByteArrayStreamData data = new ByteArrayStreamData();
		OutputStream stream = data.getOutputStream();
		PrintWriter w = new PrintWriter(stream);
		w.print("Hello world");
		w.close();
		r.put("Blob", data);
		
		assertTrue(r.isDirty());
		assertTrue(r.commit());
		int id = r.getID();
		
		
		StreamData rdata =r.getStreamDataProperty("Blob");
		assertEquals("Hello world".length(), rdata.getLength());
		assertEquals("Hello world", rdata.toString());
		assertEquals("Hello world", r.getStringProperty("Blob"));
		
		Record p = res.new Record();
		p.setID(id);
		assertEquals("Hello world", p.getStringProperty("Blob").trim());
		
		StreamData namedata =p.getStreamDataProperty("Name");
		assertEquals("Blobby".length(), namedata.getLength());
		assertEquals("Blobby", namedata.toString());
		
		StreamData newdata =p.getStreamDataProperty("Blob");
		assertEquals("Hello world".length(), newdata.getLength());
		assertEquals("Hello world", newdata.toString());
		
		assertNull(p.getStreamDataProperty("Wonka"));
		
		
	}
	
	@Test
	public void testRequiredProperty() throws DataException{
		Record r = res.new Record();
		r.put("Name","Hoppy");
		assertTrue(r.isDirty());
		assertTrue(r.commit());
		int id = r.getID();
		
		Record p = res.new Record();
		p.setID(id);
		
		assertEquals("Hoppy", p.getProperty("Name"));
		
		try{
			p.getRequiredProperty("Lucan");
			assertFalse("Should have thrown exception",true);
		}catch(ConsistencyError e){
			
		}
	}
	
	@Test
	public void testSetContents() throws SQLException, DataException{
		Record r = res.new Record();
		r.put("Name","fred");
		r.put("Number", new Integer(12));
		r.put("Boolean",false);
		assertTrue(r.commit());
		Statement s = res.getSQLContext().getConnection().createStatement();
		 ResultSet rs = s.executeQuery("select * from "+res.getTag()+" where "+res.getUniqueIdName()+"="+r.getID());
		 Record p = res.new Record();
		 assertTrue(rs.next());
		 p.setContents(rs,true);
		 System.out.println(" "+r.getID()+" "+p.getID());
		 assertTrue( r.equals(p));
		 for(Iterator it = res.getFields().iterator();it.hasNext();){
	    		Object key=it.next();
		        assertEquals(r.get(key),p.get(key));
		 }
		 rs.close();
		 rs = s.executeQuery("select "+res.getTag()+".* from "+res.getTag()+" where "+res.getTag()+"."+res.getUniqueIdName()+"="+r.getID());
		 Record q = res.new Record();
		 assertTrue(rs.next());
		 q.setContents(rs,true);
		 assertTrue( r.equals(q));
		 for(Iterator it = res.getFields().iterator();it.hasNext();){
	    		Object key=it.next();
		        assertEquals(r.get(key),p.get(key));
		 }
		 rs.close();
		 s.close();
	}
	
	@Test
	public void testPut(){
		Record r = res.new Record();
		try{
		  r.put("Noris","fred");
		  assertTrue("should not reach here",false);
		}catch(UnsupportedOperationException e){
			System.out.println("OK got expected exception");
		}
		
	}
	
	@Test
	public void testClone() throws DataFault{
		Record r = res.new Record();
		r.put("Name", "fred");
		r.commit();
		
		assertTrue(r.getID() > 0);
		
		Record c = (Record) r.clone();
		
		assertEquals(r.getID(), c.getID());
		assertEquals(r.get("Name"), c.get("Name"));
		assertEquals(r,c);
	}
	@Test
	public void testCopy() throws DataFault{
		Record r = res.new Record();
		r.put("Name", "fred");
		r.commit();
		Record c = res.new Record();
		c.put("Name", "bill");
		c.commit();
		
		assertTrue(r.getID() > 0);
		assertTrue(c.getID() > 0);
		assertFalse(r.equals(c));
		
		c.copy(r);
		assertEquals(r.getID(), c.getID());
		assertEquals(r.get("Name"), c.get("Name"));
		
	}
	@Test 
	public void testGetValues(){
		Record r = res.new Record();
		r.put("Name", "fred");
		r.put("Number", 12);
		Map m = r.getValues();
		
		assertEquals("fred", m.get("Name"));
		assertEquals(12, m.get("Number"));
		assertEquals(2, m.size());
	}
	@Test
	public void testSetMap() throws DataFault {
		Record r = res.new Record();
		r.put("Name", "fred");
		r.put("Number", 12);
		r.commit();
		int r_id = r.getID();
		Map m = r.getValues();
		m.put("Bogus", "data");
		
		Record r2 = res.new Record();
		r2.set(m);
		assertEquals("fred",r2.getProperty("Name"));
		assertEquals(12,r2.getNumberProperty("Number"));
		assertNull(r2.getProperty("Bogus"));
		
		int dup = r2.findDuplicate();
		
		assertEquals(r_id,dup);
	}
	@Test
	public void testEquals(){
		Record r = res.new Record();
		r.put("Name", "fred");
		r.put("Number", 12);
		Map m = r.getValues();
		assertTrue(r.equals(m));
		m.put("Date", new Date());
		assertFalse(r.equals(m));
		m.remove("Date");
		assertTrue(r.equals(m));
	}
	@Test 
	public void testSetProperty(){
		Record r = res.new Record();
		r.put("Name", "fred");
		r.put("Number", 12);
		
		assertEquals("fred", r.getStringProperty("Name"));
		assertEquals(Integer.valueOf(12), Integer.valueOf(r.getIntProperty("Number")));
		assertEquals(Long.valueOf(12), Long.valueOf(r.getLongProperty("Number")));
		assertEquals(Float.valueOf(12.0F), Float.valueOf(r.getFloatProperty("Number")));
		assertEquals(Double.valueOf(12.0),Double.valueOf(r.getDoubleProperty("Number")));
		
		Date d = new Date(12000L);
		assertEquals(d, r.getDateProperty("Number"));
		assertTrue(r.getBooleanProperty("Boolean", true));
		assertFalse(r.getBooleanProperty("Boolean", false));
	}
	@Test
	public void testPutAll(){
		Record r = res.new Record();
		Map<String,String> m = new HashMap<>();
		
		m.put("Noris","fred");
		try{
			r.putAll(m);
			System.out.println("OK no exception");
			assertFalse(r.containsKey("Noris"));
		}catch(UnsupportedOperationException e){
			assertTrue("should not reach here",false);
			
		}
	}
	
	@Test
	public void testFactory(){
		assertSame("Factory must return the same object for same Context",res,Repository.getInstance(ctx,"Test"));
	}
	@Test 
	public void testNumberFieldExpression(){
		NumberFieldExpression f = res.getNumberExpression(Number.class, "Number");
		assertNotNull(f);
		StringBuilder sb = new StringBuilder();
		assertEquals(Number.class, f.getTarget());
		
	}
	@Test 
	public void testStringFieldExpression(){
		StringFieldExpression f = res.getStringExpression("Name");
		assertNotNull(f);
		assertEquals(String.class, f.getTarget());
		
	}
	@Test 
	public void testDateFieldExpression(){
		FieldValue f = res.getDateExpression("Date");
		assertNotNull(f);
		assertEquals(Date.class, f.getTarget());
		
	}
	@Test 
	public void testBooleanFieldExpression(){
		FieldValue f = res.getBooleanExpression("Boolean");
		assertNotNull(f);
		assertEquals(Boolean.class, f.getTarget());
		
	}
	@Test 
	public void testReferenceValue() throws Exception{
		IndexedFieldValue f = res.getReferenceExpression("PersonID");
		assertNotNull(f);
		assertEquals(IndexedReference.class, f.getTarget());
		assertEquals("Person", f.getFactory().getTag());
	}
	
	@Test
	public void testConvert() {
		assertNull(res.convert((Class)null, null));
		assertNull(res.convert(Date.class, null));
		assertEquals(new Date(15000L), res.convert(Date.class, 15));
		assertEquals(Boolean.TRUE, res.convert(Boolean.class, "Y"));
		assertEquals(12L,res.convert(Number.class, new Date(12000L)));
	}
}