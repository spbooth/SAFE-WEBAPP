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

/** A {@link TemplateFile.PropertyPolicy} that adds {@link XMLGenerator}s
 * directly to the {@link XMLPrinter} if the {@link Writer} is a {@link XMLWriter}
 * @author spb
 *
 */
public class XMLPrintWriterPolicy implements TemplateFile.PropertyPolicy {

	private final TemplateFile.PropertyPolicy inner;
	/**
	 * 
	 */
	public XMLPrintWriterPolicy(TemplateFile.PropertyPolicy inner) {
		this.inner=inner;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.TemplateFile.DefaultPropertyPolicy#writePropertyValue(java.io.Writer, java.lang.String, java.lang.Object)
	 */
	@Override
	public void writePropertyValue(Writer out, String name, Object value) throws IOException {
		if( value instanceof XMLGenerator && out instanceof XMLPrinterWriter){
			((XMLGenerator)value).addContent(((XMLPrinterWriter)out).getPrinter());
			return;
		}
		inner.writePropertyValue(out, name, value);
	}

}
