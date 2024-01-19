package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ObjectMapper;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.content.UIProvider;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
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

	public static final Feature MAP_MESSAGE = new Feature("message_servlet.map_message",true,"Automatically use MessageServlet for post/put operaitons");
	public static final String MESSAGE_PATH="/Message/";
	private static final char MARK = '^';
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
		Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
		return enc.encodeToString(s.getBytes(StandardCharsets.UTF_8));
		// In principal we should be able to use % encoding here
		// but tomcat seems to dislike % encoded / chars in a URL
		// Safer to use our own encoding scheme to ensure URL safe chars only
//		
//		StringBuilder output = new StringBuilder();
//		for (int i = 0; i < s.length(); i++) {
//			char ch = s.charAt(i);
//			if( ! Character.isISOControl(ch)) {
//				// non printable ascii or url reserved chars
//				if( ch > 127  || ch == MARK || 
//					ch == ':' || ch == '/' || ch == '?' || ch == '#' || ch == '[' || ch ==  ']' || ch == '@' ||
//                    ch == '!' || ch == '$' || ch == '&' || ch == '\'' || ch == '(' || ch ==  ')' ||
//				    ch == '*' || ch == '+' || ch == ',' || ch == ';' || ch == '=' ) {
//					output.append(MARK);
//					output.append(String.format("%02x", (int) ch));
//				}else {
//					output.append(ch);
//				}
//			}
//		}
//		return output.toString();
	}

	public static String decodeString( String s) {
		Base64.Decoder dec = Base64.getDecoder();
		return new String(dec.decode(s),StandardCharsets.UTF_8);
//		StringBuilder output = new StringBuilder();
//		for (int i = 0; i < s.length(); i++) {
//			char ch = s.charAt(i);
//			if( ch >= ' ' && ch <= '~') {
//				// only accept printable ascii as input
//				if( ch == MARK) {
//					int code = Integer.parseInt(s.substring(i+1, i+2), 16);
//					output.append((char)code);
//					i+=2;
//				}else{
//					output.append(ch);
//				}
//			}
//		}
//		return output.toString();
	}
	/** Attempt to map a {@link MessageResult} to a redirect to this servlet.
	 * If the mapping is not supported then return null;
	 * 
	 * @param conn
	 * @param mr
	 * @return RedirectResult or null
	 */
	public static RedirectResult mapResult(AppContext conn,MessageResult  mr) {
		if( ! MAP_MESSAGE.isEnabled(conn)) {
			return null;
		}
		StringBuilder url = new StringBuilder();
		 url.append(MessageServlet.MESSAGE_PATH);
		 url.append(mr.getMessage());
		 url.append("/");
		 for(Object a : mr.getArgs()) {
			 if( a instanceof UIGenerator || a instanceof UIProvider) {
				 UIGenerator g;
				 if( a instanceof UIGenerator) {
					 g = (UIGenerator) a;
				 }else {
					 g = ((UIProvider)a).getUIGenerator();
				 }
				 String v = ObjectMapper.map(conn, g);
				 if( v == null ) {
					 a = g.toString();
				 }else {
					 a = v;
				 }
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
		path = decodeString(path); // undo custom encoding
		Object res = ObjectMapper.unmap(conn, path);
		if( res != null) {
			return res;
		}

		return path;
	}
}
