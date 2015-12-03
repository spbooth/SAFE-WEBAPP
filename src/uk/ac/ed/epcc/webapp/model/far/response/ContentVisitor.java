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
package uk.ac.ed.epcc.webapp.model.far.response;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.far.PageManager;
import uk.ac.ed.epcc.webapp.model.far.PageManager.Page;
import uk.ac.ed.epcc.webapp.model.far.PartVisitor;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.SectionManager;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link PartVisitor} to generate a view of the current state of the response against a part
 * @author spb
 *
 */

public class ContentVisitor<X extends ContentBuilder> implements PartVisitor<X>, Contexed {

	/**
	 * 
	 */
	protected static final String ANSWER_COL = "Answer";
	/**
	 * 
	 */
	protected static final String QUESTION_COL = "Question";
	protected final SessionService sess;
	protected final Response<?> response;
	private final X cb;
	private Table<String,Question> t;	
	/**
	 * 
	 */
	public ContentVisitor(X cb, SessionService sess, Response<?> response) {
		this.cb=cb;
		this.sess=sess;
		this.response=response;
	}

	public <C extends ContentBuilder> C visitPage(C builder, Page p) throws Exception{
		ContentBuilder block = builder.getPanel("dynamic_form_page");
		SectionManager manager = (SectionManager)((PageManager) p.getFactory()).getChildManager();
		for( Section s : manager.getParts(p)){
			ContentBuilder section_block = builder.getPanel("dynamic_form_section");
			visitSection(section_block, s);
			section_block.addParent();
		}
		block.addParent();
		
		return builder;
	}
	public <C extends ContentBuilder> C visitSection(C builder, Section s) throws Exception{
		
		builder.addHeading(3, "Section "+s.getName());
		builder.addText(s.getSectionText());
		QuestionManager manager = (QuestionManager)((SectionManager) s.getFactory()).getChildManager();
		t = new Table<String, Question>();
		for( Question q : manager.getParts(s)){
			visitQuestion(t, q);
		}
		t.addColAttribute(QUESTION_COL, "class", "question");
		t.addColAttribute(ANSWER_COL, "class", "answer");
		t.setPrintHeadings(false);
		builder.addTable(sess.getContext(), t);
		t=null;
		return builder;
	}
	public class NoAnswer implements UIGenerator{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
		 */
		@Override
		public ContentBuilder addContent(ContentBuilder cb) {
			ExtendedXMLBuilder hb = cb.getSpan();
			hb.open("span");
			hb.attr("warning","true" );
			hb.clean("No answer");
			hb.close();
			return cb;
		}
		
	}
	public void visitQuestion(Table<String, Question> t, Question q) throws Exception{
		
		t.put(QUESTION_COL, q, q.getQuestionText());
		ResponseData<Object, ?,?> wrapper = response.getWrapper(q);
		if( wrapper == null ){			
			t.put(ANSWER_COL, q, new NoAnswer());
		}else{
			t.put(ANSWER_COL, q, wrapper);
			// This allows the wrapper to customise content
			// in particular uploaded files can be shown as links.
			
		}
	}

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitPage(uk.ac.ed.epcc.webapp.model.far.PageManager.Page)
	 */
	@Override
	public final X visitPage(Page p) {
		try {
			return visitPage(cb, p);
		} catch (Exception e) {
			getLogger().error("problem formatting page",e);
		}
		return cb;
	}

	/**
	 * @return
	 */
	protected final Logger getLogger() {
		return sess.getContext().getService(LoggerService.class).getLogger(getClass());
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitSection(uk.ac.ed.epcc.webapp.model.far.SectionManager.Section)
	 */
	@Override
	public final X visitSection(Section s) {
		try {
			return visitSection(cb, s);
		} catch (Exception e) {
			getLogger().error("problem formatting section",e);
		}
		return cb;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitQuestion(uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question)
	 */
	@Override
	public final  X visitQuestion(Question q) {
		try {
			visitQuestion(t, q);
			return cb;
		} catch (Exception e) {
			getLogger().error("problem formatting question",e);
		}
		return cb;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return sess.getContext();
	}

}