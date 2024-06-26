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
package uk.ac.ed.epcc.webapp.editors.mail;

import java.util.*;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.FileInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.forms.result.*;
import uk.ac.ed.epcc.webapp.forms.transition.*;
import uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer;
import uk.ac.ed.epcc.webapp.model.serv.SettableServeDataProducer;
import uk.ac.ed.epcc.webapp.session.SessionDataProducer;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.validation.MaxLengthValidator;
/** This is a transition based implementation of email editing.
 * 
 * The main view page is implemented as a view transition
 * or custom page with buttons/links going to direct transitions
 * on the message.
 * The hash value is included as an additional path element
 * (ok if you think of it as a version number)
 * The view transition shows an error message if the hash does not match
 * The other transitions and actions forward to the view transition if the hash does not match
 * To reduce the number of places this check is needed the hash is checked in {@link #getTransition(MailTarget, EditAction)} and a generic Action
 * is used for most operations.
 * The edit_part view needs to use a {@link CustomFormContent} transition to insert the form
 * into its correct place in the message view.
 * 
 * @author spb
 *
 */
public class EmailTransitionProvider implements ViewPathTransitionProvider<EditAction, MailTarget>, ShowDisabledTransitions<EditAction, MailTarget>{
	public static Feature SEND_NOW=new Feature("email.send_now", false, "Enable a send-now button for email text updates");
	public class TransitionLinker implements MessageEditLinker{
		private final AppContext conn;
		private final MailTarget target;
		public TransitionLinker(AppContext conn,MailTarget target){
			this.conn=conn;
			this.target=target;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageLinker#getMessageProvider()
		 */
		@Override
		public MessageProvider getMessageProvider() throws Exception {
			return target.getHandler().getMessageProvider();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageLinker#addLink(uk.ac.ed.epcc.webapp.content.ContentBuilder, java.util.List, java.lang.String, java.lang.String)
		 */
		@Override
		public void addLink(ContentBuilder builder, List<String> args,
				String file, String text) {
			MailTarget dest = new MailTarget(target.getHandler(), target.getMessageHash(), args);
			builder.addLink(conn, text, new ChainedTransitionResult<>(EmailTransitionProvider.this, dest, EditAction.Serve));
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageEditLinker#addButton(uk.ac.ed.epcc.webapp.content.ContentBuilder, uk.ac.ed.epcc.webapp.editors.mail.EditAction, java.util.List, java.lang.String)
		 */
		@Override
		public void addButton(ContentBuilder sb, EditAction action,
				List<String> path, String text) {
			MailTarget dest = new MailTarget(target.getHandler(), target.getMessageHash(), path);
			sb.addButton(conn, text, new ChainedTransitionResult<>(EmailTransitionProvider.this, dest, action));

			
		}
		
	}
	public abstract static class DirectMailTransition extends AbstractDirectTransition<MailTarget>{

	}
	public abstract static class FormMailTransition extends AbstractFormTransition<MailTarget>{

	}
	public abstract class FormContentMailTransition extends FormMailTransition implements CustomFormContent<MailTarget>{

		@Override
		public <X extends ContentBuilder> X addFormContent(X cb,
				SessionService<?> op, Form f, MailTarget target) {
			AppContext conn = op.getContext();
			FormContentEditMessageVisitor vis = new FormContentEditMessageVisitor(conn, cb,DATA_FORM_FIELD, f, new TransitionLinker(conn, target));
			MessageWalker mw = new MessageWalker(conn);
			try{
				mw.setTarget(target.getPath(), true);
				mw.visitMessage(target.getHandler().getMessageProvider().getMessage(),vis);
			}catch(Exception e){
				getLogger().error("Error building content",e);
			}
			return cb;
		}
		
	}
	public class SendTransition extends DirectMailTransition{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(MailTarget target, AppContext c)
				throws TransitionException {
			try {
				return ((MessageComposer)target.getHandler()).send(c.getService(SessionService.class));
			} catch (Exception e) {
				getLogger().error("Error sending message",e);
				throw new TransitionException("Send failed");
			}
		}
		
	}
	public class AbortTransition extends DirectMailTransition{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(MailTarget target, AppContext c)
				throws TransitionException {
			try {
				return ((MessageComposer)target.getHandler()).abort();
			} catch (Exception e) {
				getLogger().error("Error aborting message",e);
				throw new TransitionException("Abort failed");
			}
		}
		
	}
	public class StartOverTransition extends DirectMailTransition{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(MailTarget target, AppContext c)
				throws TransitionException {
			try {
				MessageComposer comp = (MessageComposer)target.getHandler();
				comp.repopulate(c.getService(SessionService.class));
				target = new MailTarget(comp, comp.getMessageProvider().getMessageHash(), null);
				return new ViewTransitionResult<>(EmailTransitionProvider.this, target);
			} catch (Exception e) {
				getLogger().error("Error reverting message",e);
				throw new TransitionException("StartOver failed");
			}
		}
		
	}
	/** Form Field name used for data in edits.
	 * 
	 */
	private static final String DATA_FORM_FIELD = "text";
	/** Generic action to apply an EditAction (parameterised by a single piece of additional data) 
	 * to a location in a message. 
	 * 
	 * @author spb
	 *
	 */
	public class EditFormAction extends FormAction{
		public EditFormAction(MailTarget target, EditAction action) {
			super();
			this.target = target;
			this.action = action;
		}
		private final MailTarget target;
		private final EditAction action;
		private boolean auto_send=false;
		
		/** Set if the message should be automatically sent after this action.
		 * 
		 * @param val
		 */
		public void setAutoSend(boolean val){
			auto_send=val;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
		 */
		@Override
		public FormResult action(Form f) throws ActionException {
			try{
				MailTarget dest = target;
				if( target.hashMatches()){
					ActionMessageVisitor amv = new ActionMessageVisitor(getContext(),action,f.get(DATA_FORM_FIELD));
					MessageWalker mw = new MessageWalker(getContext());
					MessageProvider mp = target.getHandler().getMessageProvider();
					MimeMessage m = mp.getMessage();
					// MessageEditor actions
					mw.setTarget(target.getPath(), false);
					mw.visitMessage(m, amv);
					if( amv.modified() ){
						m.saveChanges();
						mp.setMessage(m);
						mp.commit();
						dest = new MailTarget(target.getHandler(), mp.getMessageHash(), null);
					}else{
						dest = new MailTarget(target.getHandler(), target.getMessageHash(), null);
					}
				}
				if( auto_send ){
					return ((MessageComposer)dest.getHandler()).send(c.getService(SessionService.class));
				}
				return new ViewTransitionResult<>(EmailTransitionProvider.this, dest);
			}catch(Exception e){
				throw new ActionException("Invalid input", e);
			}
		}
	}
	public class AddRecipientTransition extends FormMailTransition{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.FormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, MailTarget target, AppContext conn)
				throws TransitionException {
			MessageComposer composer =null;
			MessageProvider messageProvider = null;
			try {
				composer = (MessageComposer) target.getHandler();
				messageProvider= composer.getMessageProvider();
			} catch (Exception e) {
				getLogger().error("Error getting messageProvider", e);
			}
			if( messageProvider != null &&  ! composer.editRecipients() ) {
				throw new TransitionException("Editing recipients not allowed");
			}
			f.addInput(DATA_FORM_FIELD, "Recipient Email", ((MessageComposer)target.getHandler()).getEmailInput());
			if( messageProvider == null || ! composer.bccOnly()) {
				f.addAction(EditAction.AddCC.toString(), new EditFormAction(target, EditAction.AddCC));
				if( ! composer.editCCOnly()) {
					f.addAction(EditAction.AddTo.toString(), new EditFormAction(target, EditAction.AddTo));
				}
			}
			f.addAction(EditAction.AddBcc.toString(), new EditFormAction(target, EditAction.AddBcc));
		}
		
	}
	public class AddReplyToTransition extends FormMailTransition{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.FormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, MailTarget target, AppContext conn)
				throws TransitionException {
			MessageComposer composer =null;
			MessageProvider messageProvider = null;
			try {
				composer = (MessageComposer) target.getHandler();
				messageProvider= composer.getMessageProvider();
			} catch (Exception e) {
				getLogger().error("Error getting messageProvider", e);
			}
			if( messageProvider != null &&  ! composer.editReplyTo() ) {
				throw new TransitionException("Editing reply-to not allowed");
			}
			f.addInput(DATA_FORM_FIELD, "Reply Email", ((MessageComposer)target.getHandler()).getEmailInput());
			f.addAction(EditAction.AddReplyTo.toString(), new EditFormAction(target, EditAction.AddReplyTo));
		}
		
	}
	public class DirectOperationTransition extends DirectMailTransition{
		public DirectOperationTransition(EditAction action) {
			super();
			this.action = action;
		}

		private final EditAction action;

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(MailTarget target, AppContext c)
				throws TransitionException {
			try{
				if( target.hashMatches()){
					ActionMessageVisitor amv = new ActionMessageVisitor(getContext(),action,null);
					MessageWalker mw = new MessageWalker(getContext());
					MessageProvider mp = target.getHandler().getMessageProvider();
					MimeMessage m = mp.getMessage();
					// MessageEditor actions
					mw.setTarget(target.getPath(), false);
					mw.visitMessage(m, amv);
					if( amv.modified() ){
						m.saveChanges();
						mp.setMessage(m);
						mp.commit();
						target = new MailTarget(target.getHandler(), mp.getMessageHash(), null);
					}else{
						target = new MailTarget(target.getHandler(), target.getMessageHash(), null);
					}
				}
				return new ViewTransitionResult<>(EmailTransitionProvider.this, target);
			}catch(Exception e){
				getLogger().error("Error in direct edit",e);
				throw new TransitionException("Internal error");
			}
		}
	}
	public class AddAttachmentTransition extends FormMailTransition{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.FormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, MailTarget target, AppContext conn)
				throws TransitionException {
			f.addInput(DATA_FORM_FIELD, "Attach file", new FileInput());
			f.addAction(EditAction.Upload.toString(), new EditFormAction(target, EditAction.Upload));
		}
		
	}
	public class EditSubjectTransition extends FormContentMailTransition{

