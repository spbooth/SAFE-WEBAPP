// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Deprecated
public @interface Version {
  String value() default "unknown";
}