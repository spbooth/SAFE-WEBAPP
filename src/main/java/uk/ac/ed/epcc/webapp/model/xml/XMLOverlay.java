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
package uk.ac.ed.epcc.webapp.model.xml;

import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.w3c.dom.Document;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.editors.xml.AbstractXMLTarget;
import uk.ac.ed.epcc.webapp.editors.xml.DomVisitor;
import uk.ac.ed.epcc.webapp.editors.xml.ViewXMLTargetResult;
import uk.ac.ed.epcc.webapp.editors.xml.XMLDocumentInput;
import uk.ac.ed.epcc.webapp.editors.xml.XMLTarget;
import uk.ac.ed.epcc.webapp.editors.xml.XMLTargetFactory;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.MaxLengthValidator;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraContent;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.TextFileOverlay;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A TextFileOverlay that validates updates as XML.
 * 
 * @author spb
 * @param <X> 
 *
 */


public class XMLOverlay<X extends XMLOverlay.XMLFile> extends TextFileOverlay<X> implements XMLTargetFactory{

	public static final Feature JQUERY_EDITOR = new Feature("xmleditor.jquery_schema", false, "Use the schema aware jquery XML editor");
	public static final Feature ACE_EDITOR = new Feature("xmleditor.jquery_ace", false, "Use the basic ace XML editor");
	public XMLOverlay(AppContext c, String table) {
		super(c, table);
	}

	public XMLOverlay(AppContext c){
		super(c);
	}
	
	public final class XMLEditAction extends FormAction{

		private final XMLTarget target;
		public XMLEditAction(XMLTarget target){
			this.target=target;
		}
		@Override
		public FormResult action(Form f) throws ActionException {
			return new ViewXMLTargetResult(target);
		}

		@Override
		public boolean getMustValidate() {
			return false;
		}
		
	}
	public class XMLFileUpdator extends TextFileUpdator implements ExtraContent<X>{

		@Override
		public void buildUpdateForm( Form f, X dat,SessionService<?> operator)
				throws DataException {
			super.buildUpdateForm( f, dat,operator);
			f.addAction("XML-Editor", new XMLEditAction(new TemplateTarget(dat)));
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.ExtraContent#getExtraHtml(uk.ac.ed.epcc.webapp.content.ContentBuilder, uk.ac.ed.epcc.webapp.session.SessionService, java.lang.Object)
		 */
		@Override
		public <C extends ContentBuilder> C getExtraHtml(C cb,
				SessionService<?> op, X target) {
			AppContext conn = getContext();
			if( JQUERY_EDITOR.isEnabled(conn)){
				if( cb instanceof HtmlBuilder ){
					HtmlBuilder hb = (HtmlBuilder)cb;

					ServletService serv = conn.getService(ServletService.class);
					// add script files
					for(String path : new String[]{
							"lib/ace/src-min/ace.js",
							
							"lib/jquery.min.js",
							"lib/jquery-ui.min.js",
							"lib/json2.js",
							"lib/cycle.js",
							//"lib/jquery.autosize-min.js",
							"lib/vkbeautify.js",
							"xsd2json.js",
							"jquery.xmleditor.js"
					}){
						hb.addScriptFile(serv.encodeURL("/jquery.xmleditor/"+path));
						hb.clean("\n");
					}
					hb.addScript(
							"$(function() { \n"+
									"var extractor = new Xsd2Json(\"report.xsd\", {\"schemaURI\":\""+
									serv.encodeURL("/templates/schema/")+
									"\"});\n"+
									"$(\"#transition_"+TEXT+"\").xmlEditor({schema : extractor.getSchema(),enableDocumentStatusPanel : false });\n"+
							"});\n");
				}
			}else if (ACE_EDITOR.isEnabled(conn)){
				if( cb instanceof HtmlBuilder ){
					HtmlBuilder hb = (HtmlBuilder)cb;

					ServletService serv = conn.getService(ServletService.class);
					// add script files
					for(String path : new String[]{"lib/ace/src-min/ace.js",
							"lib/ace/src-min/mode-xml.js",
							"lib/ace/src-min/theme-textmate.js",
							"lib/jquery.min.js",
							"lib/jquery-ui.min.js",
							"jquery-ace.js"
					}){
						hb.addScriptFile(serv.encodeURL("/jquery.xmleditor/"+path));
						hb.clean("\n");
					}
					hb.addScript(
							"$('#transition_Text').ace({theme: 'textmate',lang: 'xml'})"
					);
				}
			}
			return cb;
		}

	}
	/** Get schema to validate against
	 * 
	 * @return Schema
	 */
	@Override
	public Schema getSchema(){
		return null;
	}

