// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
@uk.ac.ed.epcc.webapp.Version("$Id: FormResultActionListener.java,v 1.2 2014/09/15 14:30:21 spb Exp $")


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