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
package uk.ac.ed.epcc.webapp.email;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.apps.Command;
import uk.ac.ed.epcc.webapp.apps.CommandLauncher;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;


public class ImportQueuedMessages implements Command {
    private final AppContext conn;
    public ImportQueuedMessages(AppContext conn){
    	this.conn=conn;
    }
	public String description() {
			return "Import queued messages from file";
	}

	public String help() {
		return " import-filename";
	}

	public void run(LinkedList<String> args) {
		if( args.size() != 1 ) {
			CommandLauncher.die("Expecting file-name");
		}
		String file = args.getFirst();
		try {
			FileInputStream is = new FileInputStream(file);
			ByteArrayStreamData sd = new ByteArrayStreamData();
			sd.read(is);
			QueuedMessages fac = QueuedMessages.getFactory(getContext());
			fac.importMessages(sd);
			long count = fac.getCount(null);
			System.out.append("There are "+count+" queued messages");
			
		}catch(Exception e) {
			getLogger().error("Error exporting messages", e);
		}
	}

	public AppContext getContext() {
		return conn;
	}

}