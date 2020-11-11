//| Copyright - The University of Edinburgh 2020                            |
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

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;

/** A wrapper to convert {@link PreDefinedContent} into an {@link AppContext}
 * so it can be loaded as a plugin. For exmaple on the login page 
 *
 * @author Stephen Booth
 *
 */
public class BlockContent extends AbstractContexed implements UIGenerator{
	private final String tag;
	/**
	 * @param conn
	 */
	public BlockContent(AppContext conn,String tag) {
		super(conn);
		this.tag=tag;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
	 */
	@Override
	public ContentBuilder addContent(ContentBuilder builder) {
		PreDefinedContent content = new PreDefinedContent(getContext(), getContext().getInitParameter(tag+".message",tag));
		content.addAsBlock(builder);
		return builder;
	}

}
