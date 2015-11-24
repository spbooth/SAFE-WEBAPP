// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.editors.mail;

import java.io.InputStream;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.editors.mail.MessageWalker.WalkerException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.servlet.DefaultServletService;
import uk.ac.ed.epcc.webapp.servlet.ServletService;

/** Visitor that generates a MimeStreamData object for the targetted message part
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: MimeStreamDataVisitor.java,v 1.11 2015/11/09 16:32:07 spb Exp $")

public class MimeStreamDataVisitor extends AbstractVisitor{

	public static final Feature USE_RFC822 = new Feature("email.serve_rfc822",false,"Always Serve emails to browsers as RFC822");
	
	private final Logger log;
	private ByteArrayMimeStreamData data=null;
	public MimeStreamDataVisitor(AppContext conn){
		super(conn);
		log = conn.getService(LoggerService.class).getLogger(getClass());
	}
	public MimeStreamData getData(){
		return data;
	}
	@Override
	public boolean startMessage(MimePart parent, MimeMessage m,
			MessageWalker messageWalker) throws WalkerException {
		if( messageWalker.matchPath()){
			log.debug("Serving full message");
			data=new ByteArrayMimeStreamData();
			data.setMimeType(getMessageContentType());
			try {
				m.writeTo(data.getOutputStream());
			} catch (Exception e) {
			    throw new WalkerException(e);
			}
			return false;
		}
		return true;
	}

	

	@Override
	public void visit(MimePart parent, String content,
			MessageWalker messageWalker) throws WalkerException {
		// We force this to text plain to ensure we don't serv dangerous html
		//res.setContentType(m.getContentType());
		log.debug("serving text part");
		
		try {
			data=new ByteArrayMimeStreamData(content.getBytes("UTF-8"));
			data.setMimeType("text/plain; charset=UTF-8");
		} catch (Exception e) {
			throw new WalkerException(e);
		}
		return;
	}

	@Override
	public void visitInputStream(MimePart parent, InputStream content,
			MessageWalker messageWalker) throws WalkerException {
		try{
			String contentType = parent.getContentType();
			// Its legal to fold header content using CRLF
			// Apple Mail does this because it uses long content-types
			// however line folding is not legal in http
			contentType=contentType.replaceAll("\\s+", " ");
			log.debug("serving attachment "+contentType);
			data = new ByteArrayMimeStreamData();
			
			data.setMimeType(contentType);
			data.read((InputStream)content);
		}catch(Exception e){
			throw new WalkerException(e);
		}
		return;
	}
	/** Decide which mime type a Message object should be served as
	 * some browsers can handle message/rfc822 directly but others fail
	 * and should be served text/plain
	 * may want to consult the browser version or Accept headers to make decision.
	 * 
	
	 * @return mime type
	 */
	public final String getMessageContentType(){
		if( USE_RFC822.isEnabled(getContext())){
			return "message/rfc822";
		}
		ServletService serv = getContext().getService(ServletService.class);
		if( serv != null && serv instanceof DefaultServletService){
			if(((DefaultServletService)serv).supportsMime("message/rfc822")){
				return "message/rfc822";
			}
		}
		return "text/plain";
	}


}