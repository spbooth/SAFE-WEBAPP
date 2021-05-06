package uk.ac.ed.epcc.webapp.model.data.transition;

/** Interface for {@link TransitionKey}s that want to provide custom button text for a
 * transition. The text will usually also undergo parameter expansion.
 * 
 * @author Stephen Booth
 *
 */
public interface ButtonText {
	public String getText();
}
