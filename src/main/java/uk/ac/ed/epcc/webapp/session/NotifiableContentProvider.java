package uk.ac.ed.epcc.webapp.session;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.mail.internet.MimeMessage;
import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.PreformattedTextGenerator;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.content.TemplateFile;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter;
import uk.ac.ed.epcc.webapp.model.TemplateFinder;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;


public class NotifiableContentProvider<AU extends AppUser> extends AbstractContexed implements UIGenerator {

	public NotifiableContentProvider(AppContext conn) {
		super(conn);
	}

	@Override
	public ContentBuilder addContent(ContentBuilder builder) {
		SessionService<AU> sess = getContext().getService(SessionService.class);
		AppUserFactory<AU> login = sess.getLoginFactory();
		OrFilter<AU> fil = new OrFilter<AU>(login.getTarget(), login);
		Set<RequiredPage<AU>> requiredPages = login.getRequiredPages();
		for(RequiredPage<AU> rp : requiredPages) {
			fil.addFilter(rp.notifiable(sess));
		}
		AndFilter<AU> notify_filter = new AndFilter<AU>(login.getTarget(), fil, login.getEmailFilter(), login.getCanLoginFilter());
		MaxNotifyComposite<AU> max = login.getComposite(MaxNotifyComposite.class);
		// don't apply filter from max notify
		Table<String, AU> tab = new Table<>();
		try {
			Emailer em = new Emailer(getContext());
			for(AU p : login.getResult(notify_filter)) {
				if( p.canLogin()) {
					Set<String> notices = new LinkedHashSet<String>();
					for(RequiredPage<AU> rp : requiredPages) {
						rp.addNotifyText(notices,p);
					}
					if( ! notices.isEmpty()) {
						MimeMessage m = em.notificationMessage(p, notices);
						if( m != null ) {
							tab.put("Notications",p,new PreformattedTextGenerator(m.getContent().toString()));
						}else {
							tab.put("Notications",p, notices);
						}
					}
					if( max != null && max.apply()) {
						tab.put("Notifications sent", p, max.getNotifiedCount(p));
					}
				}
			}
		} catch (Exception e) {
			getLogger().error("Error generating notifications table",e);
		}
		tab.setKeyName("Person");
		if( tab.hasData()) {
			builder.addTable(getContext(), tab);
		}
		
		return builder;
	}

}
