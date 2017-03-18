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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.ReferenceFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.model.far.AbstractPartTransitionProvider;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.handler.QuestionFormHandler;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData;
import uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** a factory for responses to {@link DynamicForm}s.
 * This class is abstract because much of the behaviour (specifically how responses are created and the rules for who can
 * edit  a response) are very specific to the surrounding logic.
 * 
 * ResponseManagers implement {@link ServeDataProducer} allowing any user
 * @author spb
 * @param <R> type of {@link Response}
 * @param <F> type of {@link DynamicForm}
 *
 */

public abstract class ResponseManager<R extends ResponseManager.Response<F>,F extends DynamicFormManager.DynamicForm> extends DataObjectFactory<R> implements ServeDataProducer {

	private static final String FORM_ID = "FormID";
	private final DynamicFormManager<F> form_manager;
	/**
	 * 
	 */
	public ResponseManager(DynamicFormManager<F> manager, String tag) {
		super();
		this.form_manager=manager;
		setContext(manager.getContext(), tag);
	}
	/** Get the {@link DynamicFormManager} corresponding to this response
	 * 
	 * @return
	 */
	public DynamicFormManager<F> getManager(){
		return form_manager;
	}

	/** This represents a response to a {@link DynamicForm}
	 * 
	 * The logic for how a response is generated (and how the form we are responding to is selected)
	 * is up to the sub-class. For example this may be an application to a particular funding call
	 * and the form to be completed is encoded in funding-call object. Or the system might
	 * have a single application form where the current valid form is specified in some manner.
	 * 
	 * @author spb
	 *
	 * @param <D>
	 */
	public abstract static class Response<D extends DynamicForm> extends DataObject{

		private final ResponseManager manager;
		/**
		 * @param r
		 */
		public Response(ResponseManager<? extends Response<D>, D> manager,Record r) {
			super(r);
			this.manager=manager;
		}
		public ResponseManager<? extends Response<D>, D> getResponseManager(){
			return manager;
		}
		/** Get the {@link DynamicForm} we are in response to.
		 * This should be a frozen form as we assume it is static.
		 * 
		 * @return
		 * @throws DataException
		 */
		public D getForm() throws DataException{
			return (D) manager.form_manager.find(record.getIntProperty(FORM_ID));
		}
		/** Set the {@link DynamicForm}. 
		 * This should be set once when the response if first created.
		 * 
		 * @param form
		 */
		public void setForm(D form){
			record.setProperty(FORM_ID, form.getID());
		}
		/** set the data in response to a question
		 * 
		 * @param q
		 * @param data
		 * @throws Exception
		 */
		public <T> void setData(Question q, T data) throws Exception{
			manager.setData(q, this, data);
		}
		/** get the data provided in response to a particular question.
		 * 
		 * @param q
		 * @return
		 * @throws Exception
		 */
		public <T> T getData(Question q) throws Exception{
			return (T) manager.getData(q, this);
		}
		/** get the data-object that contains the response data.
		 * 
		 * 
		 * @param q
		 * @return
		 * @throws Exception
		 */
		public <T> ResponseData<T, ? extends Response<D>,D>getWrapper(Question q) throws Exception{
			return manager.getWrapper(q, this);
		}
		/** Access control method to view the response
		 * 
		 * 
		 * @param sess
		 * @return boolean
		 */
		public abstract boolean canView(SessionService<?> sess);
		/** Access control method to edit the response
		 * 
		 * @param sess
		 * @return
		 */
		public abstract boolean canEdit(SessionService<?> sess);
		/** a text descriptor for the response.
		 * For example the name of the application being made in response to an application form. 
		 * 
		 * @return
		 */
		public abstract String getDescriptor();
		
		/** method to be called when the form is finally submitted.
		 * 
		 * This should usually change internal state and freeze the response (changing the
		 * value returned by {@link #canEdit(SessionService)} so
		 * the response contents don't change while the response is reviewed.
		 * 
		 * Other submit time side-effects could also be included here
		 * 
		 * The reviewers could return the response to the submitter returning it
		 * to a previous state
		 * 
		 * @return FormResult
		 * @throws Exception
		 */
		public abstract FormResult submit() throws Exception;
		
		/** This method must return true before the form edit transition allows
		 * the submit button to be pressed.
		 * 
		 * @return boolean
		 * @throws Exception
		 */
		public abstract boolean validate() throws Exception;
		
