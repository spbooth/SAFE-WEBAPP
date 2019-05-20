package uk.ac.ed.epcc.webapp.model.data;

import java.util.Set;

/** Interface for classes/composites that register the Configuration parameters they use.
 * 
 * @author spb
 *
 */
public interface ConfigParamProvider {
	public void addConfigParameters(Set<String> params);
}
