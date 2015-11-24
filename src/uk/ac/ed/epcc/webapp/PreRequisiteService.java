// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp;
import java.lang.annotation.*; 

/** An annotation that defines services that are required by an AppContextService.
 * This does not include optional services that can be used but are not essential.
 * This allows the AppContext to build the requisites before attempting to instantiate the target
 * and should simplify the error reporting.
 * 
 * @author spb
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PreRequisiteService {
	public Class<? extends AppContextService>[] value();
}