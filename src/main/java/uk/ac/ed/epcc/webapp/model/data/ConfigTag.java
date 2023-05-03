package uk.ac.ed.epcc.webapp.model.data;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/** An annotation indicating an additional configuration tag
 * that can be used to customise a value associated with a database field.
 * These are added to string constants containing the field name.
 * 
 * 
 * @see FieldHandler
 * @author Stephen Booth
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface ConfigTag {
	public String value();
}
