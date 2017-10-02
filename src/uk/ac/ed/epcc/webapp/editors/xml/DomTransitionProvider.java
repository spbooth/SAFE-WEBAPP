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

import java.io.StringWriter;
import java.util.LinkedList;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.transition.AbstractViewPathTransitionProvider;
import uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer;
import uk.ac.ed.epcc.webapp.model.serv.SettableServeDataProducer;
import uk.ac.ed.epcc.webapp.session.SessionDataProducer;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** a {@link PathTransitionProvider} for editing XML documents.
 * While a full schema driven interface seems to be impractically difficult.
 * at the moment it should be posible to use the schema to:
 * <ul>
 * <li> mark up syntax errors at the location they occur.
 * <li> provide optional choices for elements/namespaces (maybe only those already used in document).
 * </ul>
 * 
 * 
 * @author spb
 */
public class DomTransitionProvider extends AbstractViewPathTransitionProvider<XMLTarget, XMLKey> implements PathTransitionProvider<XMLKey, XMLTarget>, ViewTransitionFactory<XMLKey,XMLTarget> {
	public static final class HasParentKey extends XMLKey {
		public HasParentKey(String name, String help) {
			super(name, help);
		}

		@Override
		public boolean allow(XMLTarget target, SessionService<?> sess) {
			Node n = target.getTargetNode();
			if( n == null ){
				return false;
			}
			if( n.getNodeType() == Node.ATTRIBUTE_NODE ){
				return true;
			}
			Node parent = n.getParentNode();
			if( parent == null ){
				return false;
			}
			if( parent == target.getDocument()){
				return false;
			}
			return true;
		}
	}

	public static class DownLoadTransition extends AbstractDirectTransition<XMLTarget>{

		public FormResult doTransition(XMLTarget target, AppContext c)
				throws TransitionException {
			try{
			SettableServeDataProducer producer = c.makeObjectWithDefault(SettableServeDataProducer.class,SessionDataProducer.class,ServeDataProducer.DEFAULT_SERVE_DATA_TAG );
			if( producer == null){
				throw new TransitionException("No ServeDataProducer configured");
			}
			DOMSource source = new DOMSource(target.getTargetNode());
			StreamResult output = new StreamResult(new StringWriter());
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.transform(source, output);
			ByteArrayMimeStreamData d = new ByteArrayMimeStreamData(output.getWriter().toString().getBytes());
			d.setMimeType("text/xml");
			return new ServeDataResult(producer, producer.setData(d));
			}catch(Exception e){
				c.getService(LoggerService.class).getLogger(getClass()).error("Error making download",e);
				throw new TransitionException("Internal error");
			}
		}
		
	}
	public static class DeleteTransition extends AbstractDirectTransition<XMLTarget>{
		public FormResult doTransition(XMLTarget target, AppContext c)
				throws TransitionException {
			Node n = target.getTargetNode();
			LinkedList<String> path=target.getTargetPath();
			path.removeLast();
			XMLTarget next = makeXMLTarget(path,c);
			if( next == null){
				throw new TransitionException("No parent target in delete");
			}
			if( n.getNodeType() == Node.ATTRIBUTE_NODE){
				Attr a = (Attr) n;
				Element e = a.getOwnerElement();
				e.removeAttributeNode(a);
			}else{
				Node parent = n.getParentNode();
				parent.removeChild(n);
			}
			try {
				target.commit();
			} catch (Exception e) {
				c.getService(LoggerService.class).getLogger(getClass()).error("Error updating document",e);
				throw new TransitionException("Operation failed");
			}
			return new ViewXMLTargetResult(next);
		}
		
	}
    
	public static class ParentTransition extends AbstractDirectTransition<XMLTarget>{
		public FormResult doTransition(XMLTarget target, AppContext c)
				throws TransitionException {
			LinkedList<String> path=target.getTargetPath();
			path.removeLast();
			XMLTarget next = makeXMLTarget(path,c);
			if( next == null){
				throw new TransitionException("No parent target in delete");
			}
			return new ViewXMLTargetResult(next);
		}
		
	}
	private static final String TEXT_FORM_FIELD = "Text";

