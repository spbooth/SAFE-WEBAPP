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
import java.util.ResourceBundle;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.messages.MessageBundleService;

/** A {@link XMLPrinter} containing pre-defined content from 
 * a message bundle. This is to allow pre-defined content
 * to be added to a {@link ContentBuilder}
 * @author spb
 *
 */
public class PreDefinedContent extends XMLPrinter implements Contexed, XMLGenerator,UIGenerator {

	private final AppContext conn;
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
		this.conn=conn;
		if(bundle==null) {
			bundle=DEFAULT_BUNDLE;
		}
		ResourceBundle mess = conn.getService(MessageBundleService.class).getBundle(bundle);
		 String val = MessageFormat.format(conn.expandText(mess.getString(message)),args);
		 if(val !=null) {
			 append(process(val));
		 }else {
			 conn.getService(LoggerService.class).getLogger(getClass()).error("missing content "+bundle+":"+message);
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
			((XMLPrinter)builder).append(this);
		}
		return builder;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return conn;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
	 */
	@Override
	public ContentBuilder addContent(ContentBuilder builder) {
		if( builder instanceof XMLPrinter) {
			((XMLPrinter)builder).append(this);
		}else {
			ExtendedXMLBuilder span = builder.getSpan();
			addContent(span);
			span.appendParent();
		}
		return builder;
	}

}
