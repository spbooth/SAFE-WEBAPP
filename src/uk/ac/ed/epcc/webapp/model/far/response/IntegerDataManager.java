// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far.response;

import java.nio.charset.StandardCharsets;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response;

/** A {@link ResponseDataManager} for storing integer data.
 * @author spb
 * @param <R> {@link Response} type
 * @param <F> {@link DynamicForm} type
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.3 $")
public class IntegerDataManager<R extends Response<F>,F extends DynamicForm> extends ResponseDataManager<IntegerDataManager<R,F>.IntegerData, R, F> {

	private static final String DATA_FIELD="Data";
 	/**
	 * @param manager
	 */
	public IntegerDataManager(ResponseManager<R, F> manager) {
		super(manager, "IntegerData");
	}

	public class IntegerData extends ResponseDataManager.ResponseData<Integer, R,F>{

		/**
		 * @param res {@link Record}
		 */
		public IntegerData(Record res) {
			super(IntegerDataManager.this, res);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData#getData()
		 */
		@Override
		public Integer getData() {
			return record.getIntProperty(DATA_FIELD);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData#setData(java.lang.Object)
		 */
		@Override
		public void setData(Integer data) {
			record.setProperty(DATA_FIELD, data);
		}
		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData#getServeData()
		 */
		@Override
		public MimeStreamData getServeData() throws Exception{
			ByteArrayMimeStreamData data = new ByteArrayMimeStreamData(getData().toString().getBytes(StandardCharsets.UTF_8.name()));
			data.setMimeType("text/plain");
			data.setName(getQuestion().getName());
			return data;
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new IntegerData(res);
	}

	@Override
	public Class<? super IntegerDataManager<R, F>.IntegerData> getTarget() {
		return IntegerData.class;
	}

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table, IndexedProducer<Question> leftFac, String leftField,
			IndexedProducer<R> rightFac, String rightField) {
		
		TableSpecification spec = super.getDefaultTableSpecification(c, table, leftFac, leftField,
						rightFac, rightField);
		spec.setField(DATA_FIELD, new IntegerFieldType(true, null));
		return spec;
	}
}
