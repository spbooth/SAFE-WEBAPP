package uk.ac.ed.epcc.webapp.model.serv;

import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** A {@link SettableServeDataProducer} wrapper that forces all content to external.
 * 
 */
public class ExternalSettableServeDataProducer implements SettableServeDataProducer {
	private final SettableServeDataProducer inner;
	private final String tag;
	public ExternalSettableServeDataProducer(AppContext conn, String tag) {
		this.tag=tag;
		this.inner=conn.makeObject(SettableServeDataProducer.class, conn.getInitParameter(tag+".inner_producer", DEFAULT_SERVE_DATA_TAG));
	}

	@Override
	public MimeStreamData getData(SessionService user, List<String> path) throws Exception {
		return inner.getData(user, path);
	}

	@Override
	public String getDownloadName(SessionService user, List<String> path) throws Exception {
		return inner.getDownloadName(user, path);
	}

	@Override
	public AppContext getContext() {
		
		return inner.getContext();
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public List<String> setData(MimeStreamData data) {
		return inner.setData(data);
	}

	@Override
	public final boolean isExternalContent(List<String> path) {
		return true;
	}

}
