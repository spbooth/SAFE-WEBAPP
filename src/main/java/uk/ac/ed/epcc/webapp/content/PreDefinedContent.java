//| Copyright - The University of Edinburgh 2017                            |
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
package uk.ac.ed.epcc.webapp.content;

import java.text.MessageFormat;
import java.util.Date;
import java.util.ResourceBundle;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.messages.MessageBundleService;

/** A {@link XMLPrinter} containing pre-defined content from 
 * a message bundle. This is to allow pre-defined content
 * to be added to a {@link ContentBuilder}. The message text is allowed to contain
 * raw HTML formatting
 * @see HtmlContentFormat
 * @author spb
 *
 */
public class PreDefinedContent extends AbstractContexed implements  XMLGenerator,UIGenerator {

	private final MessageFormat fmt;
	private final Object args[];
	/**
	 * 
	 */
	private static final String DEFAULT_BUNDLE = "content";



	public PreDefinedContent(AppContext conn,String message) {
		this(conn,DEFAULT_BUNDLE,message);
	}
	
	public PreDefinedContent(AppContext conn,String message,Object ...args) {
		this(conn,DEFAULT_BUNDLE,message,args);
	}
	/** 
	 * 
	 */
	public PreDefinedContent(AppContext conn,String bundle,String message, Object ... args) {
		this(conn,conn.getService(MessageBundleService.class).getBundle(bundle == null ? DEFAULT_BUNDLE : bundle),message,args);
	}
	
	/** Create {@link PreDefinedContent} from a specific message from a {@link ResourceBundle}
	 * 
	 * @param conn
	 * @param mess
	 * @param message
	 * @param args
	 */
	public PreDefinedContent(AppContext conn,ResourceBundle mess,String message, Object ... args) {
		super(conn);
		String pattern = null;
		if( mess != null) {
			pattern = conn.expandText(mess.getString(message));
		}
		if( pattern != null) {
			fmt = new MessageFormat(pattern);
			this.args=args;
		}else {
			fmt=null;
			this.args=null;
			conn.getService(LoggerService.class).getLogger(getClass()).error("missing content "+(mess == null ? " no bundle ":mess.getBaseBundleName())+":"+message);
		}
	}
	/** extension point to apply additional processing
	 * 
	 * @param value
	 * @return
	 */
	protected String process(String value) {
		return value;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.XMLGenerator#addContent(uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder)
	 */
	@Override
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
		if( builder instanceof XMLPrinter) {
			XMLPrinter printer = (XMLPrinter) builder;
			MessageFormat fmt2 = (MessageFormat) fmt.clone();
			if( args != null && builder instanceof HtmlBuilder) {
				// apply HtmlFormat 
				for(int i=0 ; i< args.length; i++) {
					Object a = args[i];
					// MessageFormat has direct support for number and date formats. These are safe to add to html
					// Strings in particular should use HtmlContentFormat to force html escaping.
					// raw html can be added by wrapping in a UIGenerator or XMLPrinter
					// @see messages.jsf
					if( !( a instanceof Number || a instanceof Date )) {
						fmt2.setFormatByArgumentIndex(i, new HtmlContentFormat());
					}
				}
			}
			StringBuffer buffer = new StringBuffer();
			fmt2.format(args, buffer, null);
			printer.endOpen();
			printer.append(buffer);
		}
		return builder;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
	 */
	@Override
	public ContentBuilder addContent(ContentBuilder builder) {
		if( builder instanceof XMLPrinter) {
			addContent((XMLPrinter)builder);
		}else {
			ExtendedXMLBuilder span = builder.getSpan();
			addContent(span);
			span.appendParent();
		}
		return builder;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if( fmt == null) {
			return "";
		}
		return fmt.format(args, new StringBuffer(), null).toString();
	}

}
