/** Annotation to specify the construction tag for {@link ObjectMapper}s 
 * that target this type
 * 
 */
package uk.ac.ed.epcc.webapp.content;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
/**
 * 
 */
public @interface HasObjectMapper {
	public String tag();
}
