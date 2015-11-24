// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * 
 * 
 * abstract servlet class that does the basic authentication. retreives the
 * current person and passes it to the doPost method.
 * 
 * @author spb
 */
public abstract class SessionServlet extends WebappServlet {

	/*
	 * We make this method final to ensure we don't accidentally bypass the
	 * security restrictions in subclasses
	 */
	@Override
	public final void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn) throws ServletException,
			java.io.IOException {

		ServletSessionService serv = getSessionService(conn);
		if (serv == null || ! authorized(serv)) {
			conn.getService(ServletService.class).requestAuthentication(serv);
			return;
		}

		try {
			doPost(req, res, conn, serv);
		} catch (ServletException e) {
			// higher levels can handle this
			throw e;
		} catch (Throwable e) {
			// we could throw an enclosing ServletException here but we have all
			// the context we need here and
			// best to handle as soon as we can in case ErrorFilter not
			// configured.
			conn.error(e,  "Throwable caught in SessionServlet");
			return;
		}
	}

	@Override
	public final void doPut(HttpServletRequest req, HttpServletResponse res,
			AppContext conn) throws ServletException,
			java.io.IOException {

		ServletSessionService serv = getSessionService(conn);
		if (serv == null || ! authorized(serv)) {
			conn.getService(ServletService.class).requestAuthentication(serv);
			return;
		}

		try {
			doPut(req, res, conn, serv);
		} catch (ServletException e) {
			// higher levels can handle this
			throw e;
		} catch (Throwable e) {
			// we could throw an enclosing ServletException here but we have all
			// the context we need here and
			// best to handle as soon as we can in case ErrorFilter not
			// configured.
			conn.error(e,  "Throwable caught in SessionServlet");
			return;
		}
	}
	/** Extension point to implement additional authorisation mechanisms on a per servlet basis.
	 * 
	 * @param conn
	 * @return
	 */
	protected ServletSessionService getSessionService(AppContext conn) {
		return (ServletSessionService) conn.getService(SessionService.class);
	}

	protected boolean authorized(ServletSessionService serv) {
		return serv.haveCurrentUser();
	}

	

	/**
	 * work to be performed after authorisation check.
	 * 
	 * @param req
	 * @param res
	 * @param conn
	 * @param person
	 * @throws Exception
	 */
	public abstract void doPost(HttpServletRequest req,
			HttpServletResponse res, AppContext conn, SessionService person)
			throws Exception;
	
	/** handle put requests
	 * 
	 * @param req
	 * @param res
	 * @param conn
	 * @param person
	 * @throws Exception
	 */
	public void doPut(HttpServletRequest req,
			HttpServletResponse res, AppContext conn, SessionService person)
			throws Exception{
		
	}
}