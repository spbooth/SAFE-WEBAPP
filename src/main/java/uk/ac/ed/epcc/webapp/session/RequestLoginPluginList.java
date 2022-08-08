package uk.ac.ed.epcc.webapp.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AbstractConstructedObjectList;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.servlet.RequestLoginPlugin;
/** A {@link RequestLoginPlugin} that 
 * 
 * @author Stephen Booth
 *
 */
public class RequestLoginPluginList extends AbstractConstructedObjectList<RequestLoginPlugin> implements RequestLoginPlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RequestLoginPluginList(AppContext conn, String tag) {
		super(conn, tag);
	}

	@Override
	public FormResult requestLogin(HttpServletRequest req, HttpServletResponse res) {
		for(RequestLoginPlugin p : this) {
			FormResult r = p.requestLogin(req, res);
			if( r != null ) {
				return r;
			}
		}
		return null;
	}

	@Override
	protected Class<? super RequestLoginPlugin> getTemplate() {
		return RequestLoginPlugin.class;
	}

}