	public static class EditAction extends FormAction{
		private final XMLTarget target;
		public EditAction(XMLTarget target){
			this.target=target;
		}
		@Override
		public FormResult action(Form f) throws ActionException {
			Node n = target.getTargetNode();
			final String text = (String)f.get(TEXT_FORM_FIELD);
			n.setNodeValue(text);
			assert(text.equals(n.getNodeValue()));
			try {
				target.commit();
			} catch (Exception e) {
				target.getContext().getService(LoggerService.class).getLogger(getClass()).error("Error updating document",e);
				throw new TransitionException("Operation failed");
			}
			return new ViewXMLTargetResult(target);
		}
		
	}
	public static class EditTransition extends AbstractFormTransition<XMLTarget>{
		public void buildForm(Form f, XMLTarget target, AppContext conn)
				throws TransitionException {
			Node n = target.getTargetNode();
			String value = n.getNodeValue();
			final TextInput input = new TextInput();
			input.setOptional(false);
			if( value != null && value.length() > 8){
				input.setBoxWidth(value.length());
			}
			input.setValue(value);
			if( n.getNodeType() == Node.ATTRIBUTE_NODE){
				input.setSingle(true);
			}
			f.addInput(TEXT_FORM_FIELD, "Content text", input);
			f.addAction("Update", new EditAction(target));
			
		}
		
	}
	public static class EditNodeAction extends FormAction{
		private final XMLTarget target;
		public EditNodeAction(XMLTarget target){
			this.target=target;
		}
		@Override
		public FormResult action(Form f) throws ActionException {
			Node n = target.getTargetNode();
			// remove children
			NodeList children = n.getChildNodes();
			for(int i=0; i<children.getLength();i++){
				n.removeChild(children.item(i));
			}
			Node frag = (Node) f.getItem(TEXT_FORM_FIELD);
			
			Node parent = n.getParentNode();
			parent.replaceChild(frag,n);
			try {
				target.commit();
			} catch (Exception e) {
				target.getContext().getService(LoggerService.class).getLogger(getClass()).error("Error updating document",e);
				throw new TransitionException("Operation failed");
			}
			LinkedList<String> path=target.getTargetPath();
			path.removeLast();
			XMLTarget next = makeXMLTarget(path,target.getContext());
			if( next == null){
				return new ViewXMLTargetResult(target);
			}
			return new ViewXMLTargetResult(next);
			
		}
		
	}
	public static class EditNodeTransition extends AbstractFormTransition<XMLTarget>{
		public void buildForm(Form f, XMLTarget target, AppContext conn)
				throws TransitionException {
			
			Node n = target.getTargetNode();
			try {
				NodeInput input = new NodeInput(conn,target.getDocument(),TransformerFactory.newInstance(),target.getXMLTargetFactory().getSchema());
				input.setItem(n);
				f.addInput(TEXT_FORM_FIELD, "Node text", input);
				f.addAction("Update", new EditNodeAction(target));
			} catch (Exception e) {
				conn.getService(LoggerService.class).getLogger(getClass()).error("Error building node edit form",e);
				throw new TransitionException("Internal error");
			}
			
		}
		
	}
	public static XMLKey DELETE_KEY = new HasParentKey("Delete", "Remove this XML node");

	public static XMLKey PARENT_KEY = new HasParentKey("Up", "Go to parent node");

	public static XMLKey EDIT_TEXT_KEY = new XMLKey("Edit") {
		
		@Override
		public boolean allow(XMLTarget target, SessionService<?> sess) {
			Node n = target.getTargetNode();
			if(n == null || n.getNodeValue() == null){
				return false;
			}
			return true;
		}
	};
	public static XMLKey EDIT_NODE_KEY = new XMLKey("Edit Node","Edit XML Node as text") {
		
		@Override
		public boolean allow(XMLTarget target, SessionService<?> sess) {
			Node n = target.getTargetNode();
			Node parent = n.getParentNode();
			if(n == null || n.getNodeType() != Node.ELEMENT_NODE || parent == null || parent.getNodeType() == Node.DOCUMENT_NODE){
				return false;
			}
			return true;
		}
	};
	public static XMLKey DOWNLOAD_KEY = new XMLKey("Download","Download text") {
		
		@Override
		public boolean allow(XMLTarget target, SessionService<?> sess) {
			return true;
		}
	};
	private Logger log;
	public DomTransitionProvider(AppContext c) {
		super(c);
		log=c.getService(LoggerService.class).getLogger(getClass());
		addTransition(PARENT_KEY, new ParentTransition());
		addTransition(EDIT_TEXT_KEY, new EditTransition());
		addTransition(EDIT_NODE_KEY, new EditNodeTransition());
		addTransition(DOWNLOAD_KEY, new DownLoadTransition());
		addTransition(DELETE_KEY, new DeleteTransition());
	}

	
	public String getTargetName() {
		return "XML";
	}

	
	public boolean allowTransition(AppContext c, XMLTarget target, XMLKey key) {
		final SessionService sess = c.getService(SessionService.class);
		if( target == null || ! target.canView(sess)){
			return false;
		}
		if( key == null ){
			// we know we can view
			return true;
		}
		return key.allow(target, sess);
	}

