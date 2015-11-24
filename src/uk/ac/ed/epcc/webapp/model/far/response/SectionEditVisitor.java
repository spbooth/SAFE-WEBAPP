// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far.response;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.Control;
import uk.ac.ed.epcc.webapp.content.Label;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class SectionEditVisitor<X extends ContentBuilder> extends ContentVisitor<X> {

	private final Form f;
	private final Section target;
	/**
	 * @param cb
	 * @param sess
	 * @param response
	 */
	public SectionEditVisitor(X cb, SessionService sess, Response<?> response,Form f, Section target) {
		super(cb, sess, response);
		this.f=f;
		this.target=target;
	}
	@Override
	public void visitQuestion(Table<String, Question> t, Question q)
			throws Exception {
		if( q.getOwner().equals(target)){
			t.put(QUESTION_COL, q, new Label(getContext(), f.getField(q.getName())));
			t.put(ANSWER_COL, q, new Control(getContext(), f.getField(q.getName())));
		}else{
			super.visitQuestion(t, q);
		}
	}
	@Override
	public <C extends ContentBuilder> C visitSection(C builder, Section s)
			throws Exception {
		super.visitSection(builder, s);
		if( target.equals(s)){
			builder.addActionButtons(f);
		}
		return builder;
	}
	

}
