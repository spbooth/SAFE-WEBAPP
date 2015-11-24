// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.exec;

/** Interface for the result of an external process.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public interface ProcessProxy {

	/** get the output of the process
	 * 
	 * @return String
	 */
	public abstract String getOutput();

	/** get the erro output of the process
	 * 
	 * @return
	 */
	public abstract String getErr();

	/** get the return code
	 * 
	 * @return
	 */
	public abstract Integer getExit();
	/** did the process time-out or killed via interrupt
	 * 
	 * @return
	 */
	public abstract boolean wasTerminated();
}