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
package uk.ac.ed.epcc.webapp.messages;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.config.ConfigService;

/** An {@link AppContextService} that encodes the rules for generating the application
 * message bundle. The default is to use the bundle <em>messages</em>
 * this can be changed using the property <b>messages.bundle.list</b> which may specify multiple bundles via 
 * a comma separated list.
 * @author spb
 *
 */

@PreRequisiteService(ConfigService.class)
public class MessageBundleService implements Contexed, AppContextService<MessageBundleService> {
	private final ListControl control;
	private AppContext context;
	private Map<String,ResourceBundle> bundle_map=new HashMap<String, ResourceBundle>();
	public MessageBundleService(AppContext c){
		context=c;
		control = new ListControl(c);
	}
	public ResourceBundle getBundle(){
		return getBundle("messages");
	}
	public ResourceBundle getBundle(String tag){
		ResourceBundle bundle = bundle_map.get(tag);
		if( bundle == null ){
			bundle=makeBundle(tag);
			bundle_map.put(tag, bundle);
		}
		return bundle;
	}
	protected ResourceBundle makeBundle(String tag){
		ConfigService config = context.getService(ConfigService.class);
		String bundles= config.getServiceProperties().getProperty(tag+".bundle.list", tag);
		return ResourceBundle.getBundle(bundles, control);

	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#cleanup()
	 */
	public void cleanup() {
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	public Class<? super MessageBundleService> getType() {
		return MessageBundleService.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	public AppContext getContext() {
		return context;
	}

	

}