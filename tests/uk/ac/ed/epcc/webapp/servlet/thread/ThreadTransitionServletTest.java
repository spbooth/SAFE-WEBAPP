// Copyright - The University of Edinburgh 2016
package uk.ac.ed.epcc.webapp.servlet.thread;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.servlet.AbstractTransitionServletTest;
import uk.ac.ed.epcc.webapp.servlet.thread.TestFactory.TestData;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;


/**
 * @author spb
 *
 */
public class ThreadTransitionServletTest extends AbstractTransitionServletTest {
	
	
	@Test
	public void testAddFromSingleThread() throws ServletException, IOException, DataException, TransitionException{
		TestFactory fac = new TestFactory(getContext());
		TestData data = fac.makeBDO();
		data.setData(12);
		data.commit();
		int id = data.getID();
		AppUser user = setupPerson();
		AddDataTransitionProvider provider = new AddDataTransitionProvider(getContext(), "AddTest");
		setTransition(provider, AddDataTransitionProvider.ADD, data);
		addParam("Data", 12);
		runTransition();
		
		TestData result = fac.find(id);
		
		assertEquals(24,result.getData());
		
	}
	
	
	@Test
	public void testAddFromBackgroundThread() throws ServletException, IOException, DataException, TransitionException, InterruptedException, SQLException{
		TestFactory fac = new TestFactory(getContext());
		TestData data = fac.makeBDO();
		data.setData(12);
		data.commit();
		int id = data.getID();
		AppUser user = setupPerson();
		AddDataTransitionProvider provider = new AddDataTransitionProvider(getContext(), "AddTest");
		setTransition(provider, AddDataTransitionProvider.ADD, data);
		addParam("Data", 12);
		Runnable r = new BrowserRunnable(); // copies request 
		Thread t = new Thread(r, "Browser");
		
		t.start();
		
		t.join();
		
		
		TestData result = fac.find(id);
		
		assertEquals(24,result.getData());
		
	}
	
	
	@Test
	public void testConcurrent() throws ServletException, IOException, DataException, TransitionException, InterruptedException, SQLException{
		TestFactory fac = new TestFactory(getContext());
		TestData data = fac.makeBDO();
		data.setData(12);
		data.commit();
		int id = data.getID();
		AppUser user = setupPerson();
		AddDataTransitionProvider provider = new AddDataTransitionProvider(getContext(), "AddTest");
		setTransition(provider, AddDataTransitionProvider.ADD, data);
		addParam("Data", 12);
		addParam("Wait",1);  // waits one second within the transition
		Runnable r = new BrowserRunnable();
		
		setTransition(provider, AddDataTransitionProvider.ADD, data);
		addParam("Data", 17);
		Thread t = new Thread(r, "Browser1");
		t.start();
		Thread.sleep(500); // make sure thread gets into transitions
		System.out.println("started thread");
		runTransition();
		System.out.println("run main thread");
	    Thread.currentThread().yield();
		t.join();
		
		TestData result = fac.find(id);
		
		assertEquals(41,result.getData());
		
	}
	@Test
	/** This test bypasses the java lock by using two different TransitionProviders
	 * however consistency should still be in place due to database transactions provided we
	 * set the isolation level to SERIALIZE
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws DataException
	 * @throws TransitionException
	 * @throws InterruptedException
	 * @throws SQLException
	 */
	public void testConcurrentWithoutLock() throws ServletException, IOException, DataException, TransitionException, InterruptedException, SQLException{
		TestFactory fac = new TestFactory(getContext());
		TestData data = fac.makeBDO();
		data.setData(12);
		data.commit();
		int id = data.getID();
		AppUser user = setupPerson();
		AddDataTransitionProvider provider = new AddDataTransitionProvider(getContext(), "AddTest");
		setTransition(provider, AddDataTransitionProvider.ADD, data);
		addParam("Data", 12); // added before wait
		addParam("Data2", 5); // added after wait
		addParam("Wait",1);  // waits one second within the transition
		BrowserRunnable r = new BrowserRunnable();
		AddDataTransitionProvider2 provider2 = new AddDataTransitionProvider2(getContext(), "AddTest2");
		setTransition(provider2, AddDataTransitionProvider.ADD, data);
		addParam("Data", 17);
		Thread t = new Thread(r, "Browser1");
		t.start();
		Thread.sleep(500); // make sure thread gets into transitions
		System.out.println("started thread");
		runTransition();
		System.out.println("run main thread");
	    Thread.currentThread().yield();
		t.join();
		
		TestData result = fac.find(id);
		
		assertEquals(46,result.getData());
		r.ctx.close();
		
	}
	/** Add a link table to the above test to include object creation (with unique index).
	 * Again this requires the transactions to serialize
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConcurrentWithLinkWithoutLock() throws Exception{
		DataPersonManager man = new DataPersonManager(ctx); // need to create table outside transaction
		TestFactory fac = new TestFactory(getContext());
		TestData data = fac.makeBDO();
		data.setData(12);
		data.commit();
		int id = data.getID();
		AppUser user = setupPerson();
		AddDataTransitionProvider provider = new AddDataTransitionProvider(getContext(), "AddTest");
		setTransition(provider, AddDataTransitionProvider.ADD, data);
		addParam("Data", 12); // added before wait
		addParam("Data2", 5); // added after wait
		addParam("Wait",1);  // waits one second within the transition
		addParam("Comment", "From thread\n");
		BrowserRunnable r = new BrowserRunnable();
		AddDataTransitionProvider2 provider2 = new AddDataTransitionProvider2(getContext(), "AddTest2");
		setTransition(provider2, AddDataTransitionProvider.ADD, data);
		addParam("Data", 17);
		addParam("Comment", "From main\n");
		Thread t = new Thread(r, "Browser1");
		t.start();
		Thread.sleep(500); // make sure thread gets into transitions
		System.out.println("started thread");
		runTransition();
		System.out.println("run main thread");
	    Thread.currentThread().yield();
		t.join();
		
		TestData result = fac.find(id);
		
		assertEquals(46,result.getData());
	
		String comment = man.getComment(result);
		System.out.println(comment);
		assertTrue(comment.contains("From thread"));
		assertTrue(comment.contains("From main"));
		
		r.ctx.close();
		
	}
	/** setup a valid operator 
	 * 
	 * @throws DataException
	 */
	public AppUser setupPerson() throws DataException{
		SessionService sess = setupPerson(getContext().getInitParameter("test.email"));
		return sess.getCurrentPerson();
		
		
	}
}
