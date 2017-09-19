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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.content.TemplateFile;
import uk.ac.ed.epcc.webapp.content.XMLPrintWriterPolicy;
import uk.ac.ed.epcc.webapp.content.XMLPrinterWriter;
import uk.ac.ed.epcc.webapp.email.logging.EmailLogger;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
//import uk.ac.ed.epcc.webapp.logging.email.EmailLogger;
import uk.ac.ed.epcc.webapp.model.TemplateFinder;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayStreamData;
import uk.ac.ed.epcc.webapp.resource.ResourceService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.EmailChangeRequestFactory.EmailChangeRequest;
import uk.ac.ed.epcc.webapp.session.PasswordChangeListener;
import uk.ac.ed.epcc.webapp.session.PasswordChangeRequestFactory;
import uk.ac.ed.epcc.webapp.session.PasswordChangeRequestFactory.PasswordChangeRequest;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * Emailer Class that sends emails.
 * 
 * all emails sent by the Web-app are funneled through this class so we can
 * enforce debugging modes that allow us to debug with a copy of the main
 * database without sending spurious emails to users. All dependencies on
 * javax.mail are also routed through this class to reduce dependencies.
 * 
 * We could implement encrypted emails or crytographic signing here if we
 * wanted.
 * 
 * @author spb
 * 
 */


public class Emailer {
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
	private static final String DEFAULT_HEADER_PREFIX = "X-Saf-";
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
    public static final Feature PASSWORD_RESET_SERVLET = new Feature("password_reset.servlet",false,"Send reset url in reset email");
    
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
	private Pattern dont_send_pattern=null;
	
	public Emailer(AppContext c) {
		ctx = c;
		try{
			String pattern_text = c.getInitParameter("email.dont_send_pattern");
			if( pattern_text != null){
				dont_send_pattern = Pattern.compile(pattern_text);
			}
		}catch(Throwable t){
			getLogger().error("Error making dont_send_pattern", t);
		}
	}

	protected AppContext getContext() {
		return ctx;
	}

	/**
	 * Send an email with the person new password
	 * 
	 * @param person
	 * @param new_password
	 * @throws Exception 
	 */
	public void newPassword(AppUser person, String new_password)
			throws Exception {

		TemplateFile email_template = new TemplateFinder(ctx).getTemplateFile("request_password.txt");

		if( email_template == null ){
			getLogger().debug(new_password);
			return;
		}
		email_template.setProperty("person.name", person.getName());
		email_template.setProperty("person.password", new_password);
		String email = person.getEmail();
		if( email == null){
			getLogger().error("New password for user with null email "+person.getIdentifier());;
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
			
		}
		
		doSend(templateMessage(person,email_template));
		try{
			PasswordChangeListener listener = ctx.makeObjectWithDefault(PasswordChangeListener.class,null, PasswordChangeListener.PASSWORD_LISTENER_PROP);
			if( listener != null ){
				listener.passwordInvalid(person);
			}
		}catch(Throwable t){
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

		TemplateFile email_template = new TemplateFinder(ctx).getTemplateFile("new_email.txt");

		email_template.setProperty("person.name", person.getName());
		email_template.setProperty("person.email", person.getEmail());
		email_template.setProperty("request.email", req.getEmail());
		email_template.setProperty("request.tag", req.getTag());
		

		doSend(templateMessage(req.getEmail(), null, email_template));

	}
	/**
	 * Generic welcome email giving a new signup his password. Some Webapps may
	 * need to customise this.
	 * 
	 * 
	 * @param person
	 * @param new_password
	 * @throws Exception 
	 * 
	 */
	public void newSignup(AppUser person, String new_password)
			throws Exception {

		TemplateFile email_template = new TemplateFinder(ctx).getTemplateFile("new_signup.txt");
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
			getLogger().error("Signup email destination not known "+person.getIdentifier());;
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
			
		}
		doSend(templateMessage(person,email_template));

	}

