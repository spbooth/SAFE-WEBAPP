// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/**
 * JFormDialog is a modal dialog window for editing the contents of a Form. This
 * class also implements any FormActions defined for the Form though the state
 * of the Form used to initialise this object will also be modified.
 * 
 * @author spb
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: JFormDialog.java,v 1.7 2014/11/20 23:38:01 spb Exp $")

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
		this.log = conn.getService(LoggerService.class).getLogger(getClass());
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
					JOptionPane.showMessageDialog(getParent(), "Form not valid",
							"General error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (ValidateException e1) {
				JOptionPane.showMessageDialog(getParent(), "Validation failed: "+e1.getMessage(),
						"General error", JOptionPane.ERROR_MESSAGE);
				return;
			} catch (Exception e1) {
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
		getContentPane().add(content.getComponent());
		pack();
		setVisible(true); //blocks until form completed.
		return result;
	}
	/** Get a panel containing the action buttons for a form.
	 * 
	 * @param form
	 * @return JPanel
	 */
	public JPanel getActionButtons(Form form){
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));
		buttons.add(Box.createHorizontalGlue());
		for (Iterator it = form.getActionNames(); it.hasNext();) {
			String action = (String) it.next();
			JButton b = new JButton(action);
			String help = form.getAction(action).getHelp();
			if( help != null && help.trim().length() > 0){
				b.setToolTipText(help);
			}
			buttons.add(b);
			b.addActionListener(new Action(form,action));
		}
		return buttons;
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