// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far.response;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
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

/** A {@link ResponseDataManager} for storing date data.
 * @author spb
 * @param <R> {@link Response} type
 * @param <F> {@link DynamicForm} type
 *
 */
public class DateDataManager<R extends Response<F>,F extends DynamicForm,M extends DateDataManager<R,F,M>.DateData> 
extends ResponseDataManager<M, R, F> {

	private static final String DATA_FIELD="Data";
 	/**
	 * @param manager
	 */
	public DateDataManager(ResponseManager<R, F> manager) {
		super(manager, "DateData");
	}

	public class DateData extends ResponseDataManager.ResponseData<Date, R,F>{

		/**
		 * @param res {@link Record}
		 */
		public DateData(Record res) {
			super(DateDataManager.this, res);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData#getData()
		 */
		@Override
		public Date getData() {
			return record.getDateProperty(DATA_FIELD);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData#setData(java.lang.Object)
		 */
		@Override
		public void setData(Date data) {
			record.setProperty(DATA_FIELD, data);
		}
		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData#getServeData()
		 */
		@Override
		public MimeStreamData getServeData() throws Exception{
			SimpleDateFormat df = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
			ByteArrayMimeStreamData data = new ByteArrayMimeStreamData(df.format(getData()).getBytes(StandardCharsets.UTF_8.name()));
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
		return new DateData(res);
	}

	@Override
	public Class<M> getTarget() {
		return (Class) DateData.class;
	}

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table, IndexedProducer<Question> leftFac, String leftField,
			IndexedProducer<R> rightFac, String rightField) {
		
		TableSpecification spec = super.getDefaultTableSpecification(c, table, leftFac, leftField,
						rightFac, rightField);
		spec.setField(DATA_FIELD, new DateFieldType(true, null));
		return spec;
	}
}
