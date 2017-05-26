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
package uk.ac.ed.epcc.webapp.model.data.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.DoubleFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.FloatFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.IndexType;


public class TableMakerTest extends WebappTestBase {


	@Test
	public void testBasicTable() throws SQLException{
		TableSpecification s = new TableSpecification();
		
		SQLContext sql = ctx.getService(DatabaseService.class).getSQLContext();
		LinkedList<Object> args = new LinkedList<Object>();
		String result = sql.getContext().getService(DataBaseHandlerService.class).createTableText("Fred", s, sql, args);
		System.out.println(result);
		assertTrue(result.startsWith("CREATE TABLE `Fred` ( `PrimaryRecordID` INT(11) NOT NULL auto_increment,\n"+
				"PRIMARY KEY (`PrimaryRecordID`))"));
	}
	
	@Test
	public void testNumberTable() throws SQLException{
		TableSpecification s = new TableSpecification();
		s.setField("i", new IntegerFieldType(true,null));
		s.setField("f", new FloatFieldType(false,12.0F));
		s.setField("d", new DoubleFieldType(true,12.0));
		SQLContext sql = ctx.getService(DatabaseService.class).getSQLContext();
		LinkedList<Object> args = new LinkedList<Object>();
		String result = sql.getContext().getService(DataBaseHandlerService.class).createTableText("Test", s, sql, args);
		System.out.println(result);
		assertTrue(result.startsWith("CREATE TABLE `Test` ( `PrimaryRecordID` INT(11) NOT NULL auto_increment,\n"+
				"`i` INT(11) DEFAULT NULL,\n"+
				"`f` FLOAT NOT NULL DEFAULT ?,\n"+
				"`d` DOUBLE DEFAULT ?,\n"+
				"PRIMARY KEY (`PrimaryRecordID`))"));
	}
	
	@Test
	public void testStringTable() throws SQLException{
		TableSpecification s = new TableSpecification();
		s.setField("small", new StringFieldType(true,"boris",8));
		s.setField("large", new StringFieldType(false,null,512));
		SQLContext sql = ctx.getService(DatabaseService.class).getSQLContext();
		List<Object> args = new LinkedList<Object>();
		String result = sql.getContext().getService(DataBaseHandlerService.class).createTableText("Test", s, sql, args);
		System.out.println(result);
		assertEquals("CREATE TABLE `Test` ( `PrimaryRecordID` INT(11) NOT NULL auto_increment,\n"+
				"`small` VARCHAR(8) DEFAULT ?,\n"+
				"`large` mediumtext,\n"+
				"PRIMARY KEY (`PrimaryRecordID`))", result);
	}
	
	@Test
	public void testDateTable() throws SQLException{
		TableSpecification s = new TableSpecification();
		s.setField("t", new DateFieldType(true,null));
		SQLContext sql = ctx.getService(DatabaseService.class).getSQLContext();
		List<Object> args = new LinkedList<Object>();
		String result = sql.getContext().getService(DataBaseHandlerService.class).createTableText("Test", s, sql, args);
		System.out.println(result);
		assertTrue(result.startsWith("CREATE TABLE `Test` ( `PrimaryRecordID` INT(11) NOT NULL auto_increment,\n"+
				"`t` BIGINT(20),\n"+
				"PRIMARY KEY (`PrimaryRecordID`))"));
	}
	
	@Test
	public void testIndexes() throws InvalidArgument, SQLException{
		TableSpecification s = new TableSpecification();
		s.setField("i", new IntegerFieldType(true,null));
		s.setField("f", new FloatFieldType(false,12.0F));
		s.setField("d", new DoubleFieldType(true,12.0));
		s.new Index("float_key",false,"f","d");
		s.new Index("int_key",true,"i");
		SQLContext sql = ctx.getService(DatabaseService.class).getSQLContext();
		List<Object> args = new LinkedList<Object>();
		String result = sql.getContext().getService(DataBaseHandlerService.class).createTableText("Test", s, sql, args);
		System.out.println(result);
		assertTrue(result.startsWith("CREATE TABLE `Test` ( `PrimaryRecordID` INT(11) NOT NULL auto_increment,\n"+
				"`i` INT(11) DEFAULT NULL,\n"+
				"`f` FLOAT NOT NULL DEFAULT ?,\n"+
				"`d` DOUBLE DEFAULT ?,\n"+
				"PRIMARY KEY (`PrimaryRecordID`),\n" +
				"KEY `float_key` (`f`,`d`),\n"+
				"UNIQUE KEY `int_key` (`i`)"+
				")"));
		Iterator<IndexType> it = s.getIndexes();
		assertTrue(it.hasNext());
		IndexType i = it.next();
		assertEquals(i.getName(), "float_key");
		assertTrue(it.hasNext());
		i = it.next();
		assertEquals(i.getName(), "int_key");
	}
}