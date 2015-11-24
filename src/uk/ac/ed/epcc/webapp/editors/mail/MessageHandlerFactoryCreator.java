// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.editors.mail;


/** Interface for classes that can generate a {@link MessageHandlerFactory}.
 * This is intended for cases where a single class can create several different
 * types of message.
 * 
 * The {@link MessageHandlerFactory} is parameterised by the target factory
 * so we use a 2 step creation process. 
 * 
 * @author spb
 * @param <P> type of {@link MessageHandlerFactory}
 */
@uk.ac.ed.epcc.webapp.Version("$Id: MessageHandlerFactoryCreator.java,v 1.3 2014/09/15 14:30:16 spb Exp $")
public interface MessageHandlerFactoryCreator<P extends MessageHandlerFactory> {
	public static final char TYPE_SEPERATOR = ':';

	public P getMessageHandlerFactory(String name);
}
