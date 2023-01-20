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
package uk.ac.ed.epcc.webapp.model.mail;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.editors.mail.MessageComposerFormResult;
import uk.ac.ed.epcc.webapp.editors.mail.MessageCreator;
import uk.ac.ed.epcc.webapp.editors.mail.MessageHandlerFactory;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** A simple implementaton of a {@link MessageHandlerFactory} to act as the target of
 * mail editing tests.
 * 
 * Messages can only be edited by the original creator. 
 * 
 * @author spb
 *
 * @param <H>
 */
public class TestingMessageHandlerFactory<H extends TestingMessageHandlerFactory.ExampleMessage> extends
		AbstractMessageHandlerFactory<H> implements MessageCreator{

	public static final String OWNER = "OwnerID";
	public TestingMessageHandlerFactory(AppContext conn,String table) {
		super(conn,table);
	}

	@Override
	public boolean canCreateMessage(SessionService op) {
		return true;
	}

	@Override
	public boolean createDirectly() {
		return true;
	}

	@Override
	public FormResult directCreate() throws Exception {
		H obj = makeBDO();
		SessionService service = getContext().getService(SessionService.class);
		obj.setOwner(service);
		obj.setStatus(COMPOSE);
		obj.commit();
		Composer comp = new Composer(obj);
		comp.repopulate(service);
		obj.commit();
		return new MessageComposerFormResult(getContext(), comp);
	}

	@Override
	public void buildMessageCreatorForm(Form f, SessionService operator) {
		
	}

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		
		TableSpecification spec = super.getDefaultTableSpecification(c, table);
		spec.setField(OWNER, new IntegerFieldType());
		return spec;
	}

	public static class ExampleMessage extends Provider{

		protected ExampleMessage(Record r) {
			super(r);
		}
		
		public void setOwner(SessionService sess){
			record.setProperty(OWNER, sess.getCurrentPerson().getID());
		}
		public boolean isOwner(SessionService sess){
			return record.getIntProperty(OWNER) == sess.getCurrentPerson().getID();
		}

		@Override
		public String[] getdefaultRecipients() {
			return new String[] { "fred@example.com" };
		}
		
	}

	@Override
	protected H makeBDO(Record res) throws DataFault {
		return (H) new ExampleMessage(res);
	}

	@Override
	public boolean canRead(
			H h,
			SessionService<?> user) {
		
		return h.isOwner(user);
	}

}