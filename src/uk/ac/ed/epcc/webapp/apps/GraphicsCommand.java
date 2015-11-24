// Copyright - The University of Edinburgh 2011
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
@uk.ac.ed.epcc.webapp.Version("$Id: GraphicsCommand.java,v 1.10 2014/09/15 14:30:11 spb Exp $")
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