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

import org.junit.Test;


/**
 * @author spb
 *
 */

public class JsonBuilderTest {

	@Test
	public void testSimple(){
		JsonBuilder j = new JsonBuilder();
		j.open("hello");
		j.clean("world");
		j.close();
		assertEquals("{\n\"hello\": \"world\"\n}", j.toString());
	}
	@Test
	public void testMultiple(){
		JsonBuilder j = new JsonBuilder();
		j.open("hello");
		j.clean("world");
		j.close();
		j.open("hi");
		j.clean("flowers");
		j.close();
		j.open("number");
		j.clean(12);
		j.close();
		assertEquals("{\n\"hello\": \"world\",\n\"hi\": \"flowers\",\n\"number\": 12\n}", j.toString());
	}
	@Test
	public void testEscape(){
		JsonBuilder j = new JsonBuilder();
		j.open("text");
		j.clean(" This has a \" quote \" ");
		j.clean("\n");
		j.clean("And another\tline");
		j.close();
		assertEquals("{\n\"text\": \" This has a \\\" quote \\\" \\nAnd another\\u0009line\"\n}", j.toString());
		
	}
	@Test
	public void testAttribute(){
		JsonBuilder j = new JsonBuilder();
		j.open("data");
		j.attr("att", "stuff");
		j.clean("text");
		j.close();
		assertEquals("{\n\"data\": {\n \"@att\": \"stuff\",\n \"#content\": \"text\"\n}\n}", j.toString());
	}
	@Test
	public void testNested(){
		JsonBuilder j = new JsonBuilder();
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
		System.out.println(j.toString());
		String expected = "{\n\"hello\": \"world\",\n\"flowers\": {\n \"daisy\": {\n  \"colour\": \"white\",\n  \"count\": 1000\n },\n \"rose\": {\n  \"colour\": \"red\",\n  \"count\": 1\n }\n},\n\"number\": 12\n}";
		System.out.println(expected);
		assertEquals(expected, j.toString());
	}
	
	@Test
	public void testNestedBuilder(){
		JsonBuilder j = new JsonBuilder();
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
		System.out.println(j.toString());
		String expected = "{\n\"hello\": \"world\",\n\"flowers\": {\n \"daisy\": {\n  \"colour\": \"white\",\n  \"count\": 1000\n },\n \"rose\": {\n  \"colour\": \"red\",\n  \"count\": 1\n }\n},\n\"number\": 12\n}";
		System.out.println(expected);
		assertEquals(expected, j.toString());
	}
	@Test
	public void testNestedBuilder2(){
		JsonBuilder j = new JsonBuilder();
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
		System.out.println(j.toString());
		String expected = "{\n\"hello\": \"world\",\n\"flowers\": {\n \"daisy\": {\n  \"colour\": \"white\",\n  \"count\": 1000\n },\n \"rose\": {\n  \"colour\": \"red\",\n  \"count\": 1\n }\n},\n\"number\": 12\n}";
		System.out.println(expected);
		assertEquals(expected, j.toString());
	}
}