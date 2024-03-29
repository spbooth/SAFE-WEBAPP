//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.logging.print;

import java.io.PrintStream;

import uk.ac.ed.epcc.webapp.logging.Logger;

/**
 * Implementation of the Logger Interface using simple prints to System.out
 * Allows us to produce lightweight command line applications without the need
 * to include a full Logging framework.
 * 
 * @author spb
 * 
 */


public class PrintWrapper implements Logger {
    private final PrintStream dest;
	
    public PrintWrapper(PrintStream dest){
    	this.dest=dest;
    }
    public PrintWrapper(){
    	this(System.out);
    }
	@Override
	public void debug(Object message) {
		dest.println(message);
	}

	@Override
	public void debug(Object message, Throwable t) {
		dest.println(message);
		if( t != null ){
			t.printStackTrace(System.err);
		}
	}

	@Override
	public void error(Object message) {
		dest.println(message);

	}

	@Override
	public void error(Object message, Throwable t) {
		dest.println(message);
		if( t != null ){
			t.printStackTrace(System.err);
		}
	}

	@Override
	public void fatal(Object message) {
		dest.println(message);

	}

	@Override
	public void fatal(Object message, Throwable t) {
		dest.println(message);
		if( t != null ){
			t.printStackTrace(System.err);
		}
	}

	@Override
	public void info(Object message) {
		dest.println(message);
	}

	@Override
	public void info(Object message, Throwable t) {
		dest.println(message);
		if( t != null ){
			t.printStackTrace(System.err);
		}
	}

	@Override
	public void warn(Object message) {
		dest.println(message);

	}

	@Override
	public void warn(Object message, Throwable t) {
		dest.println(message);
		if( t != null ){
			t.printStackTrace(System.err);
		}
	}

}