package uk.ac.ed.epcc.webapp.forms;

import java.util.Iterator;

import org.junit.Test;

import static org.junit.Assert.*;
import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.MultiInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;


public abstract class MultiInputTestBase<T,V extends Input,I extends MultiInput<T, V>>  extends WebappTestBase implements TestDataProvider<T, I> ,
InputInterfaceTest<T, I, MultiInputTestBase<T, V, I>>{

	
	public InputInterfaceTest<T, I, MultiInputTestBase<T, V, I>> input_test = new InputInterfaceTestImpl<T, I, MultiInputTestBase<T,V,I>>(this);
	@Test
	public void dummy(){
		
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testGetInputs() throws Exception{
		MultiInput i = getInput();
		for(Iterator<Input> it = i.getInputs(); it.hasNext();){
			Input si = it.next();
			assertNotNull(si);
		}
	}
	@Test
	public void testGetSubInput() throws Exception{
		MultiInput<?,?> i = getInput();
		for(String s : i.getSubKeys() ){
			Input it = i.getInput(s);
			assertNotNull(it);
		}
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testGetKey()
	 */
	@Override
	@Test
	public final void testGetKey() throws Exception {
		input_test.testGetKey();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testMakeHtml()
	 */
	@Override
	@Test
	public final void testMakeHtml() throws Exception {
		input_test.testMakeHtml();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testMakeSwing()
	 */
	@Override
	@Test
	public final void testMakeSwing() throws Exception {
		input_test.testMakeSwing();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testGood()
	 */
	@Override
	@Test
	public final void testGood() throws TypeError, Exception {
		input_test.testGood();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testBad()
	 */
	@Override
	@Test
	public final void testBad() throws Exception {
		input_test.testBad();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testGetString()
	 */
	@Override
	@Test
	public final void testGetString() throws Exception {
		input_test.testGetString();
		
	}
	
}
