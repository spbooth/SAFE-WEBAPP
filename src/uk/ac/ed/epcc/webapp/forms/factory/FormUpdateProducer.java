// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.registry.FormPolicy;
import uk.ac.ed.epcc.webapp.session.SessionService;

public interface FormUpdateProducer<T> {
	/** produce a FormUpdate for the target type appropriate for
	 * the requesting user
	 * 
	 * @param c AppContext
	 * @return FormUpdate or null
	 */
  public FormUpdate<T> getFormUpdate(AppContext c);
  
  /** Can the producer provide a FormUpdate for the current user.
   * This is intended to reflect a functional inability to generate the
   * FormUpdate as access control is handled by the {@link FormPolicy}
   * 
   * @param c
   * @return true if current user can update
   */
  public boolean canUpdate(SessionService c);
}