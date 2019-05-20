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
package uk.ac.ed.epcc.webapp.apps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.apps.Options;
import uk.ac.ed.epcc.webapp.apps.Option;

public class TestOptions {

	private Options opt;
	private Option flag;
	private Option flag_opt;
	
	@Before
	public void setUp() throws Exception {
		opt = new Options();
		flag = new Option(opt, 'f', "flag", "A single flag");
		flag_opt = new Option(opt,'p',"param",true,"A parameter flag");
	}

	@Test
	public void testShort(){
		Options.Instance inst = opt.newInstance();
		List<String> args = inst.parse(new String[] {"-f", "-p", "fred", "boris"});
		assertEquals(1, args.size());
		assertEquals(args.get(0), "boris");
		
		assertTrue(inst.containsOption(flag));
		assertTrue(inst.containsOption(flag_opt));
		assertEquals(inst.getOption(flag_opt).getValue(),"fred");
	}
	
	@Test
	public void testLong(){
		Options.Instance inst = opt.newInstance();
		List<String> args = inst.parse(new String[] {"--flag", "--param", "fred", "boris"});
		assertEquals(1, args.size());
		assertEquals(args.get(0), "boris");
		
		assertTrue(inst.containsOption(flag));
		assertTrue(inst.containsOption(flag_opt));
		assertEquals(inst.getOption(flag_opt).getValue(),"fred");
	}
	
	@Test
	public void testShortMerge(){
		Options.Instance inst = opt.newInstance();
		List<String> args = inst.parse(new String[] {"-f", "-pfred", "boris"});
		assertEquals(1, args.size());
		assertEquals(args.get(0), "boris");
		
		assertTrue(inst.containsOption(flag));
		assertTrue(inst.containsOption(flag_opt));
		assertEquals(inst.getOption(flag_opt).getValue(),"fred");
	}

}