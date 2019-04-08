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

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
/** A Text input for XML nodes.
 * 
 * @author spb
 *
 */
public class NodeInput extends TextInput implements ItemInput<String,Node>{

	private Transformer transformer;
	private AppContext conn;
	private Document doc;
	private Schema schema;
	public NodeInput(AppContext conn,Document doc,TransformerFactory fac,Schema schema) throws TransformerConfigurationException {
		super();
		setSingle(false);
		setMaxResultLength(1<<24);
		this.conn=conn;
		this.doc=doc;
		this.schema=schema;
		transformer=fac.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		addValidator(new FieldValidator<String>() {
			
			@Override
			public void validate(String data) throws FieldException {
				DocumentFragment frag = doc.createDocumentFragment();
				DOMResult result = new DOMResult(frag);
				StreamSource source = new StreamSource(new StringReader(data));
				try {
					transformer.transform(source, result);
					// can't schema validate a fragment reliably 
					
//					if( schema != null ){
//						Validator v = schema.newValidator();
//						source = new StreamSource(new StringReader(getValue()));
//						v.validate(source);
//					}
				} catch (Exception e1) {
					throw new ValidateException(e1.getMessage());
				}
				
			}
		});
	}



	
	public Node getItembyValue(String value) {
		DocumentFragment frag = doc.createDocumentFragment();
		DOMResult result = new DOMResult(frag);
		StreamSource source = new StreamSource(new StringReader(value));
		try {
			transformer.transform(source, result);
			return frag;
		} catch (TransformerException e1) {
			return null;
		}
	}

	
	public void setItem(Node item) {
		DOMSource source = new DOMSource(item);
		StreamResult output = new StreamResult(new StringWriter());
		try{
			transformer.transform(source, output);
			setValue(output.getWriter().toString());
		}catch(Exception e){
			conn.error(e,"error setting Node as item");
		}
	}

}