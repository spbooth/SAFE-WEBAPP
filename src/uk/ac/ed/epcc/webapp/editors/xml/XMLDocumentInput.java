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
package uk.ac.ed.epcc.webapp.editors.xml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
/** A Text input for XML nodes.
 * 
 * @author spb
 *
 */
public class XMLDocumentInput extends TextInput implements ItemInput<Document>{

	private Transformer transformer;
	private AppContext conn;
	private Schema schema;
	public XMLDocumentInput(AppContext conn,TransformerFactory fac,Schema schema) throws TransformerConfigurationException {
		super();
		setOptional(false);
		setSingle(false);
		setMaxResultLength(1<<24);
		this.conn=conn;
		this.schema=schema;
		transformer=fac.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	}



	public Document getItem() {
		DOMResult result = new DOMResult();
		StreamSource source = new StreamSource(new StringReader(getValue()));
		try {
			transformer.transform(source, result);
			return (Document) result.getNode();
		} catch (TransformerException e1) {
			return null;
		}
	}

	public void setItem(Document item) {
		DOMSource source = new DOMSource(item);
		StreamResult output = new StreamResult(new StringWriter());
		try{
			transformer.transform(source, output);
			setValue(output.getWriter().toString());
		}catch(Exception e){
			conn.error(e,"error setting Node as item");
		}
	}



	@Override
	public void validate() throws FieldException {
		super.validate();
		DOMResult result = new DOMResult();
		StreamSource source = new StreamSource(new StringReader(getValue()));
		try {
			transformer.transform(source, result);
			
			if( schema != null ){
				Validator v = schema.newValidator();
				source = new StreamSource(new StringReader(getValue()));
				v.validate(source);
			}
		} catch (Exception e1) {
			// messages can be very long without breaks
			throw new ValidateException(e1.getMessage().replaceAll(",", ", "));
		}
		
	}

}