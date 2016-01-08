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

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
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
import uk.ac.ed.epcc.webapp.content.TemplateFile;
import uk.ac.ed.epcc.webapp.email.logging.EmailLogger;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
//import uk.ac.ed.epcc.webapp.logging.email.EmailLogger;
import uk.ac.ed.epcc.webapp.model.TemplateFinder;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayStreamData;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.EmailChangeRequestFactory.EmailChangeRequest;
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

	public Emailer(AppContext c) {
		ctx = c;
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

		email_template.setProperty("person.name", person.getName());
		email_template.setProperty("person.password", new_password);
		email_template.setProperty("person.email", person.getEmail());
		if( PASSWORD_RESET_SERVLET.isEnabled(ctx)){
			PasswordChangeRequestFactory fac = new PasswordChangeRequestFactory(ctx.getService(SessionService.class).getLoginFactory());
			PasswordChangeRequest request = fac.createRequest(person);
			if( request != null ){
				email_template.setRegionEnabled("password_reset.url_region", true);
				email_template.setProperty("password_reset.tag", request.getTag());
			}
			
		}

		templateEmail(person.getEmail(), email_template);

	}

	/** Send an email requesting confiormation of a new Email address.
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
		

		templateEmail(req.getEmail(), email_template);

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

		String name = person.getName();
		if( name == null || name.trim().length() == 0){
			name = "User";
		}
		email_template.setProperty("person.name", name);
		email_template.setProperty("person.password", new_password);
		email_template.setProperty("person.email", person.getEmail());
		if( PASSWORD_RESET_SERVLET.isEnabled(ctx)){
			PasswordChangeRequestFactory fac = new PasswordChangeRequestFactory(ctx.getService(SessionService.class).getLoginFactory());
			PasswordChangeRequest request = fac.createRequest(person);
			if( request != null ){
				email_template.setRegionEnabled("password_reset.url_region", true);
				email_template.setProperty("password_reset.tag", request.getTag());
			}
			
		}
		templateEmail(person.getEmail(), email_template);

	}

	public MimeMessage templateEmail(String sendto, Hashtable h,
			TemplateFile email_template) throws IOException, MessagingException {
		Logger log = getLogger();
		// change destination depending on sendto:
		String email = mapRecipients(sendto);
		
		log.info("EmailSender sending an email to " + email);
		return templateEmail(new String[] { email }, h, email_template);
	}
    public String mapRecipients(String sendto){
    	return sendto;
    }
	/**
	 * Send an email based on a template file to a single recipient
	 * 
	 * @param sendto
	 * @param email_template
	 * @return {@link MimeMessage}
	 * @throws IOException
	 * @throws MessagingException
	 */
	public MimeMessage templateEmail(String sendto, TemplateFile email_template)
			throws IOException, MessagingException {
		return templateEmail(sendto, null, email_template);
	}

	/**
	 * send an email from a template file to multiple recipients with custom
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
	public MimeMessage templateEmail(String[] notify_emails, Hashtable headers,
			TemplateFile email_template) throws UnsupportedEncodingException,
			MessagingException {

		
		
		MimeMessage m = templateMessage(notify_emails,headers,email_template);
	    return doSend(m);
	
	}
	public MimeMessage doSend(MimeMessage m) throws MessagingException{
		AppContext conn = getContext();
		Logger log = conn.getService(LoggerService.class).getLogger(getClass());

		if( EMAILS_FEATURE.isEnabled(conn)  && (conn.getAttribute(SUPRESS_EMAIL_ATTR) == null)){
			String force_email = conn.getInitParameter(EMAIL_FORCE_ADDRESS);
			if( force_email != null){
				log.debug("Force email to "+force_email);
				Address old[] = m.getRecipients(RecipientType.TO).clone();
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
			}
			// make sure send date is right as many mail clients sort by sent date.
			m.setSentDate(new Date());
			m.saveChanges();
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
			log.info("mail sent ok");
		}else{
			log.info("email send supressed");
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
	public MimeMessage templateMessage(String[] notify_emails, Hashtable headers, TemplateFile email_template) throws MessagingException, UnsupportedEncodingException {
		return templateMessage(notify_emails,headers,null,false,email_template);
	}
	public MimeMessage templateMessage(String[] notify_emails, Hashtable headers, InternetAddress from, boolean multipart, TemplateFile email_template) throws MessagingException, UnsupportedEncodingException {
		return templateMessage(notify_emails, headers, from, multipart, email_template,null);
	}
	public MimeMessage templateMessage(String[] notify_emails, Hashtable headers, InternetAddress from, boolean multipart, TemplateFile email_template,Map<String,String> params) throws MessagingException, UnsupportedEncodingException {
			
		AppContext conn = getContext();
		
		email_template.setProperties(params);
		//		 Set a lot of standard properties from init parameters
		email_template.setProperties(conn.getInitParameters("service."));
		email_template.setProperties(conn.getInitParameters("email."));
		email_template.setProperties(params);

		String subject = null;

		
		subject = (String) email_template.getProperty("subject");
		// Try to get subject from template
		if (subject == null) {
			TemplateFile subject_template = email_template
					.getTemplateRegion("Subject");
			if (subject_template == null ) {
				Logger log = getLogger();
				log.info("EmailSender.doSendEmail no subject region");
				throw new IllegalArgumentException("no subject region");
			}
			subject = strip(subject_template.toString());
			if (subject == null || subject.length() == 0) {
				Logger log = getLogger();
				log.info("EmailSender.doSendEmail null subject");
				throw new IllegalArgumentException("null email subject");
			}
		}
		
		MimeMessage m = makeBlankEmail(conn, notify_emails, from, subject);
		if (headers != null) {
			for (Iterator it = headers.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				String val = headers.get(key).toString().trim();
				if (val != null && val.length() > 0) {
					m.addHeader("X-Saf-" + key, val);
				}
			}
		}
		
		String text = email_template.toString();
		if( multipart ){
		  MimeMultipart mp = new MimeMultipart("mixed");
		  MimeBodyPart mbp = new MimeBodyPart();
		  if( needsEncoding(text)){
			  mbp.setText(text,getEncoding());
		  }else{
			  mbp.setText(text);
		  }
		  mbp.setDisposition(Part.INLINE);
		  mp.addBodyPart(mbp);
		  m.setContent(mp);
		}else{
			if( needsEncoding(text)){
				m.setText(text,getEncoding());
			}else{
				m.setText(text);
			}
		}
		
		return m;
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
		InternetAddress ia[];
		if (force_email == null) {
			ia = new InternetAddress[notify_emails.length];
			for (int i = 0; i < notify_emails.length; i++) {
				log.debug("Add recipient "+notify_emails[i]);
				if (text_recip == null) {
					text_recip = notify_emails[i];
				} else {
					text_recip += "," + notify_emails[i];
				}
				ia[i] = new InternetAddress(notify_emails[i]);
			}

		} else {
			Set<InternetAddress> set = new HashSet<InternetAddress>();
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
			ia = set.toArray(new InternetAddress[0]);
			// make sure not other fields set.
			m.setRecipients(RecipientType.BCC, new InternetAddress[0]);
			m.setRecipients(RecipientType.CC, new InternetAddress[0]);
		}
		m.setRecipients(RecipientType.TO, ia);
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
		Session session = Session.getInstance(props, null);
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
	public void templateEmail(String[] notify_emails,
			TemplateFile email_template) throws UnsupportedEncodingException,
			MessagingException {
		templateEmail(notify_emails, null, email_template);
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
	public static void errorEmail(AppContext conn, String text) {
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
			String emailSubject = tag + " Error";
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
			errorEmail.setProperty("exception_message", e.getMessage());
			// Show stack trace
			StringWriter stackTraceWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stackTraceWriter);
			e.printStackTrace(printWriter);
			Throwable rc = e.getCause();
			while(rc != null){
				printWriter.println("Caused by:");
				rc.printStackTrace(printWriter);
				rc = rc.getCause();
			}
			printWriter.flush();
			errorEmail.setProperty("exception_stack_trace", stackTraceWriter
					.toString());
		}

		if (additional_info != null) {
			errorEmail.setProperty("additional_info", additional_info);
			errorEmail.setRegionEnabled("additional_info_region", true);
		}
        
		errorEmail(conn, errorEmail.toString());
       
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