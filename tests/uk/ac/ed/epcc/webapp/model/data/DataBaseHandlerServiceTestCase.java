/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
import uk.ac.ed.epcc.webapp.jdbc.table.DoubleFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.FloatFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;


public class DataBaseHandlerServiceTestCase extends WebappTestBase {

	private static final String SCRATCH_TABLE = "Scratch";
	DataBaseHandlerService handler;
	

	@Before
	public void setUp(){
		handler=ctx.getService(DataBaseHandlerService.class);
	}
	
	@Test
	@DataBaseFixtures({"CreateTable.xml"})
	public void testTableExists(){
		assertNotNull(handler);
		assertTrue("Test",handler.tableExists("Test"));
		assertFalse("Wombles",handler.tableExists("Wombles"));
	}
	
	@Test
	@DataBaseFixtures({"CreateTable.xml"})
	public void testGetTables() throws DataException{
		Set<String> res = handler.getTables();
		assertTrue(res.contains("Test")||res.contains("test"));
	}
	
	@Test
	public void testCreateTable() throws Exception{
		
		TableSpecification s = new TableSpecification();
		s.setField("i", new IntegerFieldType(true,null));
		s.setField("f", new FloatFieldType(false,12.0F));
		s.setField("d", new DoubleFieldType(true,12.0));
		s.new Index("float_key",false,"f","d");
		s.new Index("int_key",true,"i");
		handler.createTable(SCRATCH_TABLE, s);
		assertTrue(handler.tableExists(SCRATCH_TABLE));
		handler.deleteTable(SCRATCH_TABLE);
		assertFalse(handler.tableExists(SCRATCH_TABLE));
	}

	

	
}
