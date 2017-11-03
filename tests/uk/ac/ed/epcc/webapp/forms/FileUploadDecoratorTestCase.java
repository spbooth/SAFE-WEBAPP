//| Copyright - The University of Edinburgh 2017                            |
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
package uk.ac.ed.epcc.webapp.forms;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.inputs.FileUploadDecorator;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.SetInput;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayStreamData;

/**
 * @author spb
 *
 */
public class FileUploadDecoratorTestCase extends MultiInputTestBase<String, Input, FileUploadDecorator> implements
TestParseDataProvider<String, FileUploadDecorator>,
ParseInputInterfaceTest<String, FileUploadDecorator, FileUploadDecoratorTestCase>,
ParseMapInputInterfaceTest<String, FileUploadDecorator, FileUploadDecoratorTestCase>{

	ParseInputInterfaceTestImpl<String, FileUploadDecorator, FileUploadDecoratorTestCase> parse_impl = new ParseInputInterfaceTestImpl<String, FileUploadDecorator, FileUploadDecoratorTestCase>(this);
	ParseMapInputInterfaceTestImpl<String, FileUploadDecorator, FileUploadDecoratorTestCase> map_imp = new ParseMapInputInterfaceTestImpl<String, FileUploadDecorator, FileUploadDecoratorTestCase>(this);
	/**
	 * 
	 */
	public FileUploadDecoratorTestCase() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getGoodData()
	 */
	@Override
	public Set getGoodData() throws Exception {
		Set good=new HashSet<>();
		good.add("john");
		good.add("paul");
		return good;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getBadData()
	 */
	@Override
	public Set getBadData() throws Exception {
		Set<String> bad = new HashSet<>();
		bad.add("Tomsk");
		bad.add("Orinoco");
		return bad;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getInput()
	 */
	@Override
	public FileUploadDecorator getInput() throws Exception {
		SetInput<String> input = new SetInput<>();
		input.setCaseInsensative(true);
		input.addChoice("John");
		input.addChoice("Paul");
		input.addChoice("George");
		input.addChoice("Ringo");
		
		FileUploadDecorator fileUploadDecorator = new FileUploadDecorator(input);
		fileUploadDecorator.setKey("decorator");
		return fileUploadDecorator;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#getGoodParseData()
	 */
	@Override
	public Set<String> getGoodParseData() {
		Set good=new HashSet<>();
		good.add("geORGE");
		good.add("rinGO");
		return good;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#getBadParseData()
	 */
	@Override
	public Set<String> getBadParseData() {
		Set<String> bad = new HashSet<>();
		bad.add("Tomsk");
		bad.add("Orinoco");
		return bad;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#allowNull()
	 */
	@Override
	public boolean allowNull() {
		
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#parseNull()
	 */
	@Override
	public void parseNull() throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testGoodDataParses()
	 */
	@Override
	@Test
	public void testGoodDataParses() throws Exception {
		parse_impl.testGoodDataParses();
		map_imp.testGoodDataParses();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testGoodParse()
	 */
	@Override
	@Test
	public void testGoodParse() throws Exception {
		parse_impl.testGoodParse();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testBadParse()
	 */
	@Override
	@Test
	public void testBadParse() throws Exception {
		parse_impl.testBadParse();
		
	}
	
	
	@Test
	public void testFileLoad() throws Exception {
		Map<String,Object> data = new HashMap<>();
		ByteArrayStreamData sd = new ByteArrayStreamData("ringo".getBytes());
		data.put("decorator.File", sd);
		
		FileUploadDecorator input = getInput();
		
		input.parse(data);
		
		assertEquals("ringo",input.getValue());
	}
	@Test
	public void testFileLoadReplace() throws Exception {
		Map<String,Object> data = new HashMap<>();
		ByteArrayStreamData sd = new ByteArrayStreamData("ringo".getBytes());
		data.put("decorator.File", sd);
		
		FileUploadDecorator input = getInput();
		input.setValue("john");
		assertEquals("john",input.getValue());
		input.parse(data);
		
		assertEquals("ringo",input.getValue());
	}
	@Test
	public void testTextReplace() throws Exception {
		Map<String,Object> data = new HashMap<>();
		
		data.put("decorator.Text", "Ringo");
		
		FileUploadDecorator input = getInput();
		input.setValue("john");
		assertEquals("john",input.getValue());
		input.parse(data);
		
		assertEquals("ringo",input.getValue());
	}
}
