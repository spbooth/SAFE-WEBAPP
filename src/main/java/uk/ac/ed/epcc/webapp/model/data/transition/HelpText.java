package uk.ac.ed.epcc.webapp.model.data.transition;
/** Interface for a transition key object that can provide a help tooltip
 * 
 * @author Stephen Booth
 *
 */
public interface HelpText {
   public default String getHelp() {
	   return null;
   }
}
