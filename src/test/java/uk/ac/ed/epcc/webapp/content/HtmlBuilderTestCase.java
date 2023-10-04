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
package uk.ac.ed.epcc.webapp.content;

import static org.junit.Assert.assertEquals;
import org.junit.Assert;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder.Heading;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder.Panel;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder.Text;
import uk.ac.ed.epcc.webapp.editors.mail.DirectMessageLinker;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;

public class HtmlBuilderTestCase extends WebappTestBase {

	

	@Test
	public void testAppendString() {
		HtmlPrinter hb = new HtmlBuilder();
		hb.clean("hello");
		hb.clean(" ");
		hb.clean("world");
		assertEquals("hello world",hb.toString());
	}

	@Test
	public void testClean() {
		SimpleXMLBuilder hb = new HtmlBuilder();
		hb.clean("<>&\"\'");
		assertEquals("&lt;&gt;&amp;&#34;&#39;",hb.toString());
	}

	@Test
	public void testOpen() {
		SimpleXMLBuilder hb = new HtmlBuilder();
		hb.open("hi");
		hb.open("ho");
		hb.clean("x");
		hb.close();
		hb.close();
		assertEquals("<hi><ho>x</ho></hi>",hb.toString());
		
	}

	@Test(expected=ConsistencyError.class)
	public void testClose() {
		SimpleXMLBuilder hb = new HtmlBuilder();
		hb.close();
	}

	@Test
	public void testOpenAttr() {
		SimpleXMLBuilder hb = new HtmlBuilder();
		hb.open("hi", new String[][] {{"hello","world"},{"checked"},{"bad","<value>"}});
		hb.clean("x");
		hb.close();
		assertEquals("<hi hello='world' checked bad='&lt;value&gt;'>x</hi>",hb.toString());
	}

	@Test
	public void testOpenAttr2() {
		SimpleXMLBuilder hb = new HtmlBuilder();
		hb.open("hi");
		hb.attr("hello","world");
		hb.attr("checked",null);
		hb.attr("bad","<value>");
		hb.clean("x");
		hb.close();
		assertEquals("<hi hello='world' checked bad='&lt;value&gt;'>x</hi>",hb.toString());
	}
	
	@Test
	public void testOpenAttr3() {
		SimpleXMLBuilder hb = new HtmlBuilder();
		hb.open("hi");
		hb.attr("hello","world");
		hb.attr("checked",null);
		hb.attr("bad","<value>");
		hb.close();
		assertEquals("<hi hello='world' checked bad='&lt;value&gt;'/>",hb.toString());
	}
	
	@Test
	public void testCleanFormatted() {
		String test          ="hello\nworld\n\nThis is a <test>";
		String pre_result="<pre class='loose'>hello\nworld\n\nThis is a &lt;test&gt;</pre>";
		String break_result="<p class='longlines'>\nhello<br/>\nworld<br/>\n<br/>\nThis is a &lt;test&gt;<br/>\n</p>\n";
		HtmlPrinter hb = new HtmlBuilder();
		hb.cleanFormatted(100, test);
		assertEquals(pre_result,hb.toString());
		hb = new HtmlBuilder();
		hb.cleanFormatted(1, test);
		assertEquals(break_result,hb.toString());
	}
	
	/**
	 * Tests the method {@link HtmlBuilder#paragraph(String)}.
	 */
	@Test
	public void testParagraph() {
		HtmlBuilder hb = new HtmlBuilder();
		hb.paragraph("test paragraph");
		assertEquals("<p>test paragraph</p>", hb.toString());
	}

	/**
	 * Tests the method {@link HtmlBuilder#addText(String)}.
	 */
	@Test
	public void testAddText() {
		HtmlBuilder hb = new HtmlBuilder();
		hb.addText("test text");
		assertEquals("<div class='para'>test text</div>", hb.toString());
	}
	
	/**
	 * Tests the method {@link HtmlBuilder#addHeading(int, String)}.
	 */
	@Test
	public void testAddHeading() {
		HtmlBuilder hb = new HtmlBuilder();
		hb.addHeading(2, "test heading");
		assertEquals("<h2>test heading</h2>", hb.toString());
	}
	
	/**
	 * Tests the method {@link AbstractXMLBuilder#clean(Number)}.
	 */
	@Test
	public void testClean2() {
		HtmlBuilder hb = new HtmlBuilder();
		hb.clean(100);
		assertEquals("100", hb.toString());
	}
	
	/**
	 * Tests the method {@link AbstractXMLBuilder#clean(char)}.
	 */
	@Test
	public void testClean3() {
		HtmlBuilder hb = new HtmlBuilder();
		hb.clean('\"');
		assertEquals("&#34;", hb.toString());
	}
	
	@Test
	public void testCleanUnicode() {
		HtmlBuilder hb = new HtmlBuilder();
		hb.setEscapeUnicode(true);
		hb.clean("\u21A9 \u2660 \u2603");
		assertEquals("&#8617; &#9824; &#9731;", hb.toString());
	}
	