	public <X extends ContentBuilder> X getSummaryContent(AppContext c, X cb,
			XMLTarget target) {
		Table<String,String> props=new Table<String,String>();
		StringBuilder sb = new StringBuilder();
		for(String s : target.getTargetPath()){
			sb.append("/");
			sb.append(s);
		}
		props.put("Value", "Path",sb.toString());
		Node n = target.getTargetNode();
		if( n != null ){
			if(n.getNodeType()==Node.ELEMENT_NODE){
				props.put("Value", "Node Type", "Element");
				Element e = (Element)n;
				String ns=e.getNamespaceURI();
				if( ns != null ){
					props.put("Value", "Node Namespace", ns);
				}
			}else if( n.getNodeType() == Node.TEXT_NODE){
				props.put("Value", "Type", "Text");
			}else if( n.getNodeType() == Node.COMMENT_NODE){
				props.put("Value", "Type", "Comment");
			}else if( n.getNodeType() == Node.ATTRIBUTE_NODE){
				props.put("Value", "Type", "Attribute");
				Attr a = (Attr)n;
				String ns=a.getNamespaceURI();
				if( ns != null ){
					props.put("Value", "Namespace", ns);
				}
				props.put("Value", "Name", a.getName());
				props.put("Value", "Value", a.getNodeValue());
			}
		}
		props.setKeyName("Property");
		cb.addColumn(getContext(), props,"Value");
		return cb;
	}

	
	public boolean canView(XMLTarget target, SessionService<?> sess) {
		if( target != null ){
			return target.canView(sess);
		}
		return false;
	}

	
	public XMLTarget getTarget(LinkedList<String> id) {
		AppContext conn = getContext();
		return  makeXMLTarget(id, conn);
	}

	public static XMLTarget makeXMLTarget(LinkedList<String> id, AppContext conn) {
		if( id.size() == 0 ){
			return null;
		}
		XMLTargetFactory fac = getXMLTargetFactory(id, conn);
		if( fac == null){
			return null;
		}
		return  fac.find(id);
	}
	public static String formatPath(LinkedList<String> path){
		StringBuilder sb = new StringBuilder();
		boolean seen=false;
		for(String s : path){
			if( seen){
				sb.append(",");
			}
			sb.append(s);
			seen=true;
		}
		return sb.toString();
	}
	private static XMLTargetFactory getXMLTargetFactory(LinkedList<String> id,
			AppContext conn) {
		XMLTargetFactory fac = conn.makeObject(XMLTargetFactory.class,id.getFirst());
		return fac;
	}

	
	public LinkedList<String> getID(XMLTarget target) {
		return target.getTargetPath();
	}

	@Override
	public <X extends ContentBuilder> X getLogContent(X cb, XMLTarget target,
			SessionService<?> sess) {
		getSummaryContent(getContext(), cb, target);
		LinkedList<String> target_path = target.getTargetPath();
		InfoProvider info = getInfo(target);
		if( info != null ){
			TypeInfo ti = info.getTypeInfo(target_path);
			if( ti != null){
				Table<String,String> t = new Table<String,String>();
				t.put("Value", "Schema NameSpace", ti.getTypeNamespace());
				t.put("Value", "Schema Type", ti.getTypeName());
				t.setKeyName("Properties");
				cb.addColumn(getContext(), t, "Value");
//			}else{
//				Map<LinkedList<String>,TypeInfo> types = info.getTypes();
//				Table<String,String> t = new Table<String,String>();
//				for(LinkedList<String > path : types.keySet() ){
//					
//					TypeInfo i = types.get(path);
//					String p = formatPath(path);
//					t.put("Namespace", p, i.getTypeNamespace());
//					t.put("Type", p, i.getTypeName());
//				}
//				t.setKeyName("Path");
//				cb.addTable(getContext(), t);
			}
		}
		Node view = target.getTargetNode();
		if( view != null ){
			if(view.getNodeType() == Node.ELEMENT_NODE){
				ViewDomVisitor v = new ViewDomVisitor(getContext(), cb);
				if( info != null){
					v.setNotices(info.getErrors());
				}
				DomWalker walker = new DomWalker();
				walker.setLogger(log);
				walker.visitElement((Element)view, target_path, v);
			}else if( view.getNodeType() == Node.TEXT_NODE){
				ExtendedXMLBuilder panel = cb.getText();
				panel.clean(view.getNodeValue());
				panel.appendParent();
			}
		}
		return cb;
	}

	private InfoProvider getInfo(XMLTarget target) {
		if( target == null){
			return null;
		}
		XMLTargetFactory fac = target.getXMLTargetFactory();
		DomWalker res = null;
		Schema schema = fac.getSchema();
		if( schema != null ){
			
		// parse full document as some schema parse needs surrounding context.
		XMLTarget root=target.getRootTarget();
		Node n = root.getTargetNode();
		Source source = new DOMSource(n);
		ValidatorHandler handler = schema.newValidatorHandler();
		DomWalker walker = new DomWalker();
		walker.setPath(root.getTargetPath());
		// walker sees content first as this gives more accurate location of error
		handler.setErrorHandler(walker);
		walker.setContentHandler(handler);
		Result result = new SAXResult(walker);
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.transform(source, result);
			res=walker;
		} catch (Exception e) {
			getContext().getService(LoggerService.class).getLogger(getClass()).error("Error in validate",e);
		}
		}
		DomVisitor val = fac.getValidatingVisitor();
		Node n = target.getTargetNode();
		if( val != null && n.getNodeType()==Node.ELEMENT_NODE){
			DomWalker walker = new DomWalker();
			LinkedList<String> path = target.getTargetPath();
			walker.visitElement((Element)n,path,val);
			if( res == null){
				res=walker;
			}else{
				res.addErrors(walker.getErrors());
			}
		}
		return res;
	}
	
}