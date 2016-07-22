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

import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.XMLPrinter;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.far.HandlerPartManager.HandlerPart;
import uk.ac.ed.epcc.webapp.model.far.PageManager.Page;
import uk.ac.ed.epcc.webapp.model.far.PartManager.Part;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;
import uk.ac.ed.epcc.webapp.model.far.handler.PartConfigFactory;

/** A {@link PartVisitor} that generates a XML version of the form.
 * @author spb
 * @param <X> type of {@link XMLPrinter}
 * @see XMLFormParser
 *
 */
public class XMLVisitor<X extends XMLPrinter> implements PartVisitor<X> {
	/**
	 * 
	 */
	public static final String CONFIG_VALUE_ATTR = "value";
	/**
	 * 
	 */
	public static final String CONFIG_NAME_ATTR = "name";
	/**
	 * 
	 */
	public static final String CONFIG_ELEMENT = "Config";

	public XMLVisitor(AppContext conn,X pr) {
		super();
		this.printer = pr;
		this.conn=conn;
		this.log = conn.getService(LoggerService.class).getLogger(getClass());
	}

	private final AppContext conn;
	private final Logger log;
	private X printer;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitPage(uk.ac.ed.epcc.webapp.model.far.PageManager.Page)
	 */
	@Override
	public X visitPage(Page p) {
		openPart(p);
		return closePart(p);
	}
	private <P extends Part> X openPart(P p){
		X part = (X) printer.getNested();
		part.open(p.getTypeName());
		part.attr(PartManager.NAME_FIELD, p.getName());
		part.attr(PartManager.ORDER_FIELD,p.getSortOrder().toString());
		printer=part;
		return part;
	}
	public <O extends PartOwner> void visitOwner(PartOwnerFactory<O> my_manager, O owner) throws DataFault{
		PartManager<O,?> manager = my_manager.getChildManager();
		if( manager != null ){
			for(Part child : manager.getParts(owner)){
				child.visit(this);
			}
		}
	}
	private <P extends Part> X closePart(P part) {
		try{
			if( part instanceof HandlerPart){
				HandlerPart h = (HandlerPart) part;
				printer.open(HandlerPartManager.HANDLER_TYPE_FIELD);
				printer.clean(h.getHandlerTag());
				printer.close();
			}
			PartManager my_manager = (PartManager) part.getFactory();
			visitOwner(my_manager, part);
			PartConfigFactory config = my_manager.getConfigFactory();
			if( config != null ){
				X conf = (X) printer.getNested();
				Map<String,Object> values = config.getValues(part);
				for(String key : values.keySet()){
					conf.open(CONFIG_ELEMENT);
					conf.attr(CONFIG_NAME_ATTR, key);
					conf.attr(CONFIG_VALUE_ATTR,values.get(key).toString());
					conf.close();
				}
				conf.appendParent();
			}
		}catch(Exception e){
			log.error("Error printing child parts",e);
		}
		printer.close();
		printer = (X) printer.appendParent();
		return printer;
	}
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitSection(uk.ac.ed.epcc.webapp.model.far.SectionManager.Section)
	 */
	@Override
	public X visitSection(Section s) {
		X section = openPart(s);
		section.attr(SectionManager.SECTION_READ_ONLY_FIELD, Boolean.toString(s.isReadOnly()));
			section.open(SectionManager.SECTION_TEXT_FIELD);
				section.clean(s.getSectionText());
			section.close();
			XMLPrinter raw = s.getSectionRawHtml();
			if( raw != null ){
				section.open(SectionManager.SECTION_RAW_HTML_FIELD);	
					section.clean(raw.toString());
				section.close();
			}
		return closePart(s);
	}
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitQuestion(uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question)
	 */
	@Override
	public X visitQuestion(Question q) {
		X question = openPart(q);
		question.attr(QuestionManager.OPTIONAL_FIELD, Boolean.toString(q.isOptional()));
		question.open(QuestionManager.QUESTION_TEXT_FIELD);
		question.clean(q.getQuestionText());
		question.close();
		return closePart(q);
	}
}
