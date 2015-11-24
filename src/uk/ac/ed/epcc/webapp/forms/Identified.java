package uk.ac.ed.epcc.webapp.forms;

public interface Identified {

	/**
	 * Advisory maximum length for the Identifier string.
	 * 
	 */
	public static final int MAX_IDENTIFIER = 64;

	/**
	 * produce a unique identifying string for this object for use in forms and
	 * pull-down menus and charts. The string should be kept shorter than
	 * MAX_IDENTIFIER if at all possible. This defaults to the ID number for the
	 * object but classes should override this with something more sensible.
	 * 
	 * @return String
	 */
	public abstract String getIdentifier();
	
	/** Generate Identifier with advisory max length.
	 * 
	 * @param max_length advisory max length
	 * @return String identifier
	 */
	public abstract String getIdentifier(int max_length);

}