		/**
		 * 
		 */
		private static final int SUBJECT_MAX_WIDTH = 255;
		/**
		 * 
		 */
		private static final int SUBJECT_BOX_WIDTH = 64;

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.FormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, MailTarget target, AppContext conn)
				throws TransitionException {
			TextInput input = new TextInput();
			input.setBoxWidth(SUBJECT_BOX_WIDTH);
			input.addValidator(new MaxLengthValidator(SUBJECT_MAX_WIDTH));
			input.setSingle(true);
			f.addInput(DATA_FORM_FIELD, "Subject", input);
			f.addAction(EditAction.Update.toString(), new EditFormAction(target, EditAction.Update));
		}
		
	}
	public class EditTextTransition extends FormContentMailTransition{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.FormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, MailTarget target, AppContext conn)
				throws TransitionException {
			TextInput input = new TextInput();
			input.setBoxWidth(80);
			input.addValidator(new MaxLengthValidator(81920));
			input.setSingle(false);
			f.addInput(DATA_FORM_FIELD, "Text", input);
			f.addAction(EditAction.Update.toString(), new EditFormAction(target, EditAction.Update));
			if( SEND_NOW.isEnabled(conn)){
				EditFormAction send = new EditFormAction(target, EditAction.Update);
				send.setAutoSend(true);
				f.addAction("Send Now", send);
			}
		}
		
	}
	public class ViewDirectTransition extends DirectMailTransition{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(MailTarget target, AppContext c)
				throws TransitionException {
			return new ViewTransitionResult<>(EmailTransitionProvider.this,target);
		}
		
	}
	public class CreateTransition implements DirectTargetlessTransition<MailTarget>, TargetLessTransition<MailTarget>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.Transition#getResult(uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor)
		 */
		@Override
		public FormResult getResult(TransitionVisitor<MailTarget> vis)
				throws TransitionException {
			MessageCreator creator = (MessageCreator) messageFactory;
			if( creator.createDirectly()){
				return vis.doDirectTargetlessTransition(this);
			}else{
				return vis.doTargetLessTransition(this);
			}
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, AppContext c) throws TransitionException {
			MessageCreator creator = (MessageCreator) messageFactory;
			SessionService<?> operator = c.getService(SessionService.class);
			creator.buildMessageCreatorForm(f, operator);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition( AppContext c)
				throws TransitionException {
			MessageCreator creator = (MessageCreator) messageFactory;
			try {
				return creator.directCreate();
			} catch (Exception e) {
				getLogger().error("Error creating message",e);
				throw new TransitionException("Internal error");
			}
		}
	}
	private final AppContext c;
	private final MessageHandlerFactory messageFactory;
	public EmailTransitionProvider(MessageHandlerFactory fac) {
		this.messageFactory=fac;
		this.c=fac.getContext();
	}

	
	@Override
	public boolean canView(MailTarget target, SessionService<?> sess) {
		if( target == null){
			return false;
		}
		MessageHandler hand = target.getHandler();
		List<String> path = target.getPath();
		return hand.canView(path, sess);
	}

	
	@Override
	public String getTargetName() {
		return "Email"+TransitionFactoryCreator.TYPE_SEPERATOR+messageFactory.getTag();
	}


	
	@Override
	public <X extends ContentBuilder> X getSummaryContent(AppContext c, X cb,
			MailTarget target) {
		if(target == null) {
			return cb;
		}
		try {
			return target.getHandler().getMessageProvider().addMessageContext(cb);
		} catch (Exception e) {
			getLogger().error("Error adding message context",e);
			return cb;
		}
	}
	
	@Override
	public MailTarget getTarget(LinkedList<String> path) {
		
		if( path.size() < 2 ){
			return null;
		}
		MessageHandler mh=messageFactory.getHandler(path, c.getService(SessionService.class));
		if( mh == null ){
			return null;
		}
		path = new LinkedList<String>(path);
		for(String s : mh.getPath()) {
			String part = path.pop();
			if( ! part.equals(s)) {
				getLogger().error("Unexpected path part "+part+"!="+s);
				return null;
			}
		}
		String hash_str=path.pop();
		
		int hash = 0;
		try{
			hash = Integer.parseInt(hash_str);
		}catch(NumberFormatException e){
			return null;
		}
		
		return new MailTarget(mh, hash,path);
	}

	
	@Override
	public LinkedList<String> getID(MailTarget target) {
		LinkedList<String> result = new LinkedList<>();
		MessageHandler handler = target.getHandler();
		result.addAll(handler.getPath());
		result.add(Integer.toString(target.getMessageHash()));
		result.addAll(target.getPath());
		return result;
	}

	
	@Override
	public boolean allowTransition(AppContext c, MailTarget target, EditAction key) {
		SessionService<?> operator = c.getService(SessionService.class);
		if( operator == null || ! operator.haveCurrentUser()){
			return false;
		}
		if( key == EditAction.New){
			// should be targetless
			if( messageFactory instanceof MessageCreator){
				return((MessageCreator)messageFactory).canCreateMessage(operator);
			}else{
				return false;
			}
		}else if( target == null){
			// No targetless transitions left.
			return false;
		}
		if( key == EditAction.Serve){
			return target.canView(operator);
		}
		if( ! target.canEdit(operator)) {
			return false;
		}
		if( key == EditAction.Send) {
			
			try {
				MessageProvider prov = target.getHandler().getMessageProvider();
				if( ! prov.canSend()){
					return false;
				}
			} catch (Exception e) {
				getLogger().error("Error checking sendable",e);
				return false;
			}
		}
		return true;
	}

	
	@Override
	public <X extends ContentBuilder> X getTopContent(X cb, MailTarget target,
			SessionService<?> sess) {
		// Add additional send buttons to the top content of the view page if composer
		MessageHandler handler = target.getHandler();
		if( handler instanceof MessageComposer && ((MessageComposer)handler).canEdit(target.getPath(), sess)){
			for(EditAction action : getTransitions(target)){
				if( allowTransition(getContext(),target,action)) {
					ContentBuilder div = cb.getPanel("bar");
					div.addHeading(2,action.getHelp());
					div.addButton(getContext(), action.toString(), new ChainedTransitionResult<>(EmailTransitionProvider.this, target, action));
					div.addParent();
				}
			}
		}
		return cb;
	}

	
	@Override
	public <X extends ContentBuilder> X getLogContent(X cb, MailTarget target,
			SessionService<?> sess) {
		try {
			MessageHandler handler = target.getHandler();
			if( handler instanceof MessageComposer){
				if( target != null ) {
					long max_email = c.getLongParameter("email.max_length", -1);
					if( max_email > 0L ) {
						long len = target.getMessageLength();
						if( len > max_email) {
							cb.getHeading(2).getSpan("warn").clean("Message is too long and may not send").appendParent().appendParent();
						}
					}
				}
				if( target.hashMatches()){
					MessageComposerFormat mcf = new MessageComposerFormat(getContext(), (MessageComposer) handler, new TransitionLinker(getContext(), target));
					mcf.getContent(cb);
				}else{
					ContentBuilder heading = cb.getHeading(2);
					heading.addText("Email changed");
					heading.addParent();
					cb.addText("This email has been changed while you were editing.");
				}
				
				
			}else{
				MessageHandlerFormat mhf = new MessageHandlerFormat(getContext(), handler, new TransitionLinker(getContext(), target));
				mhf.getContent(cb);
			}
		} catch (Exception e) {
			getLogger().error("Error generating Email logContent",e);
			cb.addText("Internal error");
		}
		return cb;
		
	}

	
	@Override
	public String getHelp(EditAction key) {
		return key.getHelp();
	}
	@Override
	public String getText(EditAction key){
		return key.toString();
	}
	
	@Override
	public Set<EditAction> getTransitions(MailTarget target) {
		if( target != null ){
			try {
				if( target.hashMatches()){
					return EnumSet.of(EditAction.Send,EditAction.Abort,EditAction.StartOver);
				}
			} catch (Exception e) {
				getLogger().error("Error checking match",e);
			}
		}
		return EnumSet.noneOf(EditAction.class);
	}
	public class ServeTransition extends DirectMailTransition{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(MailTarget target, AppContext c)
				throws TransitionException {
			try{
				SettableServeDataProducer producer = c.makeObjectWithDefault(SettableServeDataProducer.class,SessionDataProducer.class,ServeDataProducer.DEFAULT_SERVE_DATA_TAG );
				if( producer == null){
					throw new TransitionException("No ServeDataProducer configured");
				}
				MimeStreamDataVisitor vis = new MimeStreamDataVisitor(c);
				MessageWalker mw = new MessageWalker(getContext());
				MessageProvider mp = target.getHandler().getMessageProvider();
				MimeMessage m = mp.getMessage();
				// MessageEditor actions
				mw.setTarget(target.getPath(), false);
				mw.visitMessage(m, vis);
				return new ServeDataResult(producer, producer.setData(vis.getData()));
			}catch(Exception e){
				getLogger().error("Error making download",e);
				throw new TransitionException("Internal error");
			}
		}
		
	}
	
	@Override
	public Transition<MailTarget> getTransition(MailTarget target, EditAction key) {
		try{
			if( target != null){
				if( target.hashMatches()){
					switch(key){
					case Send: return new SendTransition();
					case Abort: return new AbortTransition();
					case StartOver: return new StartOverTransition();
					case AddRecipient: return new AddRecipientTransition();
					case AddReplyTo: return new AddReplyToTransition();
					case AddAttachment: return new AddAttachmentTransition(); 
					case Edit: return new EditTextTransition();
					case EditSubject: return new EditSubjectTransition();
					case Serve: return new ServeTransition();
					default: return new DirectOperationTransition(key);
					}
				}else{
					return new ViewDirectTransition();
				}
			}else{
				if(key == EditAction.New){
					return new CreateTransition();
				}
				return null;
			}
		}catch(Exception e){
			getLogger().error("Error checking hash",e);
			return null;
		}
	}

	
	@Override
	public EditAction lookupTransition(MailTarget target, String name) {
		if( name == null ){
			return null;
		}
		try{
			return EditAction.valueOf(name);
		}catch(Exception t){
			return null;
		}
	}

	
	@Override
	public AppContext getContext() {
		return c;
	}


	@Override
	public <R> R accept(TransitionFactoryVisitor<R,MailTarget, EditAction> vis) {
		return vis.visitPathTransitionProvider(this);
	}
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#getBottomContent(uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public <X extends ContentBuilder> X getBottomContent(X cb, MailTarget target, SessionService<?> sess) {
		return cb;
	}


	@Override
	public boolean showDisabledTransition(AppContext c, MailTarget target, EditAction key) {
		if( key != null && key.equals(EditAction.Send) && target.canEdit(c.getService(SessionService.class))) {
			return true;
		}
		return false;
	}

    public static boolean hasRecipient(Message m) throws MessagingException {
    	Address[] recip = m.getAllRecipients();
    	if( recip == null || recip.length==0) {
    		return false;
    	}
    	if( recip.length == 1 && recip[0].equals(new InternetAddress("undisclosed-recipients:;"))) {
    		// no real recipients for a mailing list
    		return false;
    	}
    	return true;
    }
	

}