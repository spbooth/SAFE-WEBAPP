//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.model.far;

import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.HandlerPartManager.HandlerPart;
import uk.ac.ed.epcc.webapp.model.far.PartManager.Part;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;
import uk.ac.ed.epcc.webapp.model.far.handler.PartConfigFactory;
import uk.ac.ed.epcc.webapp.model.far.handler.PartConfigFactory.Config;

/** A {@link ContentHandler} that parses a XML representation of a {@link DynamicForm} or {@link Part}
 * 
 * @see XMLVisitor
 * @author spb
 *
 */
public class XMLFormParser extends AbstractContexed implements ContentHandler {
	
	
	private final StringBuilder text=new StringBuilder();
	private PartOwner owner; // The PartOwner we are adding content to.
    private PartOwnerFactory manager; // The manager for the current level 
    LinkedList<String> current_type = new LinkedList<String>();
	/**
	 * 
	 */
	public XMLFormParser(AppContext conn, PartOwnerFactory fac,PartOwner owner) {
		super(conn);
		this.manager=fac;
		this.owner=owner;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	@Override
	public void setDocumentLocator(Locator locator) {

	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {

	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {
	

	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
	 */
	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
	

	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		

	}

	private void resetText(){
		text.setLength(0);
	}
	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		resetText();
		if( qName.equals(manager.getChildTypeName())){
			// we are going down a level
			current_type.push(qName);
			String name = atts.getValue("", PartManager.NAME_FIELD);
			if( name == null){
				throw new SAXException("No name attribute"); 
			}
			String order_string = atts.getValue("", PartManager.ORDER_FIELD);
			if( order_string == null ){
				throw new SAXException("no order attribute");
			}
			int order = Integer.parseInt(order_string);
			PartManager part_manger = manager.getChildManager();
			try {
				Part part = (Part) part_manger.makeBDO();
				part.setOwner(owner);
				part.setName(name);
				part.setSortOrder(order);
				if( part instanceof SectionManager.Section){
					String section_read_only = atts.getValue("",SectionManager.SECTION_READ_ONLY_FIELD);
					if( section_read_only == null){
						throw new SAXException("No read-only attribute");
					}
					((Section)part).setSectionReadOnly(Boolean.parseBoolean(section_read_only));
				}else if ( part instanceof QuestionManager.Question){
					String question_optional = atts.getValue("",QuestionManager.OPTIONAL_FIELD);
					if( question_optional == null ){
						throw new SAXException("No is-optional attribute");
					}
					((Question)part).setOptional(Boolean.parseBoolean(question_optional));
				}
				part.commit();
				manager=part_manger;
				owner = part;
			} catch (DataFault e) {
				throw new SAXException(e);
			}
		}else if( qName.equals(XMLVisitor.CONFIG_ELEMENT)){
			if( ! (manager instanceof PartManager)  || ! (owner instanceof Part)){
				throw new SAXException("Config element outside Part");
			}
			String name = atts.getValue("", XMLVisitor.CONFIG_NAME_ATTR);
			if( name == null ){
				throw new SAXException("No config name");
			}
			String value = atts.getValue("", XMLVisitor.CONFIG_VALUE_ATTR);
			if( value == null ){
				throw new SAXException("No config name");
			}
			PartManager part_manager = (PartManager) manager;
			PartConfigFactory config = part_manager.getConfigFactory();
			if( config == null ){
				throw new SAXException("Config element on illegal parent");
			}
			try {
				Config cfg = config.makeEntry((Part)owner, name);
				cfg.setValue(value);
				cfg.commit();
			} catch (DataException e) {
				throw new SAXException(e);
			}	
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try{
			if( owner instanceof HandlerPart && qName.equals(HandlerPartManager.HANDLER_TYPE_FIELD)){
				HandlerPart part = (HandlerPart) owner;
				part.setHandlerTag(text.toString());
				part.commit();
			}else if( owner instanceof Section){

				Section section = (Section)owner;
				if( qName.equals(SectionManager.SECTION_TEXT_FIELD)){
					section.setSectionText(text.toString());
				}else if( qName.equals(SectionManager.SECTION_RAW_HTML_FIELD)){
					section.setSectionRawHtml(text.toString());
				}
				section.commit();
			}else if( owner instanceof Question){

				Question question=(Question)owner;
				if( qName.equals(QuestionManager.QUESTION_TEXT_FIELD)){
					question.setQuestionText(text.toString());
				}
				question.commit();
			}
		}catch(Exception e){
			throw new SAXException(e);
		}
		if( qName.equals(current_type.peek())){
			// go back up
			current_type.pop();
			owner = ((Part)owner).getOwner();
			manager = owner.getFactory();
		}
		resetText();
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
			for(int i= start; i<start+length ; i++){
				text.append(ch[i]);
			}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
	 */
	@Override
	public void processingInstruction(String target, String data) throws SAXException {
		

	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	@Override
	public void skippedEntity(String name) throws SAXException {

	}


}
