// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.exec;

/** A timeout thread that will interrupt another after a specified length of time.
 * 
 *This thread will just exit if interrupted itself.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class TimeoutThread extends Thread {
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			sleep(timeout);
			parent.interrupt();
		} catch (InterruptedException e) {
			
		}
		
	}
	/**
	 * @param timeout
	 * @param parent
	 */
	public TimeoutThread(long timeout, Thread parent) {
		super();
		this.timeout = timeout;
		this.parent = parent;
	}
	private final long timeout;
	private final Thread parent;

}
