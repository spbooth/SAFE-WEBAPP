package uk.ac.ed.epcc.webapp.model.data.forms;

import java.util.Map;

import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

/** Interface for {@link DataObjectFactory}s or {@link Composite}s
 * that customise form field help programatically
 * 
 * @author Stephen Booth
 *
 */
public interface FieldHelpProvider {
	
	/** return a default set of tooltip help text for form fields.
	 * 
	 * This method provides a class specific set of defaults. The individual Form classes can still override this.
	 * @param help
	 * @return {@link Map}
	 */
	default Map<String, String> addFieldHelp(Map<String, String> help){
		return help;
	}
}
