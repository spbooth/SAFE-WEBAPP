package uk.ac.ed.epcc.webapp.forms;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;



import uk.ac.ed.epcc.webapp.forms.inputs.BinaryInput;


public class BinaryInputInterfaceTestImpl<T,I extends BinaryInput<T>,X extends TestDataProvider<T, I>> implements BinaryInputInterfaceTest<T,I,X>  {

	private X target;
	public BinaryInputInterfaceTestImpl(X target){
		this.target=target;
	}

	public void testBinary() throws Exception{
		I i = target.getInput();
		assertNotNull(i.getChecked());
		
		i.setChecked(true);
		assertTrue(i.isChecked());
		i.setChecked(false);
		assertFalse(i.isChecked());
	}

}
