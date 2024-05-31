//| Copyright - The University of Edinburgh 2019                            |
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

import java.net.URL;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;

/**
 * @author Stephen Booth
 *
 */
public class ExternalLink extends AbstractContexed implements XMLGenerator{

	/**
	 * @param text
	 * @param url
	 */
	public ExternalLink(AppContext conn,String text, URL url) {
		super(conn);
		this.text = text;
		this.url = url;
	}

	private final String text;
	private final URL url;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.XMLGenerator#addContent(uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder)
	 */
	@Override
	public SimpleXMLBuilder addContent(SimpleXMLBuilder b) {
		try {
			String string_url = url.toString(); // do this first to pick up any exception
			
			b.open("a");
			
			b.attr("href", string_url);
			b.attr("target", "_blank");
			b.attr("rel","noopener noreferrer external");
			b.clean(text);
			b.close();
			
		}catch(Exception e) {
			getLogger().error("Error making link", e);
			b.clean(text);
		}
		return b;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		ExternalLink other = (ExternalLink) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return text;
	}
	
}