	public static class XMLFile extends TextFile{

		protected XMLFile(Record r, URL base) {
			super(r, base);
		}
		public Document getDocument() throws Exception{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			
			Reader data = getDataReader();
			if( data == null ){
				return null;
			}
			Source input = new StreamSource(data);
			DOMResult res = new DOMResult();
			transformer.transform(input, res);
			return (Document) res.getNode();
		}
		public LinkedList<String> getRootPath(){
			LinkedList<String> path = new LinkedList<>();
			path.add(getFactoryTag());
			path.add(Integer.toString(getID()));
			return path;
		}
		public void setDocument(Document document) throws TransformerFactoryConfigurationError, TransformerException{
			Logger log = getContext().getService(LoggerService.class).getLogger(getClass());
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
		    Source source = new DOMSource(document);
		    StreamResult output = new StreamResult(new StringWriter());
		    transformer.transform(source, output);
		    String new_text =output.getWriter().toString();
		    log.debug("New text is "+new_text);
		    String file_text=getResourceString();
			if(new_text == null ||  new_text.equals(file_text)){
				log.debug("Different from file_text");
				setText(null);
			}else{
				setText(new_text);
			}

		}
		
	}

	@Override
	protected X makeBDO(Record res) throws DataFault {
		return (X) new XMLFile(res, getBaseURL());
	}


	public boolean canView(SessionService<?> sess) {
		return sess.hasRole(SessionService.ADMIN_ROLE);
	}
	public class TemplateTarget extends AbstractXMLTarget{

		private int id;
		private XMLFile file=null;
		private Document doc=null;
		public TemplateTarget(LinkedList<String> path){
			super(path);
			id=Integer.parseInt(path.get(1));
		}
		public TemplateTarget(X dat){
			super(dat.getRootPath());
			this.id=dat.getID();
			this.file=dat;
		}
		public XMLFile getXMLFile() throws DataException{
			if( file == null ){
				file = find(id);
			}
			return file;
		}
		@Override
		public AppContext getContext() {
			return XMLOverlay.this.getContext();
		}

		@Override
		public Document getDocument() {
			if( doc == null){
				try {
					doc=getXMLFile().getDocument();
				} catch (Exception e) {
					getLogger().error("Error getting document",e);
					return null;
				}
			}
			return doc;
		}

		
		@Override
		public final boolean canView(SessionService<?> sess) {
			return XMLOverlay.this.canView(sess);
		}

		@Override
		public void commit() throws Exception {
			if( doc != null ){
				XMLFile f = getXMLFile();
				f.setDocument(doc);
				f.commit();
			}
		}
		
		@Override
		public XMLTargetFactory getXMLTargetFactory() {
			return XMLOverlay.this;
		}
		
		@Override
		public XMLTarget getRootTarget() {
			LinkedList<String> root_path =new LinkedList<>();
			LinkedList<String> path = getTargetPath();
			root_path.add(0, path.get(0));
			root_path.add(1,path.get(1));
			return find(root_path);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.xml.AbstractXMLTarget#extractPrefix(java.util.LinkedList)
		 */
		@Override
		protected LinkedList<String> extractPrefix(LinkedList<String> node_path) {
			LinkedList<String> prefix_path = new LinkedList<>();
			prefix_path.add(node_path.removeFirst()); // factory tag
			prefix_path.add(node_path.getFirst()); // file id
			return prefix_path;
		}
		
	}
	@Override
	public XMLTarget find(LinkedList<String> location) {
		return new TemplateTarget(location);
	}

	@Override
	public FormUpdate<X> getFormUpdate(AppContext c) {
		return new XMLFileUpdator();
	}

	@Override
	protected Map<String, Selector> getSelectors() {
		Map<String,Selector> res= super.getSelectors();

		res.put(TEXT, new Selector() {

			@Override
			public Input getInput() {

				try {
					return new XMLDocumentInput(getContext(), TransformerFactory.newInstance(), getSchema());
				} catch (Exception e) {
					getLogger().error("Error making input",e);
					TextInput input = new TextInput();
					input.setSingle(false);
					input.addValidator(new MaxLengthValidator(1<<24));
					return input;
				}
			}

		});

		return res;
	}

	
	@Override
	public DomVisitor getValidatingVisitor() {
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.TextFileOverlay#splitNormalised(java.lang.String)
	 */
	@Override
	protected String[] splitNormalised(String input) {
		return input.replaceAll(">\\s*<", ">\n<").split("\r?\n");
	}
}