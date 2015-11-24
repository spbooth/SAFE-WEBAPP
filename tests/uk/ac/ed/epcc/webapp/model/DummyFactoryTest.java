/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactoryTestCase;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.MultipleResultException;

public class DummyFactoryTest extends DataObjectFactoryTestCase {

	
	@Override
	public DataObjectFactory getFactory() {
		return new Dummy1.Factory(ctx);
	}

	@Before
	public void setUp()  {
	    try{
	    	((Dummy1.Factory) getFactory()).nuke();
	    }catch(Exception e){
	    	ctx.error(e, "Error nuking table");
	    }
	}
	
	@After
	public void tearDown() throws Exception {
		((Dummy1.Factory) getFactory()).nuke();
	}
	
	@Test
	public void testMultiFind() throws DataException{
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		for(int i=0; i< 17; i++){
			Dummy1 d = new Dummy1(ctx);
			d.setName("Test"+i);
			d.setUnsigned(i);
			d.setNumber(16-i);
			d.commit();
		}
	
		
		try{
			SQLAndFilter<Dummy1> f = new SQLAndFilter<Dummy1>(Dummy1.class);
			
			System.out.println("hello\n");
			fac.find(f);
			assertFalse("Exception not throws", true);
		}catch(MultipleResultException e){
			
		}
		AndFilter<Dummy1> fil = new AndFilter<Dummy1>(fac.getTarget());
		// Dummy accept filter to force non sQLFilter
		fil.addFilter(new AcceptFilter<Dummy1>() {

			public <X> X acceptVisitor(FilterVisitor<X, ? extends Dummy1> vis)
					throws Exception {
				return vis.visitAcceptFilter(this);
			}

			public Class<? super Dummy1> getTarget() {
				return Dummy1.class;
			}

			public boolean accept(Dummy1 o) {
				return true;
			}
		});
		
		try{
			fac.find(fil);
			assertFalse("Exception not throws", true);
		}catch(MultipleResultException e){
			
		}
	}
	
	
	@Test
	public void testOrder() throws DataFault{
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		for(int i=0; i< 17; i++){
			Dummy1 d = new Dummy1(ctx);
			d.setName("Test"+i);
			d.setUnsigned(i);
			d.setNumber(16-i);
			d.commit();
		}
		
		int expect=0;
		for(Dummy1 d : fac.all()){
			assertEquals(expect, d.getNumber());
			assertEquals(16-expect, (int) d.getUnsigned());
			assertEquals(d.getName(), "Test"+(16-expect));
			expect++;
		}
		assertEquals(expect, 17);
	}
	
	@Test
	public void testDefaultOrderWithFilter() throws DataFault{
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		for(int i=0; i< 17; i++){
			Dummy1 d = new Dummy1(ctx);
			d.setName("Test"+i);
			d.setUnsigned(i);
			d.setNumber(16-i);
			d.commit();
		}
		
		int expect=0;

		for(Dummy1 d : fac.getWithFilter()){
			assertEquals(expect, d.getNumber());
			assertEquals(16-expect, (int) d.getUnsigned());
			assertEquals(d.getName(), "Test"+(16-expect));
			expect++;
		}
		assertEquals(expect, 17);
	}
	
	@Test
	public void testReverseOrder() throws DataFault{
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		for(int i=0; i< 17; i++){
			Dummy1 d = new Dummy1(ctx);
			d.setName("Test"+i);
			d.setUnsigned(i);
			d.setNumber(16-i);
			d.commit();
		}
		
		int expect=16;
		for(Dummy1 d : fac.getReverse()){
			assertEquals(expect, d.getNumber());
			assertEquals(16-expect, (int) d.getUnsigned());
			assertEquals(d.getName(), "Test"+(16-expect));
			expect--;
		}
		assertEquals(expect, -1);
	}

	@Test
	public void getGetNullable(){
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		Set<String> nullable = fac.getNullFields();
		
		assertEquals(4, nullable.size());
		
		assertTrue(fac.fieldExists(Dummy1.MANDATORY));
		assertFalse(nullable.contains(Dummy1.MANDATORY));
	}
}
