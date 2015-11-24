// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.model.data.Composite;

/** Interface tot allow {@link Composite}s to add notes to the update form.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public interface UpdateNoteProvider {
	/** add Notes to be included in a signup/update form.
	 * This is included within the block element above the
	 * form.
	 * 
	 * @param cb
	 * @return
	 */
	public <CB extends ContentBuilder> CB addUpdateNotes(CB cb);
}
