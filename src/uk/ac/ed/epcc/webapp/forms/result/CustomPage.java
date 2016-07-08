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
package uk.ac.ed.epcc.webapp.forms.result;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
/** Interface for objects that create a custom page of content.
 * 
 * @author spb
 *
 */
public interface CustomPage {
	/** This is the tag used to store the {@link CustomPage} object in the request while forwarding
	 * to the display page.
	 * 
	 */
	public static final String CUSTOM_PAGE_TAG="CustomPage";
	/** Get the title to show on the page
	 * 
	 * @return
	 */
   public String getTitle();
   /** get content to show on the page
    * 
    * This is formatted within a display block
    * 
    * @param conn
    * @param cb {@link ContentBuilder} to add content
    * @return modified {@link ContentBuilder}
    */
   public ContentBuilder addContent(AppContext conn,ContentBuilder cb);
}