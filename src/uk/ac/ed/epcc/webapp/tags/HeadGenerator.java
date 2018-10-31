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
package uk.ac.ed.epcc.webapp.tags;

import java.util.LinkedHashSet;

import javax.servlet.ServletRequest;

import uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder;
import uk.ac.ed.epcc.webapp.content.XMLGenerator;

/** A class that generates the contents of a web-page <b>&lt;head&gt;</b> element for a page.
 * This may take content from both the configuration and the local page so it is cached in the
 * page request. This allows the surrounding page to add content before the head is generated if requires 
 * @author spb
 *
 */

public class HeadGenerator  implements XMLGenerator{

	/**
	 * 
	 */
	private static final String HEAD_GENERATOR_ATTR = "HeadGenerator";


	/**
	 * 
	 */
	private HeadGenerator() {
	}

	
	private String title="webapp page";  // page title
	/** most elements are stored as {@link XMLGenerator}s these should implement 
	 * hashCode and equals such that redundant entries are not duplicates
	 * 
	 */
	private LinkedHashSet<XMLGenerator> generators = new LinkedHashSet<>();
	
	public static HeadGenerator getHeadGenerator(ServletRequest request){
		HeadGenerator gen = (HeadGenerator) request.getAttribute(HEAD_GENERATOR_ATTR);
		if( gen == null){
			gen = new HeadGenerator();
			request.setAttribute(HEAD_GENERATOR_ATTR,gen);
		}
		return gen;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.XMLGenerator#addContent(uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder)
	 */
	@Override
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
		if( title != null ){
			builder.open("title");
			builder.clean(title);
			builder.close();
		}
		builder.clean("\n");
		for( XMLGenerator g : generators){
			g.addContent(builder);
			builder.clean("\n");
		}
		return builder;
	}
	
	public static class NamedMeta implements XMLGenerator{
		private final String name;
		private final String lang;
		private final String value;
		public NamedMeta(String name,String value){
			this.name=name;
			this.value=value;
			this.lang=null;
		}
		public NamedMeta(String name,String value,String lang){
			this.name=name;
			this.value=value;
			this.lang=lang;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.XMLGenerator#addContent(uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder)
		 */
		@Override
		public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
			builder.open("META");
			builder.attr("name",name);
			builder.attr("content",value);
			if( lang != null){
				builder.attr("lang",lang);
			}
			builder.close();
			return builder;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((lang == null) ? 0 : lang.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NamedMeta other = (NamedMeta) obj;
			if (lang == null) {
				if (other.lang != null)
					return false;
			} else if (!lang.equals(other.lang))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
	}
}