		/** get a {@link MimeStreamData} representing a downloadable version of the response
		 * 
		 * @return
		 * @throws DataException
		 */
		public abstract MimeStreamData getDataStream() throws DataException;
				
	}

	/** get the data from a Question from a response
	 * 
	 * @param q
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public <T> T  getData(Question q, R response) throws Exception{
		ResponseData<T, R,F> link = getWrapper(q, response);
		if( link == null ){
			return null;
		}
		return link.getData();
	}

	/** get the {@link ResponseData} object.
	 * @param q
	 * @param response
	 * @return {@link ResponseData} or null;
	 * @throws Exception
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws DataException
	 */
	public <T> ResponseData<T, R,F> getWrapper(Question q, R response)
			throws Exception, NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException, DataException {
		ResponseDataManager<?, R, F> manager = getDataManager(q);
		ResponseData<T, R,F> link = (ResponseData<T, R,F>) manager.getLink(q, response);
		return link;
	}
	/** set the data for a question and response
	 * 
	 * @param q
	 * @param response
	 * @param data
	 * @throws Exception
	 */
	public <T> void setData(Question q, R response, T data) throws Exception{
		ResponseDataManager<?, R, F> manager = getDataManager(q);
		ResponseData<T, R,F> link = (ResponseData<T, R,F>) manager.makeData(q, response);
		link.setData(data);
		link.commit();
	}

	/** get the {@link ResponseDataManager} that handles the data generated by
	 * a question. Questions with the same type of response data can share a {@link ResponseDataManager}.
	 * @param q the {@link Question}
	 * @return
	 * @throws Exception
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	protected ResponseDataManager<?, R, F> getDataManager(Question q)
			throws Exception, NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		QuestionFormHandler handler = q.getHandler(); 
		Class<? extends ResponseDataManager> clazz= handler.getDataClass();
		Constructor cons  = clazz.getConstructor(ResponseManager.class);
		ResponseDataManager<? , R, F> manager = (ResponseDataManager<?, R, F>) cons.newInstance(this);
		return manager;
	}
	

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = new TableSpecification("ResponseID");
		spec.setField(FORM_ID, new ReferenceFieldType(form_manager.getTag()));
		
		return spec;
	}

	/** get a filter for all responses to a particular form.
	 * 
	 * @param form
	 * @return
	 */
	public SQLFilter<R> getFormFilter(F form){
		return new ReferenceFilter<R, F>(this, FORM_ID, form);
	}
	
	/** get a {@link ServeDataResult} (for a download link) from a {@link ResponseData}
	 * 
	 * @param wrapper
	 * @return
	 * @throws DataException
	 */
	public <T> ServeDataResult getServeResult(ResponseData<T, R,F> wrapper) throws DataException{
		LinkedList<String> args = new LinkedList<String>();
		R response = wrapper.getResponse();
		try {
			if( wrapper.getServeData() == null || ! isMine(response)){
				return null;
			}
		} catch (Exception e) {
			getLogger().debug("Exception generating serve-data", e);
			return null;
		}
		Question q = wrapper.getQuestion();
		args = AbstractPartTransitionProvider.getID(args, q);
		args.pop(); // remove the form id
		args.push(Integer.toString(response.getID())); // replace with response id
		
		return new ServeDataResult(this, args);
	}
	
	@Override
	public MimeStreamData getData(SessionService user, List<String> path)
			throws Exception {
		LinkedList<String> list;
		if( path instanceof LinkedList){
			list=(LinkedList<String>) path;
		}else{
			list=new LinkedList<String>(path);
		}
		
		R response = find(Integer.parseInt(list.pop()));
		if( ! response.canView(user)){
			return null;
		}
		
		F response_form = response.getForm();
		Question q = (Question) AbstractPartTransitionProvider.getTarget(response_form, getManager().getChildManager(), list);
 		if( q == null){
 			getLogger().error("question not found");
 			return null;
 		}
		return getWrapper(q, response).getServeData();
	}
	
	public String getDownloadName(SessionService user, List<String> path) throws Exception{
		MimeStreamData data = getData(user, path);
		if( data == null ){
			return null;
		}
		// Could add name method direct to wrapper but overhead is small.
		return data.getName();
	}

	
}