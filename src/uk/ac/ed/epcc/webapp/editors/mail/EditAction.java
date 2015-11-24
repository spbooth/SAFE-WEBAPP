// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
/**
 * 
 */
package uk.ac.ed.epcc.webapp.editors.mail;
/** Email actions and transitions
 * 
 * @author spb
 *
 */
public enum EditAction{
	Edit,
	EditSubject,
	Update,
	Delete,
	Merge,
	Quote,
	Flatten,
	Send("Send Message"),
	Abort("Abort Message"),
	StartOver("Undo all edits"),
	AddRecipient,
	AddCC,
	AddTo,
	AddBcc,
	Serve,
	AddAttachment,
	Upload,
	New;
	private final String help;
	private EditAction(String h){
		help=h;
	}
	private EditAction(){
		help=null;
	}
	public String getHelp(){
		return help;
	}
}