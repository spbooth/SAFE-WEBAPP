/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.forms.html.FormFactoryTester;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.SimpleSessionService;
import uk.ac.ed.epcc.webapp.timer.TimerService;

public abstract class FormRegistryTestCase extends WebappTestBase {
	
	
	
	public final FormFactoryProviderRegistry getRegistry(){
		return ctx.makeObject(FormFactoryProviderRegistry.class,getTag());
	}
	
	public final AppUser getUser() throws DataException{
		AppUserFactory fac = ctx.getService(SessionService.class).getLoginFactory();
		return fac.findByEmail(ctx.getInitParameter("test.email"),true);
	}
	
	public abstract String getTag();
	
	@Test
	public void testCreate(){
		FormFactoryProviderRegistry orig = getRegistry();
		assertNotNull(orig);
		String group = orig.getGroup();
		String tag = getTag();
		assertEquals(tag, group);
	}
	
    @SuppressWarnings("unchecked")
    @Test
    public void testgetFactory() throws Exception{
	  FormFactoryTester tester = new FormFactoryTester(ctx);
	  AppUser man = getUser();
	  SessionService<AppUser> s = new SimpleSessionService(ctx);
	  if( man != null ){
		  s.setCurrentPerson(man);
	  }
	  ctx.setService(s);
	  for(Iterator<FormEntry> it = getRegistry().getTypes();it.hasNext();){
		  FormEntry f = it.next();
		  System.out.println(f.getName());
		  Contexed fac = f.getFactory(ctx);
		  if( fac instanceof DataObjectFactory){
			  if( ! ((DataObjectFactory) fac).isValid()){
				  System.out.println("Factory not valid");
				  return;
			  }
		  }
		  TimerService timer = ctx.getService(TimerService.class);
		  if( timer != null){
			  timer.startTimer(f.getName());
		  }
		  FormCreator create = f.getFormCreator(ctx);
		  if( create != null ){
			  System.out.println("test create");
			  tester.testCreate(create);
		  }
		  FormUpdate update = f.getFormUpdate(ctx);
		  if( update != null ){
			  System.out.println("test update");
			  tester.testUpdate(update);
		  }
		  if( timer != null ){
			  timer.stopTimer(f.getName());
		  }
	  }
	  
  }
  
}
