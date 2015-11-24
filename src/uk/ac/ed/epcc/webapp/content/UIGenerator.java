package uk.ac.ed.epcc.webapp.content;


/** Interface for objects that can add themselves to an {@link ContentBuilder}
 * 
 * @author spb
 *
 */
public interface UIGenerator {
	public ContentBuilder addContent(ContentBuilder builder);
}
