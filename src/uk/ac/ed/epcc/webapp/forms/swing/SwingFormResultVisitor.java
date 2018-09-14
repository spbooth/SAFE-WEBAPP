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

import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.BaseForm;
import uk.ac.ed.epcc.webapp.forms.action.NestAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.FatalTransitionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.BackResult;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.ConfirmTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.CustomPageResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.exception.ForceRollBack;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.messages.MessageBundleService;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** a FormResultVisitor that uses Swing windows.
 * 
 * To avoid deep recursion this class performs a single transition iteration 
 * any subsequent FormResults may be retrieved using getNextResult. 
 * @author spb
 *
 */


public class SwingFormResultVisitor implements FormResultVisitor,Contexed {
    private final AppContext conn;
    private final JFrame parent;
    private FormResult next_result=null;
    private Logger log;
    public SwingFormResultVisitor(JFrame parent,AppContext c){
    	this.parent=parent;
    	conn=c;
    	log = c.getService(LoggerService.class).getLogger(getClass());
    }
    public void reset(){
    	next_result=null;
    }
    public FormResult getNextResult(){
    	return next_result;
    }
	public <T, K> void visitChainedTransitionResult(
			ChainedTransitionResult<T, K> res) throws Exception {
		TransitionFactory<K, T> tp = res.getProvider();
		K key = res.getTransition();
		T target = res.getTarget();
		
		log.debug("transition="+key);
		if( key == null){
			// For ViewTransitionProvider we can default to view
			
			if( target != null && tp instanceof ViewTransitionFactory){
				log.debug("Show View page");
				ViewTransitionFactory<K, T> vtp = (ViewTransitionFactory<K, T>)tp;
				SessionService session_service = conn.getService(SessionService.class);
				if( ! vtp.canView(target, session_service)){
					doMessage("access_denied");
				}else{
					next_result = showViewForm(vtp,  target,session_service);
				}
				return;
			}
			log.debug("No transition");
			throw new InvalidArgument("No transition");
		}
	    // this is the access control
		if( ! tp.allowTransition(conn,target,key)){
			doMessage("access_denied");
        	return;
		}
		Transition<T> t=null;
		
		try{
		   synchronized(tp.getClass()){
			   DatabaseService db_serv = conn.getService(DatabaseService.class);
				boolean use_transactions = db_serv != null && TransitionServlet.TRANSITION_TRANSACTIONS.isEnabled(conn);
				if (use_transactions){
					db_serv.startTransaction();
					// re-fetch the target within the transaction
					target = tp.accept(new ReFetchTargetVisitor<T, K>(target) );
				}
				try{
					// to ensure consistency all modifications of queries and the checks
					// that operations are valid are protected by a lock.
					t = tp.getTransition(target,key);
					if( t != null ){
						next_result = t.getResult(new SwingTransitionVisitor<K,T>(conn, key, tp, target,parent));
					}
				}catch(TransitionException e){
					if( e instanceof FatalTransitionException){
						log.error("FatalTransitionException", e);
						if (use_transactions){
							db_serv.rollbackTransaction();
						}
					}
					throw e;
				}catch(Exception|ForceRollBack e){
					if (use_transactions){
						// assume this is bad and roll-back
						db_serv.rollbackTransaction();
					}
					throw e;
				}finally{
					if (use_transactions){
						// restore original mode (usually auto-commit)
						db_serv.stopTransaction();
					}
				}
		   }
		}catch(TransitionException e){
			doMessage( "transition_error",  key, e.getMessage());
			return;
		}
		if( t == null ){
			log.debug("Transition key did not resolve to a class");
			doMessage( "invalid_input");
			return;
		}
		
		
	}

