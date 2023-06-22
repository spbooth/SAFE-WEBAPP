package uk.ac.ed.epcc.webapp.forms;
/** Interface that generates text to use 
 * when a form is shown to a user. This translates 
 * form field names to custom text
 * 
 * @author Stephen Booth
 *
 */
public interface FormTextGenerator {

	/** Get the label text to use for a field
	 * 
	 * @param field
	 * @return
	 */
	public String getLabel(String field);
	/** Get the tooltip/help-text to use for a field
	 * 
	 * @param field
	 * @return
	 */
	public String getFieldHelp(String field);
}
