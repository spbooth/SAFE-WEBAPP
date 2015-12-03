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
package uk.ac.ed.epcc.webapp.junit4;

import java.io.InputStream;
import java.util.Properties;

import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.ContextHolder;
import uk.ac.ed.epcc.webapp.config.DefaultConfigService;
import uk.ac.ed.epcc.webapp.config.OverrideConfigService;
import uk.ac.ed.epcc.webapp.jdbc.DefaultDataBaseService;
import uk.ac.ed.epcc.webapp.jdbc.config.DataBaseConfigService;
import uk.ac.ed.epcc.webapp.logging.debug.DebugLoggerService;
import uk.ac.ed.epcc.webapp.logging.print.PrintLoggerService;
import uk.ac.ed.epcc.webapp.session.SimpleSessionService;
import uk.ac.ed.epcc.webapp.timer.DefaultTimerService;

/** Rule for creating an {@link AppContext} for the test
 * Additional test specific properties files can be loaded by
 * adding a {@link ConfigFixtures} annotation
 * @author spb
 *
 */
public class AppContextFixtureRule extends ExternalResource{

	ContextHolder holder;
	public AppContextFixtureRule(ContextHolder h){
		this.holder=h;
	}
	
	AppContext ctx;
	@Override
	protected void after() {
		ctx.close();
	}

	protected void before(String fixtures[]) throws Throwable {
		ClassLoader cl = this.getClass().getClassLoader();
		InputStream service_props_stream = cl.getResourceAsStream("test.properties");
		Properties overrides = new Properties();
		if( service_props_stream != null ){
			overrides.load(service_props_stream);

			service_props_stream.close();

			// Give sys-properties the final say on database connection
			// parameters in tests. This lets us override database connections
			// when running tests in a strange environment 
			// skip null or empty strings so null overrides can be passed from 
			// ant that only take effect when overidden
			Properties sys_properties = System.getProperties();
			for(String name : sys_properties.stringPropertyNames()){
				if( name.startsWith("db_") ){
					Object value = sys_properties.get(name);
					if( overrides.containsKey(name) && value != null && value.toString().trim().length() > 0){
						overrides.put(name, value);
					}
				}
			}
			
			// (spb) he DefaultConfigService needs config.path and deploy.path set
			// in the system properties copy these if set in overrides.
			// also the "testing" flag
			for(String prop : new String[]{DefaultConfigService.CONFIG_PATH_PROP_NAME,DefaultConfigService.DEFAULT_PATH_PROP_NAME,DefaultConfigService.DEPLOY_PATH_PROP_NAME,"testing"}){
				String value = overrides.getProperty(prop);
				if( value != null ){
					sys_properties.setProperty(prop,value);
				}
			}
			
			
		}
		
		for(String fix : fixtures){
			InputStream stream = holder.getClass().getResourceAsStream(fix);
			if( stream != null ){
				overrides.load(stream);
			}
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
		holder.setContext(ctx);
	}
	public Statement apply(Statement base, Description description) {
		return statement(base,description);
	}
	private Statement statement(final Statement base,final Description d) {
		ConfigFixtures fix = d.getAnnotation(ConfigFixtures.class);
		final String fixtures[];
		if( fix == null){
			fixtures = new String[0];
		}else{
			fixtures = fix.value();
		}
		return new Statement() {
			
			@Override
			public void evaluate() throws Throwable {
				before(fixtures);
				try {
					base.evaluate();
				} finally {
					after();
				}
			}
		};
	}
	
}