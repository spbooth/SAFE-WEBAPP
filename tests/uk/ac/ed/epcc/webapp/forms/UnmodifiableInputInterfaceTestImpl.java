// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.forms;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;


import uk.ac.ed.epcc.webapp.ContextHolder;
import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import static org.junit.Assert.*;
/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
@Ignore
public class UnmodifiableInputInterfaceTestImpl<T,I extends Input<T>,X extends TestDataProvider<T,I> & ContextHolder> implements UnmodifiableInputInterfaceTest<T,I,X>{

	private X target;
	/**
	 * 
	 */
	public UnmodifiableInputInterfaceTestImpl(X target) {
		this.target=target;
	}

	@Test
	public void testWebParse() throws Exception{
		I input = target.getInput();
		
		HTMLForm f = new HTMLForm(target.getContext());
		f.addInput("input", "input", input);
		Map<String,Object> params = new HashMap<String,Object>();
		Map<String,String> errors = new HashMap<String, String>();
		params.put("input", "BadValue");
		for(T data : target.getGoodData()){
			input.setValue(data);
			
			f.parsePost(errors, params, true);
			assertEquals(0, errors.size());
			assertEquals(data, f.get("input"));
		}
		
		
	}
}
