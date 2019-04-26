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
import java.util.Map;

import uk.ac.ed.epcc.webapp.content.TemplateFile.PropertyPolicy;

/** a {@link PropertyPolicy} that stores {@link XMLGenerator}s and
 * records a reference as a processing instruction for use by a {@link XMLBuilderSaxHandler}
 * @author spb
 *
 */
public class XMLPropertyPolicy implements TemplateFile.PropertyPolicy{

	private final Map<String,XMLGenerator> data;
	private final TemplateFile.PropertyPolicy inner;
	/**
	 * 
	 */
	public XMLPropertyPolicy(Map<String,XMLGenerator> data,TemplateFile.PropertyPolicy inner) {
		this.data=data;
		this.inner=inner;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.TemplateFile.PropertyPolicy#writePropertyValue(java.io.Writer, java.lang.String, java.lang.Object)
	 */
	@Override
	public void writePropertyValue(Writer out, String name, Object value) throws IOException {
		if( value instanceof XMLGenerator){
			data.put(name, (XMLGenerator) value);
			out.write("<?");
			out.write(XMLBuilderSaxHandler.EXTERNAL_CONTENT_PI);
			out.write(" ");
			out.write(name);
			out.write("?>");
		}else{
			inner.writePropertyValue(out, name, value);
		}
		
	}

}
