//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
/*
 * Created on 27-Apr-2005 by spb
 *
 */
package uk.ac.ed.epcc.webapp.email;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import jakarta.mail.*;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.internet.*;
import uk.ac.ed.epcc.webapp.*;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.content.*;
import uk.ac.ed.epcc.webapp.email.QueuedMessages.QueuedMessage;
import uk.ac.ed.epcc.webapp.email.logging.EmailLogger;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
//import uk.ac.ed.epcc.webapp.logging.email.EmailLogger;
import uk.ac.ed.epcc.webapp.model.TemplateFinder;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayStreamData;
import uk.ac.ed.epcc.webapp.resource.ResourceService;
import uk.ac.ed.epcc.webapp.session.*;
import uk.ac.ed.epcc.webapp.session.EmailChangeRequestFactory.EmailChangeRequest;
import uk.ac.ed.epcc.webapp.session.PasswordChangeRequestFactory.PasswordChangeRequest;
import uk.ac.ed.epcc.webapp.timer.TimeClosable;

/**
 * Emailer Class that sends emails.
 * 
 * all emails sent by the Web-app are funneled through this class so we can
 * enforce debugging modes that allow us to debug with a copy of the main
 * database without sending spurious emails to users. All dependencies on
 * jakarta.mail are also routed through this class to reduce dependencies.
 * 
 * We could implement encrypted emails or crytographic signing here if we
 * wanted.
 * 
 * @author spb
 * 
 */


public class Emailer implements Contexed{
	/** Config property to set an password for authenticated sends
	 * 
	 */
	private static final String MAIL_SMTP_PASSWORD = "mail.smtp.password";
	/** Config property to set the username for authenticated sends.
	 * 
	 */
	private static final String MAIL_SMTP_USER = "mail.smtp.user";
	/**
	 * 
	 */
	public static final String DEFAULT_HEADER_PREFIX = "X-Saf-";
	/**
	 * 
	 */
	private static final String MAIL_SIGNER = "MailSigner";
	/** Default string encoding to use.
	 * 
	 */
	public static final String DEFAULT_ENCODING = "UTF-8";
	/** Name of an AppContext attribute to supress emails from the current request.
	 * 
	 */
	public static final String SUPRESS_EMAIL_ATTR = "SUPRESS_EMAIL_ATTR";
	/**
	 * 
	 */
	private static final String MAIL_TRANSPORTS_PREFIX = "mail_transports.";
	/**
	 * 
	 */
	public static final String EMAIL_FROM_NAME = "email.from_name";
	/**
	 * 
	 */
	public static final String EMAIL_FROM_ADDRESS = "email.from_address";
	public static final String EMAIL_FORCE_ADDRESS = "email.force.address";
	public static final String EMAIL_BYPASS_FORCE_ADDRESS = "email.bypass_force.address";
	public static final Feature EMAILS_FEATURE = new Feature("emails",true,"emails enabled");
	public static final Feature EMAIL_FORCE_QUEUE_FEATURE = new Feature("emails.force_queue",false,"All emails are queued to the database first");
	public static final Feature EMAIL_QUEUE_FAILS_FEATURE = new Feature("emails.queue_fails",true,"Failed sends are queued to the database first");

	public static final Feature PASSWORD_RESET_SERVLET = new Feature("password_reset.servlet",false,"Send reset url in reset email");
	public static final Feature EMAIL_DEFERRED_SEND = new Feature("email.deferred_send",true,"Use cleanup service to defer send till end of transaction");
    public static final Feature HTML_ALTERNATIVE = new Feature("email.html_alternative",true,"Look for a tempalte region conatining html alternative content");
	private static final String MAIL_SMTP_HOST = "mail.smtp.host";

	private static final String ERROR_EMAIL_FROM_ADDRESS = "error.email_from_address";

	private static final String ERROR_EMAIL_NOTIFY_ADDRESS = "error.email_notify_address";

	// time last report sent in seconds
	private static long last_send = 0;

	// number of messages sent since last pause
	private static int send_count = 0;

	private static final long MAX_REPORT_FREQUENCY = 100;

	private static final int MAX_REPORTS = 10;

	public static final Feature DEBUG_SEND = new Feature("email.send.debug", false, "Log send internal operations");
	AppContext ctx;
	private final Pattern dont_send_pattern;
	private TemplateFinder finder=null;
	
	public static Emailer getFactory(AppContext conn) {
		return conn.makeObject(Emailer.class, "mailer");
	}
	
	public Emailer(AppContext c) {
		ctx = c;
		Pattern tmp = null;
		try{
			String pattern_text = c.getInitParameter("email.dont_send_pattern");
			if( pattern_text != null){
				tmp = Pattern.compile(pattern_text);
			}
		}catch(Exception t){
			getLogger().error("Error making dont_send_pattern", t);
		}
		dont_send_pattern=tmp;
	}

	public AppContext getContext() {
		return ctx;
	}

	/**
	 * Send an email with the person new password
	 * 
	 */
	public void newPassword(AppUser person, PasswordAuthComposite comp)
			throws Exception {

		TemplateFile email_template = getFinder().getTemplateFile("request_password.txt");

		if( email_template == null ){
			if( ! PASSWORD_RESET_SERVLET.isEnabled(ctx)) {
				getLogger().debug(comp.randomisePassword(person));
				person.commit();
				return;
			}
		}
		email_template.setProperty("person.name", person.getName());
		
		String email = person.getEmail();
		if( email == null){
			getLogger().error("New password for user with null email "+person.getIdentifier());;
			return;
		}
		email_template.setProperty("person.email", email);
		email_template.setProperty("person.loginnames", person.getFactory().getNames(person));
		if( PASSWORD_RESET_SERVLET.isEnabled(ctx)){
			PasswordChangeRequestFactory fac = new PasswordChangeRequestFactory(ctx.getService(SessionService.class).getLoginFactory());
			PasswordChangeRequest request = fac.createRequest(person);
			if( request != null ){
				email_template.setRegionEnabled("password_reset.url_region", true);
				email_template.setRegionEnabled("password_value.region", false);
				email_template.setProperty("password_reset.tag", request.getTag());
			}
		}else {
			email_template.setProperty("person.password", comp.randomisePassword(person));
			person.commit();
		}
		
		doSend(templateMessage(person,getFrom(person),email_template));
		try{
			PasswordChangeListener listener = ctx.makeObjectWithDefault(PasswordChangeListener.class,null, PasswordChangeListener.PASSWORD_LISTENER_PROP);
			if( listener != null ){
				listener.passwordInvalid(person);
			}
		}catch(Exception t){
			getLogger().error("Error calling PasswordChangeListener", t);
		}
	}

