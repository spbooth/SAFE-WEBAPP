// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.editors.mail;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryCreator;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/** A {@link TransitionFactoryCreator} that creates an
 * {@link EmailTransitionProvider} from the tag of the {@link MessageHandlerFactory}.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: EmailTransitionFactoryCreator.java,v 1.4 2014/09/15 14:30:15 spb Exp $")
public class EmailTransitionFactoryCreator implements TransitionFactoryCreator<EmailTransitionProvider>,Contexed {
	/**
	 * 
	 */
	public static final String MESSAGE_PROVIDER_FACTORY_TAG_PREFIX = "MessageProviderFactory.";
	private final AppContext conn;
	public EmailTransitionFactoryCreator(AppContext conn){
		this.conn=conn;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryCreator#getTransitionProvider(java.lang.String)
	 */
	public EmailTransitionProvider getTransitionProvider(String tag) {
		MessageHandlerFactory fac;
		int index = tag.indexOf(MessageHandlerFactoryCreator.TYPE_SEPERATOR);
		if( index > 0){
			MessageHandlerFactoryCreator creator = conn.makeObject(MessageHandlerFactoryCreator.class, "MessageProviderFactoryCreator."+tag.substring(0, index));
			fac = creator.getMessageHandlerFactory(tag.substring(index+1));
		}else{
			fac = conn.makeObjectWithDefault(MessageHandlerFactory.class,null, MESSAGE_PROVIDER_FACTORY_TAG_PREFIX+tag);
			if( fac == null ){
				fac = conn.makeObjectWithDefault(MessageHandlerFactory.class,null,tag);
			}
			if( fac == null ){
				conn.getService(LoggerService.class).getLogger(getClass()).error("Cannot find MessageHandler with tag "+tag);
				return null;
			}
		}
		return new EmailTransitionProvider(fac);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	public AppContext getContext() {
		return conn;
	}

}
