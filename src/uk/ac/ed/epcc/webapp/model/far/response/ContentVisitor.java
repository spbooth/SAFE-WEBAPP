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

import java.util.concurrent.CompletionException;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.far.PageManager;
import uk.ac.ed.epcc.webapp.model.far.PageManager.Page;
import uk.ac.ed.epcc.webapp.model.far.PartManager;
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
	protected final CompleteVisitor complete_viz;
	protected final HasQuestionsVisitor has_question_viz;
	private final X cb;
	private Table<String,Question> t;	
	/**
	 * 
	 */
	public ContentVisitor(X cb, SessionService sess, Response<?> response) {
		this.cb=cb;
		this.sess=sess;
		this.response=response;
		this.complete_viz = new CompleteVisitor(response);
		this.has_question_viz = new HasQuestionsVisitor<>(sess.getContext());
	}

	
	public <C extends ContentBuilder> C visitPage(C builder, Page p) throws Exception{
		//
		// If the part contains questions add the
		// complete/incomplete classes
		//
		boolean page_complete = p.visit(complete_viz);
		boolean has_questions = p.visit(has_question_viz);
		ContentBuilder block = builder.getPanel("dynamic_form_page", has_questions? (page_complete ? "complete" : "incomplete") : null );
		block.addHeading(2, p.getTypeName() + ": " + p.getName());
		SectionManager manager = (SectionManager)((PageManager) p.getFactory()).getChildManager();
		for( Section s : manager.getParts(p)){
			boolean section_complete = s.visit(complete_viz);
			boolean section_has_questions = s.visit(has_question_viz);
			ContentBuilder section_block = block.getPanel("dynamic_form_section",section_has_questions ? (section_complete ? "complete" : "incomplete"): null);
			visitSection(section_block, s);
			section_block.addParent();
		}
		block.addParent();
		
		return builder;
	}
	public <C extends ContentBuilder> C visitSection(C builder, Section s) throws Exception{
		
		builder.addHeading(3, "Section: " + s.getName());
		builder.addText(s.getSectionText());
		QuestionManager manager = (QuestionManager)((SectionManager) s.getFactory()).getChildManager();
		t = new Table<String, Question>();
		for( Question q : manager.getParts(s)){
			visitQuestion(t, q);
		}
		
		
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
			hb.attr("class","warn" );
			hb.clean("No answer");
			hb.close();
			hb.appendParent();
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
		if( q.isOptional()){
			t.addAttribute(QUESTION_COL,q, "class", "question optional");
		}else{
			t.addAttribute(QUESTION_COL,q, "class", "question");
		}
		if( q.visit(complete_viz)){
			t.addAttribute(ANSWER_COL, q, "class", "answer complete");
		}else{
			t.addAttribute(ANSWER_COL, q, "class", "answer incomplete");
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