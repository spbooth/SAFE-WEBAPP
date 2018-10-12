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

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.ContextHolder;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.model.data.XMLDataUtils;

public class DBFixtureRule implements TestRule {

	// Rule may be intialised before constructor so 
	// store reference to the test and extract context later!!
	private final ContextHolder ctx;
	
	public DBFixtureRule(ContextHolder c){
		this.ctx=c;
	}
	public Statement apply(Statement base, Description description) {
		return statement(base,description);
	}

	private XMLDataUtils getUtils(){
		return new XMLDataUtils(ctx.getContext());
	}
	private Statement statement(final Statement base,final Description d) {
		DataBaseFixtures gfix = ctx.getClass().getAnnotation(DataBaseFixtures.class);
		final String global_fixtures[];
		if( gfix == null){
			global_fixtures = new String[0];
		}else{
			global_fixtures = gfix.value();
		}
		DataBaseFixtures fix = d.getAnnotation(DataBaseFixtures.class);
		final String fixtures[];
		if( fix == null){
			fixtures = new String[0];
		}else{
			fixtures = fix.value();
		}
		return new Statement() {
			
			@Override
			public void evaluate() throws Throwable {
				before(global_fixtures,fixtures);
				try {
					base.evaluate();
				} finally {
					after();
				}
			}
		};
	}

	
	/**
	 * Override to set up your specific external resource.
	 * @throws if setup fails (which will disable {@code after}
	 */
	protected void before(String global_fixtures[],String fixtures[]) throws Throwable {
		XMLDataUtils utils = getUtils();
		utils.dropAllTables();
		AppContext conn = ctx.getContext();
		if( conn != null){
			conn.clearAttributes();
			conn.clearObjectCache();
			conn.getService(ConfigService.class).clearServiceProperties();
		}
		if( global_fixtures != null && global_fixtures.length > 0){
			utils.readFixtures(ctx.getClass(), global_fixtures);
		}
		if( fixtures != null && fixtures.length > 0){
			utils.readFixtures(ctx.getClass(), fixtures);
		}
	}

	/**
	 * Override to tear down your specific external resource.
	 */
	protected void after() {
		//XMLDataUtils utils = getUtils();
		//utils.dropAllTables();
	}

	
}