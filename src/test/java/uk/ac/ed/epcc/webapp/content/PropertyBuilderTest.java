//| Copyright - The University of Edinburgh 2013                            |
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
package uk.ac.ed.epcc.webapp.content;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;


/**
 * @author spb
 *
 */

public class PropertyBuilderTest {

	@Test
	public void testSimple(){
		Properties p = new Properties();
		PropertyBuilder j = new PropertyBuilder(p);
		j.open("hello");
		j.clean("world");
		j.close();
		assertEquals("world", p.getProperty("hello"));
	}
	@Test
	public void testMultiple(){
		Properties p = new Properties();
		PropertyBuilder j = new PropertyBuilder(p);
		j.open("hello");
		j.clean("world");
		j.close();
		j.open("hi");
		j.clean("flowers");
		j.close();
		j.open("number");
		j.clean(12);
		j.close();
		assertEquals("world", p.getProperty("hello"));
		assertEquals("flowers", p.getProperty("hi"));
		assertEquals("12", p.getProperty("number"));
		
	}
	@Test
	public void testEscape(){
		Properties p = new Properties();
		PropertyBuilder j = new PropertyBuilder(p);
		j.open("text");
		j.clean(" This has a \" quote \" ");
		j.clean("\n");
		j.clean("And another line");
		j.close();
		assertEquals(" This has a \" quote \" \nAnd another line", p.getProperty("text"));
		
		
	}
	@Test
	public void testAttribute(){
		Properties p = new Properties();
		PropertyBuilder j = new PropertyBuilder(p);
		j.open("data");
		j.attr("att", "stuff");
		j.clean("text");
		j.close();
		assertEquals("stuff", p.getProperty("data.@att"));
		
	}
	@Test
	public void testNested(){
		Properties p = new Properties();
		PropertyBuilder j = new PropertyBuilder(p);
		j.open("hello");
		  j.clean("world");
		j.close();
		j.open("flowers");
		  j.open("daisy");
		    j.open("colour");
		      j.clean("white");
		    j.close();
		    j.open("count");
		      j.clean(1000);
		    j.close();
		  j.close();
		  j.open("rose");
		    j.open("colour");
		      j.clean("red");
		    j.close();
		    j.open("count");
		      j.clean(1);
		    j.close();
		  j.close();
		j.close();
		j.open("number");
		j.clean(12);
		j.close();
		assertEquals("world", p.getProperty("hello"));
		assertEquals("12", p.getProperty("number"));
		assertEquals("white", p.getProperty("flowers.daisy.colour"));
		assertEquals("1000", p.getProperty("flowers.daisy.count"));
		assertEquals("red", p.getProperty("flowers.rose.colour"));
		assertEquals("1", p.getProperty("flowers.rose.count"));
		
	}
	
	@Test
	public void testNestedBuilder(){
		Properties p = new Properties();
		PropertyBuilder j = new PropertyBuilder(p);
		j.open("hello");
		  j.clean("world");
		j.close();
		SimpleXMLBuilder k = j.getNested();
		k.open("flowers");
		  
		  k.open("daisy");
		    k.open("colour");
		      k.clean("white");
		    k.close();
		    k.open("count");
		      k.clean(1000);
		    k.close();
		  k.close();
		  
		  k.open("rose");
		    k.open("colour");
		      k.clean("red");
		    k.close();
		    k.open("count");
		      k.clean(1);
		    k.close();
		  k.close();
		
		k.close();
		  k.appendParent();
		j.open("number");
		j.clean(12);
		j.close();
		assertEquals("world", p.getProperty("hello"));
		assertEquals("12", p.getProperty("number"));
		assertEquals("white", p.getProperty("flowers.daisy.colour"));
		assertEquals("1000", p.getProperty("flowers.daisy.count"));
		assertEquals("red", p.getProperty("flowers.rose.colour"));
		assertEquals("1", p.getProperty("flowers.rose.count"));
		
	}
	@Test
	public void testNestedBuilder2(){
		Properties p = new Properties();
		PropertyBuilder j = new PropertyBuilder(p);
		j.open("hello");
		  j.clean("world");
		j.close();
		
		j.open("flowers");
		  SimpleXMLBuilder k = j.getNested();
		  k.open("daisy");
		    k.open("colour");
		      k.clean("white");
		    k.close();
		    k.open("count");
		      k.clean(1000);
		    k.close();
		  k.close();
		  
		  k.open("rose");
		    k.open("colour");
		      k.clean("red");
		    k.close();
		    k.open("count");
		      k.clean(1);
		    k.close();
		  k.close();
		
		  k.appendParent();
		j.close();
		j.open("number");
		j.clean(12);
		j.close();
		assertEquals("world", p.getProperty("hello"));
		assertEquals("12", p.getProperty("number"));
		assertEquals("white", p.getProperty("flowers.daisy.colour"));
		assertEquals("1000", p.getProperty("flowers.daisy.count"));
		assertEquals("red", p.getProperty("flowers.rose.colour"));
		assertEquals("1", p.getProperty("flowers.rose.count"));
	}
}