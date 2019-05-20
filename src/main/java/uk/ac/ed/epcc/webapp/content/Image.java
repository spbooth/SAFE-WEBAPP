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

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;

/**
 * @author Stephen Booth
 *
 */
public class Image extends AbstractContexed implements UIGenerator {
	/**
	 * @param data
	 * @param alt
	 * @param help
	 * @param width
	 * @param height
	 */
	public Image(AppContext conn,ServeDataResult data, String alt, String help, Integer width, Integer height) {
		super(conn);
		this.data = data;
		this.alt = alt;
		this.help = help;
		this.width = width;
		this.height = height;
	}

	private final ServeDataResult data;
	private final String alt;
	private final String help;
	private Integer width;
	private Integer height;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
	 */
	@Override
	public ContentBuilder addContent(ContentBuilder builder) {
		builder.addImage(getContext(), alt, help, width, height, data);
		return builder;
	}

}
