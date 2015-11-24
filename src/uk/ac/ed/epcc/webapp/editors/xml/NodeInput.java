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
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
/** A Text input for XML nodes.
 * 
 * @author spb
 *
 */
public class NodeInput extends TextInput implements ItemInput<Node>{

	private Transformer transformer;
	private AppContext conn;
	private Document doc;
	private Schema schema;
	public NodeInput(AppContext conn,Document doc,TransformerFactory fac,Schema schema) throws TransformerConfigurationException {
		super();
		setOptional(false);
		setSingle(false);
		setMaxResultLength(1<<24);
		this.conn=conn;
		this.doc=doc;
		this.schema=schema;
		transformer=fac.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	}



	
	public Node getItem() {
		DocumentFragment frag = doc.createDocumentFragment();
		DOMResult result = new DOMResult(frag);
		StreamSource source = new StreamSource(new StringReader(getValue()));
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



	@Override
	public void validate() throws FieldException {
		super.validate();
		DocumentFragment frag = doc.createDocumentFragment();
		DOMResult result = new DOMResult(frag);
		StreamSource source = new StreamSource(new StringReader(getValue()));
		try {
			transformer.transform(source, result);
			// can't schema validate a fragment reliably 
			
//			if( schema != null ){
//				Validator v = schema.newValidator();
//				source = new StreamSource(new StringReader(getValue()));
//				v.validate(source);
//			}
		} catch (Exception e1) {
			throw new ValidateException(e1.getMessage());
		}
		
	}

}
