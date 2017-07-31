//| Copyright - The University of Edinburgh 2017                            |
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
package uk.ac.ed.epcc.webapp.apps;

import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.session.RandomService;

/**
 * @author spb
 *
 */
public class GetRandomPassword implements Command, Contexed {

	private final AppContext conn;
	/**
	 * 
	 */
	public GetRandomPassword(AppContext conn) {
		this.conn=conn;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return conn;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.apps.Command#run(java.util.LinkedList)
	 */
	@Override
	public void run(LinkedList<String> args) {
		RandomService serv = getContext().getService(RandomService.class);
		System.out.println(serv.randomString(64));

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.apps.Command#description()
	 */
	@Override
	public String description() {
		return "Generate a randon password";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.apps.Command#help()
	 */
	@Override
	public String help() {
		// TODO Auto-generated method stub
		return "";
	}
	public static void main(String args[]){
		System.out.println("Starting upgrade");
		AppContext c = new AppContext();
		CommandLauncher launcher = new CommandLauncher(c);
		launcher.run(GetRandomPassword.class, args);
	}
}
