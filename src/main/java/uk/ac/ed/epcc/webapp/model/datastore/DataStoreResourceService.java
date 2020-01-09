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
package uk.ac.ed.epcc.webapp.model.datastore;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;
import uk.ac.ed.epcc.webapp.model.datastore.DataStore.Data;
import uk.ac.ed.epcc.webapp.resource.ResourceService;

/**
 * @author Stephen Booth
 *
 */
public class DataStoreResourceService extends AbstractContexed implements ResourceService {
	/**
	 * 
	 */
	private static final String DATASTORE_PREFIX = "datastore:";
	private final ResourceService nested;
	
	public DataStoreResourceService(AppContext conn) {
		super(conn);
		nested=conn.getService(ResourceService.class);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	@Override
	public Class<? super ResourceService> getType() {
		return ResourceService.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextCleanup#cleanup()
	 */
	@Override
	public void cleanup() {
		nested.cleanup();

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.resource.ResourceService#getResource(java.lang.String)
	 */
	@Override
	public URL getResource(String name) {
		try {
			if( name.startsWith(DATASTORE_PREFIX)) {
				return new URL("datastore", "", name.substring(DATASTORE_PREFIX.length()));
			}
		} catch (MalformedURLException e) {
			getLogger().error("Failed to make URL", e);
		}
		return nested.getResource(name);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.resource.ResourceService#getResourceAsStream(java.lang.String)
	 */
	@Override
	public InputStream getResourceAsStream(String name) throws Exception {
		
		if( name.startsWith(DATASTORE_PREFIX)) {
			DataStore store = new DataStore(getContext());
			if( store.isValid()) {
				Data d = store.findFromString(name.substring(DATASTORE_PREFIX.length()));
				if( d != null ) {
					return d.getData().getInputStream();
				}else {
					return null;
				}
			}
		}
		return nested.getResourceAsStream(name);
	}

}
