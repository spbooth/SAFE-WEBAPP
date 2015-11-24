package uk.ac.ed.epcc.webapp.editors.xml;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.ValidatorHandler;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import uk.ac.ed.epcc.webapp.logging.Logger;

/** This Class is responsible to building location paths to identify XML node.
 * It operates in two distinct modes. Both modes are implemented in the same class to
 * keep the dependency on the path syntax in one place.
 * 
 * In DOM mode it walks a DOM tree building a location path (not an Xpath) for the Node objects it visits.
 * and applies a {@link DomVisitor} to these nodes (this can also control if a node is recursed into).
 * 
 * In SAX mode is operates as a {@link ContentHandler} and {@link ErrorHandler} it visits the entire tree building the 
 * path down to the element level and caches errors indexed by path.
 *
 * The form of the location path is the responsibility of this class. 
 * 
 * The actions performed are
 * the responsibility of the {@link DomVisitor}.This allows different visitors to agree on the path locations.
 * 
 * This allows the {@link DomVisitor} to build a GUI/HTML representation of the document. The controls on
 * the document can then use a different visitor to follow the path to the desired node to implement
 * a change.
 *
 * 
 * @author spb
 *
 */
public class DomWalker implements ContentHandler, ErrorHandler, InfoProvider{
	/** Counters for the various types of child element.
	 * 
	 * @author spb
	 *
	 */
	private static class Counter{
		int element_count=0;
		int text_count=0;
		int comment_count=0;
	}
	private LinkedList<Counter> counter_stack;
	private ContentHandler inner;

	private static final String ATTR_PREFIX = "A";
	private static final String ELEMENT_PREFIX = "E";
	private static final String TEXT_PREFIX = "T";
	private static final String COMMENT_PREFIX="C";
	private Logger log=null;
	public void setLogger(Logger log){
		this.log=log;
	}
	public void setContentHandler(ContentHandler h){
		inner =h;
		if( inner instanceof ValidatorHandler){
			((ValidatorHandler)inner).setContentHandler(new DefaultHandler(){

				@Override
				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {
					
					super.startElement(uri, localName, qName, attributes);
					// Needs to be called within VH !!!!
					
					
						TypeInfoProvider type_provider = ((ValidatorHandler)inner).getTypeInfoProvider();
						if( types == null ){
							types = new HashMap<LinkedList<String>, TypeInfo>();
						}
						TypeInfo info = type_provider.getElementTypeInfo();
						types.put(new LinkedList<String>(path), info);
				}
				
			});
		}
	}
	/** Visit an {@link Element} in DOM mode.
	 * 
	 * @param e
	 * @param path
	 * @param vis
	 */
	public void visitElement(Element e, LinkedList<String> path,DomVisitor vis){
		setPath(path);
		visitElement(e, vis);
	}
	public void visitElement(Element e, DomVisitor vis){
		try {
			startElement(e.getNamespaceURI(), e.getLocalName(), e.getNodeName(), null);
		} catch (SAXException e1) {
			doError(e1);
		}
		try{
		if( vis.beginStartElement(e,path) ){
			if( vis.useAttributes(e,path)){
				NamedNodeMap map = e.getAttributes();
				for(int i=0;i<map.getLength();i++){
					Attr a = (Attr) map.item(i);
					push(path,ATTR_PREFIX+a.getName());
					vis.visitAttr(a,path);
					pop(path);
				}
			}
			vis.endStartElement(e,path);
			NodeList list = e.getChildNodes();
			Counter current = counter_stack.getLast();
			for(int i=0 ; i< list.getLength(); i++){
				Node n = list.item(i);
				if( n.getNodeType() == Node.TEXT_NODE){
					push(path,TEXT_PREFIX+current.text_count++);
					vis.textNode((Text) n,path);
					pop(path);
				}else if( n.getNodeType() == Node.ELEMENT_NODE){
					visitElement((Element)n, vis);
				}else if( n.getNodeType() == Node.COMMENT_NODE){
					push(path,COMMENT_PREFIX+current.comment_count++);
					vis.commentNode((Comment)n, path);
					pop(path);
				}else{
					if( log != null ){
						log.debug("Unexpected node "+n.toString());
					}
				}
			}
			vis.endElement(e,path);
		}
		}catch(Exception e2){
			doError(e2);
		}
		try {
			endElement(e.getNamespaceURI(), e.getLocalName(), e.getNodeName());
		} catch (SAXException e1) {
			doError(e1);
		}
		
	}
	public static boolean isText(String element){
		return element.startsWith(TEXT_PREFIX);
	}
	public static  boolean isElment(String element){
		return element.startsWith(ELEMENT_PREFIX);
	}
	public static boolean isAttribute(String element){
		return element.startsWith(ATTR_PREFIX);
	}
	