	/** Send an email requesting confirmation of a new Email address.
	 * 
	 * @param person
	 * @param req
	 * @throws Exception 
	 */
	public void newEmailRequest(AppUser person, EmailChangeRequest req)
	throws Exception {
        boolean verify_only = person.getEmail().equalsIgnoreCase(req.getEmail());
		String name = "new_email.txt";
		if( verify_only ) {
			name = "verify_email.txt";
		}
		TemplateFile email_template = getFinder().getTemplateFile(name);

		email_template.setProperty("person.name", person.getName());
		email_template.setProperty("person.email", person.getEmail());
		email_template.setProperty("request.email", req.getEmail());
		email_template.setProperty("request.tag", req.getTag());
		email_template.setProperty("person.loginnames", person.getFactory().getNames(person));

		doSend(templateMessage(req.getEmail(), null, email_template));

	}
	public void newRemoteHostLogin(AppUser person, String host) throws Exception {
		TemplateFile email_template = getFinder().getTemplateFile("new_login_location.txt");
		CurrentTimeService time = getContext().getService(CurrentTimeService.class);
		email_template.setProperty("person.name", person.getName());
		email_template.setProperty("person.email", person.getEmail());
		email_template.setProperty("login.location", host);
		email_template.setProperty("login.time", time.getCurrentTime());
		AppUserFactory<?> fac = person.getFactory();
		PasswordAuthComposite comp = fac.getComposite(PasswordAuthComposite.class);
		
		email_template.setRegionEnabled("ChangePassword", comp != null && comp.canResetPassword(person));
		
		doSend(templateMessage(person, null,null,null, email_template));
	}
	/**
	 * Notify user their password has been changed.
	 * 
	 * 
	 * 
	 */
	public void passwordChanged(AppUser person)
			throws Exception {

		TemplateFile email_template = getFinder().getTemplateFile("password_changed.txt");
		

		String name = person.getName();
		if( name == null || name.trim().length() == 0){
			name = "User";
		}
		email_template.setProperty("person.name", name);
		String email = person.getEmail();
		if( email == null){
			getLogger().error("Password change email destination not known "+person.getIdentifier());;
			return;
		}
		email_template.setProperty("person.email", email);
		email_template.setProperty("person.loginnames", person.getFactory().getNames(person));
		doSend(templateMessage(person,getFrom(person),email_template));

	}
	/**
	 * Notify user their password has been locked-out.
	 * 
	 * 
	 * 
	 */
	public void passwordFailsExceeded(AppUser person)
			throws Exception {

		TemplateFile email_template = getFinder().getTemplateFile("password_fails_exceeded.txt");
		

		String name = person.getName();
		if( name == null || name.trim().length() == 0){
			name = "User";
		}
		email_template.setProperty("person.name", name);
		String email = person.getEmail();
		if( email == null){
			getLogger().error("Password fails email destination not known "+person.getIdentifier());
			return;
		}
		email_template.setProperty("person.email", email);
		doSend(templateMessage(person,getFrom(person),email_template));

	}
	
	/**
	 * Generic norification email where the only customisation
	 * is based on the recipient
	 * 
	 * 
	 * 
	 */
	public void userNotification(AppUser person,String template)
			throws Exception {

		TemplateFile email_template = getFinder().getTemplateFile(template);
		

		String name = person.getName();
		if( name == null || name.trim().length() == 0){
			name = "User";
		}
		email_template.setProperty("person.name", name);
		String email = person.getEmail();
		if( email == null){
			getLogger().error("Notification email destination not known "+person.getIdentifier());
			return;
		}
		email_template.setProperty("person.email", email);
		doSend(templateMessage(person,getFrom(person),email_template));

	}
	
	public void newSignup(AppUser person, String new_password)
			throws Exception {

		TemplateFile email_template = getFinder().getTemplateFile("new_signup.txt");
		if( email_template == null){
			getLogger().debug(new_password);
			return;
		}

		String name = person.getName();
		if( name == null || name.trim().length() == 0){
			name = "User";
		}
		email_template.setProperty("person.name", name);
		email_template.setProperty("person.password", new_password);
		String email = person.getEmail();
		if( email == null){
			getLogger().error("Signup email destination not known "+person.getIdentifier());
			return;
		}
		email_template.setProperty("person.email", email);
		if( PASSWORD_RESET_SERVLET.isEnabled(ctx)){
			PasswordChangeRequestFactory fac = new PasswordChangeRequestFactory(ctx.getService(SessionService.class).getLoginFactory());
			PasswordChangeRequest request = fac.createRequest(person);
			if( request != null ){
				email_template.setRegionEnabled("password_reset.url_region", true);
				email_template.setRegionEnabled("password_value.region", false);
				email_template.setProperty("password_reset.tag", request.getTag());
			}
			// The user needs the password info so we want to see any send exceptions
			doSendNow(templateMessage(person,getFrom(person),email_template));
		}else {
			doSend(templateMessage(person,getFrom(person),email_template));
		}

	}
	public void notificationEmail(AppUser person, Set<String> notices,Set<String> actions) throws Exception {
		MimeMessage m = notificationMessage(person, notices,actions);
		
		
		if( m != null) {
			doSend(m);
		}
	}

