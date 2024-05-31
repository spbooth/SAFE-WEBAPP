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

import java.io.FileOutputStream;
import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.apps.Command;
import uk.ac.ed.epcc.webapp.apps.CommandLauncher;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;


public class RetryQueuedMessagesCommand implements Command {
    private final AppContext conn;
    public RetryQueuedMessagesCommand(AppContext conn){
    	this.conn=conn;
    }
	public String description() {
			return "Retry Queud messages";
	}

	public String help() {
		return "no arguments";
	}

	public void run(LinkedList<String> args) {
		
		try {

			QueuedMessages fac = QueuedMessages.getFactory(getContext());
			long q = fac.getCount(null);
			int count = fac.retry();
			System.out.println("There were "+q+" queud messages, "+count+" were sent");
		}catch(Exception e) {
			getLogger().error("Error exporting messages", e);
		}
	}

	public AppContext getContext() {
		return conn;
	}

}