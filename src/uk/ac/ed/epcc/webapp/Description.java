// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/** A Run-time annotation giving a brief description of a
 * type for run-time reflection based documentation.
 * 
 * @author spb
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Description {
  String value() default "";
}