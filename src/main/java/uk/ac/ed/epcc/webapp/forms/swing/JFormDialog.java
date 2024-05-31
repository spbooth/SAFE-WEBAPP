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
package uk.ac.ed.epcc.webapp.forms.swing;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.*;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.logging.Logger;

/**
 * JFormDialog is a modal dialog window for editing the contents of a Form. This
 * class also implements any FormActions defined for the Form though the state
 * of the Form used to initialise this object will also be modified.
 * 
 * @author spb
 * 
 */


public class JFormDialog extends JDialog implements Contexed{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final AppContext conn;
	Logger log;
	private FormResult result=null; // result of form this is set by submit Action
	private JFrame parent;
	private SwingContentBuilder content;
	public JFormDialog(AppContext conn, JFrame parent) {
		super(parent, true);
		this.conn=conn;
		this.log = Logger.getLogger(conn,getClass());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		content=new SwingContentBuilder(this);
	}
	/** ActionListener that closes the window on submit.
	 * 
	 * @author spb
	 *
	 */
	public class Action implements ActionListener {
		String action;
		Form form;
		public Action(Form form,String a) {
			this.form=form;
			action = a;
		}

		public void actionPerformed(ActionEvent e) {
			try {
				log.debug("Action "+action+" called");
				if( content.validate(form) ){
					log.debug("form validated ok");
					String message=form.getAction(action).getConfirm(form);
					boolean ok=true;
					if( message != null ){
						ok = SwingFormResultVisitor.confirm(conn, parent, message);
					}
					if( ok ){
						result = form.doAction(action);
					}
					dispose();
				}else{
					log.debug("form not valid");
					JOptionPane.showMessageDialog(getParent(), "Form not valid",
							"General error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (ValidateException e1) {
				log.debug("validate exception",e1);
				JOptionPane.showMessageDialog(getParent(), "Validation failed: "+e1.getMessage(),
						"General error", JOptionPane.ERROR_MESSAGE);
				return;
			} catch (Exception e1) {
				log.debug("other exception",e1);
				String message = e1.getMessage();
				if( message == null || message.length() == 0){
					message = e1.toString();
				}
				JOptionPane.showMessageDialog(getParent(), message,
						"General error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}
	public SwingContentBuilder getContentBuilder(){
		return content;
	}

/** Show the dialog form. 
 * @param f the {@link Form} being shown.
 * @return FormResult
 */
	public FormResult showForm(Form f){
		content.setComponentValues();
		JComponent comp = content.getComponent();
		
		JScrollPane scroll = new JScrollPane(comp);
	
		getContentPane().add(scroll);
		getContentPane().validate();
		pack();
		setVisible(true); //blocks until form completed.
		return result;
	}
	/** Get a panel containing the action buttons for a form.
	 * 
	 * @param form
	 * @return JPanel
	 */
	public JPanel getActionButtons(Form form,Set<String> actions){
		JPanel buttons = new JPanel();
		buttons.setLayout(new WrapLayout(FlowLayout.RIGHT,5,10));
		//buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));
		//buttons.add(Box.createHorizontalGlue());
		buttons.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		for (String action : actions) {
			JButton b = getActionButton(form, action);
			
			buttons.add(b);
		}
		buttons.validate();
		return buttons;
	}

	public JButton getActionButton(Form form, String action) {
		JButton b = new JButton(action);
		String help = form.getAction(action).getHelp();
		if( help != null && help.trim().length() > 0){
			b.setToolTipText(help);
		}
		b.addActionListener(new Action(form,action));
		return b;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	public AppContext getContext() {
		return conn;
	}
	public JFrame getFrame() {
		return parent;
	}
}