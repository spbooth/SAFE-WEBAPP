package uk.ac.ed.epcc.webapp.content;

import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.servlet.MessageServlet.PathReWriter;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** A {@link PathReWriter} that stores the string representation of the message in the session.
 * 
 * This both shortens the URL and ensures the message cannot be tampered with
 * 
 */
public class SessionPathReWriter extends AbstractContexed implements PathReWriter {
	private static final String _DATA = "_MSG_";
	private static final String _COUNTER = "_COUNTER";
	private final String tag;
	private final SessionService session_service;
	public SessionPathReWriter(AppContext conn,String tag) {
		super(conn);
		this.tag=tag;
		this.session_service=conn.getService(SessionService.class);
	}
	public String getTag() {
		return tag;
	}
	private int getNext(){
		if(session_service == null ){
			throw new ConsistencyError("No session service");
		}
		String counter_attr = getTag()+_COUNTER;
		Integer counter = (Integer) session_service.getAttribute(counter_attr);
		if( counter == null ){
			counter = new Integer(1); // start at 1 so DB and session behave similarly in tests 
		}
		int id = counter.intValue();
		session_service.setAttribute(counter_attr, Integer.valueOf(id+1%100)); // re-use message tags
		return id;
	}
	@Override
	public LinkedList<String> encode(LinkedList<String> raw) {
		int id = getNext();
		session_service.setAttribute(getTag()+_DATA+id, raw);
		LinkedList<String> ref = new LinkedList<>();
		ref.add(Integer.toString(id));
		return ref;
	}

	@Override
	public LinkedList<String> decode(LinkedList<String> encoded) {
		if( encoded != null && ! encoded.isEmpty()) {
			try {
				int id = Integer.parseInt(encoded.getFirst());
				LinkedList<String> ll = (LinkedList<String>)session_service.getAttribute(getTag()+_DATA+id);
				if( ll != null ) {
					return new LinkedList<String>( ll);
				}
			}catch(Exception e) {
				getLogger().error("Error parsing message from session",e);
			}
		}
		LinkedList<String> err_msg = new LinkedList<String>();
		err_msg.add("session_expired");
		return err_msg;
	}

}
