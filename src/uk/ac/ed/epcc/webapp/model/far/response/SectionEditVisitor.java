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

import java.util.Map;


import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.Control;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.content.Label;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.content.XMLPrinter;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link ContentVisitor} that converts a specific target {@link Section} into
 * an edit form.
 * The rest of the page is shown as in the super class
 * @author spb
 *
 */

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
	public <C extends ContentBuilder> C visitSection(C builder,Map<String,String> errors, Section s)
			throws Exception {
		super.visitSection(builder,errors, s);
		XMLPrinter raw = s.getSectionRawHtml();
		if( raw != null && builder instanceof HtmlBuilder){
			((HtmlBuilder)builder).append(raw);
		}
		if( target.equals(s)){
			builder.addActionButtons(f);
		}
		return builder;
	}
	

}