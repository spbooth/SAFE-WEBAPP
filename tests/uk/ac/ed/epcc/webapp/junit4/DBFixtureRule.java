package uk.ac.ed.epcc.webapp.junit4;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import uk.ac.ed.epcc.webapp.ContextHolder;
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
		XMLDataUtils utils = getUtils();
		utils.dropAllTables();
	}

	
}
