package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.content.UIProvider;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
/** A servlet for displaying messages via a redirect.
 * 
 * This works for both unauthenticated and authenticated users.
 * Because the message and arguments are entirely encoded in the URL care must be taken to
 * both sanitise the arguments and avoid information leakage.
 * 
 */
@WebServlet(name="MssageServlet",urlPatterns = MessageServlet.MESSAGE_PATH+"*")
public class MessageServlet extends WebappServlet {

	public static final String MESSAGE_PATH="/Message/";
	public MessageServlet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse res, AppContext conn)
			throws ServletException, IOException {
		res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		return;
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getPathInfo();
		if( path == null || path.isEmpty()) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		String args[] = path.substring(1).split("/");
		if( args.length < 1) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		String message_type = args[0];
		AppContext conn= ErrorFilter.retrieveAppContext(req, res);
		List<Object> arg_path = new LinkedList<>();
		for(int i=1 ; i < args.length ; i++) {
			arg_path.add(decodeArg(conn, args[i]));
		}
		
		messageWithArgs(conn, req, res, message_type, arg_path.toArray());
	}

	
	/** Encode and argument as message path element.
	 * The primary purpose here is to encode arbitrary strings as valid path elements.
	 * If a user session is in place it can encode object by reference. However
	 * the decode method will need to perform access control so unless that method can
	 * prove the user is allowed to see the object this method should generate a simple 
	 * string representing the content.
	 * 
	 * @param conn
	 * @param o
	 * @return
	 */
	public static String encodeArg(AppContext conn, Object o) {
		return encodeString(o.toString());  // Placeholder
	}
	
	public static String encodeString(String s) {
		StringBuilder output = new StringBuilder();

		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			// unreserved chars
			if (((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z'))
					|| ((ch >= '0') && (ch <= '9'))|| (ch == '_')
					|| (ch == '-') || (ch == '.') ) {
				output.append(ch);
			} else {
				output.append("%");
				output.append(String.format("%02x", (int) ch));
			}
		}
		return output.toString();
	}

	/** Attempt to map a {@link MessageResult} to a redirect to this servlet.
	 * If the mapping is not supported then return null;
	 * 
	 * @param conn
	 * @param mr
	 * @return RedirectResult or null
	 */
	public static RedirectResult mapResult(AppContext conn,MessageResult  mr) {
		StringBuilder url = new StringBuilder();
		 url.append(MessageServlet.MESSAGE_PATH);
		 url.append(mr.getMessage());
		 url.append("/");
		 for(Object a : mr.getArgs()) {
			 if( a instanceof UIGenerator || a instanceof UIProvider) {
				 return null;
			 }
			 // ok to just convert to string.
			 url.append(MessageServlet.encodeArg(conn, a));
			 url.append("/");

		 }
		 return new RedirectResult(url.toString());
	}
	
	/** Decode an element of the path into a message argument
	 * The primary purpose is to decode arbitrary string values.
	 * If a user session is in place this may be an object reference but if the object type supports
	 * access control then it is an error to return an object the user does not have the ability to view.
	 * 
	 * @param conn
	 * @param path
	 * @return
	 */
	public static Object decodeArg(AppContext conn, String path) {
		return path;
	}
}
