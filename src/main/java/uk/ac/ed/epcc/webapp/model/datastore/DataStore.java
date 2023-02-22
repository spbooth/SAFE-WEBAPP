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

import java.util.Map;


import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.inputs.FileInput;
import uk.ac.ed.epcc.webapp.jdbc.table.BlobType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.Classification;
import uk.ac.ed.epcc.webapp.model.ClassificationFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;

/** A {@link DataObjectFactory} for storing named data fixtures in the database.
 * 
 * Intended for semi-dynamic data like cryptographic material that should not be embedded in the application
 * or needs to be replicated in a multi-server deployment.
 * @author Stephen Booth
 *
 */
public class DataStore extends ClassificationFactory<DataStore.Data>  {

	public DataStore(AppContext conn) {
		super();
		setContext(conn, "DataStore");
	}

	/**
	 * 
	 */
	private static final String DATA = "Data";
	

	@Override
	public TableSpecification getDefaultTableSpecification(AppContext c, String table) {
		TableSpecification spec = super.getDefaultTableSpecification(c, table);
		
		spec.setField(DATA, new BlobType());
	
		return spec;
	}


	@Override
	protected Map<String, Selector> getSelectors() {
		Map<String, Selector> selectors = super.getSelectors();
		selectors.put(DATA,new Selector<FileInput>() {

			@Override
			public FileInput getInput() {
				return new FileInput();
			}
		});
		return selectors;
	}
	public class Data extends Classification{

		/**
		 * @param r
		 */
		protected Data(Record r) {
			super(r,DataStore.this);
		}
		
		public StreamData getData() throws DataFault {
			return record.getStreamDataProperty(DATA);
		}
		
		public void setData(StreamData data) {
			record.setProperty(DATA, data);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected Data makeBDO(Record res) throws DataFault {
		return new Data(res);
	}

	
}
