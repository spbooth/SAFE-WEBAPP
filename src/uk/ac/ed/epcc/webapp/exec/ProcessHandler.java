//| Copyright - The University of Edinburgh 2015                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
package uk.ac.ed.epcc.webapp.exec;

import java.io.IOException;
import java.io.OutputStream;

/** Wrapper thread to wait for a process in a thread. 
 * 
 * Additional threads are spawned to capture any output
 * When the process exits the thread also exits so waiting for the thread is equivalent to
 * waiting for the process. If the thread is interrupted the process will be destroyed.
 * 
 * Optionally input to the process can be provided. This will be provided to the process asynchronously by the {@link ProcessHandler}
 * thread rather than using the main thread. 
 * @author spb
 *
 */
public class ProcessHandler extends Thread implements ProcessProxy {
	
	/** time to wait for output threads after process exits.
	 * 
	 */
	private static final long CLEANUP_WAIT = 5000L;
	private final Process process;
	private Integer exit=null;
	private byte input[];
	private IOException input_exception; 
	private InputStreamThread out;
	private InputStreamThread err;
	private TimeoutThread timeout=null;
	boolean terminated=false;
	public ProcessHandler(Process process){
		this(null,process);
	}
	public ProcessHandler(byte input[],Process process) {
		this.input=input;
		this.process = process;
	}
	@Override
	public void run() {
		try { 
			// don't close as main thread may be providing input.
			if( input != null ){
				OutputStream stdin = process.getOutputStream();
				try {
					stdin.write(input);
					stdin.close();
				} catch (IOException e) {
					input_exception=e;
				}
			}
			
			exit = process.waitFor();
			
		} catch (InterruptedException ignore) {
			process.destroy();
			terminated=true;
		}
		// wait for output threads but not forever
		if ( out !=null ){
			try {
				out.join(CLEANUP_WAIT);
			} catch (InterruptedException e) {
				
			}
		}
		if ( err !=null ){
			try {
				err.join(5000L);
			} catch (InterruptedException e) {
				
			}
		}
		if( timeout != null && timeout.isAlive() && ! timeout.isInterrupted()){
			timeout.interrupt();
		}
	}  
	
	/** Set an automatic timeout for the {@link ProcessHandler} implemented by a thread.
	 * This can be used if the spawning thread is not going to wait for completion.
	 * 
	 * @param timeout
	 */
	public void setTimeout(long timeout){
		if( timeout <= 0 ){
			return;
		}
		this.timeout = new TimeoutThread(timeout, this);
	}
	
	
	/** get the exit value of the process, This may return null if the timeout is  reached.
	 * 
	 */
	public Integer getExit(){
		return exit;
	}
	public IOException getInputException(){
		return input_exception;
	}
	
	@Override
	public synchronized void start() {
		out = new InputStreamThread(process.getInputStream());
		out.start();
		err = new InputStreamThread(process.getErrorStream());
		err.start();
		if( timeout != null ){
			timeout.start();
		}
		super.start();
	}
	/** blocking call to the process. The process will be destoyed if the timeout is exceeded.
	 * @param timeout_millis 
	 * @return exit code
	 * @throws Exception 
	 * 
	 */
	public Integer execute(long timeout_millis) throws Exception{
		start();
		complete(timeout_millis);
		return getExit();
				
	}
	/** wait for the process to complete. If the timeout is exceeded 
	 * the process is forcebly destroyed.
	 * 
	 * @param timeout_millis
	 * @throws InterruptedException
	 */
	public void complete(long timeout_millis) throws InterruptedException {
		join(timeout_millis);
		int count=0;
		while( isAlive()){
			terminated=true;
			process.destroy();
			join(1000L);
			exit=null;  // force exit to null if killed.
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.exec.ProcessProxy#getOutput()
	 */
	public String getOutput(){
		if( out == null ){
			return null;
		}
		return out.getString();
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.exec.ProcessProxy#getErr()
	 */
	public String getErr(){
		if( err == null ){
			return null;
		}
		return err.getString();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.exec.ProcessProxy#wasInterrupted()
	 */
	@Override
	public boolean wasTerminated() {
		return terminated;
	}
}