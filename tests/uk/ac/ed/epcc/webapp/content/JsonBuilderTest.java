// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.content;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestCase;


/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.4 $")
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
		j.clean("And another line");
		j.close();
		assertEquals("{\n\"text\": \" This has a \\\" quote \\\" \\nAnd another line\"\n}", j.toString());
		
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
}

