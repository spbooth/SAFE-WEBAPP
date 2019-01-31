//| Copyright - The University of Edinburgh 2019                            |
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

import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder;
import uk.ac.ed.epcc.webapp.content.XMLBuilderSaxHandler;

/**
 * @author Stephen Booth
 *
 */
public class HtmlStripper extends AbstractContexed {
	/**
	 * 
	 */
	public static final String EMAIL_FORMAT_WRAP_THRESHOLD_CFG = "email.format.wrap_threshold";
	
	private final ContentBuilder sb;
	public static final Feature CLEAN_WITH_STYLESHEET_FEATURE = new Feature("clean_html_stylesheet", false, "Use a stylesheet to clean html");
	/**
	 * 
	 */
	public HtmlStripper(ContentBuilder sb,AppContext conn) {
		super(conn);
		this.sb=sb;
	}

	public void clean(String string) {
		if( CLEAN_WITH_STYLESHEET_FEATURE.isEnabled(getContext())){
			try{
				// attempt to clean html vi xlst
				TransformerFactory tfac = TransformerFactory.newInstance();
				Transformer trans =tfac.newTransformer(new StreamSource(getClass().getResourceAsStream("HtmlCleaner.xsl")));
				trans.setErrorListener(new ErrorListener() {

					public void warning(TransformerException exception)
							throws TransformerException {
						// ignore warnings

					}

					public void fatalError(TransformerException exception)
							throws TransformerException {
						throw exception;

					}

					public void error(TransformerException exception)
							throws TransformerException {
						// ignore non fatal errors

					}
				});
				Source src = new StreamSource(new StringReader(string));
				SimpleXMLBuilder child =sb.getText();
				XMLBuilderSaxHandler handler = new XMLBuilderSaxHandler(child);
				Result res = new SAXResult(handler);
				trans.transform(src, res);
				child.appendParent();
				return;
			}catch(TransformerException e){
				// transform failed.
				getLogger().error("Error formatting html",e);
			}
		}
		// try quick and dirty html strip
		string = strip(string);
		sb.cleanFormatted(getContext().getIntegerParameter(EMAIL_FORMAT_WRAP_THRESHOLD_CFG,MessageWalker.MAXLENGTH), string);
	}

	private static final Pattern UNICODE_PATT = Pattern.compile("&\\#(\\d+);");
	private static final Pattern LINK_HEAD_PATT = Pattern.compile("<a\\s+href\\s*=\\s*'(.*?)'\\s*>");
	/** A quick and diry regexp based removal of html.
	 * @param string
	 * @return
	 */
	public static String strip(String string) {
		string = getLinks(string);
		string = string.replaceAll("<[bB][rR]/?>", "\n");
		string = string.replaceAll("<[^>]*>", "");
		string = string.replace("&amp;", "&");
		string = string.replace("&nbsp;", " ");
		string = string.replace("&lt;", "<");
		string = string.replace("&gt;", ">");
		string = string.replace("&quot;", "\"");
		
		// expand encoded chars into the text 
		string = expandUnicode(string);
		return string;
	}

	/** replace expanded unicode with raw 
	 * @param string
	 * @return
	 */
	public static String expandUnicode(String string) {
		StringBuffer sb = new StringBuffer();
		Matcher m = UNICODE_PATT.matcher(string);
		while( m.find() ) {
			int codepoint = Integer.parseInt(m.group(1));
			m.appendReplacement(sb, String.valueOf(Character.toChars(codepoint)));
		}
		m.appendTail(sb);
		string = sb.toString();
		return string;
	}
	public static String getLinks(String string) {
		StringBuffer sb = new StringBuffer();
		Matcher m = LINK_HEAD_PATT.matcher(string);
		while( m.find() ) {
			m.appendReplacement(sb, "[$1] ");
		}
		m.appendTail(sb);
		string = sb.toString();
		return string;
	}
}
