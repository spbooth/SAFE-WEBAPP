// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.registry.FormPolicy;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Interface for objects that create {@link FormCreator}s
 * 
 * @author spb
 *
 */
public interface FormCreatorProducer {
	/** Create a default FormCreator for the target object
	 * 
	 * The form may be customised according to the requesting AppUser
	 * @param c AppContext
	 * @return FormCreator or null;
	 */
  public FormCreator getFormCreator(AppContext c);
  /** Can the producer provide a FormUpdate for the current user.
   * This is intended to reflect a functional inability to generate the
   * FormUpdate as access control is handled by the {@link FormPolicy}
   * 
   * @param p
   * @return true if current user allowed to create objects
   */
  public boolean canCreate(SessionService p);
}