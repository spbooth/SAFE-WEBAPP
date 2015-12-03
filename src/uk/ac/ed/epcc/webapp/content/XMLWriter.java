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
package uk.ac.ed.epcc.webapp.content;

import java.io.IOException;
import java.io.Writer;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;

public class XMLWriter extends AbstractXMLBuilder {
	private Writer writer;
	public XMLWriter(Writer w) {
		writer = w;
	}

	public SimpleXMLBuilder getNested() throws UnsupportedOperationException {
		return new XMLPrinter(this);
	}

	public SimpleXMLBuilder appendParent() throws UnsupportedOperationException {
		try {
			writer.close();
		} catch (IOException e) {
			throw new ConsistencyError("Error closing writer", e);
		}
		return null;
	}

	public SimpleXMLBuilder getParent() {
		
		return null;
	}

	@Override
	protected void append(CharSequence s) {
		try {
			writer.append(s);
		} catch (IOException e) {
			throw new ConsistencyError("Error writting text", e);
		}
	}

	@Override
	protected void append(char s) {
		try {
			writer.append(s);
		} catch (IOException e) {
			throw new ConsistencyError("Error writting text", e);
		}
	}

	/** Close the existing {@link Writer} an redirect output
	 * to a new {@link Writer}
	 * 
	 * @param w
	 * @throws IOException 
	 */
    public void setWriter(Writer w) throws IOException{
    	writer.close();
    	writer=w;
    }

}