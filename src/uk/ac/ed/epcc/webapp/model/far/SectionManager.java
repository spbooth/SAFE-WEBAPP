//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.model.far;

import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.XMLPrinter;
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author spb
 *
 */

public class SectionManager extends PartManager<PageManager.Page,SectionManager.Section> {

	/**
	 * 
	 */
	private static final String SECTION_TEXT_FIELD = "SectionText";
	private static final String SECTION_RAW_HTML_FIELD = "SectionRawHTML";
	private static final String SECTION_READ_ONLY_FIELD = "SectionReadOnly";
	
	public class Section extends PartManager.Part<PageManager.Page>{

		@Override
		public Map<String, Object> getInfo() {
			Map<String, Object> info = super.getInfo();
			info.put("Text", getSectionText());
			return info;
		}
		/**
		 * @param r
		 */
		protected Section(Record r) {
			super(SectionManager.this,r);
		}
		public String getSectionText(){
			return record.getStringProperty(SECTION_TEXT_FIELD);
		}
		private class RawPrinter extends XMLPrinter{
			public RawPrinter(String raw ){
				super();
				append(raw);
			}
		}
		public XMLPrinter getSectionRawHtml(){
			String raw = record.getStringProperty(SECTION_RAW_HTML_FIELD);
			if(raw == null){
				return null;
			}
			return new RawPrinter(raw);
		}
		public boolean getSectionReadOnly(){
			return record.getBooleanProperty(SECTION_READ_ONLY_FIELD);
		}
		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.PartManager.Part#visit(uk.ac.ed.epcc.webapp.model.far.PartVisitor)
		 */
		@Override
		public <X> X visit(PartVisitor<X> vis) {
			return vis.visitSection(this);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.PartManager.Part#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return "Section";
		}
		
		public boolean isReadOnly() {
			return getSectionReadOnly();
		}
		
	}
	/**
	 * @param owner_fac
	 */
	public SectionManager(PageManager owner_fac) {
		super(owner_fac.form_manager,owner_fac, "Section");
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new Section(res);
	}
	@Override
	public Class<? super Section> getTarget() {
		return Section.class;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartOwnerFactory#getChildManager()
	 */
	@Override
	public PartManager makeChildManager() {
		return new QuestionManager(this);
	}
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = super.getDefaultTableSpecification(c, table);
		spec.setField(SECTION_TEXT_FIELD, new StringFieldType(true, null, 4096));
		spec.setField(SECTION_RAW_HTML_FIELD , new StringFieldType(true, null, 4096));
		spec.setField(SECTION_READ_ONLY_FIELD , new BooleanFieldType(true, false));
		return spec;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartManager#getChildTypeName()
	 */
	@Override
	public String getChildTypeName() {
		return "Questions";
	}

}