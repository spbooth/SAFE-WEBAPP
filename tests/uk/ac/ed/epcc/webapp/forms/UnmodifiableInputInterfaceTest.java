// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.forms;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.ContextHolder;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.3 $")
public interface UnmodifiableInputInterfaceTest<T,I extends Input<T>,X extends TestDataProvider<T,I> & ContextHolder> {

	@Test
	public void testWebParse() throws Exception;
		
		
}
