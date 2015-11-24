// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.servlet;

import javax.servlet.ServletContextEvent;
import org.apache.commons.io.FileCleaningTracker;
import org.apache.commons.io.FileCleaner;
/** ContextListener that ensures threads created by apache commons file upload
 * are shutdown.
 * 
 * failing to shutdown or delaying initialisation seems to cause classloader 
 * memory leaks.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: FileUploadWebappContextListener.java,v 1.5 2014/09/15 14:30:34 spb Exp $")

public class FileUploadWebappContextListener extends WebappContextListener {
   
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		//TODO update jars
		//FileCleaningTracker tracker = FileCleaningTracker.g
		FileCleaner.exitWhenFinished();
	}

	

}