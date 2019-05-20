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
package uk.ac.ed.epcc.webapp.content;

import java.io.IOException;
import java.io.Writer;

/** A  {@link Writer} that appends (raw un-cleaned) content to an underlying {@link XMLPrinter}
 * @author spb
 *
 */
public class XMLPrinterWriter extends Writer {

	private final XMLPrinter printer;
	/**
	 * 
	 */
	public XMLPrinterWriter(XMLPrinter printer) {
		super(printer);
		this.printer=printer;
	}

	

	/* (non-Javadoc)
	 * @see java.io.Writer#close()
	 */
	@Override
	public void close() throws IOException {
		

	}

	/* (non-Javadoc)
	 * @see java.io.Writer#flush()
	 */
	@Override
	public void flush() throws IOException {

	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[], int, int)
	 */
	@Override
	public void write(char[] chars, int off, int len) throws IOException {
		for(int i = off ; i < off+len ; i++){
			getPrinter().append(chars[i]);
		}
	}



	/**
	 * @return the printer
	 */
	public XMLPrinter getPrinter() {
		return printer;
	}

}
