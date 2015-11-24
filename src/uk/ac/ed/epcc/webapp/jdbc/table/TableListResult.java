// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
@uk.ac.ed.epcc.webapp.Version("$Id: TableListResult.java,v 1.2 2014/09/15 14:30:26 spb Exp $")


public class TableListResult extends RedirectResult {

	public TableListResult() {
		super("/tables/index.jsp");
	}

}