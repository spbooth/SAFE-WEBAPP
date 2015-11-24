// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far;

import uk.ac.ed.epcc.webapp.model.far.PageManager.Page;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public interface PartVisitor<X> {
	public X visitPage(Page p);
	public X visitSection(Section s);
	public X visitQuestion(Question q);

}
