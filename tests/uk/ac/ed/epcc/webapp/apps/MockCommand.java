// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.apps;

import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A mock object to track
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.9 $")
public class MockCommand implements Command {
	private final AppContext conn;
	/**
	 * 
	 */
	public MockCommand(AppContext c) {
		this.conn=c;
	}

	private static Options opt = new Options();
	private static Option THROW = new Option(opt,'T',"throw");
	private static Option EXPECT_USER = new Option(opt,'E',true,"expect");
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	public AppContext getContext() {
		return conn;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.apps.Command#run(java.util.LinkedList)
	 */
	public void run(LinkedList<String> args){
		Options.Instance op = opt.newInstance(args.toArray(new String[0]));
		
		if( op.containsOption(THROW)){
			throw new ConsistencyError("bang");
		}

		if(op.containsOption(EXPECT_USER)){


			SessionService sess = conn.getService(SessionService.class);
			if( ! sess.haveCurrentUser()){
				throw new ConsistencyError("No user");
			}
			String name = sess.getName();
			String value = op.getOption(EXPECT_USER).getValue();
			if( ! name.equals(value)){
				throw new ConsistencyError("User does not match");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.apps.Command#description()
	 */
	public String description() {
		// TODO Auto-generated method stub
		return "A test command";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.apps.Command#help()
	 */
	public String help() {
		return "A test helpmessage";
	}

}
