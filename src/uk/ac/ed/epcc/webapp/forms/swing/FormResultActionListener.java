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
package uk.ac.ed.epcc.webapp.forms.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;



public class FormResultActionListener implements ActionListener {
	private final JFrame frame;
	private final FormResult result;
	private final AppContext conn;
	
	public FormResultActionListener(AppContext conn,JFrame frame, FormResult result){
		this.conn=conn;
		this.frame=frame;
		this.result=result;
	}

	public void actionPerformed(ActionEvent arg0) {
		SwingTransitionHandler handler = new SwingTransitionHandler(frame, conn);
		try {
			handler.process(result);
		} catch (Exception e) {
			conn.error(e,"Error processing form result");
		}

	}

}