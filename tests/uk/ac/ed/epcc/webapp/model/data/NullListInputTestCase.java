//| Copyright - The University of Edinburgh 2015                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data;

import java.util.HashSet;
import java.util.Set;




import org.junit.Test;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.InputInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.InputInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.forms.TestDataProvider;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.NullListInput;


public abstract class NullListInputTestCase<BDO extends DataObject> extends WebappTestBase implements TestDataProvider<Integer,ListInput<Integer,Object>> ,
InputInterfaceTest<Integer, ListInput<Integer,Object>, NullListInputTestCase<BDO>>,
ListInputInterfaceTest<Integer, Object,ListInput<Integer,Object>, NullListInputTestCase<BDO>>
{
	
	
	public InputInterfaceTest<Integer, ListInput<Integer,Object>, NullListInputTestCase<BDO>> input_test = new InputInterfaceTestImpl<Integer, ListInput<Integer,Object>, NullListInputTestCase<BDO>>(this);
	
	
	public ListInputInterfaceTest<Integer, Object,ListInput<Integer,Object>, NullListInputTestCase<BDO>> list_test = new ListInputInterfaceTestImpl<Integer, Object,ListInput<Integer,Object>, NullListInputTestCase<BDO>>(this);
	
	
	DataObjectFactory<BDO> fac=null;
	   public abstract DataObjectFactory<BDO> makeFactory(AppContext conn);
	 
		
	   public DataObjectFactory<BDO> getFactory(){
		   if( fac == null){
			   fac = makeFactory(ctx);
		   }
		   return fac;
	   }
	public Set<Integer> getBadData() throws Exception{
		Set<Integer> bad = new HashSet<Integer>();
		bad.add(-12);
		bad.add(-14);
		bad.remove(NullListInput.NULL_VALUE);
		return bad;
	}
	
	
	public Set<Integer> getGoodData()  throws Exception{
		Set<Integer> good = new HashSet<Integer>();
		   good.add(NullListInput.NULL_VALUE);
	       DataObjectFactory<BDO> fac = getFactory();
			for(BDO item : fac.new FilterSet(fac.getSelectFilter())){
				good.add(item.getID());
			}
		return good;
	}

	


	@SuppressWarnings("unchecked")
	public final ListInput<Integer,Object> getInput() {
		return new NullListInput<BDO>((ListInput<Integer, BDO>) getFactory().getInput());
	}

	


	@Override
	@Test
	public final void testGetKey() throws Exception {
		input_test.testGetKey();
		
	}


	@Override
	@Test
	public final void testMakeHtml() throws Exception {
		input_test.testMakeHtml();
	}


	@Override
	@Test
	public final void testMakeSwing() throws Exception {
		input_test.testMakeSwing();
		
	}


	@Override
	@Test
	public final void testGood() throws TypeError, Exception {
		input_test.testGood();
		
	}


	@Override
	@Test
	public final void testBad() throws Exception {
		input_test.testBad();
	}


	@Override
	@Test
	public final void testGetString() throws Exception {
		input_test.testGetString();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetItembyValue()
	 */
	@Override
	@Test
	public final void testGetItembyValue() throws Exception {
		list_test.testGetItembyValue();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetItems()
	 */
	@Override
	@Test
	public final void testGetItems() throws Exception {
		list_test.testGetItems();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetTagByItem()
	 */
	@Override
	@Test
	public final void testGetTagByItem() throws Exception {
		list_test.testGetTagByItem();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetTagByValue()
	 */
	@Override
	@Test
	public final void testGetTagByValue() throws Exception {
		list_test.testGetTagByValue();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetText()
	 */
	@Override
	@Test
	public final void testGetText() throws Exception {
		list_test.testGetText();
		
	}


	@Override
	@Test
	public final void testIsValid() throws Exception {
		list_test.testIsValid();
		
	}
}