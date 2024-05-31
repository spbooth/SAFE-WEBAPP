package uk.ac.ed.epcc.webapp.forms.inputs;
/** Interface for text Inputs that can specify an autocomplete attribute in html. 
 * ie a hint to the browser about the purpose of the input.
 * 
 */
public interface AutoCompleteHint {
	/** Get the value of the autocomplete attribute
	 * return null to supress this.
	 * @return
	 */
	public String getAutoCompleteHint();
}
