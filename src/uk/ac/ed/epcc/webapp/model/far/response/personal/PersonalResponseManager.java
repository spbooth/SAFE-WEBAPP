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
package uk.ac.ed.epcc.webapp.model.far.response.personal;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryCreator;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.ReferenceFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.response.CompleteVisitor;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseTransitionProvider;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link ResponseManager} where each registered user is allowed to submit a response to the forms.
 * @author spb
 * @param <R>
 * @param <D> 
 *
 */

public class PersonalResponseManager<R extends PersonalResponseManager.PersonalResponse<D>, D extends DynamicForm> extends ResponseManager<R, D> implements TransitionFactoryCreator<TransitionFactory>{

	/**
	 * 
	 */
	public static final String FORM_MANAGER_CONF_PREFIX = "form_manager.";
	/**
	 * 
	 */
	protected static final String RESPONSE_TAG = "Response";
	/**
	 * 
	 */
	protected static final String PART_TAG = "Part";
	/**
	 * 
	 */
	protected static final String SUBMITTER_ID = "SubmitterID";

	public static class PersonalResponse<D extends DynamicForm> extends ResponseManager.Response<D>{

		/**
		 * @param r
		 */
		public PersonalResponse(PersonalResponseManager<?, D> manager,Record r) {
			super(manager, r);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response#canView(uk.ac.ed.epcc.webapp.session.SessionService)
		 */
		@Override
		public boolean canView(SessionService<?> sess) {
			return sess.hasRole(SessionService.ADMIN_ROLE) || isSubmitter(sess);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response#canEdit(uk.ac.ed.epcc.webapp.session.SessionService)
		 */
		@Override
		public boolean canEdit(SessionService<?> sess) {
			return isSubmitter(sess);
		}
		
		public String getDescriptor() {
			return "";
		}
		
		public boolean isSubmitter(SessionService<?> sess){
			return sess.getCurrentPerson().getID() == record.getIntProperty(SUBMITTER_ID);
		}
		public void setPerson(AppUser user){
			record.setProperty(SUBMITTER_ID, user.getID());
		}
		public AppUser getPerson() throws DataException{
			return (AppUser) getContext().getService(SessionService.class).getLoginFactory().find(record.getIntProperty(SUBMITTER_ID));
		}
		
		@Override
		public MessageResult submit() throws Exception {
			return new MessageResult("personal_response_created");
		}

		@Override
		public boolean validate() throws Exception {
			Class<? extends CompleteVisitor> clazz = getForm().getCompleteVisitor();
			CompleteVisitor<D, PersonalResponse<D>> vis = getContext().makeParamObject(clazz, this);
			return vis.visitForm(this.getForm());
		}
		
		@Override
		public MimeStreamData getPDFStream() throws DataException {
			return null;
		}
	}
	/** Direct construction of the response class. 
	 * 
	 * The parameter <b>form_maanger.<i>my-tag</i></b> needs to be set to the construction
	 * tag of the corresponding {@link DynamicFormManager} class. 
	 * 
	 * @param conn 
	 * @param tag 
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public PersonalResponseManager(AppContext conn, String tag) throws Exception {
		super(conn.makeContexedObject(DynamicFormManager.class, conn.getInitParameter(FORM_MANAGER_CONF_PREFIX+tag)),tag);
	}

	/** Standard constructor. Notm
	 * 
	 * @param manager
	 * @param tag
	 */
	public PersonalResponseManager(DynamicFormManager<D> manager, String tag) {
		super(manager, tag);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new PersonalResponse(this,res);
	}
	/** get/make a response for the specified form and the current user.
	 * This assumes only a single response per user and form.
	 * 
	 * @param form
	 * @return
	 * @throws DataException 
	 */
	public R getResponse(D form) throws DataException{
		SessionService<?> sess = getContext().getService(SessionService.class);
		SQLAndFilter<R> fil = new SQLAndFilter<R>(getTarget());
		fil.addFilter(getFormFilter(form));
		fil.addFilter(getMyResponsesFilter(sess));
		R result = find(fil,true);
		if( result == null ){
			result=makeBDO();
			result.setForm(form);
			result.setPerson((AppUser) sess.getCurrentPerson());
			result.commit();
		}
		return result;
	}

	public ReferenceFilter<R, AppUser> getMyResponsesFilter(SessionService<?> sess) {
		return new ReferenceFilter<R, AppUser>(this, SUBMITTER_ID, (AppUser) sess.getCurrentPerson());
	}

	
	
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = super.getDefaultTableSpecification(c, table);
		spec.setField(SUBMITTER_ID,new ReferenceFieldType(c.getService(SessionService.class).getLoginFactory().getTag()));
		return spec;
	}

	@Override
	public Class<? super R> getTarget() {
		return PersonalResponse.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryCreator#getTransitionProvider(java.lang.String)
	 */
	@Override
	public TransitionFactory getTransitionProvider(String tag) {
		if( tag == null ){
			return null;
		}
		if( tag.equals(PART_TAG)){
			return getPathResponseProvider();
		}else if ( tag.equals(RESPONSE_TAG)){
			return getPersonalResponseTransitionProvider();
		}
		return null;
	}

	/**
	 * @param tag
	 * @return
	 */
	public PersonalResponseTransitionProvider getPersonalResponseTransitionProvider() {
		return new PersonalResponseTransitionProvider(getTag()+TransitionFactoryCreator.TYPE_SEPERATOR+RESPONSE_TAG, this);
	}

	/**
	 * @param tag
	 * @return
	 */
	public ResponseTransitionProvider getPathResponseProvider() {
		return new ResponseTransitionProvider(getTag()+TransitionFactoryCreator.TYPE_SEPERATOR+PART_TAG, this);
	}

}