package uk.ac.ed.epcc.webapp.model.relationship;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.result.CustomPageResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;

public class RelationshipList<X extends DataObject> extends CustomPageResult{
	public RelationshipList(AppContext conn,String relationship, DataObjectFactory<X> fac,X target) {
		super();
		this.conn=conn;
		this.relationship = relationship;
		this.fac=fac;
		this.target = target;
	}

	private final AppContext conn;
	private final String relationship;
	private final DataObjectFactory<X> fac;
	private final X target;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.result.CustomPage#getTitle()
	 */
	@Override
	public String getTitle() {
		if( target == null ) {
			return "People with relationship "+relationship+" on any target";
		}
		return "People with relationship "+relationship+" on "+target.getIdentifier();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.result.CustomPage#addContent(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.ContentBuilder)
	 */
	@Override
	public ContentBuilder addContent(AppContext conn, ContentBuilder cb) {
		cb.addHeading(1, getTitle());
		SessionService sess = conn.getService(SessionService.class);
		ContentBuilder defn = cb.getDetails("Implementation");
		defn.addObject(sess.explainRelationship(fac, relationship));
		defn.closeDetails();
		try {
			cb.addList(sess.getPeopleInRelationship(fac, relationship, target));
		} catch (Exception e) {
			cb.addText("Internal error occured");
			Logger.getLogger(conn,getClass()).error("Error making list",e);
		}
		return cb;
	}
}