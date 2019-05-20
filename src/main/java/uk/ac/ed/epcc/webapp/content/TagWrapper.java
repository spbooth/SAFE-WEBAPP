//| Copyright - The University of Edinburgh 2018                            |
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

/**
 * @author Stephen Booth
 *
 */
public class TagWrapper implements UIGenerator {
    /**
	 * @param inner
	 * @param tag
	 */
	public TagWrapper(UIGenerator inner, String tag) {
		super();
		this.inner = inner;
		this.tag = tag;
	}
	private final UIGenerator inner;
    private final String tag;
    private boolean noBreak=false;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
	 */
	@Override
	public ContentBuilder addContent(ContentBuilder builder) {
		inner.addContent(builder);
		ExtendedXMLBuilder text = builder.getSpan();
		if( noBreak) {
		  text.nbs();
		}else{
			text.clean(" ");
		}
		text.clean(tag);
		text.appendParent();
		return builder;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inner == null) ? 0 : inner.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
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
		TagWrapper other = (TagWrapper) obj;
		if (inner == null) {
			if (other.inner != null)
				return false;
		} else if (!inner.equals(other.inner))
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}

	public String toString() {
		return inner.toString()+" "+tag;
	}
	public boolean isNoBreak() {
		return noBreak;
	}
	public void setNoBreak(boolean noBreak) {
		this.noBreak = noBreak;
	}
}
