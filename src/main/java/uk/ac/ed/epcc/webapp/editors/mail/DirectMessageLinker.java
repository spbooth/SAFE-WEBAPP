package uk.ac.ed.epcc.webapp.editors.mail;

import java.util.List;


/** A {@link MessageLinker} that can also generate a direct url
 * so t htmlparts can be shown in embedded iframes (
 * 
 */
public interface DirectMessageLinker extends MessageLinker {

	public String getLocation(List<String> args) throws Exception;
}
