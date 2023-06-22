package uk.ac.ed.epcc.webapp.content;

import java.util.Set;

/** A {@link UIGenerator} that needs additional supporting javascript
 * 
 * @author Stephen Booth
 *
 */
public interface ScriptUIGenerator extends UIGenerator {

	/** Additional script to add to page
	 * 
	 * @return
	 */
	public String getScript();
}
