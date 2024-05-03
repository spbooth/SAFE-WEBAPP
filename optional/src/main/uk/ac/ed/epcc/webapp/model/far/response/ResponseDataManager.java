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

import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.LinkManager;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** a linking table that links a response to a specific question in the targetted form.
 * This is sub-classed for each type of data produced and holds the actual response data in the linking table.
 * @author spb
 * @param <D> type of link class
 * @param <R> Response type
 * @param <F> DynamicForm type
 *
 */

public abstract class ResponseDataManager<D extends ResponseDataManager.ResponseData<?,R,F>,R extends ResponseManager.Response<F>,F extends DynamicForm> extends LinkManager<D, QuestionManager.Question, R> {
	protected static final String CHANGED_BY_FIELD = "ChangedBy";
	protected static final String MODIFIED_FIELD = "Modified";
	
	public ResponseDataManager(ResponseManager<R,F> manager,String data_tag){
		super(manager.getContext(),manager.getTag()+data_tag,((QuestionManager)manager.getManager().getChildManager().getChildManager().getChildManager()),"QuestionID",manager,"ResponseID");
	}
	public abstract static class ResponseData<T,R extends ResponseManager.Response<F>, F extends DynamicForm> extends LinkManager.Link<QuestionManager.Question, R> implements UIGenerator{

		/**
		 * @param man
		 * @param res
		 */
		public ResponseData(ResponseDataManager<?,R,F> man, Record res) {
			super(man, res);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.IndexedLinkManager.Link#setup()
		 */
		@Override
		protected void setup() throws Exception {
			
			
		}
		/** return a {@link MimeStreamData} if the content can be served via a link
		 * 
		 * @return
		 * @throws Exception 
		 */
		public abstract MimeStreamData getServeData() throws Exception;
		public Question getQuestion() throws DataException{
			return getLeft();
		}
		public R getResponse() throws DataException{
			return getRight();
		}
		 public abstract T getData();
		 public abstract void setData(T data);
		 public  boolean hasData(){
			 return getData() != null;
		 }
		 public AppUser getLastEditor(){
			 Integer i = record.getIntProperty(CHANGED_BY_FIELD, 0);
			 if( i == null || i.intValue()==0){
				 return null;
			 }
			 return  (AppUser) getContext().getService(SessionService.class).getLoginFactory().find(i);
		 }
		 public Date getLastChange(){
			 return record.getDateProperty(MODIFIED_FIELD);
		 }
		 @Override
		 protected void pre_commit(boolean dirty) throws DataFault {
			 super.pre_commit(dirty);
			 if(dirty){
				 CurrentTimeService time = getContext().getService(CurrentTimeService.class);
				 record.setOptionalProperty(MODIFIED_FIELD, time.getCurrentTime());
				 SessionService<?> sess = getContext().getService(SessionService.class);
				 if( sess != null && sess.haveCurrentUser()){
					 record.setOptionalProperty(CHANGED_BY_FIELD, sess.getCurrentPerson().getID());
				 }
			 }
		 }

		public String getDataAsString() throws Exception {
			Input input = getQuestion().getInput();
			T data = getData();
			String str = input.getPrettyString(data);
			if (str==null || str.equals("no value")) {
				str = "n/a";
			}
			return str;
		}
		
		@Override
		public ContentBuilder addContent(ContentBuilder builder) {
			try{
				ExtendedXMLBuilder answer = builder.getSpan();
				String ans = getDataAsString();
				answer.clean(ans);
				answer.appendParent();
			}catch(Exception e){
				getLogger().error("Problem generating content",e);
			}
			return builder;
		}
		public boolean isAttchement(){
			return false;
		}
	}
	public D makeData(Question q, R response) throws Exception{
		return makeLink(q, response);
	}
	
	
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table, IndexedProducer<Question> leftFac, String leftField,
			IndexedProducer<R> rightFac, String rightField) {
		
		TableSpecification spec = super.getDefaultTableSpecification(c, table, leftFac, leftField,
						rightFac, rightField);
		spec.setField(MODIFIED_FIELD, new DateFieldType(true, null));
		spec.setField(CHANGED_BY_FIELD, new IntegerFieldType());
		return spec;
	}
	
}