	public <T, K> void visitConfirmTransitionResult(
			ConfirmTransitionResult<T, K> res) throws Exception {
		
		String message_type=res.getType();
		Object args[] = res.getArgs();
		
		boolean ok = confirm(conn,parent,message_type, args);
			  if( ok){
				  // we said yes
				  visitChainedTransitionResult(res);
			  }
			  return;
	}
	public static boolean confirm(AppContext conn, Frame parent, String message_type, Object ...args) {
		return confirmWithArgs(conn, parent, message_type, args);
	}
	public static boolean confirmWithArgs(AppContext conn, Frame parent, String message_type, Object[] args) {
		ResourceBundle mess = ResourceBundle.getBundle("confirm");
		String message_title="confirm request";
		String message_text="Are you sure";
		
		String yes_text="yes";
		String no_text="no";
		
		 yes_text = mess.getString("yes");
		  no_text = mess.getString("no");
		
			  try{
				  message_title = MessageFormat.format(mess.getString(message_type + ".title"),args);
			  }catch(MissingResourceException e){
				  conn.error(e,"missing confirm title for "+message_type);
			  }
		  
		  
			  try{
				  message_text = MessageFormat.format(mess.getString(message_type + ".text"),args);
			  }catch(MissingResourceException e){
				  conn.error(e,"missing confirm text for "+message_type);
			  }
			  int n = JOptionPane.showOptionDialog(parent, message_text, message_title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{yes_text,no_text}, no_text);
			  boolean ok = (n == 0);
		return ok;
	}

	

	public void visitMessageResult(MessageResult res) throws Exception {
		next_result=null;
		
		String message_type=res.getMessage();
		Object args[] = res.getArgs();
		
		doMessage(message_type, args);
	}
	public void doMessage(String message_type, Object ...args){
		doMessageWithArgs(message_type,args);
	}
	public void doMessageWithArgs(String message_type, Object[] args) {
		ResourceBundle mess = conn.getService(MessageBundleService.class).getBundle();
		String message_title="message";
		String message_text="message text missing";
		String message_extra="";
		Object body="No message";
		 try{
			  message_title = MessageFormat.format(conn.expandText(mess.getString(message_type + ".title")),args);
		  }catch(MissingResourceException e){
			  conn.error(e,"missing message title for "+message_type);
		  }
		  try{
			  message_text = MessageFormat.format(conn.expandText(mess.getString(message_type + ".text")),args);
			  body = message_text;
		  }catch(MissingResourceException e){
			  conn.error(e,"missing message text for "+message_type);
		  }
		  try{
			  message_extra = MessageFormat.format(conn.expandText(mess.getString(message_type + ".extra")),args);
			  if( message_extra != null ){
				  body = new Object[]{message_text,message_extra};
			  }
		  }catch(MissingResourceException e){
			  // Not an error
		  }
		  
		  
		  JOptionPane.showMessageDialog(parent, body,message_title,JOptionPane.INFORMATION_MESSAGE);
	}
	public AppContext getContext() {
		return conn;
	}

	/** Make a dialog for the view target operation
	 * 
	 * @param <T> target
	 * @param <K> keys
	 * @param provider
	 * @param target
	 * @param session_service 
	 * @return FormResult
	 */
	public <T,K> FormResult  showViewForm(ViewTransitionFactory<K, T> provider,T target,SessionService session_service){
		
		String type=provider.getTargetName();
	    String type_title = conn.getInitParameter("transition_title."+type,type);
		String page_title = "View "+type_title;
		JFormDialog dialog = new JFormDialog(conn, parent);
		SwingContentBuilder builder = dialog.getContentBuilder();
		provider.getLogContent(builder.getPanel("log"), target, session_service).addParent();
		BaseForm base = new BaseForm(conn);
		for(K key : provider.getTransitions(target)){
			if( provider.allowTransition(conn,target,key) ){
				base.addAction(key.toString(), new NestAction<K,T>(provider,key,target));
			}
		}
		builder.addActionButtons(base);
		dialog.setTitle(page_title);
		if( parent != null ) {
			parent.validate();
		}
		return dialog.showForm(base);
	}
	public void visitServeDataResult(
			ServeDataResult res) throws Exception {
		JFileChooser chooser = SwingFormComponentListener.getChooser(conn);
		
		MimeStreamData msd = res.getProducer().getData(conn.getService(SessionService.class),res.getArgs());
		if( msd == null ){
			doMessage("access_denied");
			return;
		}
		String name = msd.getName();
		if( name != null && name.length() > 0 ){
			File current_dir = chooser.getCurrentDirectory();
			File def = new File(current_dir, name);
			chooser.setSelectedFile(def);
		}
		int ret = chooser.showSaveDialog(parent);
		if( ret == JFileChooser.APPROVE_OPTION){
			File dest = chooser.getSelectedFile();
			FileOutputStream out = new FileOutputStream(dest);
			msd.write(out);
			out.close();
		}
		
	}
	public void visitBackResult(BackResult res) throws Exception {
		//Do nothing navigation 
		
	}

	public void visitCustomPage(CustomPageResult res) throws Exception {
		throw new UnsupportedResultException();
	}
	
}