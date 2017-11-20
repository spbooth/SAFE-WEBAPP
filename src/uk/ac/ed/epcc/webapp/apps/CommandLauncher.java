//| Copyright - The University of Edinburgh 2011                            |
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
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.apps;

import java.io.FileInputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.config.OverrideConfigService;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.config.DataBaseConfigService;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.logging.debug.DebugLoggerService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SimpleSessionService;

/** Class to run a {@link Command} from the command-line.
 * This class can be sub-classed to customise the AppContext creation.
 * 
 * @author spb
 *
 */
public class CommandLauncher implements Contexed{
	private static final Options options = new Options();
	  private static final Option OPT_HELP = new Option(options, 'h', "help",
		"Print usage information and exit");
	  private static final Option OPT_PROP = new Option(options, 'P', true,
		"Specifiy a property value.  e.g. -Pprop=val").setMultipleArgs();

	  private static final Option OPT_PROPS_FILE = new Option(options, 'p',
		"properties", true, "Specify a properties file to load").setMultipleArgs();
	  
	  
	  private static final Option OPT_DEBUG_LEVEL = new Option(options, 'X',
				"level", true, "Specify the debug level");
	  private static final Option OPT_TRACE = new Option(options, 'T', "trace",
		"Show stack trace in debug");
	  private static final Option OPT_ROLE = new Option(options,'R',true,"Run with role").setMultipleArgs();
	  private static final Option OPT_USER = new Option(options,'U',true,"Run with user");

	  private AppContext conn;
	  private PrintStream out = System.out;
	  private PrintStream err = System.out;
	  public CommandLauncher(AppContext conn){
		  setContext(conn);
	  }
	  public void setContext(AppContext conn){
		  this.conn=conn;
	  }
	  public AppContext getContext(){
		  return conn;
	  }
	  public static void main(String[] args){
		  AppContext conn = new AppContext();
		  conn.setService( new CommandLineLoggerService());
		  CommandLauncher laucher=new CommandLauncher(conn);
		  laucher.run(args);
	  }
	/** run the Command.
	 * @param args
	 */
	
