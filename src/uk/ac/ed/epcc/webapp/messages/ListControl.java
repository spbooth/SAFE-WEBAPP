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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import uk.ac.ed.epcc.webapp.AppContext;

/** A {@link ResourceBundle.Control} that allows multiple bundles to 
 * be specified as a comma separated list. contents will be merged 
 * into a single bundle.
 * @author spb
 *
 */

public class ListControl extends ResourceBundle.Control{
	public ListControl(AppContext conn) {
		super();
		this.conn = conn;
	}

	private final AppContext conn;

	/**
	 * 
	 */
	private static final String LIST = "list";

	@Override
	public List<String> getFormats(String baseName) {
		if( baseName != null && baseName.contains(",")){
			return Arrays.asList(LIST);
		}
		return super.getFormats(baseName);
	}

	@Override
	public ResourceBundle newBundle(String baseName, Locale locale,
			String format, ClassLoader loader, boolean reload)
			throws IllegalAccessException, InstantiationException, IOException {
		if(format.equals(LIST)){
			Properties props = null;
			for(String name : baseName.split(",")){
				String bundleName = toBundleName(name, locale);
				String resourceName = toResourceName(bundleName, "properties");
				InputStream stream = loader.getResourceAsStream(resourceName);
				if( stream != null){
					if( props == null){
						props=new SubstitutionProperties(conn);
					}else{
						props = new SubstitutionProperties(conn,props);
					}
					props.load(stream);
				}
			}
			if( props == null){
				// empty bundle needed not null
				props = new Properties();
			}
			return new PropResourceBundle(props);
		}
		return super.newBundle(baseName, locale, format, loader, reload);
	}

	

}