	public MimeMessage templateMessage(String sendto, Hashtable h,
			TemplateFile email_template) throws IOException, MessagingException {
		Logger log = getLogger();
		// change destination depending on sendto:
		String email = mapRecipients(sendto);
		
		log.info("EmailSender sending an email to " + email);
		return templateMessage(new String[] { email }, h, email_template);
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
	 * @param sendto
	 * @param email_template
	 * @return {@link MimeMessage}
	 * @throws IOException
	 * @throws MessagingException
	 */
	public MimeMessage templateMessage(AppUser recipient, TemplateFile email_template)
			throws IOException, MessagingException {
		return templateMessage(recipient, null, email_template);
	}
	
	public MimeMessage templateMessage(AppUser recipient, Hashtable headers,TemplateFile email_template)
			throws IOException, MessagingException {
	Logger log = getLogger();
	String email = getEmail(recipient);
	log.debug("Email mapped "+recipient.getEmail()+"->"+email);
	if( email != null && email.trim().length() > 0){
		return templateMessage(email, headers, email_template);
	}else{
		log.warn("Email to "+recipient.getIdentifier()+" mapped to null");
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
	public MimeMessage doSend(MimeMessage m) throws MessagingException{
		if( m == null ){
			return null;
		}
		AppContext conn = getContext();
		Logger log = conn.getService(LoggerService.class).getLogger(getClass());

		DatabaseService db = conn.getService(DatabaseService.class);
		if( db != null ){
			// always commit any transactions before an external operation
			// in case it hangs up.
			// Also ensures we don't roll back state inconsistent with info sent
			// in an email
			db.commitTransaction();
		}
		
		if( EMAILS_FEATURE.isEnabled(conn)  && (conn.getAttribute(SUPRESS_EMAIL_ATTR) == null)){
			String force_email = conn.getInitParameter(EMAIL_FORCE_ADDRESS);
			Address[] recipients = m.getRecipients(RecipientType.TO);
			if( force_email != null){
				log.debug("Force email to "+force_email);
				Address old[] = recipients.clone();
				if( old != null && old.length > 0){
					// allow emails to designated testers.
					Set<String> allow = new HashSet<String>();
					for(String addr : conn.getInitParameter(EMAIL_BYPASS_FORCE_ADDRESS,"").split("\\s*,\\s*")){
						allow.add(addr);
					}
					m.setRecipient(RecipientType.TO, new InternetAddress(force_email));
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
				// apply blacklist pattern
				LinkedHashSet<Address> use_address = new LinkedHashSet<>();
				for(Address a :  recipients){
					if( ! supressSend(a)){
						use_address.add(a);
					}
				}
				if( recipients.length != use_address.size()){
					// re
					m.setRecipients(RecipientType.TO, use_address.toArray(new Address[use_address.size()]));
					m.saveChanges();
				}
			}

			// make sure send date is right as many mail clients sort by sent date.
			m.setSentDate(new Date());
			m.saveChanges();
			SignMailVisitor vis = getContext().makeObjectWithDefault(SignMailVisitor.class, null, MAIL_SIGNER);
			if( vis != null ){
				m = vis.update(m);
			}
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
			log.info("mail sent ok "+m.getSubject()+" "+m.getAllRecipients().toString());
		}else{
			log.info("email send supressed "+m.getSubject());
			try{
				ByteArrayStreamData data = new ByteArrayStreamData();
				data.read(m.getInputStream());
				log.info(data.toString());
			}catch(Exception e){
				log.error("Error logging contents",e);
			}
		}
		return m;
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
	 */
	public MimeMessage templateMessage(String[] notify_emails, Hashtable headers, TemplateFile email_template) throws MessagingException, UnsupportedEncodingException {
		return templateMessage(notify_emails,headers,null,false,email_template);
	}
	public MimeMessage templateMessage(String[] notify_emails, Hashtable headers, InternetAddress from, boolean multipart, TemplateFile email_template) throws MessagingException, UnsupportedEncodingException {
		return templateMessage(notify_emails, DEFAULT_HEADER_PREFIX,headers, from, multipart, email_template,null);
	}
	public MimeMessage templateMessage(String[] notify_emails, String header_prefix,Hashtable headers, InternetAddress from, boolean multipart, TemplateFile email_template,Map<String,String> params) throws MessagingException, UnsupportedEncodingException {
			
		AppContext conn = getContext();
		
		//		 Set a lot of standard properties from init parameters
		email_template.setProperties(conn.getInitParameters("service."));
		email_template.setProperties(conn.getInitParameters("email."));
		// explicit values override ones from config
		email_template.setProperties(params);

		String subject = null;

		
		subject = getSubject(getLogger(),email_template);
		
		MimeMessage m = makeBlankEmail(conn, notify_emails, from, subject);
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
	private static String getSubject(Logger log,TemplateFile email_template) {
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

	public MimeMessage makeBlankEmail(AppContext conn, String[] notify_emails,
			InternetAddress from, String subject)
			throws MessagingException, AddressException,
			UnsupportedEncodingException {
		Logger log = getLogger();
		String fromAddress = conn.getInitParameter(EMAIL_FROM_ADDRESS);
		String fromName = conn.getInitParameter(EMAIL_FROM_NAME);
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
		Set<InternetAddress> set = new LinkedHashSet<InternetAddress>();
		if (force_email == null) {
			for (int i = 0; i < notify_emails.length; i++) {
				log.debug("Add recipient "+notify_emails[i]);
				if (text_recip == null) {
					text_recip = notify_emails[i];
				} else {
					text_recip += "," + notify_emails[i];
				}
				set.add(new InternetAddress(notify_emails[i]));
				try{
					// Hack to allow some emails to be automatically
					// sent to two locations
					String additional = conn.getInitParameter("email.alsoto."+notify_emails[i]);
					if( additional != null ){
						set.add(new InternetAddress(additional));
					}
				}catch(Throwable t){
					getLogger().error("Error in alsoto hack", t);
				}
			}
		} else {
			
			set.add(new InternetAddress(force_email));
			text_recip=force_email;
			Set<String> allowed = new HashSet<String>();
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
           m.setSender(sender);
           log.debug("sender "+sender.toString());
           m.setFrom(from);
           log.debug("from "+from.toString());
        }
		m.addHeader("X-Helpdesk", "Saf");
		m.addHeader("X-Saf-service", conn.getInitParameter("service.name"));
		log.debug("made message to "+text_recip+" Subject:"+subject);
		m.setSentDate(new Date()); // default can override
		return m;
	}

	public Logger getLogger() {
		return getContext().getService(LoggerService.class).getLogger(getClass());
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
	/**
	 * @param conn
	 * @param log
	 * @return
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
	 */
	public MimeMessage templateEmail(String[] notify_emails,
			TemplateFile email_template) throws UnsupportedEncodingException,
			MessagingException {
		return doSend(templateMessage(notify_emails,email_template));
	}
	/**
	 * make an email from a template file to multiple recipients
	 * 
	 * @param notify_emails
	 * @param email_template
	 * @return message
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public MimeMessage templateMessage(String[] notify_emails,
			TemplateFile email_template) throws UnsupportedEncodingException,
			MessagingException {
		return templateMessage(notify_emails, null, email_template);
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
		// We use javax.mail.internet.InternetAddress to parse
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

	/**
	 * Test if its ok to send an email report. Throttle back if too many emails
	 * are bein sent
	 * 
	 * @return boolean true if email shuld be sent
	 */
	synchronized private static boolean doReport(AppContext conn) {
		Logger logger = conn.getService(LoggerService.class).getLogger(conn.getClass());
		
		if( ! EMAILS_FEATURE.isEnabled(conn)){
			logger.debug("Feature "+EMAILS_FEATURE+" is off");
			return false;
		}
		long now = System.currentTimeMillis() / 1000;
		logger.debug(
				"doReport " + (now - last_send) + " s since last count="
						+ send_count);
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
	 * @param conn
	 * @param text
	 */
	public static void errorEmail(AppContext conn, String subject,String text) {
		Logger log = conn.getService(LoggerService.class).getLogger(conn.getClass());
		try {
			
			if (!doReport(conn)) {
				log.error("error email supressed " + text);
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
            log.debug("sending "+emailSubject);
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
			m.addHeader("X-Saf-service", conn.getInitParameter("service.name"));
			m.addHeader("X-Safe-notify", "Error");
			m.setContent(mp);
			if(EMAILS_FEATURE.isEnabled(conn)){
				Transport.send(m);
			}
            log.debug("Sent error email");
		} catch (Exception me) {
			// ERROR.. uh log it?
			log.error("Failed to send error email " + me);

		}

	}

	/**
	 * Send an error email
	 * This is called within {@link EmailLogger} so logging should not be used here to
	 * prevent recursion
	 * @param conn
	 * @param props
	 * @param e
	 * @param additional_info
	 * @throws Exception 
	 */
	public static void errorEmail(AppContext conn, Throwable e,
			Map props, String additional_info) throws Exception {
		if( conn.getInitParameter(ERROR_EMAIL_NOTIFY_ADDRESS) == null ){
			// abort early if no notify address set.
			return;
		}
		TemplateFile errorEmail;
		
		errorEmail = new TemplateFinder(conn).getTemplateFile("error_email.txt");
		

		
		// Show current date and time
		DateFormat df = DateFormat.getDateTimeInstance();
		errorEmail.setProperty("date", df.format(new java.util.Date()));
		if (props != null) {
			errorEmail.setProperties(props);
		}
		if (e != null) {
			String message = e.getMessage();
			
			if(message != null ){
				String subject_message = message;

				if( subject_message.contains("\n")){
					subject_message.substring(0, subject_message.indexOf('\n'));
				}
				if( subject_message.length() > 64){
					subject_message.substring(0, 64);
				}
				errorEmail.setProperty("subject_message", subject_message);
				errorEmail.setProperty("exception_message", message);
			}
			// Show stack trace
			StringWriter stackTraceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stackTraceWriter,true);
			e.printStackTrace(printWriter);
			Throwable rc = e.getCause();
			while(rc != null){
				printWriter.println("Caused by:");
				rc.printStackTrace(printWriter);
				rc = rc.getCause();
			}
			printWriter.flush();
			printWriter.close();
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
		
		String subject = null;
		try{
			subject = getSubject(null, errorEmail);
		}catch(Throwable t){
			// can cope with this
		}
		errorEmail(conn, subject,errorEmail.toString());
       
	}

	/**
	 * General email information report
	 * 
	 * @param conn
	 * @param text
	 */
	public static void infoEmail(AppContext conn, String text) {
		Logger log = conn.getService(LoggerService.class).getLogger(conn.getClass());
		try {

			if (!doReport(conn)) {
				conn.getService(LoggerService.class).getLogger(conn.getClass()).info("info email supressed " + text);
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
			log.error("Failed to send Info email " + me);

		}

	}
    public static String[] splitEmailList(String list){
    	StringTokenizer st = new StringTokenizer(list,",",false);
    	LinkedHashSet<String> set = new LinkedHashSet<String>();
    	while(st.hasMoreTokens()){
             String s = st.nextToken();
             if(checkAddress(s)){
            	 set.add(s);
             }
    	}
    	return set.toArray(new String[set.size()]);
    }
    public static Set<InternetAddress> parseEmailList(String list) throws AddressException{
    	LinkedHashSet<InternetAddress> result = new LinkedHashSet<InternetAddress>();
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
		StringTokenizer st = new StringTokenizer(list,",",false);
    	while(st.hasMoreTokens()){
             String s = st.nextToken();
             if(! checkAddress(s)){
            	 return false;
             }
    	}
		return true;
	}
	
}