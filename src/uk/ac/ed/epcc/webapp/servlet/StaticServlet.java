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
package uk.ac.ed.epcc.webapp.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * This servlet serves static content from a location outside the application
 * install tree.
 * 
 * @author spb
 * 
 */


public class StaticServlet extends SessionServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class ExistFilter implements FilenameFilter {
		File dir;

		public ExistFilter(File d) {
			dir = d;
		}

		public boolean accept(File dir, String name) {
			File f = new File(dir.getAbsolutePath() + "/" + name);
			return f.exists() && f.canRead() && !f.isHidden();
		}

	}

	public StaticServlet() {
		super();
	}

	/**
	 * send access denied message
	 * 
	 * @param conn
	 * @param req
	 * @param res
	 * @throws IOException
	 * @throws ServletException
	 */
	private void access_denied(AppContext conn, HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {

		message(conn, req, res, "access_denied");
		return;
	}

	/**
	 * Generate a directory listing.
	 * 
	 * @param conn
	 * @param request
	 * @param response
	 * @param f
	 * @throws IOException
	 * @throws ServletException
	 * @throws IOException
	 */
	private void do_directory(AppContext conn,
			HttpServletRequest request, HttpServletResponse response, File f)
			throws ServletException, IOException {

		String list[] = f.list(new ExistFilter(f));
		String prefix=request.getContextPath()+request.getServletPath();
		String path =request.getPathInfo();
		if( path != null ){
			prefix=prefix+path;
		}
		if( ! prefix.endsWith("/")){
			prefix=prefix+"/";
		}
		String display_page = conn.getInitParameter("static.display-page");
		if (display_page != null) {
			// get an external jsp to format the directory listing.
			request.setAttribute("list", list);
			request.setAttribute("prefix", prefix);
			conn.getService(ServletService.class).forward(display_page);
			return;
		} else {
			String real_page = f.getAbsolutePath();
			
			Header(request, response, "Listing of " + prefix);
			PrintWriter out = response.getWriter();
			out.println("<ul>");
			for (int i = 0; i < list.length; i++) {
				File e = new File(real_page + "/" + list[i]);
				if (e != null) {
					if (!e.isHidden()) {
						if (e.isDirectory()) {
							out.println("<LI> <a href=\""
									+ response.encodeURL(prefix+list[i]) + "/\">"
									+ list[i] + "</a></LI>");
						}
						if (e.isFile()) {
							out.println("<LI> <a href=\""
									+ response.encodeURL(prefix+list[i]) + "\">"
									+ list[i] + "</a></LI>");
						}
					}
				}
			}

			out.println("</ul>");
			Footer(request, response);
		}
		return;
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn, SessionService person) throws Exception {
		Logger log = getLogger(conn);
		String param_basedir=conn.getExpandedProperty("static.basedir");
		if( param_basedir == null ){
			getLogger(conn).warn("static.basedir not set in StaticServlet");
			message(conn, req, res, "invalid_argument");
			return;
		}
		StringBuilder basedir = new StringBuilder(param_basedir);
        log.debug("In StaticServlet");
		log.debug("basedir="+basedir);
		// strip trailing slashes
		while (basedir.charAt(basedir.length() - 1) == '/') {
			basedir.deleteCharAt(basedir.length() - 1);
		}
		String spath = req.getPathInfo();
		if (spath == null || spath.length() == 0) {
			spath = "/";
		}
		log.debug("spath is " + spath);
		// avoid the ability to backtrack out of the directory
		if (spath.indexOf("..") >= 0) {
			access_denied(conn, req, res);
			return;
		}
		String target = basedir + spath;
		log.debug("Request to " + target);
		serve_file(conn, req, res, target);

	}

	/**
	 * get the mime tyope string for a file suffix defaults to text/html unless
	 * otherwise specified in the context parameters.
	 * 
	 * @param conn
	 * @param suffix
	 * @return String
	 */
	private String mimeType(AppContext conn, String suffix) {
		return conn.getInitParameter("mime." + suffix, "text/html");
	}

	/**
	 * send file not found denied message
	 * 
	 * @param conn
	 * @param req
	 * @param res
	 * @throws IOException
	 * @throws ServletException
	 */
	private void not_found(AppContext conn, HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {

		message(conn, req, res, "file_not_found");
		return;
	}

	private void serve_file(AppContext conn, HttpServletRequest req,
			HttpServletResponse res, String target) throws IOException,
			ServletException {
		File f = new File(target);

		Logger log = conn.getService(LoggerService.class).getLogger(conn.getClass());
		if (!f.exists() || f.isHidden()) {
			log.debug("File not found or hidden");
			not_found(conn, req, res);
			return;
		}
		if (!f.canRead()) {
			log.debug("file unreadable");
			access_denied(conn, req, res);
			return;
		}
		if (f.isDirectory()) {
			log.debug("file is directory");
			do_directory(conn, req, res, f);
			return;
		}
		log.debug("serving file");
		long length = f.length();
		String type = mimeType(conn, suffix(target));
		res.setContentType(type);
		res.setContentLength((int) length);
		try(OutputStream out = res.getOutputStream();
				InputStream in = new FileInputStream(f)){
			int val;
			while ((val = in.read()) != -1) {
				out.write(val);
			}
		}
		
	}

	/**
	 * get the file suffix for this type of file
	 * 
	 * @param target
	 * @return String suffix
	 */
	private String suffix(String target) {
		int pos = target.lastIndexOf('.');
		if (pos == -1)
			return "html";
		return target.substring(pos + 1);
	}

	protected static void Footer(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		PrintWriter out = res.getWriter();
		out.println("<hr></body></html>");
		out.close();
	}

	protected static void Header(HttpServletRequest req,
			HttpServletResponse res, String title) throws IOException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		out.println("<html lang='en'><head><title>" + title
				+ "</title></head><body></html>");
		out.println("<h2 align='center'>" + title + "</h2><hr>");
	}


}