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
/*
 * Created on 24-Aug-2004
 *
 
 */
package uk.ac.ed.epcc.webapp;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;
import uk.ac.ed.epcc.webapp.config.OverrideConfigService;
import uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder;
import uk.ac.ed.epcc.webapp.content.XMLWriter;
import uk.ac.ed.epcc.webapp.jdbc.config.DataBaseConfigService;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
import uk.ac.ed.epcc.webapp.logging.debug.DebugLoggerService;
import uk.ac.ed.epcc.webapp.logging.print.PrintLoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Dumper;
import uk.ac.ed.epcc.webapp.session.SimpleSessionService;
import uk.ac.ed.epcc.webapp.timer.DefaultTimerService;

/** Generic test framework that does AppContex setup for other classes
 * @author spb
 *
 */
public abstract class WebappTestCase extends TestCase {
	public AppContext ctx;
	public String save_name=null;
	public static void main(String[] args) {
		junit.textui.TestRunner.run(WebappTestCase.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	@Override
	protected final void setUp() throws Exception {
		// MADE final to ensure the database fixtures are loaded in the correct place
		//
		super.setUp();
		
		ClassLoader cl = this.getClass().getClassLoader();
		InputStream service_props_stream = cl.getResourceAsStream("test.properties");
		Properties overrides = new Properties();
		if( service_props_stream != null ){
			overrides.load(service_props_stream);

			service_props_stream.close();

			// (nix) The TestDataHelper expects to find the test properties set as System properties. 
			// I didn't wwant to tie TestDataHelper to the ConfigService is I reload the properties 
			// into this sapce. The only other option would be to pass a Porperties object into every 
			// call to the TestDataHelper. I may to that later instead.
			// (spb) Actually the DefaultConfigService also needs config.path and deploy.path set
			// in the system properties

			service_props_stream = cl.getResourceAsStream("test.properties");
			System.getProperties().load(service_props_stream);
			service_props_stream.close();
		}
		// WE HAVE to load the test datasets before creating the AppContext
		// to ensure that the properties table is in place BEFORE the appcontext starts
		// otherwise it might cache the wrong value.
		/* look for  test specific data */
		Boolean use_fixtures = Boolean.valueOf(overrides.getProperty("use.fixtures", "true"));
		if( use_fixtures){
			setDatabaseFixtures();
		}
		ctx = new AppContext();
		ctx.setService(new PrintLoggerService());
		ctx.setService(new DebugLoggerService(ctx));
		ctx.setService( new SimpleSessionService(ctx));
		ctx.setService(new DataBaseConfigService(ctx));
		//props only in test.properties will be visible from the service props but
		// we also want to override any values in the normal config
		ctx.setService( new OverrideConfigService(overrides,ctx));
		ctx.setService(new DefaultTimerService(ctx));
		
		if( save_name != null){
			save(save_name);
		}
		setUp(ctx);
	}

	protected void setUp(AppContext c)throws Exception{
		
	}
	protected void setDatabaseFixtures() {
			if( ! TestDataHelper.loadDataSetsForTest(getClass().getSimpleName()+"."+getName(),false) ){
				// now look for data common to the entire TestCase
				if( TestDataHelper.loadDataSetsForTest(getClass().getSimpleName())){
					save_name=getClass().getCanonicalName()+"."+getName();
				}
			}else{
				save_name=getClass().getCanonicalName();
			}
	}
	
	
	protected void save(String name){
		try{
			String dir = getClass().getCanonicalName();
			dir = dir.substring(0,dir.lastIndexOf("."));
			dir = dir.replace('.', '/');
			dir = "tests/"+dir;
			File output = new File(dir+"/"+name+".xml");
			FileWriter w = new FileWriter(output);
			SimpleXMLBuilder builder = new XMLWriter(w);
			builder.open("dataset");
			Dumper d = new Dumper(ctx, builder);
			DataBaseHandlerService serv = ctx.getService(DataBaseHandlerService.class);
			for(String tab : serv.getTables()){
				try{
					DataObjectFactory<? extends DataObject> fac = ctx.makeObject(DataObjectFactory.class, tab);
					for(DataObject o: fac.all()){
						d.dump(o);
					}
				}catch(Throwable t){

				}
			}
			builder.close();
			builder.appendParent();
			//w.close();
		}catch(Throwable t){
			ctx.error(t,"Error dumping fixtures");
		}

	}
	/*
	 * @see TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		ctx.close();
		ctx=null;
		super.tearDown();
	}

}