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
package uk.ac.ed.epcc.webapp.editors.mail;

import java.io.InputStream;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimePart;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.editors.mail.MessageWalker.WalkerException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.preferences.Preference;
import uk.ac.ed.epcc.webapp.servlet.DefaultServletService;
import uk.ac.ed.epcc.webapp.servlet.ServletService;

/** Visitor that generates a {@link MimeStreamData} object for the targetted message part
 * 
 * @author spb
 *
 */


public class MimeStreamDataVisitor extends AbstractVisitor{

	public static final Feature USE_RFC822 = new Preference("email.serve_rfc822",false,"Always Serve email downloads to browsers as RFC822 so they display in an email client instead of as text");
    public static final Feature USE_HTML = new Feature("email.serve_html",false,"Allow html mime parts from emails to be shown as html no plain text");
	
	private final Logger log;
	private ByteArrayMimeStreamData data=null;
	public MimeStreamDataVisitor(AppContext conn){
		super(conn);
		log = Logger.getLogger(conn,getClass());
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
			if( USE_HTML.isEnabled(getContext())) {
				visitInputStream(parent, parent.getInputStream(), messageWalker);
			}else {
				data=new ByteArrayMimeStreamData(content.getBytes("UTF-8"));
				data.setMimeType("text/plain; charset=UTF-8");
			}
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
			StringBuilder type = new StringBuilder();
			for(String clause : contentType.split("\\s*;\\s*")) {
				if( type.length() == 0 ) {
					type.append(clause);
				}else {
					// We have a name in the content-type map this to the stream-data name
					String prefix = "name=";
					if( clause.startsWith(prefix)) {
						String name = clause.substring(prefix.length());
						if( name.startsWith("\"") && name.endsWith("\"")) {
							name = name.substring(1, name.length()-1);
						}
						data.setName(name);
					}else {
						type.append("; ");
						type.append(clause);
					}
				}
			}
			
			data.setMimeType(type.toString());
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
		if( useRFC822(getContext())){
			return "message/rfc822";
		}
		return "text/plain";
	}
	public static boolean useRFC822(AppContext conn){
		if( USE_RFC822.isEnabled(conn)){
			return true;
		}
		ServletService serv = conn.getService(ServletService.class);
		if( serv != null && serv instanceof DefaultServletService){
			if(((DefaultServletService)serv).supportsMime("message/rfc822")){
				return true;
			}
		}
		return false;

	}
	@Override
	public boolean visitHeaders() {
		// Don't look at any of these
		return false;
	}


}