	public MimeMessage notificationMessage(AppUser person, Set<String> notices,Set<String> actions) throws Exception {
		TemplateFile email_template = getFinder().getTemplateFile("update_notices.txt");
		String name = person.getName();
		if( name == null || name.trim().length() == 0){
			name = "User";
		}
		email_template.setProperty("person.name", name);
		StringBuilder text = new StringBuilder();
		boolean seen=false;
		for(String s : notices) {
			if( seen ) {
				text.append("\n");
			}
			text.append("* ");
			text.append(s);
			seen=true;
		}
		email_template.setProperty("person.notices", text.toString());
		if( actions != null && ! actions.isEmpty()) {
			StringBuilder action_text = new StringBuilder();
			seen=false;
			for(String s: actions) {
				if( seen ) {
					action_text.append("\n");
				}
				action_text.append("* ");
				action_text.append(s);
				seen=true;
			}
			email_template.setProperty("person.actions", action_text.toString());
			email_template.setRegionEnabled("automatic_actions", true);
		}
		String email = person.getEmail();
		if( email == null){
			getLogger().error("Notification email destination not known "+person.getIdentifier());;
			return null;
		}
		email_template.setProperty("person.email", email);
		AppUserFactory<AppUser> factory = person.getFactory();
		email_template.setProperty("person.loginnames", factory.getNames(person));

		Set<String> verifications = new LinkedHashSet<>();
		if( factory instanceof VerificationProvider) {
			((VerificationProvider)factory).addVerifications(verifications,person);
		}
		for(VerificationProvider comp : factory.getComposites(VerificationProvider.class)) {
			comp.addVerifications(verifications,person);
		}
		if( ! verifications.isEmpty()) {
			StringBuilder v_text = new StringBuilder();
			boolean v_seen=false;
			for(String s : verifications) {
				if( v_seen ) {
					v_text.append("\n");
				}
				v_text.append("* ");
				v_text.append(s);
				v_seen=true;
			}
			email_template.setRegionEnabled("Verification", true);
			email_template.setProperty("person.verifications", v_text.toString());
			
		}
		String docs = getContext().getExpandedProperty("service.documentation");
		if( docs != null && ! docs.trim().isEmpty()) {
			email_template.setProperty("update.documentation",docs);
			email_template.setRegionEnabled("Documentation", true);
		}
		MimeMessage m = templateMessage(person,getFrom(person),email_template);
		return m;
	}
	public MimeMessage templateMessage(String sendto, Hashtable h,
			TemplateFile email_template) throws IOException, MessagingException, InvalidArgument {
		Logger log = getLogger();
		// change destination depending on sendto:
		String email = mapRecipients(sendto);
		
		log.info("EmailSender sending an email to " + email);
		return templateMessage(new String[] { email }, h, email_template);
	}
	public MimeMessage templateMessage(String sendto, InternetAddress from,Hashtable h,
			TemplateFile email_template,Map<String,String> params) throws IOException, MessagingException, InvalidArgument {
		Logger log = getLogger();
		// change destination depending on sendto:
		String email = mapRecipients(sendto);
		
		log.info("EmailSender sending an email to " + email);
		return templateMessage(new String[] { email }, DEFAULT_HEADER_PREFIX,h,from,true,false, email_template,params);
	}
    public String mapRecipients(String sendto){
    	return sendto;
    }
	public String getEmail(AppUser recipient){
		if( recipient.allowEmail()){
			return recipient.getEmail();
		}
		return getContext().getInitParameter("disabled_email.map_address");
	}
	/**
	 * Send an email based on a template file to a {@link AppUser}
	 *  
	 */
	public MimeMessage templateMessage(AppUser recipient,InternetAddress from, TemplateFile email_template)
			throws IOException, MessagingException, InvalidArgument {
		return templateMessage(recipient, from,null, null,email_template);
	}
	
