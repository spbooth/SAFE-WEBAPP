// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.editors.mail;

import java.io.IOException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import uk.ac.ed.epcc.webapp.email.Emailer;


/** class to build a set of mime parts incrementally
 * Text parts are merged until explicitly flushed or a non
 * text part is added.
 * 
 * The autoFlush option can be used to make separate calls act like flushText.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: MultipartMailBuilder.java,v 1.3 2015/11/09 16:32:08 spb Exp $")

public class MultipartMailBuilder extends TextMailBuilder{
	

	/**
	 * 
	 */
	private static final String DEFAULT_ENCODING = "UTF-8";
	private final MimeMultipart mp;
	private String encoding;
	
	private boolean flush_on_seperate=false;
		public MultipartMailBuilder() throws MessagingException, IOException{
		this(new MimeMultipart());
	}
	public MultipartMailBuilder(MimeMultipart mp) throws MessagingException, IOException{
		super();
		this.mp=mp;
	
		encoding=DEFAULT_ENCODING;
		
		if( mp.getCount() == 1 ){
			// if we are starting with a single text part append to it
			MimeBodyPart part = (MimeBodyPart) mp.getBodyPart(0);
			if( part.isMimeType("text/plain")){
				text.append((String) part.getContent());
				mp.removeBodyPart(part);
			}
		}
	}
	public void setAutoFlush(boolean val){
		flush_on_seperate=val;
	}
	public MimeMultipart getMultipart() throws MessagingException{
		flushText();
		return mp;
	}
	
	public int  flushText() throws MessagingException{
		int count=0;
		if(text.length()> 0){
			MimeBodyPart part = new MimeBodyPart();
			part.setDisposition(Part.INLINE);
			String string = text.toString();
			if( Emailer.needsEncoding(string)){
				part.setText(string,encoding);
			}else{
				part.setText(string);
			}
			part.addHeader("format", "flowed");
			mp.addBodyPart(part);
			text.setLength(0);
			count++;
		}
		separator=null;
		return count;
	}
	public int addBodyPart(MimeBodyPart p) throws MessagingException{
		int count = flushText();
		mp.addBodyPart(p);
		count++;
		return count;
	}
	public int addMessage(MimeMessage m) throws MessagingException{
		MimeBodyPart mbp = new MimeBodyPart();
		mbp.setContent(m, "message/rfc822");
		mbp.setDisposition(Part.INLINE);
		return addBodyPart(mbp);
	}
	public  int addDataSource(DataSource msd, String comment,String name) throws MessagingException{
		DataHandler dh = new DataHandler(msd);
		MimeBodyPart mbp = new MimeBodyPart();
	    mbp.setDataHandler(dh);
	    mbp.setFileName(name);
	    mbp.setDescription(comment);
	    mbp.setDisposition(Part.ATTACHMENT);
	    return addBodyPart(mbp);
	}
	/** Add contents of a MAilBuilder
	 * Normally as a nested multipart but single part
	 * content is added directly
	 * @param mp2
	 * @return int number of parts added
	 * @throws MessagingException
	 * @throws IOException
	 */
	public int add(MultipartMailBuilder mp2) throws MessagingException, IOException {
		MimeMultipart mm = mp2.getMultipart();
		if( mm.getCount() < 2){
			// 0 or 1 parts just merge 
			return mergeMultipart(mm);
		}
		// default to add a nested multipart
		MimeBodyPart part = new MimeBodyPart();
		part.setContent(mm);
		return addBodyPart(part);
	}
	/** add all parts to the MAilBuilder
	 * text/plain parts are added as seperate text parts text 
	 * 
	 * @param mp
	 * @return int number of parts added
	 * @throws MessagingException
	 * @throws IOException
	 */
	public int mergeMultipart(MimeMultipart mp) throws MessagingException, IOException{
		int count=0;
		for(int i=0; i<mp.getCount();i++){
			MimeBodyPart p = (MimeBodyPart) mp.getBodyPart(i);
			if( p.isMimeType("text/plain")){
				Object content = p.getContent();
				if( content instanceof String){
				   count += addText((String)content);
				   count += flushText();
				}else{
					count += addBodyPart(p);
				}
			}else{
				count += addBodyPart(p);
			}
		}
		return count;
	}
	/** add a separator bar if any more text is appended immediately  after this point.
	 * if autoFlush is set then this acts as a flush
	 * @return number of parts added
	 * @throws MessagingException 
	 * 
	 */
	@Override
	public int separate(String text) throws MessagingException{
		if( flush_on_seperate){
			return flushText();
		}
		return super.separate(text);
	}
	public void setEncoding(String enc){
		encoding=enc;
	}
}