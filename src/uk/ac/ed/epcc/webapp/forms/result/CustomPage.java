package uk.ac.ed.epcc.webapp.forms.result;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
/** Interface for objects that create a custom page of content.
 * 
 * @author spb
 *
 */
public interface CustomPage {
	public static final String CUSTOM_PAGE_TAG="CustomPage";
   public String getTitle();
   public ContentBuilder addContent(AppContext conn,ContentBuilder cb);
}
