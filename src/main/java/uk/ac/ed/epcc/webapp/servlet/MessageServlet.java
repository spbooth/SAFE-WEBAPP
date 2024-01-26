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
	
	public MessageServlet() {
		// TODO Auto-generated constructor stub
	}

	/** Interface for plug-ins that modify the message path. 
	 * e.g. adding a signature or using the session to shorten the url.
	 * For security there has to be at least one {@link PathReWriter} that ensures integrity of the 
	 * data. Otherwise it might be possible to insert spoofed content (e.g. presenting a link
	 * to approve an account as something else)
	 * 
	 */
	public static interface PathReWriter{
		LinkedList<String> encode(LinkedList<String> raw);
		
		LinkedList<String> decode(LinkedList<String> encoded);
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
		LinkedList<String> list = new LinkedList<>();
		for(int i=0;i<args.length;i++) {
			list.add(args[i]);
		}
		AppContext conn= ErrorFilter.retrieveAppContext(req, res);
		for(PathReWriter w : getReWriters(conn)) {
			list = w.decode(list);
		}
		
		String message_type = list.pop();
		List<Object> arg_path = new LinkedList<>();
		for(String s : list) {
			arg_path.add(decodeArg(conn, s));
		}
		
		messageWithArgs(conn, req, res, message_type, arg_path.toArray());
	}

	public static List<PathReWriter> getReWriters(AppContext conn){
		List<PathReWriter> l = new LinkedList<>();
		String plugins = conn.getInitParameter("message_servlet.path_rewriters");
		if( plugins != null && ! plugins.isEmpty()) {
			for(String s : plugins.split("\\s*,\\s*")) {
				PathReWriter w = conn.makeObject(PathReWriter.class, s);
				if( w != null) {
					l.add(w);
				}
			}
		}
		return l;
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
		// In principal we should be able to use % encoding here
		// but tomcat seems to dislike % encoded / chars in a URL
		// Safer to use Base64 even though the data is always strings
		Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
		return enc.encodeToString(s.getBytes(StandardCharsets.UTF_8));
		

	}

	public static String decodeString( String s) {
		Base64.Decoder dec = Base64.getUrlDecoder();
		return new String(dec.decode(s),StandardCharsets.UTF_8);

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
		LinkedList<String> args = new LinkedList<>();
		args.add(mr.getMessage());
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
					// Unsupported UIGenerator type safer to show inline
					// than assume string representation is sufficient
					return null;
				}else {
					a = v;
				}
			}
			// ok to just convert to string.
			args.add(MessageServlet.encodeArg(conn, a));
		}
		for(PathReWriter w : getReWriters(conn)) {
			args = w.encode(args);
		}
		
		StringBuilder url = new StringBuilder();
		url.append(MessageServlet.MESSAGE_PATH);
		for(String p : args) {
			url.append(p);
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
