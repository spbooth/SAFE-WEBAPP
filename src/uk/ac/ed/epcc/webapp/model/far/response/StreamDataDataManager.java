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

import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.BlobType;
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamDataWrapper;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response;
import uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link ResponseDataManager} for storing {@link StreamData} objects from uploaded files
 * @author spb
 * @param <R> {@link Response} type
 * @param <F> {@link DynamicForm} type
 *
 */

public class StreamDataDataManager<R extends Response<F>,F extends DynamicForm> extends ResponseDataManager<StreamDataDataManager<R,F>.StreamDataRecord, R, F> {

	private static final String DATA_FIELD="Data";
	private static final String MIME_FIELD="Mime";
	private static final String NAME_FIELD="Name";
 	/**
	 * @param manager
	 */
	public StreamDataDataManager(ResponseManager<R, F> manager) {
		super(manager, "StreamData");
	}

	public class StreamDataRecord extends ResponseDataManager.ResponseData<MimeStreamData, R,F> {

		/**
		 * @param res {@link Record}
		 */
		public StreamDataRecord(Record res) {
			super(StreamDataDataManager.this, res);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData#getData()
		 */
		@Override
		public MimeStreamData getData() {
			try {
				StreamData data = record.getStreamDataProperty(DATA_FIELD);
				if( data == null || data.getLength() == 0){
					return null;
				}
				String mime = record.getStringProperty(MIME_FIELD,"application/octet-stream");
				String name = record.getStringProperty(NAME_FIELD,"uploaded-file");
				
				return new MimeStreamDataWrapper(data, mime, name);
				
				
			} catch (DataFault e) {
				getLogger().error("Error retrieving stream data",e);
				return null;
			}
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData#setData(java.lang.Object)
		 */
		@Override
		public void setData(MimeStreamData msd) {
			if( msd == null ){
				record.setProperty(NAME_FIELD, null);
				record.setProperty(MIME_FIELD, null);
				record.setProperty(DATA_FIELD, null);
			}else{

				record.setProperty(NAME_FIELD, msd.getName());
				record.setProperty(MIME_FIELD, msd.getContentType());

				record.setProperty(DATA_FIELD, msd);
			}
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
		 */
		@Override
		public ContentBuilder addContent(ContentBuilder builder) {
			MimeStreamData data = getData();
			if (data != null){
				
				try {
					builder.addLink(getContext(), data.getName(), ((ResponseManager<R, F>)getResponse().getResponseManager()).getServeResult(this));
				} catch (DataException e) {
					getLogger().error("Error making serv link",e);
					builder.addText(data.getName());
				}
			}
			return builder;
		}

		@Override
		public MimeStreamData getServeData() {
			return getData();
		}
		@Override
		public boolean isAttchement() {
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new StreamDataRecord(res);
	}

	@Override
	public Class<? super StreamDataDataManager<R, F>.StreamDataRecord> getTarget() {
		return StreamDataRecord.class;
	}

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table, IndexedProducer<Question> leftFac, String leftField,
			IndexedProducer<R> rightFac, String rightField) {
		
		TableSpecification spec = super.getDefaultTableSpecification(c, table, leftFac, leftField,
						rightFac, rightField);
		spec.setField(DATA_FIELD, new BlobType());
		spec.setField(NAME_FIELD, new StringFieldType(true, null, 128));
		spec.setField(MIME_FIELD, new StringFieldType(true, null, 64));
		return spec;
	}

	

	
}