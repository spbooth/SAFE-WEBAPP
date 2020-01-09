//| Copyright - The University of Edinburgh 2019                            |
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

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.AppContext;

/**
 * @author Stephen Booth
 *
 */
public class ListFonts implements Command {
	 /**
	 * @param conn
	 */
	public ListFonts(AppContext conn) {
		super();
		this.conn = conn;
	}

	private final AppContext conn;
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
		
		GraphicsEnvironment ge;  
	    ge = GraphicsEnvironment.getLocalGraphicsEnvironment();  

	    String[] names = ge.getAvailableFontFamilyNames();
	    Font[] allFonts = ge.getAllFonts();

	    for(int x=0; x<names.length; x++)
	        System.out.println(names[x]);

	    for(int x=0; x<allFonts.length; x++){           
	        System.out.println(allFonts[x].getName());
	        System.out.println(allFonts[x].getFontName());
	        System.out.println(allFonts[x].getFamily());
	        System.out.println(allFonts[x].getPSName());
	    }
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.apps.Command#description()
	 */
	@Override
	public String description() {
		return "List font names";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.apps.Command#help()
	 */
	@Override
	public String help() {
		return "";
	}
	public static void main(String args[]){
		AppContext c = new AppContext();
		CommandLauncher launcher = new CommandLauncher(c);
		launcher.run(ListFonts.class, args);
	}
}
