// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.MultiInputTestBase;
import uk.ac.ed.epcc.webapp.forms.OptionalInputInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.OptionalInputInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.forms.ParseMapInputInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.ParseMapInputInterfaceTestImpl;


/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.4 $")
public class AlternateInputTestCase extends MultiInputTestBase<Integer,Input<Integer>,AlternateInput<Integer>> implements
ParseMapInputInterfaceTest<Integer, AlternateInput<Integer>, AlternateInputTestCase>,
OptionalInputInterfaceTest<Integer, AlternateInput<Integer>, AlternateInputTestCase>

{

	
	public ParseMapInputInterfaceTest<Integer, AlternateInput<Integer>, AlternateInputTestCase> parse_map_test = new ParseMapInputInterfaceTestImpl<Integer, AlternateInput<Integer>, AlternateInputTestCase>(this);
	
	public OptionalInputInterfaceTest<Integer, AlternateInput<Integer>, AlternateInputTestCase> optional_test = new OptionalInputInterfaceTestImpl<Integer, AlternateInput<Integer>, AlternateInputTestCase>(this);
	/**
	 * 
	 */
	public AlternateInputTestCase() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getGoodData()
	 */
	public Set<Integer> getGoodData() throws Exception {
		HashSet<Integer> result = new HashSet<Integer>();
		for(int i = 1; i< 10; i++){
			result.add(i);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getBadData()
	 */
	public Set<Integer> getBadData() throws Exception {
		HashSet<Integer> result = new HashSet<Integer>();
		result.add(-1);
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getInput()
	 */
	public AlternateInput<Integer> getInput() throws Exception {
		AlternateInput<Integer> result = new AlternateInput<Integer>();
		
		
		result.addInput("PullDown", new IntegerSetInput(new int[]{3,5,7}));
		IntegerInput i = new IntegerInput();
		i.setMin(0);
		result.addInput("Free", i);
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.OptionalInputInterfaceTest#testIsOptional()
	 */
	@Override
	public void testIsOptional() throws Exception {
		optional_test.testIsOptional();	
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseMapInputInterfaceTest#testGoodDataParses()
	 */
	@Override
	public void testGoodDataParses() throws Exception {
		parse_map_test.testGoodDataParses();
		
	}



}
