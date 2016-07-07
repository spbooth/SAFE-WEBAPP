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

import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.content.XMLPrinter;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.far.PageManager.Page;
import uk.ac.ed.epcc.webapp.model.far.PartManager.Part;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;

/**
 * @author spb
 *
 */
public class XMLVisitor<X extends XMLPrinter> implements PartVisitor<X> {
	public XMLVisitor(X pr) {
		super();
		this.printer = pr;
	}

	private final X printer;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitPage(uk.ac.ed.epcc.webapp.model.far.PageManager.Page)
	 */
	@Override
	public X visitPage(Page p) {
		return null;
	}
	private X openPart(X parent, Part p){
		X part = (X) parent.getNested();
		part.open(p.getTypeName());
		part.attr("Name", p.getName());
		part.attr("SortOrder",p.getSortOrder().toString());
		return part;
	}
	private X closePart(X part){
		part.close();
		part.appendParent();
		return part;
	}
	public X visitPage(X parent ,Page p) throws DataFault {
		X page = openPart(parent,p);
		SectionManager manager = (SectionManager)((PageManager) p.getFactory()).getChildManager();
		for( Section s : manager.getParts(p)){
			visitSection(page, s);
		}
		return closePart(page);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitSection(uk.ac.ed.epcc.webapp.model.far.SectionManager.Section)
	 */
	@Override
	public X visitSection(Section s) {
		// TODO Auto-generated method stub
		return null;
	}
	public X visitSection(X parent, Section s) throws DataFault{
		X section = openPart(parent, s);
		section.attr("ReadOnly", Boolean.toString(s.isReadOnly()));
		section.open("Text");
		section.clean(s.getSectionText());
		section.close();
		section.open("RawText");
		  //TODO appent encoded
		section.close();
		QuestionManager manager = (QuestionManager)((SectionManager) s.getFactory()).getChildManager();
		
		for( Question q : manager.getParts(s)){
			visitQuestion(section, q);
		}
		return closePart(section);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitQuestion(uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question)
	 */
	@Override
	public X visitQuestion(Question q) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public X visitQuestion(X parent, Question q){
		X question = openPart(parent, q);
		return closePart(question);
	}
}
