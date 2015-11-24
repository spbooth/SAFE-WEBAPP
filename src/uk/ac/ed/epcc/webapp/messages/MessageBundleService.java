// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.messages;
import java.util.ResourceBundle;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.config.ConfigService;

/** An {@link AppContextService} that encodes the rules for generating the application
 * message bundle. The default is to use the bundle <em>messages</em>
 * this can be changed using the property <b>messages.bundle.list</b> which may specify multiple bundles via 
 * a comma separated list.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: MessageBundleService.java,v 1.3 2015/06/23 15:29:14 spb Exp $")
@PreRequisiteService(ConfigService.class)
public class MessageBundleService implements Contexed, AppContextService<MessageBundleService> {
	private static ListControl control = new ListControl();
	private AppContext context;
	private ResourceBundle bundle=null;
	public MessageBundleService(AppContext c){
		context=c;
	}
	
	public ResourceBundle getBundle(){
		if( bundle == null ){
			bundle=makeBundle();
		}
		return bundle;
	}
	protected ResourceBundle makeBundle(){
		ConfigService config = context.getService(ConfigService.class);
		String bundles= config.getServiceProperties().getProperty("messages.bundle.list", "messages");
		return ResourceBundle.getBundle(bundles, control);

	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#cleanup()
	 */
	public void cleanup() {
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	public Class<? super MessageBundleService> getType() {
		return MessageBundleService.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	public AppContext getContext() {
		return context;
	}

	

}
