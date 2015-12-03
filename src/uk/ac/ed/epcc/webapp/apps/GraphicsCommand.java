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
package uk.ac.ed.epcc.webapp.apps;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.swing.SwingTransitionHandler;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.session.SessionService;
public abstract class GraphicsCommand implements Command{
	
		private final AppContext conn;
	 	protected JFrame frame;
	    protected Logger log;
	    public GraphicsCommand(AppContext conn){
	    	this.conn=conn;
	    	log = conn.getService(LoggerService.class).getLogger(getClass());
	    }


		

		public class TransitionActionListener<K,T> implements ActionListener{
			
			TransitionProvider<K,T> tp;
			K key;
			T target;
			public TransitionActionListener(TransitionProvider<K,T> tp, K key, T target){
				this.tp=tp;
				this.key=key;
				this.target=target;
			}
			
			public void actionPerformed(ActionEvent e) {
			
				SwingTransitionHandler handler = new SwingTransitionHandler(frame, conn);
				try {
					handler.process(new ChainedTransitionResult<T, K>(tp, target, key));
				} catch (Exception e1) {
					conn.error(e1,"Error performing transition");
				}
			}
			
		}
		
		public void run(LinkedList<String> args) {
			
			SessionService session_service=conn.getService(SessionService.class);
			
			if( session_service == null){
				CommandLauncher.die("No user or roles set");
			}
			
			frame = new JFrame(getTitle());
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			JComponent main = getMainPanel(frame,session_service);
			JScrollPane scroll = new JScrollPane(main,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			frame.getContentPane().add(scroll);
			
			frame.pack();
			frame.setVisible(true);
		}
		public String getTitle() {
			return "Admin forms";
		}
		public AppContext getContext() {
			return conn;
		}
		protected abstract JComponent getMainPanel(JFrame frame, SessionService session_service);
		
	 	public String description() {
			return "A graphics command";
		}
		public String help() {
			return "No help";
		}
}