package uk.ac.ed.epcc.webapp.forms;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.inputs.BinaryInput;



public interface BinaryInputInterfaceTest<T,I extends BinaryInput<T>,X extends TestDataProvider<T, I>>  {

	@Test
	public void testBinary() throws Exception;

}
