package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Tagged;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** An object that encodes rules for mapping a target to and
 * from a sequence of string values. This is intended for encoding objects
 * within URLs etc so also supports access control. Ideally we would use the access control to ensure the
 * objects are safe to use. However in practice we are usually only able to ensure the user
 * has permission to use (which is not the same) so we usually need some other mechanism to ensure the
 * integrity of the URL
 * 
 * @param <X> type of target
 */
public interface ObjectMapper<X> extends Tagged{

	public static final String SEPERATOR = "@";
	public static final char SEPERATOR_CHAR = SEPERATOR.charAt(0);
	public String[] encode(X target);
	
	
	/**
	 * 
	 * @param args
	 * @return
	 */
	public X decode(String args[]);
	
	/** An access control 
	 * 
	 * @param sess
	 * @param target 
	 * @return
	 */
	public boolean allowAccess(SessionService sess,X target);
	
	/** Check if an object can be encoded by this {@link ObjectMapper}
	 * 
	 * @param target
	 * @return
	 */
	public boolean isMine(Object target);
	
	/** Use an {@link ObjectMapper} to convert a target object into
	 * a single identifying string.
	 * 
	 * @param <X>
	 * @param mapper
	 * @param target
	 * @return
	 */
	public static <X> String map(ObjectMapper<X> mapper, X target) {
		StringBuilder sb = new StringBuilder();
		sb.append(SEPERATOR);
		escape(sb,mapper.getTag());
		sb.append(SEPERATOR);
		for(String a : mapper.encode(target)) {
			escape(sb,a);
			sb.append(SEPERATOR);
		}
		return sb.toString();
	}
	/** Attempt to map an arbitrary object to a String
	 * This relies on the target class having an {@link HasObjectMapper} annotation
	 * If mapping is not possible for any reason return null.
	 * 
	 * @param <X>
	 * @param conn
	 * @param target
	 * @return String or null.
	 */
	public static <X> String map(AppContext conn,X target) {
		if(target == null) {
			return null;
		}
		HasObjectMapper h = target.getClass().getAnnotation(HasObjectMapper.class);
		if( h == null ) {
			return null;
		}
		ObjectMapper<X> mapper = conn.makeObject(ObjectMapper.class, h.tag());
		if( mapper != null && mapper.isMine(target) && mapper.allowAccess(conn.getService(SessionService.class), target)) {
			return map(mapper,target);
		}
		return null;
	}
	public static boolean isMap(String in) {
		return in.startsWith(SEPERATOR) && in.endsWith(SEPERATOR);
	}
	/** Convert the output of the {@link #map(ObjectMapper, Object)} method
	 * back into a target object.
	 * 
	 * This method returns null on any error or if the current session
	 * is unauthenticated or not permitted to view the object
	 * 
	 * @param <X>
	 * @param conn
	 * @param in
	 * @return target or null.
	 */
	public static <X> X unmap(AppContext conn, String in) {
		if( (! in.startsWith(SEPERATOR)) || ! in.endsWith(SEPERATOR)) {
			// not correct format
			return null;
		}
		SessionService sess = conn.getService(SessionService.class);
		if( sess == null || ! sess.haveCurrentUser()) {
			return null;
		}
		in = in.substring(1,in.length() -1);
		String args[] = in.split(SEPERATOR);
		String tag = unescape(args[0]);
		String parts[] = new String[args.length-1];
		for(int i=1;i<args.length;i++) {
			parts[i-1]=unescape(args[i]);
		}
		ObjectMapper<X> mapper = conn.makeObject(ObjectMapper.class, tag);
		if( mapper == null ) {
			return null;
		}
		X result = mapper.decode(parts);
		
		if( result == null ||  ! mapper.allowAccess(sess,result)) {
			return null;
		}
		return result;
 	}
	public static void escape(StringBuilder sb,String in) {
		for(int i=0;i<in.length();i++) {
			char c = in.charAt(i);
			if( c == SEPERATOR_CHAR) {
				sb.append("~1");
			}else if ( c == '~') {
				sb.append("~~");
			}else {
				sb.append(c);
			}
		}
	}
	public static String  unescape(String in) {
		StringBuilder result = new StringBuilder();
		for(int i=0;i<in.length();i++) {
			char c = in.charAt(i);
			if( c == '~') {
				i++;
				char tag = in.charAt(i);
				if( tag == '1') {
					result.append(SEPERATOR_CHAR);
				}else if( tag == '~') {
					result.append('~');
				}
			}else {
				result.append(c);
			}
		}
		return result.toString();
	}
}