	@Test
	public void testCleanUnicode2() {
		HtmlBuilder hb = new HtmlBuilder();
		hb.setEscapeUnicode(true);
		hb.clean(String.valueOf(Character.toChars(0x1F4A9))); // poo emoji
		assertEquals(0x1F4A9,128169);
		assertEquals("&#128169;", hb.toString());
	}
	/**
	 * Tests the method {@link XMLPrinter#clear()}.
	 */
	@Test
	public void testClear() {
		HtmlBuilder hb = new HtmlBuilder();
		hb.open("tag1");
		hb.clear();
		assertEquals("", hb.toString());
		hb.open("tag2");
		hb.close();
		assertEquals("<tag2/>", hb.toString());
	}
	
	/**
	 * Tests the method {@link HtmlBuilder#addButton(uk.ac.ed.epcc.webapp.AppContext, String, uk.ac.ed.epcc.webapp.forms.result.FormResult)}.
	 */
	@Test
	public void testAddButton() {
		HtmlBuilder hb = new HtmlBuilder();
		hb.addButton(getContext(), "test button", new RedirectResult("http://test.url"));
		Assert.assertEquals("<form action='http://test.url'><input class='input_button' type='submit' value='test button'/></form>", hb.toString());
	}
	
	/**
	 * Tests the method {@link HtmlBuilder#addLink(AppContext, String, uk.ac.ed.epcc.webapp.forms.result.FormResult)}.
	 */
	@Test
	public void testAddLink() {
		HtmlBuilder hb = new HtmlBuilder();
		hb.addLink(getContext(), "test link", new RedirectResult("http://test.url"));
		Assert.assertEquals("<a href='http://test.url'>test link</a>", hb.toString());
		hb.clear();
		hb.addLink(getContext(), "test text", null);
		Assert.assertEquals("test text", hb.toString());
	}
	
	/**
	 * Tests the method {@link HtmlBuilder#addTable(AppContext, Table)}.
	 */
	@Test
	public void testAddTable() {
		HtmlBuilder hb = new HtmlBuilder();
		Table<String, String> t = new Table<>();
		t.put("col1", "row1", 100);
		hb.addTable(getContext(), t);
		Assert.assertEquals(
				"<table class='auto' rows='1' cols='1'>\n" +
				"<tr count='0'><th class='first' count='0'>col1</th></tr>\n" +
				"<tr count='1'><td class='first' count='0' numeric='true'>100</td></tr>\n" +
				"</table>\n",
				hb.toString());
	}
	
	
	@Test
	public void testAddTableWithColName() {
		HtmlBuilder hb = new HtmlBuilder();
		Table<String, String> t = new Table<>();
		t.put("col1", "row1", 100);
		t.put("col2", "row1", 100);
		
		t.setColName("col1", "Igor");
		t.setColName("col2", "Newt");
		hb.addTable(getContext(), t);
		Assert.assertEquals(
				"<table class='auto' rows='1' cols='2'>\n" +
				"<tr count='0'><th class='first' count='0'>Igor</th><th class='main' count='1'>Newt</th></tr>\n" +
				"<tr count='1'><td class='first' count='0' numeric='true'>100</td><td class='main' count='1' numeric='true'>100</td></tr>\n" +
				"</table>\n",
				hb.toString());
	}
	@Test
	public void testAddTableWithMulticolumn() {
		HtmlBuilder hb = new HtmlBuilder();
		Table t = new Table();
		t.put("col1", "row1", 100);
		t.put("col2", "row1", 200);
		t.put("col1", "row2", new MultiColumnText(2, "center", "text"));
		hb.addTable(getContext(), t);
		Assert.assertEquals(
				"<table class='auto' rows='2' cols='2'>\n" +
				"<tr count='0'><th class='first' count='0'>col1</th><th class='main' count='1'>col2</th></tr>\n" +
				"<tr count='1'><td class='first' count='0' numeric='true'>100</td><td class='main' count='1' numeric='true'>200</td></tr>\n" +
				"<tr count='2'><td class='first center' count='0' colspan='2'>text</td></tr>\n"+
				"</table>\n",
				hb.toString());
	}
	
	@Test
	public void testDeDupTable() {
		HtmlBuilder hb = new HtmlBuilder();
		Table t = new Table();
		t.put("col1", "row1", "A");
		t.put("col2", "row1", "B");
		t.put("col1", "row2", "A");
		t.put("col2", "row2", "C");
		
		t.getCol("col1").setDedup(true);
		t.getCol("col2").setDedup(true);
		hb.addTable(ctx, t);
		
		Assert.assertEquals(
				"<table class='auto' rows='2' cols='2'>\n" + 
				"<tr count='0'><th class='first' count='0'>col1</th><th class='main' count='1'>col2</th></tr>\n" + 
				"<tr count='1'><td class='first' count='0' rowspan='2'>A</td><td class='main' count='1'>B</td></tr>\n" + 
				"<tr count='2'>		<td class='main' count='1'>C</td></tr>\n" + 
				"</table>\n",
				hb.toString());
	}
	
