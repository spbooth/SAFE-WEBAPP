package uk.ac.ed.epcc.webapp.model.data.forms;

import java.util.Map;

import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFormFactory;
/** Interface for {@link DataObjectFormFactory}s of {@link Composite}s
 * that generate additional form labels programatically.
 * Normally form labels are static content and should be set in the appropriate contente bundles
 * but some fields are generated programatically so there needs to be an extenstion to allow the labels to 
 * be set that way too.
 * 
 * @author Stephen Booth
 *
 */
public interface FormLabelProvider {
	/**
	 * return a default set of translation between field names and text labels.
	 * This method provides a class specific set of defaults. The individual Form classes can still override this.
	 * @param translations
	 * @return {@link Map}
	 */
	Map<String, String> addTranslations(Map<String, String> translations);
}
