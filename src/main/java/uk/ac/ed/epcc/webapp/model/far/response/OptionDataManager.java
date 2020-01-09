// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far.response;

import java.nio.charset.StandardCharsets;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response;

/** A {@link ResponseDataManager} for storing option data.
 * @author spb
 * @param <R> {@link Response} type
 * @param <F> {@link DynamicForm} type
 *
 */

public class OptionDataManager<R extends Response<F>,F extends DynamicForm, M extends OptionDataManager<R,F,M>.OptionData> 
extends ResponseDataManager<M, R, F> {

	private static final String DATA_FIELD="Data";
 	/**
	 * @param manager
	 */
	public OptionDataManager(ResponseManager<R, F> manager) {
		super(manager, "OptionData");
	}

	public class OptionData extends ResponseDataManager.ResponseData<String, R,F>{

		/**
		 * @param res {@link Record}
		 */
		public OptionData(Record res) {
			super(OptionDataManager.this, res);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData#getData()
		 */
		@Override
		public String getData() {
			return record.getStringProperty(DATA_FIELD);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData#setData(java.lang.Object)
		 */
		@Override
		public void setData(String data) {
			record.setProperty(DATA_FIELD, (Object) data);
		}
		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData#getServeData()
		 */
		@Override
		public MimeStreamData getServeData() throws Exception{
			ByteArrayMimeStreamData data = new ByteArrayMimeStreamData(getData().getBytes(StandardCharsets.UTF_8.name()));
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
		return (M) new OptionData(res);
	}

	@Override
	public Class<M> getTarget() {
		return (Class) OptionData.class;
	}

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table, IndexedProducer<Question> leftFac, String leftField,
			IndexedProducer<R> rightFac, String rightField) {
		
		TableSpecification spec = super.getDefaultTableSpecification(c, table, leftFac, leftField,
						rightFac, rightField);
		spec.setField(DATA_FIELD, new StringFieldType(true, null, 4096));
		return spec;
	}
}
