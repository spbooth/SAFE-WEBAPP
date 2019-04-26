//| Copyright - The University of Edinburgh 2017                            |
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
package uk.ac.ed.epcc.webapp.logging.java;

import java.util.function.Supplier;
import java.util.logging.Level;

import uk.ac.ed.epcc.webapp.logging.Logger;

/** a {@link Logger} that maps the the java.util.logger logs.
 * @author spb
 *
 */
public class JavaLoggerWrapper implements Logger {

	public JavaLoggerWrapper(String sourceClass, String souceMethod, java.util.logging.Logger log) {
		super();
		this.sourceClass = sourceClass;
		this.sourceMethod = souceMethod;
		this.log = log;
	}

	private final java.util.logging.Logger log;
	private final String sourceClass;
	private final String sourceMethod;
	

	private void log(Level l,Object message) {
		log.logp(l, sourceClass, sourceMethod, message.toString());
	}
	private void log(Level l,Object message,Throwable t) {
		log.logp(l, sourceClass, sourceMethod, message.toString(),t);
	}
	
	private void log(Level l,Supplier<String> message) {
		log.logp(l, sourceClass, sourceMethod, message);
	}
	private void log(Level l,Supplier<String> message,Throwable t) {
		log.logp(l, sourceClass, sourceMethod, t,message);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#debug(java.lang.Object)
	 */
	@Override
	public void debug(Object message) {
		log(Level.FINE,message);
	}
	
	@Override
	public void debug(Supplier<String> message) {
		log(Level.FINE,message);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#debug(java.lang.Object, java.lang.Throwable)
	 */
	@Override
	public void debug(Object message, Throwable t) {
		log(Level.FINE, message, t);

	}
	
	@Override
	public void debug(Supplier<String> message, Throwable t) {
		log(Level.FINE, message, t);

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#error(java.lang.Object)
	 */
	@Override
	public void error(Object message) {
		log(Level.SEVERE,message);

	}
	
	@Override
	public void error(Supplier<String> message) {
		log(Level.SEVERE,message);

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#error(java.lang.Object, java.lang.Throwable)
	 */
	@Override
	public void error(Object message, Throwable t) {
		log(Level.SEVERE, message, t);

	}
	
	@Override
	public void error(Supplier<String> message, Throwable t) {
		log(Level.SEVERE, message, t);

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#fatal(java.lang.Object)
	 */
	@Override
	public void fatal(Object message) {
		log(Level.SEVERE,message);

	}
	
	@Override
	public void fatal(Supplier<String> message) {
		log(Level.SEVERE,message);

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#fatal(java.lang.Object, java.lang.Throwable)
	 */
	@Override
	public void fatal(Object message, Throwable t) {
		log(Level.SEVERE, message, t);

	}
	@Override
	public void fatal(Supplier<String> message, Throwable t) {
		log(Level.SEVERE, message, t);

	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#info(java.lang.Object)
	 */
	@Override
	public void info(Object message) {
		log(Level.INFO,message);

	}
	@Override
	public void info(Supplier<String> message) {
		log(Level.INFO,message);

	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#info(java.lang.Object, java.lang.Throwable)
	 */
	@Override
	public void info(Object message, Throwable t) {
		log(Level.INFO, message, t);

	}
	@Override
	public void info(Supplier<String> message, Throwable t) {
		log(Level.INFO, message, t);

	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#warn(java.lang.Object)
	 */
	@Override
	public void warn(Object message) {
		log(Level.WARNING,message);

	}
	@Override
	public void warn(Supplier<String> message) {
		log(Level.WARNING,message);

	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#warn(java.lang.Object, java.lang.Throwable)
	 */
	@Override
	public void warn(Object message, Throwable t) {
		log(Level.WARNING, message, t);

	}
	@Override
	public void warn(Supplier<String> message, Throwable t) {
		log(Level.WARNING, message, t);

	}
}
