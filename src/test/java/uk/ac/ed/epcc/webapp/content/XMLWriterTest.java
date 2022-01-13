//| Copyright - The University of Edinburgh 2011                            |
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
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.content;

import java.io.StringWriter;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Unit tests of the class {@link XMLWriter}.
 *  
 * @author aheyrovs
 *
 */

public class XMLWriterTest {

	/**
	 * Unit test of the class {@link XMLWriter}.
	 */
	@Test
	public void testXMLWriter() {
		StringWriter sw = new StringWriter();
		XMLWriter xmlWriter = new XMLWriter(sw);
		xmlWriter.open("tag1", new String[][]{{"attr1_name", "attr1_value"}});
		xmlWriter.clean('"');
		xmlWriter.clean("test text");
		xmlWriter.clean('"');
		xmlWriter.close();
		xmlWriter.appendParent();
		Assert.assertEquals("<tag1 attr1_name='attr1_value'>&#34;test text&#34;</tag1>", sw.toString());
		SimpleXMLBuilder builder = xmlWriter.getNested();
		builder.open("tag2");
		builder.close();
		builder.appendParent();
		Assert.assertEquals("<tag1 attr1_name='attr1_value'>&#34;test text&#34;</tag1><tag2/>", sw.toString());
		Assert.assertNull(xmlWriter.getParent());
	}
}