	@Test
	public void testFormatGroups() throws InvalidArgument {
		HtmlBuilder hb = new HtmlBuilder();
		Table t = new Table();
		t.put("col1", "row1", "A");
		t.put("col2", "row1", "B");
		t.put("col3", "row1", "C");
		t.put("col4", "row1", "D");
		t.put("col1", "row2", "P");
		t.put("col2", "row2", "Q");
		t.put("col3", "row2", "R");
		t.put("col4", "row2", "S");
		t.addToGroup("G1", "col1");
		t.addToGroup("G1", "col2");
		t.addToGroup("G2", "col3");
		t.addToGroup("G2", "col4");
		t.setKeyName("rows");
		t.setPrintGroups(true);
	
		hb.addTable(ctx, t);
		
		Assert.assertEquals(
				"<table class='auto' rows='2' cols='5'>\n" + 
				"<tr count='0'><th class='key' count='0' rowspan='2'>rows</th><th class='main' count='1' colspan='2'>G1</th><th class='main' count='3' colspan='2'>G2</th></tr>\n" + 
				"<tr count='1'><th class='main' count='1'>col1</th><th class='main' count='2'>col2</th><th class='main' count='3'>col3</th><th class='main' count='4'>col4</th></tr>\n" + 		
				"<tr count='2'><td class='key' count='0'>row1</td><td class='main' count='1'>A</td><td class='main' count='2'>B</td><td class='main' count='3'>C</td><td class='main' count='4'>D</td></tr>\n" + 
				"<tr count='3'><td class='key' count='0'>row2</td><td class='main' count='1'>P</td><td class='main' count='2'>Q</td><td class='main' count='3'>R</td><td class='main' count='4'>S</td></tr>\n" + 
				"</table>\n",
				hb.toString());
	
	}
	/**
	 * Tests the method {@link HtmlBuilder#addColumn(AppContext, Table, Object)}.
	 */
	@Test
	public void testAddColumn() {
		HtmlBuilder hb = new HtmlBuilder();
		Table<String, String> t = new Table<>();
		t.put("col1", "row1", 100);
		t.put("col1", "row2", 200);
		hb.addColumn(getContext(), t, "col1");
		Assert.assertEquals(
				"<table class='column'>\n" +
				"<tr><td numeric='true'>100</td></tr>\n" +
				"<tr><td numeric='true'>200</td></tr>\n" +
				"</table>\n",
				hb.toString());
	}
	
	/**
	 * Tests the method {@link HtmlBuilder#getText()}.
	 */
	@Test
	public void testGetText() {
		HtmlBuilder hb = new HtmlBuilder();
		Text text = (Text) hb.getText();
		text.addText("test text");
		HtmlPrinter printer = (HtmlPrinter) text.getText();
		printer.open("tag1");
		printer.close();
		printer.appendParent();
		text.appendParent();
		Assert.assertEquals("<div class='para'>test text<tag1/></div>\n", hb.toString());
	}
	
	/**
	 * Tests the method {@link HtmlBuilder#getHeading(int)}.
	 */
	@Test
	public void testGetHeading() {
		HtmlBuilder hb = new HtmlBuilder();
		Heading heading = (Heading) hb.getHeading(1);
		heading.addText("test ");
		HtmlPrinter printer = (HtmlPrinter) heading.getText();
		printer.clean("heading");
		printer.appendParent();
		heading.appendParent();
		Assert.assertEquals("<h1>test heading</h1>\n", hb.toString());
	}
	
	/**
	 * Tests the method {@link HtmlBuilder#getPanel(String)}.
	 */
	@Test
	public void testGetPanel() {
		HtmlBuilder hb = new HtmlBuilder();
		Panel p = (Panel) hb.getPanel("test");
		p.open("tag1");
		p.clean("test panel");
		p.close();
		p.appendParent();
		Assert.assertEquals("<div class='test'>\n<tag1>test panel</tag1>\n</div>\n", hb.toString());
	}
	
	/**
	 * Tests the method {@link HtmlBuilder#getSpan()}.
	 */
	@Test
	public void testGetSpan() {
		HtmlBuilder hb = new HtmlBuilder();
		ExtendedXMLBuilder builder = hb.getSpan();
		builder.clean("test text");
		builder.appendParent();
		Assert.assertEquals("test text", hb.toString());
	}
	
	/**
	 * Tests the method {@link HtmlBuilder#addParent()}.
	 */
	@Test
	public void testAddParent() {
		HtmlBuilder parent = new HtmlBuilder();
		HtmlBuilder hb = new HtmlBuilder(parent);
		hb.clean("test text");
		hb.addParent();
		Assert.assertEquals("test text", parent.toString());
	}
	
	@Test
	public void testNest() {
		HtmlBuilder parent = new HtmlBuilder();
		ContentBuilder h2 = parent.getHeading(2);
		ExtendedXMLBuilder h = h2.getText();
		
		h.addClass("warn");
		h.clean("1234");
		h.appendParent();
		h2.addParent();
		assertEquals("<h2><span class='warn'>1234</span></h2>", parent.toString().trim());
		assertEquals("<h2><span class='warn'>1234</span></h2>", h2.toString().trim());
		assertEquals("<span class='warn'>1234</span>",h.toString().trim());
	}
	
	
}