	private void push(LinkedList<String> path,String tag){
		path.addLast(tag);
	}
	private void pop(LinkedList<String> path){
		path.removeLast();
	}
	
	
	public LinkedList<String> getPath() {
		return path;
	}
	public void setPath(LinkedList<String> sax_path) {
		this.path = sax_path;
		this.counter_stack=new LinkedList<DomWalker.Counter>();
		errors=null;
	}


	private LinkedList<String> path;
	
	public void endElement(String uri, String localName, String qName) throws SAXException{
		counter_stack.removeLast();
		if( ! counter_stack.isEmpty()){
			pop(path);
		}
		if( inner != null){
			inner.endElement(uri, localName, qName);
		}
	}
	
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		if( ! counter_stack.isEmpty()){
			Counter parent_counter = counter_stack.getLast();
			if( parent_counter != null ){
				push(path,ELEMENT_PREFIX+parent_counter.element_count++);
			}
		}
		counter_stack.addLast(new Counter());
		if( inner != null ){
			inner.startElement(uri, localName, qName, atts);


		}
	}
	private Map<LinkedList<String>,Set<String>> errors = null;
	private Map<LinkedList<String>,TypeInfo> types=null;
	
	private void doError(Throwable t){
		if( errors == null ){
			errors = new HashMap<LinkedList<String>, Set<String>>();
		}
		Set<String> msg = errors.get(path);
		if( msg == null ){
			msg = new LinkedHashSet<String>();
			errors.put(new LinkedList<String>(path),msg);
		}
		msg.add(t.toString());
	}
	public void addErrors(Map<LinkedList<String>,Set<String>> errors){
		if( errors == null || errors.size() == 0){
			return;
		}
		if( this.errors == null){
			this.errors=new LinkedHashMap<LinkedList<String>, Set<String>>(errors);
		}else{
			this.errors.putAll(errors);
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.xml.InfoProvider#getErrors()
	 */
	
	public Map<LinkedList<String>, Set<String>> getErrors() {
		return errors;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.xml.InfoProvider#getTypes()
	 */
	
	public Map<LinkedList<String> ,TypeInfo> getTypes(){
		return types;
	}
	
	public void error(SAXParseException e) throws SAXException {
		doError(e);
	}
	
	public void warning(SAXParseException e) throws SAXException {
		doError(e);
	}
	
	public void fatalError(SAXParseException exception) throws SAXException {
		throw exception;
		
	}
	
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if( inner != null ){
			inner.characters(ch, start, length);
		}
	}
	
	public void endDocument() throws SAXException {
		if( inner != null){
			inner.endDocument();
		}
		
	}
	
	public void endPrefixMapping(String prefix) throws SAXException {
		if( inner != null){
			inner.endPrefixMapping(prefix);
		}
		
	}
	
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		if( inner != null ){
			inner.ignorableWhitespace(ch, start, length);
		}
		
	}
	
	public void processingInstruction(String target, String data)
			throws SAXException {
		if( inner != null ){
			inner.processingInstruction(target, data);
		}
		
	}
	
	public void setDocumentLocator(Locator locator) {
		if( inner != null ){
			inner.setDocumentLocator(locator);
		}
	}
	
	public void skippedEntity(String name) throws SAXException {
		if( inner != null ){
			inner.skippedEntity(name);
		}
	}
	
	public void startDocument() throws SAXException {
		if( inner != null){
			inner.startDocument();
		}
	}
	
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		if( inner != null ){
			inner.startPrefixMapping(prefix, uri);
		}
	}
	
	public Set<String> getError(LinkedList<String> path) {
		if( errors != null ){
			return errors.get(path);
		}
		return null;
	}
	
	public TypeInfo getTypeInfo(LinkedList<String> path) {
		if( types != null){
			return types.get(path);
		}
		return null;
	}
	

}
