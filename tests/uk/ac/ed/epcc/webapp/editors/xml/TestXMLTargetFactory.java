//| Copyright - The University of Edinburgh 2012                            |
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
package uk.ac.ed.epcc.webapp.editors.xml;

import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.w3c.dom.Document;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */

public class TestXMLTargetFactory implements XMLTargetFactory {
	public TestXMLTargetFactory(AppContext c, String tag) {
		super();
		this.c = c;
		this.tag = tag;
	}

	private final AppContext c;
	private final String tag;
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	public AppContext getContext() {
		return c;
	}
	public Schema getSchema() {
		return null;
	}

	
	public DomVisitor getValidatingVisitor() {
		return null;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.editors.xml.XMLTargetFactory#find(java.util.LinkedList)
	 */
	public XMLTarget find(LinkedList<String> location) {
		return new TestXMLTarget(location);
	}

	

	public class TestXMLTarget extends AbstractXMLTarget{

		private Document doc;
		/**
		 * @param path
		 */
		public TestXMLTarget(LinkedList<String> path) {
			super(path);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.xml.XMLTarget#getXMLTargetFactory()
		 */
		public XMLTargetFactory getXMLTargetFactory() {
			return TestXMLTargetFactory.this;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.xml.XMLTarget#getDocument()
		 */
		public Document getDocument() {
			if( doc == null){
				try{
					String name = getTargetPath().get(1);
					TransformerFactory fac = TransformerFactory.newInstance();
					DOMResult res = new DOMResult();
					InputStream stream = getClass().getResourceAsStream(name);
					if( stream == null ){
						getContext().error("Resource "+name+" not found");
						return null;
					}
					StreamSource src = new StreamSource(stream);
					Transformer t = fac.newTransformer();
					t.transform(src, res);
					doc =  (Document) res.getNode();
				}catch(Exception e){
					getContext().error(e,"Error reading document");
					return null;
				}
			}
			return doc;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.xml.XMLTarget#getRootTarget()
		 */
		public XMLTarget getRootTarget() {
			LinkedList<String> root_path = new LinkedList<String>();
			LinkedList<String> path = getTargetPath();
			root_path.add(path.get(0));
			root_path.add(path.get(1));
			return new TestXMLTarget(root_path);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.xml.XMLTarget#canView(uk.ac.ed.epcc.webapp.session.SessionService)
		 */
		public boolean canView(SessionService<?> sess) {
			return true;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.xml.XMLTarget#commit()
		 */
		public void commit() throws Exception {
			
			
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
		 */
		public AppContext getContext() {
			return TestXMLTargetFactory.this.getContext();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.xml.AbstractXMLTarget#extractPrefix(java.util.LinkedList)
		 */
		@Override
		protected LinkedList<String> extractPrefix(LinkedList<String> node_path) {
			LinkedList<String> prefix_path = new LinkedList<String>();
			prefix_path.add(node_path.removeFirst()); // factory tag
			prefix_path.add(node_path.getFirst()); // file id
			return prefix_path;
		}
		
	}
}