	public  void run(String[] args) {
		AppContext context = getContext();
		LinkedList<String> data=new LinkedList<String>();
		Options.Instance opts = setupContext(args, data, context);
		String command = data.poll();
		// HELP option
		if (opts.containsOption(OPT_HELP) || command == null) {
			out.println("Global Options:");
			out.println(options);
			Command comm = null;
			if( command != null ){
				comm = context.makeObjectWithDefault(Command.class, null, command);
			}
			if( comm != null ){
				out.println(command+": "+comm.description());
				out.println(comm.help());
			}else{
			
			out.println("Commands:");
			Map<String,Class> map = context.getClassMap(Command.class);
			for( String name : map.keySet()){
				out.println("\t"+name+"- "+context.makeObject(Command.class,name).description());
			}
			}
			return;
		}
		Command comm = context.makeObjectWithDefault(Command.class,null, command);
		if( comm == null ){
			die("Command :"+command+" Not found");
		}else{
			try{
				comm.run(data);
			}catch(Throwable e){
				if( System.getProperty("testing") == null ){
					e.printStackTrace();
					System.exit(1);
				}
				throw new ConsistencyError("Error in run",e);
			}
		}
	}
	/** launch pre-existing command.
	 * Can use this to implement main method directly in
	 * Command Class
	 * 
	 * @param comm
	 * @param args
	 * @throws Exception 
	 */
	public void run(Class<? extends Command> comm, String args[]) {
		LinkedList<String> data = new LinkedList<String>();
		AppContext conn = getContext();
		setupContext(args,data,conn);
		Command c;
		try {
			c = conn.makeContexedObject(comm);
		} catch (Exception e) {
			conn.getService(LoggerService.class).getLogger(getClass()).error("error making object",e);
			return;
		}
		
		c.run(data);
		
	}
	public static Options.Instance  setupContext(String[] args,
			LinkedList<String> data,  AppContext context) {
		if( System.getProperty("testing") == null ){
			context.setService(new CommandLineLoggerService());
		}
		Logger log = context.getService(LoggerService.class).getLogger(CommandLauncher.class);
		Options.Instance opts = options.newInstance();
		Properties prop = new Properties();
		
		try {
			
			data.addAll(opts.parse(args));
		} catch (IllegalArgumentException e) {
			die(e);
			return null; // This will never happen but java can't spot that.
		} catch (IllegalStateException e) {
			die(e);
			return null; // This will never happen but java can't spot that.
		}

		
		

		// Process the options ----------------------------------------------------

		

		
		// PROPERTIES option
		if (opts.containsOption(OPT_PROPS_FILE)) {
			for(String propFileName : opts.getOption(OPT_PROPS_FILE).getValues()){
		
				try {
					prop.load(new FileInputStream(propFileName));
				} catch (Exception e) {
					die(e);
				}
			}
		}
		

		// Set individual properties option
		if (opts.containsOption(OPT_PROP)) {
			Option.Instance optProp = opts.getOption(OPT_PROP);
			setProp(prop,optProp.getValues());
		}

	
		if( prop.size() > 0 ){
			// set in the system properties so the
			// AppContext can use them when setting DB connection
			try {
				Properties new_sys = new Properties(System.getProperties());
				for( Object key : prop.keySet()){
					new_sys.setProperty(key.toString(), prop.getProperty(key.toString()));
				}
				System.setProperties(new_sys);
			}catch(SecurityException sex) {
				// ignore
			}
		}
        
       
        
        // command line overrides
        context.setService(new OverrideConfigService(prop, context));
        // This allows a non-database app (e.g. web-service client) to 
        // supress database connections
        if( AppContext.DATABASE_FEATURE.isEnabled(context)){
        	DatabaseService db_serv = context.getService(DatabaseService.class);
        	try {
        		if( db_serv == null || db_serv.getSQLContext() == null ){
        			System.err.println("Warning: No database connection configured");
        		}else{
        			context.setService(new DataBaseConfigService(context));
        		}
        		
        	} catch (SQLException e) {
        		log.error("Error making Database connection",e);
        	}
        	
        }
		if( opts.containsOption(OPT_DEBUG_LEVEL)){
			Option.Instance optProp = opts.getOption(OPT_DEBUG_LEVEL);
			LoggerService logserv = context.getService(LoggerService.class);
			if( logserv instanceof DebugLoggerService){
				logserv = ((DebugLoggerService)logserv).getNested();
			}
			if( logserv instanceof CommandLineLoggerService){
				CommandLineLoggerService serv = (CommandLineLoggerService) logserv;
				serv.setLevel(CommandLineLoggerService.Level.valueOf(optProp.getValue()));
				serv.printStackTraces(opts.containsOption(OPT_TRACE));
			}
		}
		SimpleSessionService sess = new SimpleSessionService(context);
		context.setService( sess);
		if( opts.containsOption(OPT_ROLE)|| opts.containsOption(OPT_USER)){
			
			if( opts.containsOption(OPT_USER)){
				Option.Instance user_opt = opts.getOption(OPT_USER);
				AppUserFactory fac = sess.getLoginFactory();
				try{
					String username = user_opt.getValue();
					AppUser user = null;
					if( username.contains("@")){
						user=fac.findByEmail(username, true);
					}else{
						user=fac.findFromString(username);
					}
					if( user != null ){
						sess.setCurrentPerson(user);
					}else{
						log.error("user "+username+" not found");
					}
				}catch(Exception e){
					log.error("Error setting user",e);
				}
			}
			if( opts.containsOption(OPT_ROLE)){
				Option.Instance role_opts = opts.getOption(OPT_ROLE);
				for(String role : role_opts.getValues()){
					log.debug("Setting role "+role);
					sess.setTempRole(role);
					// enable toggle roles
					if( sess.getToggle(role) != null){
						sess.setToggle(role, true);
					}
				}
			}	
		}
		return opts;
	}
	/**
	 * Convenient method for killing the application with an error message
	 * 
	 * @param errorMessage
	 *          The error message
	 */
	public static void die(String errorMessage) {
		System.err.println(errorMessage);
		if( System.getProperty("testing") == null ){
			System.exit(1);
		}else{
			throw new ConsistencyError(errorMessage);
		}
	}

	/**
	 * Convenient method for killing the application with an error message
	 * 
	 * @param t
	 *          The <code>Throwable</code> that caused the error message
	 */
	public static void die(Throwable t) {
		System.err.println(t.getMessage());
		if( System.getProperty("testing") == null ){
			System.exit(1);
		}else{
			throw new ConsistencyError("Application error",t);
		}
	}
	
	/**
	 * Convenient method for doing a normal system exit with a specified code
	 * @param conn 
	 * @param exitCode 
	 * 
	*/
	public static void exit(AppContext conn,int exitCode) {
		// calling exit in a unit test aborts the test framework
		if( conn.getInitParameter("testing") == null ){
			System.exit(exitCode);
		}else{
			throw new ConsistencyError("Application exit: "+exitCode);
		}
	}

	
	/**
	 * Sets the specified property
	 * @param prop 
	 * 
	 * @param propKeyVals
	 *          The property key and vlaue (expected format is 'key=value')
	 */
	public static void setProp(Properties prop, List<String> propKeyVals) {

		for (String propKeyVal : propKeyVals) {
			int sepIndex = propKeyVal.indexOf('=');
			if (sepIndex < 0) {
				die("attempt to set value of property '" + propKeyVal + "' failed.  "
						+ "Couldn't find '=' character separating property key and value");
			}

			String key = propKeyVal.substring(0, sepIndex);
			try {
				String value = propKeyVal.substring(sepIndex + 1);
				prop.setProperty(key, value);
			} catch (ArrayIndexOutOfBoundsException e) {
				die("Unable to extract value of property '" + key + "'");
			}
		}
		
	}
	public void setOut(PrintStream out) {
		this.out = out;
	}
	public void setErr(PrintStream err) {
		this.err = err;
	}
}