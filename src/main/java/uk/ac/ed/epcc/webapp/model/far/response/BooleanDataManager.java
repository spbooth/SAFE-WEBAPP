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
package uk.ac.ed.epcc.webapp.model.far.response;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response;

/** A {@link ResponseDataManager} for storing Boolean data.
 * @author spb
 * @param <R> {@link Response} type
 * @param <F> {@link DynamicForm} type
 *
 */

public class BooleanDataManager<R extends Response<F>,F extends DynamicForm,M extends BooleanDataManager<R,F,M>.BooleanData> 
extends ResponseDataManager<M, R, F> {

	private static final String DATA_FIELD="Data";
 	/**
	 * @param manager
	 * @param data_tag
	 */
	public BooleanDataManager(ResponseManager<R, F> manager) {
		super(manager, "BooleanData");
	}

	public class BooleanData extends ResponseDataManager.ResponseData<Boolean, R,F>{

		/**
		 * @param res {@link Record}
		 */
		public BooleanData(Record res) {
			super(BooleanDataManager.this, res);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData#getData()
		 */
		@Override
		public Boolean getData() {
			return record.getBooleanProperty(DATA_FIELD);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData#setData(java.lang.Object)
		 */
		@Override
		public void setData(Boolean data) {
			record.setProperty(DATA_FIELD, data);
		}
		@Override
		public MimeStreamData getServeData() throws Exception{
			ByteArrayMimeStreamData data = new ByteArrayMimeStreamData(Boolean.toString(getData()).getBytes());
			data.setMimeType("text/plain");
			data.setName(getQuestion().getName());
			return data;
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected M makeBDO(Record res) throws DataFault {
		return (M) new BooleanData(res);
	}

	@Override
	public Class<M> getTarget() {
		return (Class) BooleanData.class;
	}

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table, IndexedProducer<Question> leftFac, String leftField,
			IndexedProducer<R> rightFac, String rightField) {
		
		TableSpecification spec = super.getDefaultTableSpecification(c, table, leftFac, leftField,
						rightFac, rightField);
		spec.setField(DATA_FIELD, new BooleanFieldType(true, null));
		return spec;
	}
}