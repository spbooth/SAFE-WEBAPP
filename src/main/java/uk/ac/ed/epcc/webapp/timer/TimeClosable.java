//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.timer;

import java.util.function.Supplier;

import uk.ac.ed.epcc.webapp.AppContext;

/** A {@link AutoCloseable} wrapper to allows timers to be closed automatically.
 * 
 * Its written to handle a null {@link TimerService}
 * @author Stephen Booth
 *
 */
public class TimeClosable implements AutoCloseable{
	/** start the named timer (if the service is not null).
	 * timer will be stopped when the object is closed.
	 * @param service
	 * @param name
	 */
	public TimeClosable(TimerService service, String name) {
		super();
		this.service = service;
		if( service != null ) {
			this.name = name;
			service.startTimer(name);
		}
	}
	/** start the named timer (if the service is not null).
	 * timer will be stopped when the object is closed.
	 * @param service
	 * @param name_supplier
	 */
	public TimeClosable(TimerService service, Supplier<String> name_supplier) {
		super();
		this.service = service;
		if( service != null ) {
			String name = name_supplier.get();
			this.name = name;
			service.startTimer(name);
		}
	}
	/** Create with the default {@link TimerService}
	 * 
	 * If no {@link TimerService} is enabled the {@link TimeClosable}
	 * will have no effect.
	 * 
	 * @param conn
	 * @param name
	 */
	public TimeClosable(AppContext conn,String name) {
		this(conn.getService(TimerService.class),name);
	}
	/** Create with the default {@link TimerService}
	 * 
	 * If no {@link TimerService} is enabled the {@link TimeClosable}
	 * will have no effect.
	 * 
	 * @param conn
	 * @param name_supplier
	 */
	public TimeClosable(AppContext conn,Supplier<String> name_supplier) {
		this(conn.getService(TimerService.class),name_supplier);
	}
	private final TimerService service;
	private String name;
	
	/** close the current timing phase and open a
	 * new one.
	 * 
	 * @param new_name
	 */
	public void change(String new_name) {
		if( service != null ) {
			service.stopTimer(name);
			service.startTimer(new_name);
		}
		name=new_name;
	}
	
	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close()  {
		if( service != null ) {
			service.stopTimer(name);
		}

	}

}