	public MimeMessage templateMessage(AppUser recipient,InternetAddress from, Hashtable headers,Map<String,String> params,TemplateFile email_template)
			throws IOException, MessagingException, InvalidArgument {
	Logger log = getLogger();
	// might return null if emails supressed
	String email = getEmail(recipient);
	if( email != null ) {
		log.debug("Email mapped "+recipient.getEmail()+"->"+email);
	}
	if( email != null && email.trim().length() > 0){
		if( params == null ) {
			params = new HashMap<>();
		}
		if( recipient != null) {
			addParams(params, recipient);
			AppUserFactory<?> factory = recipient.getFactory();
			for(EmailParamContributor epc : factory.getComposites(EmailParamContributor.class)) {
				epc.addParams(params, recipient);
				epc.setRegions(email_template, recipient);
			}
		}
		return templateMessage(email, from,headers, email_template,params);
	}else{
		log.warn("Email to "+recipient.getIdentifier()+" mapped to null/empty");
	}
	return null;
}
	public boolean supressSend(Address a){
		if( dont_send_pattern != null && a instanceof InternetAddress){
			InternetAddress ia = (InternetAddress) a;
			if( dont_send_pattern.matcher(ia.getAddress()).matches()  ){
				return true;
			}
		}
		return false;
	}
	public static class SendAction implements Runnable{
		/**
		 * @param m
		 */
		public SendAction(Emailer es,MimeMessage m) {
			super();
			this.es=es;
			this.m = m;
		}
		private final Emailer es;
		private final MimeMessage m;
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				postSend(es.send(m));
			} catch (Exception e) {
				es.getLogger().error("Error sending message", e);
			}
			
		}
		/** Extension point to allow sub-classes to observe the sent email
		 * 
		 * @param m
		 */
		public void postSend(MimeMessage m) {
			
		}
	}
	/** Send the message
	 * This might use the {@link CleanupService} to send at the end of
	 *  a transaction
	 * @param m
	 * @throws MessagingException
	 * @throws DataFault 
	 */
	public void doSend(MimeMessage m) throws MessagingException, DataFault{
		if( EMAIL_DEFERRED_SEND.isEnabled(getContext()) && getContext().getService(DatabaseService.class).inTransaction()) {
		   CleanupService cleanup = getContext().getService(CleanupService.class);
		   if( cleanup != null) {
			   SendAction a = new SendAction(this,m);
			   cleanup.add(a);
			   DatabaseService db = getContext().getService(DatabaseService.class);
			   if( db != null) {
				   // If we are in a transaction cancel this if transaction is rolled back
				   db.addCleanup(a);
			   }
			   return;
		   }
		}
		doSendNow(m);
	}
	/** Send the message now.
	 * 
	 * The sent message is returned so it can be logged.
	 * 
	 * @param m
	 * @return
	 * @throws MessagingException
	 * @throws DataFault 
	 */
	public MimeMessage doSendNow(MimeMessage m) throws MessagingException, DataFault{
		if( m == null ){
			return null;
		}
		
		
		m = send(m);
		return m;
	}

	/** Re-try a queued message. 
	 * If it is sent successfully then the queued message will be deleted
	 * 
	 * @param qm
	 * @throws DataFault 
	 */
	public void retry(QueuedMessage qm) throws DataFault {
		if( ! EMAILS_FEATURE.isEnabled(getContext())) {
			return;
		}
		String force = getContext().getInitParameter(EMAIL_FORCE_ADDRESS);
		if( force != null ) {
			return;
		}
		try {
			MimeMessage m = qm.getMessage();
			Transport.send(m);
			qm.delete();
			return;
		}catch(SendFailedException me) {
			getLogger().error("Send fail of queued message",me);
			qm.delete();
		}catch(MessagingException me) {
			getLogger().warn("Email retry failed", me);
		}
		qm.recordRetry();
		
	}
	/** Actually send the {@link MimeMessage}
	 * @param m {@link MimeMessage}
	 * 
	 * Note this will return null if message sending is disabled.

	 * @return sent {@link MimeMessage}
	 * @throws MessagingException
	 * @throws AddressException
	 * @throws DataFault 
	 */
	private MimeMessage send(MimeMessage m) throws MessagingException, AddressException, DataFault {
		if( m == null ){
			return null;
		}
		AppContext conn = getContext();
		DatabaseService db = conn.getService(DatabaseService.class);
		if( db != null ){
			// always commit any transactions before an external operation
			// in case it hangs up and leaves an uncommitted transaction open for
			// a long time.
			// Also ensures we don't roll back state inconsistent with info sent
			// in an email so never roll back to before a point an email is sent
			db.commitTransaction();
		}
		Logger log = Logger.getLogger(conn,getClass());
		if( EMAILS_FEATURE.isEnabled(conn)  && (conn.getAttribute(SUPRESS_EMAIL_ATTR) == null)){
			String force_email = conn.getInitParameter(EMAIL_FORCE_ADDRESS);
			Address[] recipients = m.getRecipients(RecipientType.TO);
			if( force_email != null && ! force_email.trim().isEmpty()){
				log.debug("Force email to "+force_email);
				Address old[] = recipients.clone();
				if( old != null && old.length > 0){
					// allow emails to designated testers.
					Set<String> allow = new HashSet<>();
					for(String addr : conn.getInitParameter(EMAIL_BYPASS_FORCE_ADDRESS,"").split("\\s*,\\s*")){
						allow.add(addr);
					}
					for(String a : force_email.split("\\s*,\\s*")) {
						m.setRecipient(RecipientType.TO,new InternetAddress(a));
					}
					for(Address a : old){
						if(a instanceof InternetAddress && allow.contains(((InternetAddress)a).getAddress())){
							m.addRecipient(RecipientType.TO, a);
							log.debug("Whitelist email to "+a.toString());
						}else{
							log.debug("Address "+a.toString()+" not in whitelist");
						}
					}
				}
				m.setRecipients(RecipientType.CC,(Address[]) null);
				m.setRecipients(RecipientType.BCC,(Address[]) null);
				m.saveChanges();
			}else{
				// apply ban-list pattern
				LinkedHashSet<Address> use_address = new LinkedHashSet<>();
				for(Address a :  recipients){
					if( ! supressSend(a)){
						use_address.add(a);
					}else {
						log.debug("Supress email to "+a.toString());
					}
				}
				if( recipients.length != use_address.size()){
					// re
					m.setRecipients(RecipientType.TO, use_address.toArray(new Address[use_address.size()]));
					m.saveChanges();
				}
			}

			// make sure send date is right as many mail clients sort by sent date.
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			m.setSentDate(time.getCurrentTime());
			m.saveChanges();
			SignMailVisitor vis = getContext().makeObjectWithDefault(SignMailVisitor.class, null, MAIL_SIGNER);
			if( vis != null ){
				m = vis.update(m);
			}
			if( EMAIL_FORCE_QUEUE_FEATURE.isEnabled(getContext())) {
				try {
					QueuedMessages.getFactory(getContext()).queueMessage(m);
					log.info("mail queued ok "+m.getSubject()+" "+formatAddresses(m.getAllRecipients()));
				} catch (DataFault e) {
					log.error("Error queueing message",e);
				}
				
			}else {
				try(TimeClosable tc = new TimeClosable(getContext(), "Emailer.send")) {
					if( DEBUG_SEND.isEnabled(getContext())){
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						Session s = getSession();
						PrintStream print = new PrintStream(stream);
						s.setDebugOut(print);;
						s.setDebug(true);
						try{
							Transport.send(m);
						}finally{
							s.setDebug(false);
							s.setDebugOut(null);
							print.flush();
							log.debug( stream.toString());
						}
					}else{
						Transport.send(m);
					}
					log.info("mail sent ok "+m.getSubject()+" "+formatAddresses(m.getAllRecipients()));
				}catch(SendFailedException sf) {
					throw sf; // Can't be sent at all
				}catch(MessagingException me) {
					if( EMAIL_QUEUE_FAILS_FEATURE.isEnabled(conn)) {
						QueuedMessages.getFactory(conn).queueMessage(m);
					}else {
						throw me;
					}
				}
			}
		}else{
			log.info("email send supressed "+m.getSubject());
			try{
				ByteArrayStreamData data = new ByteArrayStreamData();
				data.read(m.getInputStream());
				log.info(data.toString());
			}catch(Exception e){
				log.error("Error logging contents",e);
			}
			return null; // indicate send supressed
		}
		return m;
	}
	
	private String formatAddresses(Address list[]) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		boolean seen = false;
		for( Address a : list) {
			if( seen ) {
				sb.append(",");
			}
			sb.append(a.toString());
			seen=true;
		}
		sb.append("]");
		return sb.toString();
	}
	/**
	 * make an email from a template file to multiple recipients with custom
	 * headers
	 * 
	 * @param notify_emails
	 *            destination addresses
	 * @param headers
	 *            Hashtable of extra header info
	 * @param email_template
	 *            TemplateFile to sue to generate message
     * @return MimeMessage sent
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 * @throws InvalidArgument 
	 */
	public MimeMessage templateMessage(String[] notify_emails, Hashtable headers, TemplateFile email_template) throws MessagingException, UnsupportedEncodingException, InvalidArgument {
		return templateMessage(notify_emails,headers,null,true,false,email_template);
	}
	public MimeMessage templateMessage(String[] notify_emails, Hashtable headers, InternetAddress from, boolean set_sender,boolean multipart, TemplateFile email_template) throws MessagingException, UnsupportedEncodingException, InvalidArgument {
		return templateMessage(notify_emails, DEFAULT_HEADER_PREFIX,headers, from, set_sender,multipart, email_template,null);
	}
	public MimeMessage templateMessage(String[] notify_emails, String header_prefix,Hashtable headers, InternetAddress from,boolean set_sender, boolean multipart, TemplateFile email_template,Map<String,String> params) throws MessagingException, UnsupportedEncodingException, InvalidArgument {
			
		AppContext conn = getContext();
		if( email_template == null ) {
			throw new InvalidArgument("Null email template");
		}
		if( email_template.isEmpty()) {
			throw new InvalidArgument("Empty template");
		}
		if( notify_emails == null || notify_emails.length == 0) {
			throw new InvalidArgument("No recipients specified");
		}
		//		 Set a lot of standard properties from init parameters
		email_template.setProperties(conn.getInitParameters("service."));
		email_template.setProperties(conn.getInitParameters("email."));
		// explicit values override ones from config
		email_template.setProperties(params);

		String subject = null;

		
		subject = getSubject(getLogger(),email_template);
		
		MimeMessage m = makeBlankEmail(conn, notify_emails, from,set_sender, subject);
		if (headers != null) {
			for (Iterator it = headers.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				String val = headers.get(key).toString().trim();
				if (val != null && val.length() > 0) {
					m.addHeader(header_prefix + key, val);
				}
			}
		}
		
		String text = email_template.toString();
		MimeBodyPart html_part = null;
		if( HTML_ALTERNATIVE.isEnabled(conn)){
			TemplateFile html = email_template.getTemplateRegion("html");
			if( html != null ){
				
				try {
					
					HtmlBuilder hb = new HtmlBuilder();
					hb.open("html");
					// copy in standard inline css
					ResourceService rs = getContext().getService(ResourceService.class);
					 InputStream style = rs.getResourceAsStream("/css/email_inline.css");
					 if( style != null){
						 hb.open("style");
						 int c;
						 while((c=style.read()) > 0) {
							 hb.clean((char)c);
						 }
						 hb.close();
					 }
					 html.write(new XMLPrintWriterPolicy(new TemplateFile.DefaultPropertyPolicy()), new XMLPrinterWriter(hb));
					hb.close();
					html_part = new MimeBodyPart();
					html_part.setContent(hb.toString(), "text/html");
				} catch (Exception e) {
					getLogger().error("Error making html alternative", e);
				}
			}
		}
		if( multipart ){
		  MimeMultipart mp = new MimeMultipart("mixed");
		  MimeBodyPart mbp = new MimeBodyPart(); // plain text
		  if( needsEncoding(text)){
			  mbp.setText(text,getEncoding());
		  }else{
			  mbp.setText(text);
		  }
		  if( html_part != null ){
			  MimeMultipart alt = new MimeMultipart("alternative");
			  alt.addBodyPart(mbp);
			  alt.addBodyPart(html_part); // clients usually show last alternative they support.
			  MimeBodyPart cont = new MimeBodyPart();
			  cont.setContent(alt);
			  cont.setDisposition(Part.INLINE);
			  mp.addBodyPart(cont);
		  }else{
		      mbp.setDisposition(Part.INLINE);
		      mp.addBodyPart(mbp);
		  }
		  m.setContent(mp);
		}else{
			if( html_part != null ){
				MimeMultipart alt = new MimeMultipart("alternative");
				MimeBodyPart mbp = new MimeBodyPart(); // plain text
				  if( needsEncoding(text)){
					  mbp.setText(text,getEncoding());
				  }else{
					  mbp.setText(text);
				  }
				alt.addBodyPart(mbp);
				alt.addBodyPart(html_part);
				m.setContent(alt);
			}else{
				if( needsEncoding(text)){
					m.setText(text,getEncoding());
				}else{
					m.setText(text);
				}
			}
		}
		
		return m;
	}

	/**
	 * @param email_template
	 * @return
	 */
	private String getSubject(Logger log,TemplateFile email_template) {
		String subject;
		subject = (String) email_template.getProperty("subject");
		// Try to get subject from template
		if (subject == null) {
			TemplateFile subject_template = email_template
					.getTemplateRegion("Subject");
			if (subject_template == null ) {
				if( log != null ){
					log.info("EmailSender.doSendEmail no subject region");
				}
				throw new IllegalArgumentException("no subject region");
			}
			subject = strip(subject_template.toString());
			if (subject == null || subject.length() == 0) {
				if( log != null){
					log.info("EmailSender.doSendEmail null subject");
				}
				throw new IllegalArgumentException("null email subject");
			}
		}
		return subject;
	}

	public String getEncoding() {
		return ctx.getInitParameter("email.encoding", DEFAULT_ENCODING);
	}
	/** make a blank email as a starting point using the default sender
	 * 
	 * @param conn {@link AppContext}
	 * @param notify_emails
	 * @param subject
	 * @return
	 * @throws MessagingException
	 * @throws AddressException
	 * @throws UnsupportedEncodingException
	 */
	public MimeMessage makeBlankEmail(AppContext conn, String[] notify_emails,
			String subject)
			throws MessagingException, AddressException,
			UnsupportedEncodingException {
		return makeBlankEmail(conn, notify_emails, null, true, subject);
	}
	/** make a blank email as a starting point
	 * 
	 * @param conn {@link AppContext}
	 * @param notify_emails
	 * @param from   Address to send from, may be blank
	 * @param set_sender Set the default sending address as the sender
	 * @param subject
	 * @return
	 * @throws MessagingException
	 * @throws AddressException
	 * @throws UnsupportedEncodingException
	 */
	public MimeMessage makeBlankEmail(AppContext conn, String[] notify_emails,
			InternetAddress from, boolean set_sender,
			String subject)
			throws MessagingException, AddressException,
			UnsupportedEncodingException {
		Logger log = getLogger();
		String fromAddress = conn.getInitParameter(EMAIL_FROM_ADDRESS);
		String fromName = conn.getExpandedProperty(EMAIL_FROM_NAME);
		Session session = getSession();
		String text_recip = null;
		MimeMessage m = new MimeMessage(session);

		if( subject == null ){
			subject="";
		}
		if( needsEncoding(subject)){
			m.setSubject(subject,getEncoding());
		}else{
			m.setSubject(subject);
		}
		String force_email = conn.getInitParameter(EMAIL_FORCE_ADDRESS);
		log.debug("Force email is "+force_email);
		Set<InternetAddress> set = new LinkedHashSet<>();
		if (force_email == null) {
			for (int i = 0; i < notify_emails.length; i++) {
				String email = notify_emails[i];
				if( email == null || email.isEmpty()) {
					log.error("Missing notify email");
				}else {
					log.debug("Add recipient "+email);
					if (text_recip == null) {
						text_recip = email;
					} else {
						text_recip += "," + email;
					}
					set.add(new InternetAddress(email));
					try{
						// Hack to allow some emails to be automatically
						// sent to two locations
						String additional = conn.getInitParameter("email.alsoto."+email);
						if( additional != null ){
							set.add(new InternetAddress(additional));
						}
					}catch(Exception t){
						getLogger().error("Error in alsoto hack", t);
					}
				}
			}
		} else {
			for(String a : force_email.split("\\s*,\\s*")) {
				set.add(new InternetAddress(a));
			}
			text_recip=force_email;
			Set<String> allowed = new HashSet<>();
			for(String allow : conn.getInitParameter(EMAIL_BYPASS_FORCE_ADDRESS, "").split("\\s*,\\s*")){
				allowed.add(allow);
			}
			for(String email : notify_emails){
				if(allowed.contains(email)){
					log.debug("Whitelist email "+email);
					set.add(new InternetAddress(email));
					text_recip += ","+email;
				}else{
					log.debug("Blacklist email "+email);
				}
			}
			// make sure not other fields set.
			m.setRecipients(RecipientType.BCC, new InternetAddress[0]);
			m.setRecipients(RecipientType.CC, new InternetAddress[0]);
		}
		m.setRecipients(RecipientType.TO, set.toArray(new InternetAddress[0]));
		if( fromAddress == null ){
			throw new ConsistencyError("No sender address configured");
		}
		InternetAddress sender=new InternetAddress(fromAddress, fromName);
        if( from == null ){
		   m.setFrom(sender);
		   log.debug("from "+sender.toString());
        }else{
        	if( set_sender) {
        		m.setSender(sender);
        		log.debug("sender "+sender.toString());
        	}
           m.setFrom(from);
           log.debug("from "+from.toString());
        }
		m.addHeader("X-Helpdesk", "Saf");
		m.addHeader("X-Saf-service", conn.getInitParameter("service.name"));
		log.debug("made message to "+text_recip+" Subject:"+subject);
		CurrentTimeService time = getContext().getService(CurrentTimeService.class);
		m.setSentDate(time.getCurrentTime()); // default can override
		return m;
	}


	/** should we specify an encoding for the string
	 * @param subject
	 * @return
	 */
	public static boolean needsEncoding(String subject) {
		
		for(int i=0 ; i< subject.length() ; i++){
			if( subject.charAt(i) > 127 ){
				return true;
			}
		}
		return false;
	}

	private  Session session=null;
	/** get the {@link Session} to use
	 */
	protected Session getSession() {
		if( session == null ){
			session=getSession(getContext());
		}
		return session;
	}
	public static class PasswordAuth extends Authenticator{
		public PasswordAuth(String username, String password) {
			super();
			this.username = username;
			this.password = password;
		}
		private final String username;
		private final String password;
		@Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username,
                    password);
        }
	}
	public static Session getSession(AppContext conn){
		Properties props = conn.getService(ConfigService.class).getServiceProperties();

		
		LoggerService ls = conn.getService(LoggerService.class);
		Logger log=null;
		if( ls != null ){
			log = ls.getLogger(Emailer.class);
			String mailhost = props.getProperty(MAIL_SMTP_HOST);
			log.debug("mailhost is "+mailhost);
		}
		// don't use getDefaultInstance
		Authenticator auth=null;
		String smtp_user = props.getProperty(MAIL_SMTP_USER);
		String smtp_pass = props.getProperty(MAIL_SMTP_PASSWORD);
		if( smtp_user != null && smtp_pass != null ){
			auth = new PasswordAuth(smtp_user.trim(), smtp_pass.trim());
		}
		Session session = Session.getInstance(props, auth);
		if( log != null ){
			log.debug("session mailhost is "+session.getProperty(MAIL_SMTP_HOST));
		}
		return session;
	}

	/**
	 * send an email from a template file to multiple recipients
	 * 
	 * @param notify_emails
	 * @param email_template
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 * @throws InvalidArgument 
	 * @throws DataFault 
	 */
	public void templateEmail(String[] notify_emails,
			TemplateFile email_template) throws UnsupportedEncodingException,
			MessagingException, InvalidArgument, DataFault {
		doSend(templateMessage(notify_emails,email_template));
	}
	public void templateEmail(String[] notify_emails,InternetAddress from,
			TemplateFile email_template) throws UnsupportedEncodingException,
			MessagingException, InvalidArgument, DataFault {
		doSend(templateMessageWithFrom(notify_emails,from,email_template));
	}
	/**
	 * make an email from a template file to multiple recipients
	 * 
	 * @param notify_emails
	 * @param email_template
	 * @return message
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 * @throws InvalidArgument 
	 */
	public MimeMessage templateMessage(String[] notify_emails,
			TemplateFile email_template) throws UnsupportedEncodingException,
			MessagingException, InvalidArgument {
		return templateMessage(notify_emails, null, email_template);
	}
	/**
	 * make an email from a template file to multiple recipients
	 * 
	 * @param notify_emails
	 * @param email_template
	 * @return message
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 * @throws InvalidArgument 
	 */
	public MimeMessage templateMessageWithFrom(String[] notify_emails,InternetAddress from,
			TemplateFile email_template) throws UnsupportedEncodingException,
			MessagingException, InvalidArgument {
		return templateMessage(notify_emails, null,from,true,false, email_template);
	}
	/**
	 * check the validity of an Email address
	 * 
	 * @param email
	 *            String containing address
	 * @return true if address parses ok
	 */
	public static boolean checkAddress(String email) {
		if( email == null || email.trim().length() == 0){
			return false;
		}
		// We use jakarta.mail.internet.InternetAddress to parse
		// and
		// verify the email address.
		try {

			new InternetAddress(email);
		} catch (AddressException e) {
			return false;

		}
		// check that this is a remote address
		if (email.indexOf('@') == -1) {
			return false;
		}
		// dont want comment
		if( email.indexOf('<') != -1){
			return false;
		}
		if( email.indexOf('>') != -1){
			return false;
		}
		return true;
	}
    synchronized public static void resetReport() {
    	last_send=0;
    	send_count=0;
    }
	/**
	 * Test if its ok to send an email report. Throttle back if too many emails
	 * are bein sent
	 * 
	 * @return boolean true if email shuld be sent
	 */
	synchronized private static boolean doReport(Logger logger,AppContext conn) {
		
		
		if( ! EMAILS_FEATURE.isEnabled(conn)){
			if( logger != null) {
				logger.debug("Feature "+EMAILS_FEATURE+" is off");
			}
			return false;
		}
		long now = System.currentTimeMillis() / 1000;
		if( logger != null ) {
			logger.debug(
				"doReport " + (now - last_send) + " s since last count="
						+ send_count);
		}
		if (now > (last_send + MAX_REPORT_FREQUENCY)) {
			last_send = now;
			send_count = 0;
			return true;
		}
		if (send_count++ > MAX_REPORTS) {
			return false;
		}
		return true;
	}

	/**
	 * General email error report
	 * 
	 
	 */
	public void errorEmail(Logger log,String subject,String text) {
		if( EMAIL_FORCE_QUEUE_FEATURE.isEnabled(getContext())) {
			// Error emails are never queued. so suppress them if queueing is forced
			return;
		}
		try {
			AppContext conn = getContext();
			if (!doReport(log,conn)) {
				if( log != null ) {
					log.error("error email supressed " + text);
				}
				return;
			}
			String emailTo = conn
					.getInitParameter(ERROR_EMAIL_NOTIFY_ADDRESS);
			String emailFrom = conn
					.getInitParameter(ERROR_EMAIL_FROM_ADDRESS);
			String tag = conn.getInitParameter("service.name", "Webapp");
			//log.debug("tag is "+tag);
			String emailSubject;
			if( subject == null){
				emailSubject = tag + " Error";
			}else{
				emailSubject=subject;
			}
			if( log != null) {
				log.debug("sending "+emailSubject);
			}
			Session session = getSession(conn);

			// create the Multipart
			Multipart mp = new MimeMultipart();

			// create and fill the first message part
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText(text, "ISO-8859-1");
			mbp1.setHeader("Content-type", "text/plain; charset=ISO-8859-1");
			//mbp1.setHeader("Content-Transfer-Encoding", "base64");

			mp.addBodyPart(mbp1);

			MimeMessage m = new MimeMessage(session);
			String addresses[]= emailTo.split(",");
			InternetAddress ia[] = new InternetAddress[addresses.length];
			for(int i=0 ; i< addresses.length; i++){
				ia[i] = new InternetAddress(addresses[i]);
			}
			m.setRecipients(Message.RecipientType.TO, ia);
			m.setFrom(new InternetAddress(emailFrom));
			m.setSubject(emailSubject);
			m.addHeader("X-Saf-service", conn.getInitParameter("service.name","Webapp"));
			m.addHeader("X-Safe-notify", "Error");
			m.setContent(mp);
			if(EMAILS_FEATURE.isEnabled(conn)){
				Transport.send(m);
			}
			if( log != null) {
				log.debug("Sent error email");
			}
		} catch (Exception me) {
			// ERROR.. uh log it?
			if( log != null) {
				log.error("Failed to send error email " + me);
			}else {
				me.printStackTrace(System.err);
			}
		}

	}

	/**
	 * Send an error email
	 * This is called within {@link EmailLogger} so logging should always go through the
	 * provided logger.  We need to be careful to recover from errors if we can so that we
	 * can still send some email even if some things are failing
	 *
	 */
	public void errorEmail(Logger log,Throwable e,
			Map props, String additional_info) throws Exception {
		AppContext conn = getContext();
		if( EMAIL_FORCE_QUEUE_FEATURE.isEnabled(conn)) {
			// Error emails are never queued. so suppress them if queueing is forced
			return;
		}
	
		if( conn == null || conn.getInitParameter(ERROR_EMAIL_NOTIFY_ADDRESS) == null ){
			// abort early if no notify address set.
			return;
		}
		String subject="An Error occurred";
		String body="";
		// Show stack trace
		StringWriter stackTraceWriter = new StringWriter();
		StringBuffer buf = stackTraceWriter.getBuffer();
		int max_trace = conn.getIntegerParameter("error.max_strack_trace", 16384);
		if( e != null ) {
			PrintWriter printWriter = new PrintWriter(stackTraceWriter,true);
			e.printStackTrace(printWriter);
			printWriter.flush();
			Throwable rc = e.getCause();
			while(rc != null && buf.length() < max_trace){
				printWriter.println("Caused by:");
				rc.printStackTrace(printWriter);
				printWriter.flush();
				rc = rc.getCause();
			}
			printWriter.flush();
			printWriter.close();
		}
		try {
			TemplateFile errorEmail;
			errorEmail = getFinder().getTemplateFile("error_email.txt");



			// Show current date and time
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			CurrentTimeService time = conn.getService(CurrentTimeService.class);
			errorEmail.setProperty("date", df.format(time.getCurrentTime()));
			if (props != null) {
				errorEmail.setProperties(props);
			}
			if (e != null) {
				String message = e.getMessage();

				if(message != null ){
					String subject_message = message;

					if( subject_message.contains("\n")){
						subject_message = subject_message.substring(0, subject_message.indexOf('\n'));
					}
					if( subject_message.length() > 64){
						subject_message = subject_message.substring(0, 64);
					}
					errorEmail.setProperty("subject_message", subject_message);
					errorEmail.setProperty("exception_message", message);
				}

				errorEmail.setProperty("exception_stack_trace", stackTraceWriter.toString());
			}else{
				if( additional_info != null && additional_info.length() < 64){
					errorEmail.setProperty("subject_message", additional_info);
				}
			}

			if (additional_info != null) {
				errorEmail.setProperty("additional_info", additional_info);
				errorEmail.setRegionEnabled("additional_info_region", true);
			}


			try{
				subject = getSubject(log, errorEmail);
			}catch(Exception t){
				// can cope with this
			}
			body=errorEmail.toString();

		}catch(Exception x) {
			// Try again without template
			if( additional_info != null) {
				body += additional_info;
				body += "\n";
			}
			if( e != null ) {
				body += stackTraceWriter.toString();
			}
			if( log != null ) {
				log.error("error in email formatting", x);
			}
		}
		errorEmail(log, subject,body);
	}

	/**
	 * General email information report
	 * 
	 * @param conn
	 * @param text
	 */
	public static void infoEmail(AppContext conn, String text) {
		if( EMAIL_FORCE_QUEUE_FEATURE.isEnabled(conn)) {
			// Error emails are never queued. so suppress them if queueing is forced
			return;
		}
		Logger log = Logger.getLogger(conn,Emailer.class);
		try {

			if (!doReport(log,conn)) {
				if( log != null ) {
					log.info("info email supressed " + text);
				}
				return;
			}
			String emailTo = conn.getInitParameter("info.email_notify_address");
			String emailFrom = conn.getInitParameter("info.email_from_address");
			String tag = conn.getInitParameter("service.name", "Webapp");
			String emailSubject = tag + " Information";

			if (emailTo == null || emailFrom == null) {
				log.info("No info address set " + text);
				return;
			}

			Session session = getSession(conn);
			session.setDebug(true);

			// create the Multipart
			Multipart mp = new MimeMultipart();

			// create and fill the first message part
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText(text, "ISO-8859-1");
			mbp1.setHeader("Content-type", "text/plain");
			mbp1.setHeader("Content-Transfer-Encoding", "base64");
		
			mp.addBodyPart(mbp1);

			MimeMessage m = new MimeMessage(session);
			String addresses[]= emailTo.split(",");
			InternetAddress ia[] = new InternetAddress[addresses.length];
			for(int i=0 ; i< addresses.length; i++){
				ia[i] = new InternetAddress(addresses[i]);
			}
			m.setRecipients(Message.RecipientType.TO, ia);
			m.setFrom(new InternetAddress(emailFrom));
			m.setSubject(emailSubject);
			m.addHeader("X-Saf-service", conn.getInitParameter("service.name"));
			m.addHeader("X-Safe-notify", "Info");
			m.setContent(mp);
			if(EMAILS_FEATURE.isEnabled(conn)){
				Transport.send(m);
			}

		} catch (Exception me) {
			// ERROR.. uh log it?
			if( log != null) {
				log.error("Failed to send Info email " + me);
			}else {
				me.printStackTrace(System.err);
			}

		}

	}
    public static String[] splitEmailList(String list){
    	StringTokenizer st = new StringTokenizer(list,",",false);
    	LinkedHashSet<String> set = new LinkedHashSet<>();
    	while(st.hasMoreTokens()){
             String s = st.nextToken();
             if(checkAddress(s)){
            	 set.add(s);
             }
    	}
    	return set.toArray(new String[set.size()]);
    }
    public static Set<InternetAddress> parseEmailList(String list) throws AddressException{
    	LinkedHashSet<InternetAddress> result = new LinkedHashSet<>();
    	for(String s : list.split("\\s*,\\s*")){
    		
				result.add(new InternetAddress(s));
    	}
    	return result;
    }
	private static String strip(String s) {
		StringBuilder buff = new StringBuilder(s);
		for (int i = 0; i < buff.length(); i++) {
			if (buff.charAt(i) == '\n' || buff.charAt(i) == '\r') {
				buff.setCharAt(i, ' ');
			}
		}
		return buff.toString();
	}

	public static boolean checkAddressList(String list) {
		for(String s : list.split("\\s*,\\s*")) {
             if(! checkAddress(s)){
            	 return false;
             }
    	}
		return true;
	}

	private TemplateFinder getFinder() {
		if( finder == null) {
			finder= TemplateFinder.getTemplateFinder(getContext());
		}
		return finder;
	}
	
	/** Extension point to customise the sender address based on the recipient
	 * 
	 * Default is to return null and take the default sender
	 * 
	 * @param user
	 * @return
	 */
	public InternetAddress getFrom(AppUser user) {
		return null;
	}
	/** Extension point to customise the template params based on the recipient
	 * 
	 * @param params
	 * @param user
	 */
	public void addParams(Map<String,String> params, AppUser user) {
		
	}
}